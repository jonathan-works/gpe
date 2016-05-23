package br.com.infox.epp.entrega;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.documento.ClassificacaoDocumentoSearch;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.entrega.modelo.ClassificacaoDocumentoEntrega;

public class ClassificacaoDocumentoEntregaController implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject private ClassificacaoDocumentoSearch classificacaoDocumentoSearch;
    @Inject private ModeloEntregaService modeloEntregaService;
    private ClassificacaoDocumento classificacaoDocumento;
    private Boolean multiplosDocumentos;
    private Boolean obrigatorio;
    private ClassificacaoDocumentoEntrega classificacaoDocumentoEntrega;
    private List<ClassificacaoDocumentoEntrega> classificacoesDocumentosEntrega;

    public void clear() {
        this.obrigatorio = null;
        this.multiplosDocumentos = null;
        this.classificacaoDocumento = null;

        this.classificacoesDocumentosEntrega = new ArrayList<>();
        this.classificacaoDocumentoEntrega = null;
    }

    public List<ClassificacaoDocumentoEntrega> getClassificacoesDocumentosEntrega() {
        return classificacoesDocumentosEntrega;
    }

    public void setClassificacoesDocumentosEntrega(
            List<ClassificacaoDocumentoEntrega> classificacoesDocumentosEntrega) {
        this.classificacoesDocumentosEntrega = classificacoesDocumentosEntrega;
    }

    public List<ClassificacaoDocumento> completeClassificacaoDocumento(String pattern) {
        List<ClassificacaoDocumento> result = classificacaoDocumentoSearch
                .findClassificacaoDocumentoWithDescricaoLike(pattern);
        for (ClassificacaoDocumentoEntrega documentoEntrega : getClassificacoesDocumentosEntrega()) {
            result.remove(documentoEntrega.getClassificacaoDocumento());
        }
        return result;
    }

    public ClassificacaoDocumento getClassificacaoDocumento() {
        return classificacaoDocumento;
    }

    public void setClassificacaoDocumento(ClassificacaoDocumento documentoEntrega) {
        this.classificacaoDocumento = documentoEntrega;
    }

    public Boolean getMultiplosDocumentos() {
        return multiplosDocumentos;
    }

    public void setMultiplosDocumentos(Boolean multiplosDocumentos) {
        this.multiplosDocumentos = multiplosDocumentos;
    }

    public Boolean getObrigatorio() {
        return obrigatorio;
    }

    public void setObrigatorio(Boolean obrigatorio) {
        this.obrigatorio = obrigatorio;
    }

    public ClassificacaoDocumentoEntrega getClassificacaoDocumentoEntrega() {
        return classificacaoDocumentoEntrega;
    }

    public void setClassificacaoDocumentoEntrega(ClassificacaoDocumentoEntrega classificacaoDocumentoEntrega) {
        this.classificacaoDocumentoEntrega = classificacaoDocumentoEntrega;
    }

    public boolean emEdicao(ClassificacaoDocumentoEntrega classificacaoDocumentoEntrega){
        return Objects.equals(this.classificacaoDocumentoEntrega, classificacaoDocumentoEntrega);
    }
    
    @ExceptionHandled
    public void adicionar() {
        ClassificacaoDocumentoEntrega classificacaoDocumentoEntrega = new ClassificacaoDocumentoEntrega();
        classificacaoDocumentoEntrega.setClassificacaoDocumento(getClassificacaoDocumento());
        classificacaoDocumentoEntrega.setMultiplosDocumentos(Boolean.TRUE.equals(getMultiplosDocumentos()));
        classificacaoDocumentoEntrega.setObrigatorio(Boolean.TRUE.equals(getObrigatorio()));

        getClassificacoesDocumentosEntrega().add(classificacaoDocumentoEntrega);
        setClassificacaoDocumento(null);
        setMultiplosDocumentos(null);
        setObrigatorio(null);
    }

    @ExceptionHandled
    public void remover(ClassificacaoDocumentoEntrega classificacaoDocumentoEntrega) {
        getClassificacoesDocumentosEntrega().remove(classificacaoDocumentoEntrega);
    }

    @ExceptionHandled
    public void editar(ClassificacaoDocumentoEntrega classificacaoDocumentoEntrega) {
        this.classificacaoDocumentoEntrega = classificacaoDocumentoEntrega;
    }
    
    @ExceptionHandled
    public void finalizarEdicao(){
        this.classificacaoDocumentoEntrega = null;
    }
}
