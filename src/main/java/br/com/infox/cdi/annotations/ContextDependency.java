package br.com.infox.cdi.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.com.infox.cdi.interceptors.CdiInjectionInterceptor;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@org.jboss.seam.annotations.intercept.Interceptors(CdiInjectionInterceptor.class)
public @interface ContextDependency {

}
