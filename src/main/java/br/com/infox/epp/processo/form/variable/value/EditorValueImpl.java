package br.com.infox.epp.processo.form.variable.value;

import java.util.List;

import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.processo.documento.entity.Documento;

public class EditorValueImpl extends FileTypedValue {
    
    protected List<ModeloDocumento> modelosDocumento;
    protected ModeloDocumento modeloDocumento;
    
    public EditorValueImpl(Documento documento) {
        super(documento, ValueType.EDITOR);
    }
    
    public ModeloDocumento getModeloDocumento() {
        return modeloDocumento;
    }

    public void setModeloDocumento(ModeloDocumento modeloDocumento) {
        this.modeloDocumento = modeloDocumento;
    }

    public List<ModeloDocumento> getModelosDocumento() {
        return modelosDocumento;
    }

    public void setModelosDocumento(List<ModeloDocumento> modelosDocumento) {
        this.modelosDocumento = modelosDocumento;
    }

}
