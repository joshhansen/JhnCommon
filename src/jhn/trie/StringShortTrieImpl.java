package jhn.trie;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;

import it.unimi.dsi.fastutil.chars.Char2ObjectArrayMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;

import jhn.util.Util;

public class StringShortTrieImpl implements StringShortTrie, Serializable {
	private static final long serialVersionUID = 1L;

	private short value = defaultValue;
	private Char2ObjectMap<StringShortTrieImpl> children = new Char2ObjectArrayMap<>();
	
	@Override
	public void put(String key, short newValue) {
		if(key.length()==0) {
			this.value = newValue;
		} else {
			StringShortTrieImpl child = firstChildFor(key);
			if(child == null) {
				child = new StringShortTrieImpl();
				children.put(firstChar(key), child);
			}
			child.put(remainder(key), newValue);
		}
	}

	@Override
	public short get(String key) {
//		System.out.println("get(" + key + ")");
		StringShortTrieImpl child = firstChildFor(key);
		
		if(child == null) return defaultValue;
		
		if(key.length() == 1) return child.value;
		
		return child.get(remainder(key));
	}

	@Override
	public boolean containsKey(String key) {
		StringShortTrieImpl child = firstChildFor(key);
		
		if(child == null) return false;
		
		if(key.length()==1) return child.value != defaultValue;
		
		return child.containsKey(remainder(key));
	}
	
	private StringShortTrieImpl firstChildFor(String key) {
		return children.get(firstChar(key));
	}
	
	private static char firstChar(String s) {
		return s.charAt(0);
	}

	private static String remainder(String s) {
		return s.substring(1);
	}

	@Override
	public void increment(String key) {
		increment(key, (short) 1);
	}

	@Override
	public void increment(String key, short inc) {
		short current = get(key);
		if(current==defaultValue) current = 0;
		
		int newVal = current + inc;
		
		if(newVal >= Short.MAX_VALUE) newVal = Short.MAX_VALUE;
		
		put(key, (short) newVal);
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
		final String outputFilename = System.getenv("HOME") + "/Projects/eda_output/test.ser";
		
		StringShortTrie sst = new StringShortTrieImpl();
		sst.put("abcdefghijklmnopqrstuvwxyz", (short) 949);
		sst.put("abcdefgfedcba", (short) -4);
		Util.serialize(sst, outputFilename);
		
		StringShortTrie sst2 = (StringShortTrie) Util.deserialize(outputFilename);
		System.out.println(sst2.get("abcdefghijklmnopqrstuvwxyz"));
		System.out.println(sst2.get("abcdefgfedcba"));
		
	}
}
