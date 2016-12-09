package br.com.infox.ibpm.variable;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jboss.seam.util.Strings;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.taskmgmt.def.TaskController;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.util.ComponentUtil;

@Name(VariableHandler.NAME)
@Transactional
public class VariableHandler implements Serializable {
	
    private static final long serialVersionUID = -6777955765635127593L;

    public static final String NAME = "variableHandler";

    private static final LogProvider LOG = Logging.getLogProvider(VariableHandler.class);
    
    @In
    private DocumentoManager documentoManager;

    public List<Variavel> getVariables(long taskId) {
        return getVariables(taskId, false);
    }

    public List<Variavel> getTaskVariables(long taskId) {
        return getVariables(taskId, true);
    }

    @SuppressWarnings(UNCHECKED)
    private List<Variavel> getVariables(long taskId, boolean readOnly) {
        List<Variavel> ret = new ArrayList<Variavel>();
        TaskInstance taskInstance = ManagedJbpmContext.instance().getTaskInstanceForUpdate(taskId);

        TaskController taskController = taskInstance.getTask().getTaskController();
        if (taskController != null) {
            List<VariableAccess> list = taskController.getVariableAccesses();
            for (VariableAccess var : list) {
                if (readOnly && !var.isWritable()) {
                    continue;
                }
                String type = var.getMappedName().split(":")[0];
                try {
                    String name = var.getMappedName().split(":")[1];
                    Object value = taskInstance.getVariable(var.getMappedName());
                    if (value != null && !"".equals(value)) {
                        ret.add(new Variavel(getLabel(taskInstance.getTask().getProcessDefinition().getName() + ":" + name), value, type));
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    LOG.error("Varivel com Valor inválido: "
                            + Strings.toString(var), e);
                }
            }
        }
        return ret;
    }

    public static String getLabel(String name) {
        Map<String, String> map = ComponentUtil.getComponent("jbpmMessages");
        if (map.containsKey(name)) {
            return map.get(name);
        } else {
            String[] split = name.split(":");
            String altName = split[split.length-1];
            if (altName.length() > 1) {
                String label = altName.substring(0, 1).toUpperCase()
                        + altName.substring(1);
                return label.replaceAll("_", " ");
            } else {
                return altName;
            }
        }
    }

    public static VariableHandler instance() {
        return (VariableHandler) Component.getInstance(NAME);
    }

    public String getNumeroDocumentoTarefa(long taskId) {
        for (Variavel variavel : getTaskVariables(taskId)) {
            if (JbpmUtil.isTypeEditor(variavel.getType())) {
                return " - Doc. Nr: " + documentoManager.find(variavel.getValue()).getNumeroDocumento();
            }
        }
        return null;
    }
}
