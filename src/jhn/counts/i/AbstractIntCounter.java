package jhn.counts.i;

import java.util.Map.Entry;

import jhn.util.RandUtil;


public abstract class AbstractIntCounter<K> implements IntCounter<K> {

	@Override
	public Integer getCount(K key) {
		return Integer.valueOf(getCountI(key));
	}

	@Override
	public Integer totalCount() {
		return Integer.valueOf(totalCountI());
	}
	
	@Override
	public Integer defaultReturnValue() {
		return Integer.valueOf(defaultReturnValueI());
	}

	@Override
	public int incI(K key) {
		return inc(key, IntCounter.DEFAULT_INC);
	}
	
	@Override
	public Integer inc(K key) {
		return Integer.valueOf(incI(key));
	}

	@Override
	public Integer inc(K key, Integer count) {
		return Integer.valueOf(inc(key, count.intValue()));
	}

	@Override
	public void set(K key, Integer count) {
		set(key, count.intValue());
	}
	
	@Override
	public K sample() {
		int position = RandUtil.rand.nextInt(totalCountI());
		int sum = 0;
		for(Entry<K,Integer> entry : entries()) {
			sum += entry.getValue().intValue();
			if(sum >= position) {
				return entry.getKey();
			}
		}
		throw new IllegalStateException("This code should never be reached");
	}

}
