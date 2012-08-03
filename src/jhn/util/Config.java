package jhn.util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


public class Config implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private final Map<String,Object> map = new HashMap<>();

	public boolean containsKey(String key) {
		return map.containsKey(key);
	}
	
	public boolean containsKey(Enum<?> key) {
		return containsKey(key.toString());
	}
	
	public void putDouble(String key, double value) {
		map.put(key, Double.valueOf(value));
	}
	
	public void putDouble(Enum<?> key, double value) {
		putDouble(key.toString(), value);
	}
	
	public void putDoubleArr(String key, double... arr) {
		map.put(key, arr);
	}
	
	public void putDoubleArr(Enum<?> key, double... arr) {
		putDoubleArr(key.toString(), arr);
	}
	
	public void putInt(String key, int value) {
		map.put(key, Integer.valueOf(value));
	}
	
	public void putInt(Enum<?> key, int value) {
		putInt(key.toString(), value);
	}
	
	public void putBool(String key, boolean value) {
		map.put(key, Boolean.valueOf(value));
	}
	
	public void putBool(Enum<?> key, boolean value) {
		putBool(key.toString(), value);
	}
	
	public void putString(String key, String value) {
		map.put(key, value);
	}
	
	public void putString(Enum<?> key, String value) {
		map.put(key.toString(), value);
	}
	
	public void putObj(String key, Object value) {
		map.put(key, value);
	}
	
	public void putObj(Enum<?> key, Object value) {
		putObj(key.toString(), value);
	}
	
	
	
	public double getDouble(String key) {
		return ((Double)map.get(key)).doubleValue();
	}
	
	public double getDouble(Enum<?> key) {
		return getDouble(key.toString());
	}
	
	public double[] getDoubleArr(String key) {
		return (double[]) map.get(key);
	}
	
	public double[] getDoubleArr(Enum<?> key) {
		return getDoubleArr(key.toString());
	}
	
	public int getInt(String key) {
		return ((Integer)map.get(key)).intValue();
	}
	
	public int getInt(Enum<?> key) {
		return getInt(key.toString());
	}
	
	public boolean getBool(String key) {
		return ((Boolean)map.get(key)).booleanValue();
	}
	
	public boolean getBool(Enum<?> key) {
		return getBool(key.toString());
	}
	
	public boolean isTrue(String key) {
		return containsKey(key) && getBool(key);
	}
	
	public boolean isTrue(Enum<?> key) {
		return isTrue(key.toString());
	}
	
	public boolean isFalse(Enum<?> key) {
		return isFalse(key.toString());
	}
	
	public boolean isFalse(String key) {
		if(containsKey(key)) {
			return !getBool(key);
		}
		return false;
	}
	
	public String getString(String key) {
		return (String) map.get(key);
	}
	
	public String getString(Enum<?> key) {
		return getString(key.toString());
	}
	
	public Object getObj(String key) {
		return map.get(key);
	}
	
	public Object getObj(Enum<?> key) {
		return getObj(key.toString());
	}
	
	public void update(Config other) {
		this.map.putAll(other.map);
	}
	
	private static final Comparator<Entry<String,Object>> itemCmptor = new Comparator<Entry<String,Object>>(){
		@Override
		public int compare(Entry<String, Object> o1, Entry<String, Object> o2) {
			return o1.getKey().compareTo(o2.getKey());
		}
	};
	
	@Override
	public String toString() {
		return toString(0);
	}
	
	public String toString(int tabs) {
		StringBuilder s = new StringBuilder();
		
		@SuppressWarnings("unchecked")
		Entry<String,Object>[] entries = map.entrySet().toArray(new Entry[0]);
		Arrays.sort(entries, itemCmptor);
		
		for(Entry<String,Object> entry : entries) {
			for(int i = 0; i < tabs; i++) {
				s.append('\t');
			}
			s.append(entry.getKey());
			s.append(':');
			String valString = entry.getValue().toString();
			if(valString.length() > 200) {
				s.append("<OBJ>");
			} else {
				s.append(valString);
			}
			s.append('\n');
		}
		
		return s.toString();
	}
}
