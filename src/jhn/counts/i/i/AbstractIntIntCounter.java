package jhn.counts.i.i;

import jhn.counts.i.AbstractIntCounter;
import jhn.counts.i.IntCounter;

public abstract class AbstractIntIntCounter extends AbstractIntCounter<Integer> implements IntIntCounter {

	@Override
	public int getCountI(Integer key) {
		return getCount(key.intValue());
	}
	
	@Override
	public boolean containsKey(Integer key) {
		return containsKey(key.intValue());
	}
	
	@Override
	public void set(Integer key, int count) {
		set(key.intValue(), count);
	}

	@Override
	public int inc(int key) {
		return inc(key, IntCounter.DEFAULT_INC);
	}
	
	@Override
	public int incI(Integer key) {
		return inc(key.intValue());
	}

	@Override
	public int inc(Integer key, int inc) {
		return inc(key.intValue(), inc);
	}
	
}
