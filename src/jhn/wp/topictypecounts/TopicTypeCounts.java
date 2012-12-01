package jhn.wp.topictypecounts;

import java.util.Iterator;

public interface TopicTypeCounts {
	Iterator<TopicTypeCount> topicTypeCounts(int topicIdx) throws TopicTypeCountsException;
}
