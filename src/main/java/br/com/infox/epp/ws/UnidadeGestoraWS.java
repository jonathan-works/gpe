package br.com.infox.epp.ws;

import java.io.Serializable;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.inject.Inject;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.ws.annotation.Validate;
import br.com.infox.epp.ws.bean.UsuarioBean;
import br.com.infox.epp.ws.bean.UsuarioPerfilBean;
import br.com.infox.epp.ws.bean.UsuarioSenhaBean;
import br.com.infox.epp.ws.interceptors.InjectSeamContext;
import br.com.infox.epp.ws.interceptors.Log;

@Path(UnidadeGestoraWS.PATH)
@InjectSeamContext
public class UnidadeGestoraWS implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final String PATH = "/unidadeGestora";
	public static final String PATH_GRAVAR_USUARIO = "/gravarUsuario";
	public static final String PATH_ATUALIZAR_SENHA = "/atualizarSenha";
	public static final String PATH_ADICIONAR_PERFIL = "/adicionarPerfil";
	public static final String PATH_REMOVER_PERFIL = "/removerPerfil";
	
	@Inject
	private UnidadeGestoraService servico;
	
	@POST
	@Path(PATH_GRAVAR_USUARIO)
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	@Log("WS0001")
	public String gravarUsuario(@Validate UsuarioBean bean) throws DAOException {
		return servico.gravarUsuario(bean);
	}
	
	@POST
	@Path(PATH_ATUALIZAR_SENHA)
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	@Log("WS0002")
	public String atualizarSenha(@Validate UsuarioSenhaBean bean) throws DAOException {
		return servico.atualizarSenha(bean);
	}
	
	@POST
	@Path(PATH_ADICIONAR_PERFIL)
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	@Log("WS0003")
	public String adicionarPerfil(@HeaderParam("token") String token,
			@Validate UsuarioPerfilBean bean) throws DAOException {
		return servico.adicionarPerfil(bean);
	}
	
	@POST
	@Path(PATH_REMOVER_PERFIL)
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	@Log("WS0004")
	public String removerPerfil(@Validate UsuarioPerfilBean bean) throws DAOException {
		return servico.removerPerfil(bean);
	}
}
