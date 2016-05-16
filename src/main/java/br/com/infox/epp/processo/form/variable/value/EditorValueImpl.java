package br.com.infox.epp.processo.form.variable.value;

import java.util.List;

import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;

public class EditorValueImpl implements FileTypeValue {
    
    protected Documento documento;
    protected ClassificacaoDocumento classificacaoDocumento;
    protected List<ClassificacaoDocumento> classificacoesDocumento;
    protected List<ModeloDocumento> modelosDocumento;
    protected ModeloDocumento modeloDocumento;
    
    public EditorValueImpl(Documento documento) {
        this.documento = documento;
        this.classificacaoDocumento = documento.getClassificacaoDocumento();
    }
    
    @Override
    public Documento getValue() {
        prepareValue();
        return documento;
    }

    @Override
    public Class<Documento> getType() {
        return Documento.class;
    }

    @Override
    public String getName() {
        return documento.getDocumentoBin().getNomeArquivo();
    }

    @Override
    public String getExtension() {
        return documento.getDocumentoBin().getExtensao();
    }

    @Override
    public String getDescription() {
        return documento.getDescricao();
    }

    @Override
    public ClassificacaoDocumento getClassificacaoDocumento() {
        return classificacaoDocumento;
    }

    public DocumentoBin getDocumentoBin() {
        return documento.getDocumentoBin();
    }

    public List<ClassificacaoDocumento> getClassificacoesDocumento() {
        return classificacoesDocumento;
    }

    public void setClassificacoesDocumento(List<ClassificacaoDocumento> classificacoesDocumento) {
        this.classificacoesDocumento = classificacoesDocumento;
    }
    
    private void prepareValue() {
        documento.setClassificacaoDocumento(classificacaoDocumento);
    }
    
    public boolean podeAssinar() {
        return documento.getId() != null 
                && documento.isDocumentoAssinavel(Authenticator.getPapelAtual())
                && !documento.isDocumentoAssinado(Authenticator.getPapelAtual());
    }
    
}
