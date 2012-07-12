package jhn.wp.visitors;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import jhn.idx.RAMIndex;
import jhn.util.Util;

public class WordIndexingVisitor extends Visitor {
	private final String outputFilename;
	private final RAMIndex idx = new RAMIndex();
	
	public WordIndexingVisitor(String outputFilename) {
		this.outputFilename = outputFilename;
	}

	@Override
	public void visitWord(String word) {
		idx.indexOf(word);
	}

	@Override
	public void afterEverything() {
		Util.serialize(idx, outputFilename+"_full");
		
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(outputFilename+"_reverse"));
			idx.writeReverseIndex(out);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(outputFilename+"_fwd"));
			idx.writeForwardIndex(out);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
