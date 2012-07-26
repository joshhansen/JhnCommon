package jhn.counts.i;


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

}
