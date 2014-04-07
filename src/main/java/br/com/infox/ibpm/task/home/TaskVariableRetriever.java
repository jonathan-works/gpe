package br.com.infox.ibpm.task.home;

import static br.com.infox.ibpm.process.definition.variable.VariableType.EDITOR;
import static br.com.infox.ibpm.process.definition.variable.VariableType.FORM;
import static br.com.infox.ibpm.process.definition.variable.VariableType.MONETARY;

import java.util.Date;

import org.jboss.seam.Component;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.core.constants.FloatFormatConstants;
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

    public boolean isEditor() {
        return EDITOR.equals(type);
    }

    public boolean isForm() {
        return FORM.equals(type);
    }

    public boolean isWritable() {
        return variableAccess.isWritable();
    }

    public boolean isMonetario() {
        return MONETARY.equals(type) && (variable != null)
                && (variable.getClass().equals(Float.class));
    }

    public void formatVariableMonetaria() {
        variable = String.format(FloatFormatConstants.F2, variable);
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
        if (EDITOR.equals(type)) {
            variable = getConteudoEditor(variable);
        }
        if (variable == null && isHidden()) {
            switch (type) {
                case EDITOR:
                case TEXT:
                case STRING:
                case ENUMERATION:
                    variable = "_";
                    break;
                case BOOLEAN:
                    variable = true;
                    break;
                case DATE:
                    variable = new Date();
                    break;
                case INTEGER:
                    variable = "1";
                    break;
                case MONETARY:
                    variable = 0.0f;
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

    public void searchAndAssignConteudoToVariable() {
        variable = getConteudo();
    }

    public TaskVariableRetriever evaluateWhenDocumentoAssinado() {
        Integer id = (Integer) taskInstance.getVariable(getMappedName());
        AssinaturaDocumentoService documentoService = (AssinaturaDocumentoService) Component
                .getInstance(AssinaturaDocumentoService.NAME);
        if ((id != null) && (!documentoService.isDocumentoAssinado(id))
                && isWritable()) {
            ProcessoHome.instance().carregarDadosFluxo(id);
            return this;
        }
        return null;
    }

    public TaskVariableRetriever evaluateWhenMonetario() {
        if (isMonetario()) {
            setVariable(String.format(FloatFormatConstants.F2, getVariable()));
        }
        return this;
    }

    public TaskVariableRetriever evaluateWhenForm() {
        if (isForm()) {
            retrieveHomes();
            return this;
        }
        return null;
    }

}
