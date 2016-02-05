package br.com.infox.epp.fluxo.crud;

import static org.jboss.seam.international.StatusMessage.Severity.ERROR;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.util.IteratorIterable;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.core.action.AbstractAction;
import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.cdi.seam.ContextDependency;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.manager.DefinicaoVariavelProcessoManager;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.ibpm.jpdl.InfoxJpdlXmlReader;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Name(FluxoCrudAction.NAME)
@ContextDependency
public class FluxoCrudAction extends AbstractCrudAction<Fluxo, FluxoManager> {

    @Inject
    private DefinicaoVariavelProcessoManager definicaoVariavelProcessoManager;

    private static final long serialVersionUID = 1L;
    private static final String DESCRICAO_FLUXO_COMPONENT_ID = "defaultTabPanel:fluxoForm:descricaoFluxoDecoration:descricaoFluxo";
    private static final String COD_FLUXO_COMPONENT_ID = "defaultTabPanel:fluxoForm:codFluxoDecoration:codFluxo";
    private static final LogProvider LOG = Logging
            .getLogProvider(FluxoCrudAction.class);
    public static final String NAME = "fluxoCrudAction";
    
    @In
    private ActionMessagesService actionMessagesService;
    
    private boolean replica = false;

    public String criarReplica() {
        this.replica = true;
        if (!verificarReplica()) {
            return null;
        }
        final Fluxo fluxo = getInstance();
        fluxo.setPublicado(false);
        getManager().detach(fluxo);
        fluxo.setIdFluxo(null);
        setId(null);
        final String ret = save();
        if (AbstractAction.PERSISTED.equals(ret)) {
            this.replica = false;
        }
        return ret;
    }

    private boolean verificarReplica() {
        final boolean existeFluxoComCodigo = getManager().existeFluxoComCodigo(
                getInstance().getCodFluxo());
        final boolean existeFluxoComDescricao = getManager()
                .existeFluxoComDescricao(getInstance().getFluxo());

        if (existeFluxoComCodigo) {
            final FacesMessage message = FacesMessages.createFacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "#{infoxMessages['fluxo.codigoDuplicado']}");
            FacesContext.getCurrentInstance().addMessage(
                    FluxoCrudAction.COD_FLUXO_COMPONENT_ID, message);
        }
        if (existeFluxoComDescricao) {
            final FacesMessage message = FacesMessages.createFacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "#{infoxMessages['fluxo.descricaoDuplicada']}");
            FacesContext.getCurrentInstance().addMessage(
                    FluxoCrudAction.DESCRICAO_FLUXO_COMPONENT_ID, message);
        }

        return !existeFluxoComCodigo && !existeFluxoComDescricao;
    }

    @Override
    protected boolean isInstanceValid() {
        final Fluxo fluxo = getInstance();
        final Date dataFimPublicacao = fluxo.getDataFimPublicacao();
        final Date dataInicioPublicacao = fluxo.getDataInicioPublicacao();
        final boolean instanceValid = (dataInicioPublicacao != null)
                && ((dataFimPublicacao == null) || !dataFimPublicacao
                        .before(dataInicioPublicacao));
        if (!instanceValid) {
            getMessagesHandler().add(ERROR,
                    "#{infoxMessages['fluxo.dataPublicacaoErrada']}");
        }
        return instanceValid;
    }

    @Override
    public String inactive(final Fluxo fluxo) {
        setInstanceId(fluxo.getIdFluxo());
        if (!getManager().existemProcessosAssociadosAFluxo(fluxo)) {
            return super.inactive(fluxo);
        } else {
            final String message = "#{infoxMessages['fluxo.remocaoProibida']}";
            FluxoCrudAction.LOG.error(message);
            getMessagesHandler().add(ERROR, message);
        }
        newInstance();
        return null;
    }

    public boolean isReplica() {
        return this.replica;
    }

    @Override
    public void newInstance() {
        super.newInstance();
        getInstance().setPublicado(false);
        this.replica = false;
    }
    
    public void converterParaJpdl() {
    	try {
    		getManager().converterParaJpdl(getInstance());
    		FacesMessages.instance().add("Definição convertida com sucesso");
    	} catch (Exception e) {
    		LOG.error("", e);
    		actionMessagesService.handleGenericException(e, "");
    	}
    }
    
    @Override
    protected void afterSave(String ret) {
        super.afterSave(ret);
        if (PERSISTED.equals(ret)) {
            definicaoVariavelProcessoManager.createDefaultDefinicaoVariavelProcessoList(getInstance());
        }
    }
    
    public void converterParaBpmn() {
    	try {
    		getManager().converterParaBpmn(getInstance());
    		FacesMessages.instance().add("Definição convertida com sucesso");
    	} catch (Exception e) {
    		LOG.error("", e);
    		actionMessagesService.handleGenericException(e, "");
    	}
    }
    
    public void teste() {
    	EntityManager entityManager = EntityManagerProducer.getEntityManager();
    	List<Fluxo> fluxos = entityManager.createQuery("select o from Fluxo o", Fluxo.class).getResultList();
    	for (Fluxo fluxo : fluxos) {
    		System.out.println("testando fluxo " + fluxo.getFluxo());
    		x(fluxo.getXml(), migrateTransitions(fluxo.getXml()));
    		System.out.println("testando fluxo execução " + fluxo.getFluxo());
    		x(fluxo.getXmlExecucao(), migrateTransitions(fluxo.getXmlExecucao()));
    	}
    }
    
    private void x(String processDefinitionXml, String processDefinitionXmlMigrated) {
    	ProcessDefinition processDefinition = new InfoxJpdlXmlReader(new StringReader(processDefinitionXml)).readProcessDefinition();
		ProcessDefinition processDefinitionMigrated = new InfoxJpdlXmlReader(new StringReader(processDefinitionXmlMigrated)).readProcessDefinition();
		
		Map<String, Boolean> marked = new HashMap<>();
		List<String> result1 = new ArrayList<>();
		for (Node node : processDefinition.getNodes()) {
			if (!marked.containsKey(node.getKey())) {
				visit(node, marked, result1);
			}
		}
		
		marked = new HashMap<>();
		List<String> result2 = new ArrayList<>();
		for (Node node : processDefinitionMigrated.getNodes()) {
			if (!marked.containsKey(node.getKey())) {
				visit(node, marked, result2);
			}
		}
		
		if (result1.size() != result2.size()) {
			System.out.println(result1);
			System.out.println(result2);
			throw new RuntimeException();
		}
		for (int i = 0; i < result1.size(); i++) {
			if (!result1.get(i).equals(result2.get(i))) {
				System.out.println(result1);
				System.out.println(result2);
				System.out.println(result1.get(i));
				System.out.println(result2.get(i));
				throw new RuntimeException();
			}
		}
    }
    
    private void visit(Node node, Map<String, Boolean> marked, List<String> result) {
    	marked.put(node.getKey(), true);
    	result.add(node.getKey());
    	if (node.getLeavingTransitions() != null) {
    		for (Transition t : node.getLeavingTransitions()) {
    			if (!marked.containsKey(t.getTo().getKey())) {
    				visit(t.getTo(), marked, result);
    			}
    		}
    	}
    }
    
    private String migrateTransitions(String processDefinitionXml) {
		SAXBuilder saxBuilder = new SAXBuilder();
		Document document;
		try {
			document = saxBuilder.build(new StringReader(processDefinitionXml));
		} catch (JDOMException | IOException e) {
			throw new RuntimeException(e);
		}

		Map<String, String> nodeKeys = new HashMap<>();
		Element processDefinition = document.getRootElement();
		
		for (Element element : processDefinition.getChildren()) {
			String name = element.getAttributeValue("name");
			String key = element.getAttributeValue("key");
			if (name != null && key != null) {
				nodeKeys.put(name, key);
			}
		}
		
		IteratorIterable<Element> it = processDefinition.getDescendants(new ElementFilter("transition", processDefinition.getNamespace()));
		for (Element transition : it) {
			String to = transition.getAttributeValue("to");
			if (nodeKeys.containsKey(to)) {
				transition.setAttribute("to", nodeKeys.get(to));
			}
		}
		
		XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
		return out.outputString(document);
	}
}
