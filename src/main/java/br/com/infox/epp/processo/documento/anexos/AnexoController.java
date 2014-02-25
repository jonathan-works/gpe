package br.com.infox.epp.processo.documento.anexos;

import static br.com.itx.util.ComponentUtil.getComponent;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.controller.AbstractController;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.entity.Processo;

@Name(AnexoController.NAME)
@Scope(ScopeType.CONVERSATION)
public class AnexoController extends AbstractController {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "anexoController";

    private Processo processo;
    private List<DocumentoCreator> creators;
    private List<ProcessoDocumento> documentosDaSessao;

    @Create
    public void init() {
        creators = new ArrayList<>();
        creators.add((DocumentoCreator) getComponent(DocumentoUploader.NAME));
        creators.add((DocumentoCreator) getComponent(DocumentoEditor.NAME));
        documentosDaSessao = new ArrayList<>();
    }

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

    public List<ProcessoDocumento> getdocumentosDaSessao() {
        return documentosDaSessao;
    }

    public void setdocumentosDaSessao(List<ProcessoDocumento> documentosDaSessao) {
        this.documentosDaSessao = documentosDaSessao;
    }

    public void onClickTabAnexar(Processo processo) {
        for (DocumentoCreator creator : creators) {
            creator.setProcesso(processo);
            creator.clear();
        }
    }

}
