package jhn.util;

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
	
	public int getCount(final int key) {
		return counts.get(key);
	}
	
	public Set<Entry<Integer,Integer>> entries() {
		return counts.entrySet();
	}
	
	public double totalCount() {
		return totalCount;
	}
}
