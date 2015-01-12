package br.com.infox.hibernate.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.Filter;
import org.hibernate.dialect.Dialect;
import org.hibernate.proxy.HibernateProxy;
import org.jboss.seam.Component;

import br.com.infox.hibernate.session.SessionAssistant;
import br.com.infox.seam.util.ComponentUtil;

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
    	EntityManager em = ComponentUtil.getComponent("entityManager");
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

}
