package jhn.counts.d;

import jhn.counts.CounterMap;

public interface DoubleCounterMap<K,V> extends CounterMap<K,V,Double> {
	double getCountD(K key, V value);
	
	double totalCountD();
	
	void inc(K key, V value, double inc);
	
	void set(K key, V value, double count);
}
