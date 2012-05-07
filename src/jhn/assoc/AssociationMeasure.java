package jhn.assoc;

public interface AssociationMeasure<T1,T2> {
	double association(T1 x, T2 y) throws Exception;
}
