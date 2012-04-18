package jhn.wp.exceptions;

public class DisambiguationPage extends BadLabel {
	private static final long serialVersionUID = 1L;

	
	public DisambiguationPage(String label) {
		this("Disambiguation page", label);
	}
	
	public DisambiguationPage(String message, String label) {
		super(message, label, "d");
	}
}
