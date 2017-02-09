package br.com.infox.epp.fluxo.importador;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.access.dao.PerfilTemplateDAO;
import br.com.infox.epp.access.manager.LocalizacaoManager;
import br.com.infox.epp.documento.ClassificacaoDocumentoSearch;
import br.com.infox.epp.documento.modelo.ModeloDocumentoSearch;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.exportador.FluxoExporterService;
import br.com.infox.epp.processo.status.entity.StatusProcesso;
import br.com.infox.epp.processo.status.manager.StatusProcessoSearch;
import br.com.infox.epp.usuario.UsuarioLoginSearch;
import br.com.infox.ibpm.node.handler.NodeHandler;
import br.com.infox.ibpm.sinal.DispatcherConfiguration;
import br.com.infox.ibpm.sinal.SignalSearch;
import br.com.infox.ibpm.task.handler.GenerateDocumentoHandler;
import br.com.infox.ibpm.task.handler.GenerateDocumentoHandler.GenerateDocumentoConfiguration;
import br.com.infox.ibpm.task.handler.StatusHandler;
import br.com.infox.ibpm.type.PooledActorType;
import br.com.infox.ibpm.variable.VariableDominioEnumerationHandler;
import br.com.infox.ibpm.variable.VariableDominioEnumerationHandler.EnumerationConfig;
import br.com.infox.ibpm.variable.VariableEditorModeloHandler;
import br.com.infox.ibpm.variable.VariableEditorModeloHandler.FileConfig;
import br.com.infox.ibpm.variable.dao.DominioVariavelTarefaSearch;
import br.com.infox.seam.exception.BusinessException;
import br.com.infox.seam.exception.BusinessRollbackException;
import br.com.infox.seam.exception.ValidationException;

@Stateless
public class FluxoImporterService extends PersistenceController {
    private static final Pattern PATTERN_EXPRESSION = Pattern.compile("#[{][^{}]+[}]");
	
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
	private SignalSearch signalSearch;
	@Inject
	private UsuarioLoginSearch usuarioLoginSearch;
	@Inject
	private LocalizacaoManager localizacaoManager;
	
	public Fluxo importarFluxo(HashMap<String, String> xmls, Fluxo fluxo) {
		String xpdl = xmls.get(FluxoExporterService.FLUXO_XML);
		Document doc = readDocument(xpdl);
		validarExistenciaCodigos(doc);
		atualizaNameProcessDefinition(doc, fluxo);

        String bpmn = xmls.get(FluxoExporterService.FLUXO_BPMN);

        try {
            xpdl = convertToXml(doc);
            fluxo.getDefinicaoProcesso().setXml(xpdl);
            if (bpmn != null && !bpmn.isEmpty()) {
                fluxo.getDefinicaoProcesso().setBpmn(bpmn);
            }
            fluxo = getEntityManager().merge(fluxo);
            getEntityManager().flush();
        } catch (IOException e) {
            throw new BusinessRollbackException("Erro na conversão do xml.");
        }

        return fluxo;
    }

	private void atualizaNameProcessDefinition(Document doc, Fluxo fluxo) {
	    Element processDefinition = doc.getRootElement();
	    if (processDefinition.getName().equals("process-definition")) {
	    	processDefinition.setAttribute("name", fluxo.getFluxo());
	    }
	}
	
	private void validarExistenciaCodigos(Document doc) {
		List<String> erros = new ArrayList<String>();
		validaConfiguracaoAssigments(doc, erros);
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
                    erros.add(MessageFormat.format(
                            InfoxMessages.getInstance().get("importador.erro.emailModeloDocumento"), codigo,
                            mailNode.getAttributeValue("name")));
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
                        erros.add(MessageFormat.format(InfoxMessages.getInstance().get("importador.erro.sinalCodigo"),
                                codigo, event.getParentElement().getAttributeValue("name")));
					}
				} else if (eventType.equals("dispatcher")) {
					String codigo = DispatcherConfiguration.fromJson(event.getAttributeValue("configuration")).getCodigoSinal();
					if (!signalSearch.existeSignalByCodigo(codigo)) {
					    erros.add(MessageFormat.format(InfoxMessages.getInstance().get("importador.erro.sinalCodigo"),
                                codigo, event.getParentElement().getAttributeValue("name")));
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
            erros.add(MessageFormat.format(InfoxMessages.getInstance().get("importador.erro.dominioCodigo"),
                    configuration.getCodigoDominio(), variableNode.getAttributeValue("name"),
                    variableNode.getParentElement().getParentElement().getParentElement().getAttributeValue("name")));
		}
	}

	private void validaFileConfiguration(Element variableNode, List<String> erros) {
		FileConfig configuration = VariableEditorModeloHandler.fromJson(variableNode.getAttributeValue("configuration"));
		if (configuration == null)
		    return;
        if (configuration.getCodigosModeloDocumento() != null && !configuration.getCodigosModeloDocumento().isEmpty()) {
            List<String> codigosModeloInexistentes = new ArrayList<>();
            for (String codigo : configuration.getCodigosModeloDocumento()) {
                if (!modeloDocumentoSearch.existeModeloByCodigo(codigo)) {
                    codigosModeloInexistentes.add(codigo);
                }
            }
            if (codigosModeloInexistentes.size() > 0) {
                erros.add(MessageFormat.format(InfoxMessages.getInstance().get("importador.erro.fileModelo"),
                        StringUtil.concatList(codigosModeloInexistentes, ", "), variableNode.getAttributeValue("name"),
                        variableNode.getParentElement().getParentElement().getParentElement().getAttributeValue("name")));
            }
        }
        if (configuration.getCodigosClassificacaoDocumento() != null && !configuration.getCodigosClassificacaoDocumento().isEmpty()) {
            List<String> codigosClassificacaoInexistentes = new ArrayList<>();
            for (String codigo : configuration.getCodigosClassificacaoDocumento()) {
                if (!classificacaoDocumentoSearch.existeClassificacaoByCodigo(codigo)) {
                    codigosClassificacaoInexistentes.add(codigo);
                }
            }
            if (codigosClassificacaoInexistentes.size() > 0) {
                erros.add(MessageFormat.format(InfoxMessages.getInstance().get("importador.erro.fileClassificacao"),
                        StringUtil.concatList(codigosClassificacaoInexistentes, ", "),
                        variableNode.getAttributeValue("name"), variableNode.getParentElement().getParentElement()
                                .getParentElement().getAttributeValue("name")));
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
                erros.add(MessageFormat.format(InfoxMessages.getInstance().get("importador.erro.geracaoClassificacao"),
                        configuration.getCodigoClassificacaoDocumento(),
                        action.getParentElement().getParentElement().getAttributeValue("name")));
			}
			if (!modeloDocumentoSearch.existeModeloByCodigo(configuration.getCodigoModeloDocumento())) {
                erros.add(MessageFormat.format(InfoxMessages.getInstance().get("importador.erro.geracaoModelo"),
                        configuration.getCodigoModeloDocumento(),
                        action.getParentElement().getParentElement().getAttributeValue("name")));
			}
		}
	}

	private void validaConfiguracaoStatusProcesso(Element action, List<String> erros) {
		if (StatusProcesso.STATUS_PROCESSO_ACTION_NAME.equals(action.getAttributeValue("name"))) {
		    String codigo = new StatusHandler(action.getText()).getCodigoStatusProcesso();
			if (!statusProcessoSearch.existeStatusProcessoByNome(codigo)) {
                erros.add(MessageFormat.format(InfoxMessages.getInstance().get("importador.erro.statusCodigo"), codigo));
			}
		}
	}

    private void validaConfiguracaoAssigments(Document doc, List<String> erros) {
        Set<String> perfisInexistentes = new HashSet<>();
        Set<String> localizacoesInexistentes = new HashSet<>();
        Set<String> usuariosInexistentes = new HashSet<>();
        for (Element assignment : doc.getDescendants(new ElementFilter("assignment"))) {
            String pooledActor = assignment.getAttributeValue("pooled-actors");
            if (pooledActor != null && !pooledActor.isEmpty()) {
                String pooledActorExpressions = removeEL(pooledActor);
                if (!pooledActorExpressions.isEmpty()) {
                    for (String token : pooledActorExpressions.split(",")) {
                        String configuration = token.trim();
                        if (!configuration.isEmpty()) {
                            String[] configs = configuration.split(":");
                            if (configuration.startsWith(PooledActorType.USER.getValue())) {
                                String login = configs[1];
                                if (!usuarioLoginSearch.existeUsuarioByLogin(login)) {
                                    usuariosInexistentes.add(login);
                                }
                            } else if (configuration.startsWith(PooledActorType.LOCAL.getValue())) {
                                String localizacao = configs[1];
                                if (localizacaoManager.getLocalizacaoByCodigo(localizacao) == null) {
                                    localizacoesInexistentes.add(localizacao);
                                }
                            } else if (configuration.startsWith(PooledActorType.GROUP.getValue())) {
                                String[] groupConfig = configs[1].split("&");
                                if (localizacaoManager.getLocalizacaoByCodigo(groupConfig[0]) == null) {
                                    localizacoesInexistentes.add(groupConfig[0]);
                                }
                                if (!perfilTemplateDAO.existePerfilTemplateByCodigo(groupConfig[1])) {
                                    perfisInexistentes.add(groupConfig[1]);
                                }
                            } else if (!perfilTemplateDAO.existePerfilTemplateByCodigo(configuration)) {
                                perfisInexistentes.add(configuration);
                            }
                        }
                    }
                }
            }
        }
        if (perfisInexistentes.size() > 0) {
            erros.add(MessageFormat.format(InfoxMessages.getInstance().get("importador.erro.raiaCodigo"),
                    StringUtil.concatList(perfisInexistentes, ", ")));
        }
        if (localizacoesInexistentes.size() > 0) {
            erros.add(MessageFormat.format(InfoxMessages.getInstance().get("importador.erro.localizacaoCodigo"),
                    StringUtil.concatList(localizacoesInexistentes, ", ")));
        }
        if (usuariosInexistentes.size() > 0) {
            erros.add(MessageFormat.format(InfoxMessages.getInstance().get("importador.erro.usuario"),
                    StringUtil.concatList(usuariosInexistentes, ", ")));
        }
    }

    private String removeEL(String expression) {
        Matcher matcher = PATTERN_EXPRESSION.matcher(expression);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "");
        }
        matcher.appendTail(sb);
        return sb.toString();
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
			throw new BusinessException("Erro na leitura do xml.");
		}
	}

}
