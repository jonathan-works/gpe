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
import org.jbpm.JbpmContext;
import org.jbpm.graph.exe.ProcessInstance;

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
import br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.DocumentoModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.manager.ModeloComunicacaoManager;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoComunicacao;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.system.MetadadoProcessoProvider;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.ibpm.task.home.VariableTypeResolver;

@Name(DocumentoComunicacaoService.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
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
	
	public List<ClassificacaoDocumento> getClassificacoesDocumentoDisponiveisRespostaComunicacao(TipoComunicacao tipoComunicacao, boolean isModelo) {
		return classificacaoDocumentoManager.getClassificacoesDocumentoDisponiveisRespostaComunicacao(tipoComunicacao, isModelo, Authenticator.getPapelAtual());
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
	
	public DocumentoModeloComunicacao getDocumentoInclusoPorUsuarioInterno(ModeloComunicacao modeloComunicacao) {
		return modeloComunicacaoManager.getDocumentoInclusoPorPapel(papelManager.getIdentificadoresPapeisMembros("usuarioInterno"), modeloComunicacao);
	}
	
	public String evaluateComunicacao(DestinatarioModeloComunicacao destinatario) {
		DocumentoBin comunicacao = destinatario.getComunicacao();
		ModeloDocumento modeloDocumento = destinatario.getModeloComunicacao().getModeloDocumento();
		if (modeloDocumento == null) {
			return comunicacao.getModeloDocumento();
		}

		Map<String, String> variaveis = createVariaveis(destinatario);
		ProcessInstance processInstance = jbpmContext.getProcessInstance(destinatario.getModeloComunicacao().getProcesso().getIdJbpm());
		variableTypeResolver.setProcessInstance(processInstance);

		
		ExpressionResolverChain chain = ExpressionResolverChainBuilder.with(new ArbitraryExpressionResolver(variaveis))
				.and(new JbpmExpressionResolver(variableTypeResolver.getVariableTypeMap(), processInstance.getContextInstance()))
				.and(new SeamExpressionResolver()).build();
		return modeloDocumentoManager.evaluateModeloDocumento(modeloDocumento, comunicacao.getModeloDocumento(), chain);
	}
	
	public List<ClassificacaoDocumento> getClassificacoesProrrogacaoPrazo(TipoComunicacao tipoComunicacao) {
		return classificacaoDocumentoManager.getClassificacoesDocumentoProrrogacaoPrazo(tipoComunicacao);
	}
	
	void gravarDocumentos(DestinatarioModeloComunicacao destinatario, Processo processoComunicacao) throws DAOException {
		DocumentoBin comunicacao = destinatario.getComunicacao();
		ModeloComunicacao modeloComunicacao = destinatario.getModeloComunicacao();
		Processo processoRaiz = processoComunicacao.getProcessoRoot();
		Documento documentoComunicacao = documentoManager.createDocumento(processoRaiz, comunicacao.getNomeArquivo(), comunicacao, modeloComunicacao.getClassificacaoComunicacao());
		Pasta pasta = processoRaiz.getMetadado(EppMetadadoProvider.PASTA_DEFAULT).getValue(); 
		processoRaiz.getDocumentoList().add(documentoComunicacao);
		
		MetadadoProcessoProvider metadadoProcessoProvider = new MetadadoProcessoProvider(processoComunicacao);
		processoComunicacao.getMetadadoProcessoList().add(metadadoProcessoProvider
				.gerarMetadado(ComunicacaoMetadadoProvider.COMUNICACAO, documentoComunicacao.getId().toString()));
	}
	
	private Map<String, String> createVariaveis(DestinatarioModeloComunicacao destinatario) {
		Map<String, String> variaveis = new HashMap<>();
		String format = "#'{'{0}'}'";
		
		variaveis.put(MessageFormat.format(format, ComunicacaoService.MEIO_EXPEDICAO), destinatario.getMeioExpedicao().getLabel());
		variaveis.put(MessageFormat.format(format, ComunicacaoService.PRAZO_DESTINATARIO), destinatario.getPrazo() != null ? destinatario.getPrazo().toString() : null);
		variaveis.put(MessageFormat.format(format, ComunicacaoService.NOME_DESTINATARIO), destinatario.getNome());
		return variaveis;
	}
}
