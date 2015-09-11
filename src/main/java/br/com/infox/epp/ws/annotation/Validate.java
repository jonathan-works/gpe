package br.com.infox.epp.ws.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Anotação utilizada para indicar que um parâmetro de um serviço REST deve ser validado 
 * @author paulo
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface Validate {
	
	
}
