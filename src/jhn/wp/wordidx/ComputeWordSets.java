package jhn.wp.wordidx;

import java.io.FileNotFoundException;

import jhn.Paths;
import jhn.wp.ArticlesProcessor;
import jhn.wp.CorpusProcessor;
import jhn.wp.visitors.PrintingVisitor;

public class ComputeWordSets {
	// Aggregate words
	public static void main(String[] args) throws Exception {
		final int baseChunkSize = 500000;
		final int chunkSize = 1000000;
		
		String articlesFilename, destDir;
		if(args.length > 0) {
			articlesFilename = args[0];
			destDir = args[1];
		} else {
			destDir = Paths.outputDir("JhnCommon") + "/word_sets";
			articlesFilename = Paths.dataDir("wikipedia.org") + "/enwiki-20120403-pages-articles.xml.bz2";
		}
		aggregateWords(articlesFilename, destDir + "/main.log", destDir + "/main.err", destDir +"/chunks",
				baseChunkSize, chunkSize);
	}
	
	public static void aggregateWords(String articlesFilename, String logFilename, String errLogFilename,
			String outputDir, int baseChunkSize, int chunkSize) throws FileNotFoundException, Exception {
		try(CorpusProcessor ac = new ArticlesProcessor(articlesFilename, logFilename, errLogFilename)) {
			ac.addVisitor(new PrintingVisitor());
			ac.addVisitor(new ChunkedWordSetVisitor(baseChunkSize, chunkSize, outputDir));
			ac.process();
		}
	}
}
