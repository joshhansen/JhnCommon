package jhn.wp.exceptions;

public class BadLabel extends CountException {
	private static final long serialVersionUID = 1L;

	public BadLabel(String label) {
		this("Bad label", label);
	}

	public BadLabel(String message, String label) {
		this(message, label, "l");
	}
	
	protected BadLabel(String message, String label, String shortCode) {
		super(message, label, shortCode);
	}
}
