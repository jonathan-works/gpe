package br.com.infox.epp.processo.form.type;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.graph.def.Event;

import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.form.FormData;
import br.com.infox.epp.processo.form.FormField;
import br.com.infox.epp.processo.form.variable.value.TypedValue;
import br.com.infox.epp.processo.form.variable.value.ValueType;

public class EditorFormType extends FileFormType {

    public EditorFormType() {
        super("editor", "/Processo/form/editor.xhtml");
    }
    
    @Override
    public void performValue(FormField formField, FormData formData) {
        super.performValue(formField, formData);
        List<ModeloDocumento> modelos = readModelosDocumento(formField, formData);
        formField.addProperty("modelosDocumento", modelos);
        formField.addProperty("modeloDocumento", null);
        Documento documento = formField.getTypedValue(Documento.class);
        if (documento != null && documento.getId() != null) {
            formField.addProperty("classificacaoDocumento", documento.getClassificacaoDocumento());
        }
    }
    
    public void performModeloDocumento(FormField formField, FormData formFata) {
        Documento documento = formField.getTypedValue(Documento.class);
        ModeloDocumento modeloDocumento = formField.getProperty("modeloDocumento", ModeloDocumento.class);
        String evaluatedModelo = "";
        if (modeloDocumento != null) {
            evaluatedModelo = getModeloDocumentoManager().evaluateModeloDocumento(modeloDocumento, formFata.getExpressionResolver());
        }
        documento.getDocumentoBin().setModeloDocumento(evaluatedModelo);
    }
    
    @Override
    public void performUpdate(FormField formField, FormData formData) {
        super.performUpdate(formField, formData);
        Documento documento = formField.getTypedValue(Documento.class);
        ClassificacaoDocumento classificacaoDocumento = formField.getProperty("classificacaoDocumento", ClassificacaoDocumento.class);
        if (classificacaoDocumento == null) {
            if (documento.getId() != null) {
                formData.setVariable(formField.getId(), new TypedValue(null, ValueType.FILE));
                formField.addProperty("modeloDocumento", null);
                getDocumentoManager().remove(documento);
                getDocumentoBinManager().remove(documento.getDocumentoBin());
                documento = createNewDocumento();
                formField.setValue(documento);
            } else {
                formField.addProperty("modeloDocumento", null);
                documento.getDocumentoBin().setModeloDocumento("");
            }
        } else {
            if (documento.getId() != null) {
                if (!classificacaoDocumento.equals(documento.getClassificacaoDocumento())) {
                    documento = getDocumentoManager().update(documento);
                }
                DocumentoBin documentoBin = getDocumentoBinManager().update(documento.getDocumentoBin());
                documento.setDocumentoBin(documentoBin);
                formField.setValue(documento);
            } else {
                documento.setDescricao(formField.getLabel());
                documento.setClassificacaoDocumento(classificacaoDocumento);
                if (documento.getDocumentoBin().getModeloDocumento() == null) documento.getDocumentoBin().setModeloDocumento("");
                getDocumentoBinManager().createProcessoDocumentoBin(documento);
                getDocumentoManager().gravarDocumentoNoProcesso(formData.getProcesso(), documento);
            }
        }
    }
    
    @Override
    public Object convertToFormValue(Object value) {
        if (value == null) {
            Documento documento = createNewDocumento();
            return documento;
        } else {
            if (value instanceof String) {
                value = Integer.valueOf((String) value);
            }
            if (value instanceof Integer) {
                Documento documento = getDocumentoManager().find((Integer) value);
                return documento;
            }
        }
        throw new IllegalArgumentException("Cannot convert " + value + " to Documento");
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
        Event event = formData.getNode().getEvent(Event.EVENTTYPE_NODE_ENTER);
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
