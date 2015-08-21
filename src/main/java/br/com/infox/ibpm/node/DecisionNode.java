package br.com.infox.ibpm.node;

import static java.text.MessageFormat.format;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jboss.el.parser.ParseException;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmException;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.node.DecisionCondition;
import org.jbpm.graph.node.DecisionHandler;
import org.jbpm.instantiation.Delegation;
import org.jbpm.jpdl.el.impl.JbpmExpressionEvaluator;
import org.jbpm.jpdl.xml.JpdlXmlReader;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.epp.documento.entity.Variavel;
import br.com.infox.epp.fluxo.entity.DefinicaoVariavelProcesso;
import br.com.infox.epp.processo.variavel.bean.VariavelProcesso;
import br.com.infox.epp.processo.variavel.service.VariavelProcessoService;
import br.com.infox.epp.system.annotation.Ignore;
import br.com.infox.ibpm.process.definition.expressionWizard.ExpressionTokenizer;
import br.com.infox.ibpm.process.definition.variable.VariableType;
import br.com.infox.ibpm.task.handler.VariableCollector;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.ApplicationException;
import br.com.infox.seam.util.ComponentUtil;

@Ignore
public class DecisionNode extends Node {
    private static final long serialVersionUID = 1L;

    private static final LogProvider LOG = Logging.getLogProvider(DecisionNode.class);

    private List<DecisionCondition> decisionConditions = null;
    private Delegation decisionDelegation = null;
    private String decisionExpression = null;
    private List<String> booleanVariables = new ArrayList<>();
    private List<String> numberVariables = new ArrayList<>();
    private List<String> leavingTransitionList = new ArrayList<>();
    private List<String> stringVariables = new ArrayList<>();

    public List<String> getStringVariables(List<DefinicaoVariavelProcesso> processVariables, List<Variavel> externalVariables){
        List<String> variables = getProcessedVariables(stringVariables, VariableType.STRING, VariableType.ENUMERATION);
        
        for (DefinicaoVariavelProcesso variable : processVariables) {
        	String name = variable.getNome();
			if (name == null) {
				throw new ApplicationException(InfoxMessages.getInstance().get("processDefinition.variable.invalid"));
			}
			variables.add(format("''{0}''", name));
		}
        
        for (Variavel variavel : externalVariables) {
            variables.add(format("''{0}''", variavel.getValorVariavel().replaceAll("[#\\{\\}]", "")));
        }
        
        return variables;
    }
    
    public List<String> getNumberVariables() {
        return getProcessedVariables(numberVariables, VariableType.INTEGER, VariableType.MONETARY);
    }

    public List<String> getLeavingTransitionList() {
        leavingTransitionList = new ArrayList<String>();
        for (Object transition : leavingTransitions) {
            leavingTransitionList.add(format("''{0}''", ((Transition) transition).getName()));
        }
        return leavingTransitionList;
    }

    private List<String> getProcessedVariables(List<String> variables, VariableType... types) {
    	variables.clear();
        final VariableCollector variableCollector = new VariableCollector(this);
		final List<VariableAccess> variablesOfTypes = variableCollector.getVariablesOfTypes(types);
        for (VariableAccess variableAccess : variablesOfTypes) {
			String[] split = variableAccess.getMappedName().split(":");
			if (split.length < 2) {
				throw new ApplicationException(InfoxMessages.getInstance().get("processDefinition.variable.invalid"));
			}
			variables.add(format("''{0}''", split[1]));
		}
        return variables;
    }
    
    public List<String> getBooleanVariables() {
        return getProcessedVariables(booleanVariables, VariableType.BOOLEAN);
    }

    public String getDecisionExpression() {
        if (decisionExpression==null){
            decisionExpression=format("#'{'{0}'}'", getLeavingTransitionList().get(0));
        }
        return decisionExpression;
    }

    public void setDecisionExpression(String decisionExpression) {
        try {
            ExpressionTokenizer.validateExpression(decisionExpression);
            this.decisionExpression = decisionExpression;
        } catch (ParseException e){
            this.decisionExpression=format("#'{'{0}'}'", getLeavingTransitionList().get(0));
            LogProvider logProvider = Logging.getLogProvider(DecisionNode.class);
            ActionMessagesService actionMessagesService = ComponentUtil.getComponent(ActionMessagesService.NAME);
            actionMessagesService.handleException("Erro na expressão",e);
            logProvider.error("Erro ao recuperar expressão", e);
        }
    }

    public String getTokenStack(){
        String result="";
        try {
            result = ExpressionTokenizer.toNodeJSON(getDecisionExpression());
        } catch (ParseException e) {
            LOG.error("Erro ao recuperar expressão", e);
            decisionExpression=null;
            return getTokenStack();
        } catch (Exception e) {
            LOG.error("Erro inesperado", e);
        }
        return result;
    }
    public void setTokenStack(String stack){
        setDecisionExpression(ExpressionTokenizer.fromNodeJSON(stack));
    }
    
    // ----------

    public DecisionNode() {
    }

    public DecisionNode(String name) {
        super(name);
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.Decision;
    }

    @Override
    public void read(Element decisionElement, JpdlXmlReader jpdlReader) {
        String expression = decisionElement.attributeValue("expression");
        Element decisionHandlerElement = decisionElement.element("handler");

        if (expression != null) {
            decisionExpression = expression;
        } else if (decisionHandlerElement != null) {
            decisionDelegation = new Delegation();
            decisionDelegation.read(decisionHandlerElement, jpdlReader);
        }
    }

    @Override
    public void execute(ExecutionContext executionContext) {
        Transition transition = null;

        // set context class loader correctly for delegation class
        // (https://jira.jboss.org/jira/browse/JBPM-1448)
        Thread currentThread = Thread.currentThread();
        ClassLoader contextClassLoader = currentThread.getContextClassLoader();
        currentThread.setContextClassLoader(JbpmConfiguration.getProcessClassLoader(executionContext.getProcessDefinition()));

        //TODO is this really necessary?
        populateVariables(executionContext);
        try {
            if (decisionDelegation != null) {
                DecisionHandler decisionHandler = (DecisionHandler) decisionDelegation.getInstance();
                if (decisionHandler == null) {
                    decisionHandler = (DecisionHandler) decisionDelegation.instantiate();
                }

                String transitionName = decisionHandler.decide(executionContext);
                transition = getLeavingTransition(transitionName);
                if (transition == null) {
                    throw new JbpmException("decision '" + name
                            + "' selected non existing transition '"
                            + transitionName + "'");
                }
            } else if (decisionExpression != null) {
                Object result = JbpmExpressionEvaluator.evaluate(decisionExpression, executionContext);
                if (result == null) {
                    throw new JbpmException("decision expression '"
                            + decisionExpression + "' returned null");
                }
                String transitionName = result.toString();
                transition = getLeavingTransition(transitionName);
                if (transition == null) {
                    throw new JbpmException("decision '" + name
                            + "' selected non existing transition '"
                            + transitionName + "'");
                }
            } else if (decisionConditions != null
                    && !decisionConditions.isEmpty()) {
                // backwards compatible mode based on separate
                // DecisionCondition's
                for (DecisionCondition decisionCondition : decisionConditions) {
                    Object result = JbpmExpressionEvaluator.evaluate(decisionCondition.getExpression(), executionContext);
                    if (Boolean.TRUE.equals(result)) {
                        String transitionName = decisionCondition.getTransitionName();
                        transition = getLeavingTransition(transitionName);
                        if (transition != null) {
                            break;
                        }
                    }
                }
            } else {
                // new mode based on conditions in the transition itself
                for (Object o : leavingTransitions) {
                    Transition candidate = (Transition) o;
                    String conditionExpression = candidate.getCondition();
                    if (conditionExpression != null) {
                        Object result = JbpmExpressionEvaluator.evaluate(conditionExpression, executionContext);
                        if (Boolean.TRUE.equals(result)) {
                            transition = candidate;
                            break;
                        }
                    }
                }
            }
        } catch (Exception exception) {
            raiseException(exception, executionContext);
            if (!equals(executionContext.getNode())) {
                return;
            }
        } finally {
            currentThread.setContextClassLoader(contextClassLoader);
        }

        if (transition == null) {
            transition = getDefaultLeavingTransition();

            if (transition == null) {
                throw new JbpmException("decision cannot select transition: "
                        + this);
            }

            log.debug("decision did not select transition, taking default "
                    + transition);
        }

        // since the decision node evaluates condition expressions, the
        // condition of the
        // taken transition will always be met. therefore we can safely turn off
        // the standard condition enforcement in the transitions after a
        // decision node.
        transition.setConditionEnforced(false);

        log.debug("decision '" + name + "' is taking " + transition);
        executionContext.leaveNode(transition);
    }

    private void populateVariables(ExecutionContext executionContext) {
        Integer idProcesso = (Integer) executionContext.getContextInstance().getVariable("processo", executionContext.getProcessInstance().getRootToken());
        VariavelProcessoService variavelProcessoService = ComponentUtil.getComponent(VariavelProcessoService.NAME);
        List<VariavelProcesso> variaveis = variavelProcessoService.getVariaveisHierquiaProcesso(idProcesso);
        for (VariavelProcesso variavel : variaveis) {
            Boolean alreadyExists = executionContext.getVariable(variavel.getNome()) != null;
            if (!alreadyExists) {
                executionContext.setVariable(variavel.getNome(), variavel.getValor());
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DecisionNode)) {
            return false;
        }
        DecisionNode other = (DecisionNode) obj;
        if (getId() != 0) {
            return getId() == other.getId();
        } else {
            return getName().equals(other.getName());
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) getId();
        return result;
    }

    public List<DecisionCondition> getDecisionConditions() {
        return decisionConditions;
    }

    public void setDecisionDelegation(Delegation decisionDelegation) {
        this.decisionDelegation = decisionDelegation;
    }

    private static Log log = LogFactory.getLog(DecisionNode.class);
}
