package jhn.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;


public class TopNTracker {
	private final Queue<Double> topNItems = new PriorityQueue<>();
	private final int n;
	public TopNTracker(final int n) {
		this.n = n;
	}
	
	public void add(double value) {
		topNItems.add(value);
		if(topNItems.size() > n) topNItems.remove();
	}
	
	public List<Double> topN() {
		List<Double> topN = new ArrayList<>(n);
		while(!topNItems.isEmpty()) {
			topN.add(topNItems.remove());
		}
		Collections.reverse(topN);
		return topN;
	}
}
