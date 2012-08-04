package jhn;

import java.io.File;

public class Paths {
	public static String homeDir() {
		return System.getenv("HOME");
	}
	
	public static String outputDir() {
		return homeDir() + "/Projects/Output";
	}
	
	public static String outputDir(String projectName) {
		return outputDir() + "/" + projectName;
	}
	
	public static String jhncOutputDir() {
		return outputDir("JhnCommon");
	}
	
	public static String indicesDir() {
		return jhncOutputDir() + "/indices";
	}
	
	public static String titleIndexDir() {
		return indicesDir() + "/titles/" + wikipediaDumpName();
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
	
	public static String dataDir() {
		return homeDir() + "/Data";
	}
	
	public static String dataDir(String datasetName) {
		return dataDir() + "/" + datasetName;
	}
	
	public static String wikipediaDumpName() {
		return "enwiki-20120403";
	}
	
	public static String wikipediaArticlesFilename() {
		return dataDir("wikipedia.org") + "/" + wikipediaDumpName() + "-pages-articles.xml.bz2";
	}
}
