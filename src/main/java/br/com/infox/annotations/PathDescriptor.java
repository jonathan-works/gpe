package br.com.infox.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação para definir qual campo representa o valor que 
 * será usado para popular o caminho completo da entidade 
 * @author Infox
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface PathDescriptor {
	//Definição das anotações utilizada pelo framework infox
}