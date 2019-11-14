package br.com.infox.ibpm.variable.components;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação para identificar os controladores de um frame
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface Frame {

	/**
	 * identificador utilizado no nome da variável
	 */
	String id ();
	/**
	 * Caminho para o xhtml que representa o frame
	 */
	String xhtmlPath();
	/**
	 *  nome amigável do frame
	 */
    String name ();
    /**
     * descrição opcional para o frame
     */
    String description () default "";
    /**
     * parâmetros utilizados pelo frame
     */
    ParameterVariable[] parameters() default { };
    
    boolean disabled() default false;    
    
}
