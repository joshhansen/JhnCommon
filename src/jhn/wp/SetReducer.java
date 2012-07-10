package jhn.wp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import jhn.Paths;

/** Merges any number of sorted lists of strings, one list per file, reducing duplicate values to a single instance
 * thereof. */
public class SetReducer {
	private static final String WRITTEN = "-12489$*#()1-421840-12423%$%$#%^#^gjsfFJklfjw;w3%$Jk3l41234jk";
	private static final String EMPTY = "-12489$*#()1-421840-12423%$%$#%^#^gjsfFJklfjw;w3%$Jk3l41234jk 2";
	
	private static final String FILE_EXT = ".set";
	private final File dir;
	
	private BufferedWriter out;
	private BufferedReader[] in;
	public SetReducer(File dir) {
		this.dir = dir;
	}
	
	private boolean someStreamNotEmpty(String[] keys) throws Exception {
		for(String key : keys) {
			if(!key.equals(EMPTY)) {
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
	
	private static final int REDUCE_AT_A_TIME = 300;
	public void reduce() throws Exception {
		while(true) {
			File[] files = dir.listFiles(filter);
			if(files.length <= 1) {
				break;
			}
			File[] subset = Arrays.copyOf(files, Math.min(files.length, REDUCE_AT_A_TIME));
			reduce(nextFilename(), subset);
			
			for(File f : subset) {
				f.delete();
			}
		}
	}
	
	private void reduce(String destFilename, File... srcFiles) throws Exception {
		System.out.println(destFilename);
		for(File f : srcFiles) {
			System.out.println("\t" + f.getName());
		}
		
		out = new BufferedWriter(new FileWriter(destFilename));
		in = new BufferedReader[srcFiles.length];
		for(int i = 0; i < srcFiles.length; i++) {
			in[i] = new BufferedReader(new FileReader(srcFiles[i]));
		}
		
		String[] keys = null;
		do {
			keys = updateKeys(keys);
			if(!someStreamNotEmpty(keys)) {
				break;
			}
			
			processMinKeys(keys);
		} while(true);
		
		// Clean up
		out.close();
		for(BufferedReader r : in) {
			r.close();
		}
	}
	
	private String[] updateKeys(String[] keys) throws Exception {
		if(keys == null) {
			keys = new String[in.length];
			Arrays.fill(keys, WRITTEN);
		}
		for(int i = 0; i < keys.length; i++) {
			if(keys[i]==WRITTEN) {
				keys[i] = in[i].readLine();
				if(keys[i]==null) {
					keys[i] = EMPTY;
				}
			}
		}
		return keys;
	}
	
	private void processMinKeys(String[] keys) throws Exception {
		String minKey = minKey(keys);
		IntList indices = indicesMatching(keys, minKey);
		out.write(minKey);
		out.newLine();
		for(int idx : indices) {
			keys[idx] = WRITTEN;
		}
	}
	
	private static String minKey(String... keys) {
		String[] sortedKeys = Arrays.copyOf(keys, keys.length);
		Arrays.sort(sortedKeys);
		for(String key : sortedKeys) {
			if(!key.equals(EMPTY)) {
				return key;
			}
		}
		throw new IllegalArgumentException("None of the streams are open");
	}
	
	private static <T> IntList indicesMatching(T[] values, T targetValue) {
		IntList inputsWithMinKey = new IntArrayList();
		for(int i = 0; i < values.length; i++) {
			if(values[i].equals(targetValue)) {
				inputsWithMinKey.add(i);
			}
		}
		return inputsWithMinKey;
	}
	
	
	
	public static void main(String[] args) throws Exception {
		SetReducer sr = new SetReducer(new File(Paths.outputDir("JhnCommon") + "/word_sets/chunks"));
		sr.reduce();
	}
}
