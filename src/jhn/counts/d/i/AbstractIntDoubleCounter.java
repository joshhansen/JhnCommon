package jhn.counts.d.i;

import jhn.counts.d.AbstractDoubleCounter;

public abstract class AbstractIntDoubleCounter extends AbstractDoubleCounter<Integer> implements IntDoubleCounter {

	@Override
	public double inc(int key) {
		return inc(key, 1.0);
	}
	
	@Override
	public double inc(Integer key, double inc) {
		return inc(key.intValue(), inc);
	}

	@Override
	public void set(Integer key, double count) {
		set(key.intValue(), count);
	}

	@Override
	public double getCountD(Integer key) {
		return getCount(key.intValue());
	}

	@Override
	public boolean containsKey(Integer key) {
		return containsKey(key.intValue());
	}

}
