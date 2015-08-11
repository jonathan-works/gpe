package br.com.infox.certificado.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.Component;
import org.jboss.seam.servlet.ContextualHttpServletRequest;

import com.google.gson.Gson;
import com.samskivert.mustache.Mustache;

import br.com.infox.certificado.bean.CertificateSignatureConfigBean;
import br.com.infox.epp.certificado.manager.CertificateSignatureGroupManager;


@WebServlet(urlPatterns = CertificadoDigitalJNLPServlet.SERVLET_PATH)
public class CertificadoDigitalJNLPServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public static final int COOKIE_MAX_AGE = 8 * 60;
	public static final String SERVLET_PATH = "/certificadodigital/jnlp";
	private static final String SIGN_COOKIE_NAME = "br.com.infox.epp.sign.token";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/x-java-jnlp-file");
		resp.setHeader("Content-disposition", "attachment; filename=\"certificado_digital.jnlp\"");
		final String uuid = UUID.randomUUID().toString();
		new ContextualHttpServletRequest(req) {
            @Override
            public void process() throws Exception {
                CertificateSignatureGroupManager certificateSignatureGroupManager = (CertificateSignatureGroupManager) Component.getInstance(CertificateSignatureGroupManager.NAME);
                certificateSignatureGroupManager.createForToken(uuid);
            }
        }.run();
		Cookie cookie = new Cookie(SIGN_COOKIE_NAME, uuid);
		cookie.setMaxAge(COOKIE_MAX_AGE);
		cookie.setPath(req.getServletContext().getContextPath());
		resp.addCookie(cookie);
		generateJnlp(req, resp.getWriter(), uuid);
	}
	
	private void generateJnlp(HttpServletRequest request, Writer responseWriter, String uuid) {
		Map<String, Object> params = new HashMap<>();

		String urlEpp = request.getRequestURL().toString().replace(SERVLET_PATH, "");

		CertificateSignatureConfigBean config = new CertificateSignatureConfigBean();
		config.setUrl(urlEpp + "/rest" + CertificadoDigitalWS.PATH);
		config.setToken(uuid);
		config.setMd5s(new ArrayList<String>());
		config.setMultiSign(new HashMap<String, String>());
		
		String md5s = request.getParameter("md5");
		if (md5s != null && !md5s.isEmpty()) {
			for (String md5 : md5s.split(",")) {
				config.getMd5s().add(md5);
			}
		} else {
		    String loteDocumentos = request.getParameter("multiSign");
		    if (loteDocumentos != null && !loteDocumentos.isEmpty()) {
		        for (String documentData : loteDocumentos.split(",")) {
		            String[] split = documentData.split(":");
		            String documentUuid = split[0];
		            String documentMd5 = split[1];
		            config.getMultiSign().put(documentUuid, documentMd5);
		        }
		    }
		}
		
		params.put("urlEpp", urlEpp);
		params.put("config", new Gson().toJson(config));
		
		InputStreamReader text = new InputStreamReader(getClass().getResourceAsStream("/templates/certificado_digital.jnlp"));
		Mustache.compiler().escapeHTML(false).compile(text).execute(params, responseWriter);
	}
}