package jhn.wp.cocounts;

import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedList;

import jhn.counts.ints.IntIntCounter;
import jhn.counts.ints.IntIntIntRAMCounterMap;
import jhn.counts.ints.IntIntRAMCounter;
import jhn.idx.DiskStringIndex;
import jhn.idx.ReverseIndex;
import jhn.wp.exceptions.CountException;
import jhn.wp.visitors.Visitor;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

public class WindowedCocountVisitor extends Visitor {
	private enum Compression {
		NONE, BZIP2, GZIP
	}

	private static final int REDUCE_INTERVAL = 20;
	
	private final Compression compression = Compression.NONE;
	private int[] wordArr = new int[2];
	private final int windowSize;
	private final ReverseIndex<String> words;
	private LinkedList<Integer> ll = new LinkedList<>();
	private final int chunkSize;
	private int currentChunkSize = 0;
	private final File outputDir;
	private static final String FILE_EXT = ".counts";

	private IntIntIntRAMCounterMap counts = new IntIntIntRAMCounterMap();

	public WindowedCocountVisitor(String outputDir, String indexFilename, int chunkSize, int windowSize) throws Exception {
		this.outputDir = new File(outputDir);
		this.words = new DiskStringIndex(indexFilename);
		if (!this.outputDir.exists()) {
			this.outputDir.mkdirs();
		}
		this.chunkSize = chunkSize;
		this.windowSize = windowSize;
	}
	
	private ObjectOutputStream stream(File file) throws Exception {
		OutputStream os = new FileOutputStream(file);

		switch (compression) {
		case BZIP2:
			os = new BZip2CompressorOutputStream(os);
			break;
		case GZIP:
			os = new GzipCompressorOutputStream(os);
			break;
		case NONE:
		default:
			break;
		}

		return new ObjectOutputStream(os);
	}

	private File depthDir(int depth) {
		File depthDir = new File(outputDir.getPath() + "/" + depth);
		depthDir.mkdirs();
		return depthDir;
	}
	
	private IntIntCounter fileNumByDepth = new IntIntRAMCounter(new Int2IntArrayMap());
	private File nextFile(int depth) {
		int next = fileNumByDepth.getCount(depth);
		fileNumByDepth.inc(depth);
		return new File(depthDir(depth).getPath() + "/" + next + FILE_EXT);
	}

	private void pairFound(int word1idx, int word2idx) throws Exception {
		counts.inc(word1idx, word2idx);
		currentChunkSize++;

		if (currentChunkSize >= chunkSize) {
			File dest = nextFile(0);
			System.err.println("Serializing");
			currentChunkSize = 0;
			writeCounts(counts, dest);
			counts.reset();
			System.gc();
			
			reduceIfNeeded();
		}
	}
	
	private static final FileFilter countsFiles = new FileFilter(){
		@Override
		public boolean accept(File pathname) {
			return !pathname.isDirectory() && pathname.getName().endsWith(FILE_EXT);
		}
	};
	
	private File[] sourceFiles(int depth) {
		return depthDir(depth).listFiles(countsFiles);
	}
	
	private File destFile(int depth) {
		return nextFile(depth+1);
	}
	
	private void reduceIfNeeded() {
		for(int depth : fileNumByDepth.keySetI()) {
			File[] src = sourceFiles(depth);
			if(src.length > 0 && src.length % REDUCE_INTERVAL == 0) {
				System.out.println("Reducing depth " + depth + " to depth " + (depth+1));
				CountReducer cr = new CountReducer(src, destFile(depth));
				cr.run();
			}
		}
		
		System.out.println();
		System.out.println();
	}
	
	private void finalReduction() {
		for(int depth : fileNumByDepth.keySetI()) {
			System.out.println("Final reduction for depth " + depth);
			CountReducer cr = new CountReducer(sourceFiles(depth), destFile(depth));
			cr.run();
		}
	}
	
	private void writeCounts(IntIntIntRAMCounterMap theCounts, File destFile) throws Exception {
		try(ObjectOutputStream oos = stream(destFile)) {
			theCounts.writeObject(oos);
		}
	}

	@Override
	public void visitLabel(String label) throws CountException {
		ll.clear();
	}

	@Override
	public void visitWord(String word) {
		int word1idx = words.indexOf(word);
		int word2idxI;
		if (word1idx != ReverseIndex.KEY_NOT_FOUND) {
			for (Integer word2idx : ll) {
				word2idxI = word2idx.intValue();
				if (word2idxI != ReverseIndex.KEY_NOT_FOUND) {
					wordArr[0] = word1idx;
					wordArr[1] = word2idxI;
					Arrays.sort(wordArr);
					try {
						pairFound(wordArr[0], wordArr[1]);
					} catch (Exception e) {
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
		if (ll.size() > windowSize - 1) {
			ll.removeFirst();
		}
	}

	@Override
	public void afterEverything() throws Exception {
		writeCounts(counts, nextFile(0));
		finalReduction();
	}
}
