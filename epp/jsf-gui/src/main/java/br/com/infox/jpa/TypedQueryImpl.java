package br.com.infox.jpa;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Parameter;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

public class TypedQueryImpl<X> extends QueryImpl implements TypedQuery<X>, Serializable {

	private static final long serialVersionUID = 1L;

	private TypedQuery<X> typedQuery;

	public TypedQueryImpl(TypedQuery<X> typedQuery, EntityManager entityManager) {
		super(typedQuery, entityManager);
		this.typedQuery = typedQuery;
	}

	@Override
	public List<X> getResultList() {
		return typedQuery.getResultList();
	}

	@Override
	public X getSingleResult() {
		return typedQuery.getSingleResult();
	}

	@Override
	public TypedQuery<X> setMaxResults(int maxResult) {
		typedQuery.setMaxResults(maxResult);
		return this;
	}

	@Override
	public TypedQuery<X> setFirstResult(int startPosition) {
		typedQuery.setFirstResult(startPosition);
		return this;
	}

	@Override
	public TypedQuery<X> setHint(String hintName, Object value) {
		typedQuery.setHint(hintName, value);
		return this;
	}

	@Override
	public <T> TypedQuery<X> setParameter(Parameter<T> param, T value) {
		typedQuery.setParameter(param, value);
		return this;
	}

	@Override
	public TypedQuery<X> setParameter(Parameter<Calendar> param, Calendar value, TemporalType temporalType) {
		typedQuery.setParameter(param, value, temporalType);
		return this;
	}

	@Override
	public TypedQuery<X> setParameter(Parameter<Date> param, Date value, TemporalType temporalType) {
		typedQuery.setParameter(param, value, temporalType);
		return this;
	}

	@Override
	public TypedQuery<X> setParameter(String name, Object value) {
		typedQuery.setParameter(name, value);
		return this;
	}

	@Override
	public TypedQuery<X> setParameter(String name, Calendar value, TemporalType temporalType) {
		typedQuery.setParameter(name, value, temporalType);
		return this;
	}

	@Override
	public TypedQuery<X> setParameter(String name, Date value, TemporalType temporalType) {
		typedQuery.setParameter(name, value, temporalType);
		return this;
	}

	@Override
	public TypedQuery<X> setParameter(int position, Object value) {
		typedQuery.setParameter(position, value);
		return this;
	}

	@Override
	public TypedQuery<X> setParameter(int position, Calendar value, TemporalType temporalType) {
		typedQuery.setParameter(position, value, temporalType);
		return this;
	}

	@Override
	public TypedQuery<X> setParameter(int position, Date value, TemporalType temporalType) {
		typedQuery.setParameter(position, value, temporalType);
		return this;
	}

	@Override
	public TypedQuery<X> setFlushMode(FlushModeType flushMode) {
		typedQuery.setFlushMode(flushMode);
		return this;
	}

	@Override
	public TypedQuery<X> setLockMode(LockModeType lockMode) {
		typedQuery.setLockMode(lockMode);
		return this;
	}

}
