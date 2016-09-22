package br.com.infox.epp.processo.documento.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jbpm.graph.exe.ProcessInstance;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.estatistica.type.SituacaoPrazoEnum;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.epp.fluxo.manager.NaturezaCategoriaFluxoManager;
import br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.service.PrazoComunicacaoService;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.Processo_;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso_;
import br.com.infox.epp.processo.metadado.manager.MetadadoProcessoManager;
import br.com.infox.epp.processo.metadado.system.MetadadoProcessoProvider;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.service.IniciarProcessoService;
import br.com.infox.epp.processo.service.VariaveisJbpmAnaliseDocumento;
import br.com.infox.epp.processo.type.TipoProcesso;
import br.com.infox.epp.system.Parametros;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ProcessoAnaliseDocumentoService {
	
	@Inject
	private NaturezaCategoriaFluxoManager naturezaCategoriaFluxoManager;
	@Inject
	private ProcessoManager processoManager;
	@Inject
	private MetadadoProcessoManager metadadoProcessoManager;
	@Inject
	private FluxoManager fluxoManager;
	@Inject
	private IniciarProcessoService iniciarProcessoService;
	@Inject
	private PrazoComunicacaoService prazoComunicacaoService;
	
	private String codigoFluxoDocumento = Parametros.CODIGO_FLUXO_DOCUMENTO.getValue();

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Processo criarProcessoAnaliseDocumentos(Processo processoPai, Documento... documentoAnalise) throws DAOException {
		Fluxo fluxoDocumento = getFluxoDocumento();
		List<NaturezaCategoriaFluxo> ncfs = naturezaCategoriaFluxoManager.getActiveNaturezaCategoriaFluxoListByFluxo(fluxoDocumento);
		if (ncfs == null || ncfs.isEmpty()) {
			throw new DAOException(InfoxMessages.getInstance().get("fluxo.naoExisteCategoria") + fluxoDocumento.getFluxo());
		}
		
		Processo processoAnalise = new Processo();
		processoAnalise.setNaturezaCategoriaFluxo(ncfs.get(0));
		processoAnalise.setProcessoPai(processoPai);
		processoAnalise.setNumeroProcesso("");
		processoAnalise.setSituacaoPrazo(SituacaoPrazoEnum.SAT);
		processoAnalise.setLocalizacao(Authenticator.getLocalizacaoAtual());
		processoAnalise.setUsuarioCadastro(Authenticator.getUsuarioLogado());
		processoAnalise.setDataInicio(new Date());
		processoManager.persist(processoAnalise);
		
		criarMetadadosProcessoAnalise(processoAnalise, documentoAnalise);
		
		return processoAnalise;
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void inicializarFluxoDocumento(Processo processoAnalise, Map<String, Object> variaveisJbpm) throws DAOException {
		if (variaveisJbpm == null) {
			variaveisJbpm = new HashMap<>();
		} else {
			variaveisJbpm = new HashMap<>(variaveisJbpm);
		}
		variaveisJbpm.put(VariaveisJbpmAnaliseDocumento.RESPOSTA_COMUNICACAO, false);
		variaveisJbpm.put(VariaveisJbpmAnaliseDocumento.PEDIDO_PRORROGACAO_PRAZO, false);
		MetadadoProcesso metadadoTipoProcesso = processoAnalise.getProcessoPai().getMetadado(EppMetadadoProvider.TIPO_PROCESSO);
		if (metadadoTipoProcesso != null) {
			TipoProcesso tipoProcessoPai = metadadoTipoProcesso.getValue();
			if (tipoProcessoPai.equals(TipoProcesso.COMUNICACAO) || tipoProcessoPai.equals(TipoProcesso.COMUNICACAO_NAO_ELETRONICA)) {
				variaveisJbpm.put(VariaveisJbpmAnaliseDocumento.RESPOSTA_COMUNICACAO, true);
				
				List<MetadadoProcesso> metadadoDocumentoList = processoAnalise.getMetadadoList(EppMetadadoProvider.DOCUMENTO_EM_ANALISE);
				List<Documento> documentos = new ArrayList<Documento>(metadadoDocumentoList.size());
				for(MetadadoProcesso metadadoDocumentoAnalise : metadadoDocumentoList){
					Documento documentoAnalise = metadadoDocumentoAnalise.getValue();
					documentos.add(documentoAnalise);
				}
				MetadadoProcesso metadadoDestinatario = processoAnalise.getProcessoPai().getMetadado(ComunicacaoMetadadoProvider.DESTINATARIO);
				DestinatarioModeloComunicacao destinatarioComunicacao = metadadoDestinatario.getValue();
				if(hasPedidoProrrogacaoPrazo(documentos, destinatarioComunicacao)){
					variaveisJbpm.put(VariaveisJbpmAnaliseDocumento.PEDIDO_PRORROGACAO_PRAZO, true);
					variaveisJbpm.putAll(getVariaveisProrrogacaoPrazo(processoAnalise));
				}
			}
		}
		iniciarProcessoService.iniciarProcesso(processoAnalise, variaveisJbpm);
	}

	protected Boolean hasPedidoProrrogacaoPrazo(List<Documento> documentos, DestinatarioModeloComunicacao destinatarioComunicacao) {
		return prazoComunicacaoService.containsClassificacaoProrrogacaoPrazo(documentos, destinatarioComunicacao.getModeloComunicacao().getTipoComunicacao());
	}
	
	protected Map<String, Object> getVariaveisProrrogacaoPrazo(Processo processoAnalise) {
		return new HashMap<>();
	}

	private Fluxo getFluxoDocumento() throws DAOException {
		if (codigoFluxoDocumento == null) {
			throw new DAOException(InfoxMessages.getInstance().get("fluxo.analiseDocumentoNaoEncontrado"));
		}
		Fluxo fluxo = fluxoManager.getFluxoByCodigo(codigoFluxoDocumento);
		if (fluxo == null) {
			throw new DAOException(InfoxMessages.getInstance().get("fluxo.analiseDocumentoNaoEncontrado"));
		}
		return fluxo;
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	private void criarMetadadosProcessoAnalise(Processo processoAnalise, Documento... documentoAnalise) throws DAOException {
		MetadadoProcessoProvider metadadoProcessoProvider = new MetadadoProcessoProvider(processoAnalise);
		MetadadoProcesso metadado = metadadoProcessoProvider.gerarMetadado(EppMetadadoProvider.TIPO_PROCESSO, TipoProcesso.DOCUMENTO.toString());
		metadadoProcessoManager.persist(metadado);
		processoAnalise.getMetadadoProcessoList().add(metadado);
		
		metadado = metadadoProcessoProvider.gerarMetadado(EppMetadadoProvider.LOCALIZACAO_DESTINO, processoAnalise.getProcessoPai().getLocalizacao().getIdLocalizacao().toString());
		metadadoProcessoManager.persist(metadado);
		processoAnalise.getMetadadoProcessoList().add(metadado);
		
		for(Documento documento : documentoAnalise){
			metadado = metadadoProcessoProvider.gerarMetadado(EppMetadadoProvider.DOCUMENTO_EM_ANALISE, documento.getId().toString());
			metadadoProcessoManager.persist(metadado);
			processoAnalise.getMetadadoProcessoList().add(metadado);
		}
		
	}
	
	public List<Documento> getDocumentosRespostaComunicacao(Processo comunicacao){
	    EntityManager entityManager = EntityManagerProducer.getEntityManager();
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<MetadadoProcesso> query = cb.createQuery(MetadadoProcesso.class);
		
		Root<MetadadoProcesso> root = query.from(MetadadoProcesso.class);
		
		
		Predicate predicate = cb.and();
		predicate = cb.and(predicate, cb.equal(root.get(MetadadoProcesso_.metadadoType), EppMetadadoProvider.DOCUMENTO_EM_ANALISE.getMetadadoType()));
		
		Join<MetadadoProcesso, Processo> p = root.join(MetadadoProcesso_.processo);
		predicate =  cb.and(predicate, cb.equal(p.get(Processo_.processoPai), comunicacao));
		
		query.select(root);
		query.where(predicate);
		
		List<MetadadoProcesso> listMetadado = entityManager.createQuery(query).getResultList();
		List<Documento> docList = new ArrayList<Documento>();
		for (MetadadoProcesso metadado : listMetadado) {
			docList.add(metadado.<Documento>getValue());
		}
		
		return docList;
	}

	public List<Documento> getDocumentosAnalise(Processo analiseDocumentos){
	    EntityManager entityManager = EntityManagerProducer.getEntityManager();
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<MetadadoProcesso> query = cb.createQuery(MetadadoProcesso.class);
		
		Root<MetadadoProcesso> root = query.from(MetadadoProcesso.class);
		
		
		Predicate predicate = cb.and();
		predicate = cb.and(predicate, cb.equal(root.get(MetadadoProcesso_.metadadoType), EppMetadadoProvider.DOCUMENTO_EM_ANALISE.getMetadadoType()));
		
		predicate = cb.and(predicate, cb.equal(root.get(MetadadoProcesso_.processo), analiseDocumentos));
		
		query.select(root);
		query.where(predicate);
		List<MetadadoProcesso> listMetadado = entityManager.createQuery(query).getResultList();
		List<Documento> docList = new ArrayList<Documento>();
		for (MetadadoProcesso metadado : listMetadado) {
			docList.add(metadado.<Documento>getValue());
		}

		return docList;
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public boolean isRespostaComunicacao(Processo processoAnalise){
		ProcessInstance processInstance = ManagedJbpmContext.instance().getProcessInstance(processoAnalise.getIdJbpm());
		return (Boolean)processInstance.getContextInstance().getVariable(VariaveisJbpmAnaliseDocumento.RESPOSTA_COMUNICACAO);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public boolean isPedidoProrrogacaoPrazo(Processo processoAnalise){
		ProcessInstance processInstance = ManagedJbpmContext.instance().getProcessInstance(processoAnalise.getIdJbpm());
		return (Boolean)processInstance.getContextInstance().getVariable(VariaveisJbpmAnaliseDocumento.PEDIDO_PRORROGACAO_PRAZO);
	}
}
