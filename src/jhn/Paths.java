package jhn;

import java.io.File;

public final class Paths {
	private Paths() {
		// Do not allow instantiation
	}
	
	// File extensions
	public static final String STATE_EXT = ".state.gz";
	public static final String KEYS_EXT = ".keys";
	public static final String TOPIC_LABELS_EXT = ".topic_labels";
	public static final String DOC_LABELS_EXT = ".doc_labels";
	public static final String DOCTOPICS_EXT = ".doctopics";
	public static final String MALLET_DATA_EXT = ".mallet";
	public static final String FILENAME_IDX_EXT = ".filename_idx";
	
	// Path hierarchy
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
	
		public static String indexDir(String indexName) {
			return indicesDir() + "/" + indexName;
		}
		
			public static String topicWordIndicesDir() {
				return indexDir("topic_words");
			}
		
			public static String topicWordIndexDir(String name) {
				return topicWordIndicesDir() + "/" + name;
			}
	
	public static String malletOutputDir() {
		return outputDir("Mallet");
	}
	
	private static String ldaResultsDir(String datasetName) {
		return malletOutputDir() + "/results/" + datasetName;
	}
	
	public static String ldaStateFilename(String datasetName, int run) {
		return ldaResultsDir(datasetName) + "/" + run + STATE_EXT;
	}
	
	public static String ldaKeysFilename(String datasetName, int run) {
		return ldaResultsDir(datasetName) + "/" + run + KEYS_EXT;
	}
	
	public static String ldaDocTopicsFilename(String datasetName, int run) {
		return ldaResultsDir(datasetName) + "/" + run + DOCTOPICS_EXT;
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
	
	public static String wikipediaWordCountsDbFilename() {
		return jhncOutputDir() + "/counts/counts.sqlite3";
	}
	
	public static String wikipediaWordCocountsDbFilename() {
		return jhncOutputDir() + "/cocounts/cocounts.sqlite3";
	}
	
	public static String wikipediaWordIndexFilename() {
		return jhncOutputDir() + "/word_sets/chunks/19.set";
	}
	
	public static String malletDatasetsDir() {
		return malletOutputDir() + "/datasets";
	}
	
	public static String malletDatasetFilename(String datasetName) {
		return malletDatasetsDir() + "/" + datasetName + MALLET_DATA_EXT;
	}
	
	public static String malletDatasetFilenameIndexFilename(String datasetName) {
		return malletDatasetsDir() + "/" + datasetName + FILENAME_IDX_EXT;
	}
}
