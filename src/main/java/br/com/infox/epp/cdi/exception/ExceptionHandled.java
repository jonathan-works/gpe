package br.com.infox.epp.cdi.exception;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;

@Inherited
@InterceptorBinding
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExceptionHandled {
    
	@Nonbinding
	String lockExceptionMessage() default "";
	@Nonbinding
	String createdMessage() default "#{infoxMessages['entity_created']}";
	@Nonbinding
	String updatedMessage() default "#{infoxMessages['entity_updated']}";
	@Nonbinding
	String removedMessage() default "#{infoxMessages['entity_deleted']}";
	@Nonbinding
	String inactivatedMessage() default "#{infoxMessages['entity_inactived']}";
	@Nonbinding
	MethodType value() default MethodType.UNSPECIFIED;
	@Nonbinding
    String errorMessage() default "";
	@Nonbinding
    String successMessage() default "";
	@Nonbinding
    boolean createLogErro() default false;
	
	public enum MethodType {
		PERSIST, UPDATE, REMOVE, INACTIVE, UNSPECIFIED;
	}
	
}
