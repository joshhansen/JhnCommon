package jhn.counts;

public interface DoubleCounter<T> extends Counter<T,Double> {
	void inc(T key, double inc);
	void set(T key, double count);
	double getCountD(T key);
	double totalCountD();
}
