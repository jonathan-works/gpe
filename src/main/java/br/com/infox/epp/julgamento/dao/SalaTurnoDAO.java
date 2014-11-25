package br.com.infox.epp.julgamento.dao;

import static br.com.infox.epp.julgamento.query.SalaTurnoQuery.DELETE_TURNOS_ANTERIORES;
import static br.com.infox.epp.julgamento.query.SalaTurnoQuery.LIST_BY_SALA;
import static br.com.infox.epp.julgamento.query.SalaTurnoQuery.QUERY_PARAM_SALA;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.julgamento.entity.Sala;
import br.com.infox.epp.julgamento.entity.SalaTurno;

@AutoCreate
@Name(SalaTurnoDAO.NAME)
public class SalaTurnoDAO extends DAO<SalaTurno> {

    static final String NAME = "salaTurnoDAO";
    private static final long serialVersionUID = 1L;

    public void removerTurnosAnteriores(Sala sala) throws DAOException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(QUERY_PARAM_SALA, sala);
        executeNamedQueryUpdate(DELETE_TURNOS_ANTERIORES, parameters);
    }

    public List<SalaTurno> listBySala(Sala sala) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(QUERY_PARAM_SALA, sala);
        return getNamedResultList(LIST_BY_SALA, parameters);
    }
}
