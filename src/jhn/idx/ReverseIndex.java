package jhn.idx;


public interface ReverseIndex<T> {
	public static final int KEY_NOT_FOUND = -1;
	
	int indexOf(T value);
	
	int indexOf(T value, boolean addIfNotPresent);
	
	boolean contains(T value);
}
