package jhn.wp.visitors;

import org.ardverk.collection.PatriciaTrie;
import org.ardverk.collection.StringKeyAnalyzer;
import org.ardverk.collection.Trie;

import jhn.util.Util;

public class TrieWordCountVisitor extends Visitor {
	private Trie<String,Integer> counts;
	private final String outputFilename;
	
	public TrieWordCountVisitor(String outputFilename) {
		this.outputFilename = outputFilename;
	}

	@Override
	public void beforeEverything() throws Exception {
		super.beforeEverything();
		counts = new PatriciaTrie<String,Integer>(StringKeyAnalyzer.BYTE);
	}

	@Override
	public void visitWord(String word) {
		super.visitWord(word);
		Integer count = counts.get(word);
		count = Integer.valueOf( count==null ? 1 : count.intValue()+1 );
		counts.put(word, count);
	}

	@Override
	public void afterEverything() {
		super.afterEverything();
		Util.serialize(counts, outputFilename);
	}
	
}
