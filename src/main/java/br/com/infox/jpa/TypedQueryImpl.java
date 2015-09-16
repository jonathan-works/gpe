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
		return typedQuery.setMaxResults(maxResult);
	}

	@Override
	public TypedQuery<X> setFirstResult(int startPosition) {
		return typedQuery.setFirstResult(startPosition);
	}

	@Override
	public TypedQuery<X> setHint(String hintName, Object value) {
		return typedQuery.setHint(hintName, value);
	}

	@Override
	public <T> TypedQuery<X> setParameter(Parameter<T> param, T value) {
		return typedQuery.setParameter(param, value);
	}

	@Override
	public TypedQuery<X> setParameter(Parameter<Calendar> param, Calendar value, TemporalType temporalType) {
		return typedQuery.setParameter(param, value, temporalType);
	}

	@Override
	public TypedQuery<X> setParameter(Parameter<Date> param, Date value, TemporalType temporalType) {
		return typedQuery.setParameter(param, value, temporalType);
	}

	@Override
	public TypedQuery<X> setParameter(String name, Object value) {
		return typedQuery.setParameter(name, value);
	}

	@Override
	public TypedQuery<X> setParameter(String name, Calendar value, TemporalType temporalType) {
		return typedQuery.setParameter(name, value, temporalType);
	}

	@Override
	public TypedQuery<X> setParameter(String name, Date value, TemporalType temporalType) {
		return typedQuery.setParameter(name, value, temporalType);
	}

	@Override
	public TypedQuery<X> setParameter(int position, Object value) {
		return typedQuery.setParameter(position, value);
	}

	@Override
	public TypedQuery<X> setParameter(int position, Calendar value, TemporalType temporalType) {
		return typedQuery.setParameter(position, value, temporalType);
	}

	@Override
	public TypedQuery<X> setParameter(int position, Date value, TemporalType temporalType) {
		return typedQuery.setParameter(position, value, temporalType);
	}

	@Override
	public TypedQuery<X> setFlushMode(FlushModeType flushMode) {
		return typedQuery.setFlushMode(flushMode);
	}

	@Override
	public TypedQuery<X> setLockMode(LockModeType lockMode) {
		return typedQuery.setLockMode(lockMode);
	}

}
