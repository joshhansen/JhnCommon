package jhn.wp.categories;

import java.io.BufferedReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jhn.util.Util;
import jhn.wp.CorpusCounter;
import jhn.wp.exceptions.CountException;


public class CategoryCategoryCounter extends CorpusCounter {
	private final String categoryCategoriesFilename;
	public CategoryCategoryCounter(String logFilename, String errLogFilename, String categoryCategoriesFilename) {
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
			while( (line=r.readLine()) != null) {// && count < 1000) {
				if(line.contains("<http://www.w3.org/2004/02/skos/core#broader>")) {
					Matcher m = categoryRgx.matcher(line);
					if(m.matches()) {
						everMatched |= true;
						
						final String childCat = m.group(1);
						final String parentCat = m.group(2);
	
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
}
