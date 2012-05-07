package jhn.wp.visitors;

import jhn.wp.exceptions.CountException;


public class LabelAwareVisitor extends Visitor {
	protected String currentLabel;
	
	@Override
	public void visitLabel(String label) throws CountException {
		super.visitLabel(label);
		currentLabel = label;
	}
}
