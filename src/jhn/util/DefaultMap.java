package jhn.util;

import java.util.HashMap;

public class DefaultMap<K,V> extends HashMap<K,V> {
	private static final long serialVersionUID = 1L;

	private final Factory<V> factory;
	public DefaultMap(Factory<V> factory) {
		this.factory = factory;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public V get(Object key) {
		V value = super.get(key);
		if(value == null) {
			value = factory.create();
			super.put((K) key, value);
		}
		return value;
	}
}
