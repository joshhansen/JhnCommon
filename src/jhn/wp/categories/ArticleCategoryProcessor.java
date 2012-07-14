package jhn.wp.categories;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jhn.util.Util;
import jhn.wp.CorpusProcessor;
import jhn.wp.exceptions.CountException;

public class ArticleCategoryProcessor extends CorpusProcessor {
	private final String articleCategoriesFilename;
	public ArticleCategoryProcessor(String logFilename, String errLogFilename, String articleCategoriesFilename) throws FileNotFoundException {
		super(logFilename, errLogFilename);
		this.articleCategoriesFilename = articleCategoriesFilename;
	}

	//<http://dbpedia.org/resource/Aristotle> <http://purl.org/dc/terms/subject> <http://dbpedia.org/resource/Category:Ancient_Greek_philosophers> .
	private static final String articleS = "<http://dbpedia\\.org/resource/([^>]+)> <http://purl\\.org/dc/terms/subject> <http://dbpedia\\.org/resource/(Category:[^>]+)> \\.";
	private static final Pattern articleRgx = Pattern.compile(articleS);
	@Override
	public void process() {
		try(BufferedReader r = new BufferedReader(Util.smartReader(articleCategoriesFilename))) {
			events.beforeEverything();
			
			String prevLabel = null;
			
			String line = null;
			
			boolean everMatched = false;
			while( (line=r.readLine()) != null) {// && count < 10000) {
				Matcher m = articleRgx.matcher(line);
				if(m.matches()) {
					everMatched |= true;
					
					final String article = URLDecoder.decode(m.group(1), "UTF-8");
					final String category = URLDecoder.decode(m.group(2), "UTF-8");

					if(!article.equals(prevLabel)) {
						if(prevLabel != null) {
							events.afterLabel();
						}
						events.beforeLabel();
						
						try {
							events.visitLabel(article);
						} catch(CountException e) {
							System.err.print(e.shortCode());
						}
					}
					
					events.visitWord(category);
					
					prevLabel = article;
				} else {
					System.err.println("Can't parse line: " + line);
				}
			}
			
			if(everMatched) events.afterLabel();
			
			events.afterEverything();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
