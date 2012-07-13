package jhn.counts;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.Set;

import jhn.util.Util;

public class IntIntIntCounterMap implements IntCounterMap<Integer,Integer>, Serializable {
	private static final long serialVersionUID = 1L;
	private Int2ObjectMap<Counter<Integer,Integer>> counters = newMap();

	private static Int2ObjectMap<Counter<Integer,Integer>> newMap() {
		return new Int2ObjectOpenHashMap<Counter<Integer,Integer>>();
	}
	
	@Override
	public Integer getCount(Integer key, Integer value) {
		return Integer.valueOf(getCount(key.intValue(), value.intValue()));
	}

	@Override
	public void inc(Integer key, Integer value) {
		inc(key.intValue(), value.intValue());
	}

	@Override
	public void inc(Integer key, Integer value, Integer inc) {
		inc(key.intValue(), value.intValue(), inc.intValue());
	}

	@Override
	public Set<Entry<Integer, Counter<Integer, Integer>>> entrySet() {
		return counters.entrySet();
	}
	
	public ObjectSet<Int2ObjectMap.Entry<Counter<Integer, Integer>>> int2ObjectEntrySet() {
		return counters.int2ObjectEntrySet();
	}

	@Override
	public int getCountI(Integer key, Integer value) {
		return getCount(key.intValue(), value.intValue());
	}

	@Override
	public void inc(Integer key, Integer value, int inc) {
		inc(key.intValue(), value.intValue(), inc);
	}
	
	public int getCount(int key, int value) {
		Counter<Integer,Integer> counter = counters.get(key);
		if(counter==null) {
			return 0;
		}
		return ((IntCounter<Integer>)counter).getCountI(value);
	}
	
	public void inc(int key, int value) {
		inc(key, value, 1);
	}
	
	public void inc(int key, int value, int inc) {
		Counter<Integer,Integer> counter = counters.get(key);
		if(counter==null) {
			counter = new IntIntRAMCounter();
			counters.put(key, counter);
		}
		((IntIntRAMCounter)counter).inc(value, inc);
	}

	@Override
	public void set(Integer key, Integer value, Integer count) {
		set(key.intValue(), value.intValue(), count.intValue());
	}

	@Override
	public void set(Integer key, Integer value, int count) {
		set(key.intValue(), value.intValue(), count);
	}
	
	public void set(int key, int value, int count) {
		Counter<Integer,Integer> counter = counters.get(key);
		if(counter==null) {
			counter = new IntIntRAMCounter();
			counters.put(key, counter);
		}
		((IntIntRAMCounter)counter).set(value, count);
	}
	
	private static final Comparator<Int2ObjectMap.Entry<?>> entryCmp = new Comparator<Int2ObjectMap.Entry<?>>(){
		@Override
		public int compare(Int2ObjectMap.Entry<?> o1, Int2ObjectMap.Entry<?> o2) {
			return Util.compareInts(o1.getIntKey(), o2.getIntKey());
		}
	};
	
	private static final Comparator<Int2IntMap.Entry> countCmp = new Comparator<Int2IntMap.Entry>(){
		@Override
		public int compare(Int2IntMap.Entry o1, Int2IntMap.Entry o2) {
			return Util.compareInts(o1.getIntKey(), o2.getIntKey());
		}
	};

	public static final int END_OF_KEY = -1;
	private static final int NULL_IDX = -3;
	public void writeObject (ObjectOutputStream oos) throws IOException {
		@SuppressWarnings("unchecked")
		Int2ObjectMap.Entry<Counter<Integer,Integer>>[] entries = int2ObjectEntrySet().toArray(new Int2ObjectMap.Entry[0]);
		Arrays.sort(entries, entryCmp);
		for(Int2ObjectMap.Entry<Counter<Integer,Integer>> entry : entries) {
			// Write word1idx
			oos.writeInt(entry.getIntKey());
			
			Int2IntMap.Entry[] arr = ((IntIntRAMCounter)entry.getValue()).int2IntEntrySet().toArray(new Int2IntMap.Entry[0]);
			Arrays.sort(arr, countCmp);
			for(Int2IntMap.Entry count : arr) {
				// Write word2idx
				oos.writeInt(count.getIntKey());
				// Write count
				oos.writeInt(count.getIntValue());
			}
			
			oos.writeInt(END_OF_KEY);
		}
	}
	
	private void readObject (ObjectInputStream ois) throws IOException, ClassNotFoundException {
		 counters = new Int2ObjectOpenHashMap<Counter<Integer,Integer>>();
		 
		 int word1idx;
		 int word2idx;
		 int count;
		 while(ois.available() > 0) {
			 word1idx = ois.readInt();
			 
			 word2idx = NULL_IDX;
			 while(true) {
				 word2idx = ois.readInt();
				 if(word2idx==END_OF_KEY) {
					 break;
				 }
				 count = ois.readInt();
				 
				 this.set(word1idx, word2idx, count);
			 }
		 }
	}
	
	public void reset() {
		counters = newMap();
	}
	
	/** The total number of (key,value) pairs in all counters */
	public int totalSize() {
		int total = 0;
		for(Int2ObjectMap.Entry<Counter<Integer,Integer>> entry : int2ObjectEntrySet()) {
			total += entry.getValue().size();
		}
		return total;
	}

	@Override
	public boolean containsKey(Integer key) {
		return counters.containsKey(key);
	}
	
	public boolean containsKey(int key) {
		return counters.containsKey(key);
	}

	@Override
	public boolean containsValue(Integer key, Integer value) {
		return containsValue(key.intValue(), value.intValue());
	}
	
	public boolean containsValue(int key, int value) {
		Counter<Integer,Integer> counter = counters.get(key);
		if(counter==null) {
			return false;
		}
		return ((IntCounter<Integer>)counter).containsKey(value);
	}
}
