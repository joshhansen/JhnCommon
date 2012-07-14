package jhn.counts.ints;

import jhn.counts.Counter;

/** A counter whose counts are integers */
public interface IntCounter<K> extends Counter<K,Integer> {
	void set(K key, int count);
	
	int incI(K key);
	
	int inc(K key, int inc);
	
	int getCountI(K key);
	
	int totalCountI();
}
