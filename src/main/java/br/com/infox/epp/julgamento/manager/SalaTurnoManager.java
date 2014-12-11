package br.com.infox.epp.julgamento.manager;

import java.util.Date;
import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.joda.time.DateTime;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.julgamento.dao.SalaTurnoDAO;
import br.com.infox.epp.julgamento.entity.Sala;
import br.com.infox.epp.julgamento.entity.SalaTurno;
import br.com.infox.epp.turno.type.DiaSemanaEnum;


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
    
    public boolean isDiaHoraValido(Date data, Date horaInicio, Date horaFim, Sala sala) {
    	DateTime dia = new DateTime(data);
    	int weekDay = dia.getDayOfWeek();
    	DiaSemanaEnum diaDaSemana = DiaSemanaEnum.values()[weekDay];
    	return getDao().isSalaDisponivel(horaInicio, horaFim, diaDaSemana, sala);
    }

}
