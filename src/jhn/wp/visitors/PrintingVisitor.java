package jhn.wp.visitors;


public class PrintingVisitor extends Visitor {
	private int count = 0;

	@Override
	public void visitLabel(String label) {
		count++;
		if(count % 10000 == 0) System.out.println(count + " " + label);
	}
}
