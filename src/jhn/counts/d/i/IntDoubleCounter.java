package jhn.counts.d.i;

import java.util.List;

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;

import jhn.counts.d.DoubleCounter;

/** A counter whose keys are integers and whose counts are doubles */
public interface IntDoubleCounter extends DoubleCounter<Integer> {
	void set(int key, double count);
	
	double inc(int key);
	
	double inc(int key, double inc);
	
	boolean containsKey(int key);
	
	double getCount(int key);
	
	@Override
	IntSet keySet();
	
	ObjectSet<Int2DoubleMap.Entry> int2DoubleEntrySet();
	
	List<Int2DoubleMap.Entry> fastTopN(int n);
}
