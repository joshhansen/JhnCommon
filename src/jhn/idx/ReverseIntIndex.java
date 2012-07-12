package jhn.idx;

public interface ReverseIntIndex extends ReverseIndex<Integer> {
	int indexOfI(int key);

	int indexOfI(int key, boolean addIfNotPresent);
	
	boolean containsI(int key);
}
