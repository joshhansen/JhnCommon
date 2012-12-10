package jhn.io;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TopicLabel {
	private static final Pattern rgx = Pattern.compile("([^,]+),(.+),\"([^\"]+)\"");
	
	private int topicNum;
	private String label;
	private String[] words;
	
	public TopicLabel(String line) {
		Matcher m = rgx.matcher(line);
		m.matches();
		topicNum = Integer.parseInt(m.group(1));
		words = m.group(2).split(",");
		label = m.group(3);
	}
	
	public int topicNum() {
		return topicNum;
	}
	
	public String label() {
		return label;
	}
	
	public String[] words() {
		return words;
	}
}
