package br.com.infox.epp.processo.comunicacao.envio.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.access.manager.UsuarioPerfilManager;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.manager.PessoaFisicaManager;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.MeioExpedicao;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.list.ParticipanteProcessoComunicacaoList;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;
import br.com.infox.hibernate.util.HibernateUtil;

@Name(DestinatarioComunicacaoAction.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class DestinatarioComunicacaoAction {
	public static final String NAME = "destinatarioComunicacaoAction";
	private static final LogProvider LOG = Logging.getLogProvider(DestinatarioComunicacaoAction.class);

	@In
	private ParticipanteProcessoComunicacaoList participanteProcessoComunicacaoList;
	@In
	private PessoaFisicaManager pessoaFisicaManager;
	@In
	private UsuarioPerfilManager usuarioPerfilManager;
	@In
	private GenericManager genericManager;
	
	private List<Integer> idsLocalizacoesSelecionadas = new ArrayList<>();
	private Map<Localizacao, List<PerfilTemplate>> perfisSelecionados = new HashMap<>();
	
	private ModeloComunicacao modeloComunicacao;
	private boolean processoPossuiRelator;
	private boolean adicionarDestinatarioRelator;
	private Localizacao localizacao;
	private PerfilTemplate perfilDestino;
	
	void init() {
		initEntityLists();
	}
	
	void persistDestinatarios() throws DAOException {
		for (DestinatarioModeloComunicacao destinatario : modeloComunicacao.getDestinatarios()) {
			if (destinatario.getId() == null) {
				genericManager.persist(destinatario);
			}
		}
	}
	
	void resetEntityState() {
		for (DestinatarioModeloComunicacao dest : modeloComunicacao.getDestinatarios()) {
			if (!genericManager.contains(dest)) {
				dest.setId(null);
			}
		}
	}
	
	public void adicionarDestinatario(ParticipanteProcesso participante) {
		DestinatarioModeloComunicacao destinatario = new DestinatarioModeloComunicacao();
		destinatario.setModeloComunicacao(modeloComunicacao);
		try {
			// Tem que remover o proxy porque o proxy vem como Pessoa. 
			// A query sempre retorna PessoaFisica
			destinatario.setDestinatario(pessoaFisicaManager.merge((PessoaFisica) HibernateUtil.removeProxy(participante.getPessoa())));
			participanteProcessoComunicacaoList.adicionarIdPessoa(destinatario.getDestinatario().getIdPessoa());
			modeloComunicacao.getDestinatarios().add(destinatario);
		} catch (DAOException e) {
			LOG.error("", e);
			FacesMessages.instance().add("Erro ao adicionar destinatário");
		}
	}
	
	public void adicionarDestino(Localizacao localizacao, PerfilTemplate perfilDestino) {
	    if (perfilDestino == null) {
	        if (idsLocalizacoesSelecionadas.contains(localizacao.getIdLocalizacao())) {
	            FacesMessages.instance().add("Localização já adicionada");
	            return;
	        }
	        DestinatarioModeloComunicacao destinatario = new DestinatarioModeloComunicacao();
	        destinatario.setModeloComunicacao(modeloComunicacao);
	        destinatario.setDestino(localizacao);
	        modeloComunicacao.getDestinatarios().add(destinatario);
	        idsLocalizacoesSelecionadas.add(localizacao.getIdLocalizacao());
	    } else {
	        if (hasPerfilSelecionado(localizacao, perfilDestino)) {
	            FacesMessages.instance().add("Perfil já adicionado para esta localização");
	            return;
	        }
	        DestinatarioModeloComunicacao destinatario = new DestinatarioModeloComunicacao();
            destinatario.setModeloComunicacao(modeloComunicacao);
            destinatario.setDestino(localizacao);
            destinatario.setPerfilDestino(perfilDestino);
            modeloComunicacao.getDestinatarios().add(destinatario);
            addPerfilSelecionado(destinatario);
	    }
	}
	
	public void removerDestinatario(DestinatarioModeloComunicacao destinatario) {
		modeloComunicacao.getDestinatarios().remove(destinatario);
		if (destinatario.getDestinatario() != null) {
			participanteProcessoComunicacaoList.removerIdPessoa(destinatario.getDestinatario().getIdPessoa());
			PessoaFisica relator = getRelator();
			if (adicionarDestinatarioRelator && destinatario.getDestinatario().equals(relator)) {
				adicionarDestinatarioRelator = false;
			}
		} else if (destinatario.getPerfilDestino() != null) {
		    removePerfilSelecionado(destinatario);
		} else {
			idsLocalizacoesSelecionadas.remove(destinatario.getDestino().getIdLocalizacao());
		}
	}
	
	public void replicarPrazo(DestinatarioModeloComunicacao destinatario) {
		for (DestinatarioModeloComunicacao dest : modeloComunicacao.getDestinatarios()) {
			dest.setPrazo(destinatario.getPrazo());
		}
	}
	
	public void gerenciarRelator() {
		PessoaFisica relator = getRelator();
		if (adicionarDestinatarioRelator) {
			DestinatarioModeloComunicacao destinatario = new DestinatarioModeloComunicacao();
			destinatario.setDestinatario(relator);
			destinatario.setModeloComunicacao(modeloComunicacao);
			modeloComunicacao.getDestinatarios().add(destinatario);
			participanteProcessoComunicacaoList.adicionarIdPessoa(relator.getIdPessoa());
		} else {
			Iterator<DestinatarioModeloComunicacao> it = modeloComunicacao.getDestinatarios().iterator();
			while (it.hasNext()) {
				DestinatarioModeloComunicacao destinatario = it.next();
				if (destinatario.getDestinatario() != null && destinatario.getDestinatario().equals(relator)) {
					it.remove();
					participanteProcessoComunicacaoList.removerIdPessoa(relator.getIdPessoa());
					break;
				}
			}
		}
	}
	
	public MeioExpedicao[] getMeiosExpedicao(DestinatarioModeloComunicacao destinatario) {
		if (destinatario.getDestinatario() != null) {
			PessoaFisica pessoa = destinatario.getDestinatario();
			if (pessoa.getTermoAdesao() != null) {
				return MeioExpedicao.getValues(true);
			}
		}
		return MeioExpedicao.getValues(false);
	}
	
	public boolean isAdicionarDestinatarioRelator() {
		return adicionarDestinatarioRelator;
	}
	
	public void setAdicionarDestinatarioRelator(boolean adicionarDestinatarioRelator) {
		this.adicionarDestinatarioRelator = adicionarDestinatarioRelator;
	}
	
	public boolean isProcessoPossuiRelator() {
		return processoPossuiRelator;
	}
	
	public List<PerfilTemplate> getPerfisPermitidos() {
		if (modeloComunicacao.getLocalizacaoResponsavelAssinatura() == null) {
			return Collections.emptyList();
		}
		return usuarioPerfilManager.getPerfisPermitidos(modeloComunicacao.getLocalizacaoResponsavelAssinatura());
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
	}
	
	public PerfilTemplate getPerfilDestino() {
	    return perfilDestino;
	}
	
	public void setPerfilDestino(PerfilTemplate perfilDestino) {
	    this.perfilDestino = perfilDestino;
	}
	
	private void initEntityLists() {
		participanteProcessoComunicacaoList.getEntity().setProcesso(modeloComunicacao.getProcesso().getProcessoRoot());
		
		PessoaFisica relator = getRelator();
		processoPossuiRelator = relator != null;
		for (DestinatarioModeloComunicacao destinatario : modeloComunicacao.getDestinatarios()) {
			if (destinatario.getDestinatario() != null) {
				participanteProcessoComunicacaoList.adicionarIdPessoa(destinatario.getDestinatario().getIdPessoa());
				if (relator != null && !adicionarDestinatarioRelator && relator.equals(destinatario.getDestinatario())) {
					adicionarDestinatarioRelator = true;
				}
			} else if (destinatario.getPerfilDestino() != null) {
				addPerfilSelecionado(destinatario);
			} else if (destinatario.getDestino() != null) {
			    idsLocalizacoesSelecionadas.add(destinatario.getDestino().getIdLocalizacao());
			}
		}
	}

	private PessoaFisica getRelator() {
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
	    if (perfisSelecionados.containsKey(destinatario.getDestino())) {
	        perfisSelecionados.remove(destinatario.getPerfilDestino());
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
	
	void setModeloComunicacao(ModeloComunicacao modeloComunicacao) {
		this.modeloComunicacao = modeloComunicacao;
	}
}
