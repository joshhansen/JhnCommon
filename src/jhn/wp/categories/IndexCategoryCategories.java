package jhn.wp.categories;

import jhn.wp.CorpusProcessor;
import jhn.wp.Fields;
import jhn.wp.visitors.PrintingVisitor;

public class IndexCategoryCategories {
	public static void main(String[] args) throws Exception {
		final String outputDir = System.getenv("HOME") + "/Projects/eda_output/indices";
		final String name = "category_categories";
		
		final String logFilename = outputDir + "/" + name + ".log";
		final String errLogFilename = outputDir + "/" + name + ".error.log";
		
		final String srcDir = System.getenv("HOME") + "/Data/dbpedia.org/3.7";
		final String categoryCategoriesFilename = srcDir + "/skos_categories_en.nt.bz2";
		
		CorpusProcessor ccc = new CategoryCategoryProcessor(logFilename, errLogFilename, categoryCategoriesFilename);
		
		final String luceneDir = outputDir + "/" + name;
		
		ccc.addVisitor(new ItemwiseLuceneIndexingVisitor(luceneDir, Fields.categoryCategory));
		ccc.addVisitor(new PrintingVisitor());
		
		ccc.process();
	}
}
