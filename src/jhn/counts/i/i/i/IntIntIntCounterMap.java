package jhn.counts.i.i.i;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;

import jhn.counts.Counter;
import jhn.counts.i.IntCounterMap;
import jhn.counts.i.i.IntIntCounter;

public interface IntIntIntCounterMap extends IntCounterMap<Integer,Integer> {

	ObjectSet<Int2ObjectMap.Entry<Counter<Integer, Integer>>> int2ObjectEntrySet();

	int getCount(int key, int value);

	void inc(int key, int value);

	void inc(int key, int value, int inc);

	void set(int key, int value, int count);

	void reset();

	/** The total number of (key,value) pairs in all counters */
	int totalSize();

	boolean containsKey(int key);

	boolean containsValue(int key, int value);

	IntIntCounter getCounter(int key);
}
