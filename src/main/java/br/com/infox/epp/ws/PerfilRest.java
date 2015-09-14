package br.com.infox.epp.ws;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.ws.bean.UsuarioPerfilBean;
import br.com.infox.epp.ws.messages.WSMessages;

@Path(PerfilRest.PATH)
public class PerfilRest {
	
	public static final String PATH = "/perfil";
	public static final String PATH_ADICIONAR_PERFIL = "/adicionar";

	@POST
	@Path(PATH_ADICIONAR_PERFIL)
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public String adicionarPerfil(@HeaderParam("token") String token, final UsuarioPerfilBean bean) throws DAOException {
		ChamadaWebService<UsuarioPerfilBean> chamada = new ChamadaWebService<>(WSMessages.WS_UG_ADICIONAR_PERFIL, token, new ChamadaWebService.Servico<UsuarioPerfilBean>() {
			public String executar(UsuarioPerfilBean parametro) throws DAOException {
				return BeanManager.INSTANCE.getReference(PerfilRestService.class).adicionarPerfil(bean);			}
		});
		return chamada.executar(bean);
	}
	
}
