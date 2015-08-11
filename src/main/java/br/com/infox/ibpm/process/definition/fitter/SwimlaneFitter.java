package br.com.infox.ibpm.process.definition.fitter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jbpm.taskmgmt.def.Swimlane;

import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.access.manager.PerfilTemplateManager;
import br.com.infox.ibpm.process.definition.ProcessBuilder;
import br.com.infox.ibpm.swimlane.SwimlaneHandler;
import br.com.infox.seam.util.ComponentUtil;

@Scope(ScopeType.PAGE)
@Name(SwimlaneFitter.NAME)
@AutoCreate
public class SwimlaneFitter extends Fitter implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "swimlaneFitter";

    private List<SwimlaneHandler> swimlanes;
    private SwimlaneHandler currentSwimlane;
    private List<PerfilTemplate> perfisDisponiveis;

    private ProcessBuilder pb = ComponentUtil.getComponent(ProcessBuilder.NAME);
    
    @In 
    private PerfilTemplateManager perfilTemplateManager;

    public void addSwimlane() {
        Swimlane s = new Swimlane("Raia " + (swimlanes.size() + 1));
        s.setKey(UUID.randomUUID().toString());
        setCurrentSwimlane(new SwimlaneHandler(s));
        pb.getInstance().getTaskMgmtDefinition().addSwimlane(s);
        swimlanes.add(currentSwimlane);
    }

    public void removeSwimlane(SwimlaneHandler s) {
        swimlanes.remove(s);
        setCurrentSwimlane(null);
        pb.getInstance().getTaskMgmtDefinition().getSwimlanes().remove(s.getSwimlane());
    }

    public SwimlaneHandler getCurrentSwimlane() {
        return currentSwimlane;
    }

    public void setCurrentSwimlane(SwimlaneHandler cSwimlane) {
        this.currentSwimlane = cSwimlane;
    }

    public List<SwimlaneHandler> getSwimlanes() {
        if (swimlanes == null) {
            swimlanes = SwimlaneHandler.createList(pb.getInstance());
        }
        return swimlanes;
    }

    public List<String> getSwimlaneList() {
        Map<String, Swimlane> swimlaneList = pb.getInstance().getTaskMgmtDefinition().getSwimlanes();
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
