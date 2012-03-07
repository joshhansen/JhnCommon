package jhn.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Counter<T> {
	private final Map<T,Double> counts = new HashMap<T,Double>();
	
	private static final Double one = new Double(1.0);
	public void inc(final T key) {
		inc(key, one);
	}
	
	public void inc(final T key, final Double count) {
		counts.put(key, getCount(key)+count);
	}
	
	public double getCount(final T key) {
		Double count = counts.get(key);
		if(count == null)
			return 0.0;
		else
			return count.doubleValue();
	}
	
	public Set<Entry<T,Double>> entries() {
		return counts.entrySet();
	}
}
