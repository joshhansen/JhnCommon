package jhn.counts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;

public class ObjDoubleCounter<T> implements DoubleCounter<T> {
	private final Object2DoubleMap<T> counts = new Object2DoubleOpenHashMap<T>();
	
	private double totalCount = 0.0;
	public Double inc(final T key) {
		return Double.valueOf(inc(key, 1.0));
	}
	
	public double inc(final T key, final double count) {
		final double newVal = getCountD(key)+count;
		counts.put(key, newVal);
		totalCount += count;
		return newVal;
	}
	
	public void set(final T key, final double count) {
		totalCount -= counts.put(key, count);
		totalCount += count;
	}
	
	public double getCountD(final T key) {
		return counts.getDouble(key);
	}
	
	public Set<Entry<T,Double>> entries() {
		return counts.entrySet();
	}
	
	public double totalCountD() {
		return totalCount;
	}

	@Override
	public Double inc(T key, Double count) {
		return Double.valueOf(inc(key, count.doubleValue()));
	}

	@Override
	public void set(T key, Double count) {
		set(key, count.doubleValue());
	}


	@Override
	public Double totalCount() {
		return Double.valueOf(totalCount);
	}

	private final Comparator<Entry<T,Double>> cmp = new Comparator<Entry<T,Double>>(){
		@Override
		public int compare(Entry<T, Double> o1, 	Entry<T, Double> o2) {
			return o2.getValue().compareTo(o1.getValue());
		}
	};
	@Override
	public List<Entry<T, Double>> topN(int n) {
		List<Entry<T,Double>> entries = new ArrayList<Entry<T,Double>>(entries());
		Collections.sort(entries, cmp);
		return new ArrayList<Entry<T,Double>>(entries.subList(0, Math.min(n, entries.size())));
	}

	@Override
	public Double getCount(T key) {
		return counts.get(key);
	}

	@Override
	public int size() {
		return counts.size();
	}

	@Override
	public boolean containsKey(T key) {
		return counts.containsKey(key);
	}
}
