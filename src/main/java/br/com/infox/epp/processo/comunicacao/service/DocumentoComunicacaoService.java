package br.com.infox.epp.processo.comunicacao.service;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jbpm.JbpmContext;
import org.jbpm.graph.exe.ProcessInstance;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.manager.PapelManager;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;
import br.com.infox.epp.documento.manager.ClassificacaoDocumentoManager;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.documento.type.ArbitraryExpressionResolver;
import br.com.infox.epp.documento.type.ExpressionResolverChain;
import br.com.infox.epp.documento.type.ExpressionResolverChain.ExpressionResolverChainBuilder;
import br.com.infox.epp.documento.type.JbpmExpressionResolver;
import br.com.infox.epp.documento.type.SeamExpressionResolver;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.DocumentoModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.DocumentoRespostaComunicacao;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.dao.DocumentoRespostaComunicacaoDAO;
import br.com.infox.epp.processo.comunicacao.manager.ModeloComunicacaoManager;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoComunicacao;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.system.Parametros;
import br.com.infox.ibpm.task.home.VariableTypeResolver;

@Name(DocumentoComunicacaoService.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
@Transactional
public class DocumentoComunicacaoService {
	
	public static final String NAME = "documentoComunicacaoService";
	
	@In
	private ClassificacaoDocumentoManager classificacaoDocumentoManager;
	@In
	private ModeloDocumentoManager modeloDocumentoManager;
	@In
	private DocumentoManager documentoManager;
	@In
	private ModeloComunicacaoManager modeloComunicacaoManager;
	@In
	private PapelManager papelManager;
	@In
	private VariableTypeResolver variableTypeResolver;
	@In("org.jboss.seam.bpm.jbpmContext")
	private JbpmContext jbpmContext;
	@In
	private DocumentoRespostaComunicacaoDAO documentoRespostaComunicacaoDAO;
	@In
	private DocumentoBinManager documentoBinManager;
	@In
	private GenericManager genericManager;
	
	public List<ClassificacaoDocumento> getClassificacoesDocumentoDisponiveisRespostaComunicacao(DestinatarioModeloComunicacao destinatarioModeloComunicacao, boolean isEditor) {
		return classificacaoDocumentoManager.getClassificacoesDocumentoDisponiveisRespostaComunicacao(destinatarioModeloComunicacao, isEditor, Authenticator.getPapelAtual());
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
	
	public String evaluateComunicacao(DestinatarioModeloComunicacao destinatario) {
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
				.and(new JbpmExpressionResolver(variableTypeResolver.getVariableTypeMap(), processInstance.getContextInstance()))
				.and(new SeamExpressionResolver(processInstance)).build();
		return modeloDocumentoManager.evaluateModeloDocumento(modeloDocumento, textoComunicacao, chain);
	}
	
	public void desvincularDocumentoRespostaComunicacao(Documento documento) throws DAOException {
		documentoRespostaComunicacaoDAO.removerDocumentoResposta(documento);
	}
	
	public void vincularDocumentoRespostaComunicacao(Documento documento, Processo comunicacao) throws DAOException {
		DocumentoRespostaComunicacao documentoRespostaComunicacao = new DocumentoRespostaComunicacao();
		documentoRespostaComunicacao.setDocumento(documento);
		documentoRespostaComunicacao.setComunicacao(comunicacao);
		documentoRespostaComunicacaoDAO.persist(documentoRespostaComunicacao);
	}
	
	private Map<String, String> createVariaveis(DestinatarioModeloComunicacao destinatario) {
		Map<String, String> variaveis = new HashMap<>();
		String format = "#'{'{0}'}'";
		
		variaveis.put(MessageFormat.format(format, VariaveisJbpmComunicacao.MEIO_EXPEDICAO), destinatario.getMeioExpedicao().getLabel());
		variaveis.put(MessageFormat.format(format, VariaveisJbpmComunicacao.PRAZO_DESTINATARIO), destinatario.getPrazo() != null ? destinatario.getPrazo().toString() : null);
		variaveis.put(MessageFormat.format(format, VariaveisJbpmComunicacao.NOME_DESTINATARIO), destinatario.getNome());
		return variaveis;
	}

	void gravarDocumentos(ModeloComunicacao modeloComunicacao) throws DAOException {
		Processo processoRaiz = modeloComunicacao.getProcesso().getProcessoRoot();
		if (!modeloComunicacao.isDocumentoBinario()) {
			for (DestinatarioModeloComunicacao destinatario : modeloComunicacao.getDestinatarios()) {
				String textoComunicacaoDestinatario = evaluateComunicacao(destinatario);
				DocumentoBin bin = documentoBinManager.createProcessoDocumentoBin("Comunicação", textoComunicacaoDestinatario);
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

	public DocumentoModeloComunicacao getDocumentoInclusoPorUsuarioInterno(ModeloComunicacao modeloComunicacao) {
		return modeloComunicacaoManager.getDocumentoInclusoPorPapel(papelManager.getIdentificadoresPapeisHerdeiros(Parametros.PAPEL_USUARIO_INTERNO.getValue()), modeloComunicacao);
	}
}
