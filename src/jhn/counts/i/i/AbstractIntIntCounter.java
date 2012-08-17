package jhn.counts.i.i;

import it.unimi.dsi.fastutil.ints.Int2IntMap;

import jhn.counts.i.AbstractIntCounter;
import jhn.counts.i.IntCounter;
import jhn.util.RandUtil;

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

	@Override
	public Integer sample() {
		return Integer.valueOf(sampleI());
	}
	
	@Override
	public int sampleI() {
		int position = RandUtil.rand.nextInt(totalCountI());
		int sum = 0;
		for(Int2IntMap.Entry entry : int2IntEntrySet()) {
			sum += entry.getIntValue();
			if(sum >= position) {
				return entry.getIntKey();
			}
		}
		throw new IllegalStateException("This code should never be reached");
	}
	
	
}
