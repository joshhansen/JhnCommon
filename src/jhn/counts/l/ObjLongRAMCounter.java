package jhn.counts.l;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import jhn.util.RandUtil;


public class ObjLongRAMCounter<T> extends AbstractLongCounter<T> {
	private final Object2LongMap<T> counts = new Object2LongOpenHashMap<>();
	
	private long totalCount = 0L;
	
	@Override
	public long inc(final T key, final long count) {
		final long newVal = getCountL(key)+count;
		counts.put(key, newVal);
		totalCount += count;
		return newVal;
	}
	
	@Override
	public void set(final T key, final long count) {
		totalCount -= counts.put(key, count);
		totalCount += count;
	}
	
	@Override
	public void setIfAbsent(T key, long count) {
		if(!counts.containsKey(key)) {
			set(key, count);
		}
	}
	
	@Override
	public long getCountL(final T key) {
		return counts.getLong(key);
	}
	
	@Override
	public Long getCount(T key) {
		return counts.get(key);
	}
	
	@Override
	public Set<Entry<T,Long>> entries() {
		return counts.entrySet();
	}
	
	@Override
	public long totalCountL() {
		return totalCount;
	}
	
	@Override
	public long defaultReturnValueL() {
		return counts.defaultReturnValue();
	}

	private final Comparator<Entry<T,Long>> cmp = new Comparator<Entry<T,Long>>(){
		@Override
		public int compare(Entry<T, Long> o1, Entry<T, Long> o2) {
			return o2.getValue().compareTo(o1.getValue());
		}
	};
	@Override
	public List<Entry<T, Long>> topN(int n) {
		List<Entry<T,Long>> entries = new ArrayList<>(entries());
		Collections.sort(entries, cmp);
		return new ArrayList<>(entries.subList(0, Math.min(n, entries.size())));
	}

	@Override
	public int size() {
		return counts.size();
	}

	@Override
	public boolean containsKey(T key) {
		return counts.containsKey(key);
	}

	@Override
	public ObjectSet<T> keySet() {
		return counts.keySet();
	}

	@Override
	public void reset() {
		counts.clear();
	}

	@Override
	public T sample() {
		double pos = RandUtil.rand.nextDouble() * totalCount;
		double sum = 0.0;
		
		for(Object2LongMap.Entry<T> entry : counts.object2LongEntrySet()) {
			sum += entry.getLongValue();
			if(sum >= pos) {
				return entry.getKey();
			}
		}
		throw new IllegalStateException("This code should never be reached");
	}

	@Override
	public ObjectSet<Object2LongMap.Entry<T>> object2LongEntrySet() {
		return counts.object2LongEntrySet();
	}
}

