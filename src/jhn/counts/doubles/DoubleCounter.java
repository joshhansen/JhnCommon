package jhn.counts.doubles;

import jhn.counts.Counter;

/** A counter whose counts are doubles */
public interface DoubleCounter<T> extends Counter<T,Double> {
	double inc(T key, double inc);
	
	void set(T key, double count);
	
	double getCountD(T key);
	
	double totalCountD();
}
