package jhn.wp.visitors;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;

public class ChunkedWordSetVisitor extends Visitor {
	private static final String FILE_EXT = ".set";
	private ObjectSet<String> set;
	private final int chunkSize;
	private final File outputDir;
	
	public ChunkedWordSetVisitor(int chunkSize, String outputDir) {
		this.chunkSize = chunkSize;
		this.outputDir = new File(outputDir);
		this.set = newSet();
	}

	private static ObjectSet<String> newSet() {
		return new ObjectOpenHashSet<String>();
	}
	
	@Override
	public void visitWord(String word) {
		set.add(word);
		
		if(set.size() >= chunkSize) {
			writeSet();
			set = newSet();
			System.gc();
		}
	}

	private void writeSet() {
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(nextFilename()));
			String[] arr = set.toArray(new String[0]);
			Arrays.sort(arr);
			for(String s : arr) {
				w.write(s);
				w.newLine();
			}
			w.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void afterEverything() {
		writeSet();
	}
	
	
	
	private String nextFilename() {
		return outputDir.getPath() + "/" + nextInt() + FILE_EXT;
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
		for(File f : outputDir.listFiles(filter)) {
			value = Integer.parseInt(f.getName().split("\\.")[0]);
			if(value > max) {
				max = value;
			}
		}
		return max+1;
	}
}
