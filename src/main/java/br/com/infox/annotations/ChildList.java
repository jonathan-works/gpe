package br.com.infox.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação para definir qual campo representa a lista de filhos da entidade 
 * @author Infox
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface ChildList {
	//Anotação para definir qual campo representa a lista de filhos da entidade 
}