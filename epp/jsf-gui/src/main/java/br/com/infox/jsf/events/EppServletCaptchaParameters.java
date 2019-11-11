package br.com.infox.jsf.events;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import br.com.infox.epp.cdi.util.JNDI;
import br.com.infox.epp.system.dao.ParametroDAO;

public class EppServletCaptchaParameters implements ServletContextListener {

	public static final String NOME_PARAMETRO_PUBLIC_API_KEY = "recaptchaPublicKey";
	public static final String NOME_PARAMETRO_PRIVATE_API_KEY = "recaptchaPrivateKey";
	
	private static final String PUBLIC_CAPTCHA_KEY = "primefaces.PUBLIC_CAPTCHA_KEY"; 
	private static final String PRIVATE_CAPTCHA_KEY = "primefaces.PRIVATE_CAPTCHA_KEY";
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		carregarParametrosCaptcha(sce);
	}
	
	private void carregarParametrosCaptcha(ServletContextEvent event) {
		ServletContext ctx = event.getServletContext();
		
		ParametroDAO parametroDao = JNDI.lookup("java:module/ParametroDAO");
		
		String publicKey = parametroDao.getParametroByNomeVariavel(NOME_PARAMETRO_PUBLIC_API_KEY).getValorVariavel();
		String privateKey = parametroDao.getParametroByNomeVariavel(NOME_PARAMETRO_PRIVATE_API_KEY).getValorVariavel();
				
		ctx.setInitParameter(PUBLIC_CAPTCHA_KEY, publicKey);
		ctx.setInitParameter(PRIVATE_CAPTCHA_KEY, privateKey);
	}


	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}

}
