package br.com.infox.epp.ws;

import javax.inject.Inject;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.ws.bean.UsuarioBean;
import br.com.infox.epp.ws.bean.UsuarioSenhaBean;
import br.com.infox.epp.ws.services.UsuarioRestService;


public class UsuarioRestImpl implements UsuarioRest {

	@Inject
	private UsuarioRestService servico;

	public String gravarUsuario(String token, UsuarioBean bean) throws DAOException {
		return servico.gravarUsuario(bean);
	}
	
	public String atualizarSenha(String token, UsuarioSenhaBean bean) throws DAOException {
		return servico.atualizarSenha(bean);
	}
}
