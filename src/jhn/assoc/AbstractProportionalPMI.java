package jhn.assoc;

public abstract class AbstractProportionalPMI<T1,T2> implements AssociationMeasure<T1,T2> {

	@Override
	public double association(T1 x, T2 y) throws Exception {
		return   Math.log(jointCount(x, y))
        - Math.log(count1(x))
        - Math.log(count2(y));
	}

	protected abstract int count1(T1 x) throws Exception;
	
	protected abstract int count2(T2 y) throws Exception;

	protected abstract int jointCount(T1 x, T2 y) throws Exception;

}
