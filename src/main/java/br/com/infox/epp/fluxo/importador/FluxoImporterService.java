package br.com.infox.epp.fluxo.importador;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
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
		StringBuilder msgErro = new StringBuilder();
		validaConfiguracaoRaiaPerfil(doc, msgErro);
		validaActions(doc, msgErro);
		validaVariaveis(doc, msgErro);
		if (msgErro.length() > 0) {
			throw new BusinessException(msgErro.toString());
		}
	}
	private void validaVariaveis(Document doc, StringBuilder msgErro) {
        for (Element variableNode : doc.getDescendants(new ElementFilter("variable"))) {
        	String typeName = variableNode.getAttributeValue("type");
			switch (typeName) {
			case "EDITOR":
			case "FILE": 
				validaFileConfiguration(variableNode, msgErro);
				break;
			case "ENUMERATION_MULTIPLE":
			case "ENUMERATION": 
				validaDominioConfiguration(variableNode, msgErro);
				break;
			default:
				break;
			}
		}
	}

	private void validaDominioConfiguration(Element variableNode, StringBuilder msgErro) {
		EnumerationConfig configuration = VariableDominioEnumerationHandler.fromJson(variableNode.getAttributeValue("configuration"));
		if (!dominioVariavelTarefaSearch.existeDominioByCodigo(configuration.getCodigoDominio())) {
			msgErro.append("Não foi encontrado o domínio de dados com o código ");
			msgErro.append(configuration.getCodigoDominio());
			msgErro.append(" utilizado na variável ");
        	msgErro.append(variableNode.getAttributeValue("name"));
        	msgErro.append(" do nó ");
			msgErro.append(variableNode.getParentElement().getParentElement().getParentElement().getAttributeValue("name"));
        	msgErro.append(". \n");
		}
	}

	private void validaFileConfiguration(Element variableNode, StringBuilder msgErro) {
		FileConfig configuration = VariableEditorModeloHandler.fromJson(variableNode.getAttributeValue("configuration"));
		if (configuration.getCodigosModeloDocumento() != null && !configuration.getCodigosModeloDocumento().isEmpty()) {
			int initialLength = msgErro.length();
			for (String codigo : configuration.getCodigosModeloDocumento()) {
				if (!modeloDocumentoSearch.existeModeloByCodigo(codigo)) {
					msgErro.append(codigo);
					msgErro.append(" ,");
				}
			}
			if (msgErro.length() > initialLength) {
	        	msgErro.insert(initialLength,"Não foram encontrados modelos de documento com os códigos ");
	        	msgErro.setLength(msgErro.length() - 2);
	        	msgErro.append(" utilizados na variável ");
	        	msgErro.append(variableNode.getAttributeValue("name"));
	        	msgErro.append(" do nó ");
				msgErro.append(variableNode.getParentElement().getParentElement().getParentElement().getAttributeValue("name"));
	        	msgErro.append(". \n");
	        }
		}
		if (configuration.getCodigosClassificacaoDocumento() != null && !configuration.getCodigosClassificacaoDocumento().isEmpty()) {
			int initialLength = msgErro.length();
			for (String codigo : configuration.getCodigosClassificacaoDocumento()) {
				if (!classificacaoDocumentoSearch.existeClassificacaoByCodigo(codigo)) {
					msgErro.append(codigo);
					msgErro.append(" ,");
				}
			}
			if (msgErro.length() > initialLength) {
	        	msgErro.insert(initialLength,"Não foram encontradas classificações de documento com os códigos ");
	        	msgErro.setLength(msgErro.length() - 2);
	        	msgErro.append(" utilizadas na variável ");
	        	msgErro.append(variableNode.getAttributeValue("name"));
	        	msgErro.append(" do nó ");
				msgErro.append(variableNode.getParentElement().getParentElement().getParentElement().getAttributeValue("name"));
	        	msgErro.append(". \n");
	        }
		}
	}

	private void validaActions(Document doc, StringBuilder msgErro) {
		for (Element action : doc.getDescendants(new ElementFilter("action"))) {
			String actionName = action.getAttributeValue("name");
			if (actionName != null && !actionName.isEmpty()) {
				validaConfiguracaoStatusProcesso(action, msgErro);
				validaConfiguracaoGeracaoDocumento(action, msgErro);
			}
		}
	}

	private void validaConfiguracaoGeracaoDocumento(Element action, StringBuilder msgErro) {
		if (NodeHandler.GENERATE_DOCUMENTO_ACTION_NAME.equals(action.getAttributeValue("name"))) {
			GenerateDocumentoConfiguration configuration = new GenerateDocumentoHandler(action.getText()).getConfiguration();
			if (!classificacaoDocumentoSearch.existeClassificacaoByCodigo(configuration.getCodigoClassificacaoDocumento())) {
				msgErro.append("Não foi encontrada a classificação de documento com o código ");
				msgErro.append(configuration.getCodigoClassificacaoDocumento());
				msgErro.append(" utilizada para geração de documento no nó ");
				msgErro.append(action.getParentElement().getParentElement().getAttributeValue("name"));
				msgErro.append(". \n");
			}
			if (!modeloDocumentoSearch.existeModeloByCodigo(configuration.getCodigoModeloDocumento())) {
				msgErro.append("Não foi encontrado o modelo de documento com o código ");
				msgErro.append(configuration.getCodigoModeloDocumento());
				msgErro.append(" utilizada para geração de documento no nó ");
				msgErro.append(action.getParentElement().getParentElement().getAttributeValue("name"));
				msgErro.append(". \n");
			}
		}
	}

	private void validaConfiguracaoStatusProcesso(Element action, StringBuilder msgErro) {
		int initialLength = msgErro.length();
		if (StatusProcesso.STATUS_PROCESSO_ACTION_NAME.equals(action.getAttributeValue("name"))) {
			String codigo = new StatusHandler(action.getText()).getCodigoStatusProcesso();
			if (!statusProcessoSearch.existeStatusProcessoByNome(codigo)) {
				msgErro.append(codigo);
				msgErro.append(" ,");
			}
		}
		if (msgErro.length() > initialLength) {
        	msgErro.insert(initialLength,"Não foram encontrados status do processo com os nomes: ");
        	msgErro.setLength(msgErro.length() - 2);
        	msgErro.append(". \n");
        }
	}

	private void validaConfiguracaoRaiaPerfil(Document doc, StringBuilder msgErro) {
		int initialLength = msgErro.length();
		for (Element swinlaneNode : doc.getDescendants(new ElementFilter("swimlane"))) {
        	for (Element assignment : swinlaneNode.getChildren("assignment", swinlaneNode.getNamespace())) {
        		String listaCodigos = assignment.getAttributeValue("pooled-actors");
        		if (listaCodigos != null && !listaCodigos.isEmpty()) {
        			String[] codigos = listaCodigos.split(",");
        			for (String codigo : codigos) {
        				if (!perfilTemplateDAO.existePerfilTemplateByCodigo(codigo)) {
        					msgErro.append(codigo);
        					msgErro.append(" ,");
        				}
        			}
				}
			}
        }
        if (msgErro.length() > initialLength) {
        	msgErro.insert(initialLength,"Não foram encontrados perfis com os códigos: ");
        	msgErro.setLength(msgErro.length() - 2);
        	msgErro.append(". \n");
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
