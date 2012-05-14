package jhn.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


public class Config {
	private final Map<String,Object> map = new HashMap<String,Object>();
	
	public boolean containsKey(String key) {
		return map.containsKey(key);
	}
	
	public void putDouble(String key, double value) {
		map.put(key, Double.valueOf(value));
	}
	
	public void putInt(String key, int value) {
		map.put(key, Integer.valueOf(value));
	}
	
	public void putBool(String key, boolean value) {
		map.put(key, Boolean.valueOf(value));
	}
	
	public void putString(String key, String value) {
		map.put(key, value);
	}
	
	public void putObj(String key, Object value) {
		map.put(key, value);
	}
	
	
	
	public double getDouble(String key) {
		return ((Double)map.get(key)).doubleValue();
	}
	
	public int getInt(String key) {
		return ((Integer)map.get(key)).intValue();
	}
	
	public boolean getBool(String key) {
		return ((Boolean)map.get(key)).booleanValue();
	}
	
	public String getString(String key) {
		return (String) map.get(key);
	}
	
	public Object getObj(String key) {
		return map.get(key);
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
