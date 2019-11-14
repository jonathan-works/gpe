package br.com.infox.epp.ws.interceptors;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;

import br.com.infox.epp.ws.autenticacao.AutenticadorToken;
import br.com.infox.epp.ws.autenticacao.AutenticadorTokenPadrao;

@Inherited
@InterceptorBinding
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TokenAuthentication {
	
	public enum TipoExcecao {
		STRING, JSON;
	}
	
	@Nonbinding
	public Class<? extends AutenticadorToken> autenticador() default AutenticadorTokenPadrao.class;
	
	@Nonbinding
	public TipoExcecao tipoExcecao() default TipoExcecao.JSON;
}
