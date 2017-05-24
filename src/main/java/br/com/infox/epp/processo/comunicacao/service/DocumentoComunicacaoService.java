package br.com.infox.epp.processo.comunicacao.service;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.jboss.com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;
import org.jbpm.JbpmContext;
import org.jbpm.graph.exe.ProcessInstance;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.core.manager.GenericManager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.access.entity.PerfilTemplate_;
import br.com.infox.epp.access.manager.PapelManager;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;
import br.com.infox.epp.documento.manager.ClassificacaoDocumentoManager;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.documento.type.ArbitraryExpressionResolver;
import br.com.infox.epp.documento.type.ExpressionResolverChain;
import br.com.infox.epp.documento.type.ExpressionResolverChain.ExpressionResolverChainBuilder;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.DocumentoModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.DocumentoRespostaComunicacao;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.dao.DocumentoRespostaComunicacaoDAO;
import br.com.infox.epp.processo.comunicacao.manager.ModeloComunicacaoManager;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoComunicacao;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.entity.Documento_;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.marcador.MarcadorService;
import br.com.infox.epp.system.Parametros;
import br.com.infox.ibpm.task.home.VariableTypeResolver;
import br.com.infox.seam.util.ComponentUtil;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class DocumentoComunicacaoService {

	@Inject
	private ModeloDocumentoManager modeloDocumentoManager;
	@Inject
	private DocumentoManager documentoManager;
	@Inject
	private ModeloComunicacaoManager modeloComunicacaoManager;
	@Inject
	private DocumentoBinManager documentoBinManager;
	@Inject
	private PrazoComunicacaoService prazoComunicacaoService;
	@Inject
	private GenericManager genericManager;
	@Inject
	private MarcadorService marcadorService;
	
	private PapelManager papelManager = ComponentUtil.getComponent(PapelManager.NAME);
	private ClassificacaoDocumentoManager classificacaoDocumentoManager = ComponentUtil.getComponent(ClassificacaoDocumentoManager.NAME);
	private VariableTypeResolver variableTypeResolver = ComponentUtil.getComponent(VariableTypeResolver.NAME);
	private DocumentoRespostaComunicacaoDAO documentoRespostaComunicacaoDAO = ComponentUtil.getComponent(DocumentoRespostaComunicacaoDAO.NAME);
	
	public List<ClassificacaoDocumento> getClassificacoesDocumentoDisponiveisRespostaComunicacao(DestinatarioModeloComunicacao destinatarioModeloComunicacao, boolean isEditor) {
		List<ClassificacaoDocumento> classificacaoesResposta = classificacaoDocumentoManager.getClassificacoesDocumentoDisponiveisRespostaComunicacao(destinatarioModeloComunicacao, isEditor, Authenticator.getPapelAtual());
		if (prazoComunicacaoService.canRequestProrrogacaoPrazo(destinatarioModeloComunicacao)) {
			ClassificacaoDocumento classificacaoPorrogacao = destinatarioModeloComunicacao.getModeloComunicacao().getTipoComunicacao().getClassificacaoProrrogacao();
			if (!classificacaoesResposta.contains(classificacaoPorrogacao))
				classificacaoesResposta.add(classificacaoPorrogacao);
		}
		return classificacaoesResposta;
	}
	
	public List<ModeloDocumento> getModelosDocumentoDisponiveisComunicacao(TipoComunicacao tipoComunicacao) {
		if (tipoComunicacao == null || tipoComunicacao.getTipoModeloDocumento() == null) {
			return modeloDocumentoManager.getModeloDocumentoList();
		} else {
			TipoModeloDocumento tipoModeloDocumento = tipoComunicacao.getTipoModeloDocumento();
			return modeloDocumentoManager.getModeloDocumentoByGrupoAndTipo(tipoModeloDocumento.getGrupoModeloDocumento(), tipoModeloDocumento);
		}
	}
	
	public List<ClassificacaoDocumento> getClassificacoesDocumentoDisponiveisComunicacao(TipoComunicacao tipoComunicacao) {
		if (tipoComunicacao == null || tipoComunicacao.getClassificacaoDocumento() == null) {
			return classificacaoDocumentoManager.getUseableClassificacaoDocumento(true, Authenticator.getPapelAtual());
		} else {
			return Arrays.asList(tipoComunicacao.getClassificacaoDocumento());
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String evaluateComunicacao(DestinatarioModeloComunicacao destinatario) {
		JbpmContext jbpmContext = ComponentUtil.getComponent("org.jboss.seam.bpm.jbpmContext");
		ModeloComunicacao modeloComunicacao = destinatario.getModeloComunicacao();
		String textoComunicacao = modeloComunicacao.getTextoComunicacao();
		ModeloDocumento modeloDocumento = modeloComunicacao.getModeloDocumento();
		if (modeloDocumento == null) {
			return textoComunicacao;
		}

		Map<String, String> variaveis = createVariaveis(destinatario);
		ProcessInstance processInstance = jbpmContext.getProcessInstance(modeloComunicacao.getProcesso().getIdJbpm());
		variableTypeResolver.setProcessInstance(processInstance);
		
		ExpressionResolverChain chain = ExpressionResolverChainBuilder.with(new ArbitraryExpressionResolver(variaveis))
				.and(ExpressionResolverChainBuilder.defaultExpressionResolverChain(modeloComunicacao.getProcesso().getIdProcesso(), processInstance))
				.build();
		return modeloDocumentoManager.evaluateModeloDocumento(modeloDocumento, textoComunicacao, chain);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void desvincularDocumentoRespostaComunicacao(Documento documento) throws DAOException {
		documentoRespostaComunicacaoDAO.removerDocumentoResposta(documento);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void vincularDocumentoRespostaComunicacao(Documento documento, Processo comunicacao) throws DAOException {
		DocumentoRespostaComunicacao documentoRespostaComunicacao = new DocumentoRespostaComunicacao();
		documentoRespostaComunicacao.setDocumento(documento);
		documentoRespostaComunicacao.setComunicacao(comunicacao);
		documentoRespostaComunicacaoDAO.persist(documentoRespostaComunicacao);
	}
	
	private Map<String, String> createVariaveis(DestinatarioModeloComunicacao destinatario) {
		Map<String, String> variaveis = new HashMap<>();
		String format = "#'{'{0}'}'";
		
		variaveis.put(MessageFormat.format(format, VariaveisJbpmComunicacao.MEIO_EXPEDICAO), destinatario.getMeioExpedicao().getDescricao());
		variaveis.put(MessageFormat.format(format, VariaveisJbpmComunicacao.PRAZO_DESTINATARIO), destinatario.getPrazo() != null ? destinatario.getPrazo().toString() : null);
		variaveis.put(MessageFormat.format(format, VariaveisJbpmComunicacao.NOME_DESTINATARIO), destinatario.getNome());
		variaveis.put(MessageFormat.format(format, VariaveisJbpmComunicacao.TIPO_PARTICIPANTE_COMUNICACAO), destinatario.getTipoParte() == null ? "" : destinatario.getTipoParte().getDescricao());
		return variaveis;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void gravarDocumentos(ModeloComunicacao modeloComunicacao) throws DAOException {
		Processo processoRaiz = modeloComunicacao.getProcesso().getProcessoRoot();
		if (!modeloComunicacao.isDocumentoBinario()) {
			for (DestinatarioModeloComunicacao destinatario : modeloComunicacao.getDestinatarios()) {
				String textoComunicacaoDestinatario = evaluateComunicacao(destinatario);
				DocumentoBin bin = documentoBinManager.createProcessoDocumentoBin("Comunicação", textoComunicacaoDestinatario);
				marcadorService.createMarcadores(bin, modeloComunicacao.getCodigosMarcadores(), processoRaiz);
				Documento documentoComunicacao = documentoManager.createDocumento(processoRaiz, "Comunicação", bin, modeloComunicacao.getClassificacaoComunicacao());
				destinatario.setDocumentoComunicacao(documentoComunicacao);
				genericManager.update(destinatario);
			}
		} else {
			DocumentoModeloComunicacao documentoModeloComunicacao = getDocumentoInclusoPorUsuarioInterno(modeloComunicacao);
			Documento documentoComunicacao = documentoModeloComunicacao.getDocumento();
			for (DestinatarioModeloComunicacao destinatario : modeloComunicacao.getDestinatarios()) {
				destinatario.setDocumentoComunicacao(documentoComunicacao);
				genericManager.update(destinatario);
			}
			modeloComunicacao.getDocumentos().remove(documentoModeloComunicacao);
			genericManager.remove(documentoModeloComunicacao);
		}
	}

	public boolean hasDocumentoInclusoPorUsuarioInterno(List<Documento> documentos) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Documento> from = cq.from(Documento.class);
		cq.select(cb.count(from.get(Documento_.id)));
		Join<Documento, PerfilTemplate> perfil = from.join(Documento_.perfilTemplate,JoinType.INNER);
		cq.where(perfil.get(PerfilTemplate_.codigo).in(papelManager.getIdentificadoresPapeisHerdeiros(Parametros.PAPEL_USUARIO_INTERNO.getValue())),
				from.in(documentos));
		return getEntityManager().createQuery(cq).getSingleResult() > 0L;
		
	}
	public DocumentoModeloComunicacao getDocumentoInclusoPorUsuarioInterno(ModeloComunicacao modeloComunicacao) {
		return modeloComunicacaoManager.getDocumentoInclusoPorPapel(papelManager.getIdentificadoresPapeisHerdeiros(Parametros.PAPEL_USUARIO_INTERNO.getValue()), modeloComunicacao);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void persistDocumentos(List<DocumentoModeloComunicacao> documentos) throws DAOException{
		for (DocumentoModeloComunicacao documento : documentos) {
			if (documento.getId() == null) {
				genericManager.persist(documento);
			}
		}
	}
	
	public EntityManager getEntityManager(){
		return EntityManagerProducer.getEntityManager();
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void removerDocumento(DocumentoModeloComunicacao documentoModelo) throws DAOException{
		if(documentoModelo.getId() != null){
			genericManager.remove(documentoModelo);
		}
	}
	
}
