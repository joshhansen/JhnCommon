package jhn.wp.exceptions;

/**
 * Exception type thrown whenever something prevents the words associated with a specific label from being counted.
 * @author Josh Hansen
 *
 */
public abstract class CountException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private final String label;
	private final String shortCode;
	public CountException(String message, String label, String shortCode) {
		super("[" + CountException.class.getName() + "] " + label + ": " + message);
		this.label = label;
		this.shortCode = shortCode;
	}
	
	public String label() {
		return label;
	}
	
	public String shortCode() {
		return shortCode;
	}
}
