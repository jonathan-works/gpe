package br.com.infox.ibpm.process.definition.fitter;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jbpm.taskmgmt.def.Swimlane;

import br.com.infox.epp.access.entity.Perfil;
import br.com.infox.epp.access.manager.PerfilManager;
import br.com.infox.ibpm.process.definition.ProcessBuilder;
import br.com.infox.ibpm.swimlane.SwimlaneHandler;
import br.com.infox.seam.util.ComponentUtil;

@Name(SwimlaneFitter.NAME)
@AutoCreate
public class SwimlaneFitter extends Fitter implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "swimlaneFitter";

    private List<SwimlaneHandler> swimlanes;
    private SwimlaneHandler currentSwimlane;
    private List<Perfil> perfisDisponiveis;

    private ProcessBuilder pb = ComponentUtil.getComponent(ProcessBuilder.NAME);
    
    @In private PerfilManager perfilManager;

    public void addSwimlane() {
        Swimlane s = new Swimlane("Raia " + (swimlanes.size() + 1));
        setCurrentSwimlane(new SwimlaneHandler(s));
        pb.getInstance().getTaskMgmtDefinition().addSwimlane(s);
        swimlanes.add(currentSwimlane);
    }

    @SuppressWarnings(UNCHECKED)
    public void removeSwimlane(SwimlaneHandler s) {
        swimlanes.remove(s);
        setCurrentSwimlane(null);
        Map<String, Swimlane> swimlaneMap = pb.getInstance().getTaskMgmtDefinition().getSwimlanes();
        swimlaneMap.remove(s.getSwimlane().getName());
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

    @SuppressWarnings(UNCHECKED)
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

    public List<Perfil> getPerfisDisponiveis() {
        if (perfisDisponiveis == null) {
            perfisDisponiveis = perfilManager.listPerfisDentroDeEstrutura();
        }
        List<Perfil> perfisDisponiveisParaSwimlaneAtual;
        if (currentSwimlane != null) {
            perfisDisponiveisParaSwimlaneAtual = new ArrayList<>();
            for (Perfil perfil : perfisDisponiveis) {
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
