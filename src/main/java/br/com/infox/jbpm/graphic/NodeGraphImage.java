package br.com.infox.jbpm.graphic;

import org.hibernate.Hibernate;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.log.NodeLog;
import org.jbpm.graph.node.EndState;
import org.jbpm.graph.node.StartState;
import org.jbpm.graph.node.TaskNode;

public class NodeGraphImage extends GraphImageBean {
    
    private NodeLog nodeLog;
    private boolean current;
    
    public NodeGraphImage(NodeLog nodeLog, Boolean isCurrent) {
        super(nodeLog.getNode().getKey(), nodeLog.getToken());
        this.nodeLog = nodeLog;
        this.current = isCurrent;
    }
    
    public NodeGraphImage(NodeLog nodeLog) {
        this(nodeLog, false);
    }
    
    public NodeLog getNodeLog() {
        return nodeLog;
    }

    public Node getNode() {
        return nodeLog.getNode();
    }

    public boolean isCurrent() {
        return current;
    }
    
    public boolean isNotEndNodeAndStartNode() {
        return !isStartNode() && !isEndNode();
    }
    
    public boolean isTaskNode() {
        return Hibernate.getClass(getNode()).equals(TaskNode.class);
    }
    
    public boolean isStartNode() {
        return Hibernate.getClass(getNode()).equals(StartState.class);
    }
    
    public boolean isEndNode() {
        return Hibernate.getClass(getNode()).equals(EndState.class);
    }
    
}
