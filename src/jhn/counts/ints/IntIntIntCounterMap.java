package jhn.counts.ints;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;

import jhn.counts.Counter;

public interface IntIntIntCounterMap extends IntCounterMap<Integer,Integer> {

	public abstract ObjectSet<Int2ObjectMap.Entry<Counter<Integer, Integer>>> int2ObjectEntrySet();

	public abstract int getCount(int key, int value);

	public abstract void inc(int key, int value);

	public abstract void inc(int key, int value, int inc);

	public abstract void set(int key, int value, int count);

	public abstract void reset();

	/** The total number of (key,value) pairs in all counters */
	public abstract int totalSize();

	public abstract boolean containsKey(int key);

	public abstract boolean containsValue(int key, int value);

}