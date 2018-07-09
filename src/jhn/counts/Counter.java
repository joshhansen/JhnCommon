package jhn.counts;

import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

public interface Counter<K,N extends Number> {
	N getCount(K key);

	Set<Entry<K, N>> entries();

	int size();
	
	N totalCount();
	
	N defaultReturnValue();

	List<Entry<K, N>> topN(int n);
	
	boolean containsKey(K key);
	
	Set<K> keySet();
	
	N inc(K key);

	N inc(K key, N count);

	void set(K key, N count);
	
//	void setIfAbsent(K key, N count);
	
	void reset();

	/** Treat this counter as a categorical distribution over its keys and return a random sample therefrom. */
	K sample();
}