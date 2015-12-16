package br.com.infox.epp.cliente.manager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.util.DateUtil;
import br.com.infox.epp.calendario.TipoEvento;
import br.com.infox.epp.cliente.dao.CalendarioEventosDAO;
import br.com.infox.epp.cliente.entity.CalendarioEventos;
import br.com.infox.util.time.DateRange;

@Scope(ScopeType.STATELESS)
@Stateless
@AutoCreate
@Name(CalendarioEventosManager.NAME)
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class CalendarioEventosManager extends Manager<CalendarioEventosDAO, CalendarioEventos> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "calendarioEventosManager";
    //TODO: Atualizar para retornar eventos virtuais. Datas que ainda não existem no banco de dados, mas que existem na progressão de uma série
    public List<CalendarioEventos> getByDate(Date date) {
        return getDao().getByDate(date);
    }
  //TODO: Atualizar para retornar eventos virtuais. Datas que ainda não existem no banco de dados, mas que existem na progressão de uma série
    public List<CalendarioEventos> getByDate(DateRange dateRange) {
        return getDao().getByDate(dateRange);
    }

    /**
     * Retorna o primeiro dia útil de forma recursiva
     * 
     * @param dia
     *            Data base a considerar
     * @param qtdDias
     *            quantidade de dias
     * @return
     */
    public Date getPrimeiroDiaUtil(Date dia, int qtdDias) {
        Calendar prazo = Calendar.getInstance();
        prazo.setTime(DateUtil.getEndOfDay(dia));
        prazo.add(Calendar.DAY_OF_MONTH, qtdDias);
        Date dataPrazo = prazo.getTime();
        if (isDiaUtil(dataPrazo))
            return dataPrazo;
        else
            return getPrimeiroDiaUtil(dataPrazo, 1);
    }

    public Date getPrimeiroDiaUtil(Date dia) {
        if (isDiaUtil(dia)) {
            return dia;
        } else {
            return getPrimeiroDiaUtil(dia, 1);
        }
    }

    public Boolean isDiaUtil(Date dia) {
        return !(isWeekend(dia) || hasEventAt(dia));
    }

    public Boolean isWeekend(Date dia) {
        Calendar c = Calendar.getInstance();
        c.setTime(dia);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY;
    }

    public boolean hasEventAt(Date dia) {
        return getByDate(dia) != null && !getByDate(dia).isEmpty();
    }

    @Override
    public CalendarioEventos remove(CalendarioEventos calendarioEventos) throws DAOException {
        return null;
    }

    @Override
    public CalendarioEventos persist(CalendarioEventos calendarioEventos) throws DAOException {
        return null;
    }

    @Override
    public CalendarioEventos update(CalendarioEventos calendarioEventos) throws DAOException {
        return null;
    }

	private List<DateRange> getSuspensoesPrazo(DateRange periodo){
	    List<DateRange> resultList = new ArrayList<>();
	    for (CalendarioEventos calendarioEventos : getByDate(periodo)) {
	        if (TipoEvento.S.equals(calendarioEventos.getTipoEvento())){
	            resultList.add(calendarioEventos.getInterval());
	        }
	    }
	    return resultList;
	}

	private Collection<DateRange> getFeriados(DateRange periodo) {
		Collection<DateRange> result = new ArrayList<>();
		for (CalendarioEventos calendarioEventos : getByDate(periodo)) {
			result.add(calendarioEventos.getInterval());
		}
		return DateRange.reduce(result);
	}

	public Date getDateMinusBusinessDays(Date date, int totalBusinessDays, Collection<DateRange> eventos){
		int businessDays = 0;
		br.com.infox.util.time.Date newDate = new br.com.infox.util.time.Date(date);
		while(businessDays++ < totalBusinessDays){
			newDate = newDate.minusDays(1).prevWeekday(eventos.toArray(new DateRange[eventos.size()]));
		}
		return newDate.toDate();
	}
	public Date getDateMinusBusinessDays(Date date, int totalBusinessDays){
		DateRange periodo = new DateRange(date, date);
		periodo = periodo.withStart(periodo.getStart().minusYears(1)).withEnd(periodo.getEnd().plusYears(1));
		return getDateMinusBusinessDays(date, totalBusinessDays, getFeriados(periodo));
	}
	
	public br.com.infox.util.time.Date getPreviousBusinessDay(Date date, Collection<DateRange> eventos){
		return new br.com.infox.util.time.Date(date).prevWeekday(eventos.toArray(new DateRange[eventos.size()]));
	}
	
	public br.com.infox.util.time.Date getPreviousBusinessDay(Date date){
		DateRange periodo = new DateRange(date, date);
		periodo = periodo.withStart(periodo.getStart().minusYears(1)).withEnd(periodo.getEnd().plusYears(1));
		return getPreviousBusinessDay(date, getFeriados(periodo));
	}
	
	public br.com.infox.util.time.Date getNextWeekday(Date date, Collection<DateRange> eventos){
		if (date == null){
			return null;
		}
		return new br.com.infox.util.time.Date(date).nextWeekday(eventos.toArray(new DateRange[eventos.size()]));
	}
	public br.com.infox.util.time.Date getNextWeekday(Date date){
		DateRange periodo = new DateRange(date, date);
		periodo = periodo.withStart(periodo.getStart().minusYears(1)).withEnd(periodo.getEnd().plusYears(1));
		return getNextWeekday(date, getFeriados(periodo));
	}
	
	public DateRange calcularPrazoIniciandoEmDiaUtil(DateRange periodo){
		DateRange periodoEventos = periodo.withStart(periodo.getStart().minusYears(1)).withEnd(periodo.getEnd().plusYears(1));
		return calcularPrazoIniciandoEmDiaUtil(periodo, getFeriados(periodoEventos));
	}

	public DateRange calcularPrazoIniciandoEmDiaUtil(DateRange periodo, Collection<DateRange> eventos) {
		DateRange[] periodosNaoUteis = eventos.toArray(new DateRange[eventos.size()]);
	    return periodo.withStart(periodo.getStart().nextWeekday(periodosNaoUteis).withTimeAtStartOfDay());
	}

	public DateRange calcularPrazoEncerrandoEmDiaUtil(DateRange periodo, Collection<DateRange> eventos){
		DateRange[] periodosNaoUteis = eventos.toArray(new DateRange[eventos.size()]);
	    return periodo.withEnd(periodo.getEnd().nextWeekday(periodosNaoUteis).withTimeAtEndOfDay());
	}

	public DateRange calcularPrazoEncerrandoEmDiaUtil(DateRange periodo){
		DateRange periodoEventos = periodo.withStart(periodo.getStart().minusYears(1)).withEnd(periodo.getEnd().plusYears(1));
	    return calcularPrazoEncerrandoEmDiaUtil(periodo, getFeriados(periodoEventos));
	}

	public DateRange calcularPrazoSuspensao(DateRange periodo){
		DateRange periodoEventos = periodo.withStart(periodo.getStart().minusYears(1)).withEnd(periodo.getEnd().plusYears(1));
		return calcularPrazoSuspensao(periodo, getSuspensoesPrazo(periodoEventos));
	}

	public DateRange calcularPrazoSuspensao(DateRange periodo, List<DateRange> suspensoesPrazo) {
		DateRange result = new DateRange(periodo);
		Set<DateRange> applied = new HashSet<>();
		boolean changed=false;
		do {
			changed = false;
			for (DateRange suspensao : DateRange.reduce(suspensoesPrazo)) {
				DateRange connection = result.connection(suspensao);
	    		if (connection != null && applied.add(suspensao)){
	    			result = result.incrementStartByDuration(connection);
	    			changed = true;
	    		}
			}
		} while(changed);
		return result;
	}

}
