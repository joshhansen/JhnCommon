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
	
	public void put(String key, int value) {
		map.put(key, Integer.valueOf(value));
	}
	
	public void put(String key, boolean value) {
		map.put(key, Boolean.valueOf(value));
	}
	
	public void put(String key, String value) {
		map.put(key, value);
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
	
	private static final Comparator<Entry<String,Object>> itemCmptor = new Comparator<Entry<String,Object>>(){
		@Override
		public int compare(Entry<String, Object> o1, Entry<String, Object> o2) {
			return o1.getKey().compareTo(o2.getKey());
		}
	};
	
	public String toString() {
		StringBuilder s = new StringBuilder();
		
		@SuppressWarnings("unchecked")
		Entry<String,Object>[] entries = map.entrySet().toArray(new Entry[0]);
		Arrays.sort(entries, itemCmptor);
		
		for(Entry<String,Object> entry : entries) {
			s.append(entry.getKey());
			s.append(':');
			s.append(entry.getValue());
			s.append('\n');
		}
		
		return s.toString();
	}
}
