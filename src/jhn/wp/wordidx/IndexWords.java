package jhn.wp.wordidx;

import jhn.Paths;
import jhn.wp.ArticlesProcessor;
import jhn.wp.CorpusProcessor;
import jhn.wp.visitors.PrintingVisitor;

public class IndexWords {
//	// Index words
//	public static void main(String[] args) throws Exception {
//		final String indexDir = Paths.outputDir("JhnCommon") + "/indices/words";
//		
//		final String logFilename = indexDir + "/main.log";
//		final String errLogFilename = indexDir + "/main.err";
//		
//		final String srcDir = System.getenv("HOME") + "/Data/wikipedia.org";
//		final String articlesFilename = srcDir + "/enwiki-20120403-pages-articles.xml.bz2";
//		
//		
//		CorpusProcessor ac = new ArticlesProcessor(articlesFilename, logFilename, errLogFilename);
//		ac.addVisitor(new PrintingVisitor());
//		ac.addVisitor(new WordIndexingVisitor(indexDir + "/words.idx"));
//		ac.process();
//	}
	
	// Aggregate words
	public static void main(String[] args) throws Exception {
		final String indexDir = Paths.outputDir("JhnCommon") + "/word_sets";
		
		final String logFilename = indexDir + "/main.log";
		final String errLogFilename = indexDir + "/main.err";
		
		final String srcDir = System.getenv("HOME") + "/Data/wikipedia.org";
		final String articlesFilename = srcDir + "/enwiki-20120403-pages-articles.xml.bz2";
		
		
		CorpusProcessor ac = new ArticlesProcessor(articlesFilename, logFilename, errLogFilename);
		ac.addVisitor(new PrintingVisitor());
		ac.addVisitor(new ChunkedWordSetVisitor(500000, 1000000, indexDir+"/chunks"));
		ac.process();
	}
	
//	// Index words in a trie
//	public static void main(String[] args) {
//		final String outputDir = System.getenv("HOME") + "/Projects/eda_output";
//		
//		final String name = "wordIndexTrie";
//		final String outputFilename = outputDir + "/" + name + ".ser";
//		final String logFilename = outputDir + "/" + name + ".log";
//		final String errLogFilename = outputDir + "/" + name + ".err.log";
//		
//		final String srcDir = System.getenv("HOME") + "/Data/wikipedia.org";
//		final String articlesFilename = srcDir + "/enwiki-20120403-pages-articles.xml.bz2";
//		
//		
//		CorpusProcessor ac = new ArticlesProcessor(articlesFilename, logFilename, errLogFilename);
//		ac.addVisitor(new PrintingVisitor());
//		ac.addVisitor(new WordIndexVisitorTrie(outputFilename));
//		ac.count();
//	}
}
