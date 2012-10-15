package br.com.infox.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anota��o para definir se a entidade � recursiva 
 * @author Infox
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Recursive {
	//Defini��o das anota��es utilizada pelo framework infox
}