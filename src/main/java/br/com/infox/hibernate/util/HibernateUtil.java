package br.com.infox.hibernate.util;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.dialect.Dialect;
import org.hibernate.ejb.EntityManagerFactoryImpl;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.internal.QueryImpl;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.proxy.HibernateProxy;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.core.util.ReflectionsUtil;
import br.com.infox.epp.cdi.config.BeanManager;

public final class HibernateUtil {

	private static final Logger LOGGER = Logger.getLogger(HibernateUtil.class.getName());

	private HibernateUtil() {
	}

	public static Session getSession() {
		return EntityManagerProducer.getEntityManager().unwrap(Session.class);
	}

	public static Object removeProxy(Object object) {
		if (object instanceof HibernateProxy) {
			return ((HibernateProxy) object).getHibernateLazyInitializer().getImplementation();
		}
		return object;
	}

	public static Dialect getDialect() {
		Class<? extends Dialect> dialectClass = getDialectClass();
		if (dialectClass == null)
			return null;
		try {
			return dialectClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			LOGGER.log(Level.SEVERE, "", e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static Class<? extends Dialect> getDialectClass() {
		EntityManager em = EntityManagerProducer.getEntityManager();
		EntityManagerFactory emf = em.getEntityManagerFactory();
		String dialectClassName = (String) emf.getProperties().get("hibernate.dialect");
		try {
			return (Class<? extends Dialect>) Class.forName(dialectClassName);
		} catch (ClassNotFoundException e) {
			LOGGER.log(Level.SEVERE, "", e);
		}
		return null;
	}

	public static void enableCache(Query query) {
		query.setHint("org.hibernate.cacheable", true);
	}

	public static String getQueryString(Query query) {
		QueryImpl queryImpl = query.unwrap(org.hibernate.internal.QueryImpl.class);
		return queryImpl.getQueryString();
	}

	public static Map<String, Object> getQueryParams(Query query) {
		QueryImpl queryImpl = unwrapQuery(query);
		Map<String, TypedValue> namedParameters = ReflectionsUtil.getValue(queryImpl, "namedParameters");
		Map<String, Object> parameters = new HashMap<>(namedParameters.size());
		for (String key : namedParameters.keySet()) {
			TypedValue typedValue = namedParameters.get(key);
			parameters.put(key, typedValue.getValue());
		}
		return parameters;
	}

	private static final QueryImpl unwrapQuery(Query query) {
		return query.unwrap(org.hibernate.internal.QueryImpl.class);
	}

	public static SessionFactoryImpl getSessionFactoryImpl() {
		EntityManager entityManager = BeanManager.INSTANCE.getReference(EntityManager.class);
		EntityManagerFactoryImpl entityManagerFactoryImpl = (EntityManagerFactoryImpl) entityManager
				.getEntityManagerFactory();
		return entityManagerFactoryImpl.getSessionFactory();
	}

	public static Class<?> getClass(Object entity) {
		// Não inicializa proxies, mas não funciona com herança
		// HibernateProxyHelper.getClassWithoutInitializingProxy(entity);
		// Inicializa o proxy, mas reconhece heranças corretamente
		return Hibernate.getClass(entity);
	}

}
