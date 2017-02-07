package br.com.infox.servlet.menu;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.servlet.ContextualHttpServletRequest;

import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@WebServlet(urlPatterns = "/trocarPerfil")
public class ServletTrocarPerfil extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final LogProvider LOG = Logging.getLogProvider(ServletTrocarPerfil.class);

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        final Integer idPerfil = Integer.valueOf(req.getParameter("idPerfil"));
        try {
            new ContextualHttpServletRequest(req) {
                @Override
                public void process() throws Exception {
                    String url = Authenticator.instance().trocarPerfil(idPerfil);
                    if (url != null) {
                        resp.sendRedirect(resp.encodeRedirectURL(req.getContextPath() + url));
                    }
                }
            }.run();
        } catch (Exception e) {
            LOG.error("", e);
            resp.sendRedirect(resp.encodeRedirectURL(req.getContextPath() + "/errorUnexpected.seam"));
        }
    }
}
