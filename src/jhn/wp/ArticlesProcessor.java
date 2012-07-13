package jhn.wp;

import info.bliki.wiki.filter.ITextConverter;
import info.bliki.wiki.filter.PlainTextConverter;
import info.bliki.wiki.model.WikiModel;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jhn.Paths;
import jhn.wp.exceptions.ArticleTooShort;
import jhn.wp.exceptions.BadLabel;
import jhn.wp.exceptions.BadLabelPrefix;
import jhn.wp.exceptions.BadWikiTextException;
import jhn.wp.exceptions.CountException;
import jhn.wp.exceptions.DisambiguationPage;
import jhn.wp.exceptions.RedirectException;
import jhn.wp.visitors.ChunkedWordSetVisitor;
import jhn.wp.visitors.PrintingVisitor;
import jhn.wp.visitors.WordIndexingVisitor;
import jhn.wp.visitors.WordSetVisitor;
import jhn.wp.visitors.counting.WindowedCocountVisitor;
import edu.jhu.nlp.wikipedia.PageCallbackHandler;
import edu.jhu.nlp.wikipedia.WikiPage;
import edu.jhu.nlp.wikipedia.WikiXMLParser;
import edu.jhu.nlp.wikipedia.WikiXMLParserFactory;

public class ArticlesProcessor extends CorpusProcessor {
	private static final boolean PRINT_WIKI_TEXT = false;
	private static final boolean PRINT_PLAIN_TEXT = PRINT_WIKI_TEXT;
	
	private final String wpdumpFilename;
	
	public ArticlesProcessor(String wpdumpFilename, String logFilename, String errLogFilename) throws FileNotFoundException {
		super(logFilename, errLogFilename);
		this.wpdumpFilename = wpdumpFilename;
	}

	@Override
	public void process() {
		try {
			events.beforeEverything();
			
			WikiXMLParser wxsp = WikiXMLParserFactory.getSAXParser(wpdumpFilename);
			
			wxsp.setPageCallback(new PageCallbackHandler() {
				int badLabelPrefix = 0;
				int disambiguation = 0;
				int redirect = 0;
				int ok = 0;
				int tooShort = 0;
				int total = 0;
				public void process(WikiPage page) {
					total++;
					final String label = page.getTitle().trim();
					try {
						
						assertLabelOK(label);
						
//						final String wikiText = page.getText();
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
//						System.out.println(label);
//						String headingsCleaned = cleanHeadings(wikiText);
						String fixedHeadings = fixBadHeadings(wikiText);
						String sansRefs = removeRefUrls(fixedHeadings);
						String text = wikiToText3(sansRefs).trim();
						
						events.visitDocument(text);
						
						if(PRINT_PLAIN_TEXT) {
//							System.out.println("-----------------------------HEADINGS------------------");
//							System.out.println(headingsCleaned);
//							System.out.println();
							
							System.out.println("-----------------------------PLAIN TEXT----------------------------");
							System.out.println(text);
							System.out.println();
						}
						
						String[] tokens = tokenize(text);
//						System.out.println("-----------------------------TOKENS----------------------------");
//						
//						for(String token : tokens) {
//							System.out.print(token);
//							System.out.print(' ');
//						}
//						System.out.println();
						
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
			});

			wxsp.parse();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			events.afterEverything();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private static final WikiModel wikiModel = new WikiModel("http://www.mywiki.com/wiki/${image}", "http://www.mywiki.com/wiki/${title}");
	private static final ITextConverter conv = new PlainTextConverter();
	private static String wikiToText3(String markup) {
		return wikiModel.render(conv, markup);
	}
	
	private static final Pattern refUrlRgx = Pattern.compile("<ref>[^<]+?</ref>");
	private static String removeRefUrls(String markup) {
		Matcher m = refUrlRgx.matcher(markup);
//		while(m.find()) {
//			System.err.println(m.group());
//		}
		return m.replaceAll(" ");
	}
	
	private static final Pattern headingRgx = Pattern.compile("(==+)([^=]+?)\\1");
	private static String cleanHeadings(String markup) {
		Matcher m = headingRgx.matcher(markup);
//		while(m.find()) {
//			System.err.println(m.group());
//		}
		return m.replaceAll(" $2. ");
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
	
	private void assertLabelOK(String label) throws BadLabel {
		for(String badStart : dontStartWithThese) {
			if(label.startsWith(badStart)) throw new BadLabelPrefix(label);
		}
		
		if(label.contains("(disambiguation)")) {
			throw new DisambiguationPage(label);
		}
	}
	
	private void assertWikiTextOK(String wikiText, String label) throws BadWikiTextException {
		if(wikiText.startsWith("#REDIRECT")) throw new RedirectException(label);
	}
	
	// Generate chunked co-occurrence counts
	public static void main(String[] args) throws Exception {
		final String cocountsDir = Paths.outputDir("JhnCommon") + "/cocounts";
		final String outputDir = cocountsDir + "/phase1";
		
		final String logFilename = cocountsDir + "/main.log";
		final String errLogFilename = cocountsDir + "/main.err";
		
		final String srcDir = System.getenv("HOME") + "/Data/wikipedia.org";
		final String articlesFilename = srcDir + "/enwiki-20120403-pages-articles.xml.bz2";
		
		final String wordIdxFilename = Paths.outputDir("JhnCommon") + "/word_sets/chunks/19.set";
		
		CorpusProcessor ac = new ArticlesProcessor(articlesFilename, logFilename, errLogFilename);
		ac.addVisitor(new PrintingVisitor());
		ac.addVisitor(new WindowedCocountVisitor(outputDir, wordIdxFilename, 5000000, 6500000, 20));
//		ac.addVisitor(new WindowedCocountVisitor(outputDir, 1000, 20));
		ac.process();
	}
	
//	// Index words
//	public static void main(String[] args) throws Exception {
//		final String indexDir = Paths.outputDir("JhnCommon") + "/indices/words";
//		
//		final String logFilename = indexDir + "/main.log";
//		final String errLogFilename = indexDir + "/main.err";
//		
//		final String srcDir = System.getenv("HOME") + "/Data/wikipedia.org";
//		final String articlesFilename = srcDir + "/enwiki-20120403-pages-articles.xml.bz2";
//		
//		
//		CorpusProcessor ac = new ArticlesProcessor(articlesFilename, logFilename, errLogFilename);
//		ac.addVisitor(new PrintingVisitor());
//		ac.addVisitor(new WordIndexingVisitor(indexDir + "/words.idx"));
//		ac.process();
//	}
	
//	// Aggregate words
//	public static void main(String[] args) throws Exception {
//		final String indexDir = Paths.outputDir("JhnCommon") + "/word_sets";
//		
//		final String logFilename = indexDir + "/main.log";
//		final String errLogFilename = indexDir + "/main.err";
//		
//		final String srcDir = System.getenv("HOME") + "/Data/wikipedia.org";
//		final String articlesFilename = srcDir + "/enwiki-20120403-pages-articles.xml.bz2";
//		
//		
//		CorpusProcessor ac = new ArticlesProcessor(articlesFilename, logFilename, errLogFilename);
//		ac.addVisitor(new PrintingVisitor());
//		ac.addVisitor(new ChunkedWordSetVisitor(500000, 1000000, indexDir+"/chunks"));
//		ac.process();
//	}
	
//	// Index article text in Lucene
//	public static void main(String[] args) throws FileNotFoundException {
//		final String outputDir = System.getenv("HOME") + "/Projects/eda_output";
//		final String idxDir = outputDir + "/indices/topic_words";
//		final String name = "wp_lucene5";
//		final String luceneDir = idxDir + "/" + name;
//		final String logFilename = idxDir + "/" + name + ".log";
//		final String errLogFilename = idxDir + "/" + name + ".error.log";
//		
//		final String srcDir = System.getenv("HOME") + "/Data/wikipedia.org";
//		final String articlesFilename = srcDir + "/enwiki-20120403-pages-articles.xml.bz2";
//		
//		
//		CorpusProcessor ac = new ArticlesProcessor(articlesFilename, logFilename, errLogFilename);
//		ac.addVisitor(new PrintingVisitor());
//		ac.addVisitor(new LuceneVisitor3(luceneDir, Fields.text));
//		ac.process();
//	}
	
//	// Create a dictionary of article titles in a trie
//	public static void main(String[] args) {
//		final String outputDir = System.getenv("HOME") + "/Projects/eda_output";
//		
//		final String dictFilename = outputDir + "/labelDictionary.ser";
//		final String logFilename = outputDir + "/labelDictionary.log";
//		final String errLogFilename = outputDir + "/labelDictionary.err.log";
//		
//		final String srcDir = System.getenv("HOME") + "/Data/wikipedia.org";
//		final String articlesFilename = srcDir + "/enwiki-20120403-pages-articles.xml.bz2";
//		
//		CorpusProcessor ac = new ArticlesProcessor(articlesFilename, logFilename, errLogFilename);
//		ac.addVisitor(new PrintingVisitor());
//		ac.addVisitor(new LabelDictionaryBuildingVisitor(dictFilename));
//		ac.count();
//	}
	
//	// Count words in a trie
//	public static void main(String[] args) throws FileNotFoundException {
//		final String outputDir = System.getenv("HOME") + "/Projects/eda_output";
//		
//		final String name = "wordCountsTrie";
//		final String outputFilename = outputDir + "/" + name + ".ser";
//		final String logFilename = outputDir + "/" + name + ".log";
//		final String errLogFilename = outputDir + "/" + name + ".err.log";
//		
//		final String srcDir = System.getenv("HOME") + "/Data/wikipedia.org";
//		final String articlesFilename = srcDir + "/enwiki-20120403-pages-articles.xml.bz2";
//		
//		
//		CorpusProcessor ac = new ArticlesProcessor(articlesFilename, logFilename, errLogFilename);
//		ac.addVisitor(new PrintingVisitor());
////		ac.addVisitor(new WordCountVisitorPatriciaTrie(outputFilename));
//		ac.addVisitor(new WordCountVisitorTrie(outputFilename));
//		ac.process();
//	}
	
//	// Index words in a trie
//	public static void main(String[] args) {
//		final String outputDir = System.getenv("HOME") + "/Projects/eda_output";
//		
//		final String name = "wordIndexTrie";
//		final String outputFilename = outputDir + "/" + name + ".ser";
//		final String logFilename = outputDir + "/" + name + ".log";
//		final String errLogFilename = outputDir + "/" + name + ".err.log";
//		
//		final String srcDir = System.getenv("HOME") + "/Data/wikipedia.org";
//		final String articlesFilename = srcDir + "/enwiki-20120403-pages-articles.xml.bz2";
//		
//		
//		CorpusProcessor ac = new ArticlesProcessor(articlesFilename, logFilename, errLogFilename);
//		ac.addVisitor(new PrintingVisitor());
//		ac.addVisitor(new WordIndexVisitorTrie(outputFilename));
//		ac.count();
//	}
}
