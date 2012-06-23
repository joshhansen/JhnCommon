package jhn.idx;

import java.util.List;
import java.util.Map;

public interface Index<T> {
	public static final int KEY_NOT_FOUND = -1;
	
	int indexOf(T value);
	
	int indexOf(T value, boolean addIfNotPresent);
	
	T objectAt(int idx);
	
	int size();
	
	List<T> list();
	
	Map<T,Integer> map();
	
	boolean contains(T value);
}
