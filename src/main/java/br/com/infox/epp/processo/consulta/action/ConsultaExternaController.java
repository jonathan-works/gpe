package br.com.infox.epp.processo.consulta.action;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.controller.AbstractController;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.entity.ProcessoEpa;
import br.com.infox.epp.tarefa.entity.ProcessoTarefa;

@Scope(ScopeType.CONVERSATION)
@Name(ConsultaExternaController.NAME)
public class ConsultaExternaController extends AbstractController {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "consultaExternaController";
    public static final String TAB_VIEW = "processoExternoView";

    @In
    private DocumentoManager documentoManager;

    private ProcessoEpa processoEpa;

    public ProcessoEpa getProcessoEpa() {
        return processoEpa;
    }

    public void setProcessoEpa(ProcessoEpa processoEpa) {
        this.processoEpa = processoEpa;
    }

    public void selectProcesso(ProcessoEpa processoEpa) {
        setTab(TAB_VIEW);
        setProcessoEpa(processoEpa);
    }

    public List<Documento> getAnexosPublicos(ProcessoTarefa processoTarefa) {
        return documentoManager.getAnexosPublicos(processoTarefa.getTaskInstance());
    }

    public void onClickSearchNumeroTab() {
        setProcessoEpa(null);
    }

    public void onClickSearchParteTab() {
        setProcessoEpa(null);
    }
}
