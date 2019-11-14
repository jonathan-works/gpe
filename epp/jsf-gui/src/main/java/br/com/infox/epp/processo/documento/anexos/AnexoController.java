package br.com.infox.epp.processo.documento.anexos;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.core.controller.AbstractController;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.entity.Processo;

@Named
@ViewScoped
public class AnexoController extends AbstractController {

    private static final long serialVersionUID = 1L;
    
    @Inject
    private DocumentoEditor documentoEditor;
    @Inject
    private DocumentoUploader documentoUploader;
    
    private Processo processo;
    private List<Documento> documentosDaSessao;
    
    @PostConstruct
    public void init() {
        documentosDaSessao = new ArrayList<>();
    }

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        if (getProcesso() == null) {
            this.processo = processo;
            onClickTabAnexar(processo);
        }
    }

    public List<Documento> getdocumentosDaSessao() {
        return documentosDaSessao;
    }

    public void setdocumentosDaSessao(List<Documento> documentosDaSessao) {
        this.documentosDaSessao = documentosDaSessao;
    }

    public void onClickTabAnexar(Processo processo) {
    	documentoEditor.setProcesso(processo);
    	documentoUploader.setProcesso(processo);
    	documentoEditor.clear();
    	documentoUploader.clear();
    }
	
}
