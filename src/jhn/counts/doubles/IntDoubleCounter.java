package jhn.counts.doubles;

import it.unimi.dsi.fastutil.ints.IntSet;

/** A counter whose keys are integers and whose counts are doubles */
public interface IntDoubleCounter extends DoubleCounter<Integer> {
	void set(int key, double count);
	
	double inc(int key);
	
	double inc(int key, double inc);
	
	boolean containsKey(int key);
	
	double getCount(int key);
	
	@Override
	IntSet keySet();
}
