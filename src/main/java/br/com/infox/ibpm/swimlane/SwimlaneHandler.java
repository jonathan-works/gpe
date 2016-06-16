package br.com.infox.ibpm.swimlane;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.taskmgmt.def.Swimlane;

import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.access.manager.PerfilTemplateManager;
import br.com.infox.seam.util.ComponentUtil;

public class SwimlaneHandler implements Serializable {

    private static final long serialVersionUID = 7688420965362230234L;
    private Swimlane swimlane;
    private List<PerfilTemplate> perfilList = new ArrayList<>();
    private boolean dirty;
    private boolean contabilizar = false;
    private PerfilTemplate perfil;

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

    public void setPerfil(PerfilTemplate perfilTemplate) {
        this.perfil = perfilTemplate;
        this.contabilizar = false;
    }

    public PerfilTemplate getPerfil() {
        return perfil;
    }
    
    public void addPerfil() {
        if (perfil == null) {
            return;
        }
        getPerfilList().add(perfil);
        buildExpression();
        setPerfil(new PerfilTemplate());
    }

    public void removePerfil(PerfilTemplate perfil) {
        getPerfilList().remove(perfil);
        buildExpression();
    }

    private void buildExpression() {
        if (getPerfilList().isEmpty()) {
            swimlane.setPooledActorsExpression("");
        } else {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (PerfilTemplate perfil : getPerfilList()) {
                if (!first) {
                    sb.append(",");
                }
                sb.append(perfil.getCodigo());
                first = false;
            }
            String expression = sb.toString();
            dirty = !expression.equals(swimlane.getPooledActorsExpression());
            swimlane.setPooledActorsExpression(expression);
        }
    }

    public static List<SwimlaneHandler> createList(ProcessDefinition instance) {
        List<SwimlaneHandler> ret = new ArrayList<SwimlaneHandler>();
        Map<String, Swimlane> swimlanes = instance.getTaskMgmtDefinition().getSwimlanes();
        if (swimlanes == null) {
            return ret;
        }
        Collection<Swimlane> values = swimlanes.values();
        for (Swimlane swimlane : values) {
            SwimlaneHandler sh = new SwimlaneHandler(swimlane);
            sh.setPerfilList(new ArrayList<PerfilTemplate>());
            String exp = swimlane.getPooledActorsExpression();
            if (exp != null && !exp.isEmpty()) { //TODO verificar se existe alguma base com isso ainda, se não existir retirar esse if
                String[] perfis = exp.split(",");
                if (!perfis[0].startsWith("#")) { // Se começar com # é expressão com perfis, fluxo antigo
                    for (String perfil : perfis) {
                        sh.getPerfilList().add(perfilManager().getPerfilTemplateByCodigo(perfil));
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
        return this.getSwimlane().getKey().equals(other.getSwimlane().getKey());
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
    
    public void setPerfilList(List<PerfilTemplate> perfilList) {
        this.perfilList = perfilList;
    }
    
    public List<PerfilTemplate> getPerfilList() {
        return perfilList;
    }

    public void setContabilizar(boolean contabilizar) {
        this.contabilizar = contabilizar;
    }

    public boolean getContabilizar() {
        return contabilizar;
    }

    private static PerfilTemplateManager perfilManager() {
        return ComponentUtil.getComponent(PerfilTemplateManager.NAME);
    }
}
