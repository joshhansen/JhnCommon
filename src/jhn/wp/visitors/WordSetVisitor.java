package jhn.wp.visitors;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import jhn.util.Util;

public class WordSetVisitor extends Visitor {
	private final String outputFilename;
	private final ObjectOpenHashSet<String> words = new ObjectOpenHashSet<String>();
	
	public WordSetVisitor(String outputFilename) {
		this.outputFilename = outputFilename;
	}

	@Override
	public void visitWord(String word) {
		words.add(word.intern());
	}

	@Override
	public void afterEverything() {
		Util.serialize(words, outputFilename);
	}
}