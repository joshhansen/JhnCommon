package jhn.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;


public class TopNTracker<T extends Comparable<T>> {
	private final Queue<T> topNItems = new PriorityQueue<>();
	private final int n;
	public TopNTracker(final int n) {
		this.n = n;
	}
	
	public void add(T value) {
		topNItems.add(value);
		if(topNItems.size() > n) topNItems.remove();
	}
	
	public List<T> topN() {
		List<T> topN = new ArrayList<>(n);
		while(!topNItems.isEmpty()) {
			topN.add(topNItems.remove());
		}
		Collections.reverse(topN);
		return topN;
	}
}
