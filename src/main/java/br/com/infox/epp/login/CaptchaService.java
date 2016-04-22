package br.com.infox.epp.login;

import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.ejb.Stateless;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.infox.cdi.producer.EntityManagerProducer;
import edu.emory.mathcs.backport.java.util.Arrays;

@Stateless
public class CaptchaService {

	private static final Integer MAX_LOGIN_ATTEMPTS = 5;
	private static final Integer MAX_LOGIN_ATTEMPTS_CHECK_SECONDS = 3600;

	private static final String NOME_COOKIE = "clientID";
	private static final String PATH_COOKIE = "/";
	private static final int MAX_AGE_COOKIE = 365 * 24 * 3600;

	
	@Inject
	private HttpServletRequest request;

	@Inject
	private CookieCaptchaSearch cookieCaptchaSearch;
	
	private boolean isLoginInvalidosExcedidos(Integer cookieId) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, -MAX_LOGIN_ATTEMPTS_CHECK_SECONDS);
		Long failedLogins = cookieCaptchaSearch.getTentativasLoginsInvalidos(cookieId, cal.getTime());
		if (failedLogins >= MAX_LOGIN_ATTEMPTS) {
			return true;
		}
		return false;
	}

	private Cookie getCookieRequest() {
		for (Cookie cookie : request.getCookies()) {
			if (NOME_COOKIE.equals(cookie.getName())) {
				return cookie;
			}
		}
		return null;
	}

	public boolean isMostrarCaptcha() {
		Cookie cookieRequest = getCookieRequest();
		if (cookieRequest == null) {
			return true;
		}
		String clientId = cookieRequest.getValue();
		CookieCaptcha cookieCaptcha = cookieCaptchaSearch.findByClientId(clientId);
		if (cookieCaptcha != null && !isLoginInvalidosExcedidos(cookieCaptcha.getId())) {
			return false;
		}
		return true;
	}

	public boolean isMostrarCaptcha(String login) {
		Cookie cookieRequest = getCookieRequest();
		if (cookieRequest == null) {
			return true;
		}
		String clientId = cookieRequest.getValue();
		CookieCaptcha cookieCaptcha = cookieCaptchaSearch.findByClientId(clientId);
		if (cookieCaptcha != null) {
			if (cookieCaptcha.getUsuarios().contains(login)) {
				return false;
			}
		}
		return true;
	}

	private String bin2hex(byte[] data) {
		StringBuffer retorno = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			retorno.append(String.format("%02X", data[i]));
		}
		return retorno.toString();
	}

	private String generateRandomId() {
		SecureRandom secureRandom = new SecureRandom();
		byte[] bytes = new byte[32];
		secureRandom.nextBytes(bytes);
		return bin2hex(bytes);
	}

	@SuppressWarnings("unchecked")
	public void loggedIn(String username) {
		Cookie cookie = getCookieRequest();
		
		CookieCaptcha cookieCaptcha = null;
		if (cookie == null) {
			HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext()
					.getResponse();
			
			//TODO: checar se o clientId já existe no banco
			String clientId = generateRandomId();
			
			cookie = new Cookie(NOME_COOKIE, clientId);
			cookie.setPath(PATH_COOKIE);
			cookie.setMaxAge(MAX_AGE_COOKIE);

			response.addCookie(cookie);
			
			cookieCaptcha = new CookieCaptcha();
			cookieCaptcha.setClientId(clientId);
			cookieCaptcha.setDataCriacao(new Date());
			cookieCaptcha.setUsuarios(new HashSet<String>(Arrays.asList(new String[] {username})));
			getEntityManager().persist(cookieCaptcha);
			getEntityManager().flush();
		}
		else
		{
			cookieCaptcha = cookieCaptchaSearch.findByClientId(cookie.getValue());
			if(!cookieCaptcha.getUsuarios().contains(username)) {
				cookieCaptcha.getUsuarios().add(username);
				getEntityManager().persist(cookieCaptcha);
			}
			resetTentativasLoginInvalido(cookieCaptcha.getId());
		}
	}
	
	protected void resetTentativasLoginInvalido(Integer cookieCaptchaId) {
		List<LoginInvalido> loginsInvalidos = cookieCaptchaSearch.listTentativasLoginInvalido(cookieCaptchaId);
		for(LoginInvalido loginInvalido : loginsInvalidos) {
			getEntityManager().remove(loginInvalido);
		}
		getEntityManager().flush();		
	}
	
	public void failedLogin(String username) {
		Cookie cookie = getCookieRequest();
		if(cookie == null) {
			return;
		}
		String clientId = cookie.getValue();
		CookieCaptcha cookieCaptcha = cookieCaptchaSearch.findByClientId(clientId);
		
		LoginInvalido loginInvalido = new LoginInvalido();
		loginInvalido.setData(new Date());
		loginInvalido.setCookieCaptcha(cookieCaptcha);
		getEntityManager().persist(loginInvalido);
		getEntityManager().flush();
	}
	
    private EntityManager getEntityManager() {
        return EntityManagerProducer.getEntityManager();
    }		
}
