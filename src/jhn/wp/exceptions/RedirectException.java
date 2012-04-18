package jhn.wp.exceptions;


public class RedirectException extends BadWikiTextException {
	private static final long serialVersionUID = 1L;

	public RedirectException(String label) {
		super("Redirect", label, "r");
	}
}
