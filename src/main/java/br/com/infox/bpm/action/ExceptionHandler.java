package br.com.infox.bpm.action;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.ibpm.home.Authenticator;

@Name("org.jboss.seam.exception.exceptions")
@Scope(ScopeType.APPLICATION)
@Install(precedence = Install.APPLICATION)
@BypassInterceptors
public class ExceptionHandler extends br.com.itx.exception.ExceptionHandler {


	protected void printErroInfo(Exception e) {
		super.printErroInfo(e);
		try {
			printUserInfo();
		} catch (Exception eLocal) {
			log.error(eLocal.getMessage());
		}
	}
	
	private void printUserInfo() {
		log.error("Usuario logado: " + Authenticator.getUsuarioLogado());
		log.error("Localizacao: " + Authenticator.getLocalizacaoAtual());
		log.error("Papel: " + Authenticator.getPapelAtual());
		printLine();
	}

}
