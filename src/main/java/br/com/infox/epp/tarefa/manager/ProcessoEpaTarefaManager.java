package br.com.infox.epp.tarefa.manager;

import static java.text.MessageFormat.format;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.util.DateUtil;
import br.com.infox.epp.estatistica.type.SituacaoPrazoEnum;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.processo.dao.ProcessoEpaDAO;
import br.com.infox.epp.processo.entity.ProcessoEpa;
import br.com.infox.epp.tarefa.dao.ProcessoEpaTarefaDAO;
import br.com.infox.epp.tarefa.entity.ProcessoEpaTarefa;
import br.com.infox.epp.tarefa.type.PrazoEnum;
import br.com.infox.epp.turno.dao.LocalizacaoTurnoDAO;
import br.com.infox.epp.turno.entity.LocalizacaoTurno;
import br.com.infox.epp.turno.type.DiaSemanaEnum;
import br.com.infox.util.time.DateRange;

@Name(ProcessoEpaTarefaManager.NAME)
@AutoCreate
public class ProcessoEpaTarefaManager extends Manager<ProcessoEpaTarefaDAO, ProcessoEpaTarefa> {

    private static final int PORCENTAGEM_MAXIMA = 100;
    private static final int HOURS_OF_DAY = 24;

    private static final long serialVersionUID = 7702766272346991620L;

    public static final String NAME = "processoEpaTarefaManager";

    @In
    private LocalizacaoTurnoDAO localizacaoTurnoDAO;
    @In
    private ProcessoEpaDAO processoEpaDAO;

    public ProcessoEpaTarefa getByTaskInstance(Long taskInstance) {
        return getDao().getByTaskInstance(taskInstance);
    }

    public List<ProcessoEpaTarefa> getTarefaEnded() {
        return getDao().getTarefaEnded();
    }

    public List<ProcessoEpaTarefa> getTarefaNotEnded(PrazoEnum tipoPrazo) {
        return getDao().getTarefaNotEnded(tipoPrazo);
    }

    public List<Object[]> listForaPrazoFluxo(Categoria c) {
        return getDao().listForaPrazoFluxo(c);
    }

    public List<Object[]> listForaPrazoTarefa(Categoria c) {
        return getDao().listForaPrazoTarefa(c);
    }

    public List<Object[]> listTarefaPertoLimite() {
        return getDao().listTarefaPertoLimite();
    }

    public Map<String, Object> findProcessoEpaTarefaByIdProcessoAndIdTarefa(
            final Integer idProcesso, final Integer idTarefa) {
        return getDao().findProcessoEpaTarefaByIdProcessoAndIdTarefa(idProcesso, idTarefa);
    }

    /**
     * Verifica se existe algum turno da localizacao da tarefa em que no dia
     * informado
     * 
     * @param pt
     * @param horario
     * @return turno da localização da tarefa
     */
    public boolean contemTurnoTarefaDia(ProcessoEpaTarefa pt, Date data) {
        Calendar horarioCalendar = Calendar.getInstance();
        int diaSemana = horarioCalendar.get(Calendar.DAY_OF_WEEK);
        return localizacaoTurnoDAO.countTurnoTarefaDia(pt, data, DiaSemanaEnum.values()[diaSemana - 1]) > 0;
    }

    /**
     * Atualiza os atributos referentes ao tempo gasto em uma tarefa caso exista
     * incremento.
     * 
     * @param fireTime
     * @param tipoPrazo
     * @throws DAOException
     */
    public void updateTarefasNaoFinalizadas(Date fireTime, PrazoEnum tipoPrazo) throws DAOException {
        for (ProcessoEpaTarefa pt : getTarefaNotEnded(tipoPrazo)) {
            updateTempoGasto(fireTime, pt);
        }
    }

    public void updateTempoGasto(Date fireTime,
            ProcessoEpaTarefa processoEpaTarefa) throws DAOException {
        if (processoEpaTarefa.getTarefa().getTipoPrazo() == null) {
            return;
        }
        if (processoEpaTarefa.getUltimoDisparo().before(fireTime)) {
            float incrementoTempoGasto = getIncrementoTempoGasto(fireTime, processoEpaTarefa);
            Integer prazo = processoEpaTarefa.getTarefa().getPrazo();
            int porcentagem = 0;
            int tempoGasto = (int) (processoEpaTarefa.getTempoGasto() + incrementoTempoGasto);
            if (prazo != null && prazo.compareTo(Integer.valueOf(0)) > 0) {
                porcentagem = (tempoGasto * PORCENTAGEM_MAXIMA) / (prazo * 60);
            }

            ProcessoEpa processoEpa = processoEpaTarefa.getProcessoEpa();
            if (porcentagem > PORCENTAGEM_MAXIMA) {
                processoEpa.setSituacaoPrazo(SituacaoPrazoEnum.TAT);
            }

            processoEpaTarefa.setPorcentagem(porcentagem);
            processoEpaTarefa.setTempoGasto(tempoGasto);
            processoEpaTarefa.setUltimoDisparo(fireTime);
            update(processoEpaTarefa);
            updateTempoGasto(processoEpa);
        }
    }

    public void updateTempoGasto(ProcessoEpa processoEpa) throws DAOException {
        Map<String, Object> result = processoEpaDAO.getTempoGasto(processoEpa);

        if (result != null) {
            
            Date dataFim = processoEpa.getDataFim();
            DateRange dateRange;
            if (dataFim != null) {
                 dateRange = new DateRange(processoEpa.getDataInicio(), dataFim);
            } else {
                dateRange = new DateRange(processoEpa.getDataInicio(), new Date());
            }
            processoEpa.setTempoGasto(new Long(dateRange.get(DateRange.DAYS)).intValue());
            Fluxo f = processoEpa.getNaturezaCategoriaFluxo().getFluxo();

            if (f.getQtPrazo() != null && f.getQtPrazo() != 0) {
                processoEpa.setPorcentagem((processoEpa.getTempoGasto() * PORCENTAGEM_MAXIMA)
                        / f.getQtPrazo());
            }
            if (processoEpa.getPorcentagem() > PORCENTAGEM_MAXIMA) {
                processoEpa.setSituacaoPrazo(SituacaoPrazoEnum.PAT);
            }
            processoEpaDAO.update(processoEpa);
        }
    }

    /**
     * Calcula o tempo a incrementar no {@link ProcessoEpaTarefa} de acordo com
     * a data em que ocorreu o disparo.
     * 
     * @param horaDisparo
     * @param processoEpaTarefa
     * @return Incremento a ser adicionado ao tempo gasto de um
     *         {@link ProcessoEpaTarefa}
     */
    private float getIncrementoTempoGasto(Date horaDisparo,
            ProcessoEpaTarefa processoEpaTarefa) {
        PrazoEnum tipoPrazo = processoEpaTarefa.getTarefa().getTipoPrazo();
        float result = 0;
        if (tipoPrazo == null) {
            return 0;
        }
        switch (tipoPrazo) {
            case H:
                result = calcularTempoGastoMinutos(horaDisparo, processoEpaTarefa.getTaskInstance(), processoEpaTarefa.getUltimoDisparo());
            break;
            case D:
                result = calcularTempoGastoDias(horaDisparo, processoEpaTarefa);
            break;
        }
        return result;
    }

    private Date getDisparoIncrementado(Date ultimoDisparo, Date disparoAtual,
            int tipoIncremento, int incremento) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ultimoDisparo);
        calendar.add(tipoIncremento, incremento);
        Date proxDisparo = calendar.getTime();

        if (proxDisparo.before(disparoAtual)) {
            return proxDisparo;
        } else {
            return disparoAtual;
        }
    }

    private void adjustCalendar(Calendar toGet, Calendar toSet1,
            Calendar toSet2, int field) {
        int value = toGet.get(field);
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

            final List<LocalizacaoTurno> localizacoes = localizacaoTurnoDAO.getTurnosTarefa(idTaskInstance, DiaSemanaEnum.values()[ultimaAtualizacao.get(Calendar.DAY_OF_WEEK) - 1], ultimaAtualizacao.getTime());
            for (int i = 0, l = localizacoes.size(); i < l; i++) {
                LocalizacaoTurno localizacaoTurno = localizacoes.get(i);
                final Calendar inicioTurno = new GregorianCalendar();
                inicioTurno.setTime(localizacaoTurno.getHoraInicio());

                final Calendar fimTurno = new GregorianCalendar();
                fimTurno.setTime(localizacaoTurno.getHoraFim());

                adjustCalendar(ultimaAtualizacao, inicioTurno, fimTurno, Calendar.DAY_OF_MONTH);
                adjustCalendar(ultimaAtualizacao, inicioTurno, fimTurno, Calendar.MONTH);
                adjustCalendar(ultimaAtualizacao, inicioTurno, fimTurno, Calendar.YEAR);
                DateRange range = getIncrementoLocalizacaoTurno(disparoAtual, ultimaAtualizacao, inicioTurno, fimTurno);
                result = result + range.get(DateRange.MINUTES);

                ultimaAtualizacao.setTime(range.getEnd());

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

    private int calcularTempoGastoDias(Date dataDisparo,
            ProcessoEpaTarefa processoEpaTarefa) {
        int result = 0;
        Date ultimaAtualizacao = processoEpaTarefa.getUltimoDisparo();

        while (ultimaAtualizacao.before(dataDisparo)) {
            Date disparoAtual = getDisparoIncrementado(ultimaAtualizacao, dataDisparo, Calendar.DAY_OF_MONTH, 1);
            if (contemTurnoTarefaDia(processoEpaTarefa, disparoAtual)) {
                result += DateUtil.diferencaDias(disparoAtual, ultimaAtualizacao);
            }
            ultimaAtualizacao = disparoAtual;
        }

        return result;
    }
}
