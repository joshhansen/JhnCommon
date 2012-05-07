package jhn.wp.categories;

import java.io.BufferedReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jhn.util.Util;
import jhn.wp.CorpusProcessor;
import jhn.wp.Fields;
import jhn.wp.exceptions.CountException;
import jhn.wp.visitors.LuceneVisitor2;
import jhn.wp.visitors.PrintingVisitor;

public class ArticleCategoryProcessor extends CorpusProcessor {
	private final String articleCategoriesFilename;
	public ArticleCategoryProcessor(String logFilename, String errLogFilename, String articleCategoriesFilename) {
		super(logFilename, errLogFilename);
		this.articleCategoriesFilename = articleCategoriesFilename;
	}

	//<http://dbpedia.org/resource/Aristotle> <http://purl.org/dc/terms/subject> <http://dbpedia.org/resource/Category:Ancient_Greek_philosophers> .
	private static final String articleS = "<http://dbpedia\\.org/resource/([^>]+)> <http://purl\\.org/dc/terms/subject> <http://dbpedia\\.org/resource/(Category:[^>]+)> \\.";
	private static final Pattern articleRgx = Pattern.compile(articleS);
	@Override
	public void count() {
		int count = 0;
		try {
			beforeEverything();
			
			String prevLabel = null;
			BufferedReader r = new BufferedReader(Util.smartReader(articleCategoriesFilename));
			String line = null;
			
			boolean everMatched = false;
			while( (line=r.readLine()) != null) {// && count < 10000) {
				Matcher m = articleRgx.matcher(line);
				if(m.matches()) {
					everMatched |= true;
					
					final String article = m.group(1);
					final String category = m.group(2);

					if(!article.equals(prevLabel)) {
						if(prevLabel != null) {
							afterLabel();
						}
						beforeLabel();
						
						try {
							visitLabel(article);
						} catch(CountException e) {
							System.err.print(e.shortCode());
						}
					}
					
					visitWord(category);
					
					prevLabel = article;
				} else {
					System.err.println("Can't parse line: " + line);
				}
				count++;
			}
			r.close();
			
			if(everMatched) afterLabel();
			afterEverything();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		final String outputDir = System.getenv("HOME") + "/Projects/eda_output/indices";
		final String name = "article_categories";
		
		final String logFilename = outputDir + "/" + name + ".log";
		final String errLogFilename = outputDir + "/" + name + ".error.log";
		
		final String srcDir = System.getenv("HOME") + "/Data/dbpedia.org/3.7";
		final String articleCategoriesFilename = srcDir + "/article_categories_en.nt.bz2";
		
		ArticleCategoryProcessor acc = new ArticleCategoryProcessor(logFilename, errLogFilename, articleCategoriesFilename);
		
		
		final String luceneDir = outputDir + "/" + name;
		
		acc.addVisitor(new LuceneVisitor2(luceneDir, Fields.articleCategory, false, true));
		acc.addVisitor(new PrintingVisitor());
		
		acc.count();
	}
}
