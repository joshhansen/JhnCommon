package jhn.counts.i;

import jhn.counts.Counter;

/** A counter whose counts are integers */
public interface IntCounter<K> extends Counter<K,Integer> {
	public static final int DEFAULT_INC = 1;
	
	void set(K key, int count);
	
	int incI(K key);
	
	int inc(K key, int inc);
	
	int getCountI(K key);
	
	int totalCountI();
}
