package jhn.counts.ints;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;

/** A counter whose counts and keys are integers */
public interface IntIntCounter extends IntCounter<Integer> {
	void set(int key, int count);
	
	int inc(int key);
	
	int inc(int key, int inc);
	
	boolean containsKey(int key);
	
	int getCount(int key);
	
	ObjectSet<Int2IntMap.Entry> int2IntEntrySet();
	
	IntSet keySetI();
}
