package jhn.wp.counts;

import jhn.wp.ArticlesProcessor;
import jhn.wp.CorpusProcessor;
import jhn.wp.visitors.PrintingVisitor;

public class CountWordOccurrences {
	// Count words in a trie
	public static void main(String[] args) throws Exception {
		final String outputDir = System.getenv("HOME") + "/Projects/eda_output";
		
		final String name = "wordCountsTrie";
		final String outputFilename = outputDir + "/" + name + ".ser";
		final String logFilename = outputDir + "/" + name + ".log";
		final String errLogFilename = outputDir + "/" + name + ".err.log";
		
		final String srcDir = System.getenv("HOME") + "/Data/wikipedia.org";
		final String articlesFilename = srcDir + "/enwiki-20120403-pages-articles.xml.bz2";
		
		
		CorpusProcessor ac = new ArticlesProcessor(articlesFilename, logFilename, errLogFilename);
		ac.addVisitor(new PrintingVisitor());
//		ac.addVisitor(new WordCountVisitorPatriciaTrie(outputFilename));
		ac.addVisitor(new WordCountVisitorTrie(outputFilename));
		ac.process();
	}
}
