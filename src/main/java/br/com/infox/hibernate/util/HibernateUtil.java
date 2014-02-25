package br.com.infox.hibernate.util;

import org.hibernate.Filter;
import org.hibernate.proxy.HibernateProxy;
import org.jboss.seam.Component;

import br.com.infox.hibernate.session.SessionAssistant;

public final class HibernateUtil {

    private HibernateUtil() {
    }

    public static void setFilterParameter(String filterName, String parameterName, Object paremeterValue) {
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

}
