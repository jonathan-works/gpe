package br.com.infox.epp.processo.form.variable.value;

import java.util.List;

import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.processo.documento.entity.Documento;

public abstract class FileTypedValue implements TypedValue {
    
    protected Documento documento;
    protected ClassificacaoDocumento classificacaoDocumento;
    protected List<ClassificacaoDocumento> classificacoesDocumento;
    protected ValueType valueType;
    
    public FileTypedValue(Documento documento, ValueType valueType) {
        this.valueType = valueType;
        this.documento = documento;
    }
    
    @Override
    public ValueType getType() {
        return valueType;
    }

    public Documento getValue(){
        return documento;
    }
    
    public void setValue(Documento value) {
        this.documento = value;
    }
    
    public ClassificacaoDocumento getClassificacaoDocumento() {
        return classificacaoDocumento;
    }
    
    public void setClassificacaoDocumento(ClassificacaoDocumento classificacaoDocumento) {
        this.classificacaoDocumento = classificacaoDocumento;
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
    
    public List<ClassificacaoDocumento> getClassificacoesDocumento() {
        return classificacoesDocumento;
    }
    
    public void setClassificacoesDocumento(List<ClassificacaoDocumento> classificacoesDocumento) {
        this.classificacoesDocumento = classificacoesDocumento;
    }
    
    public boolean podeAssinar() {
        return documento != null && documento.getId() != null 
                && documento.isDocumentoAssinavel(Authenticator.getPapelAtual())
                && !documento.isDocumentoAssinado(Authenticator.getPapelAtual());
    }
    
}
