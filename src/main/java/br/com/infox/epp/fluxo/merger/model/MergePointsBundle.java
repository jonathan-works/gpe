package br.com.infox.epp.fluxo.merger.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jbpm.graph.def.ProcessDefinition;

public class MergePointsBundle {

    private List<MergePoint> mergePoints;
    private ProcessDefinition processDefinition;
    private boolean valid;

    public MergePointsBundle(){
        this(new ArrayList<MergePoint>());
    }
    
    public MergePointsBundle(List<MergePoint> mergePoints) {
        this(mergePoints, null);
    }
    
    public MergePointsBundle(List<MergePoint> mergePoints, ProcessDefinition processDefinition) {
        this.mergePoints = mergePoints;
        this.valid = true;
        setProcessDefinition(processDefinition);
    }

    public List<MergePoint> getMergePoints() {
        return mergePoints;
    }
    
    public boolean isValid(){
        return valid;
    }

    public ProcessDefinition getProcessDefinition() {
        return processDefinition;
    }

    public void setProcessDefinition(ProcessDefinition processDefinition) {
        this.processDefinition = processDefinition;
        updateValidField();
    }

    private void updateValidField() {
        valid = true;
        for (MergePoint mergePoint : mergePoints) {
            valid &= MergePoint.isValid(mergePoint, getProcessDefinition());
        }
    }
    
    public List<MergePoint> getInvalidMergePoints(){
        List<MergePoint> invalids = new ArrayList<>();
        for (Iterator<MergePoint> iterator = mergePoints.iterator(); iterator.hasNext();) {
            MergePoint mergePoint = iterator.next();
            if (!mergePoint.isValid()){
                invalids.add(mergePoint);
            }
        }
        return invalids;
    }
    
}
