package jhn.wp.topiccounts;

public interface TopicCounter {
	void setTotalCount(int topic, int count);
	void close();
}