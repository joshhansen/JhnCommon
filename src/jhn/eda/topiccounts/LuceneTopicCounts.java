package jhn.eda.topiccounts;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.store.FSDirectory;

import jhn.wp.Fields;

public class LuceneTopicCounts implements TopicCounts, AutoCloseable {
	private final IndexReader topicWordIdx;
	
	public LuceneTopicCounts(IndexReader topicWordIdx) {
		this.topicWordIdx = topicWordIdx;
	}

	@Override
	public int topicCount(int topicID) throws TopicCountsException {
		try {
			final TermFreqVector termFreqVector = topicWordIdx.getTermFreqVector(topicID, Fields.text);
			
			int totalTermFreq = 0;
			if(termFreqVector != null) {
				int[] docTermFreqs = termFreqVector.getTermFrequencies();
				for(int termFreq : docTermFreqs) {
					totalTermFreq += termFreq;
				}
			}
			
			return totalTermFreq;
		} catch (IOException e) {
			throw new TopicCountsException(e);
		}
	}

	@Override
	public void close() throws Exception {
		topicWordIdx.close();
	}
	
	public static void main(String[] args) throws Exception {
		String topicWordIdxName = "wp_lucene4";
		String topicWordIdxLuceneDir = jhn.Paths.topicWordIndexDir(topicWordIdxName);
		IndexReader topicWordIdx = IndexReader.open(FSDirectory.open(new File(topicWordIdxLuceneDir)));
		TopicCounts srcTopicCounts = new LuceneTopicCounts(topicWordIdx);
		
		for(int i = 0; i < 10; i++) {
			System.out.println(i + ": " + srcTopicCounts.topicCount(i));
		}
	}

}
