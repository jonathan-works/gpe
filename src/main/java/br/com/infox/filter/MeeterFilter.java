package br.com.infox.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.time.StopWatch;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.annotations.web.Filter;
import org.jboss.seam.core.Init;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.web.AbstractFilter;

@Name("meeterFilter")
@BypassInterceptors
@Scope(ScopeType.APPLICATION)
@Filter
public class MeeterFilter extends AbstractFilter {

    private static final LogProvider LOG = Logging.getLogProvider(MeeterFilter.class);

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp,
            FilterChain chain) throws IOException, ServletException {
        StopWatch sw = new StopWatch();
        sw.start();
        chain.doFilter(req, resp);
        String ajaxPushHeader = null;
        long time = sw.getTime();
        if (ajaxPushHeader == null && time > 100) {
            LOG.info(((HttpServletRequest) req).getRequestURI() + ": " + time);
        }
    }

    @Override
    public boolean isDisabled() {
        return !Init.instance().isDebug();
    }

}
