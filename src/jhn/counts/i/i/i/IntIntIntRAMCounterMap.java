package jhn.counts.i.i.i;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.Set;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;

import jhn.counts.Counter;
import jhn.counts.i.i.IntIntCounter;
import jhn.counts.i.i.IntIntRAMCounter;
import jhn.util.Util;

public class IntIntIntRAMCounterMap extends AbstractIntIntIntCounterMap implements Serializable {
	private static final long serialVersionUID = 1L;
	private Int2ObjectMap<Counter<Integer,Integer>> counters = newMap();

	private static Int2ObjectMap<Counter<Integer,Integer>> newMap() {
		return new Int2ObjectOpenHashMap<>();
	}
	
	private static IntIntCounter newCounter() {
		return new IntIntRAMCounter();
	}

	@Override
	public Set<Entry<Integer, Counter<Integer, Integer>>> entrySet() {
		return counters.entrySet();
	}
	
	@Override
	public ObjectSet<Int2ObjectMap.Entry<Counter<Integer, Integer>>> int2ObjectEntrySet() {
		return counters.int2ObjectEntrySet();
	}
	
	@Override
	public int getCount(int key, int value) {
		Counter<Integer,Integer> counter = counters.get(key);
		if(counter==null) {
			return 0;
		}
		return ((IntIntCounter)counter).getCount(value);
	}

	@Override
	public IntIntCounter getCounter(int key) {
		IntIntCounter counter = (IntIntCounter) counters.get(key);
		if(counter==null) {
			counter = newCounter();
			counters.put(key, counter);
		}
		return counter;
	}
	
	@Override
	public void inc(int key, int value) {
		inc(key, value, 1);
	}
	
	@Override
	public void inc(int key, int value, int inc) {
		IntIntCounter counter = getCounter(key);
		counter.inc(value, inc);
	}
	
	@Override
	public void set(int key, int value, int count) {
		IntIntCounter counter = getCounter(key);
		counter.set(value, count);
	}
	
	private static final Comparator<Int2ObjectMap.Entry<?>> entryCmp = new Comparator<Int2ObjectMap.Entry<?>>(){
		@Override
		public int compare(Int2ObjectMap.Entry<?> o1, Int2ObjectMap.Entry<?> o2) {
			return Util.compareInts(o1.getIntKey(), o2.getIntKey());
		}
	};

	
	public void writeObject (ObjectOutputStream oos) throws IOException {
		@SuppressWarnings("unchecked")
		Int2ObjectMap.Entry<Counter<Integer,Integer>>[] entries = int2ObjectEntrySet().toArray(new Int2ObjectMap.Entry[0]);
		Arrays.sort(entries, entryCmp);
		for(Int2ObjectMap.Entry<Counter<Integer,Integer>> entry : entries) {
			// Write word1idx
			oos.writeInt(entry.getIntKey());
			
			IntIntRAMCounter.writeObj((IntIntCounter) entry.getValue(), oos);
		}
	}
	
	private void readObject (ObjectInputStream ois) throws IOException {
		 counters = newMap();
		 
		 int word1idx;
		 IntIntCounter counter;
		 while(ois.available() > 0) {
			 word1idx = ois.readInt();
			 counter = getCounter(word1idx);
			 IntIntRAMCounter.readObj(counter, ois);
		 }
	}
	
	@Override
	public void reset() {
		counters = newMap();
	}
	
	/** The total number of (key,value) pairs in all counters */
	@Override
	public int totalSize() {
		int total = 0;
		for(Int2ObjectMap.Entry<Counter<Integer,Integer>> entry : int2ObjectEntrySet()) {
			total += entry.getValue().size();
		}
		return total;
	}
	
	@Override
	public boolean containsKey(int key) {
		return counters.containsKey(key);
	}
	
	@Override
	public boolean containsValue(int key, int value) {
		IntIntCounter counter = (IntIntCounter) counters.get(key);
		if(counter==null) {
			return false;
		}
		return counter.containsKey(value);
	}

	@Override
	public int totalCountI() {
		int total = 0;
		for(Counter<Integer,Integer> counter : counters.values()) {
			total += ((IntIntCounter)counter).totalCountI();
		}
		return total;
	}
}
