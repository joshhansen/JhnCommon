package jhn.wp.exceptions;


public abstract class BadWikiTextException extends CountException {
	public BadWikiTextException(String message, String label) {
		super(message, label);
	}
}