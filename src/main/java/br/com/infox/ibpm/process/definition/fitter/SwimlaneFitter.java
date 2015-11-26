package br.com.infox.ibpm.process.definition.fitter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;

import org.jbpm.taskmgmt.def.Swimlane;

import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.access.manager.PerfilTemplateManager;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.ibpm.swimlane.SwimlaneHandler;

@Named
@ViewScoped
public class SwimlaneFitter extends Fitter implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<SwimlaneHandler> swimlanes;
    private SwimlaneHandler currentSwimlane;
    private List<PerfilTemplate> perfisDisponiveis;

    @Inject
    private PerfilTemplateManager perfilTemplateManager;

    public void addSwimlane() {
        Swimlane s = new Swimlane("Raia " + (swimlanes.size() + 1));
        s.setKey(UUID.randomUUID().toString());
        setCurrentSwimlane(new SwimlaneHandler(s));
        getProcessBuilder().getInstance().getTaskMgmtDefinition().addSwimlane(s);
        swimlanes.add(currentSwimlane);
    }

    public void removeSwimlane(SwimlaneHandler s) {
        swimlanes.remove(s);
        setCurrentSwimlane(null);
        getProcessBuilder().getInstance().getTaskMgmtDefinition().getSwimlanes().remove(s.getSwimlane());
    }

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

    public List<PerfilTemplate> getPerfisDisponiveis() {
        if (perfisDisponiveis == null) {
            perfisDisponiveis = perfilTemplateManager.listPerfisDentroDeEstrutura();
        }
        List<PerfilTemplate> perfisDisponiveisParaSwimlaneAtual;
        if (currentSwimlane != null) {
            perfisDisponiveisParaSwimlaneAtual = new ArrayList<>();
            for (PerfilTemplate perfil : perfisDisponiveis) {
                if (!currentSwimlane.getPerfilList().contains(perfil)) {
                    perfisDisponiveisParaSwimlaneAtual.add(perfil);
                }
            }
        } else {
            perfisDisponiveisParaSwimlaneAtual = perfisDisponiveis;
        }
        return perfisDisponiveisParaSwimlaneAtual;
    }
}
