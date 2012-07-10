package jhn.idx;


public interface ForwardIndex<T> {
	int size();
	
	T objectAt(int idx);
}
