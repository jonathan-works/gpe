package br.com.infox.epp.cliente.dao;

import static br.com.infox.epp.cliente.query.CalendarioEventosQuery.GET_BY_DATA;
import static br.com.infox.epp.cliente.query.CalendarioEventosQuery.GET_BY_DATA_RANGE;
import static br.com.infox.epp.cliente.query.CalendarioEventosQuery.Param.DATA;
import static br.com.infox.epp.cliente.query.CalendarioEventosQuery.Param.PARAM_END_DATE;
import static br.com.infox.epp.cliente.query.CalendarioEventosQuery.Param.PARAM_START_DATE;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.cliente.entity.CalendarioEventos;
import br.com.infox.hibernate.util.HibernateUtil;
import br.com.infox.util.time.DateRange;

@Stateless
@AutoCreate
@Name(CalendarioEventosDAO.NAME)
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
    
    public Date dataUtilAdd(String type, Date baseDate, int amount) {
    	List<Object> arguments = new ArrayList<>();
    	arguments.add(type);
    	arguments.add(amount);
    	arguments.add(baseDate);
		Query query = HibernateUtil.createCallFunctionQuery("DataUtilAdd", arguments, getEntityManager());
    	return (Date) query.getSingleResult();    			
    }


}
