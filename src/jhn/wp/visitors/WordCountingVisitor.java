package jhn.wp.visitors;

import jhn.counts.ints.IntCounter;
import jhn.counts.ints.ObjIntRAMCounter;
import jhn.util.Util;
import jhn.wp.exceptions.CountException;

public class WordCountingVisitor extends LabelAwareVisitor {
	protected int wordsInLabel = 0;
	
	protected IntCounter<String> currentLabelWordCounts;
	
	@Override
	public void beforeEverything() throws Exception {
		super.beforeEverything();
		currentLabelWordCounts = new ObjIntRAMCounter<>();
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
		
		currentLabelWordCounts.inc(word);
//		Integer count = currentLabelWordCounts.get(word);
//		count = count==null? 1 : count+1;
//		currentLabelWordCounts.put(word, count);
		wordsInLabel++;
	}

	@Override
	public void afterLabel() throws Exception {
		currentLabelWordCounts.reset();
		super.afterLabel();
	}
}
