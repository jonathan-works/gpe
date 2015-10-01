package br.com.infox.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import br.com.infox.cdi.producer.EntityManagerProducer;

@WebFilter(urlPatterns = "*")
public class ThreadLocalRequestCleanerFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		EntityManagerProducer.clear();
		chain.doFilter(request, response);
		EntityManagerProducer.clear();
	}

	@Override
	public void destroy() {
	}
}
