package jhn.counts.i.i.i;

import jhn.counts.Counter;


public abstract class AbstractIntIntIntCounterMap implements IntIntIntCounterMap {

	@Override
	public int getCountI(Integer key, Integer value) {
		return getCount(key.intValue(), value.intValue());
	}

	@Override
	public void inc(Integer key, Integer value, int inc) {
		inc(key.intValue(), value.intValue());
	}

	@Override
	public void set(Integer key, Integer value, int count) {
		set(key.intValue(), value.intValue(), count);
	}

	@Override
	public Integer getCount(Integer key, Integer value) {
		return Integer.valueOf(getCount(key.intValue(), value.intValue()));
	}

	@Override
	public Integer totalCount() {
		return Integer.valueOf(totalCountI());
	}

	@Override
	public void inc(Integer key, Integer value) {
		inc(key.intValue(), value.intValue());
	}

	@Override
	public void inc(Integer key, Integer value, Integer inc) {
		inc(key.intValue(), value.intValue(), inc.intValue());
	}

	@Override
	public void set(Integer key, Integer value, Integer count) {
		set(key.intValue(), value.intValue(), count.intValue());
	}

	@Override
	public boolean containsKey(Integer key) {
		return containsKey(key.intValue());
	}

	@Override
	public boolean containsValue(Integer key, Integer value) {
		return containsValue(key.intValue(), value.intValue());
	}
	
	@Override
	public Counter<Integer, Integer> getCounter(Integer key) {
		return getCounter(key.intValue());
	}

}
