package jhn.counts.d;



public abstract class AbstractDoubleCounterMap<K,V> implements DoubleCounterMap<K,V> {

	@Override
	public Double getCount(K key, V value) {
		return Double.valueOf(getCountD(key, value));
	}

	@Override
	public Double totalCount() {
		return Double.valueOf(totalCountD());
	}

	@Override
	public void inc(K key, V value, Double inc) {
		inc(key, value, inc.doubleValue());
	}

	@Override
	public void set(K key, V value, Double count) {
		set(key, value, count.doubleValue());
	}

}
