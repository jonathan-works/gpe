package br.com.infox.epp.ws;

import static br.com.infox.epp.ws.messages.WSMessages.ME_LOCALIZACAO_DA_ESTRUTURA_INEXISTENTE;
import static br.com.infox.epp.ws.messages.WSMessages.ME_PERFIL_INEXISTENTE;
import static br.com.infox.epp.ws.messages.WSMessages.ME_USUARIO_INEXISTENTE;
import static br.com.infox.epp.ws.messages.WSMessages.MS_SUCESSO_ATUALIZAR;
import static br.com.infox.epp.ws.messages.WSMessages.MS_SUCESSO_INSERIR;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.access.manager.LocalizacaoManager;
import br.com.infox.epp.access.manager.PerfilTemplateManager;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.access.manager.UsuarioPerfilManager;
import br.com.infox.epp.ws.bean.UsuarioPerfilBean;

@Stateless
public class PerfilRestService {

	@Inject
	private PerfilTemplateManager perfilTemplateManager;
	@Inject
	private UsuarioLoginManager usuarioLoginManager;
	@Inject
	private UsuarioPerfilManager usuarioPerfilManager;
	@Inject
	private LocalizacaoManager localizacaoManager;
	
	public String adicionarPerfil(UsuarioPerfilBean bean) throws DAOException {
		UsuarioPerfilBean usuarioPerfilBean = (UsuarioPerfilBean) bean;
		UsuarioLogin usuarioLogin = usuarioLoginManager.getUsuarioFetchPessoaFisicaByNrCpf(usuarioPerfilBean.getCpf());
		if (usuarioLogin == null) {
			return ME_USUARIO_INEXISTENTE.codigo();
		}

		Localizacao localizacao = localizacaoManager.getLocalizacaoByCodigo(usuarioPerfilBean.getCodigoLocalizacao());
		if (localizacao == null || localizacao.getEstruturaFilho() == null) {
			return ME_LOCALIZACAO_DA_ESTRUTURA_INEXISTENTE.codigo();
		}

		PerfilTemplate perfilTemplate = perfilTemplateManager.getPerfilTemplateByLocalizacaoPaiDescricao(localizacao, bean.getPerfil());
		if(perfilTemplate == null) {
			return ME_PERFIL_INEXISTENTE.codigo();
		}
		
		UsuarioPerfil usuarioPerfil = usuarioPerfilManager.getByUsuarioLoginPerfilTemplateLocalizacao(usuarioLogin,	perfilTemplate, localizacao);
		if (usuarioPerfil != null) {
			if(!usuarioPerfil.getAtivo()) {
				usuarioPerfil.setAtivo(Boolean.TRUE);
			}
			usuarioPerfilManager.update(usuarioPerfil);
			return MS_SUCESSO_ATUALIZAR.codigo();
		}

		usuarioPerfilManager.persist(new UsuarioPerfil(usuarioLogin, perfilTemplate, localizacao));
		if (!usuarioLogin.getAtivo()) {
			usuarioLogin.setAtivo(Boolean.TRUE);
			usuarioLoginManager.update(usuarioLogin);
		}

		return MS_SUCESSO_INSERIR.codigo();
	}

}
