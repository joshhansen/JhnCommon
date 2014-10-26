package jhn.eda.topiccounts;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;

import jhn.ExtractorParams;
import jhn.util.Factory;
import jhn.util.Util;

/**
 * NOTE: requires topicID's to be contiguous
 *
 */
public class ArrayTopicCounts implements TopicCounts, Serializable {
	public static Factory<TopicCounts> factory(final int[] counts) {
		return new Factory<TopicCounts>(){
			final TopicCounts inst = new ArrayTopicCounts(counts);
			@Override
			public TopicCounts create() {
				return inst;
			}
		};
	}
	
	private static final long serialVersionUID = 1L;
	
	private final int[] counts;
	
	public ArrayTopicCounts(int[] counts) {
		this.counts = counts;
	}

	@Override
	public int topicCount(int topicID) throws TopicCountsException {
		return counts[topicID];
	}

	public static void main(String[] args) throws Exception {
		ExtractorParams ep = new ExtractorParams()
			.topicWordIdxName("wp_lucene4")
			.datasetName("sotu_chunks")
			.minCount(2);
		
		String topicCountsFilename = jhn.Paths.filteredTopicCountsFilename(ep);
		TopicCounts topicCounts = (TopicCounts) Util.deserialize(topicCountsFilename);
		for(int i = 0; i < 10; i++) {
			System.out.println(i + ": " + topicCounts.topicCount(i));
		}
	}
}
