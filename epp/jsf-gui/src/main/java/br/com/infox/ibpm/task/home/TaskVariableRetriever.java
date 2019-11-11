package br.com.infox.ibpm.task.home;

import org.jbpm.context.def.VariableAccess;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.epp.cdi.util.Beans;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.ibpm.process.definition.variable.VariableType;
import br.com.infox.ibpm.variable.FragmentConfiguration;
import br.com.infox.ibpm.variable.FragmentConfigurationCollector;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
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
            case FRAGMENT:
                variable = getConteudoFragment(variable);
                break;
            case EDITOR:
                variable = getConteudoEditor(variable);
                break;
            case FILE:
                variable = getNomeFileUploaded(variable);
                break;
            default:
                break;
            }
        } else if (VariableType.FRAGMENT.equals(type)){
            variable = getConteudoFragment(variable);
        }
        return variable;
    }

	private Object getConteudoFragment(Object variable) {
        Object result = variable;
        if (result == null) {
            FragmentConfigurationCollector collector = Beans.getReference(FragmentConfigurationCollector.class);
            String code = variableAccess.getMappedName().split(":")[2];
            FragmentConfiguration fragmentConfiguration = collector.getByCode(code);
            result = fragmentConfiguration.init(taskInstance);
        }
        return result;
    }

    private Object getConteudoEditor(Object variable) {
        Integer idDocumento = (Integer) variable;
        if (idDocumento != null) {
            DocumentoManager documentoManager = ComponentUtil.getComponent(DocumentoManager.NAME);
            Object modeloDocumento = documentoManager.getModeloDocumentoByIdDocumento(idDocumento);
            if (modeloDocumento != null) {
                variable = modeloDocumento;
            } else {
                LOG.warn("Documento não encontrado: " + idDocumento);
            }
        }
        return variable;
    }
    
    private Object getNomeFileUploaded(Object variable) {
        Integer idDocumento = (Integer) variable;
        if (idDocumento != null) {
            DocumentoManager documentoManager = ComponentUtil.getComponent(DocumentoManager.NAME);
            Documento documento = documentoManager.find(idDocumento);
            if (documento != null) {
                variable = documento.getDescricao();
            } else {
                LOG.warn("Documento não encontrado: " + idDocumento);
            }
        }
        return variable;
    }
    
    public void retrieveVariableContent() {
        variable = getConteudo();
    }



}
