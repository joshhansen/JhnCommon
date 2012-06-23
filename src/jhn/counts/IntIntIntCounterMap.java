package jhn.util;

import java.util.Map.Entry;
import java.util.Set;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class IntIntIntCounterMap {
	private final Int2ObjectMap<IntIntCounter> counters = new Int2ObjectOpenHashMap<IntIntCounter>();
	
	public int getCount(int key, int value) {
		IntIntCounter counter = counters.get(key);
		if(counter==null) {
			return 0;
		}
		return counter.getCount(value);
	}
	
	public void inc(int key, int value) {
		inc(key, value, 1);
	}
	
	public void inc(int key, int value, int inc) {
		IntIntCounter counter = counters.get(key);
		if(counter==null) {
			counter = new IntIntCounter();
			counters.put(key, counter);
		}
		counter.inc(value, inc);
	}

	public Set<Entry<Integer,IntIntCounter>> entrySet() {
		return counters.entrySet();
	}
}
