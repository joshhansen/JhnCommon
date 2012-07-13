package jhn.counts;

/** A counter whose counts are doubles */
public interface DoubleCounter<T> extends Counter<T,Double> {
	void inc(T key, double inc);
	
	void set(T key, double count);
	
	double getCountD(T key);
	
	double totalCountD();
}
