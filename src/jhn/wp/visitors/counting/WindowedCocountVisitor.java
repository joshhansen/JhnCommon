package jhn.wp.visitors.counting;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.LinkedList;

import jhn.counts.IntIntIntCounterMap;
import jhn.idx.DiskStringIndex;
import jhn.idx.ReverseIndex;
import jhn.ifaces.Trimmable;
import jhn.util.Util;
import jhn.wp.exceptions.CountException;
import jhn.wp.visitors.Visitor;

public class WindowedCocountVisitor extends Visitor {
	private int[] wordArr = new int[2];
	private final int windowSize;
	private final ReverseIndex<String> words;
	private LinkedList<Integer> ll = new LinkedList<Integer>();
	private final int chunkSize;
	private int currentChunkSize = 0;
	private final File outputDir;
	private static final String FILE_EXT = ".counts";
	
	private IntIntIntCounterMap counts = new IntIntIntCounterMap();
	public WindowedCocountVisitor(String outputDir, int chunkSize, int windowSize) throws Exception {
		this.outputDir = new File(outputDir);
		this.words = new DiskStringIndex(indexFilename);
		if(!this.outputDir.exists()) {
			this.outputDir.mkdirs();
		}
		this.chunkSize = chunkSize;
		this.windowSize = windowSize;
	}
	
	private ObjectOutputStream nextStream() throws Exception {
		return new ObjectOutputStream(new FileOutputStream(nextFilename()));
	}
	
	private String nextFilename() {
		return outputDir.getPath() + "/" + nextInt() + FILE_EXT;
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
		counts.inc(word1idx, word2idx);
		currentChunkSize += 1;
		if(currentChunkSize >= chunkSize) {
			System.err.println("Serializing");
			currentChunkSize = 0;
			ObjectOutputStream oos = nextStream();
			counts.writeObject(oos);
			oos.flush();
			oos.close();
			counts.reset();
			System.gc();
		}
	}
	
	@Override
	public void visitLabel(String label) throws CountException {
		ll.clear();
	}

	@Override
	public void visitWord(String word) {
		int word1idx = words.indexOf(word);
		for(Integer word2idx : ll) {
			wordArr[0] = word1idx;
			wordArr[1] = word2idx.intValue();
			Arrays.sort(wordArr);
			try {
				pairFound(wordArr[0], wordArr[1]);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		ll.addLast(word1idx);
		if(ll.size() > windowSize-1) {
			ll.removeFirst();
		}
	}

	@Override
	public void afterEverything() {
		if(words instanceof Trimmable) {
			((Trimmable)words).trim();
		}
		Util.serialize(words, outputDir + "/words.idx");
	}
}
