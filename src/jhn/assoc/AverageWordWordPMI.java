package jhn.assoc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import jhn.Paths;
import jhn.counts.Counter;
import jhn.counts.d.DoubleCounterMap;
import jhn.counts.d.o.o.ObjObjDoubleCounterMap;
import jhn.idx.DiskStringIndex;
import jhn.util.Util;
import jhn.wp.CorpusProcessor;


public class AverageWordWordPMI implements AssociationMeasure<String,String>, AutoCloseable {
	private static final boolean IGNORE_SELF_ASSOCIATION = false;
//	private final ReverseIndex<String> wordIdx;
	
	private final WordWordPMI pmi;
	
	public AverageWordWordPMI(DiskStringIndex wordIdx, String countsDbFilename, String cocountsDbFilename) throws Exception {
		pmi = new WordWordPMI(wordIdx, countsDbFilename, cocountsDbFilename);
	}
	
	public AverageWordWordPMI(String wordIdxFilename, String countsDbFilename, String cocountsDbFilename) throws Exception {
		this(new DiskStringIndex(wordIdxFilename), countsDbFilename, cocountsDbFilename);
	}


	/** Given a label and a set of words, returns average PMI between each label word and all given words */
	@Override
	public double association(String label, String... words) throws Exception {
		String[] labelWords = CorpusProcessor.tokenize(label);
		
		DoubleCounterMap<String,String> countsUsed = new ObjObjDoubleCounterMap<>();
		
//		double totalPMI = 0.0;
		
		for(String labelWord : labelWords) {
			if(!labelWord.isEmpty() && !Util.isStopword(labelWord)) {
				for(String word : words) {
					if(!word.isEmpty() && (
							!IGNORE_SELF_ASSOCIATION || !labelWord.equals(word)
						)) {
						countsUsed.set(labelWord, word, pmi.association(labelWord, word));
//						totalPMI += wordWordPMI(labelWord, word);
					}
				}
			}
		}
		
		
		List<AssocEntry> entries = new ArrayList<>();
		
		for(Map.Entry<String,Counter<String,Double>> entry1 : countsUsed.entrySet()) {
			for(Map.Entry<String, Double> entry2 : entry1.getValue().entries()) {
				entries.add(new AssocEntry(entry1.getKey(), entry2.getKey(), entry2.getValue().doubleValue()));
			}
		}
		
		Collections.sort(entries, new Comparator<AssocEntry>(){
			@Override
			public int compare(AssocEntry o1, AssocEntry o2) {
				return Double.compare(o2.count, o1.count);
			}
		});
		
		double totalPMI = countsUsed.totalCountD();
//		for(AssocEntry entry : entries.subList(0, Math.min(5, entries.size()))) {
//			System.out.println("\t" + entry);
//		}
//		System.out.println();
		
		return totalPMI / (labelWords.length * words.length);
	}
	
	private static class AssocEntry {
		private final String word1;
		private final String word2;
		private final double count;
		public AssocEntry(String word1, String word2, double count) {
			this.word1 = word1;
			this.word2 = word2;
			this.count = count;
		}
		
		@Override
		public String toString() {
			return word1 + "," + word2 + ":" + count;
		}
	}

	@Override
	public void close() throws Exception {
		Util.closeIfPossible(pmi);
	}
	
	public static void main(String[] args) throws Exception {
		String wordIdxFilename = Paths.outputDir("JhnCommon") + "/word_sets/chunks/19.set";
		String countsDbFilename = Paths.outputDir("JhnCommon") + "/counts/counts.sqlite3";
		String cocountsDbFilename = Paths.outputDir("JhnCommon") + "/cocounts/cocounts.sqlite3";
		try(AverageWordWordPMI pmi = new AverageWordWordPMI(wordIdxFilename, countsDbFilename, cocountsDbFilename)) {
			go(pmi, "dog", "cat");
			go(pmi, "united", "states");
			go(pmi, "mormon", "utah");
			go(pmi, "chicken", "ocean");
			go(pmi, "dog", "zebra");
		}
	}
	
	private static void go(AssociationMeasure<String,String> pmi, String word1, String word2) throws Exception {
		System.out.print("pmi(");
		System.out.print(word1);
		System.out.print(',');
		System.out.print(word2);
		System.out.print(") = ");
		System.out.println(pmi.association(word1, word2));
	}
}
