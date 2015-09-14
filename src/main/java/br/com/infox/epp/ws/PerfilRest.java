package br.com.infox.epp.ws;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.seam.contexts.Lifecycle;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.ws.annotation.Validate;
import br.com.infox.epp.ws.bean.UsuarioPerfilBean;
import br.com.infox.epp.ws.interceptors.InjectSeamContext;
import br.com.infox.epp.ws.interceptors.Log;

@Path(PerfilRest.PATH)
@InjectSeamContext
public class PerfilRest {
	
	public static final String PATH = "/perfil";
	public static final String PATH_ADICIONAR_PERFIL = "/adicionar";

	@POST
	@Path(PATH_ADICIONAR_PERFIL)
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	@Log("WS0003")
	public String adicionarPerfil(@Validate UsuarioPerfilBean bean) throws DAOException {
		Lifecycle.beginCall();
		String retorno = BeanManager.INSTANCE.getReference(PerfilRestService.class).adicionarPerfil(bean);
		Lifecycle.endCall();
		return retorno;
	}
	
}
