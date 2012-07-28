package jhn.wp.counts;

import jhn.Paths;
import jhn.wp.ArticlesProcessor;
import jhn.wp.CorpusProcessor;
import jhn.wp.visitors.PrintingVisitor;

public class CountWordOccurrences {
	public static void main(String[] args) throws Exception {
		final String outputDir = Paths.outputDir("JhnCommon") + "/counts";
		
		final String name = "counts";
		final String wordIdxFilename = Paths.outputDir("JhnCommon") + "/word_sets/chunks/19.set";
//		final String outputFilename = outputDir + "/" + name + ".sqlite3";
		final String chunkDir = outputDir + "/chunks";
		final String logFilename = outputDir + "/" + name + ".log";
		final String errLogFilename = outputDir + "/" + name + ".err.log";
		
		final String srcDir = System.getenv("HOME") + "/Data/wikipedia.org";
		final String articlesFilename = srcDir + "/enwiki-20120403-pages-articles.xml.bz2";
		
		
		try(CorpusProcessor ac = new ArticlesProcessor(articlesFilename, logFilename, errLogFilename)) {
			ac.addVisitor(new PrintingVisitor());
//			ac.addVisitor(new WordCountVisitorSQLite(outputFilename));
			ac.addVisitor(new WordCountVisitorChunked(wordIdxFilename, chunkDir));
			ac.process();
		}
	}
}
