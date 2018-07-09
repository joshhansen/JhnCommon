package jhn.counts.i.i;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectSet;

import jhn.util.Util;

public class IntIntRAMCounter extends AbstractIntIntCounter implements Serializable {
	public static final int NO_MORE_ENTRIES = -10395782;
	
	private final Int2IntMap counts;
	
	private int totalCount = 0;
	
	public IntIntRAMCounter() {
		this(new Int2IntOpenHashMap());
	}
	
	public IntIntRAMCounter(Int2IntMap map) {
		this.counts = map;
	}
	
	@Override
	public int inc(final int key) {
		return inc(key, 1);
	}
	
	@Override
	public int inc(int key, int inc) {
		final int newVal = getCount(key)+inc;
		counts.put(key, newVal);
		totalCount += inc;
		return newVal;
	}
	
	@Override
	public void set(int key, int count) {
		totalCount -= getCount(key);
		counts.put(key, count);
		totalCount += count;
	}
	
	@Override
	public int getCount(int key) {
		return counts.get(key);
	}

	@Override
	public int totalCountI() {
		return totalCount;
	}
	
	@Override
	public int defaultReturnValueI() {
		return counts.defaultReturnValue();
	}
	
	@Override
	public Set<Entry<Integer,Integer>> entries() {
		return counts.entrySet();
	}
	
	public static final Comparator<Entry<Integer,Integer>> cmp = new Comparator<Entry<Integer,Integer>>(){
		@Override
		public int compare(Entry<Integer, Integer> o1, Entry<Integer, Integer> o2) {
			return o2.getValue().compareTo(o1.getValue());
		}
	};
	
	@Override
	public List<Entry<Integer,Integer>> topN(int n) {
		ObjectList<Entry<Integer,Integer>> entries = new ObjectArrayList<>(entries());
		Collections.sort(entries, cmp);
		return entries.subList(0, Math.min(n, entries.size()));
	}
	
	public static final Comparator<Int2IntMap.Entry> fastCmp = new Comparator<Int2IntMap.Entry>(){
		@Override
		public int compare(Int2IntMap.Entry o1, Int2IntMap.Entry o2) {
			return Util.compareInts(o2.getIntValue(), o1.getIntValue());
		}
	};
	
	@Override
	public List<Int2IntMap.Entry> fastTopN(int n) {
		ObjectList<Int2IntMap.Entry> entries = new ObjectArrayList<>(int2IntEntrySet());
		Collections.sort(entries, fastCmp);
		return entries.subList(0, Math.min(n, entries.size()));
	}

	@Override
	public ObjectSet<Int2IntMap.Entry> int2IntEntrySet() {
		return counts.int2IntEntrySet();
	}

	@Override
	public int size() {
		return counts.size();
	}

	@Override
	public boolean containsKey(int key) {
		return counts.containsKey(key);
	}

	@Override
	public IntSet keySet() {
		return counts.keySet();
	}

	@Override
	public void reset() {
		counts.clear();
	}
	
	public static final Comparator<Int2IntMap.Entry> countCmp = new Comparator<Int2IntMap.Entry>(){
		@Override
		public int compare(Int2IntMap.Entry o1, Int2IntMap.Entry o2) {
			return Util.compareInts(o1.getIntKey(), o2.getIntKey());
		}
	};
	
	public static void writeObj(IntIntCounter counter, ObjectOutputStream oos) throws IOException {
		Int2IntMap.Entry[] arr = counter.int2IntEntrySet().toArray(new Int2IntMap.Entry[0]);
		Arrays.sort(arr, countCmp);
		for(Int2IntMap.Entry count : arr) {
			// Write word2idx
			oos.writeInt(count.getIntKey());
			// Write count
			oos.writeInt(count.getIntValue());
		}
		oos.writeInt(NO_MORE_ENTRIES);
	}
	
	private void writeObject (ObjectOutputStream oos) throws IOException {
		writeObj(this, oos);
	}
	
	public static void readObj(IntIntCounter counter, ObjectInputStream ois) throws IOException {
		 int item;
		 int count;
		 while(true) {
			 item = ois.readInt();
			 if(item==NO_MORE_ENTRIES) {
				 break;
			 }
			 count = ois.readInt();
			 counter.set(item, count);
		 }
	}
	
	private void readObject (ObjectInputStream ois) throws IOException {
		readObj(this, ois);
	}

}
