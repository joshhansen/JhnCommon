package jhn.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class CounterMap<K,V> {
	private final Map<K,Counter<V>> counters = new HashMap<K,Counter<V>>();
	
	public double getCount(K key, V value) {
		Counter<V> counter = counters.get(key);
		if(counter==null) {
			return 0.0;
		}
		return counter.getCount(value);
	}
	
	public void inc(K key, V value) {
		inc(key, value, 1.0);
	}
	
	public void inc(K key, V value, double inc) {
		Counter<V> counter = counters.get(key);
		if(counter==null) {
			counter = new Counter<V>();
			counters.put(key, counter);
		}
		counter.inc(value, inc);
	}

	public Set<Entry<K,Counter<V>>> entrySet() {
		return counters.entrySet();
	}
}
