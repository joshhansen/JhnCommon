package jhn.wp;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import jhn.util.Util;
import jhn.wp.exceptions.ArticleTooShort;
import jhn.wp.exceptions.CountException;
import jhn.wp.visitors.Visitor;

public abstract class CorpusCounter {
	private List<Visitor> visitors = new ArrayList<Visitor>();
	
	protected PrintStream log;
	protected PrintStream errLog;
	
	private final String logFilename;
	private final String errLogFilename;
	
	public CorpusCounter(String logFilename, String errLogFilename) {
		this.logFilename = logFilename;
		this.errLogFilename = errLogFilename;
	}

	public abstract void count();
	
	protected void print(String s) {
		System.out.print(s);
		log.append(s);
	}
	
	protected void println(String s) {
		print(s);
		print("\n");
	}
	
	protected void printErr(String s) {
//		System.err.print(s);
		errLog.append(s);
	}
	
	protected void printlnErr(String s) {
		printErr(s);
		printErr("\n");
	}
	
	protected void printBoth(String s) {
		System.out.print(s);
		log.append(s);
		errLog.append(s);
	}
	
	protected void printlnBoth(String s) {
		printBoth(s);
		printBoth("\n");
	}
	
	protected boolean isStopword(String s) {
		return Util.stopwords().contains(s);
	}
	
	protected void beforeEverything() throws Exception {
		log = new PrintStream(new FileOutputStream(logFilename), true);
		errLog = new PrintStream(new FileOutputStream(errLogFilename), true);
		
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
		for (Visitor v : visitors) {
			v.afterEverything();
		}
		
		log.close();
		errLog.close();
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
