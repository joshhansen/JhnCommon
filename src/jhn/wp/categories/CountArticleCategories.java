package jhn.wp.categories;

import jhn.wp.visitors.LuceneVisitor;
import jhn.wp.visitors.PrintingVisitor;

public class CountArticleCategories {
	public static void main(String[] args) {
		final String outputDir = System.getenv("HOME") + "/Projects/eda_output";
		final String name = "article_categories";
		
		final String logFilename = outputDir + "/" + name + ".log";
		final String errLogFilename = outputDir + "/" + name + ".error.log";
		
		final String srcDir = System.getenv("HOME") + "/Data/dbpedia.org/3.7";
		final String articleCategoriesFilename = srcDir + "/article_categories_en.nt.bz2";
		
		ArticleCategoryCounter acc = new ArticleCategoryCounter(logFilename, errLogFilename, articleCategoriesFilename);
		
		
		final String luceneDir = outputDir + "/" + name;
		
		acc.addVisitor(new LuceneVisitor(luceneDir, false, true));
		acc.addVisitor(new PrintingVisitor());
		
		acc.count();
	}
}
