package jhn.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;

public class IntIntCounter {
	private final Int2IntMap counts = new Int2IntArrayMap();
	
	private int totalCount = 0;
	public void inc(final int key) {
		inc(key, 1);
	}
	
	public void inc(final int key, final int count) {
		counts.put(key, getCount(key)+count);
		totalCount += count;
	}
	
	public void set(final int key, final int count) {
		totalCount -= getCount(key);
		counts.put(key, count);
		totalCount += count;
	}
	
	public int getCount(final int key) {
		return counts.get(key);
	}
	
	public Set<Entry<Integer,Integer>> entries() {
		return counts.entrySet();
	}
	
	public double totalCount() {
		return totalCount;
	}
	
	private static final Comparator<Entry<Integer,Integer>> cmp = new Comparator<Entry<Integer,Integer>>(){
		@Override
		public int compare(Entry<Integer, Integer> o1, 	Entry<Integer, Integer> o2) {
			return o2.getValue().compareTo(o1.getValue());
		}
	};
	
	public List<Entry<Integer,Integer>> topN(int n) {
		List<Entry<Integer,Integer>> entries = new ArrayList<Entry<Integer,Integer>>(entries());
		Collections.sort(entries, cmp);
		return new ArrayList<Entry<Integer,Integer>>(entries.subList(0, Math.min(n, entries.size())));
	}
}
