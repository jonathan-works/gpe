package br.com.infox.ibpm.task.handler;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.manager.ClassificacaoDocumentoManager;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.documento.type.ExpressionResolverChain;
import br.com.infox.epp.documento.type.ExpressionResolverChain.ExpressionResolverChainBuilder;
import br.com.infox.epp.documento.type.JbpmExpressionResolver;
import br.com.infox.epp.documento.type.SeamExpressionResolver;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.documento.manager.PastaManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.system.entity.Parametro;
import br.com.infox.epp.system.manager.ParametroManager;
import br.com.infox.ibpm.task.home.VariableTypeResolver;
import br.com.infox.seam.util.ComponentUtil;

import com.google.gson.Gson;

public class GenerateDocumentoHandler implements ActionHandler, CustomAction {
	private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(GenerateDocumentoHandler.class);
	private static final String PARAMETRO_PASTA_DOCUMENTO_GERADO = "pastaDocumentoGerado";

	private GenerateDocumentoConfiguration configuration;

	public GenerateDocumentoHandler() {
	}
	
	public GenerateDocumentoHandler(String configuration) {
		this.configuration = new Gson().fromJson(parseJbpmConfiguration(configuration), GenerateDocumentoConfiguration.class);
	}
	
	@Override
	public String parseJbpmConfiguration(String configuration) {
		Pattern pattern = Pattern.compile("<!\\[CDATA\\[(.+?)\\]\\]>");
		Matcher matcher = pattern.matcher(configuration);
		if (matcher.find()) {
			configuration = matcher.group(1);
		}
		return configuration;
	}

	@Override
	public void execute(ExecutionContext executionContext) throws Exception {
		DocumentoManager documentoManager = ComponentUtil.getComponent(DocumentoManager.NAME);
		DocumentoBinManager documentoBinManager = ComponentUtil.getComponent(DocumentoBinManager.NAME);
		ModeloDocumentoManager modeloDocumentoManager = ComponentUtil.getComponent(ModeloDocumentoManager.NAME);
		ClassificacaoDocumentoManager classificacaoDocumentoManager = ComponentUtil.getComponent(ClassificacaoDocumentoManager.NAME);
		PastaManager pastaManager = ComponentUtil.getComponent(PastaManager.NAME);
		VariableTypeResolver variableTypeResolver = ComponentUtil.getComponent(VariableTypeResolver.NAME);
		ProcessoManager processoManager = ComponentUtil.getComponent(ProcessoManager.NAME);
		ParametroManager parametroManager = ComponentUtil.getComponent(ParametroManager.NAME);
		ContextInstance contextInstance = executionContext.getContextInstance();
		Processo processo = processoManager.getProcessoEpaByIdJbpm(executionContext.getProcessInstance().getId());
		ClassificacaoDocumento classificacaoDocumento = classificacaoDocumentoManager.find(configuration.idClassificacaoDocumento);
		Parametro parametroNomePastaDocumentoGerado = parametroManager.getParametro(PARAMETRO_PASTA_DOCUMENTO_GERADO);
		try {
			ModeloDocumento modeloDocumento = modeloDocumentoManager.find(configuration.idModeloDocumento);
			ExpressionResolverChain chain = ExpressionResolverChainBuilder.with(new JbpmExpressionResolver(variableTypeResolver.getVariableTypeMap(), contextInstance))
	                .and(new SeamExpressionResolver()).build();
			String texto = modeloDocumentoManager.evaluateModeloDocumento(modeloDocumento, chain);
			DocumentoBin documentoBin = documentoBinManager.createProcessoDocumentoBin(modeloDocumento.getTituloModeloDocumento(), texto);
			Documento documento = documentoManager.createDocumento(processo, modeloDocumento.getTituloModeloDocumento(), documentoBin, classificacaoDocumento);
			 
			if (parametroNomePastaDocumentoGerado != null) {
				Pasta pasta = pastaManager.getPastaByNome(parametroNomePastaDocumentoGerado.getValorVariavel(), processo);
				if (pasta != null) {
					documento.setPasta(pasta);
					documentoManager.update(documento);
				}
			}
		} catch (Exception e) {
			LOG.error(MessageFormat.format("Erro ao gerar documento para o id de modelo de documento: {0}, no processo com id: {1}, nó: {2}", 
					configuration.idModeloDocumento, processo.getIdProcesso(), executionContext.getNode().getName()), e);
		}
	}
	
	public static class GenerateDocumentoConfiguration {
		private Integer idModeloDocumento;
		private Integer idClassificacaoDocumento;
		
		public Integer getIdClassificacaoDocumento() {
			return idClassificacaoDocumento;
		}
		
		public void setIdClassificacaoDocumento(Integer idClassificacaoDocumento) {
			this.idClassificacaoDocumento = idClassificacaoDocumento;
		}
		
		public Integer getIdModeloDocumento() {
			return idModeloDocumento;
		}
		
		public void setIdModeloDocumento(Integer idModeloDocumento) {
			this.idModeloDocumento = idModeloDocumento;
		}
	}
}
