package br.com.infox.epp.ws;

import static br.com.infox.epp.ws.messages.WSMessages.ME_PAPEL_INEXISTENTE;
import static br.com.infox.epp.ws.messages.WSMessages.ME_USUARIO_INEXISTENTE;
import static br.com.infox.epp.ws.messages.WSMessages.MS_SUCESSO_ATUALIZAR;
import static br.com.infox.epp.ws.messages.WSMessages.MS_SUCESSO_INSERIR;
import static br.com.infox.epp.ws.messages.WSMessages.ME_LOCALIZACAO_DA_ESTRUTURA_INEXISTENTE;
import static br.com.infox.epp.ws.messages.WSMessages.ME_LOCALIZACAO_DO_PERFIL_INEXISTENTE;
import static br.com.infox.epp.ws.messages.WSMessages.ME_PERFIL_INEXISTENTE;
import static br.com.infox.epp.ws.messages.WSMessages.ME_USUARIO_SEM_PERFIL_ASSOCIADO;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.access.manager.LocalizacaoManager;
import br.com.infox.epp.access.manager.PapelManager;
import br.com.infox.epp.access.manager.PerfilTemplateManager;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.access.manager.UsuarioPerfilManager;
import br.com.infox.epp.ws.bean.UsuarioPerfilBean;
import br.com.infox.seam.util.ComponentUtil;

public class PerfilRestService {

	private PapelManager papelManager = ComponentUtil.getComponent(PapelManager.NAME);
	private PerfilTemplateManager perfilTemplateManager = ComponentUtil.getComponent(PerfilTemplateManager.NAME);
	private UsuarioLoginManager usuarioLoginManager = ComponentUtil.getComponent(UsuarioLoginManager.NAME);
	private UsuarioPerfilManager usuarioPerfilManager = ComponentUtil.getComponent(UsuarioPerfilManager.NAME);

	private LocalizacaoManager localizacaoManager = ComponentUtil.getComponent(LocalizacaoManager.NAME);
	private ActionMessagesService actionMessagesService = ComponentUtil.getComponent(ActionMessagesService.NAME);

	public String adicionarPerfil(UsuarioPerfilBean bean) throws DAOException {
		UsuarioPerfilBean usuarioPerfilBean = (UsuarioPerfilBean) bean;
		UsuarioLogin usuarioLogin = usuarioLoginManager.getUsuarioFetchPessoaFisicaByNrCpf(usuarioPerfilBean.getCpf());
		if (usuarioLogin == null) {
			return ME_USUARIO_INEXISTENTE.codigo();
		}

		Papel papel = papelManager.getPapelByIdentificador(usuarioPerfilBean.getPapel());
		if (papel == null) {
			return ME_PAPEL_INEXISTENTE.codigo();
		}

		Localizacao localizacaoEstrutura = localizacaoManager
				.getLocalizacaoByCodigo(usuarioPerfilBean.getCodigoLocalizacaoEstrutura());
		if (localizacaoEstrutura == null || localizacaoEstrutura.getEstruturaPai() == null) {
			return ME_LOCALIZACAO_DA_ESTRUTURA_INEXISTENTE.codigo();
		}

		Localizacao localizacaoPerfil = localizacaoManager.getLocalizacaoByCodigo(bean.getCodigoLocalizacaoPerfil());
		if (localizacaoPerfil == null) {
			return ME_LOCALIZACAO_DO_PERFIL_INEXISTENTE.codigo();
		}

		PerfilTemplate perfilTemplate = getPerfilTemplateCreateIfNotExists(localizacaoEstrutura, papel);

		UsuarioPerfil usuarioPerfil = usuarioPerfilManager.getByUsuarioLoginPerfilTemplateLocalizacao(usuarioLogin,
				perfilTemplate, localizacaoPerfil);
		if (usuarioPerfil != null) {
			return MS_SUCESSO_ATUALIZAR.codigo();
		}

		usuarioPerfilManager.persist(new UsuarioPerfil(usuarioLogin, perfilTemplate, localizacaoPerfil));
		if (!usuarioLogin.getAtivo()) {
			usuarioLogin.setAtivo(Boolean.TRUE);
			usuarioLoginManager.update(usuarioLogin);
		}

		return MS_SUCESSO_INSERIR.codigo();
	}

	public String removerPerfil(UsuarioPerfilBean bean) throws DAOException {
		UsuarioPerfilBean usuarioPerfilBean = (UsuarioPerfilBean) bean;
		UsuarioLogin usuarioLogin = usuarioLoginManager.getUsuarioFetchPessoaFisicaByNrCpf(usuarioPerfilBean.getCpf());
		if (usuarioLogin == null) {
			return ME_USUARIO_INEXISTENTE.codigo();
		}

		Papel papel = papelManager.getPapelByIdentificador(usuarioPerfilBean.getPapel());
		if (papel == null) {
			return ME_PAPEL_INEXISTENTE.codigo();
		}

		Localizacao localizacaoEstrutura = localizacaoManager
				.getLocalizacaoByCodigo(usuarioPerfilBean.getCodigoLocalizacaoEstrutura());
		if (localizacaoEstrutura == null || localizacaoEstrutura.getEstruturaPai() == null) {
			return ME_LOCALIZACAO_DA_ESTRUTURA_INEXISTENTE.codigo();
		}

		Localizacao localizacaoPerfil = localizacaoManager.getLocalizacaoByCodigo(bean.getCodigoLocalizacaoPerfil());
		if (localizacaoPerfil == null) {
			return ME_LOCALIZACAO_DO_PERFIL_INEXISTENTE.codigo();
		}

		PerfilTemplate perfilTemplate = perfilTemplateManager.getByLocalizacaoPapel(localizacaoEstrutura, papel);
		if (perfilTemplate == null) {
			return ME_PERFIL_INEXISTENTE.codigo();
		}

		UsuarioPerfil usuarioPerfil = usuarioPerfilManager.getByUsuarioLoginPerfilTemplateLocalizacao(usuarioLogin,
				perfilTemplate, localizacaoPerfil);
		if (usuarioPerfil == null) {
			return ME_USUARIO_SEM_PERFIL_ASSOCIADO.codigo();
		}

		usuarioPerfilManager.removeByUsuarioPerfilTemplateLocalizacao(usuarioLogin, perfilTemplate, localizacaoPerfil);
		return MS_SUCESSO_ATUALIZAR.codigo();
	}

	private PerfilTemplate getPerfilTemplateCreateIfNotExists(Localizacao localizacao, Papel papel)
			throws DAOException {
		PerfilTemplate perfilTemplate = perfilTemplateManager.getByLocalizacaoPapel(localizacao, papel);
		if (perfilTemplate == null) {
			perfilTemplate = perfilTemplateManager.persist(new PerfilTemplate(localizacao, papel));
		}
		return perfilTemplate;
	}

}
