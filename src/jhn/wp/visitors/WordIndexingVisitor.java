package jhn.wp.visitors;

import jhn.idx.StringIndex;
import jhn.util.Util;

public class WordIndexingVisitor extends Visitor {
	private final String outputFilename;
	private final StringIndex idx = new StringIndex();
	
	public WordIndexingVisitor(String outputFilename) {
		this.outputFilename = outputFilename;
	}

	@Override
	public void visitWord(String word) {
		idx.indexOf(word);
	}

	@Override
	public void afterEverything() {
		idx.trim();
		Util.serialize(idx, outputFilename);
	}
}
