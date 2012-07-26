package jhn.counts.d.o;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;

import jhn.counts.d.AbstractDoubleCounter;

public class ObjDoubleCounter<T> extends AbstractDoubleCounter<T> {
	private final Object2DoubleMap<T> counts = new Object2DoubleOpenHashMap<>();
	
	private double totalCount = 0.0;
	
	@Override
	public double inc(final T key, final double count) {
		final double newVal = getCountD(key)+count;
		counts.put(key, newVal);
		totalCount += count;
		return newVal;
	}
	
	@Override
	public void set(final T key, final double count) {
		totalCount -= counts.put(key, count);
		totalCount += count;
	}
	
	@Override
	public double getCountD(final T key) {
		return counts.getDouble(key);
	}
	
	@Override
	public Double getCount(T key) {
		return counts.get(key);
	}
	
	@Override
	public Set<Entry<T,Double>> entries() {
		return counts.entrySet();
	}
	
	@Override
	public double totalCountD() {
		return totalCount;
	}

	private final Comparator<Entry<T,Double>> cmp = new Comparator<Entry<T,Double>>(){
		@Override
		public int compare(Entry<T, Double> o1, 	Entry<T, Double> o2) {
			return o2.getValue().compareTo(o1.getValue());
		}
	};
	@Override
	public List<Entry<T, Double>> topN(int n) {
		List<Entry<T,Double>> entries = new ArrayList<>(entries());
		Collections.sort(entries, cmp);
		return new ArrayList<>(entries.subList(0, Math.min(n, entries.size())));
	}

	@Override
	public int size() {
		return counts.size();
	}

	@Override
	public boolean containsKey(T key) {
		return counts.containsKey(key);
	}

	@Override
	public ObjectSet<T> keySet() {
		return counts.keySet();
	}

	@Override
	public void reset() {
		counts.clear();
	}

}
