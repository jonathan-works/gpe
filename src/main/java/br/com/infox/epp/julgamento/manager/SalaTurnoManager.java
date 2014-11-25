package br.com.infox.epp.julgamento.manager;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.julgamento.dao.SalaTurnoDAO;
import br.com.infox.epp.julgamento.entity.Sala;
import br.com.infox.epp.julgamento.entity.SalaTurno;


@AutoCreate
@Name(SalaTurnoManager.NAME)
public class SalaTurnoManager extends Manager<SalaTurnoDAO, SalaTurno> {

    static final String NAME = "salaTurnoManager";
    private static final long serialVersionUID = 1L;
    public List<SalaTurno> listBySala(Sala sala) {
        return getDao().listBySala(sala);
    }
    public void removerTurnosAnteriores(Sala sala) throws DAOException {
        getDao().removerTurnosAnteriores(sala);
    }

}
