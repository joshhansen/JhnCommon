package jhn.idx;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class StringIndex implements Index<String>, Serializable {
	private static final long serialVersionUID = 1L;

	public static final int KEY_NOT_FOUND = -1;
	
	private ObjectArrayList<String> list = new ObjectArrayList<String>();
	private Object2IntOpenHashMap<String> map = new Object2IntOpenHashMap<String>();
	
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
	
	public void trim() {
		list.trim();
		map.trim();
	}
	
	/** Allows serialization of only the int->String reverse mapping, as a String[]. This should achieve substantial
	 * space savings versus serializing the entire StringIndex if the String->int mapping won't be needed again. */
	public void writeReverseIndex(ObjectOutputStream out) throws IOException {
		list.trim();
		out.writeObject(list.elements());
	}
	
	public void writeForwardIndex(ObjectOutputStream out) throws IOException {
		map.trim();// Try to trim first
		out.writeObject(map);
	}
}
