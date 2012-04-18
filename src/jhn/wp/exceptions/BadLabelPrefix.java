package jhn.wp.exceptions;


public class BadLabelPrefix extends BadLabel {
	private static final long serialVersionUID = 1L;

	public BadLabelPrefix(String label) {
		super("Bad label prefix", label, "p");
	}
}
