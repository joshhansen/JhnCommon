package jhn.wp.categories;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jhn.util.Util;
import jhn.wp.CorpusProcessor;
import jhn.wp.exceptions.CountException;


public class CategoryCategoryProcessor extends CorpusProcessor {
	private final String categoryCategoriesFilename;
	public CategoryCategoryProcessor(String logFilename, String errLogFilename, String categoryCategoriesFilename) throws FileNotFoundException {
		super(logFilename, errLogFilename);
		this.categoryCategoriesFilename = categoryCategoriesFilename;
	}

	//<http://dbpedia.org/resource/Category:World_War_II> <http://www.w3.org/2004/02/skos/core#broader> <http://dbpedia.org/resource/Category:Wars_involving_the_Soviet_Union> .	
	private static final String categoryS = "<http://dbpedia\\.org/resource/(Category:[^>]+)> <http://www\\.w3\\.org/2004/02/skos/core#broader> <http://dbpedia\\.org/resource/(Category:[^>]+)> \\.";
	private static final Pattern categoryRgx = Pattern.compile(categoryS);
	
	@Override
	public void process() {
		try(BufferedReader r = new BufferedReader(Util.smartReader(categoryCategoriesFilename))) {
			events.beforeEverything();
			
			String prevLabel = null;
			
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
								events.afterLabel();
							}
							events.beforeLabel();
							
							try {
								events.visitLabel(childCat);
							} catch(CountException e) {
								System.err.print(e.shortCode());
							}
						}
						
						events.visitWord(parentCat);
						
						prevLabel = childCat;
					} else {
						System.err.println("Can't parse line: " + line);
					}
				}
			}
			
			if(everMatched) events.afterLabel();
			
			events.afterEverything();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
