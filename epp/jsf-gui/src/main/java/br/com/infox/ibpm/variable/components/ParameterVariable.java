package br.com.infox.ibpm.variable.components;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.com.infox.ibpm.variable.components.ParameterDefinition.ParameterType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface ParameterVariable {

    String id();
    ParameterType type() default ParameterType.STRING;
    String description() default "";
}
