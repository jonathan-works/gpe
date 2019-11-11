package br.com.infox.servlet.menu;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.servlet.ContextualHttpServletRequest;

import br.com.infox.epp.access.api.Authenticator;

@WebServlet(urlPatterns = "/logout")
public class ServletLogout extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        new ContextualHttpServletRequest(req) {
            @Override
            public void process() throws Exception {
                resp.sendRedirect(resp.encodeRedirectURL(req.getContextPath() + Authenticator.instance().unAuthenticate()));
            }
        }.run();
    }
}
