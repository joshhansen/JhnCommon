package jhn.wp.fulltext;

import java.io.FileNotFoundException;

import jhn.wp.ArticlesProcessor;
import jhn.wp.CorpusProcessor;
import jhn.wp.Fields;
import jhn.wp.visitors.PrintingVisitor;

/** Index article text in Lucene */
public class IndexFullText {
	public static void main(String[] args) throws Exception {
		final String outputDir = System.getenv("HOME") + "/Projects/eda_output";
		final String idxDir = outputDir + "/indices/topic_words";
		final String name = "wp_lucene5";
		final String luceneDir = idxDir + "/" + name;
		final String logFilename = idxDir + "/" + name + ".log";
		final String errLogFilename = idxDir + "/" + name + ".error.log";
		
		final String srcDir = System.getenv("HOME") + "/Data/wikipedia.org";
		final String articlesFilename = srcDir + "/enwiki-20120403-pages-articles.xml.bz2";
		
		
		CorpusProcessor ac = new ArticlesProcessor(articlesFilename, logFilename, errLogFilename);
		ac.addVisitor(new PrintingVisitor());
		ac.addVisitor(new FulltextIndexingVisitor(luceneDir, Fields.text));
		ac.process();
	}
}
