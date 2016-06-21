package br.com.infox.certificado.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.samskivert.mustache.Mustache;

import br.com.infox.certificado.bean.CertificateSignatureConfigBean;


@WebServlet(urlPatterns = CertificadoDigitalJNLPServlet.SERVLET_PATH)
public class CertificadoDigitalJNLPServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public static final int COOKIE_MAX_AGE = 8 * 60;
	public static final String SERVLET_PATH = "/certificadodigital/jnlp";
	private static final String SIGN_COOKIE_NAME = "br.com.infox.epp.sign.token";
	public static final String DOCUMENTOS_ASSINATURA="DocumentoView.documentosAssinatura";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/x-java-jnlp-file");
		resp.setHeader("Content-disposition", "attachment; filename=\"certificado_digital.jnlp\"");
		final String token = req.getParameter("token");
		
		Cookie cookie = new Cookie(SIGN_COOKIE_NAME, token);
		cookie.setMaxAge(COOKIE_MAX_AGE);
		cookie.setPath(req.getServletContext().getContextPath());
		resp.addCookie(cookie);
		generateJnlp(req, resp.getWriter(), token);
	}
	
	private void generateJnlp(HttpServletRequest request, Writer responseWriter, String token) {
		Map<String, Object> params = new HashMap<>();
		String urlEpp = request.getRequestURL().toString().replace(SERVLET_PATH, "");
		//caso a requisição seja redirecionada pega o protocolo utilizado originalmente. Precisa que o parâmetro ProxyPreserveHost = On esteja configurado no apache.
		String originalRequestProtocol = request.getHeader("X_FORWARDED_PROTO");
		if( originalRequestProtocol != null){
			urlEpp = urlEpp.replace("http://", originalRequestProtocol + "://");
		}
		CertificateSignatureConfigBean config = new CertificateSignatureConfigBean();
		config.setUrl(urlEpp + "/rest");
		config.setToken(token);
		config.setMd5s(new ArrayList<String>());
		config.setMultiSign(new HashMap<String, String>());
		
		params.put("urlEpp", urlEpp);
		params.put("config", new Gson().toJson(config));
		
		InputStreamReader text = new InputStreamReader(getClass().getResourceAsStream("/templates/certificado_digital.jnlp"));
		Mustache.compiler().escapeHTML(false).compile(text).execute(params, responseWriter);
	}
}