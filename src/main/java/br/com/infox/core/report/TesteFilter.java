//package br.com.infox.core.report;
//
//import java.io.IOException;
//
//import javax.naming.InitialContext;
//import javax.naming.NamingException;
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.jboss.seam.ScopeType;
//import org.jboss.seam.annotations.Name;
//import org.jboss.seam.annotations.Scope;
//import org.jboss.seam.annotations.intercept.BypassInterceptors;
//import org.jboss.seam.annotations.web.Filter;
//import org.jboss.seam.web.AbstractFilter;
//
//@Name("testeFilter")
//@BypassInterceptors
//@Scope(ScopeType.APPLICATION)
//@Filter
//public class TesteFilter extends AbstractFilter {
//
//	@Override
//	public String getRegexUrlPattern() {
//		return "/RelacaoJulgamento/PautaDOE/pautadoe.seam";
//	}
//	
//	@Override
//	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//		try {
//			InitialContext ic = new InitialContext();
//			AutomaticReportController automaticReportController = (AutomaticReportController) ic.lookup("java:module/AutomaticReportController");
//			if (automaticReportController.isValid(((HttpServletRequest) request).getHeader(AutomaticReportController.KEY_HEADER_NAME))) {
//				chain.doFilter(request, response);
//				return;
//			}
//		} catch (NamingException e1) {
//			e1.printStackTrace();
//			return;
//		}
//		((HttpServletResponse) response).setStatus(401);
//	}
//}
