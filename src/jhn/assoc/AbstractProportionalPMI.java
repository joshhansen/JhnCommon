package jhn.assoc;

public abstract class AbstractProportionalPMI<T1,T2> implements AssociationMeasure<T1,T2> {

	@Override
	public double association(T1 x, T2... Ys) throws Exception {
		if(Ys.length > 1) throw new IllegalArgumentException();
		
		return   Math.log(jointCount(x, Ys[0]))
        - Math.log(count1(x))
        - Math.log(count2(Ys[0]));
	}

	protected abstract int count1(T1 x) throws Exception;
	
	protected abstract int count2(T2 y) throws Exception;

	protected abstract int jointCount(T1 x, T2 y) throws Exception;

}
