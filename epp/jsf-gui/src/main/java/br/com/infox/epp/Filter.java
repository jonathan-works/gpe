package br.com.infox.epp;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.metamodel.SingularAttribute;

public class Filter<T,V> {

	private static enum Operation {
		IS_NULL, IS_NOT_NULL, IN, EQUAL, NOT_EQUAL, IS_TRUE, IS_FALSE
	}

	private Filter(SingularAttribute<T,V> attribute){
		this(attribute, null, Operation.IS_NULL);
	}
	
	private Filter(SingularAttribute<T,V> attribute, V value){
		this(attribute, value, Operation.EQUAL);
	}
	private Filter(SingularAttribute<T,V> attribute, Operation operation, Collection<V> values){
		this.operation = operation;
		this.singularAttribute = attribute;
		this.values = values;
		this.value = null;
	}
	
	private Filter(SingularAttribute<T,V> attribute, V value, Operation operation){
		this.operation = operation;
		this.singularAttribute = attribute;
		this.value = value;
		this.values = null;
	}
	
	public Expression<Boolean> filter(Path<T> path, CriteriaBuilder cb){
		if (singularAttribute != null){
			switch (operation) {
			case EQUAL:
				return cb.equal(path.get(singularAttribute), value);
			case IS_NOT_NULL:
				return cb.isNotNull(path.get(singularAttribute));
			case IS_NULL:
				return cb.isNull(path.get(singularAttribute));
			case NOT_EQUAL:
				return cb.not(cb.equal(path.get(singularAttribute), value));
			case IS_TRUE:
				return cb.isTrue((Expression<Boolean>) path.get(singularAttribute));
			case IS_FALSE:
				return cb.isFalse((Expression<Boolean>) path.get(singularAttribute));
			case IN:
				return path.get(singularAttribute).in(values);
			default:
				break;	
			}
		}
		return cb.literal(Boolean.TRUE);
	}
	
	private final Operation operation;
	private final SingularAttribute<T, V> singularAttribute;
	private final V value;
	private final Collection<V> values;

	public static <X,Y> Filter<X,Y> equal(SingularAttribute<X,Y> attribute, Y value){
		return new Filter<X,Y>(attribute, value, Operation.EQUAL);
	}
	public static <X,Y> Filter<X, Y> in(SingularAttribute<X,Y> attribute, Y... value){
		Set<Y> set = new HashSet<>(Arrays.asList(value));
		return new Filter<X, Y>(attribute, Operation.IN, set);
	}
	public static <X,Y> Filter<X,Y> notEquals(SingularAttribute<X,Y> attribute, Y value){
		return new Filter<X,Y>(attribute, value, Operation.NOT_EQUAL);
	}
	public static <X> Filter<X,Boolean> isTrue(SingularAttribute<X,Boolean> attribute){
		return new Filter<X,Boolean>(attribute, null, Operation.IS_TRUE);
	}
	public static <X> Filter<X,Boolean> isFalse(SingularAttribute<X,Boolean> attribute){
		return new Filter<X,Boolean>(attribute, null, Operation.IS_FALSE);
	}
	public static <X,Y> Filter<X,Y> isNull(SingularAttribute<X,Y> attribute){
		return new Filter<X,Y>(attribute, null, Operation.IS_NULL);
	}
	public static <X,Y> Filter<X,Y> isNotNull(SingularAttribute<X,Y> attribute){
		return new Filter<X,Y>(attribute, null, Operation.IS_NOT_NULL);
	}
	public static <X,Y> Filter<X,Y> create(SingularAttribute<X,Y> attribute, Y value, Operation operation){
		return new Filter<X,Y>(attribute, value, operation);
	}
	
	
}
