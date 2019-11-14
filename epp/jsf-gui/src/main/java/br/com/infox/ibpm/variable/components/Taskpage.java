package br.com.infox.ibpm.variable.components;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação para identificar os controladores de uma taskpage.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface Taskpage {

	/**
	 * identificador utilizado no nome da variável
	 */
	String id ();
	/**
	 * Caminho para o xhtml que representa a taskpage
	 */
	String xhtmlPath();
	/**
	 *  nome amigável da taskpage
	 */
    String name ();
    /**
     * descrição opcional para a taskpage
     */
    String description () default "";
    /**
     * parâmetros utilizados pela taskpage
     */
    ParameterVariable[] parameters() default { };
    
    boolean disabled() default false;
    
}
