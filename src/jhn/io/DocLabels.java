package jhn.io;

import java.util.Iterator;
import java.util.regex.Pattern;

public class DocLabels implements Iterator<String>, Iterable<String> {
	private static final Pattern delimRgx = Pattern.compile("[\",]+");
	
	private int docNum;
	private String docSource;
	private String[] labels;
	private int idx = 0;
	
	public DocLabels(String line) {
		String[] parts = delimRgx.split(line);
		docNum = Integer.parseInt(parts[0]);
		docSource = parts[1];
		labels = new String[parts.length - 2];
		for(int i = 2; i < parts.length; i++) {
			labels[i-2] = parts[i];
		}
	}
	
	public DocLabels(int docNum, String docSource, String[] labels) {
		super();
		this.docNum = docNum;
		this.docSource = docSource;
		this.labels = labels;
	}

	@Override
	public Iterator<String> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		return idx < labels.length;
	}

	@Override
	public String next() {
		return labels[idx++];
	}
	
	public int docNum() {
		return docNum;
	}
	
	public String docSource() {
		return docSource;
	}
	
	public String[] labels() {
		return labels;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
