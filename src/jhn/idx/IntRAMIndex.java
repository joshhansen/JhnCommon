package jhn.idx;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

public class IntRAMIndex implements Serializable, IntIndex {
	private static final long serialVersionUID = 1L;

	private Int2IntMap map = new Int2IntOpenHashMap();
	private IntList list = new IntArrayList();
	
	public IntRAMIndex() {
		map.defaultReturnValue(ReverseIndex.KEY_NOT_FOUND);
	}
	
	@Override
	public int indexOfI(int value) {
		return indexOfI(value, true);
	}
	
	@Override
	public int indexOfI(int value, boolean addIfNotPresent) {
		int idx = map.get(value);
		if(addIfNotPresent && idx==ReverseIndex.KEY_NOT_FOUND) {
			list.add(value);
			idx = list.size() - 1;
			map.put(value, idx);
		}
		return idx;
	}
	
	@Override
	public int objectAtI(int idx) {
		return list.getInt(idx);
	}
	
	@Override
	public int size() {
		return list.size();
	}
	
	public IntList listI() {
		return list;
	}
	
	public Int2IntMap mapI() {
		return map;
	}
	
	@Override
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
