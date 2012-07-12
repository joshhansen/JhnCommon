package jhn.wp.visitors.counting;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedList;

import jhn.counts.IntIntIntCounterMap;
import jhn.idx.DiskStringIndex;
import jhn.idx.ReverseIndex;
import jhn.ifaces.Trimmable;
import jhn.util.Util;
import jhn.wp.exceptions.CountException;
import jhn.wp.visitors.Visitor;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

public class WindowedCocountVisitor extends Visitor {
	private enum Compression {
		NONE,
		BZIP2,
		GZIP
	}
	private int[] wordArr = new int[2];
	private final int windowSize;
	private final ReverseIndex<String> words;
	private LinkedList<Integer> ll = new LinkedList<Integer>();
	private final int baseChunkSize;
	private final int chunkSize;
	private int currentBaseChunkSize = 0;
	private int currentChunkSize = 0;
	private final File outputDir;
	private static final String FILE_EXT = ".counts";
	
	private IntIntIntCounterMap baseCounts = new IntIntIntCounterMap();
	private IntIntIntCounterMap counts = new IntIntIntCounterMap();
	public WindowedCocountVisitor(String outputDir, String indexFilename, int baseChunkSize,
			int chunkSize, int windowSize) throws Exception {
		
		this.outputDir = new File(outputDir);
		this.words = new DiskStringIndex(indexFilename);
		if(!this.outputDir.exists()) {
			this.outputDir.mkdirs();
		}
		this.baseChunkSize = baseChunkSize;
		this.chunkSize = chunkSize;
		this.windowSize = windowSize;
	}
	
	
	
	private ObjectOutputStream nextStream() throws Exception {
		return nextStream(Compression.NONE);
	}
	
	private ObjectOutputStream nextStream(Compression compression) throws Exception {
		OutputStream os = new FileOutputStream(nextFilename());
		
		switch(compression) {
			case BZIP2:
				os = new BZip2CompressorOutputStream(os);
				break;
			case GZIP:
				os = new GzipCompressorOutputStream(os);
				break;
			case NONE: default:
				break;
		}
		
		return new ObjectOutputStream(os);
	}
	
	private String nextFilename() {
		int i = nextInt();
		int folderNum = i % 40;
		String path = outputDir.getPath() + "/" + folderNum;
		File dir = new File(path);
		dir.mkdirs();
		
		return path + "/" + i + FILE_EXT;
	}
	
	private static final FileFilter filter = new FileFilter(){
		@Override
		public boolean accept(File f) {
			return f.getName().endsWith(FILE_EXT);
		}
	};
	private int nextInt() {
		int max = -1;
		int value;
		for(File f : outputDir.listFiles(filter)) {
			value = Integer.parseInt(f.getName().split("\\.")[0]);
			if(value > max) {
				max = value;
			}
		}
		return max+1;
	}
	
	private void pairFound(int word1idx, int word2idx) throws Exception {
		if(currentBaseChunkSize < baseChunkSize || baseCounts.containsValue(word1idx, word2idx)) {
			baseCounts.inc(word1idx, word2idx);
			currentBaseChunkSize++;
		} else {
			counts.inc(word1idx, word2idx);
			currentChunkSize++;
			
			if(currentChunkSize >= chunkSize) {
				System.err.println("Serializing");
				currentChunkSize = 0;
				writeCounts(counts);
				counts.reset();
				System.gc();
			}
		}

	}
	
	private void writeCounts(IntIntIntCounterMap counts) throws Exception {
		ObjectOutputStream oos = nextStream();
		counts.writeObject(oos);
		oos.close();
	}
	
	@Override
	public void visitLabel(String label) throws CountException {
		ll.clear();
	}

	@Override
	public void visitWord(String word) {
		int word1idx = words.indexOf(word);
		int word2idxI;
		if(word1idx != ReverseIndex.KEY_NOT_FOUND) {
			for(Integer word2idx : ll) {
				word2idxI = word2idx.intValue();
				if(word2idxI != ReverseIndex.KEY_NOT_FOUND) {
					wordArr[0] = word1idx;
					wordArr[1] = word2idxI;
					Arrays.sort(wordArr);
					try {
						pairFound(wordArr[0], wordArr[1]);
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			System.err.print('[');
			System.err.print(word);
			System.err.print(']');
		}
		ll.addLast(word1idx);
		if(ll.size() > windowSize-1) {
			ll.removeFirst();
		}
	}

	@Override
	public void afterEverything() throws Exception {
		writeCounts(baseCounts);
		
		if(words instanceof Trimmable) {
			((Trimmable)words).trim();
		}
		Util.serialize(words, outputDir + "/words.idx");
	}
}
