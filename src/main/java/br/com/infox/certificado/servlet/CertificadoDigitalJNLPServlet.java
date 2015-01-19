package br.com.infox.certificado.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.rythmengine.RythmEngine;


@WebServlet(urlPatterns = "/certificadodigital/jnlp")
public class CertificadoDigitalJNLPServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public static final int COOKIE_MAX_AGE = 8 * 60;
	private static final String SIGN_COOKIE_NAME = "br.com.infox.epp.sign.id";

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

		String urlEpp = request.getRequestURL().toString().replace("/certificadodigital/jnlp", "");
		
		List<String> jarArgs = new ArrayList<>();
		jarArgs.add(urlEpp + CertificateServlet.SERVLET_PATH);
		String uuid = UUID.randomUUID().toString();
		jarArgs.add(uuid);
		String md5s = request.getParameter("md5");
		if (md5s != null && !md5s.isEmpty()) {
			for (String md5 : md5s.split(",")) {
				jarArgs.add(md5);
			}
		}
		
		params.put("jarArgs", jarArgs);
		params.put("urlEpp", urlEpp);
		String jnlp = engine.render("certificado_digital.jnlp", params);
		engine.shutdown();
		
		Cookie cookie = new Cookie(SIGN_COOKIE_NAME, uuid);
		cookie.setMaxAge(COOKIE_MAX_AGE);
		cookie.setPath(request.getServletContext().getContextPath());
		response.addCookie(cookie);
		
		return jnlp;
	}
}