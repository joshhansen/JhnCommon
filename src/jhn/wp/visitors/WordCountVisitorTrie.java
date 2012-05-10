package jhn.wp.visitors;

import jhn.trie.StringShortTrie;
import jhn.trie.StringShortTrieImpl;
import jhn.util.Util;

public class WordCountVisitorTrie extends Visitor {
	private StringShortTrie counts;
	private final String outputFilename;
	
	public WordCountVisitorTrie(String outputFilename) {
		this.outputFilename = outputFilename;
	}

	@Override
	public void beforeEverything() throws Exception {
		super.beforeEverything();
		counts = new StringShortTrieImpl();
	}

	@Override
	public void visitWord(String word) {
		super.visitWord(word);
		if(!word.isEmpty()) {
			counts.increment(word);
		}
	}

	@Override
	public void afterEverything() {
		super.afterEverything();
		Util.serialize(counts, outputFilename);
	}
	
}
