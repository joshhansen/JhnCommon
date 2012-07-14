package jhn.wp.wordidx;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;

import jhn.wp.visitors.Visitor;

public class ChunkedWordSetVisitor extends Visitor {
	private static final String FILE_EXT = ".set";
	
	private ObjectSet<String> baseSet;
	private ObjectSet<String> set;
	private final int baseChunkSize;
	private final int chunkSize;
	private final File outputDir;
	
	public ChunkedWordSetVisitor(int baseChunkSize, int chunkSize, String outputDir) {
		this.baseChunkSize = baseChunkSize;
		this.chunkSize = chunkSize;
		this.outputDir = new File(outputDir);
		this.baseSet = newSet();
		this.set = newSet();
	}

	private static ObjectSet<String> newSet() {
		return new ObjectOpenHashSet<>();
	}
	
	@Override
	public void visitWord(String word) {
		final int basePreSize = baseSet.size();
		if(basePreSize < baseChunkSize) {
			baseSet.add(word);
			final int basePostSize = baseSet.size();
			
			if(basePostSize > basePreSize) {
				if(basePostSize % 10000 == 0) {
					System.out.print('b');
					System.out.println(basePostSize);
				}
				
				if(basePostSize >= baseChunkSize) {
					writeSet(baseSet, nextFilename());
				}
			}
		} else {
			if(!baseSet.contains(word)) {
				final int preSize = set.size();
				set.add(word);
				final int postSize = set.size();
				
				if(postSize > preSize) {
					if(postSize % 10000 == 0) {
						System.out.println(postSize);
					}
					
					if(postSize >= chunkSize) {
						writeSet();
						set = newSet();
						System.gc();
					}
				}
			}
		}
	}
	
	private void writeSet() {
		writeSet(set, nextFilename());
	}
	
	private static void writeSet(ObjectSet<String> set, String filename) {
		try(BufferedWriter w = new BufferedWriter(new FileWriter(filename))) {
			String[] arr = set.toArray(new String[0]);
			Arrays.sort(arr);
			for(String s : arr) {
				w.write(s);
				w.newLine();
			}
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
