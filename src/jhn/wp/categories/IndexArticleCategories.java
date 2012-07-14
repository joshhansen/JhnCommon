package jhn.wp.categories;

import jhn.wp.CorpusProcessor;
import jhn.wp.Fields;
import jhn.wp.visitors.PrintingVisitor;

public class IndexArticleCategories {
	public static void main(String[] args) throws Exception {
		final String outputDir = System.getenv("HOME") + "/Projects/eda_output/indices";
		final String name = "article_categories";
		
		final String logFilename = outputDir + "/" + name + ".log";
		final String errLogFilename = outputDir + "/" + name + ".error.log";
		
		final String srcDir = System.getenv("HOME") + "/Data/dbpedia.org/3.7";
		final String articleCategoriesFilename = srcDir + "/article_categories_en.nt.bz2";
		
		CorpusProcessor acc = new ArticleCategoryProcessor(logFilename, errLogFilename, articleCategoriesFilename);
		
		
		final String luceneDir = outputDir + "/" + name;
		
		acc.addVisitor(new ItemwiseLuceneIndexingVisitor(luceneDir, Fields.articleCategory));
		acc.addVisitor(new PrintingVisitor());
		
		acc.process();
	}
}
