package jhn.counts.d;

import jhn.counts.Counter;

/** A counter whose counts are doubles */
public interface DoubleCounter<T> extends Counter<T,Double> {
	double incD(T key);
	
	double inc(T key, double inc);
	
	void set(T key, double count);
	
//	void setIfAbsent(T key, double count);
	
	double getCountD(T key);
	
	double totalCountD();
	
	double defaultReturnValueD();
}
