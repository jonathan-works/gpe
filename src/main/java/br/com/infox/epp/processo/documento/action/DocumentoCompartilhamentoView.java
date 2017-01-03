package br.com.infox.epp.processo.documento.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.faces.FacesMessages;

import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.RelacionamentoProcessoInterno;
import br.com.infox.epp.relacionamentoprocessos.RelacionamentoProcessoDAO;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Named
@ViewScoped
public class DocumentoCompartilhamentoView implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private DocumentoCompartilhamentoSearch documentoCompartilhamentoSearch;
    @Inject
    private DocumentoCompartilhamentoService documentoCompartilhamentoService;
    @Inject
    private RelacionamentoProcessoDAO relacionamentoProcessoDAO;

    private LogProvider LOG = Logging.getLogProvider(DocumentoCompartilhamentoView.class);

    private Documento documento;
    private List<Processo> processosRelacionados;
    private Map<Integer, Boolean> compartilhamento;

    public void initWithDocumento(Documento documento) {
        this.documento = documento;
        Processo processo = documento.getPasta().getProcesso().getProcessoRoot();
        List<RelacionamentoProcessoInterno> relacionamentoList = relacionamentoProcessoDAO.getListProcessosEletronicosRelacionados(processo);
        processosRelacionados = new ArrayList<>(relacionamentoList.size());
        for (RelacionamentoProcessoInterno relacionamentoProcesso : relacionamentoList) {
            processosRelacionados.add(relacionamentoProcesso.getProcesso());
        }
        compartilhamento = new HashMap<>(processosRelacionados.size());
    }

    public String getDialogTitle() {
        return documento == null ? "" : documento.getNumeroDocumento() + " - " + documento.getDescricao();
    }

    public Boolean possuiCompartilhamento(Processo processo) {
        Boolean resp = compartilhamento.get(processo);
        if (resp == null) {
            resp = documentoCompartilhamentoSearch.possuiCompartilhamento(documento, processo);
            compartilhamento.put(processo.getIdProcesso(), resp);
        }
        return resp;
    }

    public void adicionarCompartilhamento(Processo processo) {
        try {
            documentoCompartilhamentoService.adicionarCompartilhamento(documento, processo, Authenticator.getUsuarioLogado());
            compartilhamento.put(processo.getIdProcesso(), true);
        } catch (Exception e) {
            LOG.error("documentoCompartilhamentoView.adicionarCompartilhamento(processo)", e);
            FacesMessages.instance().add("Falha ao tentar configurar compartilhamento. Favor tentar novamente.");
        }
    }

    public void removerCompartilhamento(Processo processo) {
        try {
            documentoCompartilhamentoService.removerCompartilhamento(documento, processo, Authenticator.getUsuarioLogado());
            compartilhamento.put(processo.getIdProcesso(), false);
        } catch (Exception e) {
            LOG.error("documentoCompartilhamentoView.removerCompartilhamento(processo)", e);
            FacesMessages.instance().add("Falha ao tentar configurar compartilhamento. Favor tentar novamente.");
        }
    }

    public Documento getDocumento() {
        return documento;
    }

    public List<Processo> getProcessosRelacionados() {
        return processosRelacionados;
    }
}
