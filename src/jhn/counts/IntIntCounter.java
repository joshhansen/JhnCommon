package jhn.counts;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectSet;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import jhn.util.Util;

public class IntIntCounter implements IntCounter<Integer> {
	private final Int2IntMap counts;
	
	private int totalCount = 0;
	
	public IntIntCounter() {
		this(new Int2IntOpenHashMap());
	}
	
	public IntIntCounter(Int2IntMap map) {
		this.counts = map;
	}
	
	public void inc(final int key) {
		inc(key, 1);
	}
	
	public void inc(final int key, final int count) {
		counts.put(key, getCountI(key)+count);
		totalCount += count;
	}
	
	public void set(final int key, final int count) {
		totalCount -= getCountI(key);
		counts.put(key, count);
		totalCount += count;
	}
	
	@Override
	public int getCountI(int key) {
		return counts.get(key);
	}

	@Override
	public int totalCountI() {
		return totalCount;
	}
	
	@Override
	public Set<Entry<Integer,Integer>> entries() {
		return counts.entrySet();
	}
	
	public static final Comparator<Entry<Integer,Integer>> cmp = new Comparator<Entry<Integer,Integer>>(){
		@Override
		public int compare(Entry<Integer, Integer> o1, Entry<Integer, Integer> o2) {
			return o2.getValue().compareTo(o1.getValue());
		}
	};
	
	public List<Entry<Integer,Integer>> topN(int n) {
		ObjectList<Entry<Integer,Integer>> entries = new ObjectArrayList<Entry<Integer,Integer>>(entries());
		Collections.sort(entries, cmp);
		return entries.subList(0, Math.min(n, entries.size()));
	}
	
	public static final Comparator<Int2IntMap.Entry> fastCmp = new Comparator<Int2IntMap.Entry>(){
		@Override
		public int compare(Int2IntMap.Entry o1, Int2IntMap.Entry o2) {
			return Util.compareInts(o2.getIntValue(), o1.getIntValue());
		}
	};
	
	public List<Int2IntMap.Entry> fastTopN(int n) {
		ObjectList<Int2IntMap.Entry> entries = new ObjectArrayList<Int2IntMap.Entry>(int2IntEntrySet());
		Collections.sort(entries, fastCmp);
		return entries.subList(0, Math.min(n, entries.size()));
	}
	
	@Override
	public void inc(Integer key) {
		inc(key.intValue());
	}

	@Override
	public void inc(Integer key, Integer count) {
		inc(key.intValue(), count.intValue());
	}

	@Override
	public void set(Integer key, Integer count) {
		set(key.intValue(), count.intValue());
	}
	
	@Override
	public Integer totalCount() {
		return Integer.valueOf(totalCount);
	}

	@Override
	public Integer getCount(Integer key) {
		return counts.get(key);
	}

	public ObjectSet<Int2IntMap.Entry> int2IntEntrySet() {
		return counts.int2IntEntrySet();
	}

	@Override
	public int size() {
		return counts.size();
	}

}
