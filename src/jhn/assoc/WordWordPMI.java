package jhn.assoc;

import java.sql.Connection;
import java.sql.DriverManager;

import jhn.Paths;
import jhn.counts.i.i.IntIntCounter;
import jhn.counts.i.i.IntIntSQLiteCounter;
import jhn.counts.i.i.i.IntIntIntSQLiteCounterMap;
import jhn.idx.DiskStringIndex;
import jhn.idx.ReverseIndex;
import jhn.util.Util;


public class WordWordPMI implements AssociationMeasure<String,String>, AutoCloseable {
	private final ReverseIndex<String> wordIdx;
	private final IntIntCounter counts;
	private final IntIntIntSQLiteCounterMap cocounts;
	
	public WordWordPMI(DiskStringIndex wordIdx, String countsDbFilename, String cocountsDbFilename) throws Exception {
		this.wordIdx = wordIdx;
		
		Class.forName("org.sqlite.JDBC");
		
		Connection countsDB = DriverManager.getConnection("jdbc:sqlite:" + countsDbFilename);
		Connection cocountsDB = DriverManager.getConnection("jdbc:sqlite:" + cocountsDbFilename);
		counts = new IntIntSQLiteCounter(countsDB);
		cocounts = new IntIntIntSQLiteCounterMap(cocountsDB);
	}
	
	public WordWordPMI(String wordIdxFilename, String countsDbFilename, String cocountsDbFilename) throws Exception {
		this(new DiskStringIndex(wordIdxFilename), countsDbFilename, cocountsDbFilename);
	}
	
	@Override
	public double association(String first, String... seconds) throws Exception {
		
		if(seconds.length > 1) {
			throw new IllegalArgumentException(this.getClass().getSimpleName() + " allows only two words to be compared");
		}
		
		final String second = seconds[0];
		
		int word1idx = wordIdx.indexOf(first);
		int word2idx = wordIdx.indexOf(second);
		return smartLog(p_word_word(word1idx, word2idx)) - smartLog(p_word(word1idx)) - smartLog(p_word(word2idx));
	}
	
	private static double smartLog(double x) {
		if(x == 0.0) return 0.0;
		return Math.log(x);
	}

	private double p_word(int wordIdx_) {
		return (double) counts.getCount(wordIdx_) / (double) counts.totalCountI();
	}
	
	private double p_word_word(int word1idx, int word2idx) {
		return (double) cocounts.getCount(word1idx, word2idx) / (double) cocounts.totalCountL();
	}
	
	@Override
	public void close() throws Exception {
		Util.closeIfPossible(wordIdx);
		Util.closeIfPossible(counts);
		Util.closeIfPossible(cocounts);
	}
	
	public static void main(String[] args) throws Exception {
		String wordIdxFilename = Paths.outputDir("JhnCommon") + "/word_sets/chunks/19.set";
		String countsDbFilename = Paths.outputDir("JhnCommon") + "/counts/counts.sqlite3";
		String cocountsDbFilename = Paths.outputDir("JhnCommon") + "/cocounts/cocounts.sqlite3";
		try(WordWordPMI pmi = new WordWordPMI(wordIdxFilename, countsDbFilename, cocountsDbFilename)) {
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
