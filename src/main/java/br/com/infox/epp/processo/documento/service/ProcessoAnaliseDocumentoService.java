package br.com.infox.epp.processo.documento.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.estatistica.type.SituacaoPrazoEnum;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.epp.fluxo.manager.NaturezaCategoriaFluxoManager;
import br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.service.ProrrogacaoPrazoService;
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
import br.com.infox.epp.processo.type.TipoProcesso;

@Name(ProcessoAnaliseDocumentoService.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
@Transactional
public class ProcessoAnaliseDocumentoService {
	
	public static final String NAME = "processoAnaliseDocumentoService";
	
	@In
	private NaturezaCategoriaFluxoManager naturezaCategoriaFluxoManager;
	@In
	private ProcessoManager processoManager;
	@In
	private MetadadoProcessoManager metadadoProcessoManager;
	@In(required = false)
	private String codigoFluxoDocumento;
	@In
	private FluxoManager fluxoManager;
	@In
	private IniciarProcessoService iniciarProcessoService;
	@In
	private ProrrogacaoPrazoService prorrogacaoPrazoService;
	@In
	private EntityManager entityManager;
	
	public Processo criarProcessoAnaliseDocumentos(Processo processoPai, Documento... documentoAnalise) throws DAOException {
		Fluxo fluxoDocumento = getFluxoDocumento();
		List<NaturezaCategoriaFluxo> ncfs = naturezaCategoriaFluxoManager.getActiveNaturezaCategoriaFluxoListByFluxo(fluxoDocumento);
		if (ncfs == null || ncfs.isEmpty()) {
			throw new DAOException("Não existe Natureza/Categoria para o fluxo " + fluxoDocumento.getFluxo());
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
	
	public void inicializarFluxoDocumento(Processo processoAnalise, Map<String, Object> variaveisJbpm) throws DAOException {
		if (variaveisJbpm == null) {
			variaveisJbpm = new HashMap<>();
		} else {
			variaveisJbpm = new HashMap<>(variaveisJbpm);
		}
		variaveisJbpm.put("respostaComunicacao", false);
		variaveisJbpm.put("pedidoProrrogacaoPrazo", false);
		MetadadoProcesso metadadoTipoProcesso = processoAnalise.getProcessoPai().getMetadado(EppMetadadoProvider.TIPO_PROCESSO);
		if (metadadoTipoProcesso != null) {
			TipoProcesso tipoProcessoPai = metadadoTipoProcesso.getValue();
			if (tipoProcessoPai.equals(TipoProcesso.COMUNICACAO) || tipoProcessoPai.equals(TipoProcesso.COMUNICACAO_NAO_ELETRONICA)) {
				variaveisJbpm.put("respostaComunicacao", true);
				
				List<MetadadoProcesso> metadadoDocumentoList = processoAnalise.getMetadadoList(EppMetadadoProvider.DOCUMENTO_EM_ANALISE);
				List<Documento> documentos = new ArrayList<Documento>(metadadoDocumentoList.size());
				for(MetadadoProcesso metadadoDocumentoAnalise : metadadoDocumentoList){
					Documento documentoAnalise = metadadoDocumentoAnalise.getValue();
					documentos.add(documentoAnalise);
				}
				MetadadoProcesso metadadoDestinatario = processoAnalise.getProcessoPai().getMetadado(ComunicacaoMetadadoProvider.DESTINATARIO);
				DestinatarioModeloComunicacao destinatarioComunicacao = metadadoDestinatario.getValue();
				if(prorrogacaoPrazoService.containsClassificacaoProrrogacaoPrazo(documentos, destinatarioComunicacao.getModeloComunicacao().getTipoComunicacao())){
					variaveisJbpm.put("pedidoProrrogacaoPrazo", true);
				}
			}
		}
		iniciarProcessoService.iniciarProcesso(processoAnalise, variaveisJbpm);
	}
	
	private Fluxo getFluxoDocumento() throws DAOException {
		if (codigoFluxoDocumento == null) {
			throw new DAOException("Fluxo de documento não encontrado");
		}
		Fluxo fluxo = fluxoManager.getFluxoByCodigo(codigoFluxoDocumento);
		if (fluxo == null) {
			throw new DAOException("Fluxo de documento não encontrado");
		}
		return fluxo;
	}
	
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
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<MetadadoProcesso> query = cb.createQuery(MetadadoProcesso.class);
		
		Root<MetadadoProcesso> root = query.from(MetadadoProcesso.class);
		
		
		Predicate predicate = cb.and();
		predicate = cb.and(predicate, cb.equal(root.get(MetadadoProcesso_.metadadoType), "documentoEmAnalise"));
		
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
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<MetadadoProcesso> query = cb.createQuery(MetadadoProcesso.class);
		
		Root<MetadadoProcesso> root = query.from(MetadadoProcesso.class);
		
		
		Predicate predicate = cb.and();
		predicate = cb.and(predicate, cb.equal(root.get(MetadadoProcesso_.metadadoType), "documentoEmAnalise"));
		
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
	
}
