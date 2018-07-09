package jhn.counts.l;

import java.util.Map.Entry;

import jhn.util.RandUtil;


public abstract class AbstractLongCounter<K> implements LongCounter<K> {

	@Override
	public Long getCount(K key) {
		return Long.valueOf(getCountL(key));
	}

	@Override
	public Long totalCount() {
		return Long.valueOf(totalCountL());
	}
	
	@Override
	public Long defaultReturnValue() {
		return Long.valueOf(defaultReturnValueL());
	}

	@Override
	public Long inc(K key) {
		return Long.valueOf(incL(key));
	}
	
	@Override
	public long incL(K key) {
		return inc(key, 1L);
	}

	@Override
	public Long inc(K key, Long count) {
		return Long.valueOf(inc(key, count.longValue()));
	}

	@Override
	public void set(K key, Long count) {
		set(key, count.longValue());
	}

	@Override
	public K sample() {
		double pos = RandUtil.rand.nextDouble() * totalCountL();
		double sum = 0.0;
		
		for(Entry<K, Long> entry : this.entries()) {
			sum += entry.getValue().longValue();
			if(sum >= pos) {
				return entry.getKey();
			}
		}
		throw new IllegalStateException("This code should never be reached");
	}

//	@Override
//	public void setIfAbsent(K key, Long count) {
//		setIfAbsent(key, count.longValue());
//	}
	
}
