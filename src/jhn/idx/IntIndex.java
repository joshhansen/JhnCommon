package jhn.idx;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

public class IntIndex implements Index<Integer>, Serializable {
	private static final long serialVersionUID = 1L;

	public static final int KEY_NOT_FOUND = -1;
	
	private Int2IntMap map = new Int2IntOpenHashMap();
	private IntList list = new IntArrayList();
	
	public IntIndex() {
		map.defaultReturnValue(KEY_NOT_FOUND);
	}
	
	public int indexOfI(int value) {
		return indexOfI(value, true);
	}
	
	public int indexOfI(int value, boolean addIfNotPresent) {
		int idx = map.get(value);
		if(addIfNotPresent && idx==KEY_NOT_FOUND) {
			list.add(value);
			idx = list.size() - 1;
			map.put(value, idx);
		}
		return idx;
	}
	
	public int objectAtI(int idx) {
		return list.getInt(idx);
	}
	
	public int size() {
		return list.size();
	}
	
	public IntList listI() {
		return list;
	}
	
	public Int2IntMap mapI() {
		return map;
	}
	
	public boolean containsI(int value) {
		return map.containsKey(value);
	}

	@Override
	public int indexOf(Integer value) {
		return indexOfI(value.intValue());
	}

	@Override
	public int indexOf(Integer value, boolean addIfNotPresent) {
		return indexOfI(value.intValue(), addIfNotPresent);
	}

	public List<Integer> list() {
		return list;
	}

	public Map<Integer, Integer> map() {
		return map;
	}

	@Override
	public boolean contains(Integer value) {
		return containsI(value.intValue());
	}

	@Override
	public Integer objectAt(int idx) {
		return Integer.valueOf(objectAtI(idx));
	}
}
