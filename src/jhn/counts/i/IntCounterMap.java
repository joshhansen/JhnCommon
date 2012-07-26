package jhn.counts.i;

import jhn.counts.CounterMap;

public interface IntCounterMap<K,V> extends CounterMap<K,V,Integer> {
	int getCountI(K key, V value);
	
	int totalCountI();
	
	void inc(K key, V value, int inc);
	
	void set(K key, V value, int count);
}
