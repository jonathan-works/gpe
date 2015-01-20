package br.com.infox.certificado.service;

import java.io.IOException;
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

import org.rythmengine.RythmEngine;

import br.com.infox.certificado.bean.CertificateSignatureConfigBean;
import br.com.infox.seam.path.PathResolver;

import com.google.gson.Gson;


@WebServlet(urlPatterns = CertificadoDigitalJNLPServlet.SERVLET_PATH)
public class CertificadoDigitalJNLPServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public static final int COOKIE_MAX_AGE = 8 * 60;
	public static final String SERVLET_PATH = "/certificadodigital/jnlp";
	private static final String SIGN_COOKIE_NAME = "br.com.infox.epp.sign.token";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String jnlp = generateJnlp(req, resp);
		resp.setContentType("application/x-java-jnlp-file");
		resp.setContentLength(jnlp.length());
		resp.setHeader("Content-disposition", "attachment; filename=\"certificado_digital.jnlp\"");
		resp.getWriter().print(jnlp);
	}
	
	private String generateJnlp(HttpServletRequest request, HttpServletResponse response) {
		RythmEngine engine = new RythmEngine();
		Map<String, Object> params = new HashMap<>();

		String urlEpp = request.getRequestURL().toString().replace(SERVLET_PATH, "");
		
		CertificateSignatureConfigBean config = new CertificateSignatureConfigBean();
		config.setUrl(urlEpp + PathResolver.SEAM_REST_URL + CertificadoDigitalWS.PATH);
		String uuid = UUID.randomUUID().toString();
		config.setToken(uuid);
		config.setMd5s(new ArrayList<String>());
		
		String md5s = request.getParameter("md5");
		if (md5s != null && !md5s.isEmpty()) {
			for (String md5 : md5s.split(",")) {
				config.getMd5s().add(md5);
			}
		}
		
		params.put("urlEpp", urlEpp);
		params.put("config", new Gson().toJson(config));
		String jnlp = engine.render("certificado_digital.jnlp", params);
		engine.shutdown();
		
		Cookie cookie = new Cookie(SIGN_COOKIE_NAME, uuid);
		cookie.setMaxAge(COOKIE_MAX_AGE);
		cookie.setPath(request.getServletContext().getContextPath());
		response.addCookie(cookie);
		
		return jnlp;
	}
}