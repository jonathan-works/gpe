package br.com.infox.epp.cliente.dao;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.cliente.entity.CalendarioEventos;

@Name(CalendarioEventosDAO.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class CalendarioEventosDAO extends DAO<CalendarioEventos> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "calendarioEventosDAO";
}
