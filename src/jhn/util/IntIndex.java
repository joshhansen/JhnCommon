package jhn.util;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

public class IntIndex {
	private static final int KEY_NOT_FOUND = -1;
	
	private Int2IntMap map = new Int2IntOpenHashMap();
	private IntList list = new IntArrayList();
	
	public IntIndex() {
		map.defaultReturnValue(KEY_NOT_FOUND);
	}
	
	public int indexOf(int value) {
		int idx = map.get(value);
		if(idx==KEY_NOT_FOUND) {
			list.add(value);
			idx = list.size() - 1;
			map.put(value, idx);
		}
		return idx;
	}
	
	public int objectAt(int idx) {
		return list.getInt(idx);
	}
	
	public int size() {
		return list.size();
	}
	
	public IntList list() {
		return list;
	}
	
	public Int2IntMap map() {
		return map;
	}
}
