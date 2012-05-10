package jhn.trie;

public interface StringShortTrie {
	public static final short defaultValue = Short.MIN_VALUE;

	void put(String key, short value);

	/**
	 * Returns defaultValue (Short.MIN_VALUE) if the key is not found
	 */
	short get(String key);

	boolean containsKey(String key);
	
	void increment(String key);
	
	void increment(String key, short inc);
}
