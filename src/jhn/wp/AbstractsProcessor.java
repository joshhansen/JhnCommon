package jhn.wp;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jhn.util.Util;
import jhn.wp.exceptions.CountException;
import jhn.wp.visitors.PrintingVisitor;

import org.apache.commons.lang3.StringEscapeUtils;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;

public class AbstractsProcessor extends CorpusProcessor {
	private String triplesFilename;
	private static final Pattern subjectRgx = Pattern.compile("^http://dbpedia\\.org/resource/(.+)$");
	
	public AbstractsProcessor(String triplesFilename, String logFilename, String errLogFilename) {
		super(logFilename, errLogFilename);
		this.triplesFilename = triplesFilename;
	}
	
	public void count() {
		try {
			beforeEverything();
			NxParser nxp = new NxParser(Util.smartInputStream(triplesFilename));
			for(Node[] ns : nxp) {
				beforeLabel();
				if(ns.length != 3) System.err.println("Not a triple");
				final Matcher m = subjectRgx.matcher(ns[0].toString());
				m.matches();
				final String label = m.group(1);
				
				try {
					visitLabel(label);
					
					final String abstrakt = StringEscapeUtils.unescapeHtml4(ns[2].toString());
					for(String word : tokenize(abstrakt))
						if(!isStopword(word))
							visitWord(word);
					afterLabel();
				} catch(CountException e) {
					System.err.write('s');
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		afterEverything();
	}

//		private static final int LABEL_COUNT = 3550567;
//		private static final int WORD_TYPE_COUNT = 1978075;
	
	/**
	 * Index DBPedia article abstracts
	 */
	public static void main(String[] args) {
		final String srcDir = System.getenv("HOME") + "/Data/dbpedia.org/3.7";
		final String name = "eda_output";
		final String destDir = System.getenv("HOME") + "/Projects/" + name;
		final String logFilename = destDir + "/" + name + ".log";
		final String errLogFilename = destDir + "/" + name + ".error.log";
		
		final String abstractsFilename = srcDir + "/long_abstracts_en.nt.bz2";
//		final String wordIdxFilename = destDir + "/dbpedia37_longabstracts_alphabet.ser";
//		final String topicIdxFilename = destDir + "/dbpedia37_longabstracts_label_alphabet.ser";
		
		AbstractsProcessor ac = new AbstractsProcessor(abstractsFilename, logFilename, errLogFilename);
		ac.addVisitor(new PrintingVisitor());//Provide some console output
//			ac.addVisitor(new OldMapReduceVisitor(topicIdxFilename, wordIdxFilename));
//			ap.addVisitor(new LabelIndexingVisitor(destDir+"/labelAlphabet.ser"));
//			ap.addVisitor(new WordIndexingVisitor(destDir+"/alphabet.ser"));
//			ap.addVisitor(new LabelCountingVisitor());
//			ap.addVisitor(new WordCountingVisitor());
		ac.count();
	}
}