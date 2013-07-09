package br.com.infox.ibpm.service;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.GenericManager;

@Name(AuthenticatorService.NAME)
@AutoCreate
public class AuthenticatorService extends GenericManager {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "authenticatorService";

}
