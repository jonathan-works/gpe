package br.com.infox.filter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.annotations.web.Filter;
import org.jboss.seam.web.AbstractFilter;

@Name("richfacesFirefoxFilter")
@BypassInterceptors
@Scope(ScopeType.APPLICATION)
@Filter
/**
 * Corrigir codificação da URL ao usar o Firefox > 11 e Richfaces 3
 * https://community.jboss.org/thread/197045?start=15&tstart=0
 * @author gabriel
 *
 */
public class RichfacesFirefoxFilter extends AbstractFilter {
	
	private static final String PATTERN = "/a4j/.*";
	
	@Override
	public String getRegexUrlPattern() {
		return PATTERN;
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		chain.doFilter(new HttpServletRequestWrapper((HttpServletRequest) request) { 
			 @Override
			public String getRequestURI() {
				try {
					return URLDecoder.decode(super.getRequestURI(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					throw new IllegalStateException("Erro ao decodificar URI", e);
				}
			}
		}, response);
	}
}
