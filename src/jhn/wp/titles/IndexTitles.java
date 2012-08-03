package jhn.wp.titles;

import jhn.Paths;
import jhn.wp.ArticlesProcessor;
import jhn.wp.visitors.PrintingVisitor;

public class IndexTitles {
	public static void main(String[] args) throws Exception {
		final String outputDir = Paths.jhncOutputDir();
		final String idxDir = outputDir + "/indices/titles";
		final String name = Paths.wikipediaDumpName();
		final String luceneDir = idxDir + "/" + name;
		final String logFilename = idxDir + "/" + name + ".log";
		final String errLogFilename = idxDir + "/" + name + ".error.log";
		
		ArticlesProcessor ap = new ArticlesProcessor(Paths.wikipediaArticlesFilename(), logFilename, errLogFilename);
		ap.conf.putBool(ArticlesProcessor.Options.SKIP_DISAMBIGUATION, false);
		ap.conf.putBool(ArticlesProcessor.Options.SKIP_REDIRECTS, false);
		
		ap.addVisitor(new PrintingVisitor());
		ap.addVisitor(new TitleIndexingVisitor(luceneDir));
		ap.process();
	}
}
