package jhn.assoc;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import jhn.wp.Fields;

public class PhraseWordProportionalPMI implements AssociationMeasure<Label,Word> {
	private static final Version defaultLuceneVersion = Version.LUCENE_36;
	private static final int defaultMaxHits = 100; /* 1000000 */
	
	private final IndexSearcher s;
	private final IndexReader r;
	private final QueryParser qp;
	private final int maxHits;
	private final String field;
	
	public PhraseWordProportionalPMI(String topicWordIdxLuceneDir) {
		this(topicWordIdxLuceneDir, Fields.text, defaultMaxHits);
	}
	
	public PhraseWordProportionalPMI(String topicWordIdxLuceneDir, String field, int maxHits) {
		this(topicWordIdxLuceneDir, field, maxHits, defaultLuceneVersion);
	}
	
	
	public PhraseWordProportionalPMI(String topicWordIdxLuceneDir, String field, int maxHits, Version luceneVersion) {
		Analyzer a = new StandardAnalyzer(luceneVersion);
		qp = new QueryParser(luceneVersion, field, a);
		this.maxHits = maxHits;
		
		IndexSearcher s = null;
		try {
			s = new IndexSearcher(IndexReader.open(FSDirectory.open(new File(topicWordIdxLuceneDir))));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.s = s;
		this.r = this.s.getIndexReader();
		
		this.field = field;
	}
	
	private static double smartLog(int x) {
		if(x==0) return 0.0;
		return Math.log(x);
	}
	
	@Override
	public double association(Label l, Word... words) throws Exception {
		ScoreDoc[] labelDocs = labelHits(l).scoreDocs;
		
		int[] jointCounts = jointCounts(labelDocs, words);
		int[] wordCounts = counts(words);

		double logLabelCount = smartLog(labelDocs.length);
		
		double totalPMI = 0.0;
		for(int i = 0; i < words.length; i++) {
			totalPMI += smartLog(jointCounts[i]) - logLabelCount - smartLog(wordCounts[i]);
		}
		return totalPMI / (double) words.length;
	}
	
	protected int[] jointCounts(ScoreDoc[] labelDocs, Word... words) throws ParseException, IOException {
		int[] counts = new int[words.length];
		
		for(int i = 0; i < labelDocs.length; i++) {
			ScoreDoc sd = labelDocs[i];
			
			TermFreqVector tfv = r.getTermFreqVector(sd.doc, field);
			int[] termFreqs = tfv.getTermFrequencies();
			
			for(int wordNum = 0; wordNum < words.length; wordNum++) {
				int wordIdx = tfv.indexOf(words[wordNum].word);
				if(wordIdx != -1) {
					counts[wordNum] += termFreqs[wordIdx];
				}
			}
		}
		
		return counts;
	}

	private TopDocs labelHits(Label l) throws ParseException, IOException {
		StringBuilder query = new StringBuilder();
		query.append(field).append(":\"").append(l.label).append("\"");
		Query q = qp.parse(query.toString());
		
		return s.search(q, maxHits);
	}
	
	protected int[] counts(Word... words) {
		int[] counts = new int[words.length];
		
		for(int i = 0; i < words.length; i++) {
			counts[i] = count(words[i]);
		}
		
		return counts;
	}

	protected int count(Word w) {
		try {
			Term t = new Term(Fields.text, w.word);
			int freq = s.docFreq(t);
//			System.out.println("c(" + w + ") = " + freq);
			return freq;
		} catch(Exception e) {
			throw new IllegalArgumentException();
		}
	}

	public void close() throws IOException {
		s.close();
	}

}
