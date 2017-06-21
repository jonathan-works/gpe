package br.com.infox.epp.turno.manager;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.joda.time.DateTime;
import org.joda.time.Minutes;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.turno.dao.LocalizacaoTurnoDAO;
import br.com.infox.epp.turno.entity.LocalizacaoTurno;

@Name(LocalizacaoTurnoManager.NAME)
@AutoCreate
@Stateless
public class LocalizacaoTurnoManager extends Manager<LocalizacaoTurnoDAO, LocalizacaoTurno> {

    private static final int MINUTES_OF_HOUR = 60;

    private static final long serialVersionUID = -7441171561119813498L;

    public static final String NAME = "localizacaoTurnoManager";

    /**
     * Lista todos os turnos da localização
     * 
     * @param localizacao
     * @return lista de turnos
     */
    public List<LocalizacaoTurno> listByLocalizacao(Localizacao localizacao) {
        return getDao().listByLocalizacao(localizacao);
    }

    /**
     * Calcula o tempo útil total dos turnos, em minutos
     * 
     * @param localizacaoTurnoList
     * @return
     */
    public int contarTempoUtilTurnos(List<LocalizacaoTurno> localizacaoTurnoList) {
        int minutos = 0;
        for (LocalizacaoTurno lt : localizacaoTurnoList) {
            minutos += minutesBetween(lt.getHoraInicio(), lt.getHoraFim());
        }
        return minutos;
    }

    private int minutesBetween(Date inicio, Date fim) {
        DateTime dtInicio = new DateTime(inicio);
        DateTime dtFim = new DateTime(fim);
        return Minutes.minutesBetween(dtInicio, dtFim).getMinutes();
    }

    /**
     * Verifica as possibilidades para os intervalos do turno de uma determinada
     * localização, para então calcular a diferença de horas que deve ser
     * acrescentada ao tempo gasto de uma tarefa.
     * 
     * @param fireTime - Hora de disparo da trigger
     * @param pt - ProcessoEpaTarefa a ser verificado o tempo gasto
     * @param lt - LocalizacaoTurno da tarefa em verificação.
     * @return minutos gastos dentro do turno informado
     */
    public int calcularMinutosGastos(Date fireTime, Date lastFire,
            LocalizacaoTurno lt) {
        int minutesBegin = Math.max(getMinutesOfDay(lastFire), getMinutesOfDay(lt.getHoraInicio()));
        int minutesEnd = Math.min(getMinutesOfDay(fireTime), getMinutesOfDay(lt.getHoraFim()));
        if (minutesBegin < minutesEnd) {
            return minutesEnd - minutesBegin;
        }
        return 0;
    }

    private int getMinutesOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE)
                + (calendar.get(Calendar.HOUR_OF_DAY) * MINUTES_OF_HOUR);
    }

    public void removerTurnosAnteriores(Localizacao localizacao) throws DAOException {
        getDao().removerTurnosAnteriores(localizacao);
    }
}
