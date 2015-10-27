package br.com.infox.epp.ws.services;

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
import br.com.infox.epp.ws.exception.ValidacaoException;
import br.com.infox.epp.ws.interceptors.Log;
import br.com.infox.epp.ws.interceptors.TokenAuthentication;
import br.com.infox.epp.ws.interceptors.ValidarParametros;
import br.com.infox.epp.ws.messages.CodigosServicos;

@Stateless
@TokenAuthentication
@ValidarParametros
public class PerfilRestService {

	@Inject
	private PerfilTemplateManager perfilTemplateManager;
	@Inject
	private UsuarioLoginManager usuarioLoginManager;
	@Inject
	private UsuarioPerfilManager usuarioPerfilManager;
	@Inject
	private LocalizacaoManager localizacaoManager;
	
	@Log(codigo=CodigosServicos.WS_PERFIS_ADICIONAR_PERFIL)
	public String adicionarPerfil(UsuarioPerfilBean bean) throws DAOException {
		UsuarioPerfilBean usuarioPerfilBean = (UsuarioPerfilBean) bean;
		UsuarioLogin usuarioLogin = usuarioLoginManager.getUsuarioFetchPessoaFisicaByNrCpf(usuarioPerfilBean.getCpf());
		if (usuarioLogin == null) {
			throw new ValidacaoException(ME_USUARIO_INEXISTENTE);
		}

		Localizacao localizacao = localizacaoManager.getLocalizacaoByCodigo(usuarioPerfilBean.getCodigoLocalizacao());
		if (localizacao == null || localizacao.getEstruturaFilho() == null) {
			throw new ValidacaoException(ME_LOCALIZACAO_DA_ESTRUTURA_INEXISTENTE);
		}

		PerfilTemplate perfilTemplate = perfilTemplateManager.getPerfilTemplateByLocalizacaoPaiDescricao(localizacao, bean.getPerfil());
		if(perfilTemplate == null) {
			throw new ValidacaoException(ME_PERFIL_INEXISTENTE);
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
