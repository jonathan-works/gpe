package br.com.infox.epp.ws;

import java.io.Serializable;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.ws.bean.UsuarioBean;
import br.com.infox.epp.ws.bean.UsuarioSenhaBean;
import br.com.infox.epp.ws.messages.WSMessages;

@Path(UsuarioRest.PATH)
public class UsuarioRest implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String PATH = "/usuario";
	public static final String PATH_GRAVAR_USUARIO = "/gravar";
	public static final String PATH_ATUALIZAR_SENHA = "/atualizarSenha";


	@POST
	@Path(PATH_GRAVAR_USUARIO)
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public String gravarUsuario(@HeaderParam("token") String token, final UsuarioBean bean) throws DAOException {
		ChamadaWebService<UsuarioBean> chamada = new ChamadaWebService<>(WSMessages.WS_UG_ADICIONAR_PERFIL, token, new ChamadaWebService.Servico<UsuarioBean>() {
			public String executar(UsuarioBean parametro) throws DAOException {
				return BeanManager.INSTANCE.getReference(UsuarioRestService.class).gravarUsuario(bean);
			}
		});
		return chamada.executar(bean);
	}

	@POST
	@Path(PATH_ATUALIZAR_SENHA)
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public String atualizarSenha(@HeaderParam("token") String token,final UsuarioSenhaBean bean) throws DAOException {
		ChamadaWebService<UsuarioSenhaBean> chamada = new ChamadaWebService<>(WSMessages.WS_UG_ADICIONAR_PERFIL, token, new ChamadaWebService.Servico<UsuarioSenhaBean>() {
			public String executar(UsuarioSenhaBean parametro) throws DAOException {
				return BeanManager.INSTANCE.getReference(UsuarioRestService.class).atualizarSenha(bean);
			}
		});
		return chamada.executar(bean);
	}
}
