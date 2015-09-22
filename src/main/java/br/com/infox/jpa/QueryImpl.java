package br.com.infox.jpa;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Parameter;
import javax.persistence.Query;
import javax.persistence.TemporalType;


public class QueryImpl implements Query, Serializable {

	private static final long serialVersionUID = 1L;
	private Query query;
	protected EntityManager entityManager;

	public QueryImpl(Query query, EntityManager entityManager) {
		this.query = query;
		this.entityManager = entityManager;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getResultList() {
		return query.getResultList();
	}

	@Override
	public Object getSingleResult() {
		return query.getSingleResult();
	}

	@Override
	public int executeUpdate() {
		entityManager.joinTransaction();
		return query.executeUpdate();
	}

	@Override
	public Query setMaxResults(int maxResult) {
		query.setMaxResults(maxResult);
		return this;
	}

	@Override
	public int getMaxResults() {
		return query.getMaxResults();
	}

	@Override
	public Query setFirstResult(int startPosition) {
		query.setFirstResult(startPosition);
		return this;
	}

	@Override
	public int getFirstResult() {
		return query.getFirstResult();
	}

	@Override
	public Query setHint(String hintName, Object value) {
		query.setHint(hintName, value);
		return this;
	}

	@Override
	public Map<String, Object> getHints() {
		return query.getHints();
	}

	@Override
	public <T> Query setParameter(Parameter<T> param, T value) {
		query.setParameter(param, value);
		return this;
	}

	@Override
	public Query setParameter(Parameter<Calendar> param, Calendar value, TemporalType temporalType) {
		query.setParameter(param, value, temporalType);
		return this;
	}

	@Override
	public Query setParameter(Parameter<Date> param, Date value, TemporalType temporalType) {
		query.setParameter(param, value, temporalType);
		return this;
	}

	@Override
	public Query setParameter(String name, Object value) {
		query.setParameter(name, value);
		return this;
	}

	@Override
	public Query setParameter(String name, Calendar value, TemporalType temporalType) {
		query.setParameter(name, value, temporalType);
		return this;
	}

	@Override
	public Query setParameter(String name, Date value, TemporalType temporalType) {
		query.setParameter(name, value);
		return this;
	}

	@Override
	public Query setParameter(int position, Object value) {
		query.setParameter(position, value);
		return this;
	}

	@Override
	public Query setParameter(int position, Calendar value, TemporalType temporalType) {
		query.setParameter(position, value, temporalType);
		return this;
	}

	@Override
	public Query setParameter(int position, Date value, TemporalType temporalType) {
		query.setParameter(position, value, temporalType);
		return this;
	}

	@Override
	public Set<Parameter<?>> getParameters() {
		return query.getParameters();
	}

	@Override
	public Parameter<?> getParameter(String name) {
		return query.getParameter(name);
	}

	@Override
	public <T> Parameter<T> getParameter(String name, Class<T> type) {
		return query.getParameter(name, type);
	}

	@Override
	public Parameter<?> getParameter(int position) {
		return query.getParameter(position);
	}

	@Override
	public <T> Parameter<T> getParameter(int position, Class<T> type) {
		return query.getParameter(position, type);
	}

	@Override
	public boolean isBound(Parameter<?> param) {
		return query.isBound(param);
	}

	@Override
	public <T> T getParameterValue(Parameter<T> param) {
		return query.getParameterValue(param);
	}

	@Override
	public Object getParameterValue(String name) {
		return query.getParameterValue(name);
	}

	@Override
	public Object getParameterValue(int position) {
		return query.getParameterValue(position);
	}

	@Override
	public Query setFlushMode(FlushModeType flushMode) {
		return query.setFlushMode(flushMode);
	}

	@Override
	public FlushModeType getFlushMode() {
		return query.getFlushMode();
	}

	@Override
	public Query setLockMode(LockModeType lockMode) {
		query.setLockMode(lockMode);
		return this;
	}

	@Override
	public LockModeType getLockMode() {
		return query.getLockMode();
	}

	@Override
	public <T> T unwrap(Class<T> cls) {
		return query.unwrap(cls);
	}

}
