package jhn.counts;

public interface DoubleCounterMap<K,V> extends CounterMap<K,V,Double> {
	double getCountD(K key, V value);
	
	void inc(K key, V value, double inc);
	
	void set(K key, V value, double count);
}
