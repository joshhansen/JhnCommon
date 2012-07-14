package jhn.counts.ints;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectSet;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import jhn.util.Util;

public class ObjIntRAMCounter<K> implements IntCounter<K> {
	private final Object2IntMap<K> counts;
	
	private int totalCount = 0;
	
	public ObjIntRAMCounter() {
		this(new Object2IntOpenHashMap<K>());
	}
	
	public ObjIntRAMCounter(Object2IntMap<K> map) {
		this.counts = map;
	}
	
	@Override
	public int incI(K key) {
		return inc(key, 1);
	}
	
	@Override
	public int inc(K key, int inc) {
		final int newVal = getCountI(key)+inc;
		counts.put(key, newVal);
		totalCount += inc;
		return newVal;
	}
	
	@Override
	public Integer inc(K key) {
		return Integer.valueOf(incI(key));
	}

	@Override
	public Integer inc(K key, Integer count) {
		return Integer.valueOf(inc(key, count.intValue()));
	}
	
	@Override
	public void set(K key, int count) {
		totalCount -= getCountI(key);
		counts.put(key, count);
		totalCount += count;
	}
	
	@Override
	public void set(K key, Integer count) {
		set(key, count.intValue());
	}
	
	@Override
	public int getCountI(K key) {
		return counts.getInt(key);
	}
	
	@Override
	public Integer getCount(K key) {
		return counts.get(key);
	}

	@Override
	public int totalCountI() {
		return totalCount;
	}
	
	@Override
	public Set<Entry<K,Integer>> entries() {
		return counts.entrySet();
	}
	
	public final Comparator<Entry<K,Integer>> cmp = new Comparator<Entry<K,Integer>>(){
		@Override
		public int compare(Entry<K, Integer> o1, Entry<K, Integer> o2) {
			return o2.getValue().compareTo(o1.getValue());
		}
	};
	
	@Override
	public List<Entry<K,Integer>> topN(int n) {
		ObjectList<Entry<K,Integer>> entries = new ObjectArrayList<>(entries());
		Collections.sort(entries, cmp);
		return entries.subList(0, Math.min(n, entries.size()));
	}
	
	public final Comparator<Object2IntMap.Entry<K>> fastCmp = new Comparator<Object2IntMap.Entry<K>>(){
		@Override
		public int compare(Object2IntMap.Entry<K> o1, Object2IntMap.Entry<K> o2) {
			return Util.compareInts(o2.getIntValue(), o1.getIntValue());
		}
	};
	
	public List<Object2IntMap.Entry<K>> fastTopN(int n) {
		ObjectList<Object2IntMap.Entry<K>> entries = new ObjectArrayList<>(object2IntEntrySet());
		Collections.sort(entries, fastCmp);
		return entries.subList(0, Math.min(n, entries.size()));
	}
	
	@Override
	public Integer totalCount() {
		return Integer.valueOf(totalCount);
	}

	public ObjectSet<Object2IntMap.Entry<K>> object2IntEntrySet() {
		return counts.object2IntEntrySet();
	}

	@Override
	public int size() {
		return counts.size();
	}

	@Override
	public boolean containsKey(K key) {
		return counts.containsKey(key);
	}

	@Override
	public ObjectSet<K> keySet() {
		return counts.keySet();
	}

	@Override
	public void reset() {
		counts.clear();
	}
}
