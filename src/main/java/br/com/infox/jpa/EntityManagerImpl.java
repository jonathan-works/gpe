package br.com.infox.jpa;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.Metamodel;
import javax.transaction.Synchronization;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class EntityManagerImpl implements EntityManager, Serializable, Synchronization {

	private static final long serialVersionUID = 1L;
	private static final Pattern patternEl = Pattern.compile("#\\{.*?\\}");

	private EntityManager entityManager;

	public EntityManagerImpl(EntityManagerFactory entityManagerFactory) {
		this.entityManager = entityManagerFactory.createEntityManager();
		this.entityManager.setProperty("org.hibernate.flushMode", "MANUAL");
	}

	@Override
	public void persist(Object entity) {
		entityManager.persist(entity);
	}

	@Override
	public <T> T merge(T entity) {
		return entityManager.merge(entity);
	}

	@Override
	public void remove(Object entity) {
		entityManager.remove(entity);
	}

	@Override
	public <T> T find(Class<T> entityClass, Object primaryKey) {
		return entityManager.find(entityClass, primaryKey);
	}

	@Override
	public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {
		return entityManager.find(entityClass, primaryKey, properties);
	}

	@Override
	public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode) {
		entityManager.joinTransaction();
		return entityManager.find(entityClass, primaryKey, lockMode);
	}

	@Override
	public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode, Map<String, Object> properties) {
		entityManager.joinTransaction();
		return entityManager.find(entityClass, primaryKey, lockMode, properties);
	}

	@Override
	public <T> T getReference(Class<T> entityClass, Object primaryKey) {
		return entityManager.getReference(entityClass, primaryKey);
	}

	@Override
	public void flush() {
		entityManager.joinTransaction();
		entityManager.flush();
	}

	@Override
	public void setFlushMode(FlushModeType flushMode) {
		entityManager.setFlushMode(flushMode);
	}

	@Override
	public FlushModeType getFlushMode() {
		return entityManager.getFlushMode();
	}

	@Override
	public void lock(Object entity, LockModeType lockMode) {
		entityManager.joinTransaction();
		entityManager.lock(entity, lockMode);
	}

	@Override
	public void lock(Object entity, LockModeType lockMode, Map<String, Object> properties) {
		entityManager.joinTransaction();
		entityManager.lock(entity, lockMode, properties);
	}

	@Override
	public void refresh(Object entity) {
		entityManager.refresh(entity);
	}

	@Override
	public void refresh(Object entity, Map<String, Object> properties) {
		entityManager.refresh(entity, properties);
	}

	@Override
	public void refresh(Object entity, LockModeType lockMode) {
		entityManager.joinTransaction();
		entityManager.refresh(entity, lockMode);
	}

	@Override
	public void refresh(Object entity, LockModeType lockMode, Map<String, Object> properties) {
		entityManager.joinTransaction();
		entityManager.refresh(entity, lockMode, properties);
	}

	@Override
	public void clear() {
		entityManager.clear();
	}

	@Override
	public void detach(Object entity) {
		entityManager.detach(entity);
	}

	@Override
	public boolean contains(Object entity) {
		return entityManager.contains(entity);
	}

	@Override
	public LockModeType getLockMode(Object entity) {
		return entityManager.getLockMode(entity);
	}

	@Override
	public void setProperty(String propertyName, Object value) {
		entityManager.setProperty(propertyName, value);
	}

	@Override
	public Map<String, Object> getProperties() {
		return entityManager.getProperties();
	}

	@Override
	public Query createQuery(String qlString) {
		Pair<String, Map<String, Object>> resolvedElQlString = resolveEl(qlString);
		Query query = entityManager.createQuery(resolvedElQlString.getLeft());
		for (String key : resolvedElQlString.getRight().keySet()) {
			query.setParameter(key, resolvedElQlString.getRight().get(key));
		}
		return new QueryImpl(query, entityManager);
	}

	@Override
	public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
		TypedQuery<T> typedQuery = entityManager.createQuery(criteriaQuery);
		return new TypedQueryImpl<T>(typedQuery, entityManager);
	}

	@Override
	public <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass) {
		TypedQuery<T> typedQuery = entityManager.createQuery(qlString, resultClass);
		return new TypedQueryImpl<T>(typedQuery, entityManager);
	}

	@Override
	public Query createNamedQuery(String name) {
		Query query = entityManager.createNamedQuery(name);
		return new QueryImpl(query, entityManager);
	}

	@Override
	public <T> TypedQuery<T> createNamedQuery(String name, Class<T> resultClass) {
		TypedQuery<T> typedQuery = entityManager.createNamedQuery(name, resultClass);
		return new TypedQueryImpl<T>(typedQuery, entityManager);
	}

	@Override
	public Query createNativeQuery(String sqlString) {
		Query query = entityManager.createNativeQuery(sqlString);
		return new QueryImpl(query, entityManager);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Query createNativeQuery(String sqlString, Class resultClass) {
		Query query = entityManager.createNativeQuery(sqlString, resultClass);
		return new QueryImpl(query, entityManager);
	}

	@Override
	public Query createNativeQuery(String sqlString, String resultSetMapping) {
		Query query = entityManager.createNativeQuery(sqlString, resultSetMapping);
		return new QueryImpl(query, entityManager);
	}

	@Override
	public void joinTransaction() {
		entityManager.joinTransaction();
	}

	@Override
	public <T> T unwrap(Class<T> cls) {
		return entityManager.unwrap(cls);
	}

	@Override
	public Object getDelegate() {
		return entityManager.getDelegate();
	}

	@Override
	public void close() {
		entityManager.close();
	}

	@Override
	public boolean isOpen() {
		return entityManager.isOpen();
	}

	@Override
	public EntityTransaction getTransaction() {
		return entityManager.getTransaction();
	}

	@Override
	public EntityManagerFactory getEntityManagerFactory() {
		return entityManager.getEntityManagerFactory();
	}

	@Override
	public CriteriaBuilder getCriteriaBuilder() {
		return entityManager.getCriteriaBuilder();
	}

	@Override
	public Metamodel getMetamodel() {
		return entityManager.getMetamodel();
	}

	private Pair<String, Map<String, Object>> resolveEl(String qlString) {
		Map<String, Object> parameters = new HashMap<>();
		String newQlString = new String(qlString);
		FacesContext facesContext = FacesContext.getCurrentInstance();
		Matcher matcher = patternEl.matcher(qlString);
		while (matcher.find()) {
			String expression = matcher.group();
			String key = "parameterExpression_" + parameters.size();
			Object value = facesContext.getApplication().evaluateExpressionGet(facesContext, expression, Object.class);
			parameters.put(key, value);
			newQlString = newQlString.replace(expression, ":" + key);
		}
		return new ImmutablePair<String, Map<String, Object>>(newQlString, parameters);
	}

    @Override
    public void beforeCompletion() {
        flush();
    }

    @Override
    public void afterCompletion(int status) {
        if (isOpen()) {
            close();
        }
    }
}
