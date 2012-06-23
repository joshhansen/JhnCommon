package jhn.idx;

import java.util.List;
import java.util.Map;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

public class StringIndex implements Index<String> {
	public static final int KEY_NOT_FOUND = -1;
	
	private ObjectList<String> list = new ObjectArrayList<String>();
	private Object2IntMap<String> map = new Object2IntOpenHashMap<String>();
	
	public StringIndex() {
		map.defaultReturnValue(KEY_NOT_FOUND);
	}
	
	@Override
	public int indexOf(String value) {
		return indexOf(value, true);
	}

	@Override
	public int indexOf(String value, boolean addIfNotPresent) {
		int idx = map.getInt(value);
		if(addIfNotPresent && idx==KEY_NOT_FOUND) {
			list.add(value);
			idx = list.size() - 1;
			map.put(value, idx);
		}
		return idx;
	}

	@Override
	public String objectAt(int idx) {
		return list.get(idx);
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public List<String> list() {
		return list;
	}

	@Override
	public Map<String, Integer> map() {
		return map;
	}

	@Override
	public boolean contains(String value) {
		return map.containsKey(value);
	}
}
