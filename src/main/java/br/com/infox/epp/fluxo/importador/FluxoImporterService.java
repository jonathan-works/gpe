package br.com.infox.epp.fluxo.importador;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.access.dao.PerfilTemplateDAO;
import br.com.infox.epp.documento.ClassificacaoDocumentoSearch;
import br.com.infox.epp.documento.modelo.ModeloDocumentoSearch;
import br.com.infox.epp.fluxo.dao.DefinicaoVariavelProcessoDAO;
import br.com.infox.epp.fluxo.entity.DefinicaoVariavelProcesso;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.exportador.FluxoConfiguration;
import br.com.infox.epp.processo.status.entity.StatusProcesso;
import br.com.infox.epp.processo.status.manager.StatusProcessoSearch;
import br.com.infox.ibpm.node.handler.NodeHandler;
import br.com.infox.ibpm.sinal.DispatcherConfiguration;
import br.com.infox.ibpm.sinal.SignalSearch;
import br.com.infox.ibpm.task.handler.GenerateDocumentoHandler;
import br.com.infox.ibpm.task.handler.GenerateDocumentoHandler.GenerateDocumentoConfiguration;
import br.com.infox.ibpm.task.handler.StatusHandler;
import br.com.infox.ibpm.variable.VariableDominioEnumerationHandler;
import br.com.infox.ibpm.variable.VariableDominioEnumerationHandler.EnumerationConfig;
import br.com.infox.ibpm.variable.VariableEditorModeloHandler;
import br.com.infox.ibpm.variable.VariableEditorModeloHandler.FileConfig;
import br.com.infox.ibpm.variable.dao.DominioVariavelTarefaSearch;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.BusinessException;
import br.com.infox.seam.exception.BusinessRollbackException;
import br.com.infox.seam.exception.ValidationException;

@Stateless
public class FluxoImporterService {
	private static final LogProvider LOG = Logging.getLogProvider(FluxoImporterService.class);
	
	@Inject
	private PerfilTemplateDAO perfilTemplateDAO;
	@Inject
	private StatusProcessoSearch statusProcessoSearch;
	@Inject
	private ClassificacaoDocumentoSearch classificacaoDocumentoSearch;
	@Inject 
	private ModeloDocumentoSearch modeloDocumentoSearch;
	@Inject
	private DominioVariavelTarefaSearch dominioVariavelTarefaSearch;
	@Inject
	private DefinicaoVariavelProcessoDAO definicaoVariavelProcessoDAO;
	@Inject
	private SignalSearch signalSearch;
	
	public Fluxo importarFluxo(HashMap<String, String> xmls, Fluxo fluxo) {
		String xpdl = xmls.get(FluxoConfiguration.FLUXO_XML);
		Document doc = readDocument(xpdl);
		validarExistenciaCodigos(doc);
		atualizaNameProcessDefinition(doc, fluxo);
		fluxo = gravarXpdlFluxo(fluxo, doc);
		removeVariaveisProcesso(fluxo);
		adicionarVariaveisProcesso(xmls.get(FluxoConfiguration.PROCESS_VARIABLES_XML), fluxo);
		return fluxo;
	}

	private void removeVariaveisProcesso(Fluxo fluxo) {
		List<DefinicaoVariavelProcesso> variaveis = definicaoVariavelProcessoDAO.listVariaveisByFluxo(fluxo);
		for (DefinicaoVariavelProcesso definicaoVariavelProcesso : variaveis) {
			getEntityManager().remove(definicaoVariavelProcesso);
		}
		getEntityManager().flush();
	}

	private Fluxo gravarXpdlFluxo(Fluxo fluxo, Document doc) {
		String xpdl;
		try {
			xpdl = convertToXml(doc);
			fluxo.setXml(xpdl);
			fluxo = getEntityManager().merge(fluxo);
			return fluxo;
		} catch (IOException e) {
			LOG.error("Erro ao gerar o xml", e);
			throw new BusinessRollbackException("Erro na conversão do xml.");
		}
	}
	
	private void atualizaNameProcessDefinition(Document doc, Fluxo fluxo) {
	    Element processDefinition = doc.getRootElement();
	    if (processDefinition.getName().equals("process-definition")) {
	    	processDefinition.setAttribute("name", fluxo.getFluxo());
	    }
	}
	
	private void validarExistenciaCodigos(Document doc) {
		List<String> erros = new ArrayList<String>();
		validaConfiguracaoRaiaPerfil(doc, erros);
		validaActions(doc, erros);
		validaEvents(doc, erros);
		validaMailNode(doc, erros);
		validaVariaveis(doc, erros);
		if (!erros.isEmpty()) {
			throw new ValidationException(erros);
		}
	}
	
	private void validaMailNode(Document doc, List<String> erros) {
		for (Element mailNode : doc.getDescendants(new ElementFilter("mail-node"))) {
			String configurationModelo = mailNode.getChild("text", mailNode.getNamespace()).getText();
			if (configurationModelo.startsWith("{codigoModeloDocumento=")) {
				configurationModelo = configurationModelo.substring(0, configurationModelo.length() - 1);
				String[] att = configurationModelo.split("=");
				String codigo = att[1];
				if (!modeloDocumentoSearch.existeModeloByCodigo(codigo)) {
					StringBuilder msgErro = new StringBuilder();
					msgErro.append("Não foi encontrado o modelo de documento com código '");
					msgErro.append(codigo);
					msgErro.append("' utilizado no nó de email '");
					msgErro.append(mailNode.getAttributeValue("name"));
					msgErro.append("'");
		        	erros.add(msgErro.toString());
				}
			}
		}
	}

	private void validaEvents(Document doc, List<String> erros) {
		for (Element event : doc.getDescendants(new ElementFilter("event"))) {
			String eventType = event.getAttributeValue("type");
			if (eventType != null && !eventType.isEmpty()) {
				if (eventType.startsWith("listener-")) {
					String codigo = eventType.replaceFirst("listener-", "");
					if (!signalSearch.existeSignalByCodigo(codigo)) {
						StringBuilder msgErro = new StringBuilder();
						msgErro.append("Não foi encontrado o sinal com código '");
						msgErro.append(codigo);
						msgErro.append("' utilizado em um observador de sinal do nó '");
						msgErro.append(event.getParentElement().getAttributeValue("name"));
						msgErro.append("'");
			        	erros.add(msgErro.toString());
					}
				} else if (eventType.equals("dispatcher")) {
					String codigo = DispatcherConfiguration.fromJson(event.getAttributeValue("configuration")).getCodigoSinal();
					if (!signalSearch.existeSignalByCodigo(codigo)) {
						StringBuilder msgErro = new StringBuilder();
						msgErro.append("Não foi encontrado o sinal com código '");
						msgErro.append(codigo);
						msgErro.append("' utilizado em um observador de sinal do nó '");
						msgErro.append(event.getParentElement().getAttributeValue("name"));
						msgErro.append("'");
			        	erros.add(msgErro.toString());
					}
				}
			}
		}
	}

	private void validaVariaveis(Document doc, List<String> erros) {
        for (Element variableNode : doc.getDescendants(new ElementFilter("variable"))) {
        	String typeName = variableNode.getAttributeValue("type");
			switch (typeName) {
			case "EDITOR":
			case "FILE": 
				validaFileConfiguration(variableNode, erros);
				break;
			case "ENUMERATION_MULTIPLE":
			case "ENUMERATION": 
				validaDominioConfiguration(variableNode, erros);
				break;
			default:
				break;
			}
		}
	}

	private void validaDominioConfiguration(Element variableNode, List<String> erros) {
		EnumerationConfig configuration = VariableDominioEnumerationHandler.fromJson(variableNode.getAttributeValue("configuration"));
		if (!dominioVariavelTarefaSearch.existeDominioByCodigo(configuration.getCodigoDominio())) {
			StringBuilder msgErro = new StringBuilder();
			msgErro.append("Não foi encontrado o domínio de dados com o código '");
			msgErro.append(configuration.getCodigoDominio());
			msgErro.append("' utilizado na variável '");
        	msgErro.append(variableNode.getAttributeValue("name"));
        	msgErro.append("' do nó '");
			msgErro.append(variableNode.getParentElement().getParentElement().getParentElement().getAttributeValue("name"));
			msgErro.append("'");
        	erros.add(msgErro.toString());
		}
	}

	private void validaFileConfiguration(Element variableNode, List<String> erros) {
		FileConfig configuration = VariableEditorModeloHandler.fromJson(variableNode.getAttributeValue("configuration"));
		if (configuration.getCodigosModeloDocumento() != null && !configuration.getCodigosModeloDocumento().isEmpty()) {
			StringBuilder msgErro = new StringBuilder();
			for (String codigo : configuration.getCodigosModeloDocumento()) {
				if (!modeloDocumentoSearch.existeModeloByCodigo(codigo)) {
					msgErro.append("'");
					msgErro.append(codigo);
					msgErro.append("', ");
				}
			}
			if (msgErro.length() > 0) {
	        	msgErro.insert(0,"Não foram encontrados modelos de documento com os códigos: ");
	        	msgErro.setLength(msgErro.length() - 2);
	        	msgErro.append(". Utilizados na variável '");
	        	msgErro.append(variableNode.getAttributeValue("name"));
	        	msgErro.append("' do nó '");
				msgErro.append(variableNode.getParentElement().getParentElement().getParentElement().getAttributeValue("name"));
				msgErro.append("'");
	        	erros.add(msgErro.toString());
	        }
		}
		if (configuration.getCodigosClassificacaoDocumento() != null && !configuration.getCodigosClassificacaoDocumento().isEmpty()) {
			StringBuilder msgErro = new StringBuilder();
			for (String codigo : configuration.getCodigosClassificacaoDocumento()) {
				if (!classificacaoDocumentoSearch.existeClassificacaoByCodigo(codigo)) {
					msgErro.append("'");
					msgErro.append(codigo);
					msgErro.append("', ");
				}
			}
			if (msgErro.length() > 0) {
	        	msgErro.insert(0,"Não foram encontradas classificações de documento com os códigos: ");
	        	msgErro.setLength(msgErro.length() - 2);
	        	msgErro.append(". Utilizadas na variável '");
	        	msgErro.append(variableNode.getAttributeValue("name"));
	        	msgErro.append("' do nó '");
				msgErro.append(variableNode.getParentElement().getParentElement().getParentElement().getAttributeValue("name"));
	        	msgErro.append("'");
				erros.add(msgErro.toString());
	        }
		}
	}

	private void validaActions(Document doc, List<String> erros) {
		for (Element action : doc.getDescendants(new ElementFilter("action"))) {
			String actionName = action.getAttributeValue("name");
			if (actionName != null && !actionName.isEmpty()) {
				validaConfiguracaoStatusProcesso(action, erros);
				validaConfiguracaoGeracaoDocumento(action, erros);
			}
		}
	}

	private void validaConfiguracaoGeracaoDocumento(Element action, List<String> erros) {
		if (NodeHandler.GENERATE_DOCUMENTO_ACTION_NAME.equals(action.getAttributeValue("name"))) {
			GenerateDocumentoConfiguration configuration = new GenerateDocumentoHandler(action.getText()).getConfiguration();
			if (!classificacaoDocumentoSearch.existeClassificacaoByCodigo(configuration.getCodigoClassificacaoDocumento())) {
				StringBuilder msgErro = new StringBuilder();
				msgErro.append("Não foi encontrada a classificação de documento com o código '");
				msgErro.append(configuration.getCodigoClassificacaoDocumento());
				msgErro.append("' utilizada para geração de documento no nó '");
				msgErro.append(action.getParentElement().getParentElement().getAttributeValue("name"));
				msgErro.append("'");
				erros.add(msgErro.toString());
			}
			if (!modeloDocumentoSearch.existeModeloByCodigo(configuration.getCodigoModeloDocumento())) {
				StringBuilder msgErro = new StringBuilder();
				msgErro.append("Não foi encontrado o modelo de documento com o código '");
				msgErro.append(configuration.getCodigoModeloDocumento());
				msgErro.append("' utilizada para geração de documento no nó '");
				msgErro.append(action.getParentElement().getParentElement().getAttributeValue("name"));
				msgErro.append("'");
				erros.add(msgErro.toString());
			}
		}
	}

	private void validaConfiguracaoStatusProcesso(Element action, List<String> erros) {
		StringBuilder msgErro = new StringBuilder();
		if (StatusProcesso.STATUS_PROCESSO_ACTION_NAME.equals(action.getAttributeValue("name"))) {
			String codigo = new StatusHandler(action.getText()).getCodigoStatusProcesso();
			if (!statusProcessoSearch.existeStatusProcessoByNome(codigo)) {
				msgErro.append("'");
				msgErro.append(codigo);
				msgErro.append("', ");
			}
		}
		if (msgErro.length() > 0) {
        	msgErro.insert(0,"Não foram encontrados status do processo com os nomes: ");
        	msgErro.setLength(msgErro.length() - 2);
        	erros.add(msgErro.toString());
        }
	}

	private void validaConfiguracaoRaiaPerfil(Document doc, List<String> erros) {
		StringBuilder msgErro = new StringBuilder();
		for (Element swinlaneNode : doc.getDescendants(new ElementFilter("swimlane"))) {
        	for (Element assignment : swinlaneNode.getChildren("assignment", swinlaneNode.getNamespace())) {
        		String listaCodigos = assignment.getAttributeValue("pooled-actors");
        		if (listaCodigos != null && !listaCodigos.isEmpty()) {
        			String[] codigos = listaCodigos.split(",");
        			for (String codigo : codigos) {
        				if (!perfilTemplateDAO.existePerfilTemplateByCodigo(codigo)) {
        					msgErro.append("'");
        					msgErro.append(codigo);
        					msgErro.append("', ");
        				}
        			}
				}
			}
        }
        if (msgErro.length() > 0) {
        	msgErro.insert(0,"Não foram encontrados perfis com os códigos: ");
        	msgErro.setLength(msgErro.length() - 2);
        	erros.add(msgErro.toString());
        }
	}
	
	private String convertToXml(Document doc) throws IOException {
		StringWriter stringWriter = new StringWriter();
		XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat().setIndent("    "));
		outputter.output(doc, stringWriter);
		stringWriter.flush();
		return stringWriter.toString();
	}
	
	private Document readDocument(String xml) {
		InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
		SAXBuilder builder = new SAXBuilder();
		try {
			Document doc = builder.build(inputStream);
			return doc;
		} catch (Exception e) {
			LOG.info("Erro ao importar fluxo", e);
			throw new BusinessException("Erro na leitura do xml.");
		}
	}
	
	private void adicionarVariaveisProcesso(String xmlVariaveisProcesso, Fluxo fluxo) {
		try {
			FluxoConfiguration configuration = readFluxoConfiguration(xmlVariaveisProcesso);
			List<DefinicaoVariavelProcesso> list = configuration.createDefinicaoVariavelProcessoList();
			for (DefinicaoVariavelProcesso definicaoVariavelProcesso : list) {
				definicaoVariavelProcesso.setFluxo(fluxo);
				getEntityManager().persist(definicaoVariavelProcesso);
			}
			getEntityManager().flush();
		} catch (JAXBException e) {
			LOG.error("Erro ao adicionar as variaveis do processo.", e);
			new BusinessRollbackException("Erro ao recuperar as variaveis do xml");
		}
	}

	private FluxoConfiguration readFluxoConfiguration(String string) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(FluxoConfiguration.class);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		return (FluxoConfiguration) unmarshaller.unmarshal(new StringReader(string));
	}
	
	private EntityManager getEntityManager() {
		return EntityManagerProducer.getEntityManager();
	}

}
