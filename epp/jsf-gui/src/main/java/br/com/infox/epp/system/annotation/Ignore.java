package br.com.infox.epp.system.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Anotação indicativa que a classe deve ser ignorada no log
 * 
 * @author Rodrigo Menezes
 * @version 1.0
 * 
 */

@Target(TYPE)
@Retention(RUNTIME)
public @interface Ignore {
    // Definição das anotações utilizada pelo framework infox
}
