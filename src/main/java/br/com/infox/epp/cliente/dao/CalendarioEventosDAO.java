package br.com.infox.epp.cliente.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.cliente.entity.CalendarioEventos;
import br.com.infox.util.time.DateRange;
import static br.com.infox.epp.cliente.query.CalendarioEventosQuery.*;

@Name(CalendarioEventosDAO.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class CalendarioEventosDAO extends DAO<CalendarioEventos> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "calendarioEventosDAO";
    
    public CalendarioEventos getByDate(Date date) {
        Map<String, Object> parameters = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        parameters.put(PARAM_DIA, calendar.get(Calendar.DAY_OF_MONTH));
        parameters.put(PARAM_MES, calendar.get(Calendar.MONTH) + 1);
        parameters.put(PARAM_ANO, calendar.get(Calendar.YEAR));
        return getNamedSingleResult(GET_BY_DATA, parameters);
    }
    
    public CalendarioEventos getByDate(DateRange date) {
    	return null;
    }
    
}
