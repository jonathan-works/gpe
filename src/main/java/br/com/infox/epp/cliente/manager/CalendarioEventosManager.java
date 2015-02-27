package br.com.infox.epp.cliente.manager;

import java.util.Calendar;
import java.util.Date;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.util.DateUtil;
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
    	Calendar prazo = Calendar.getInstance();
    	prazo.setTime(DateUtil.getEndOfDay(dia));
    	prazo.add(Calendar.DAY_OF_MONTH, qtdDias);
    	Date dataPrazo = prazo.getTime();
        if (isDiaUtil(dataPrazo))
            return dataPrazo;
        else 
            return getPrimeiroDiaUtil(dataPrazo, 1);
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
