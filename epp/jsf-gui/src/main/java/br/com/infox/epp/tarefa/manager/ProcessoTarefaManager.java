package br.com.infox.epp.tarefa.manager;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.util.DateUtil;
import br.com.infox.epp.estatistica.type.SituacaoPrazoEnum;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.processo.dao.ProcessoDAO;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.tarefa.dao.ProcessoTarefaDAO;
import br.com.infox.epp.tarefa.entity.ProcessoTarefa;
import br.com.infox.epp.tarefa.type.PrazoEnum;
import br.com.infox.epp.turno.dao.LocalizacaoTurnoDAO;
import br.com.infox.epp.turno.entity.LocalizacaoTurno;
import br.com.infox.epp.turno.type.DiaSemanaEnum;
import br.com.infox.util.time.DateRange;

@AutoCreate
@Stateless
@Name(ProcessoTarefaManager.NAME)
public class ProcessoTarefaManager extends Manager<ProcessoTarefaDAO, ProcessoTarefa> {

    private static final int PORCENTAGEM_MAXIMA = 100;

    private static final long serialVersionUID = 7702766272346991620L;
    public static final String NAME = "processoTarefaManager";

    @Inject
    private LocalizacaoTurnoDAO localizacaoTurnoDAO;
    @Inject
    private ProcessoDAO processoDAO;

    public ProcessoTarefa getByTaskInstance(final Long taskInstance) {
        return getDao().getByTaskInstance(taskInstance);
    }

    public List<ProcessoTarefa> getTarefaEnded() {
        return getDao().getTarefaEnded();
    }

    public List<ProcessoTarefa> getTarefaNotEnded(final PrazoEnum tipoPrazo) {
        return getDao().getTarefaNotEnded(tipoPrazo);
    }

    public List<Object[]> listForaPrazoFluxo(final Categoria c) {
        return getDao().listForaPrazoFluxo(c);
    }

    public List<Object[]> listForaPrazoTarefa(final Categoria c) {
        return getDao().listForaPrazoTarefa(c);
    }

    public List<Object[]> listTarefaPertoLimite() {
        return getDao().listTarefaPertoLimite();
    }

    public Map<String, Object> findProcessoTarefaByIdProcessoAndIdTarefa(
            final Integer idProcesso, final Integer idTarefa) {
        return getDao().findProcessoTarefaByIdProcessoAndIdTarefa(idProcesso, idTarefa);
    }

    /**
     * Verifica se existe algum turno da localizacao da tarefa em que no dia
     * informado
     *
     * @param pt
     * @param horario
     * @return turno da localização da tarefa
     */
    public boolean contemTurnoTarefaDia(final ProcessoTarefa pt, final Date data) {
        final Calendar horarioCalendar = Calendar.getInstance();
        final int diaSemana = horarioCalendar.get(Calendar.DAY_OF_WEEK);
        return this.localizacaoTurnoDAO.countTurnoTarefaDia(pt, data, DiaSemanaEnum.values()[diaSemana - 1]) > 0;
    }

    public void updateTempoGasto(Date fireTime, ProcessoTarefa processoTarefa) throws DAOException {
        if (processoTarefa.getTarefa().getTipoPrazo() != null && processoTarefa.getUltimoDisparo().before(fireTime)) {
            float incrementoTempoGasto = getIncrementoTempoGasto(fireTime, processoTarefa);
            Integer prazo = processoTarefa.getTarefa().getPrazo();
            int porcentagem = 0;
            int tempoGasto = (int) (processoTarefa.getTempoGasto() + incrementoTempoGasto);
            if (prazo != null && prazo.compareTo(0) > 0) {
                porcentagem = (tempoGasto * PORCENTAGEM_MAXIMA) / (prazo * 60);
            }

            Processo processo = processoTarefa.getProcesso();
            if (porcentagem > PORCENTAGEM_MAXIMA && !SituacaoPrazoEnum.PAT.equals(processo.getSituacaoPrazo())) {
                processo.setSituacaoPrazo(SituacaoPrazoEnum.TAT);
            }

            processoTarefa.setTempoGasto(tempoGasto);
            processoTarefa.setUltimoDisparo(fireTime);
            update(processoTarefa);
            updateTempoGasto(processo);
        }
    }

    public void updateTempoGasto(final Processo processo) throws DAOException {
        Map<String, Object> result = this.processoDAO.getTempoGasto(processo);
        if (result != null) {
            Date dataFim = processo.getDataFim();
            DateRange dateRange;
            if (dataFim != null) {
                dateRange = new DateRange(processo.getDataInicio(), dataFim);
            } else {
                dateRange = new DateRange(processo.getDataInicio(), new Date());
            }
            processo.setTempoGasto(new Long(dateRange.get(DateRange.DAYS)).intValue());
            if (processo.getPorcentagem() > PORCENTAGEM_MAXIMA) {
                processo.setSituacaoPrazo(SituacaoPrazoEnum.PAT);
            }
            this.processoDAO.update(processo);
        }
    }

    public List<ProcessoTarefa> getByProcesso(Processo processo) {
        return getDao().getByProcesso(processo);
    }


    /**
     * Calcula o tempo a incrementar no {@link ProcessoTarefa} de acordo com
     * a data em que ocorreu o disparo.
     *
     * @param horaDisparo
     * @param processoTarefa
     * @return Incremento a ser adicionado ao tempo gasto de um
     *         {@link ProcessoTarefa}
     */
    private float getIncrementoTempoGasto(final Date horaDisparo,
            final ProcessoTarefa processoTarefa) {
        final PrazoEnum tipoPrazo = processoTarefa.getTarefa().getTipoPrazo();
        float result = 0;
        if (tipoPrazo == null) {
            return 0;
        }
        switch (tipoPrazo) {
        case H:
            result = calcularTempoGastoMinutos(horaDisparo, processoTarefa.getTaskInstance(), processoTarefa.getUltimoDisparo());
            break;
        case D:
            result = calcularTempoGastoDias(horaDisparo, processoTarefa);
            break;
        }
        return result;
    }

    private Date getDisparoIncrementado(final Date ultimoDisparo, final Date disparoAtual,
            final int tipoIncremento, final int incremento) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(ultimoDisparo);
        calendar.add(tipoIncremento, incremento);
        final Date proxDisparo = calendar.getTime();

        if (proxDisparo.before(disparoAtual)) {
            return proxDisparo;
        } else {
            return disparoAtual;
        }
    }

    private void adjustCalendar(final Calendar toGet, final Calendar toSet1,
            final Calendar toSet2, final int field) {
        final int value = toGet.get(field);
        toSet1.set(field, value);
        toSet2.set(field, value);
    }

    private float calcularTempoGastoMinutos(final Date dataDisparo,
            final long idTaskInstance, final Date ultimoDisparo) {
        float result = 0;

        final Calendar ultimaAtualizacao = new GregorianCalendar();
        final Calendar disparoAtual = new GregorianCalendar();
        disparoAtual.setTime(dataDisparo);
        ultimaAtualizacao.setTime(ultimoDisparo);
        while (ultimaAtualizacao.before(disparoAtual)) {

            final List<LocalizacaoTurno> localizacoes = this.localizacaoTurnoDAO.getTurnosTarefa(idTaskInstance, DiaSemanaEnum.values()[ultimaAtualizacao.get(Calendar.DAY_OF_WEEK) - 1], ultimaAtualizacao.getTime());
            for (int i = 0, l = localizacoes.size(); i < l; i++) {
                final LocalizacaoTurno localizacaoTurno = localizacoes.get(i);
                final Calendar inicioTurno = new GregorianCalendar();
                inicioTurno.setTime(localizacaoTurno.getHoraInicio());

                final Calendar fimTurno = new GregorianCalendar();
                fimTurno.setTime(localizacaoTurno.getHoraFim());

                adjustCalendar(ultimaAtualizacao, inicioTurno, fimTurno, Calendar.DAY_OF_MONTH);
                adjustCalendar(ultimaAtualizacao, inicioTurno, fimTurno, Calendar.MONTH);
                adjustCalendar(ultimaAtualizacao, inicioTurno, fimTurno, Calendar.YEAR);
                final DateRange range = getIncrementoLocalizacaoTurno(disparoAtual, ultimaAtualizacao, inicioTurno, fimTurno);
                result = result + range.get(DateRange.MINUTES);

                ultimaAtualizacao.setTime(range.getEnd().toDate());

                if (!ultimaAtualizacao.before(disparoAtual)) {
                    break;
                }
            }
            if (ultimaAtualizacao.before(disparoAtual)) {
                ultimaAtualizacao.set(Calendar.HOUR_OF_DAY, 0);
                ultimaAtualizacao.set(Calendar.MINUTE, 0);
                ultimaAtualizacao.set(Calendar.SECOND, 0);
                ultimaAtualizacao.set(Calendar.MILLISECOND, 0);
                ultimaAtualizacao.add(Calendar.DAY_OF_MONTH, 1);
            }
        }

        return result;
    }

    private DateRange getIncrementoLocalizacaoTurno(final Calendar dataDisparo,
            final Calendar ultimaAtualizacao, final Calendar inicioTurno,
            final Calendar fimTurno) {
        if (dataDisparo.before(inicioTurno) || ultimaAtualizacao.after(fimTurno)) {
            return new DateRange(ultimaAtualizacao.getTime(), ultimaAtualizacao.getTime());
        }
        final Date beginning = inicioTurno.after(ultimaAtualizacao) ? inicioTurno.getTime() : ultimaAtualizacao.getTime();
        final Date end = fimTurno.before(dataDisparo) ? fimTurno.getTime() : dataDisparo.getTime();
        final DateRange dateRange = new DateRange(beginning, end);
        return dateRange;
    }

    private int calcularTempoGastoDias(final Date dataDisparo,
            final ProcessoTarefa processoTarefa) {
        int result = 0;
        Date ultimaAtualizacao = processoTarefa.getUltimoDisparo();

        while (ultimaAtualizacao.before(dataDisparo)) {
            final Date disparoAtual = getDisparoIncrementado(ultimaAtualizacao, dataDisparo, Calendar.DAY_OF_MONTH, 1);
            if (contemTurnoTarefaDia(processoTarefa, disparoAtual)) {
                result += DateUtil.diferencaDias(disparoAtual, ultimaAtualizacao);
            }
            ultimaAtualizacao = disparoAtual;
        }

        return result;
    }

    public int getDiasDesdeInicioProcesso(final Processo processo) {
        final LocalDate dataInicio = LocalDate.fromDateFields(getDao().getDataInicioPrimeiraTarefa(processo));
        final LocalDate now = LocalDate.now();
        return Days.daysBetween(dataInicio, now).getDays();
    }

    public void finalizarInstanciaTarefa(final TaskInstance taskInstance) throws DAOException {
        taskInstance.end();
        final ProcessoTarefa pt = getByTaskInstance(taskInstance.getId());
        final Date dtFinalizacao = taskInstance.getEnd();
        pt.setDataFim(dtFinalizacao);
        update(pt);
        updateTempoGasto(dtFinalizacao, pt);
    }

    @Transactional
    public void finalizarInstanciaTarefa(final TaskInstance taskInstance, final String transicao) throws DAOException {
        if (!taskInstance.hasEnded()) {
            taskInstance.end(transicao);
        }
        final ProcessoTarefa pt = getByTaskInstance(taskInstance.getId());
        final Date dtFinalizacao = taskInstance.getEnd();
        pt.setDataFim(dtFinalizacao);
        update(pt);
        updateTempoGasto(dtFinalizacao, pt);
    }

    public ProcessoTarefa getProcessoTarefaAberto(Processo processo, Integer idTarefa) {
        return getDao().getProcessoTarefaAberto(processo, idTarefa);
    }

    public ProcessoTarefa getUltimoProcessoTarefa(Processo processo) {
        return getDao().getUltimoProcessoTarefa(processo);
    }
}
