package jhn.util;

public class ConstFactory<T> implements Factory<T> {
	private final T instance;
	
	public ConstFactory(T instance) {
		this.instance = instance;
	}

	@Override
	public T create() {
		return instance;
	}
	
}
