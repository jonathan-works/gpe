package br.com.infox.epp.login;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.Component;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.cdi.exception.ExceptionHandled;

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
    @Inject
    private ExtensaoLogin extensaoLogin;

	private static final long serialVersionUID = 1L;

	private boolean forcarMostrarCaptcha = false;
	
	public boolean isMostrarCaptcha() {
		String login = Identity.instance().getCredentials().getUsername();
		if(login != null && captchaService.isMostrarCaptcha(login)) {
			return true;
		}
		
		return captchaService.isMostrarCaptcha() || forcarMostrarCaptcha;
	}
	
	@ExceptionHandled
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
	
	public List<String> getExtensoes(){
	    return extensaoLogin.getExtensoes();
	}
}
