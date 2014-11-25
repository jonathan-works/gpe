package br.com.infox.epp.julgamento.view;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.julgamento.entity.Sala;
import br.com.infox.epp.julgamento.entity.SalaTurno;
import br.com.infox.epp.julgamento.manager.SalaTurnoManager;
import br.com.infox.epp.turno.component.TurnoBean;
import br.com.infox.epp.turno.component.TurnoHandler;

@Scope(ScopeType.CONVERSATION)
@Name(SalaTurnoAction.NAME)
public class SalaTurnoAction implements Serializable {

    @In
    private SalaTurnoManager salaTurnoManager;
    @In
    private ActionMessagesService actionMessagesService;

    private Sala sala;
    private TurnoHandler turnoHandler;

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }

    public TurnoHandler getTurnoHandler() {
        return turnoHandler;
    }

    public void setTurnoHandler(TurnoHandler turnoHandler) {
        this.turnoHandler = turnoHandler;
    }

    public void newInstance(Sala sala) {
        this.sala = sala;
        createTurnoHandler();
    }

    private void createTurnoHandler() {
        turnoHandler = new TurnoHandler(UMA_HORA_EM_MINUTOS);
        for (SalaTurno salaTurno : salaTurnoManager.listBySala(sala)) {
            turnoHandler.addIntervalo(salaTurno.getDiaSemana(), salaTurno.getHoraInicio(), salaTurno.getHoraFim());
        }
    }

    public void gravarTurnos() {
        try {
            salaTurnoManager.removerTurnosAnteriores(sala);
        } catch (DAOException e) {
            actionMessagesService.handleDAOException(e);
        }
        String resultMessage = "#{eppmessages['salaTurno.erroGravacaoTurno']}";
        try {
            inserirTurnosSelecionados();
            resultMessage = "#{eppmessages['entity_updated']}";
        } catch (DAOException e) {
            LOG.error(".inserirTurnosSelecionados()", e);
        }
        FacesMessages.instance().add(resultMessage);
    }

    private void inserirTurnosSelecionados() throws DAOException {
        for (TurnoBean turno : turnoHandler.getTurnosSelecionados()) {
            SalaTurno salaTurno = new SalaTurno();
            salaTurno.setSala(sala);
            salaTurno.setDiaSemana(turno.getDiaSemana());
            salaTurno.setHoraInicio(turno.getHoraInicial());
            salaTurno.setHoraFim(turno.getHoraFinal());
            salaTurnoManager.persist(salaTurno);
        }
    }

    public static final String NAME = "salaTurnoAction";
    private static final int UMA_HORA_EM_MINUTOS = 60;
    private static final long serialVersionUID = 1L;
    private static final Log LOG = Logging.getLog(SalaTurnoAction.class);

}
