package jhn.wp.counts;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Set;

import jhn.counts.i.i.IntIntCounter;
import jhn.counts.i.i.IntIntRAMCounter;
import jhn.idx.DiskStringIndex;
import jhn.idx.ReverseIndex;
import jhn.util.Util;
import jhn.wp.visitors.Visitor;

public class WordCountVisitorChunked extends Visitor implements AutoCloseable {
	private static final int INTERVAL = 40000000;
	
	private final Set<String> stopwords = jhn.util.Util.stopwords();
	
	private ReverseIndex<String> wordIdx;
	private final String outputDir;
	private IntIntCounter wordCounts;
	
	private int i = 0;
	private int chunkNum = 0;
	private int wordIdx_;
	
	public WordCountVisitorChunked(String wordIdxFilename, String outputDir) throws Exception {
		wordIdx = new DiskStringIndex(wordIdxFilename);
		this.outputDir = outputDir;
		new File(outputDir).mkdirs();
	}
	
	private void resetCounts() throws Exception {
		if(wordCounts != null) {
			Util.closeIfPossible(wordCounts);
		}
		
		wordCounts = new IntIntRAMCounter();
	}

	@Override
	public void beforeEverything() throws Exception {
		resetCounts();
	}
	
	@Override
	public void visitWord(String word) {
		if(!word.isEmpty() && !stopwords.contains(word)) {
			super.visitWord(word);
			
			wordIdx_ = wordIdx.indexOf(word);
			
			if(wordIdx_ >= 0) {
				wordCounts.inc(wordIdx_);
				i++;
				if(i >= INTERVAL) {
					writeChunkToDisk();
					try {
						resetCounts();
					} catch (Exception e) {
						e.printStackTrace();
					}
					i = 0;
				}
			}
		}
	}
	
	private void writeChunkToDisk() {
		String filename = outputDir + "/" + chunkNum++ + ".counts";
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename));
			
			IntIntRAMCounter.writeObj(wordCounts, oos);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void afterEverything() throws Exception {
		writeChunkToDisk();
	}

	@Override
	public void close() throws Exception {
		Util.closeIfPossible(wordIdx);
		Util.closeIfPossible(wordCounts);
	}	
}
