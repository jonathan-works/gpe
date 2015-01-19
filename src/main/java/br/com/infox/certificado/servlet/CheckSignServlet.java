package br.com.infox.certificado.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.servlet.ContextualHttpServletRequest;

import com.google.gson.Gson;


@WebServlet(urlPatterns = "/certificadodigital/checksign")
public class CheckSignServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        new ContextualHttpServletRequest(request) {
            @SuppressWarnings("unchecked")
			@Override
            public void process() throws Exception {
                Context applicationContext = Contexts.getApplicationContext();
                Map<String, Object> map = (Map<String, Object>) applicationContext.get(CertificateServlet.SIGNATURE_CONTEXT_KEY);
                response.setContentType("application/json");
                String id = request.getParameter("id");
                Map<String, Object> result = new HashMap<>();
                if (map != null) {
                	result.put("signed", map.containsKey(id));
                } else {
                	result.put("signed", false);
                }
                response.getWriter().print(new Gson().toJson(result));
            }
        }.run();
    }
    
}
