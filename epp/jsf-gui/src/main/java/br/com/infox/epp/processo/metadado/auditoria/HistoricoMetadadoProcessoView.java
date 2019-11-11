package br.com.infox.epp.processo.metadado.auditoria;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.manager.MetadadoProcessoManager;

@Named(HistoricoMetadadoProcessoView.NAME)
@ViewScoped
public class HistoricoMetadadoProcessoView implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "historicoMetadadoProcessoView";

    @Inject
    private HistoricoMetadadoProcessoSearch historicoSearch;
    @Inject
    private MetadadoProcessoManager metadadoManager;

    private MetadadoProcesso metadado;
    private Processo processo;

    public List<HistoricoMetadadoProcesso> getHistorico() {
        return historicoSearch.findAllByProcessoMetadado(processo.getIdProcesso(), metadado.getId());
    }

    public List<MetadadoProcesso> getMetadados() {
        return metadadoManager.getListMetadadoVisivelByProcesso(processo);
    }

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

    public MetadadoProcesso getMetadado() {
        return metadado;
    }

    public void setMetadado(MetadadoProcesso metadado) {
        this.metadado = metadado;
    }

}
