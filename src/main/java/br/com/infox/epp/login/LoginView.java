package br.com.infox.epp.login;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.Component;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.security.Credentials;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.epp.access.api.Authenticator;

@Named
@SessionScoped
public class LoginView implements Serializable {
	
	@Inject
	private CaptchaService captchaService;
    @Inject
    private LoginService loginService;
    @Inject
    private Authenticator authenticator;
    @Inject
    private InfoxMessages infoxMessages;

	private static final long serialVersionUID = 1L;

	private boolean forcarMostrarCaptcha = false;
	
	public boolean isMostrarCaptcha() {
		return captchaService.isMostrarCaptcha() || forcarMostrarCaptcha;
	}
	
	public void login() {
	    
		Credentials credentials = (Credentials) Component.getInstance(Credentials.class);
		String username = credentials.getUsername();
		String password = credentials.getPassword();
		
        if(!isMostrarCaptcha() && captchaService.isMostrarCaptcha(username)) {
        	forcarMostrarCaptcha = true;
        	FacesMessages.instance().add(Severity.ERROR, infoxMessages.get("captcha.obrigatorio"));
        	return;
        }
        
        if(loginService.autenticar(username, password)) {
			captchaService.loggedIn(username);
			forcarMostrarCaptcha = false;        	
        }
        else
        {
        	captchaService.failedLogin(username);
        }
        
        authenticator.login();
	}
	
	
}
