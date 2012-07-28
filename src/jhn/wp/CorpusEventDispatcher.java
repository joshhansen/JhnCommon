package jhn.wp;

import java.util.ArrayList;
import java.util.List;

import jhn.wp.exceptions.ArticleTooShort;
import jhn.wp.exceptions.CountException;
import jhn.wp.visitors.Visitor;

public class CorpusEventDispatcher implements CorpusEventHandler, AutoCloseable {
	
	private List<Visitor> visitors = new ArrayList<>();

	public void addVisitor(Visitor v) {
		visitors.add(v);
	}

	@Override
	public void beforeEverything() throws Exception {
		for (Visitor v : visitors) {
			v.beforeEverything();
		}
	}

	@Override
	public void beforeLabel() {
		for (Visitor v : visitors) {
			v.beforeLabel();
		}
	}

	@Override
	public void visitLabel(final String label) throws CountException {
		boolean tooShort = false;
		int length = -1;
		for (Visitor v : visitors) {
			try {
				v.visitLabel(label);
			} catch(ArticleTooShort e) {
				tooShort = true;
				length = e.length();
			} catch(CountException e) {
				throw e;
			}
		}
		
		if(tooShort) throw new ArticleTooShort(label, length);
	}
	
	@Override
	public void visitDocument(String text) {
		for(Visitor v : visitors) {
			v.visitDocument(text);
		}
	}

	@Override
	public void visitWord(final String word) {
		for (Visitor v : visitors)
			v.visitWord(word);
	}

	@Override
	public void afterLabel() throws Exception {
		for (Visitor v : visitors) {
			v.afterLabel();
		}
	}

	@Override
	public void afterEverything() throws Exception {
		for (Visitor v : visitors) {
			v.afterEverything();
		}
	}

	@Override
	public void close() throws Exception {
		for(Visitor v : visitors) {
			if(v instanceof AutoCloseable) {
				((AutoCloseable) v).close();
			}
		}
	}

}
