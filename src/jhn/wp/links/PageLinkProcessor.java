package jhn.wp.links;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jhn.util.Util;
import jhn.wp.CorpusProcessor;
import jhn.wp.exceptions.CountException;

public class PageLinkProcessor extends CorpusProcessor {

	private final String pageLinksFilename;
	public PageLinkProcessor(String logFilename, String errLogFilename, String pageLinksFilename) throws FileNotFoundException {
		super(logFilename, errLogFilename);
		this.pageLinksFilename = pageLinksFilename;
	}

//	<http://dbpedia.org/resource/Anarchism> <http://dbpedia.org/ontology/wikiPageWikiLink> <http://dbpedia.org/resource/Mikhail_Bakunin> .
	private static final String lineS = "<http://dbpedia\\.org/resource/([^>]+)> <http://dbpedia\\.org/ontology/wikiPageWikiLink> <http://dbpedia\\.org/resource/([^>]+)> \\.";
	private static final Pattern lineRgx = Pattern.compile(lineS);
	@Override
	public void process() {
		try(BufferedReader r = new BufferedReader(Util.smartReader(pageLinksFilename))) {
			events.beforeEverything();
			
			String prevLabel = null;
			String line = null;
			
			boolean everMatched = false;
			while( (line=r.readLine()) != null) {// && count < 1000) {
				Matcher m = lineRgx.matcher(line);
				if(m.matches()) {
					everMatched |= true;
					
					final String src = URLDecoder.decode(m.group(1), "UTF-8");
					final String dest = URLDecoder.decode(m.group(2), "UTF-8");

					if(!src.equals(prevLabel)) {
						if(prevLabel != null) {
							events.afterLabel();
						}
						events.beforeLabel();
						
						try {
							events.visitLabel(src);
						} catch(CountException e) {
							System.err.print(e.shortCode());
						}
					}
					
					events.visitWord(dest);
					
					prevLabel = src;
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
