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

public class PhraseWordProportionalPMI extends AbstractProportionalPMI<Label,Word> {
	private static final Version defaultLuceneVersion = Version.LUCENE_36;
	private static final int defaultMaxHits = 1000000;
	
	private final IndexSearcher s;
	private final QueryParser qp;
	private final int maxHits;
	private final String field;
	
	public PhraseWordProportionalPMI(String topicWordIdxLuceneDir) {
		this(topicWordIdxLuceneDir, Fields.text, defaultMaxHits, defaultLuceneVersion);
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
		this.field = field;
	}
	
	
	@Override
	protected int jointCount(Label l, Word w) throws ParseException, IOException {
		int count = 0;
		
		// The sum of the frequency of word w across all documents containing the label l
		TopDocs td = labelHits(l);
		for(int i = 0; i < td.totalHits; i++) {
			ScoreDoc sd = td.scoreDocs[i];
			
			TermFreqVector tfv = s.getIndexReader().getTermFreqVector(sd.doc, field);
			int wordIdx = tfv.indexOf(w.word);
			if(wordIdx != -1) {
				count += tfv.getTermFrequencies()[wordIdx];
			}
		}
		
		return count;
	}


	private TopDocs labelHits(Label l) throws ParseException, IOException {
		StringBuilder query = new StringBuilder();
		query.append(field).append(":\"").append(l.label).append("\"");
		Query q = qp.parse(query.toString());
		
		return s.search(q, maxHits);
	}
	
	@Override
	protected int count1(Label l) throws ParseException, IOException {
		return labelHits(l).totalHits;
	}


	@Override
	protected int count2(Word w) {
		try {
			Term t = new Term(Fields.text, w.word);
			int freq = s.docFreq(t);
			System.out.println("c(" + w + ") = " + freq);
			return freq;
		} catch(Exception e) {
			throw new IllegalArgumentException();
		}
	}

	public void close() throws IOException {
		s.close();
	}
}
