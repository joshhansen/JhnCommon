package jhn.wp.cocounts;

import jhn.Paths;
import jhn.wp.ArticlesProcessor;
import jhn.wp.CorpusProcessor;
import jhn.wp.visitors.PrintingVisitor;

public class CountWordCooccurrences {
	// Generate chunked co-occurrence counts
	public static void main(String[] args) throws Exception {
		final String cocountsDir = Paths.outputDir("JhnCommon") + "/cocounts";
//		final String outputDir = cocountsDir;// + "/depth";
		final String outputDir = cocountsDir + "/test";
		
		final String logFilename = outputDir + "/main.log";
		final String errLogFilename = outputDir + "/main.err";
		
		final String srcDir = System.getenv("HOME") + "/Data/wikipedia.org";
		final String articlesFilename = srcDir + "/enwiki-20120403-pages-articles.xml.bz2";
		
		final String wordIdxFilename = Paths.outputDir("JhnCommon") + "/word_sets/chunks/19.set";
		
		CorpusProcessor ac = new ArticlesProcessor(articlesFilename, logFilename, errLogFilename);
		ac.addVisitor(new PrintingVisitor());
		ac.addVisitor(new WindowedCocountVisitor(outputDir, wordIdxFilename, 250000000, 20));
//		ac.addVisitor(new WindowedCocountVisitor(outputDir, 1000, 20));
		ac.process();
	}
}
