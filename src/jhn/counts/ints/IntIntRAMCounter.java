package jhn.counts.ints;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectSet;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import jhn.util.Util;

public class IntIntRAMCounter implements IntIntCounter {
	private final Int2IntMap counts;
	
	private int totalCount = 0;
	
	public IntIntRAMCounter() {
		this(new Int2IntOpenHashMap());
	}
	
	public IntIntRAMCounter(Int2IntMap map) {
		this.counts = map;
	}
	
	@Override
	public int inc(final int key) {
		return inc(key, 1);
	}
	
	@Override
	public int inc(int key, int inc) {
		final int newVal = getCount(key)+inc;
		counts.put(key, newVal);
		totalCount += inc;
		return newVal;
	}
	
	@Override
	public int incI(Integer key) {
		return inc(key.intValue());
	}
	
	@Override
	public void set(int key, int count) {
		totalCount -= getCount(key);
		counts.put(key, count);
		totalCount += count;
	}
	
	@Override
	public void set(Integer key, int count) {
		set(key.intValue(), count);
	}
	
	@Override
	public int getCount(int key) {
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
	
	@Override
	public List<Entry<Integer,Integer>> topN(int n) {
		ObjectList<Entry<Integer,Integer>> entries = new ObjectArrayList<>(entries());
		Collections.sort(entries, cmp);
		return entries.subList(0, Math.min(n, entries.size()));
	}
	
	public static final Comparator<Int2IntMap.Entry> fastCmp = new Comparator<Int2IntMap.Entry>(){
		@Override
		public int compare(Int2IntMap.Entry o1, Int2IntMap.Entry o2) {
			return Util.compareInts(o2.getIntValue(), o1.getIntValue());
		}
	};
	
	@Override
	public List<Int2IntMap.Entry> fastTopN(int n) {
		ObjectList<Int2IntMap.Entry> entries = new ObjectArrayList<>(int2IntEntrySet());
		Collections.sort(entries, fastCmp);
		return entries.subList(0, Math.min(n, entries.size()));
	}
	
	@Override
	public Integer inc(Integer key) {
		return Integer.valueOf(inc(key.intValue()));
	}

	@Override
	public Integer inc(Integer key, Integer count) {
		return Integer.valueOf(inc(key.intValue(), count.intValue()));
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

	@Override
	public ObjectSet<Int2IntMap.Entry> int2IntEntrySet() {
		return counts.int2IntEntrySet();
	}

	@Override
	public int size() {
		return counts.size();
	}

	@Override
	public int inc(Integer key, int inc) {
		return inc(key.intValue(), inc);
	}

	@Override
	public int getCountI(Integer key) {
		return getCount(key.intValue());
	}

	@Override
	public boolean containsKey(Integer key) {
		return containsKey(key.intValue());
	}

	@Override
	public boolean containsKey(int key) {
		return counts.containsKey(key);
	}

	@Override
	public Set<Integer> keySet() {
		return keySetI();
	}

	@Override
	public IntSet keySetI() {
		return counts.keySet();
	}

	@Override
	public void reset() {
		counts.clear();
	}

}
