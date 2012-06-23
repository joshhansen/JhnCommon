package jhn.counts;

import java.util.Map.Entry;
import java.util.Set;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;

public class IntIntIntCounterMap implements IntCounterMap<Integer,Integer> {
	private final Int2ObjectMap<Counter<Integer,Integer>> counters = new Int2ObjectOpenHashMap<Counter<Integer,Integer>>();

	@Override
	public Integer getCount(Integer key, Integer value) {
		return Integer.valueOf(getCount(key.intValue(), value.intValue()));
	}

	@Override
	public void inc(Integer key, Integer value) {
		inc(key.intValue(), value.intValue());
	}

	@Override
	public void inc(Integer key, Integer value, Integer inc) {
		inc(key.intValue(), value.intValue(), inc.intValue());
	}

	@Override
	public Set<Entry<Integer, Counter<Integer, Integer>>> entrySet() {
		return counters.entrySet();
	}
	
	public ObjectSet<Int2ObjectMap.Entry<Counter<Integer, Integer>>> int2ObjectEntrySet() {
		return counters.int2ObjectEntrySet();
	}

	@Override
	public int getCountI(Integer key, Integer value) {
		return getCount(key.intValue(), value.intValue());
	}

	@Override
	public void inc(Integer key, Integer value, int inc) {
		inc(key.intValue(), value.intValue(), inc);
	}
	
	public int getCount(int key, int value) {
		Counter<Integer,Integer> counter = counters.get(key);
		if(counter==null) {
			return 0;
		}
		return ((IntCounter<Integer>)counter).getCountI(value);
	}
	
	public void inc(int key, int value) {
		inc(key, value, 1);
	}
	
	public void inc(int key, int value, int inc) {
		Counter<Integer,Integer> counter = counters.get(key);
		if(counter==null) {
			counter = new IntIntCounter();
			counters.put(key, counter);
		}
		((IntIntCounter)counter).inc(value, inc);
	}
}
