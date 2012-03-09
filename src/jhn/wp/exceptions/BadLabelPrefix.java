package jhn.wp.exceptions;


public class BadLabelPrefix extends CountException {
	public BadLabelPrefix(String label) {
		super("Bad label prefix", label);
	}
}