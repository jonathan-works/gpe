package br.com.infox.ibpm.node;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmException;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.node.DecisionCondition;
import org.jbpm.graph.node.DecisionHandler;
import org.jbpm.instantiation.Delegation;
import org.jbpm.jpdl.el.impl.JbpmExpressionEvaluator;
import org.jbpm.jpdl.xml.JpdlXmlReader;

import br.com.infox.ibpm.process.definition.variable.VariableType;
import br.com.infox.ibpm.task.handler.TaskHandlerVisitor;

public class DecisionNode extends Node {
    private static final long serialVersionUID = 1L;

    private List<DecisionCondition> decisionConditions = null;
    private Delegation decisionDelegation = null;
    private String decisionExpression = null;
    private List<String> booleanVariables = null;
    private List<String> numberVariables = null;
    private List<String> leavingTransitionList = null;

    public List<String> getNumberVariables() {
        if (numberVariables == null) {
            List<String> list = new ArrayList<String>();
            list.add(VariableType.INTEGER.name());
            list.add(VariableType.MONETARY.name());
            TaskHandlerVisitor visitor = new TaskHandlerVisitor(false, list);
            visitor.visit(this);
            numberVariables = new ArrayList<String>();
            for (String string : visitor.getVariables()) {
                numberVariables.add("\"" + string + "\"");
            }
        }
        return numberVariables;
    }

    public List<String> getLeavingTransitionList() {
        if (leavingTransitionList == null) {
            leavingTransitionList = new ArrayList<String>();
            for (Object transition : leavingTransitions) {
                leavingTransitionList.add("\"'"
                        + ((Transition) transition).getName() + "'\"");
            }
        }
        return leavingTransitionList;
    }

    public List<String> getBooleanVariables() {
        if (booleanVariables == null || booleanVariables.isEmpty()) {
            List<String> list = new ArrayList<String>();
            list.add(VariableType.BOOLEAN.name());
            TaskHandlerVisitor visitor = new TaskHandlerVisitor(false, list);
            visitor.visit(this);
            booleanVariables = new ArrayList<String>();
            for (String string : visitor.getVariables()) {
                booleanVariables.add("\"" + string + "\"");
            }
        }
        return booleanVariables;
    }

    public String getDecisionExpression() {
        return decisionExpression;
    }

    public void setDecisionExpression(String decisionExpression) {
        this.decisionExpression = decisionExpression;
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
