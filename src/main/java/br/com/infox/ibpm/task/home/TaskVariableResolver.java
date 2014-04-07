package br.com.infox.ibpm.task.home;

import java.text.DateFormat;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.Messages;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.epp.processo.home.ProcessoHome;
import br.com.infox.ibpm.process.definition.variable.VariableType;
import br.com.infox.ibpm.util.JbpmUtil;

final class TaskVariableResolver extends TaskVariable {

    private static final LogProvider LOG = Logging.getLogProvider(TaskVariableResolver.class);

    private Object value;

    public TaskVariableResolver(VariableAccess variableAccess,
            TaskInstance taskInstance) {
        super(variableAccess, taskInstance);
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
    
    public void resolve() {
        if (VariableType.MONETARY.equals(type) && value != null) {
            String val = String.valueOf(value);
            try {
                value = Float.parseFloat(val);
            } catch (NumberFormatException e) {
                value = Float.parseFloat(val.replace(".", "").replace(",", "."));
            }
        } else if (VariableType.DATE.equals(type) && value != null) {
            try {
                value = DateFormat.getDateInstance( DateFormat.MEDIUM).format(value);
            } catch (IllegalArgumentException e) {
                LOG.warn(".resolveWhenDate()", e);
            }
        }
    }

    public void resolveWhenEditor(boolean assinar) throws CertificadoException {
        Integer valueInt = salvarProcessoDocumento(assinar);
        if (valueInt != null && valueInt != 0) {
            this.value = valueInt;
            atribuirValorDaVariavelNoContexto();
            if (assinar) {
                FacesMessages.instance().add(Messages.instance().get("assinatura.assinadoSucesso"));
            }
        }
    }

    public boolean isEditor() {
        return VariableType.EDITOR.equals(type);
    }

    private String getLabel() {
        return JbpmUtil.instance().getMessages().get(name);
    }

    private Integer getIdDocumento() {
        if (taskInstance.getVariable(variableAccess.getMappedName()) != null) {
            return (Integer) taskInstance.getVariable(variableAccess.getMappedName());
        } else {
            return null;
        }
    }

    private Integer salvarProcessoDocumento(boolean assinar) throws CertificadoException {
        return ProcessoHome.instance().salvarProcessoDocumentoFluxo(value, getIdDocumento(), assinar, getLabel());
    }

    public void atribuirValorDaVariavelNoContexto() {
        Contexts.getBusinessProcessContext().set(variableAccess.getMappedName(), value);
    }

    private Object getValueFromMapaDeVariaveis(
            Map<String, Object> mapaDeVariaveis) {
        if (mapaDeVariaveis == null) {
            return null;
        }
        Set<Entry<String, Object>> entrySet = mapaDeVariaveis.entrySet();
        for (Entry<String, Object> entry : entrySet) {
            if (entry.getKey().split("-")[0].equals(name)
                    && entry.getValue() != null) {
                return entry.getValue();
            }
        }
        return null;
    }

    public void assignValueFromMapaDeVariaveis(
            Map<String, Object> mapaDeVariaveis) {
        value = getValueFromMapaDeVariaveis(mapaDeVariaveis);
    }
}
