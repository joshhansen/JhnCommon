package jhn.counts;

import java.util.Set;
import java.util.Map.Entry;

public interface CounterMap<K, V, N extends Number> {

	N getCount(K key, V value);

	void inc(K key, V value);

	void inc(K key, V value, N inc);
	
	void set(K key, V value, N count);

	Set<Entry<K, Counter<V,N>>> entrySet();

}