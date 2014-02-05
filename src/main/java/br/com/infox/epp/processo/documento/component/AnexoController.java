package br.com.infox.epp.processo.documento.component;

import java.util.List;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.controller.AbstractController;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.entity.Processo;

@Name(AnexoController.NAME)
public class AnexoController extends AbstractController {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "anexoController";
    
    private Processo processo;
    private List<ProcessoDocumento> processoDocumentosDaSessao;
    
    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

    public List<ProcessoDocumento> getProcessoDocumentosDaSessao() {
        return processoDocumentosDaSessao;
    }

    public void setProcessoDocumentosDaSessao(
            List<ProcessoDocumento> processoDocumentosDaSessao) {
        this.processoDocumentosDaSessao = processoDocumentosDaSessao;
    }

    @Override
    public void onClickFormTab() {
        super.onClickFormTab();
    }

}
