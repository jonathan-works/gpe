package br.com.infox.servlet.menu;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.servlet.ContextualHttpServletRequest;

import br.com.infox.epp.ajuda.view.AjudaView;
import br.com.infox.seam.util.ComponentUtil;

@WebServlet(urlPatterns = "/openHelp")
public class ServletOpenHelp extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        new ContextualHttpServletRequest(req) {
            @Override
            public void process() throws Exception {
                AjudaView ajudaView = ComponentUtil.getComponent(AjudaView.NAME);
                ajudaView.setViewId(req.getParameter("viewId"), true);
                resp.sendRedirect(resp.encodeRedirectURL(req.getContextPath() + "/help/show.seam?scid=" + req.getParameter("scid")));
            }
        }.run();
    }
}
