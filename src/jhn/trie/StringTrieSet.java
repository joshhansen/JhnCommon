package jhn.trie;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;

import it.unimi.dsi.fastutil.chars.Char2ObjectArrayMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;

import jhn.util.Util;

public class StringTrieSet implements StringSet, Serializable {
	private static final long serialVersionUID = 1L;
	
	private boolean value = false;
	private Char2ObjectMap<StringTrieSet> children = new Char2ObjectArrayMap<>();
	
	@Override
	public void add(String s) {
		if(s.length()==0) {
			this.value = true;
		} else {
			StringTrieSet child = firstChildFor(s);
			if(child == null) {
				child = new StringTrieSet();
				children.put(firstChar(s), child);
			}
			child.add(remainder(s));
		}
	}

	@Override
	public boolean contains(String key) {
		StringTrieSet child = firstChildFor(key);
		
		if(child == null) return false;
		
		if(key.length()==1) return child.value;
		
		return child.contains(remainder(key));
	}
	
	private StringTrieSet firstChildFor(String key) {
		return children.get(firstChar(key));
	}
	
	private static char firstChar(String s) {
		return s.charAt(0);
	}

	private static String remainder(String s) {
		return s.substring(1);
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
		final String outputFilename = System.getenv("HOME") + "/Projects/eda_output/test.ser";
		
		StringSet sst = new StringTrieSet();
		sst.add("abcdefghijklmnopqrstuvwxyz");
		sst.add("abcdefgfedcba");
		Util.serialize(sst, outputFilename);
		
		StringSet sst2 = (StringSet) Util.deserialize(outputFilename);
		System.out.println(sst2.contains("abcdefghijklmnopqrstuvwxyz"));
		System.out.println(sst2.contains("abcdefgfedcba"));
		
	}

//	private class STSIterator implements Iterator<String> {
//		private StringTrieSet parent;
//		private StringTrieSet current;
//		
//		public STSIterator() {
//			
//		}
//		@Override
//		public boolean hasNext() {
//			// TODO Auto-generated method stub
//			return false;
//		}
//
//		@Override
//		public String next() {
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//		@Override
//		public void remove() {
//			// TODO Auto-generated method stub
//			
//		}
//		
//	}
	
	@Override
	public Iterator<String> iterator() {
		// TODO Auto-generated method stub
		return null;
	}
}
