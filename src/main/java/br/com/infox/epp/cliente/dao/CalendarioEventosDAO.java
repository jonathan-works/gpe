package br.com.infox.epp.cliente.dao;

import static br.com.infox.epp.cliente.query.CalendarioEventosQuery.GET_BY_DATA;
import static br.com.infox.epp.cliente.query.CalendarioEventosQuery.GET_BY_DATA_RANGE;
import static br.com.infox.epp.cliente.query.CalendarioEventosQuery.Param.DATA;
import static br.com.infox.epp.cliente.query.CalendarioEventosQuery.Param.PARAM_END_DATE;
import static br.com.infox.epp.cliente.query.CalendarioEventosQuery.Param.PARAM_START_DATE;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.cliente.entity.CalendarioEventos;
import br.com.infox.util.time.DateRange;

@Name(CalendarioEventosDAO.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class CalendarioEventosDAO extends DAO<CalendarioEventos> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "calendarioEventosDAO";

    public List<CalendarioEventos> getByDate(Date date) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(DATA, date);
        return getNamedResultList(GET_BY_DATA, parameters);
    }

    public List<CalendarioEventos> getByDate(DateRange date) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_START_DATE, date.getStart().toDate());
        parameters.put(PARAM_END_DATE, date.getEnd().toDate());
        return getNamedResultList(GET_BY_DATA_RANGE, parameters);
    }

}
