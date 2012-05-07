package jhn.wp.visitors;

import jhn.wp.exceptions.CountException;


public class Visitor {
	public void beforeEverything() throws Exception {}
	public void beforeLabel() {}
	public void visitLabel(final String label) throws CountException {}
	public void visitWord(final String word) {}
	public void afterLabel() throws Exception {}
	public void afterEverything() {}
}