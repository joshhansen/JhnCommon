package jhn.counts;

import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

public interface Counter<T,N extends Number> {

	void inc(T key);

	void inc(T key, N count);

	void set(T key, N count);

	N getCount(T key);

	Set<Entry<T, N>> entries();

	N totalCount();

	List<Entry<T, N>> topN(int n);
}