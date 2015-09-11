package br.com.infox.epp.ws.interceptors;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.interceptor.InterceptorBinding;

/**
 * @author paulo
 *
 */
@InterceptorBinding
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Log {
	
	/**
	 * Define o código do LOG que será gerado
	 * @return
	 */
	String value();

}
