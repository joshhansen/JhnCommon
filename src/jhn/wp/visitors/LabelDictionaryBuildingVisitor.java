package jhn.wp.visitors;

import org.ardverk.collection.PatriciaTrie;
import org.ardverk.collection.StringKeyAnalyzer;
import org.ardverk.collection.Trie;

import jhn.util.Util;
import jhn.wp.exceptions.CountException;

public class LabelDictionaryBuildingVisitor extends Visitor {
	private Trie<String,Boolean> dictionary;
	private final String outputFilename;
	
	public LabelDictionaryBuildingVisitor(String outputFilename) {
		this.outputFilename = outputFilename;
	}
	
	@Override
	public void beforeEverything() throws Exception {
		super.beforeEverything();
		dictionary = new PatriciaTrie<String,Boolean>(StringKeyAnalyzer.BYTE);
	}

	@Override
	public void visitLabel(String label) throws CountException {
		super.visitLabel(label);
		dictionary.put(label, Boolean.TRUE);
	}

	@Override
	public void afterEverything() {
		super.afterEverything();
		
		Util.serialize(dictionary, outputFilename);
	}

}
