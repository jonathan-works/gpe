package br.com.infox.epp.processo.form.type;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.graph.def.Event;

import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.form.FormData;
import br.com.infox.epp.processo.form.FormField;
import br.com.infox.epp.processo.form.StartFormData;
import br.com.infox.epp.processo.form.TaskFormData;
import br.com.infox.epp.processo.form.variable.value.EditorValueImpl;
import br.com.infox.epp.processo.form.variable.value.TypedValue;
import br.com.infox.epp.processo.form.variable.value.ValueType;

public class EditorFormType extends FileFormType {

    public EditorFormType() {
        super("editor", ValueType.EDITOR);
    }
    
    @Override
    public void performValue(FormField formField, FormData formData) {
        super.performValue(formField, formData);
        EditorValueImpl typedValue = (EditorValueImpl) formField.getTypedValue();
        List<ModeloDocumento> modelos = readModelosDocumento(formField, formData);
        typedValue.setModelosDocumento(modelos);
    }
    
    public void performModeloDocumento(EditorValueImpl editorValue, FormData formFata) {
        Documento documento = editorValue.getValue();
        ModeloDocumento modeloDocumento = editorValue.getModeloDocumento();
        String evaluatedModelo = "";
        if (modeloDocumento != null) {
            evaluatedModelo = getModeloDocumentoManager().evaluateModeloDocumento(modeloDocumento, formFata.getExpressionResolver());
        }
        documento.getDocumentoBin().setModeloDocumento(evaluatedModelo);
    }
    
    @Override
    public void performUpdate(FormField formField, FormData formData) {
        super.performUpdate(formField, formData);
        EditorValueImpl editorValue = (EditorValueImpl) formField.getTypedValue();
        Documento documento = editorValue.getValue();
        if (documento.getId() == null) {
            documento.setDescricao(formField.getLabel());
            documento.setClassificacaoDocumento(editorValue.getClassificacaoDocumento());
            getDocumentoBinManager().createProcessoDocumentoBin(documento);
            getDocumentoManager().gravarDocumentoNoProcesso(formData.getProcesso(), documento);
        } else {
            if (!documento.getClassificacaoDocumento().equals(editorValue.getClassificacaoDocumento())) {
                getDocumentoManager().update(documento);
            }
            getDocumentoBinManager().update(documento.getDocumentoBin());
        }
    }
    
    @Override
    public TypedValue convertToFormValue(Object value) {
        if (value == null) {
            Documento documento = createNewDocumento();
            return new EditorValueImpl(documento);
        } else {
            if (value instanceof String) {
                value = Integer.valueOf((String) value);
            }
            if (value instanceof Integer) {
                Documento documento = getDocumentoManager().find((Integer) value);
                return new EditorValueImpl(documento);
            }
        }
        return null;
    }

    private Documento createNewDocumento() {
        DocumentoBin documentoBin = new DocumentoBin();
        documentoBin.setMinuta(false);
        documentoBin.setModeloDocumento("");
        Documento documento = new Documento();
        documento.setAnexo(false);
        documento.setDocumentoBin(documentoBin);
        return documento;
    }
    
    private List<ModeloDocumento> readModelosDocumento(FormField formField, FormData formData) {
        Event event = null;
        if (formData instanceof StartFormData) {
            event = ((StartFormData) formData).getProcessDefinition().getStartState().getEvent(Event.EVENTTYPE_NODE_ENTER);
        } else if (formData instanceof TaskFormData) {
            event = ((TaskFormData) formData).getTaskInstance().getTask().getTaskNode().getEvent(Event.EVENTTYPE_NODE_ENTER);
        }
        List<ModeloDocumento> modelos = new ArrayList<>();
        if (event != null && event.getAction(formField.getId()) != null) {
            String expression = event.getAction(formField.getId()).getActionExpression();
            int start = expression.indexOf(",");
            int end = expression.indexOf(")", start);
            String modeloIds = expression.substring(start + 1, end);
            modelos = getModeloDocumentoManager().getModelosDocumentoInListaModelo(modeloIds);
        }
        return modelos;
    }
    
    protected ModeloDocumentoManager getModeloDocumentoManager() {
        return BeanManager.INSTANCE.getReference(ModeloDocumentoManager.class);
    }
}
