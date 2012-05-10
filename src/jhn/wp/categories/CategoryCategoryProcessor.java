package jhn.wp.categories;

import java.io.BufferedReader;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jhn.util.Util;
import jhn.wp.CorpusProcessor;
import jhn.wp.Fields;
import jhn.wp.exceptions.CountException;
import jhn.wp.visitors.LuceneVisitor2;
import jhn.wp.visitors.PrintingVisitor;


public class CategoryCategoryProcessor extends CorpusProcessor {
	private final String categoryCategoriesFilename;
	public CategoryCategoryProcessor(String logFilename, String errLogFilename, String categoryCategoriesFilename) {
		super(logFilename, errLogFilename);
		this.categoryCategoriesFilename = categoryCategoriesFilename;
	}

	//<http://dbpedia.org/resource/Category:World_War_II> <http://www.w3.org/2004/02/skos/core#broader> <http://dbpedia.org/resource/Category:Wars_involving_the_Soviet_Union> .	
	private static final String categoryS = "<http://dbpedia\\.org/resource/(Category:[^>]+)> <http://www\\.w3\\.org/2004/02/skos/core#broader> <http://dbpedia\\.org/resource/(Category:[^>]+)> \\.";
	private static final Pattern categoryRgx = Pattern.compile(categoryS);
	
	@Override
	public void count() {
		int count = 0;
		try {
			beforeEverything();
			
			String prevLabel = null;
			BufferedReader r = new BufferedReader(Util.smartReader(categoryCategoriesFilename));
			String line = null;
			
			boolean everMatched = false;
			while( (line=r.readLine()) != null) {// && count < 10000) {
				if(line.contains("<http://www.w3.org/2004/02/skos/core#broader>")) {
					Matcher m = categoryRgx.matcher(line);
					if(m.matches()) {
						everMatched |= true;
						
						final String childCat = URLDecoder.decode(m.group(1), "UTF-8");
						final String parentCat = URLDecoder.decode(m.group(2), "UTF-8");
	
						if(!childCat.equals(prevLabel)) {
							if(prevLabel != null) {
								afterLabel();
							}
							beforeLabel();
							
							try {
								visitLabel(childCat);
							} catch(CountException e) {
								System.err.print(e.shortCode());
							}
						}
						
						visitWord(parentCat);
						
						prevLabel = childCat;
					} else {
						System.err.println("Can't parse line: " + line);
					}
					count++;
				}
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
		final String name = "category_categories";
		
		final String logFilename = outputDir + "/" + name + ".log";
		final String errLogFilename = outputDir + "/" + name + ".error.log";
		
		final String srcDir = System.getenv("HOME") + "/Data/dbpedia.org/3.7";
		final String categoryCategoriesFilename = srcDir + "/skos_categories_en.nt.bz2";
		
		CategoryCategoryProcessor ccc = new CategoryCategoryProcessor(logFilename, errLogFilename, categoryCategoriesFilename);
		
		final String luceneDir = outputDir + "/" + name;
		
		ccc.addVisitor(new LuceneVisitor2(luceneDir, Fields.categoryCategory, false, true));
		ccc.addVisitor(new PrintingVisitor());
		
		ccc.count();
	}
}