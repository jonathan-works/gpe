package br.com.infox.ibpm.event;

import static br.com.infox.epp.processo.service.VariaveisJbpmProcessosGerais.PROCESSO;

import java.io.Serializable;

import javax.ejb.Stateless;
import javax.inject.Named;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.comunicacao.prazo.ContabilizadorPrazo;
import br.com.infox.epp.processo.documento.manager.PastaManager;

import com.google.inject.Inject;

@Stateless
@Named(BpmExpressionService.NAME)
public class EppBpmExpressionService extends AbstractBpmExpressionService implements Serializable, BpmExpressionService {

    private static final long serialVersionUID = 1L;

    @Inject private ContabilizadorPrazo contabilizadorPrazo;
    @Inject private PastaManager pastaManager;

    @External(tooltip="???Verificar???")
    public void atribuirCiencia() {
        contabilizadorPrazo.atribuirCiencia();
    }

    @External(tooltip="???Verificar???")
    public void atribuirCumprimento() {
        contabilizadorPrazo.atribuirCumprimento();
    }

    @External(value={
            @Parameter(label = "Nome da pasta", tooltip = "Nome da pasta conforme definido em campo Descrição", selectable = true),
            @Parameter(defaultValue = PROCESSO) }, tooltip = "Expressão utilizada para tornar pasta disponível para leitura para participantes do processo")
    public void disponibilizarPastaParaParticipantesProcesso(String descricaoPasta, Long idProcesso)
            throws DAOException {
        pastaManager.disponibilizarPastaParaParticipantesProcesso(descricaoPasta, idProcesso);
    }
    
    @External(value={
        @Parameter(label = "Nome da pasta", tooltip = "Nome da pasta conforme definido em campo Descrição", selectable = true),
        @Parameter(defaultValue = PROCESSO) }, tooltip = "Expressão utilizada para tornar pasta disponível para leitura para não participantes e consulta pública")
    public void tornarPastaPublica(String nomePasta, Long processo) throws DAOException{
        pastaManager.tornarPastaPublica(nomePasta, processo);
    }

}
