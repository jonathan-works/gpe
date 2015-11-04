package br.com.infox.hibernate.util;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.hibernate.Filter;
import org.hibernate.dialect.Dialect;
import org.hibernate.ejb.EntityManagerFactoryImpl;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.internal.QueryImpl;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.proxy.HibernateProxy;
import org.jboss.seam.Component;

import br.com.infox.core.util.ReflectionsUtil;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.hibernate.session.SessionAssistant;

public final class HibernateUtil {

    private HibernateUtil() {
    }

    public static void setFilterParameter(String filterName,
            String parameterName, Object paremeterValue) {
        Filter enabledFilter = getEnabledFilter(filterName);
        enabledFilter.setParameter(parameterName, paremeterValue);
    }

    public static Filter getEnabledFilter(String filterName) {
        Filter enabledFilter = sessionAssistant().getSession().getEnabledFilter(filterName);
        if (enabledFilter == null) {
            sessionAssistant().getSession().enableFilter(filterName);
            enabledFilter = sessionAssistant().getSession().getEnabledFilter(filterName);
        }
        return enabledFilter;
    }

    public static Object removeProxy(Object object) {
        if (object instanceof HibernateProxy) {
            return ((HibernateProxy) object).getHibernateLazyInitializer().getImplementation();
        }
        return object;
    }

    private static SessionAssistant sessionAssistant() {
        return (SessionAssistant) Component.getInstance(SessionAssistant.NAME);
    }
    
    @SuppressWarnings("unchecked")
	public static Dialect getDialect() {
    	EntityManager em = BeanManager.INSTANCE.getReference(EntityManager.class);
    	EntityManagerFactory emf = em.getEntityManagerFactory();
    	String dialectClassName = (String) emf.getProperties().get("hibernate.dialect");
    	Dialect dialect = null;
    	try {
			Class<? extends Dialect> clazz = (Class<? extends Dialect>) Class.forName(dialectClassName);
			dialect = clazz.newInstance();
    	} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
    	return dialect;
    }
    
    public static void enableCache(Query query){
    	query.setHint("org.hibernate.cacheable", true);
    }
    
    public static String getQueryString(Query query) {
    	QueryImpl queryImpl = query.unwrap(org.hibernate.internal.QueryImpl.class);
    	return queryImpl.getQueryString();
    }
    
    public static Map<String, Object> getQueryParams(Query query) {
    	QueryImpl queryImpl = unwrapQuery(query);
    	Map<String,TypedValue> namedParameters = ReflectionsUtil.getValue(queryImpl, "namedParameters");
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
		EntityManagerFactoryImpl entityManagerFactoryImpl = (EntityManagerFactoryImpl) entityManager.getEntityManagerFactory();
		return entityManagerFactoryImpl.getSessionFactory();
    }

}
