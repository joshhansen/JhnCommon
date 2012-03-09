package jhn.wp.exceptions;


public class ArticleTooShort extends CountException {
	private static final long serialVersionUID = 1L;
	
	private final int length;
	public ArticleTooShort(String label, int length) {
		super("Too short (length " + length + ")", label);
		this.length = length;
	}
	
	public int length() { return length; }
}