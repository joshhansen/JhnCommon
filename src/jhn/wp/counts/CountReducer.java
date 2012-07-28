package jhn.wp.counts;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import jhn.Paths;
import jhn.counts.i.i.IntIntRAMCounter;

public class CountReducer implements Runnable {
	private static final int EMPTY_STREAM = -10;
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
				
				processMinKey(keys);
			} while(true);
			out.writeInt(IntIntRAMCounter.NO_MORE_ENTRIES);
			
			
			// Clean up
			out.close();
			for(ObjectInputStream ois : in) {
				ois.close();
			}
			
//			for(File f : src) {
//				f.delete();
//			}
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
	
	private void processMinKey(int[] keys) throws Exception {
		int minKey = minKey(keys);
		IntList indices = indicesMatching(keys, minKey);
		
		int minKeyValueSum = 0;
		for(int idx : indices) {
			minKeyValueSum += in[idx].readInt();
			keys[idx] = KEY_READ;
		}
		
		out.writeInt(minKey);
		out.writeInt(minKeyValueSum);
		
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
	
	public static void main(String[] args) throws Exception {
		File[] src = new File(Paths.outputDir("JhnCommon") + "/counts/chunks").listFiles();
		File dest = new File(Paths.outputDir("JhnCommon") + "/counts/all.counts");
		CountReducer cr = new CountReducer(src, dest);
		cr.run();
	}
}
