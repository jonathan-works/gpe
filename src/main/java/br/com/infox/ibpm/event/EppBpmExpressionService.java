package br.com.infox.ibpm.event;

import static br.com.infox.epp.processo.service.VariaveisJbpmProcessosGerais.PROCESSO;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Named;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.comunicacao.prazo.ContabilizadorPrazo;
import br.com.infox.epp.processo.documento.manager.PastaManager;

import com.google.inject.Inject;

@Stateless
@Named(BpmExpressionService.NAME)
public class EppBpmExpressionService implements Serializable, BpmExpressionService {

    private static final long serialVersionUID = 1L;

    @Inject private ContabilizadorPrazo contabilizadorPrazo;
    @Inject private PastaManager pastaManager;

    @External(tooltip = "process.events.expression.atribuirCiencia.tooltip")
    public void atribuirCiencia() {
        contabilizadorPrazo.atribuirCiencia();
    }

    @External(tooltip = "process.events.expression.atribuirCumprimento.tooltip")
    public void atribuirCumprimento() {
        contabilizadorPrazo.atribuirCumprimento();
    }

    @External(value = {
            @Parameter(defaultValue = "'Nome da pasta'", label = "process.events.expression.param.nomePasta.label", tooltip = "process.events.expression.param.nomePasta.tooltip", selectable = true),
            @Parameter(defaultValue = PROCESSO, label = "process.events.expression.param.processo.label", tooltip = "process.events.expression.param.processo.tooltip") }, tooltip = "process.events.expression.disponibilizarPastaParaParticipantesProcesso.tooltip")
    public void disponibilizarPastaParaParticipantesProcesso(String descricaoPasta, Long idProcesso)
            throws DAOException {
        pastaManager.disponibilizarPastaParaParticipantesProcesso(descricaoPasta, idProcesso);
    }

    @External(value = {
            @Parameter(defaultValue = "'Nome da pasta'", label = "process.events.expression.param.nomePasta.label", tooltip = "process.events.expression.param.nomePasta.tooltip", selectable = true),
            @Parameter(defaultValue = PROCESSO, label = "process.events.expression.param.processo.label", tooltip = "process.events.expression.param.processo.tooltip") }, tooltip = "process.events.expression.tornarPastaPublica.tooltip")
    public void tornarPastaPublica(String nomePasta, Long processo) throws DAOException {
        pastaManager.tornarPastaPublica(nomePasta, processo);
    }

    @Override
    public List<ExternalMethod> getExternalMethods() {
        return BpmExpressionServiceConsumer.instance().getExternalMethods(this);
    }

}
