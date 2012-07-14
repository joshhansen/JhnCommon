package jhn.counts;

/** A counter whose counts are integers */
public interface IntCounter<K> extends Counter<K,Integer> {
	int inc(K key, int inc);
	
	int getCountI(K key);
	
	int totalCountI();
}
