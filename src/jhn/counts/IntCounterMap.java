package jhn.counts;

public interface IntCounterMap<K,V> extends CounterMap<K,V,Integer> {
	int getCountI(K key, V value);
	
	void inc(K key, V value, int inc);
	
	void set(K key, V value, int count);
}
