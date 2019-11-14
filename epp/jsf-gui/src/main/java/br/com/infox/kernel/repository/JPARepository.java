package br.com.infox.kernel.repository;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Id;

import org.apache.commons.beanutils.PropertyUtils;

import br.com.infox.log.Log;
import br.com.infox.log.Logging;

public abstract class JPARepository<E, K> implements Repository<E, K> {
	
	protected EntityManager entityManager;
	private static final Log LOG = Logging.getLog(JPARepository.class);

	@Override
	public E create(E entity) {
		try {
			entityManager.persist(entity);
			return entity;
		} catch (Exception e) {
			try {
				resetId(entity);
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
				LOG.warn("Erro ao resetar id da entidade de classe " + getEntityClass().getCanonicalName(), e1);
			}
			throw e;
		}
	}

	@Override
	public E update(E entity) {
		return entityManager.merge(entity);
	}

	@Override
	public E delete(E entity) {
		entityManager.remove(entity);
		return entity;
	}

	@Override
	public E getById(K id) {
		return entityManager.find(getEntityClass(), id);
	}
	
	@Inject
	protected void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	private void resetId(E entity) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Class<? super E> entityClass = getEntityClass();
		while (entityClass != null) {
			for (Field f : entityClass.getFields()) {
				if (f.getAnnotation(Id.class) != null) {
					PropertyUtils.setProperty(entity, f.getName(), null);
					return;
				}
			}
			entityClass = entityClass.getSuperclass();
		}
	}

	protected abstract Class<E> getEntityClass();
}
