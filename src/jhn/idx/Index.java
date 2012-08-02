package jhn.idx;


public interface Index<T> extends ForwardIndex<T>, ReverseIndex<T> {
	// This interface's methods are the union of the respective methods of ForwardIndex and ReverseIndex.
}
