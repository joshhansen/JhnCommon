package jhn.wp.exceptions;

/**
 * Exception type thrown whenever something prevents the words associated with a specific label from being counted.
 * @author Josh Hansen
 *
 */
public abstract class CountException extends Exception {
	private static final long serialVersionUID = 1L;

//	public CountException(String message) {
//		super(message);
//	}
	
	private final String label;
	public CountException(String message, String label) {
		super("[" + CountException.class.getName() + "] " + label + ": " + message);
		this.label = label;
	}
	
	public String label() {
		return label;
	}
}