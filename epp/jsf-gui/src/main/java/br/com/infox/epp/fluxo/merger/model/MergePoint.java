package br.com.infox.epp.fluxo.merger.model;

import java.io.Serializable;

import org.jbpm.graph.def.ProcessDefinition;

public class MergePoint implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private long count;
    private boolean valid;

    public MergePoint() {
        this("", 0);
    }

    public MergePoint(String name) {
        this(name, 0);
    }

    public MergePoint(String name, long count) {
        setName(name);
        setCount(count);
        this.setValid(false);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public boolean isValid() {
        return valid;
    }

    private void setValid(boolean valid) {
        this.valid = valid;
    }
    
    public static final boolean isValid(MergePoint mergePoint, ProcessDefinition processDefinition){
        boolean valid = processDefinition != null && processDefinition.getNode(mergePoint.getName()) != null;
        mergePoint.setValid(valid);
        return mergePoint.isValid();
    }

    @Override
    public String toString() {
        return getName();
    }
    
}
