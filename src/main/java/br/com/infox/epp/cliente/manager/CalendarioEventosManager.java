package br.com.infox.epp.cliente.manager;

import java.util.Calendar;
import java.util.Date;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.cliente.dao.CalendarioEventosDAO;
import br.com.infox.epp.cliente.entity.CalendarioEventos;

@Name(CalendarioEventosManager.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class CalendarioEventosManager extends Manager<CalendarioEventosDAO, CalendarioEventos> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "calendarioEventosManager";
    
    public CalendarioEventos getByDate(Date date) {
        return getDao().getByDate(date);
    }
    
    /**
     * Retorna o primeiro dia útil de forma recursiva
     * @param dia Data base a considerar
     * @param qtdDias quantidade de dias 
     * @return
     */
    public Date getPrimeiroDiaUtil(Date dia, int qtdDias) {
        Date prazo = new Date();
        prazo.setTime(dia.getTime() + getDiaInMilis(qtdDias));
        if (isDiaUtil(prazo))
            return prazo;
        else 
            return getPrimeiroDiaUtil(prazo, 1);
    }
    
    /**
     * Transforma uma quantidade de dias em milisegundos.
     * @param dias
     * @return dias em milisegundos
     */
    public Integer getDiaInMilis(Integer dias) {
        if (dias == null) dias = 10;
        return dias * 24 * 60 * 60 * 1000;
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
        return getByDate(dia) != null;
    }
}
