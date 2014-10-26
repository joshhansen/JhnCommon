package jhn;

public class ExtractorParams {
	public String topicWordIdxName;
	public String datasetName;
	public int minCount;
	
	public ExtractorParams() {
		// Do nothing
	}
	
	public ExtractorParams(String topicWordIdxName) {
		this.topicWordIdxName = topicWordIdxName;
	}
	
	public ExtractorParams(String topicWordIdxName, String datasetName, int minCount) {
		this.topicWordIdxName = topicWordIdxName;
		this.datasetName = datasetName;
		this.minCount = minCount;
	}
	
	@SuppressWarnings("hiding")
	public ExtractorParams topicWordIdxName(String topicWordIdxName) {
		this.topicWordIdxName = topicWordIdxName;
		return this;
	}
	
	@SuppressWarnings("hiding")
	public ExtractorParams datasetName(String datasetName) {
		this.datasetName = datasetName;
		return this;
	}
	
	@SuppressWarnings("hiding")
	public ExtractorParams minCount(int minCount) {
		this.minCount = minCount;
		return this;
	}
}