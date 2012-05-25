package jhn.util;

import java.util.Map.Entry;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;

public class Counter<T> {
	private final Object2DoubleMap<T> counts = new Object2DoubleOpenHashMap<T>();
	
	private double totalCount = 0.0;
	public void inc(final T key) {
		inc(key, 1.0);
	}
	
	public void inc(final T key, final double count) {
		counts.put(key, getCount(key)+count);
		totalCount += count;
	}
	
	public double getCount(final T key) {
		return counts.getDouble(key);
	}
	
	public Set<Entry<T,Double>> entries() {
		return counts.entrySet();
	}
	
	public double totalCount() {
		return totalCount;
	}
}
