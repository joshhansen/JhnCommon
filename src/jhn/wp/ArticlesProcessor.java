package jhn.wp;

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

public class ArticlesProcessor extends CorpusProcessor {
	private final String wpdumpFilename;
	
	public ArticlesProcessor(String wpdumpFilename, String logFilename, String errLogFilename) {
		super(logFilename, errLogFilename);
		this.wpdumpFilename = wpdumpFilename;
	}

	@Override
	public void count() {
		try {
			super.beforeEverything();
			
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
								
								println(String.format("-----%s-----\n", label));
								println(String.format("ok:%d (%.2f) redirect:%d (%.2f) badLabel:%d (%.2f) disambig:%d (%.2f) tooShort:%d (%.2f) total:%d\n",
										ok, okPct, redirect, redirectPct, badLabelPrefix, badLabelPrefixPct, disambiguation, disambigPct, tooShort, tooShortPct, total));
							}
						}
						
						ArticlesProcessor.this.beforeLabel();
						try {
							ArticlesProcessor.this.visitLabel(label);
						} catch (CountException e) {
							e.printStackTrace();
							e.printStackTrace(errLog);
						}
						print(".");
//						System.out.println(label);
						
						String text = wikiToText3(wikiText).trim();
//						System.out.println("-----------------------------PLAIN TEXT----------------------------");
//						System.out.println(text);
//						System.out.println();
						
						String[] tokens = tokenize(text);
//						System.out.println("-----------------------------TOKENS----------------------------");
//						
//						for(String token : tokens) {
//							System.out.print(token);
//							System.out.print(' ');
//						}
//						System.out.println();
						
						for(String word : tokens) {
							ArticlesProcessor.this.visitWord(word);
						}
						
						ArticlesProcessor.this.afterLabel();
					} catch(RedirectException e) {
						print("r");
						printlnErr("Redirect: " + e.label());
						redirect++;
					} catch(BadWikiTextException e) {
						print("t");
						printlnErr("Other text problem: " + e.label());
					} catch (ArticleTooShort e) {
						print("s");
						printlnErr("Too short: " + e.label());
						tooShort++;
					} catch(BadLabelPrefix e) {
						print("l");
						printlnErr("Bad prefix: " + e.label());
						badLabelPrefix++;
					} catch(DisambiguationPage e) {
						print("d");
						printlnErr("Disambiguation: " + e.label());
						disambiguation++;
					} catch (Exception e) {
						e.printStackTrace();
						e.printStackTrace(errLog);
						print("u");
						printlnErr("Unknown: " + label);
					}
				}
			});

			wxsp.parse();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		super.afterEverything();
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
	
//	// Index article text in Lucene
//	public static void main(String[] args) {
//		final String outputDir = System.getenv("HOME") + "/Projects/eda_output";
//		final String idxDir = outputDir + "/indices/topic_words";
//		final String name = "wp_lucene4";
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
//		ac.addVisitor(new LuceneVisitor(luceneDir, Fields.text));
//		ac.count();
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
	
	// Count words in a trie
	public static void main(String[] args) {
		final String outputDir = System.getenv("HOME") + "/Projects/eda_output";
		
		
		final String name = "wordCountsTrie";
		final String outputFilename = outputDir + "/" + name + ".ser";
		final String logFilename = outputDir + "/" + name + ".log";
		final String errLogFilename = outputDir + "/" + name + ".err.log";
		
		final String srcDir = System.getenv("HOME") + "/Data/wikipedia.org";
		final String articlesFilename = srcDir + "/enwiki-20120403-pages-articles.xml.bz2";
		
		
		CorpusProcessor ac = new ArticlesProcessor(articlesFilename, logFilename, errLogFilename);
		ac.addVisitor(new PrintingVisitor());
//		ac.addVisitor(new WordCountVisitorPatriciaTrie(outputFilename));
		ac.addVisitor(new WordCountVisitorTrie(outputFilename));
		ac.count();
	}
	
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
