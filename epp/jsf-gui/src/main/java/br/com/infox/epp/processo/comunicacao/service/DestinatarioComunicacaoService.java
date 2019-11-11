package br.com.infox.epp.processo.comunicacao.service;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.access.manager.LocalizacaoManager;
import br.com.infox.epp.access.manager.PapelManager;
import br.com.infox.epp.access.manager.UsuarioPerfilManager;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.meioexpedicao.MeioExpedicao;
import br.com.infox.epp.processo.comunicacao.meioexpedicao.MeioExpedicaoSearch;
import br.com.infox.epp.system.Parametros;
import br.com.infox.seam.util.ComponentUtil;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class DestinatarioComunicacaoService implements Serializable{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "destinatarioComunicacaoService";
	
	@Inject
	private UsuarioPerfilManager usuarioPerfilManager;
	@Inject
	private LocalizacaoManager localizacaoManager;
	@Inject
	private MeioExpedicaoSearch meioExpedicaoSearch;
	@Inject
	private PapelManager papelManager;
	
	private GenericManager genericManager = ComponentUtil.getComponent(GenericManager.NAME);
	
	private String raizLocalizacoesComunicacao = Parametros.RAIZ_LOCALIZACOES_COMUNICACAO.getValue();
	
	public List<MeioExpedicao> getMeiosExpedicao(DestinatarioModeloComunicacao destinatario) {
		if (destinatario.getDestinatario() != null) {
			PessoaFisica pessoa = destinatario.getDestinatario();
			UsuarioLogin usuario = pessoa.getUsuarioLogin();
			if (pessoa.getTermoAdesao() != null) {
				return meioExpedicaoSearch.getMeiosExpedicaoAtivos();
			}
			if (usuario != null) {
				List<UsuarioPerfil> usuarioPerfilList = usuarioPerfilManager.listByUsuarioLogin(usuario);
				List<String> papeisHerdeirosUsuarioInterno = papelManager.getIdentificadoresPapeisHerdeiros(Parametros.PAPEL_USUARIO_INTERNO.getValue());
				for (UsuarioPerfil usuarioPerfil : usuarioPerfilList) {
					Papel papel = usuarioPerfil.getPerfilTemplate().getPapel();
					if (papeisHerdeirosUsuarioInterno.contains(papel.getIdentificador())) {
					    return meioExpedicaoSearch.getMeiosExpedicaoAtivos();
					}
				}
			}
		} else {
			Localizacao localizacaoRaiz = localizacaoManager.getLocalizacaoByNome(raizLocalizacoesComunicacao);
			if (destinatario.getDestino().getCaminhoCompleto().startsWith(localizacaoRaiz.getCaminhoCompleto())) {
			    return meioExpedicaoSearch.getMeiosExpedicaoAtivos();
			}
		}
		
		return meioExpedicaoSearch.getMeiosExpedicaoAtivosNaoEletronicos();
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void removeDestinatarioModeloComunicacao(DestinatarioModeloComunicacao destinatarioModeloComunicacao) throws DAOException {
		if(destinatarioModeloComunicacao.getId() != null){
			genericManager.remove(destinatarioModeloComunicacao);
		}
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void gravaDestinatariosModeloComunicacaoList(List<DestinatarioModeloComunicacao> destinatarios) throws DAOException {
		for (DestinatarioModeloComunicacao destinatario : destinatarios) {
			if (destinatario.getId() == null) {
				genericManager.persist(destinatario);
			}
		} 
	}
	
	public void removeDestinatariosModeloComunicacaoList(List<DestinatarioModeloComunicacao> destinatarios) throws DAOException {
		for (DestinatarioModeloComunicacao destinatarioModeloComunicacao : destinatarios) {
			removeDestinatarioModeloComunicacao(destinatarioModeloComunicacao);
		}
	}
}
