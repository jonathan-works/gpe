package br.com.infox.cdi.interceptors;


import java.lang.reflect.Field;

import javax.inject.Inject;

import org.jboss.seam.annotations.intercept.AroundInvoke;
import org.jboss.seam.annotations.intercept.Interceptor;
import org.jboss.seam.core.BijectionInterceptor;
import org.jboss.seam.intercept.InvocationContext;

import br.com.infox.cdi.util.CdiUtil;

@Interceptor(around={BijectionInterceptor.class})
public class CdiInjectionInterceptor {
	
	@AroundInvoke
	public Object injectCdi(InvocationContext ic) throws Exception {
		Object target = ic.getTarget();
		Class<?> clazz = target.getClass();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(Inject.class)) {
				field.setAccessible(true);
				Object value = CdiUtil.getComponent(field.getType());
				field.set(target, value);
			}
		}
		return ic.proceed();
	}
	
}
