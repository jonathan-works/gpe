package br.com.infox.epp.processo.documento.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.faces.FacesMessages;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.RelacionamentoProcessoInterno;
import br.com.infox.epp.relacionamentoprocessos.RelacionamentoProcessoDAO;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Named
@ViewScoped
public class PastaCompartilhamentoView implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private PastaCompartilhamentoSearch pastaCompartilhamentoSearch;
    @Inject
    private PastaCompartilhamentoService pastaCompartilhamentoService;
    @Inject
    private RelacionamentoProcessoDAO relacionamentoProcessoDAO;

    private LogProvider LOG = Logging.getLogProvider(PastaCompartilhamentoView.class);

    private Pasta pasta;
    private List<Processo> processosRelacionados;
    private Map<Integer, Boolean> compartilhamento;

    public void initWithPasta(Pasta pasta) {
        this.pasta = pasta;
        Processo processo = pasta.getProcesso().getProcessoRoot();
        List<RelacionamentoProcessoInterno> relacionamentoList = relacionamentoProcessoDAO.getListProcessosEletronicosRelacionados(processo);
        processosRelacionados = new ArrayList<>(relacionamentoList.size());
        for (RelacionamentoProcessoInterno relacionamentoProcesso : relacionamentoList) {
            processosRelacionados.add(relacionamentoProcesso.getProcesso());
        }
        compartilhamento = new HashMap<>(processosRelacionados.size());
    }

    public String getDialogTitle() {
        return pasta == null ? "" : pasta.getNome();
    }

    public Boolean possuiCompartilhamento(Processo processo) {
        Boolean resp = compartilhamento.get(processo.getIdProcesso());
        if (resp == null) {
            resp = pastaCompartilhamentoSearch.possuiCompartilhamento(pasta, processo);
            compartilhamento.put(processo.getIdProcesso(), resp);
        }
        return resp;
    }

    public void adicionarCompartilhamento(Processo processo) {
        try {
            pastaCompartilhamentoService.adicionarCompartilhamento(pasta, processo);
            compartilhamento.put(processo.getIdProcesso(), true);
        } catch (Exception e) {
            LOG.error("pastaCompartilhamentoView.adicionarCompartilhamento(processo)", e);
            FacesMessages.instance().add("Falha ao tentar configurar compartilhamento. Favor tentar novamente");
        }
    }

    public void removerCompartilhamento(Processo processo) {
        try {
            pastaCompartilhamentoService.removerCompartilhamento(pasta, processo);
            compartilhamento.put(processo.getIdProcesso(), false);
        } catch (Exception e) {
            LOG.error("pastaCompartilhamentoView.removerCompartilhamento(processo)", e);
            FacesMessages.instance().add("Falha ao tentar configurar compartilhamento. Favor tentar novamente");
        }
    }

    public Pasta getPasta() {
        return pasta;
    }

    public List<Processo> getProcessosRelacionados() {
        return processosRelacionados;
    }
}
