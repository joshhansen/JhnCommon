package jhn.assoc;

import java.io.IOException;
import java.util.Collections;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.Version;

import jhn.wp.Fields;

public class PMI {
	private static final StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);
	private static final QueryParser qp = new QueryParser(Version.LUCENE_35, Fields.text, analyzer);
	private static final int maxHits = 1000000;
	public static double proportionalPMI(IndexSearcher s, String w1, String w2, int window) throws ParseException, IOException {
		return   Math.log(jointCount(s, w1, w2, window))
		        - Math.log(count(s, w1))
		        - Math.log(count(s, w2));
	}
	
	private static int count(IndexSearcher s, String w) throws IOException {
		Term t = new Term(Fields.text, w);
		int freq = s.docFreq(t);
		System.out.println("c(" + w + ") = " + freq);
		return freq;
	}
	
	private static int jointCount(IndexSearcher s, String w1, String w2, int window) throws IOException, ParseException {
		String query = "\"" + w1 + " " + w2 + "\"~" + window;
		Query q = qp.parse(query);
		TopDocs top = s.search(q, maxHits);
		int count = top.totalHits;
		System.out.println("c(" + w1 + "," + w2 + ") = " + count);
		return count;
	}
}
