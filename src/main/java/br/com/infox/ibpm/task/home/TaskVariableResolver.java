package br.com.infox.ibpm.task.home;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.epp.processo.home.ProcessoHome;
import br.com.infox.ibpm.util.JbpmUtil;

final class TaskVariableResolver extends TaskVariable {

    private static final LogProvider LOG = Logging.getLogProvider(TaskVariableResolver.class);
    public static final int SUCCESS = 0x1;
    public static final int FAIL = 0x2;
    public static final int SIGNED = 0x8;

    private Object value;
    private boolean assinarDocumento;
    private int resolve;

    public TaskVariableResolver(VariableAccess variableAccess,
            TaskInstance taskInstance) {
        super(variableAccess, taskInstance);
        this.assinarDocumento = false;
    }
    
    public TaskVariableResolver(VariableAccess variableAccess,
            TaskInstance taskInstance, boolean assinar) {
        super(variableAccess, taskInstance);
        this.assinarDocumento = assinar;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
    
    public void resolve() {
        resolve = 0;
        if (value != null) {
            switch (type) {
                case MONETARY:
                    if (value instanceof String) {
                        try {
                            value = NumberFormat.getNumberInstance().parse(value.toString()).doubleValue();
                        } catch (ParseException e) {}
                    }
                    atribuirValorDaVariavelNoContexto();
                    break;
                case INTEGER:
                    if (value instanceof String) {
                        try {
                            value = NumberFormat.getNumberInstance().parse(value.toString()).longValue();
                        } catch (ParseException e) {}
                    }
                    atribuirValorDaVariavelNoContexto();
                    break;
                case DATE:
                    if (value instanceof String) {
                        try {
                            value = DateFormat.getDateInstance( DateFormat.MEDIUM).parse(value.toString());
                        } catch (ParseException e) {}
                    }
                    atribuirValorDaVariavelNoContexto();
                    break;
                case EDITOR:
                    resolveEditor();
                    break;
                default:
                    atribuirValorDaVariavelNoContexto();
                    break;
            }
        }
    }
    
    private void resolveEditor() {
        try {
            ProcessoHome processoHome = ProcessoHome.instance();
            Integer valueInt = processoHome.salvarProcessoDocumentoFluxo(value, getIdDocumento(), assinarDocumento, getLabel());
            resolve = resolve | SIGNED;
            
            if (valueInt != null && valueInt != 0) {
                this.value = valueInt;
                atribuirValorDaVariavelNoContexto();
            }
        } catch (CertificadoException e) {
            LOG.error("Falha na assinatura", e);
        }
    }

    public boolean isEditorAssinado() {
        return (resolve & TaskVariableResolver.SIGNED) == TaskVariableResolver.SIGNED;
    }
    
    private String getLabel() {
        return JbpmUtil.instance().getMessages().get(name);
    }

    private Integer getIdDocumento() {
        Object variable = taskInstance.getVariable(variableAccess.getMappedName());
        if (variable != null) {
            return (Integer) variable;
        } else {
            return null;
        }
    }

    public void atribuirValorDaVariavelNoContexto() {
        Contexts.getBusinessProcessContext().set(variableAccess.getMappedName(), value);
        resolve = resolve | SUCCESS;
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

    public void assignValueFromMapaDeVariaveis(Map<String, Object> mapaDeVariaveis) {
        value = getValueFromMapaDeVariaveis(mapaDeVariaveis);
    }
}
