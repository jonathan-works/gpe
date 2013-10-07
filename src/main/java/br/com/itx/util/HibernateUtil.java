/* $Id: HibernateUtil.java 516 2010-08-12 23:21:53Z jplacerda $ */

package br.com.itx.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.Filter;
import org.hibernate.Session;
import org.hibernate.proxy.HibernateProxy;
import org.jboss.seam.persistence.FullTextHibernateSessionProxy;

public final class HibernateUtil {
	
	private HibernateUtil() {}
	
	public static void disableFilters(String...names) {
		Session session = getSession();
		for (String name : names) {
			session.disableFilter(name);
		}
	}


	public static void enableFilters(String...names) {
		Session session = getSession();
		for (String name : names) {
			session.enableFilter(name);
		}
	}
	
	public static void setFilterParameter(String filterName, 
			String parameterName, Object paremeterValue) {
		Filter enabledFilter = getEnabledFilter(filterName);
		enabledFilter.setParameter(parameterName, paremeterValue);
	}
	
	public static void setFilterParameterList(String filterName, 
			String parameterName, Collection<?> paremeterValues) {
		Filter enabledFilter = getEnabledFilter(filterName);
		enabledFilter.setParameterList(parameterName, paremeterValues);
	}	
	
	public static void setFilterParameterList(String filterName, 
			String parameterName, Object[] paremeterValues) {
		Filter enabledFilter = getEnabledFilter(filterName);
		enabledFilter.setParameterList(parameterName, paremeterValues);
	}	
	
	public static Filter getEnabledFilter(String filterName) {
		Filter enabledFilter = getSession().getEnabledFilter(filterName);
		if (enabledFilter == null) {
			getSession().enableFilter(filterName);
			enabledFilter = getSession().getEnabledFilter(filterName);
		}
		return enabledFilter;
	}

	public static Session getSession() {
		return (Session) EntityUtil.getEntityManager().getDelegate();
	}

	public static void disableAllFilters() {
		FullTextHibernateSessionProxy session = (FullTextHibernateSessionProxy) getSession();
		Map m = session.getEnabledFilters();
		
		// Dividido em dois fors para evitar o erro de acesso concorrente ao Map que contém os filtros ativos
		List<String> filters = new ArrayList<String>();
		for (Object s : m.keySet()) {
			filters.add(s.toString());
		}
		for (String s: filters) {
			session.disableFilter(s);
		}
	}
	
	public static Object removeProxy(Object object) {
		if (object instanceof HibernateProxy) {
			return ((HibernateProxy) object).getHibernateLazyInitializer().getImplementation();
		}
		return object;
	}

	
}
