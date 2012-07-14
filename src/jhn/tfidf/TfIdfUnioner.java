package jhn.tfidf;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import jhn.util.Util;
import jhn.wp.Fields;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.store.FSDirectory;

public class TfIdfUnioner {
	private static class TermScore implements Comparable<TermScore> {
		private final String term;
		private final double score;
		public TermScore(String term, double score) {
			this.term = term;
			this.score = score;
		}
		@Override
		public int compareTo(TermScore o) {
			return Double.compare(o.score, this.score);
		}
		
		@Override
		public String toString() {
			return term + ": " + score;
		}
		
	}
	
	public static void main(String[] args) {
		final int N = 1;
		final String outputDir = System.getenv("HOME") + "/Projects/eda_output";
		final String srcLuceneIdxDir = outputDir + "/indices/topic_words/wp_lucene4";
		final String dest = outputDir + "/featsel/tfidfTop" + N + ".ser";
		
		try(IndexReader r = IndexReader.open(FSDirectory.open(new File(srcLuceneIdxDir)))) {
			Set<String> selected = new HashSet<>();
			
			final int numDocs = r.numDocs();
			final double logNumDocs = Math.log(numDocs);
			for(int docNum = 0; docNum < numDocs; docNum++) {
				Document doc = r.document(docNum);
				
				System.out.println(docNum + ": " + doc.get(Fields.label));
				TermFreqVector tfv = r.getTermFreqVector(docNum, Fields.text);
				
				if(tfv == null) {
					System.err.println("\tNULL");
				} else {
					TermScore[] scores = new TermScore[tfv.size()];
					
					int[] tfs = tfv.getTermFrequencies();
					String[] terms = tfv.getTerms();
					for(int i = 0; i < tfs.length; i++) {
						double tf = tfs[i];
						int df = r.docFreq(new Term(Fields.text, terms[i]));
						double idf = logNumDocs - Math.log(df);
						double tfIdf = tf * idf;
						scores[i] = new TermScore(terms[i], tfIdf);
					}
					
					Arrays.sort(scores);
					
					for(int i = 0; i < Math.min(scores.length, N); i++) {
						System.out.print("\t" + scores[i]);
						
						boolean added = selected.add(scores[i].term);
						if(added) System.out.print(" *");
						System.out.println();
					}
					System.out.println();
				}
			}
			
			r.close();
			
			Util.serialize(selected, dest);
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	
}
