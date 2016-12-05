package br.com.infox.ibpm.process.definition.fitter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Named;

import org.jbpm.taskmgmt.def.Swimlane;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.ibpm.swimlane.SwimlaneHandler;

@Named
@ViewScoped
public class SwimlaneFitter extends Fitter implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<SwimlaneHandler> swimlanes;
    private SwimlaneHandler currentSwimlane;

    public SwimlaneHandler getCurrentSwimlane() {
        return currentSwimlane;
    }

    public void setCurrentSwimlane(SwimlaneHandler cSwimlane) {
        this.currentSwimlane = cSwimlane;
    }

    public List<SwimlaneHandler> getSwimlanes() {
        if (swimlanes == null) {
            swimlanes = SwimlaneHandler.createList(getProcessBuilder().getInstance());
        }
        return swimlanes;
    }

    public List<String> getSwimlaneList() {
        Map<String, Swimlane> swimlaneList = getProcessBuilder().getInstance().getTaskMgmtDefinition().getSwimlanes();
        if (swimlaneList == null) {
            return null;
        }
        return new ArrayList<String>(swimlaneList.keySet());
    }

    @Override
    public void clear() {
        swimlanes = null;
    }
}
