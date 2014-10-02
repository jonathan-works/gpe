package br.com.infox.ibpm.task.home;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.seam.util.ComponentUtil;

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

    private Object getConteudo() {
        Object variable = taskInstance.getVariable(getMappedName());
        if (variable != null) {
            switch (type) {
                case EDITOR:
                    variable = getConteudoEditor(variable);
                    break;
                case FILE:
                    variable = getNomeFileUploaded(variable);
                default:
                    break;
            }
        }
        return variable;
    }

    private Object getConteudoEditor(Object variable) {
        Integer idProcessoDocumento = (Integer) variable;
        if (idProcessoDocumento != null) {
            DocumentoManager processoDocumentoManager = ComponentUtil
                    .getComponent(DocumentoManager.NAME);
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
    
    private Object getNomeFileUploaded(Object variable) {
        Integer idProcessoDocumento = (Integer) variable;
        if (idProcessoDocumento != null) {
            DocumentoManager processoDocumentoManager = ComponentUtil
                    .getComponent(DocumentoManager.NAME);
            Documento documento = processoDocumentoManager
                    .find(idProcessoDocumento);
            if (documento != null) {
                variable = documento.getProcessoDocumento();
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



}
