package br.com.infox.ibpm.task.handler;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

import com.google.gson.Gson;

import br.com.infox.epp.cdi.util.Beans;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.manager.ClassificacaoDocumentoManager;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.documento.modelo.ModeloDocumentoSearch;
import br.com.infox.epp.documento.pasta.PastaSearch;
import br.com.infox.epp.documento.type.ExpressionResolverChain;
import br.com.infox.epp.documento.type.SeamExpressionResolver;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.documento.manager.PastaManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.service.VariaveisJbpmProcessosGerais;
import br.com.infox.epp.system.Parametros;
import br.com.infox.seam.util.ComponentUtil;

public class GenerateDocumentoHandler implements ActionHandler, CustomAction {
	private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(GenerateDocumentoHandler.class);

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
		ModeloDocumentoManager modeloDocumentoManager = Beans.getReference(ModeloDocumentoManager.class);
		ModeloDocumentoSearch modeloDocumentoSearch = Beans.getReference(ModeloDocumentoSearch.class);
		ClassificacaoDocumentoManager classificacaoDocumentoManager = ComponentUtil.getComponent(ClassificacaoDocumentoManager.NAME);
		PastaManager pastaManager = ComponentUtil.getComponent(PastaManager.NAME);
		ProcessoManager processoManager = ComponentUtil.getComponent(ProcessoManager.NAME);
		Processo processo = processoManager.find(executionContext.getVariable(VariaveisJbpmProcessosGerais.PROCESSO));
		Processo processoRaiz = processo.getProcessoRoot();
		ClassificacaoDocumento classificacaoDocumento = classificacaoDocumentoManager.findByCodigo(configuration.codigoClassificacaoDocumento);
		try {
			ModeloDocumento modeloDocumento = modeloDocumentoSearch.getModeloDocumentoByCodigo(configuration.codigoModeloDocumento);
			
			ExpressionResolverChain chain = new ExpressionResolverChain(new SeamExpressionResolver(executionContext));
			String texto = modeloDocumentoManager.evaluateModeloDocumento(modeloDocumento, chain);
			DocumentoBin documentoBin = documentoBinManager.createProcessoDocumentoBin(modeloDocumento.getTituloModeloDocumento(), texto);
			Documento documento = documentoManager.createDocumento(processoRaiz, modeloDocumento.getTituloModeloDocumento(), documentoBin, classificacaoDocumento);
			
			Pasta pasta = null;
			if (configuration.getCodigoPasta() != null && !configuration.getCodigoPasta().isEmpty()){
				PastaSearch pastaSearch = Beans.getReference(PastaSearch.class);
				pasta = pastaSearch.getPastaByCodigoIdProcesso(configuration.getCodigoPasta(), processo.getIdProcesso());
				if (pasta == null) {
					pasta = pastaSearch.getPastaByCodigoIdProcesso(configuration.getCodigoPasta(), processoRaiz.getIdProcesso());
				}
			}
			if (pasta == null) {
				String parametroNomePastaDocumentoGerado = Parametros.PASTA_DOCUMENTO_GERADO.getValue();
				if (parametroNomePastaDocumentoGerado != null) {
					pasta = pastaManager.getPastaByNome(parametroNomePastaDocumentoGerado, processoRaiz);
				}
			}
			if (pasta != null) {
				documento.setPasta(pasta);
				documentoManager.update(documento);
			}
		} catch (Exception e) {
			LOG.error(MessageFormat.format("Erro ao gerar documento para o id de modelo de documento: {0}, no processo com id: {1}, nó: {2}", 
					configuration.codigoModeloDocumento, processo.getIdProcesso(), executionContext.getNode().getName()), e);
		}
	}
	
	public GenerateDocumentoConfiguration getConfiguration() {
		return this.configuration;
	}
	
	public static class GenerateDocumentoConfiguration {
		private String codigoModeloDocumento;
		private String codigoClassificacaoDocumento;
		private String codigoPasta;
		
		public String getCodigoClassificacaoDocumento() {
			return codigoClassificacaoDocumento;
		}
		
		public void setCodigoClassificacaoDocumento(String codigoClassificacaoDocumento) {
			this.codigoClassificacaoDocumento = codigoClassificacaoDocumento;
		}
		
		public String getCodigoModeloDocumento() {
			return codigoModeloDocumento;
		}
		
		public void setCodigoModeloDocumento(String codigoModeloDocumento) {
			this.codigoModeloDocumento = codigoModeloDocumento;
		}

		public String getCodigoPasta() {
			return codigoPasta;
		}

		public void setCodigoPasta(String codigoPasta) {
			this.codigoPasta = codigoPasta;
		}
	}
}
