package jhn.wp.visitors;

import jhn.util.Util;

public abstract class SerializingVisitor<T> extends Visitor {
	protected T index;
	private final String outputFilename;
	
	public SerializingVisitor(String outputFilename) {
		this.outputFilename = outputFilename;
	}

	@Override
	public void afterEverything() {
		Util.serialize(index, outputFilename);
	}
}