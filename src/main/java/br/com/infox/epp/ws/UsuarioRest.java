package br.com.infox.epp.ws;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.seam.contexts.Lifecycle;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.ws.annotation.Validate;
import br.com.infox.epp.ws.bean.UsuarioBean;
import br.com.infox.epp.ws.bean.UsuarioSenhaBean;

@Path(UsuarioRest.PATH)
// @InjectSeamContext
public class UsuarioRest implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String PATH = "/usuario";
	public static final String PATH_GRAVAR_USUARIO = "/gravar";
	public static final String PATH_ATUALIZAR_SENHA = "/atualizarSenha";


	@POST
	@Path(PATH_GRAVAR_USUARIO)
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	// @Log("WS0001")
	public String gravarUsuario(@Validate UsuarioBean bean) throws DAOException {
		Lifecycle.beginCall();
		String retorno = BeanManager.INSTANCE.getReference(UsuarioRestService.class).gravarUsuario(bean);
		Lifecycle.endCall();
		return retorno;
	}

	@POST
	@Path(PATH_ATUALIZAR_SENHA)
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	// @Log("WS0002")
	public String atualizarSenha(@Validate UsuarioSenhaBean bean) throws DAOException {
		Lifecycle.beginCall();
		String retorno = BeanManager.INSTANCE.getReference(UsuarioRestService.class).atualizarSenha(bean);
		Lifecycle.endCall();
		return retorno;
	}
}
