package jhn.wp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import jhn.util.Util;
import jhn.wp.exceptions.CountException;
import jhn.wp.exceptions.ArticleTooShort;
import jhn.wp.visitors.Visitor;

public abstract class CorpusCounter {
	private Set<String> stopwords = Util.stopwords();
	private List<Visitor> visitors = new ArrayList<Visitor>();
	
	public abstract void count();
	
	protected boolean isStopword(String s) {
		return stopwords.contains(s);
	}
	
	protected void beforeEverything() throws Exception {
		for (Visitor v : visitors)
			v.beforeEverything();
	}

	protected void beforeLabel() {
		for (Visitor v : visitors)
			v.beforeLabel();
	}

	protected void visitLabel(final String label) throws CountException {
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

	protected void visitWord(final String word) {
		for (Visitor v : visitors)
			v.visitWord(word);
	}

	protected void afterLabel() throws Exception {
		for (Visitor v : visitors) {
			v.afterLabel();
		}
	}

	protected void afterEverything() {
		for (Visitor v : visitors)
			v.afterEverything();
	}

	public void addVisitor(Visitor v) {
		visitors.add(v);
	}

	public List<Visitor> visitors() {
		return Collections.unmodifiableList(visitors);
	}
	
	private static final Pattern tokenSplitRgx = Pattern.compile("[^a-z]");
	protected String[] tokenize(final String s) {
		return tokenSplitRgx.split(s.toLowerCase());
	}
}
