package jhn.counts.d;


public abstract class AbstractDoubleCounter<K> implements DoubleCounter<K> {

	@Override
	public Double getCount(K key) {
		return Double.valueOf(getCountD(key));
	}

	@Override
	public Double totalCount() {
		return Double.valueOf(totalCountD());
	}

	@Override
	public Double inc(K key) {
		return Double.valueOf(incD(key));
	}
	
	@Override
	public double incD(K key) {
		return inc(key, 1.0);
	}

	@Override
	public Double inc(K key, Double count) {
		return Double.valueOf(inc(key, count.doubleValue()));
	}

	@Override
	public void set(K key, Double count) {
		set(key, count.doubleValue());
	}

}
