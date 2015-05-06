package br.com.infox.cdi.util;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public final class CdiUtil {
	
	@SuppressWarnings("unchecked")
	public static <T> T getComponent(Class<T> clazz) {
		BeanManager beanManager = getBeanManager();
		Bean<T> bean = (Bean<T>) beanManager.getBeans(clazz).iterator().next();
        CreationalContext<T> ctx = beanManager.createCreationalContext(bean);
        return (T) beanManager.getReference(bean, clazz, ctx);
	}
	
	private static BeanManager getBeanManager() {
		try {
			InitialContext initialContext = new InitialContext();
			return (BeanManager) initialContext.lookup("java:comp/BeanManager");
		} catch (NamingException e) {
			throw new IllegalStateException("Error finding BeanManger", e);
		}
	}

}
