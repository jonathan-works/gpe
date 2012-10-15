package br.com.infox.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anota��o para definir qual campo representa o valor que 
 * ser� usado para popular o caminho completo da entidade 
 * @author Infox
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface PathDescriptor {
	//Defini��o das anota��es utilizada pelo framework infox
}