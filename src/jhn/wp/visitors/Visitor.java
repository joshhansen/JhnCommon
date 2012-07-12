package jhn.wp.visitors;

import jhn.wp.CorpusEventHandler;
import jhn.wp.exceptions.CountException;


public class Visitor implements CorpusEventHandler {
	@Override
	public void beforeEverything() throws Exception {}
	
	@Override
	public void beforeLabel() {}
	
	@Override
	public void visitLabel(final String label) throws CountException {}
	
	@Override
	public void visitWord(final String word) {}
	
	@Override
	public void afterLabel() throws Exception {}
	
	@Override
	public void afterEverything() throws Exception {}
	
	@Override
	public void visitDocument(String text) {}
}