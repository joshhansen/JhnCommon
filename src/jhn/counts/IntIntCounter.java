package jhn.counts;

/** A counter whose counts and keys are integers */
public interface IntIntCounter extends IntCounter<Integer> {
	void set(int key, int count);
	
	void inc(int key);
	
	void inc(int key, int inc);
	
	boolean containsKey(int key);
	
	int getCountI(int key);
}
