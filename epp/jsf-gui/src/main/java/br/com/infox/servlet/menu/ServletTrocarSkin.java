package br.com.infox.servlet.menu;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.infox.epp.cdi.util.Beans;
import br.com.infox.epp.layout.manager.SkinSessaoManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@WebServlet(urlPatterns = "/trocarSkin")
public class ServletTrocarSkin extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final LogProvider LOG = Logging.getLogProvider(ServletTrocarSkin.class);

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        try {
            SkinSessaoManager skinSessaoManager = Beans.getReference(SkinSessaoManager.class);
            String idSkin = req.getParameter("idSkin");
            skinSessaoManager.setSkin(idSkin);
            skinSessaoManager.setSkinCookie(idSkin, req.getContextPath(), resp);
            resp.sendRedirect(resp.encodeRedirectURL(req.getHeader("Referer")));
        } catch (Exception e) {
            LOG.error("", e);
            resp.sendRedirect(resp.encodeRedirectURL(req.getContextPath() + "/errorUnexpected.seam"));
        }
    }
}
