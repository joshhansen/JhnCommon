package jhn.wp;

import jhn.wp.exceptions.CountException;

public interface CorpusEventHandler {
	void beforeEverything() throws Exception;

	void beforeLabel();

	void visitLabel(String label) throws CountException;
	
	void visitDocument(String text);

	void visitWord(String word);

	void afterLabel() throws Exception;

	void afterEverything() throws Exception;
}
