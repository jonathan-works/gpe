package br.com.infox.epp.processo.comunicacao.envio.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.access.manager.UsuarioPerfilManager;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.localizacao.LocalizacaoSearch;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.manager.PessoaFisicaManager;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.MeioExpedicao;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.list.ParticipanteProcessoComunicacaoList;
import br.com.infox.epp.processo.comunicacao.service.DestinatarioComunicacaoService;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoComunicacao;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;
import br.com.infox.epp.usuario.UsuarioLoginSearch;
import br.com.infox.hibernate.util.HibernateUtil;
import br.com.infox.seam.util.ComponentUtil;

@Named(DestinatarioComunicacaoAction.NAME)
@ViewScoped
@Stateful
public class DestinatarioComunicacaoAction implements Serializable{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "destinatarioComunicacaoAction";
	private static final LogProvider LOG = Logging.getLogProvider(DestinatarioComunicacaoAction.class);

	private ParticipanteProcessoComunicacaoList participanteProcessoComunicacaoList = ComponentUtil.getComponent(ParticipanteProcessoComunicacaoList.NAME);
	
	@Inject
	private PessoaFisicaManager pessoaFisicaManager;
	@Inject
	private UsuarioPerfilManager usuarioPerfilManager;
	@Inject
	private DestinatarioComunicacaoService destinatarioComunicacaoService;
	@Inject
	private LocalizacaoSearch localizacaoSearch;
	@Inject
	private UsuarioLoginSearch usuarioLoginSearch;
	
	private Localizacao localizacaoRaizComunicacao;
	private Integer prazoDefaultTarefa;
	private PerfilTemplate perfilAssinatura;
	private List<Integer> idsLocalizacoesSelecionadas = new ArrayList<>();
	private Map<Localizacao, List<PerfilTemplate>> perfisSelecionados = new HashMap<>();
	private List<DestinatarioModeloComunicacao> destinatariosExcluidos = new ArrayList<>();
	
	private ModeloComunicacao modeloComunicacao;
	private boolean processoPossuiRelator;
	private Localizacao localizacao;
	private PerfilTemplate perfilDestino;
	private boolean existeUsuarioNoDestino = true;
	
	public void init(Localizacao localizacaoRaizComunicacao, Integer prazoDefaultTarefa, PerfilTemplate perfilAssinatura) {
		initEntityLists();
		this.localizacaoRaizComunicacao = localizacaoRaizComunicacao;
		this.prazoDefaultTarefa = prazoDefaultTarefa;
		this.perfilAssinatura = perfilAssinatura;
	}
	
	@Remove
	public void destroy() {}
	
	//TODO ver como colocar esse método no service
	public void persistDestinatarios() throws DAOException {
		destinatarioComunicacaoService.removeDestinatariosModeloComunicacaoList(destinatariosExcluidos);
		destinatarioComunicacaoService.gravaDestinatariosModeloComunicacaoList(modeloComunicacao.getDestinatarios());
	}
	
	public void resetEntityState() {
		for (DestinatarioModeloComunicacao dest : modeloComunicacao.getDestinatarios()) {
			dest.setId(null);
		}
	}
	
	public void adicionarDestinatario(ParticipanteProcesso participante) {
		DestinatarioModeloComunicacao destinatario = new DestinatarioModeloComunicacao();
		destinatario.setModeloComunicacao(modeloComunicacao);
		try {
			// Tem que remover o proxy porque o proxy vem como Pessoa. 
			// A query sempre retorna PessoaFisica
			destinatario.setDestinatario(pessoaFisicaManager.merge((PessoaFisica) HibernateUtil.removeProxy(participante.getPessoa())));
			destinatario.setPrazo(getPrazoDefaultByTipoComunicacao(modeloComunicacao.getTipoComunicacao()));
			destinatario.setTipoParte(participante.getTipoParte());
			participanteProcessoComunicacaoList.adicionarIdPessoa(destinatario.getDestinatario().getIdPessoa());
			modeloComunicacao.getDestinatarios().add(destinatario);
		} catch (DAOException e) {
			LOG.error("", e);
			FacesMessages.instance().add("Erro ao adicionar destinatário");
		}
	}
	
	public boolean existeUsuarioDestino() {
		return existeUsuarioNoDestino;
	}
	
	public void adicionarDestino() {
		DestinatarioModeloComunicacao destinatario = new DestinatarioModeloComunicacao();
        destinatario.setModeloComunicacao(modeloComunicacao);
        destinatario.setDestino(localizacao);
        destinatario.setPrazo(getPrazoDefaultByTipoComunicacao(modeloComunicacao.getTipoComunicacao()));
		
		if (perfilDestino == null) {
	        if (idsLocalizacoesSelecionadas.contains(localizacao.getIdLocalizacao())) {
	            FacesMessages.instance().add("Localização já adicionada");
	            return;
	        }
	        modeloComunicacao.getDestinatarios().add(destinatario);
	        idsLocalizacoesSelecionadas.add(localizacao.getIdLocalizacao());
	    } else {
	        if (hasPerfilSelecionado(localizacao, perfilDestino)) {
	            FacesMessages.instance().add("Perfil já adicionado para esta localização");
	            return;
	        }
            destinatario.setPerfilDestino(perfilDestino);
            modeloComunicacao.getDestinatarios().add(destinatario);
            addPerfilSelecionado(destinatario);
	    }
	}

	protected Integer getPrazoDefaultByTipoComunicacao(TipoComunicacao tipoComunicacao) { 
		return prazoDefaultTarefa;
	}
	
	public void removerDestinatario(DestinatarioModeloComunicacao destinatario) {
		destinatariosExcluidos.add(destinatario);
		modeloComunicacao.getDestinatarios().remove(destinatario);
		if (destinatario.getDestinatario() != null) {
			participanteProcessoComunicacaoList.removerIdPessoa(destinatario.getDestinatario().getIdPessoa());
			PessoaFisica relator = getRelator();
			if (modeloComunicacao.getEnviarRelatoria() && destinatario.getDestinatario().equals(relator)) {
				modeloComunicacao.setEnviarRelatoria(false);
			}
		} else if (destinatario.getPerfilDestino() != null) {
		    removePerfilSelecionado(destinatario);
		} else {
			idsLocalizacoesSelecionadas.remove(destinatario.getDestino().getIdLocalizacao());
		}
	}
	
	public void excluirDestinatario (DestinatarioModeloComunicacao destinatarioModeloComunicacao) throws DAOException {
		removerDestinatario(destinatarioModeloComunicacao);
		destinatarioComunicacaoService.removeDestinatarioModeloComunicacao(destinatarioModeloComunicacao);
	}
	
	public void replicarPrazo(DestinatarioModeloComunicacao destinatario) {
		for (DestinatarioModeloComunicacao dest : modeloComunicacao.getDestinatarios()) {
			dest.setPrazo(destinatario.getPrazo());
		}
	}
	
	@Deprecated
	public void gerenciarRelator() { //FIXME Método subistituido pelo adicionar relatoria após alteração de usabilidade. Verificar clientes para excluir.
		PessoaFisica relator = getRelator();
		if (modeloComunicacao.getEnviarRelatoria()) {
			if (!isPessoaFisicaNaListaDestinatarios(relator)) {
				DestinatarioModeloComunicacao destinatario = new DestinatarioModeloComunicacao();
				destinatario.setDestinatario(relator);
				destinatario.setModeloComunicacao(modeloComunicacao);
				destinatario.setPrazo(getPrazoDefaultByTipoComunicacao(modeloComunicacao.getTipoComunicacao()));
				modeloComunicacao.getDestinatarios().add(destinatario);
				participanteProcessoComunicacaoList.adicionarIdPessoa(relator.getIdPessoa());
			}
		} else {
			DestinatarioModeloComunicacao destinatarioRelator = null;
			for (DestinatarioModeloComunicacao destinatarioModeloComunicacao : modeloComunicacao.getDestinatarios()) {
				if (destinatarioModeloComunicacao.getDestinatario() != null && destinatarioModeloComunicacao.getDestinatario().equals(relator)) {
					destinatarioRelator = destinatarioModeloComunicacao;
					break;
				}
			}
			removerDestinatario(destinatarioRelator);
		}
	}

    public void adicionarRelatoria() {
        modeloComunicacao.setEnviarRelatoria(true);
        PessoaFisica relator = getRelator();
        if (!isPessoaFisicaNaListaDestinatarios(relator)) {
            DestinatarioModeloComunicacao destinatario = new DestinatarioModeloComunicacao();
            destinatario.setDestinatario(relator);
            destinatario.setModeloComunicacao(modeloComunicacao);
            destinatario.setPrazo(getPrazoDefaultByTipoComunicacao(modeloComunicacao.getTipoComunicacao()));
            modeloComunicacao.getDestinatarios().add(destinatario);
            participanteProcessoComunicacaoList.adicionarIdPessoa(relator.getIdPessoa());
        }
    }

	private boolean isPessoaFisicaNaListaDestinatarios(PessoaFisica relator) {
		for (DestinatarioModeloComunicacao destinatario : modeloComunicacao.getDestinatarios()) {
			if (destinatario.getDestinatario() != null && destinatario.getDestinatario().equals(relator)) {
				return true;
			}
		}
		return false;
	}

	public List<MeioExpedicao> getMeiosExpedicao(DestinatarioModeloComunicacao destinatario) {
		return destinatarioComunicacaoService.getMeiosExpedicao(destinatario);
	}
	
	public boolean isProcessoPossuiRelator() { 
		return processoPossuiRelator;
	}
	
	public List<Localizacao> getLocalizacoesDisponiveis(String query) {
		if (localizacaoRaizComunicacao != null) {
			return localizacaoSearch.getLocalizacoesByRaizWithDescricaoLike(localizacaoRaizComunicacao, query, EnvioComunicacaoController.MAX_RESULTS);
		}
		return Collections.emptyList();
	}
	
	public List<PerfilTemplate> getPerfisPermitidos() {
	    if (modeloComunicacao.getLocalizacaoResponsavelAssinatura() == null) {
			return Collections.emptyList();
		}
		List<PerfilTemplate> perfisPermitidos = usuarioPerfilManager.getPerfisPermitidos(modeloComunicacao.getLocalizacaoResponsavelAssinatura());
		if (modeloComunicacao.getPerfilResponsavelAssinatura() != null && !perfisPermitidos.contains(modeloComunicacao.getPerfilResponsavelAssinatura())) {
		    modeloComunicacao.setPerfilResponsavelAssinatura(null);
		} else {
		    modeloComunicacao.setPerfilResponsavelAssinatura(perfilAssinatura);
		}
		return perfisPermitidos;
	}
	
	public List<PerfilTemplate> getPerfisPermitidosDestino() {
	    if (localizacao == null) {
	        return Collections.emptyList();
	    }
	    return usuarioPerfilManager.getPerfisPermitidos(localizacao);
	}
	
	public Localizacao getLocalizacao() {
		return localizacao;
	}
	
	public void setLocalizacao(Localizacao localizacao) {
		this.localizacao = localizacao;
		setPerfilDestino(null);
	}
	
	public PerfilTemplate getPerfilDestino() {
	    return perfilDestino;
	}
	
	public void setPerfilDestino(PerfilTemplate perfilDestino) {
	    this.perfilDestino = perfilDestino;
		existeUsuarioNoDestino = usuarioLoginSearch.existsUsuarioWithLocalizacaoPerfil(localizacao, perfilDestino);
	}
	
	private void initEntityLists() {
		participanteProcessoComunicacaoList.getEntity().setProcesso(modeloComunicacao.getProcesso().getProcessoRoot());
		participanteProcessoComunicacaoList.clearIdPessoa();
		destinatariosExcluidos = new ArrayList<>();
		PessoaFisica relator = getRelator();
		processoPossuiRelator = relator != null;
		perfisSelecionados =  new HashMap<>();
		for (DestinatarioModeloComunicacao destinatario : modeloComunicacao.getDestinatarios()) {
			if (destinatario.getDestinatario() != null) {
				participanteProcessoComunicacaoList.adicionarIdPessoa(destinatario.getDestinatario().getIdPessoa());
				if (relator != null && !modeloComunicacao.getEnviarRelatoria() && relator.equals(destinatario.getDestinatario())) {
					modeloComunicacao.setEnviarRelatoria(false);
				}
			} else if (destinatario.getPerfilDestino() != null) {
				addPerfilSelecionado(destinatario);
			} else if (destinatario.getDestino() != null) {
			    idsLocalizacoesSelecionadas.add(destinatario.getDestino().getIdLocalizacao());
			}
		}
	}

	public PessoaFisica getRelator() {
		MetadadoProcesso metadadoProcesso = modeloComunicacao.getProcesso().getProcessoRoot().getMetadado(EppMetadadoProvider.RELATOR);
		return (PessoaFisica) (metadadoProcesso != null ? metadadoProcesso.getValue() : null);
	}
	
	/**
	 * Adiciona um perfil na lista de perfis de determinada localização guardados no cache
	 * perfisSelecionados
	 */
	private void addPerfilSelecionado(DestinatarioModeloComunicacao destinatario) {
	    if (perfisSelecionados.containsKey(destinatario.getDestino())) {
	        perfisSelecionados.get(destinatario.getDestino()).add(destinatario.getPerfilDestino());
	    } else {
	        List<PerfilTemplate> perfis = new ArrayList<>();
	        perfis.add(destinatario.getPerfilDestino());
	        perfisSelecionados.put(destinatario.getDestino(), perfis);
	    }
	}
	
	private void removePerfilSelecionado(DestinatarioModeloComunicacao destinatario) {
	    if (perfisSelecionados.containsKey(destinatario.getDestino()) && perfisSelecionados.get(destinatario.getDestino()).contains(destinatario.getPerfilDestino())) {
	    	perfisSelecionados.get(destinatario.getDestino()).remove(destinatario.getPerfilDestino());
	    }
	}
	
	/**
	 * @param localizacao
	 * @param perfil
	 * @return True, se o perfil já tiver sido escolhido para esta localização, False caso contrário 
	 */
	private Boolean hasPerfilSelecionado(Localizacao localizacao, PerfilTemplate perfil) {
	    boolean resp = false;
	    if (!perfisSelecionados.containsKey(localizacao)) {
	        resp = false;
	    } else {
	        resp = perfisSelecionados.get(localizacao).contains(perfil);
	    }
	    return resp;
	}
	
	public void setModeloComunicacao(ModeloComunicacao modeloComunicacao) {
		this.modeloComunicacao = modeloComunicacao;
	}

	public ModeloComunicacao getModeloComunicacao() {
		return modeloComunicacao;
	}
	
}
