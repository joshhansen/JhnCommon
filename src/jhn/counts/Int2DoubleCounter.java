package jhn.counts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectSet;

public class Int2DoubleCounter implements DoubleCounter<Integer> {
	private final Int2DoubleMap counts;
	
	private double totalCount = 0;
	
	public Int2DoubleCounter() {
		this(new Int2DoubleOpenHashMap());
	}
	
	public Int2DoubleCounter(Int2DoubleMap map) {
		this.counts = map;
	}
	
	public void inc(final int key) {
		inc(key, 1);
	}
	
	public void inc(final int key, final double count) {
		counts.put(key, getCountD(key)+count);
		totalCount += count;
	}
	
	public void set(final int key, final double count) {
		totalCount -= counts.put(key, count);
		totalCount += count;
	}
	
	public double getCountD(int key) {
		return counts.get(key);
	}

	@Override
	public double totalCountD() {
		return totalCount;
	}
	
	public static final Comparator<Entry<Integer,Double>> cmp = new Comparator<Entry<Integer,Double>>(){
		@Override
		public int compare(Entry<Integer, Double> o1, Entry<Integer, Double> o2) {
			return o2.getValue().compareTo(o1.getValue());
		}
	};
	
	public List<Entry<Integer,Double>> topN(int n) {
		List<Entry<Integer,Double>> entries = new ArrayList<Entry<Integer,Double>>(entries());
		Collections.sort(entries, cmp);
		return entries.subList(0, Math.min(n, entries.size()));
	}
	
	
	public static final Comparator<Int2DoubleMap.Entry> fastCmp = new Comparator<Int2DoubleMap.Entry>(){
		@Override
		public int compare(Int2DoubleMap.Entry o1, Int2DoubleMap.Entry o2) {
			return Double.compare(o2.getDoubleValue(), o1.getDoubleValue());
		}
	};
	public List<Int2DoubleMap.Entry> fastTopN(int n) {
		ObjectList<Int2DoubleMap.Entry> entries = new ObjectArrayList<Int2DoubleMap.Entry>(int2DoubleEntrySet());
		Collections.sort(entries, fastCmp);
		return entries.subList(0, Math.min(n, entries.size()));
	}
	
	@Override
	public void inc(Integer key) {
		inc(key.intValue());
	}
	
	@Override
	public Double totalCount() {
		return Double.valueOf(totalCount);
	}

	@Override
	public Double getCount(Integer key) {
		return counts.get(key);
	}

	@Override
	public void inc(Integer key, Double count) {
		inc(key.intValue(), count.doubleValue());
	}

	@Override
	public void set(Integer key, Double count) {
		set(key.intValue(), count.doubleValue());
	}

	@Override
	public Set<Entry<Integer, Double>> entries() {
		return counts.entrySet();
	}

	public ObjectSet<Int2DoubleMap.Entry> int2DoubleEntrySet() {
		return counts.int2DoubleEntrySet();
	}

	@Override
	public void inc(Integer key, double inc) {
		inc(key.intValue(), inc);
	}

	@Override
	public double getCountD(Integer key) {
		return getCountD(key.intValue());
	}

	public void trim() {
		if(counts instanceof Int2DoubleOpenHashMap) {
			((Int2DoubleOpenHashMap)counts).trim();
		}
	}
}