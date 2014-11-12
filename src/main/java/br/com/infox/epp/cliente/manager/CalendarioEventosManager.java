package br.com.infox.epp.cliente.manager;

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
}
