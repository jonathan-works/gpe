package br.com.infox.ibpm.variable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação para identificar um parâmetro esperado por uma {@link Taskpage}.
 * Deve definir o nome do parâmetro no atributo 'name'.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface TaskpageParameter {

    String name (); // nome do parâmetro
    String type () default "String"; // class type do parêmetro
    String tooltip () default ""; // descrição opcional para o parâmetro

}
