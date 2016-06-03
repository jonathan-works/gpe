package br.com.infox.epp.processo.form.variable.value;

import br.com.infox.epp.processo.documento.entity.Documento;

public class FileValue implements TypedValue {
    
    protected Documento documento;
    
    public FileValue(Documento documento) {
        this.documento = documento;
    }

    @Override
    public ValueType getType() {
        return ValueType.FILE;
    }
    
    @Override
    public Documento getValue(){
        return documento;
    }
    
    @Override
    public void setValue(Object object) {
        this.documento = (Documento) object;
    }
    
    public void setValue(Documento value) {
        this.documento = value;
    }
    
    public String getName() {
        return documento.getDocumentoBin().getNomeArquivo();
    }
    
    public String getExtension() {
        return documento.getDocumentoBin().getExtensao();
    }
    
    public String getDescription() {
        return documento.getDescricao();
    }
    
}
