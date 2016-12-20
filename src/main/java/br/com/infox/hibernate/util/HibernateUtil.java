package br.com.infox.hibernate.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.ejb.EntityManagerFactoryImpl;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.internal.QueryImpl;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.proxy.HibernateProxy;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.core.util.ReflectionsUtil;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.hibernate.oracle.dialect.InfoxOracleDialect;

public final class HibernateUtil {

	private HibernateUtil() {
	}

	public static Session getSession() {
		return EntityManagerProducer.getEntityManager().unwrap(Session.class);
	}

        @SuppressWarnings("unchecked")
	public static <T> T removeProxy(T object) {
		if (object instanceof HibernateProxy) {
			return (T)((HibernateProxy) object).getHibernateLazyInitializer().getImplementation();
		}
		return object;
	}

	public static Dialect getDialect() {
		EntityManager em = EntityManagerProducer.getEntityManager();
		return getDialect(em);
	}
	
	public static Dialect getDialect(EntityManager entityManager) {
		Properties properties = new Properties();
		properties.putAll(entityManager.getEntityManagerFactory().getProperties());
    	return Dialect.getDialect(properties);
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
	
	public static Query createCallFunctionQuery(String functionName, List<Object> arguments, EntityManager entityManager) {
		Map<String, Object> namedParameters =  new HashMap<>();
		List<String> stringNamedParameters = new ArrayList<>();
		for (int index = 0 ; index < arguments.size() ; index++) {
			String argName = "arg" + index;
			stringNamedParameters.add(":" + argName);
			namedParameters.put(argName, arguments.get(index)); 
		}
    	Dialect dialect = getDialect(entityManager);
    	SQLFunction sqlFunction = dialect.getFunctions().get(functionName.toLowerCase());
    	String nativeQuery = "select " + sqlFunction.render(null, stringNamedParameters, HibernateUtil.getSessionFactoryImpl());
		if (dialect instanceof InfoxOracleDialect) {
			nativeQuery += " from dual";
		}
		Query query = entityManager.createNativeQuery(nativeQuery);
		for (Map.Entry<String, Object> namedParam : namedParameters.entrySet()) {
			query.setParameter(namedParam.getKey(), namedParam.getValue());
		}
		return query;
	}

}
