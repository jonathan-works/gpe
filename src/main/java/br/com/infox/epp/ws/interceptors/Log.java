package br.com.infox.epp.ws.interceptors;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;

@Inherited
@InterceptorBinding
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
/**
 * Grava log no banco das chamadas aos serviços REST
 * @author paulo
 *
 */
public @interface Log {
	
	public static final String EMPTY = "";
	
	/**
	 * Define o código do LOG que será gerado
	 * @return
	 */
	@Nonbinding
	String codigo() default EMPTY;
}
