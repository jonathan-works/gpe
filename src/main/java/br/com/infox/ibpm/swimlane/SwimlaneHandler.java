package br.com.infox.ibpm.swimlane;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.taskmgmt.def.Swimlane;

import br.com.infox.core.util.ReflectionsUtil;
import br.com.infox.epp.access.entity.Perfil;
import br.com.infox.epp.access.manager.PerfilManager;
import br.com.infox.seam.util.ComponentUtil;

public class SwimlaneHandler implements Serializable {

    private static final long serialVersionUID = 7688420965362230234L;
    private Swimlane swimlane;
    private List<Perfil> perfilList = new ArrayList<>();
    private boolean dirty;
    private boolean contabilizar = false;
    private Perfil perfil;

    public SwimlaneHandler(Swimlane swimlane) {
        this.swimlane = swimlane;
    }

    public Swimlane getSwimlane() {
        return swimlane;
    }

    public void setSwimlane(Swimlane swimlane) {
        this.swimlane = swimlane;
    }

    public String getName() {
        return swimlane.getName();
    }

    @SuppressWarnings(UNCHECKED)
    public void setName(String name) {
        Map<String, Swimlane> swimlanes = swimlane.getTaskMgmtDefinition().getSwimlanes();
        swimlanes.remove(swimlane.getName());
        ReflectionsUtil.setValue(swimlane, "name", name);
        swimlane.getTaskMgmtDefinition().addSwimlane(swimlane);
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
        this.contabilizar = false;
    }

    public Perfil getPerfil() {
        return perfil;
    }
    
    public void addPerfil() {
        if (perfil == null) {
            return;
        }
        getPerfilList().add(perfil);
        buildExpression();
        setPerfil(new Perfil());
    }

    public void removePerfil(Perfil perfil) {
        getPerfilList().remove(perfil);
        buildExpression();
    }

    private void buildExpression() {
        if (getPerfilList().isEmpty()) {
            swimlane.setPooledActorsExpression("");
        } else {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (Perfil perfil : getPerfilList()) {
                if (!first) {
                    sb.append(",");
                }
                sb.append(perfil.getIdPerfil());
                first = false;
            }
            String expression = sb.toString();
            dirty = !expression.equals(swimlane.getPooledActorsExpression());
            swimlane.setPooledActorsExpression(expression);
        }
    }

    @SuppressWarnings(UNCHECKED)
    public static List<SwimlaneHandler> createList(ProcessDefinition instance) {
        List<SwimlaneHandler> ret = new ArrayList<SwimlaneHandler>();
        Map<String, Swimlane> swimlanes = instance.getTaskMgmtDefinition().getSwimlanes();
        if (swimlanes == null) {
            return ret;
        }
        Collection<Swimlane> values = swimlanes.values();
        for (Swimlane swimlane : values) {
            SwimlaneHandler sh = new SwimlaneHandler(swimlane);
            sh.setPerfilList(new ArrayList<Perfil>());
            String exp = swimlane.getPooledActorsExpression();
            if (exp != null && !exp.isEmpty()) {
                String[] perfis = exp.split(",");
                if (!perfis[0].startsWith("#")) { // Se começar com # é expressão com perfis, fluxo antigo
                    for (String perfil : perfis) {
                        sh.getPerfilList().add(perfilManager().find(Integer.valueOf(perfil)));
                    }
                }
            }
            ret.add(sh);
        }
        return ret;
    }

    public boolean isDirty() {
        return dirty;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SwimlaneHandler)) {
            return false;
        }
        SwimlaneHandler other = (SwimlaneHandler) obj;
        return this.getSwimlane().equals(other.getSwimlane());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.getSwimlane().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return swimlane.getName();
    }
    
    public void setPerfilList(List<Perfil> perfilList) {
        this.perfilList = perfilList;
    }
    
    public List<Perfil> getPerfilList() {
        return perfilList;
    }

    public void setContabilizar(boolean contabilizar) {
        this.contabilizar = contabilizar;
    }

    public boolean getContabilizar() {
        return contabilizar;
    }

    private static PerfilManager perfilManager() {
        return ComponentUtil.getComponent(PerfilManager.NAME);
    }
}
