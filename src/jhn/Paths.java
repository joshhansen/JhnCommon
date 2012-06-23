package jhn;

import java.io.File;

public class Paths {
	public static String outputDir() {
		return System.getenv("HOME") + "/Projects/Output";
	}
	
	public static String outputDir(String projectName) {
		return outputDir() + "/" + projectName;
	}
	
	private static String ldaResultsDir(String datasetName) {
		return outputDir("LDA") + "/results/" + datasetName;
	}
	
	public static String ldaStateFilename(String datasetName, int run) {
		return ldaResultsDir(datasetName) + "/" + run + ".state.gz";
	}
	
	public static String ldaKeysFilename(String datasetName, int run) {
		return ldaResultsDir(datasetName) + "/" + run + ".keys";
	}
	
	public static String ldaDocTopicsFilename(String datasetName, int run) {
		return ldaResultsDir(datasetName) + "/" + run + ".doctopics";
	}
	
	public static int nextRun(String runsDir) {
		int max = -1;
		for(File f : new File(runsDir).listFiles()) {
			if(f.isDirectory()) {
				int value = Integer.parseInt(f.getName());
				if(value > max) {
					max = value;
				}
			}
		}
		return max + 1;
	}
}
