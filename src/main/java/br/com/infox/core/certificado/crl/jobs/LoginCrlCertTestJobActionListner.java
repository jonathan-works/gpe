package br.com.infox.core.certificado.crl.jobs;

import org.jboss.seam.security.Identity;

public class LoginCrlCertTestJobActionListner implements CrlCertTestJobActionListner {
	
	Identity identity;
	
	public LoginCrlCertTestJobActionListner() {
		try {
			this.identity = Identity.instance();
		} catch (IllegalStateException e) {
			// não há contexto
		}
	}

	public void execute(boolean revoked) {
		if (revoked && identity != null) {
			identity.unAuthenticate();
		}
	}

}