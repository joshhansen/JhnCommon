package jhn.wp;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.jhu.nlp.wikipedia.PageCallbackHandler;
import edu.jhu.nlp.wikipedia.WikiPage;
import edu.jhu.nlp.wikipedia.WikiXMLParser;
import edu.jhu.nlp.wikipedia.WikiXMLParserFactory;
import info.bliki.wiki.filter.ITextConverter;
import info.bliki.wiki.filter.PlainTextConverter;
import info.bliki.wiki.model.WikiModel;

import jhn.util.Config;
import jhn.wp.exceptions.ArticleTooShort;
import jhn.wp.exceptions.BadLabel;
import jhn.wp.exceptions.BadLabelPrefix;
import jhn.wp.exceptions.BadWikiTextException;
import jhn.wp.exceptions.CountException;
import jhn.wp.exceptions.DisambiguationPage;
import jhn.wp.exceptions.RedirectException;

public class ArticlesProcessor extends CorpusProcessor {
	public static enum Options {
		SKIP_DISAMBIGUATION,
		SKIP_REDIRECTS,
		SKIP_BAD_LABELS
	}
	
	
	private static final boolean PRINT_WIKI_TEXT = false;
	private static final boolean PRINT_PLAIN_TEXT = PRINT_WIKI_TEXT;
	
	public final Config conf = new Config();
	
	private final String wpdumpFilename;
	
	public ArticlesProcessor(String wpdumpFilename, String logFilename, String errLogFilename) throws FileNotFoundException {
		super(logFilename, errLogFilename);
		this.wpdumpFilename = wpdumpFilename;
	}
	
	private final PageCallbackHandler pageHandler = new PageCallbackHandler() {
		int badLabelPrefix = 0;
		int disambiguation = 0;
		int redirect = 0;
		int ok = 0;
		int tooShort = 0;
		int total = 0;
		
		@Override
		public void process(WikiPage page) {
			total++;
			final String label = page.getTitle().trim();
			try {
				
				assertLabelOK(label);
				
//				final String wikiText = page.getText();
				final String wikiText = page.getWikiText().replaceAll("<ref", " <ref");
				assertWikiTextOK(wikiText, label);
				
				if(PRINT_WIKI_TEXT) {
					System.out.println("-----------------------------WIKI TEXT----------------------------");
					
					System.out.println(wikiText);
					System.out.println();
				}
				
				ok++;
				if(ok % 100 == 0){
					if(ok > 0) {
						System.out.println();
					}
					if(ok % 500 == 0) {
						System.out.println();
						float okPct = (float)ok / (float)total;
						float redirectPct = (float)redirect / (float)total;
						float badLabelPrefixPct = (float)badLabelPrefix / (float)total;
						float disambigPct = (float)disambiguation / (float) total;
						float tooShortPct = (float)tooShort / (float)total;
						
						log.println(String.format("-----%s-----\n", label));
						log.println(String.format("ok:%d (%.2f) redirect:%d (%.2f) badLabel:%d (%.2f) disambig:%d (%.2f) tooShort:%d (%.2f) total:%d\n",
								ok, okPct, redirect, redirectPct, badLabelPrefix, badLabelPrefixPct, disambiguation, disambigPct, tooShort, tooShortPct, total));
					}
				}
				
				events.beforeLabel();
				try {
					events.visitLabel(label);
				} catch (CountException e) {
					for(PrintStream errLogPS : errLog.logs()) {
						e.printStackTrace(errLogPS);
					}
				}
				log.print(".");
//				System.out.println(label);
//				String headingsCleaned = cleanHeadings(wikiText);
				String fixedHeadings = fixBadHeadings(wikiText);
				String sansRefs = removeRefUrls(fixedHeadings);
				String text = wikiToText3(sansRefs).trim();
				
				events.visitDocument(text);
				
				if(PRINT_PLAIN_TEXT) {
//					System.out.println("-----------------------------HEADINGS------------------");
//					System.out.println(headingsCleaned);
//					System.out.println();
					
					System.out.println("-----------------------------PLAIN TEXT----------------------------");
					System.out.println(text);
					System.out.println();
				}
				
				String[] tokens = tokenize(text);
//				System.out.println("-----------------------------TOKENS----------------------------");
//				
//				for(String token : tokens) {
//					System.out.print(token);
//					System.out.print(' ');
//				}
//				System.out.println();
				
				for(int tokenIdx = 0; tokenIdx < tokens.length; tokenIdx++) {
					events.visitWord(tokens[tokenIdx]);
				}
				
				events.afterLabel();
			} catch(RedirectException e) {
				log.print("r");
				errLog.println("Redirect: " + e.label());
				redirect++;
			} catch(BadWikiTextException e) {
				log.print("t");
				errLog.println("Other text problem: " + e.label());
			} catch (ArticleTooShort e) {
				log.print("s");
				errLog.println("Too short: " + e.label());
				tooShort++;
			} catch(BadLabelPrefix e) {
				log.print("l");
				errLog.println("Bad prefix: " + e.label());
				badLabelPrefix++;
			} catch(DisambiguationPage e) {
				log.print("d");
				errLog.println("Disambiguation: " + e.label());
				disambiguation++;
			} catch (Exception e) {
				for(PrintStream errLogPS : errLog.logs()) {
					e.printStackTrace(errLogPS);
				}
				log.print("u");
				errLog.println("Unknown: " + label);
			}
		}
	};

	@Override
	public void process() throws Exception {
		log.println(conf);
		
		events.beforeEverything();
		
		WikiXMLParser wxsp = WikiXMLParserFactory.getSAXParser(wpdumpFilename);
		wxsp.setPageCallback(pageHandler);
		wxsp.parse();
		
		events.afterEverything();
	}
	
	
	private static final WikiModel wikiModel = new WikiModel("http://www.mywiki.com/wiki/${image}", "http://www.mywiki.com/wiki/${title}");
	private static final ITextConverter conv = new PlainTextConverter();
	private static String wikiToText3(String markup) {
		return wikiModel.render(conv, markup);
	}
	
	private static final Pattern refUrlRgx = Pattern.compile("<ref>[^<]+?</ref>");
	private static String removeRefUrls(String markup) {
		return refUrlRgx.matcher(markup).replaceAll(" ");
	}
	
	/** A "bad" heading is one followed by a single newline character rather than multiple newline characters. This is
	 * "bad" because the wiki-to-plaintext converter mistakenly combines the heading text with the first word following.
	 * 
	 * For example:
	 *    === HeaderA ===
	 *    Some text
	 *    
	 *    === HeaderB ===
	 *    
	 *    Some text
	 * 
	 * becomes
	 *     HeaderASome text
	 *     HeaderB Some text
	 * This produces many tokens that are the concatenation of two other tokens
	 * */
	private static final Pattern badHeading = Pattern.compile("(==+)([^=]+?)\\1\\n([^\\n])");
	private static String fixBadHeadings(String markup) {
		
		Matcher m = badHeading.matcher(markup);
		
		return m.replaceAll("$1$2$1\n\n$3");
	}
	
	private static String[] dontStartWithThese = {
		"List of ",
		"Portal:",
		"Glossary of ",
		"Index of ",
		"Wikipedia:",
		"Category:",
		"File:",
		"Template:",
		"Book:", /* collections of pages */
		"MediaWiki:", /* wiki software info */
		"Help:", /* wikipedia help */
		"P:", /* redirects to portals */
//		"UN/LOCODE:", /* redirects to locations */
//		"ISO:", /* redirects to ISO standards */
//		"ISO 639:" /* redirects to languages */
	};
	
	private static void assertLabelOK(String label) throws BadLabel {
		for(String badStart : dontStartWithThese) {
			if(label.startsWith(badStart)) throw new BadLabelPrefix(label);
		}
		
		if(label.contains("(disambiguation)")) {
			throw new DisambiguationPage(label);
		}
	}
	
	private static void assertWikiTextOK(String wikiText, String label) throws BadWikiTextException {
		if(wikiText.startsWith("#REDIRECT")) throw new RedirectException(label);
	}
}
