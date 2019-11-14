package br.com.infox.filter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.annotations.web.Filter;
import org.jboss.seam.util.Resources;
import org.jboss.seam.util.Strings;
import org.jboss.seam.web.AbstractFilter;

import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Name("resourceFilter")
@BypassInterceptors
@Scope(ScopeType.APPLICATION)
@Filter
public class ResourceFilter extends AbstractFilter {

    private static final String PATTERN = "/img/.*|/resources/js/.*|/resources/stylesheet/.*|/resources/styleSkinInfox/.*";
    private static final LogProvider LOG = Logging.getLogProvider(ResourceFilter.class);

    @Override
    public String getRegexUrlPattern() {
        return PATTERN;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        String path = request.getServletPath();
        ServletContext servletContext = request.getSession().getServletContext();
        String realPath = servletContext.getRealPath(path);
        if (new File(realPath).exists()) {
            chain.doFilter(request, response);
        } else {
            InputStream is = Resources.getResourceAsStream(path, servletContext);
            if (is != null) {
                writeResponse(response, path, is);
            }
        }
    }

    private void writeResponse(HttpServletResponse response, String path,
            InputStream is) {
        if (path.endsWith(".png") || path.endsWith(".gif")
                || path.endsWith(".jpg")) {
            try {
                ServletOutputStream os = response.getOutputStream();
                byte[] trecho = new byte[10240];
                for (int n; is.read(trecho) != -1;) {
                    n = is.read(trecho);
                    os.write(trecho, 0, n);
                }
                is.close();
                os.flush();
            } catch (IOException err) {
                LOG.error(".writeResponse()", err);
            }
        } else {
            try {
                String text = Strings.toString(is);
                response.getWriter().write(text.trim());
                response.getWriter().flush();
            } catch (IOException e) {
                LOG.error(".writeResponse()", e);
            }
        }
        response.setStatus(HttpServletResponse.SC_OK);
    }

}
