/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informação Ltda.

 Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; versão 2 da Licença.
 Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 
 Consulte a GNU GPL para mais detalhes.
 Você deve ter recebido uma cópia da GNU GPL junto com este programa; se não, 
 veja em http://www.gnu.org/licenses/   
 */
package br.com.infox.epp.access.api;

import javax.security.auth.login.LoginException;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.exception.BusinessException;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.service.PasswordService;

@Name(PasswordRequester.NAME)
public class PasswordRequester {
	
	private static final LogProvider LOG = Logging.getLogProvider(PasswordRequester.class);

	public static final String NAME = "passwordRequester";
	
	@In private PasswordService passwordService;

	private String login;
	private String email;

	public void requisitarNovaSenha() throws LoginException {
	    final FacesMessages facesMessages = FacesMessages.instance();
        try {
	        passwordService.requisitarNovaSenha(email, login);
	    } catch (BusinessException be){
	    	LOG.warn(".requisitarNovaSenha()", be);
            facesMessages.add(be.getLocalizedMessage());
        } catch (DAOException e) {
            LOG.warn(".requisitarNovaSenha()", e);
            facesMessages.add(e.getLocalizedMessage());
        }
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
}