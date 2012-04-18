package jhn.wp.categories;

import java.io.BufferedReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jhn.util.Util;
import jhn.wp.CorpusProcessor;
import jhn.wp.exceptions.CountException;

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
			while( (line=r.readLine()) != null ) {//&& count < 1000) {
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
}
