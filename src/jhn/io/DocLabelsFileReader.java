package jhn.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

public class DocLabelsFileReader implements Iterator<DocLabels>, Iterable<DocLabels>, AutoCloseable {
	private String nextLine;
	private BufferedReader r;
	public DocLabelsFileReader(String filename) throws IOException {
		r = new BufferedReader(new FileReader(filename));
		getNextNonCommentLine();
	}
	
	@Override
	public Iterator<DocLabels> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		return nextLine != null;
	}

	@Override
	public DocLabels next() {
		DocLabels dl = new DocLabels(nextLine);
		try {
			getNextNonCommentLine();
		} catch (IOException e) {
			e.printStackTrace();
			nextLine = null;
		}
		return dl;
	}
	
	private void getNextNonCommentLine() throws IOException {
		do {
			nextLine = r.readLine();
		} while(nextLine != null && nextLine.startsWith("#"));
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void close() throws IOException {
		r.close();
	}
	
}
