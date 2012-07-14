package jhn.counts.doubles;

import java.util.Map.Entry;
import java.util.Set;

import jhn.counts.Counter;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class ObjObjDoubleCounterMap<K,V> implements DoubleCounterMap<K, V> {
	private final Object2ObjectMap<K,Counter<V,Double>> counters = new Object2ObjectOpenHashMap<>();

	@Override
	public Double getCount(K key, V value) {
		return Double.valueOf(getCountD(key, value));
	}

	@Override
	public void inc(K key, V value) {
		inc(key, value, 1.0);
	}

	@Override
	public void inc(K key, V value, Double inc) {
		inc(key, value, inc.doubleValue());
	}

	@Override
	public Set<Entry<K, Counter<V,Double>>> entrySet() {
		return counters.entrySet();
	}

	@Override
	public double getCountD(K key, V value) {
		Counter<V,Double> counter = counters.get(key);
		if(counter==null) {
			return 0.0;
		}
		return ((DoubleCounter<V>)counter).getCountD(value);
	}

	@Override
	public void inc(K key, V value, double inc) {
		Counter<V,Double> counter = counters.get(key);
		if(counter==null) {
			counter = new ObjDoubleCounter<>();
			counters.put(key, counter);
		}
		((DoubleCounter<V>)counter).inc(value, inc);
	}

	@Override
	public void set(K key, V value, Double count) {
		set(key, value, count.doubleValue());
	}

	@Override
	public void set(K key, V value, double count) {
		Counter<V,Double> counter = counters.get(key);
		if(counter==null) {
			counter = new ObjDoubleCounter<>();
			counters.put(key, counter);
		}
		((DoubleCounter<V>)counter).set(value, count);
	}

	@Override
	public boolean containsKey(K key) {
		return counters.containsKey(key);
	}

	@Override
	public boolean containsValue(K key, V value) {
		Counter<V,Double> counter = counters.get(key);
		if(counter==null) {
			return false;
		}
		return ((DoubleCounter<V>)counter).containsKey(value);
	}
}
