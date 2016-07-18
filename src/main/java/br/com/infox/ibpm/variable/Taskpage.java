package br.com.infox.ibpm.variable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação para identificar os controladores de uma taskpage.
 * Deve conter o nome da taskpage no atributo 'name'.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Taskpage {

    String name (); // nome do arquivo xhtml da taskpage
    String description () default ""; // descrição opcional para a taskpage
}
