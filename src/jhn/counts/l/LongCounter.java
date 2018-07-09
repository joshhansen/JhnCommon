package jhn.counts.l;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import jhn.counts.Counter;

/** A counter whose counts are longs */
public interface LongCounter<T> extends Counter<T,Long> {
	long incL(T key);
	
	long inc(T key, long inc);
	
	void set(T key, long count);
	
	void setIfAbsent(T key, long count);
	
	long getCountL(T key);
	
	long totalCountL();
	
	long defaultReturnValueL();
	
	ObjectSet<Object2LongMap.Entry<T>> object2LongEntrySet();
}
