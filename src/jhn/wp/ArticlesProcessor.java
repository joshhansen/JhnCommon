package jhn.wp;

import java.io.FileNotFoundException;
import java.io.PrintStream;

import edu.jhu.nlp.wikipedia.PageCallbackHandler;
import edu.jhu.nlp.wikipedia.WikiPage;
import edu.jhu.nlp.wikipedia.WikiXMLParser;
import edu.jhu.nlp.wikipedia.WikiXMLParserFactory;
import info.bliki.wiki.filter.ITextConverter;
import info.bliki.wiki.filter.PlainTextConverter;
import info.bliki.wiki.model.WikiModel;

import jhn.wp.exceptions.ArticleTooShort;
import jhn.wp.exceptions.BadLabel;
import jhn.wp.exceptions.BadLabelPrefix;
import jhn.wp.exceptions.BadWikiTextException;
import jhn.wp.exceptions.CountException;
import jhn.wp.exceptions.DisambiguationPage;
import jhn.wp.exceptions.RedirectException;
import jhn.wp.visitors.PrintingVisitor;
import jhn.wp.visitors.WordCountVisitorTrie;
import jhn.wp.visitors.lucene.LuceneVisitor3;

public class ArticlesProcessor extends CorpusProcessor {
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
						
//						System.out.println("-----------------------------WIKI TEXT----------------------------");
//						
//						System.out.println(wikiText);
//						System.out.println();
						
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
						
						String text = wikiToText3(wikiText).trim();
						
						events.visitDocument(text);
						
						
//						System.out.println("-----------------------------PLAIN TEXT----------------------------");
//						System.out.println(text);
//						System.out.println();
						
//						String[] tokens = tokenize(text);
//						System.out.println("-----------------------------TOKENS----------------------------");
//						
//						for(String token : tokens) {
//							System.out.print(token);
//							System.out.print(' ');
//						}
//						System.out.println();
						
//						for(String word : tokens) {
//							events.visitWord(word);
//						}
						
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
		
		events.afterEverything();
	}
	
	
	private static final WikiModel wikiModel = new WikiModel("http://www.mywiki.com/wiki/${image}", "http://www.mywiki.com/wiki/${title}");
	private static final ITextConverter conv = new PlainTextConverter();
	private String wikiToText3(String markup) {
		return wikiModel.render(conv, markup);
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
	
	// Index article text in Lucene
	public static void main(String[] args) throws FileNotFoundException {
		final String outputDir = System.getenv("HOME") + "/Projects/eda_output";
		final String idxDir = outputDir + "/indices/topic_words";
		final String name = "wp_lucene5";
		final String luceneDir = idxDir + "/" + name;
		final String logFilename = idxDir + "/" + name + ".log";
		final String errLogFilename = idxDir + "/" + name + ".error.log";
		
		final String srcDir = System.getenv("HOME") + "/Data/wikipedia.org";
		final String articlesFilename = srcDir + "/enwiki-20120403-pages-articles.xml.bz2";
		
		
		CorpusProcessor ac = new ArticlesProcessor(articlesFilename, logFilename, errLogFilename);
		ac.addVisitor(new PrintingVisitor());
		ac.addVisitor(new LuceneVisitor3(luceneDir, Fields.text));
		ac.process();
	}
	
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
