package jhn.wp.visitors;

import java.util.HashMap;
import java.util.Map;

import jhn.util.Util;
import jhn.wp.exceptions.CountException;

public class WordCountingVisitor extends LabelAwareVisitor {
	protected int wordsInLabel = 0;
	protected Map<String,Integer> currentLabelWordCounts;
	
	@Override
	public void beforeEverything() throws Exception {
		super.beforeEverything();
		currentLabelWordCounts = new HashMap<String,Integer>();
	}
	
	@Override
	public void visitLabel(String label) throws CountException {
		super.visitLabel(label);
		wordsInLabel = 0;
	}
	
	@Override
	public void visitWord(String word) {
		super.visitWord(word);
		if(word.isEmpty() || Util.stopwords().contains(word)) return;
		
		Integer count = currentLabelWordCounts.get(word);
		count = count==null? 1 : count+1;
		currentLabelWordCounts.put(word, count);
		wordsInLabel++;
	}

	@Override
	public void afterLabel() throws Exception {
		currentLabelWordCounts.clear();
		super.afterLabel();
	}
}
