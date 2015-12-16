package br.com.infox.epp.ws;

import javax.inject.Inject;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.ws.bean.UsuarioPerfilBean;
import br.com.infox.epp.ws.services.PerfilRestService;

public class PerfilRestImpl implements PerfilRest {
	
	@Inject
	private PerfilRestService servico;

	public String adicionarPerfil(String token, UsuarioPerfilBean bean) throws DAOException {
		return servico.adicionarPerfil(bean);
	}
	
}
