package jhn.counts.d.o.o;

import java.util.Map.Entry;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import jhn.counts.Counter;
import jhn.counts.d.AbstractDoubleCounterMap;
import jhn.counts.d.DoubleCounter;
import jhn.counts.d.o.ObjDoubleCounter;

public class ObjObjDoubleCounterMap<K,V> extends AbstractDoubleCounterMap<K, V> {
	private final Object2ObjectMap<K,Counter<V,Double>> counters = new Object2ObjectOpenHashMap<>();

	@Override
	public void inc(K key, V value) {
		inc(key, value, 1.0);
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

	@Override
	public double totalCountD() {
		double total = 0.0;
		for(Counter<V,Double> counter : counters.values()) {
			total += ((DoubleCounter<V>)counter).totalCountD();
		}
		return total;
	}
}
