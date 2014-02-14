package br.com.infox.epp.processo.manager;

import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.estatistica.type.SituacaoPrazoEnum;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.Item;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;
import br.com.infox.epp.processo.dao.ProcessoEpaDAO;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.ProcessoEpa;

@Name(ProcessoEpaManager.NAME)
@AutoCreate
public class ProcessoEpaManager extends Manager<ProcessoEpaDAO, ProcessoEpa> {

    private static final int PORCENTAGEM = 100;

    private static final int HOURS_OF_DAY = 24;

    private static final long serialVersionUID = 168832523707680478L;

    public static final String NAME = "processoEpaManager";

    public List<ProcessoEpa> listAllNotEnded() {
        return getDao().listAllNotEnded();
    }

    public List<ProcessoEpa> listNotEnded(Fluxo fluxo) {
        return getDao().listNotEnded(fluxo);
    }

    public Boolean podeInativarPartesDoProcesso(Processo processo) {
        return getDao().podeInativarPartes(getDao().getProcessoEpaByProcesso(processo));
    }

    public boolean podeInativarPartesDoProcesso(ProcessoEpa processoEpa) {
        return getDao().podeInativarPartes(processoEpa);
    }

    public void updateTempoGastoProcessoEpa() throws DAOException {
        List<ProcessoEpa> listAllNotEnded = listAllNotEnded();
        for (ProcessoEpa processoEpa : listAllNotEnded) {
            Map<String, Object> result = getDao().getTempoGasto(processoEpa);

            if (result != null) {
                Fluxo f = processoEpa.getNaturezaCategoriaFluxo().getFluxo();
                Long dias = (Long) result.get("dias");
                Long tempoGasto = ((Long) result.get("horas")) / HOURS_OF_DAY;
                if (dias != null) {
                    tempoGasto += dias;
                }
                processoEpa.setTempoGasto(tempoGasto.intValue());

                if (f.getQtPrazo() != null && f.getQtPrazo() != 0) {
                    processoEpa.setPorcentagem((processoEpa.getTempoGasto() * PORCENTAGEM)
                            / f.getQtPrazo());
                }
                if (processoEpa.getPorcentagem() > PORCENTAGEM) {
                    processoEpa.setSituacaoPrazo(SituacaoPrazoEnum.PAT);
                }
                getDao().update(processoEpa);
            }
        }
    }

    public Item getItemDoProcesso(int idProcesso) {
        return getDao().getItemDoProcesso(idProcesso);
    }

    public boolean hasPartes(Processo processo) {
        return getDao().hasPartes(processo);
    }

    public boolean hasPartes(Long idJbpm) {
        return getDao().hasPartes(idJbpm);
    }

    public List<PessoaFisica> getPessoaFisicaList() {
        return getDao().getPessoaFisicaList();
    }

    public List<PessoaJuridica> getPessoaJuridicaList() {
        return getDao().getPessoaJuridicaList();
    }

    public int getDiasDesdeInicioProcesso(ProcessoEpa processoEpa) {
        LocalDate dataInicio = LocalDate.fromDateFields(getDao().getDataInicioPrimeiraTarefa(processoEpa));
        LocalDate now = LocalDate.now();
        return Days.daysBetween(dataInicio, now).getDays();
    }

    public Double getMediaTempoGasto(Fluxo fluxo, SituacaoPrazoEnum prazoEnum) {
        return getDao().getMediaTempoGasto(fluxo, prazoEnum);
    }
}
