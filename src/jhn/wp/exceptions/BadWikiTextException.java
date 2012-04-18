package jhn.wp.exceptions;


public abstract class BadWikiTextException extends CountException {
	private static final long serialVersionUID = 1L;

	public BadWikiTextException(String message, String label) {
		this(message, label, "t");
	}
	
	protected BadWikiTextException(String message, String label, String shortCode) {
		super(message, label, shortCode);
	}
}
