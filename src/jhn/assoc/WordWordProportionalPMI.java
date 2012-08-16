package jhn.assoc;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import jhn.wp.Fields;

/**
 * "Proportional" means that it's PMI without the normalizing constant (1 / total token count)
 */
public class WordWordProportionalPMI extends AbstractProportionalPMI<String,String> {
	private static final Version defaultLuceneVersion = Version.LUCENE_36;
	private static final int defaultMaxHits = 1000000;
	
	private final QueryParser qp;
	private final int maxHits;
	private final IndexSearcher s;
	private final int cooccurrenceWindow;
	public WordWordProportionalPMI(String topicWordIdxLuceneDir, int cooccurrenceWindow) throws CorruptIndexException, IOException {
		this(topicWordIdxLuceneDir, cooccurrenceWindow, Fields.text, defaultMaxHits, defaultLuceneVersion);
	}
	
	public WordWordProportionalPMI(String topicWordIdxLuceneDir, int cooccurrenceWindow, String field, int maxHits, Version luceneVersion) throws CorruptIndexException, IOException {
		this.cooccurrenceWindow = cooccurrenceWindow;
		
		Analyzer a = new StandardAnalyzer(luceneVersion);
		qp = new QueryParser(luceneVersion, field, a);
		this.maxHits = maxHits;
		
		s = new IndexSearcher(IndexReader.open(FSDirectory.open(new File(topicWordIdxLuceneDir))));
	}
	
	@Override
	protected int count1(String x) {
		return count(x);
	}

	@Override
	protected int count2(String y) {
		return count(y);
	}
	
	protected int count(String w) {
		try {
			Term t = new Term(Fields.text, w);
			int freq = s.docFreq(t);
			System.out.println("c(" + w + ") = " + freq);
			return freq;
		} catch(Exception e) {
			throw new IllegalArgumentException();
		}
	}
	
	@Override
	protected int jointCount(String w1, String w2) {
		try {
			String query = "\"" + w1 + " " + w2 + "\"~" + cooccurrenceWindow;
			Query q = qp.parse(query);
			TopDocs top = s.search(q, maxHits);
			int count = top.totalHits;
			System.out.println("c(" + w1 + "," + w2 + ") = " + count);
			return count;
		} catch(Exception e) {
			throw new IllegalArgumentException();
		}
	}


}
