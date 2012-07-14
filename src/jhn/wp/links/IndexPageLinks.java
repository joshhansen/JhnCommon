package jhn.wp.links;

import jhn.wp.CorpusProcessor;
import jhn.wp.Fields;
import jhn.wp.categories.ItemwiseLuceneIndexingVisitor;
import jhn.wp.visitors.PrintingVisitor;

public class IndexPageLinks {
	public static void main(String[] args) throws Exception {
		final String outputDir = System.getenv("HOME") + "/Projects/eda_output/indices";
		final String name = "page_links";
		
		final String logFilename = outputDir + "/" + name + ".log";
		final String errLogFilename = outputDir + "/" + name + ".error.log";
		
		final String srcDir = System.getenv("HOME") + "/Data/dbpedia.org/3.7";
		final String pageLinksFilename = srcDir + "/page_links_en.nt.bz2";
		
		CorpusProcessor acc = new PageLinkProcessor(logFilename, errLogFilename, pageLinksFilename);
		
		final String luceneDir = outputDir + "/" + name;
		
		acc.addVisitor(new ItemwiseLuceneIndexingVisitor(luceneDir, Fields.linkedPage));
		acc.addVisitor(new PrintingVisitor());
		
		acc.process();
	}
}
