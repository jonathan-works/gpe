package br.com.infox.epp.ws;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.ws.bean.UsuarioPerfilBean;
import br.com.infox.epp.ws.interceptors.Log;
import br.com.infox.epp.ws.interceptors.ValidarParametros;
import br.com.infox.epp.ws.messages.CodigosServicos;

@Path(PerfilRest.PATH)
@Stateless
@ValidarParametros
public class PerfilRest {
	
	public static final String PATH = "/perfil";
	public static final String PATH_ADICIONAR_PERFIL = "/adicionar";
	
	@Inject
	private PerfilRestService servico;

	@POST
	@Path(PATH_ADICIONAR_PERFIL)
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	@Log(codigo=CodigosServicos.WS_PERFIS_ADICIONAR_PERFIL)
	public String adicionarPerfil(UsuarioPerfilBean bean) throws DAOException {
		return servico.adicionarPerfil(bean);
	}
	
}
