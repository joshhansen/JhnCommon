package jhn.wp.exceptions;

public class BadLabel extends CountException {
	private static final long serialVersionUID = 1L;

	public BadLabel(String label) {
		super("Bad label", label);
	}

	public BadLabel(String message, String label) {
		super(message, label);
	}
	
	

}
