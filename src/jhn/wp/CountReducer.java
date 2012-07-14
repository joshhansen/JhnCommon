package jhn.wp;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import jhn.counts.ints.IntIntIntCounterMap;

public class CountReducer implements Runnable {
	private static final int EMPTY_STREAM = -10;
	private static final int NULL = -11;
	private static final int KEY_READ = -12;

	private final File[] src;
	private final File dest;
	
	private ObjectOutputStream out;
	private ObjectInputStream[] in;
	public CountReducer(File[] src, File dest) {
		this.src = src;
		this.dest = dest;
	}
	
	private static boolean someStreamNotEmpty(int[] keys) throws Exception {
		for(int key : keys) {
			if(key >= 0) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void run() {
		try {
			System.out.println(dest.getPath());
			for(File f : src) {
				System.out.println("\t" + f.getName());
			}
			
			out = new ObjectOutputStream(new FileOutputStream(dest));
			in = new ObjectInputStream[src.length];
			for(int i = 0; i < src.length; i++) {
				in[i] = new ObjectInputStream(new FileInputStream(src[i]));
			}
			
			int[] keys = null;
			do {
				keys = updateKeys(keys);
				if(!someStreamNotEmpty(keys)) {
					break;
				}
				
				processMinKeys(keys);
			} while(true);
			
			// Clean up
			out.close();
			for(ObjectInputStream ois : in) {
				ois.close();
			}
			
			for(File f : src) {
				f.delete();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private int[] updateKeys(int[] keys) throws Exception {
		if(keys == null) {
			keys = new int[in.length];
			Arrays.fill(keys, KEY_READ);
		}
		for(int i = 0; i < keys.length; i++) {
			if(keys[i]==KEY_READ) {
				try {
					keys[i] = in[i].readInt();
				} catch(EOFException e) {
					keys[i] = EMPTY_STREAM;
				}
			}
		}
		return keys;
	}
	
	private void processMinKeys(int[] keys) throws Exception {
		int minKey = minKey(keys);
		processMinKey(keys, minKey, indicesMatching(keys, minKey));
	}
	
	private static int minKey(int... ints) {
		int min = Integer.MAX_VALUE;
		for(int i : ints) {
			if(i < min && i >= 0) {// gte 0 so we don't count error codes
				min = i;
			}
		}
		return min;
	}
	
	private static IntList indicesMatching(int[] values, int targetValue) {
		IntList inputsWithMinKey = new IntArrayList();
		for(int i = 0; i < values.length; i++) {
			if(values[i] == targetValue) {
				inputsWithMinKey.add(i);
			}
		}
		return inputsWithMinKey;
	}
	
	private void processMinKey(int[] keys, int minKey, IntList indices) throws Exception {
		out.writeInt(minKey);
		
		int[] subkeys = getSubkeys(indices);
		int sum;
		do {
			int minSubkey = minKey(subkeys);
			IntList minSubkeyIndices = indicesMatching(subkeys, minSubkey);
			sum = subValueSum(minSubkeyIndices);
			
			out.writeInt(minSubkey);
			out.writeInt(sum);
			
			updateActiveSubkeys(subkeys, minSubkeyIndices);
		} while(subkeysRemain(subkeys));
		
		// Mark keys as exhausted so they're dealt with properly in the upper loop
		for(int i = 0; i < subkeys.length; i++) {
			if(subkeys[i]==IntIntIntCounterMap.END_OF_KEY) {
				keys[i] = KEY_READ;
			}
		}
		
		out.writeInt(IntIntIntCounterMap.END_OF_KEY);
	}
	
	private int subValueSum(IntList subkeyIndices) throws Exception {
		int sum = 0;
		for(int idx : subkeyIndices) {
			sum += in[idx].readInt();
		}
		return sum;
	}
	
	private void updateActiveSubkeys(int[] subkeys, IntList minSubkeyIndices) throws Exception {
		for(int i : minSubkeyIndices) {
			subkeys[i] = in[i].readInt();
		}
	}

	private static boolean subkeysRemain(int[] subkeys) {
		for(int i = 0; i < subkeys.length; i++) {
			if(subkeys[i] >= 0) {// gte 0 makes sure error code values are ignored
				return true;
			}
		}
		return false;
	}
	
	private int[] getSubkeys(IntList indices) throws Exception {
		int[] subkeys = new int[in.length];
		for(int i = 0; i < subkeys.length; i++) {
			if(!indices.contains(i)) {
				subkeys[i] = NULL;
			} else {
				subkeys[i] = in[i].readInt();
			}
		}
		return subkeys;
	}
	
//	public static void main(String[] args) throws Exception {
//		CountReducer cr = new CountReducer(new File(Paths.outputDir("JhnCommon") + "/cocounts/phase1-work"));
//		cr.reduce();
//	}
}
