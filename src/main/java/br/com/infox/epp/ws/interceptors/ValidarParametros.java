package br.com.infox.epp.ws.interceptors;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.interceptor.InterceptorBinding;


/**
 * Anotação utilizada para indicar que os parâmetros dos métodos chamados devem ser validados  
 * @author paulo
 */
@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface ValidarParametros {
	
	
}
