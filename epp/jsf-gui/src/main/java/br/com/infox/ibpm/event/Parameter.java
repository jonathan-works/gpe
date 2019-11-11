package br.com.infox.ibpm.event;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

@Retention(RUNTIME)
public @interface Parameter {
	boolean selectable () default false;
	String defaultValue () default "";
	String label () default "";
	String tooltip () default "";
}
