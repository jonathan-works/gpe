package br.com.infox.ibpm.event;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;


@Target(METHOD)
@Retention(RUNTIME)
public @interface External {
	
	Parameter [] value () default {};
	String tooltip () default "";
	ExpressionType expressionType() default ExpressionType.EVENTOS;
	String example() default "";

	public static enum ExpressionType {
		GERAL, RAIA_DINAMICA, EVENTOS, GATEWAY;
	}
}
