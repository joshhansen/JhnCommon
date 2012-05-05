package jhn.wp.links;

import java.io.BufferedReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jhn.util.Util;
import jhn.wp.CorpusProcessor;
import jhn.wp.Fields;
import jhn.wp.exceptions.CountException;
import jhn.wp.visitors.LuceneVisitor;
import jhn.wp.visitors.PrintingVisitor;

public class PageLinkProcessor extends CorpusProcessor {

	private final String pageLinksFilename;
	public PageLinkProcessor(String logFilename, String errLogFilename, String pageLinksFilename) {
		super(logFilename, errLogFilename);
		this.pageLinksFilename = pageLinksFilename;
	}

//	<http://dbpedia.org/resource/Anarchism> <http://dbpedia.org/ontology/wikiPageWikiLink> <http://dbpedia.org/resource/Mikhail_Bakunin> .
	private static final String lineS = "<http://dbpedia\\.org/resource/([^>]+)> <http://dbpedia\\.org/ontology/wikiPageWikiLink> <http://dbpedia\\.org/resource/([^>]+)> \\.";
	private static final Pattern lineRgx = Pattern.compile(lineS);
	@Override
	public void count() {
		int count = 0;
		try {
			beforeEverything();
			
			String prevLabel = null;
			BufferedReader r = new BufferedReader(Util.smartReader(pageLinksFilename));
			String line = null;
			
			boolean everMatched = false;
			while( (line=r.readLine()) != null) {// && count < 1000) {
				Matcher m = lineRgx.matcher(line);
				if(m.matches()) {
					everMatched |= true;
					
					final String src = m.group(1);
					final String dest = m.group(2);

					if(!src.equals(prevLabel)) {
						if(prevLabel != null) {
							afterLabel();
						}
						beforeLabel();
						
						try {
							visitLabel(src);
						} catch(CountException e) {
							System.err.print(e.shortCode());
						}
					}
					
					visitWord(dest);
					
					prevLabel = src;
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
		final String name = "page_links";
		
		final String logFilename = outputDir + "/" + name + ".log";
		final String errLogFilename = outputDir + "/" + name + ".error.log";
		
		final String srcDir = System.getenv("HOME") + "/Data/dbpedia.org/3.7";
		final String pageLinksFilename = srcDir + "/page_links_en.nt.bz2";
		
		PageLinkProcessor acc = new PageLinkProcessor(logFilename, errLogFilename, pageLinksFilename);
		
		final String luceneDir = outputDir + "/" + name;
		
		acc.addVisitor(new LuceneVisitor(luceneDir, Fields.links, false, true));
		acc.addVisitor(new PrintingVisitor());
		
		acc.count();
	}
}
