package jhn.wp;

import java.io.FileNotFoundException;
import java.util.regex.Pattern;

import jhn.util.Log;
import jhn.util.Util;
import jhn.wp.visitors.Visitor;

public abstract class CorpusProcessor {
	protected final Log log;
	protected final Log errLog;
	protected final CorpusEventDispatcher events;
	
	public CorpusProcessor(String logFilename, String errLogFilename) throws FileNotFoundException {
		this.log = new Log(System.out, logFilename);
		this.errLog = new Log(errLogFilename);
		this.events = new CorpusEventDispatcher();
	}

	public abstract void process() throws Exception;
	
	protected boolean isStopword(String s) {
		return Util.stopwords().contains(s);
	}
	
	private static final Pattern tokenSplitRgx = Pattern.compile("[^a-z]");
	protected String[] tokenize(final String s) {
		return tokenSplitRgx.split(s.toLowerCase());
	}
	
	public void addVisitor(Visitor v) {
		events.addVisitor(v);
	}
}
