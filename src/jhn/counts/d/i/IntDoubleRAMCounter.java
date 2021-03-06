package jhn.counts.d.i;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import jhn.ifaces.Trimmable;

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectSet;

public class IntDoubleRAMCounter extends AbstractIntDoubleCounter implements Trimmable {
	private final Int2DoubleMap counts;
	
	private double totalCount = 0;
	
	public IntDoubleRAMCounter() {
		this(new Int2DoubleOpenHashMap());
	}
	
	public IntDoubleRAMCounter(Int2DoubleMap map) {
		this.counts = map;
	}
	
	@Override
	public double inc(final int key, final double count) {
		final double newVal = getCount(key)+count;
		counts.put(key, newVal);
		totalCount += count;
		return newVal;
	}
	
	@Override
	public void set(final int key, final double count) {
		totalCount -= counts.put(key, count);
		totalCount += count;
	}

	@Override
	public Double getCount(Integer key) {
		return counts.get(key);
	}
	
	@Override
	public double totalCountD() {
		return totalCount;
	}
	
	@Override
	public double defaultReturnValueD() {
		return counts.defaultReturnValue();
	}
	
	public static final Comparator<Entry<Integer,Double>> cmp = new Comparator<Entry<Integer,Double>>(){
		@Override
		public int compare(Entry<Integer, Double> o1, Entry<Integer, Double> o2) {
			return o2.getValue().compareTo(o1.getValue());
		}
	};
	
	@Override
	public List<Entry<Integer,Double>> topN(int n) {
		List<Entry<Integer,Double>> entries = new ArrayList<>(entries());
		Collections.sort(entries, cmp);
		return entries.subList(0, Math.min(n, entries.size()));
	}
	
	public static final Comparator<Int2DoubleMap.Entry> fastCmp = new Comparator<Int2DoubleMap.Entry>(){
		@Override
		public int compare(Int2DoubleMap.Entry o1, Int2DoubleMap.Entry o2) {
			return Double.compare(o2.getDoubleValue(), o1.getDoubleValue());
		}
	};
	
	@Override
	public List<Int2DoubleMap.Entry> fastTopN(int n) {
		ObjectList<Int2DoubleMap.Entry> entries = new ObjectArrayList<>(int2DoubleEntrySet());
		Collections.sort(entries, fastCmp);
		return entries.subList(0, Math.min(n, entries.size()));
	}

	@Override
	public Set<Entry<Integer, Double>> entries() {
		return counts.entrySet();
	}

	@Override
	public ObjectSet<Int2DoubleMap.Entry> int2DoubleEntrySet() {
		return counts.int2DoubleEntrySet();
	}

	@Override
	public void trim() {
		if(counts instanceof Int2DoubleOpenHashMap) {
			((Int2DoubleOpenHashMap)counts).trim();
		}
	}

	@Override
	public int size() {
		return counts.size();
	}

	@Override
	public boolean containsKey(int key) {
		return counts.containsKey(key);
	}

	@Override
	public double getCount(int key) {
		return counts.get(key);
	}

	@Override
	public IntSet keySet() {
		return counts.keySet();
	}

	@Override
	public void reset() {
		counts.clear();
	}

}
