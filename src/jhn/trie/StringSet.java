package jhn.trie;

import java.util.Iterator;

public interface StringSet extends Iterable<String> {
	void add(String s);
	boolean contains(String s);
	Iterator<String> iterator();
}
