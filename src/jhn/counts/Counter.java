package jhn.counts;

import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

public interface Counter<K,N extends Number> {

	N inc(K key);

	N inc(K key, N count);

	void set(K key, N count);

	N getCount(K key);

	Set<Entry<K, N>> entries();

	int size();
	
	N totalCount();

	List<Entry<K, N>> topN(int n);
	
	boolean containsKey(K key);
	
	Set<K> keySet();
	
	void reset();
}