package br.com.infox.epp.ws;

import java.io.Serializable;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.ws.bean.UsuarioBean;
import br.com.infox.epp.ws.bean.UsuarioSenhaBean;
import br.com.infox.epp.ws.interceptors.Log;
import br.com.infox.epp.ws.interceptors.TokenAuthentication;
import br.com.infox.epp.ws.messages.CodigosServicos;
import br.com.infox.epp.ws.services.UsuarioRestService;

@TokenAuthentication
@Path(UsuarioRest.PATH)
public class UsuarioRest implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String PATH = "/usuario";
	public static final String PATH_GRAVAR_USUARIO = "/gravar";
	public static final String PATH_ATUALIZAR_SENHA = "/atualizarSenha";

	@Inject
	private UsuarioRestService servico;

	@POST
	@Path(PATH_GRAVAR_USUARIO)
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	@Log(codigo=CodigosServicos.WS_PERFIS_GRAVAR_USUARIO)
	public String gravarUsuario(final UsuarioBean bean) throws DAOException {
		return servico.gravarUsuario(bean);
	}

	@POST
	@Path(PATH_ATUALIZAR_SENHA)
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	@Log(codigo=CodigosServicos.WS_PERFIS_ATUALIZAR_SENHA)
	public String atualizarSenha(final UsuarioSenhaBean bean) throws DAOException {
		return servico.atualizarSenha(bean);
	}
}
