package jhn.wp;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import jhn.Paths;
import jhn.counts.IntIntIntCounterMap;

public class CountReducer {
	private static final int EMPTY_STREAM = -10;
	private static final int NULL = -11;
	private static final int KEY_READ = -12;
	private static final String FILE_EXT = ".counts";
	private final File dir;
	
	private ObjectOutputStream out;
	private ObjectInputStream[] in;
	public CountReducer(File dir) {
		this.dir = dir;
	}
	
	private boolean someStreamNotEmpty() throws Exception {
		for(ObjectInputStream ois : in) {
			if(ois.available() > 0) {
				return true;
			}
		}
		return false;
	}
	
	private String nextFilename() {
		return dir.getPath() + "/" + nextInt() + FILE_EXT;
	}
	
	private static final FileFilter filter = new FileFilter(){
		@Override
		public boolean accept(File f) {
			return f.getName().endsWith(FILE_EXT);
		}
	};
	private int nextInt() {
		int max = -1;
		int value;
		for(File f : dir.listFiles(filter)) {
			value = Integer.parseInt(f.getName().split("\\.")[0]);
			if(value > max) {
				max = value;
			}
		}
		return max+1;
	}
	
	private static final int REDUCE_AT_A_TIME = 2;
	public void reduce() throws Exception {
		while(true) {
			File[] files = dir.listFiles(filter);
			if(files.length <= 1) {
				break;
			}
			reduce(nextFilename(), Arrays.copyOf(files, REDUCE_AT_A_TIME));
		}
	}
	
	private void readInts(int[] dest, int... indices) throws Exception {
		for(int index : indices) {
			if(in[index].available() <= 0) {
				dest[index] = EMPTY_STREAM;
			} else {
				dest[index] = in[index].readInt();
			} 
		}
	}
	
	private void reduce(String destFilename, File... srcFiles) throws Exception {
		in = new ObjectInputStream[srcFiles.length];
		for(int i = 0; i < srcFiles.length; i++) {
			in[i] = new ObjectInputStream(new FileInputStream(srcFiles[i]));
		}
		
		int[] keys = null;
		while(someStreamNotEmpty()) {
			keys = updateKeys(keys);
			processMinKeys(keys);
		}
	}
	
	private int[] updateKeys(int[] keys) throws Exception {
		if(keys == null) {
			keys = new int[in.length];
			Arrays.fill(keys, KEY_READ);
		}
		for(int i = 0; i < keys.length; i++) {
			if(keys[i]==KEY_READ) {
				readInts(keys, i);
				
				if(in[i].available() <= 0) {
					keys[i] = EMPTY_STREAM;
				} else {
					keys[i] = in[i].readInt();
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
			if(i < min && i != EMPTY_STREAM) {
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
		
		int[] subkeys = getSubkeys(keys, indices);
		int minSubkey = minKey(subkeys);
		IntList minSubkeyIndices = indicesMatching(keys, minKey);
		
		int sum;
		do {
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
		int[] values = new int[subkeyIndices.size()];
		for(int i = 0; i < values.length; i++) {
			values[i] = in[subkeyIndices.getInt(i)].readInt();
		}
		return sum(values);
	}
	
	private static int sum(int... values) {
		int sum = 0;
		for(int value : values) {
			sum += value;
		}
		return sum;
	}
	
	private void updateActiveSubkeys(int[] subkeys, IntList minSubkeyIndices) throws Exception {
		for(int i : minSubkeyIndices) {
			if(subkeys[i] != IntIntIntCounterMap.END_OF_KEY) {
				subkeys[i] = in[i].readInt();
			}
		}
	}

	private static boolean subkeysRemain(int[] subkeys) {
		for(int i = 0; i < subkeys.length; i++) {
			if(subkeys[i] != NULL && subkeys[i] != IntIntIntCounterMap.END_OF_KEY) {
				return true;
			}
		}
		return false;
	}
	
	private int[] getSubkeys(int[] keys, IntList indices) throws Exception {
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
	
	
	public static void main(String[] args) throws Exception {
		CountReducer cr = new CountReducer(new File(Paths.outputDir("JhnCommon") + "/cocounts/counts"));
		cr.reduce();
	}
}
