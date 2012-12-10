package jhn.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

public class TopicLabelFileReader implements AutoCloseable, Iterator<TopicLabel>, Iterable<TopicLabel> {
	private String nextLine;
	private BufferedReader r;
	public TopicLabelFileReader(String filename) throws IOException {
		r = new BufferedReader(new FileReader(filename));
		getNextNonCommentLine();
	}
	
	private void getNextNonCommentLine() throws IOException {
		do {
			nextLine = r.readLine();
		} while(nextLine != null && nextLine.startsWith("#"));
	}
	
	@Override
	public void close() throws IOException {
		r.close();
	}

	@Override
	public Iterator<TopicLabel> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		return nextLine != null;
	}

	@Override
	public TopicLabel next() {
		TopicLabel tl = new TopicLabel(nextLine);
		try {
			getNextNonCommentLine();
		} catch(IOException e) {
			nextLine = null;
		}
		return tl;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
