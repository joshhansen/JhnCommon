package jhn.idx;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import jhn.Paths;

/** The backing file must be sorted (ascending) */
public class DiskStringIndex implements Index<String> {
	private String[] arr;
	public DiskStringIndex(String filename) throws IOException {
		try(BufferedReader r = new BufferedReader(new FileReader(filename))) {
			ObjectArrayList<String> list = new ObjectArrayList<>();
			String line = null;
			while( (line=r.readLine()) != null) {
				list.add(line);
			}
			
			System.out.println("Loaded");
			arr = list.toArray(new String[0]);
			System.out.println("Array'd");
		}
	}

	@Override
	public int indexOf(String value) {
		int left = 0;
		int right = arr.length;
		int mid;
		int cmp;
		while(left <= right) {
			mid = (left + right) / 2;
			
			cmp = arr[mid].compareTo(value);
			if(cmp < 0) {
				left = mid + 1;
			} else if(cmp > 0) {
				right = mid - 1;
			} else {
				return mid;
			}
		}
		return KEY_NOT_FOUND;
	}


	@Override
	public int indexOf(String value, boolean addIfNotPresent) {
		if(addIfNotPresent) {
			throw new UnsupportedOperationException("Can't add to this index");
		}
		return indexOf(value);
	}


	@Override
	public boolean contains(String value) {
		return indexOf(value) != KEY_NOT_FOUND;
	}
	
	public List<String> values() {
		return Arrays.asList(arr);
	}

	public static void main(String[] args) throws IOException {
		String filename = Paths.outputDir("JhnCommon")+"/word_sets/chunks/19.set";
		DiskStringIndex idx = new DiskStringIndex(filename);
		System.out.print("dog: ");
		System.out.println(idx.indexOf("dog"));
		System.out.print("music: ");
		System.out.println(idx.indexOf("music"));
		System.out.print("zebra: ");
		System.out.println(idx.indexOf("zebra"));
		System.out.print("^^^^^^: ");
		System.out.println(idx.indexOf("^^^^^^"));
	}

	@Override
	public int size() {
		return arr.length;
	}

	@Override
	public String objectAt(int idx) {
		return arr[idx];
	}
}
