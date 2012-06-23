package jhn.counts;

public interface IntCounter<T> extends Counter<T,Integer> {
	int getCountI(int key);
	int totalCountI();
}
