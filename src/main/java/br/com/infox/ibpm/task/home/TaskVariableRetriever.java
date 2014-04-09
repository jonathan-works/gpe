package br.com.infox.ibpm.task.home;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import org.jboss.seam.Component;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumentoService;
import br.com.infox.epp.processo.documento.manager.ProcessoDocumentoManager;
import br.com.infox.epp.processo.home.ProcessoHome;
import br.com.infox.seam.util.ComponentUtil;
import br.com.itx.component.AbstractHome;

final class TaskVariableRetriever extends TaskVariable {

    private Object variable;

    private static final LogProvider LOG = Logging
            .getLogProvider(TaskVariableRetriever.class);

    public TaskVariableRetriever(VariableAccess variableAccess,
            TaskInstance taskInstance) {
        super(variableAccess, taskInstance);
    }

    public Object getVariable() {
        return variable;
    }

    public void setVariable(Object variable) {
        this.variable = variable;
    }

    public boolean hasVariable() {
        return variable != null;
    }

    public void setVariablesHome() {
        AbstractHome<?> home = ComponentUtil.getComponent(getName() + "Home");
        home.setId(getVariable());
    }

    public void retrieveHomes() {
        if (hasVariable()) {
            setVariablesHome();
        }
    }

    private Object getConteudo() {
        Object variable = taskInstance.getVariable(getMappedName());
        if (variable != null) {
            switch (type) {
                case EDITOR:
                    variable = getConteudoEditor(variable);
                    break;
                case DATE:
                    try {
                        variable = DateFormat.getDateInstance( DateFormat.MEDIUM).parse(variable.toString());
                    } catch (ParseException e) {
                        LOG.warn("parseDateFail", e);
                    }
                    break;
                case MONETARY:
                    try {
                        variable = NumberFormat.getNumberInstance().parse(variable.toString());
                    } catch (ParseException e) {
                        LOG.warn("parseNumberFail", e);
                    }
                    break;
                default:
                    break;
            }
        }
        return variable;
    }

    private Object getConteudoEditor(Object variable) {
        Integer idProcessoDocumento = (Integer) variable;
        if (idProcessoDocumento != null) {
            ProcessoDocumentoManager processoDocumentoManager = ComponentUtil
                    .getComponent(ProcessoDocumentoManager.NAME);
            Object modeloDocumento = processoDocumentoManager
                    .getModeloDocumentoByIdProcessoDocumento(idProcessoDocumento);
            if (modeloDocumento != null) {
                variable = modeloDocumento;
            } else {
                LOG.warn("ProcessoDocumento não encontrado: "
                        + idProcessoDocumento);
            }
        }
        return variable;
    }

    public void retrieveVariableContent() {
        variable = getConteudo();
    }

    public boolean isValid() {
        boolean result = true;
        if (variable != null) {
            switch (type) {
                case EDITOR:
                    result = isDocumentoAssinadoValid();
                    break;
                case MONETARY:
                    if (!(variable instanceof String)) {
                        variable = NumberFormat.getInstance().format(variable);
                    }
                    break;
                case FORM:
                    retrieveHomes();
                    break;
                default:
                    break;
            }
        }
        return result;
    }
    
    private boolean isDocumentoAssinadoValid() {
        Integer id = (Integer) taskInstance.getVariable(getMappedName());
        AssinaturaDocumentoService documentoService = (AssinaturaDocumentoService) Component
                .getInstance(AssinaturaDocumentoService.NAME);
        if ((id != null) && (!documentoService.isDocumentoAssinado(id))
                && isWritable()) {
            ProcessoHome.instance().carregarDadosFluxo(id);
            return true;
        }
        return false;
    }

}
