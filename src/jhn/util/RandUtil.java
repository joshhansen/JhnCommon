package jhn.util;

import java.util.List;
import java.util.Set;

import it.unimi.dsi.fastutil.ints.IntSet;

public class RandUtil {
	public static final java.util.Random rand = new java.util.Random();
	public static <T> T randItem(T[] arr) {
		return arr[rand.nextInt(arr.length)];
	}
	
	public static int randItem(int[] arr) {
		return arr[rand.nextInt(arr.length)];
	}
	
	public static <T> T randItem(List<T> list) {
		return list.get(rand.nextInt(list.size()));
	}
	
	public static int randItem(IntSet set) {
		return randItem(set.toIntArray());
	}
	
	public static <T> T randItem(T[] arr, int n) {
		int cmp = Util.compareInts(n, 1);
		switch(cmp) {
		case -1:
			// n < 1
			throw new IllegalArgumentException("Top N must be greater than zero");
		case 0:
			// n==1 so return the first item without randomness
			return arr[0];
		case 1:	default:
			// n > 1 so return a random item
			return arr[rand.nextInt(Math.min(arr.length, n))];
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T randItem(Set<T> set) {
		return (T) randItem(set.toArray());
	}
}
