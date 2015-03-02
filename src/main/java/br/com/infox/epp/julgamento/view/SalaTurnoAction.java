package br.com.infox.epp.julgamento.view;

import java.io.Serializable;

import javax.ejb.EJB;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.julgamento.entity.Sala;
import br.com.infox.epp.julgamento.entity.SalaTurno;
import br.com.infox.epp.julgamento.manager.SalaTurnoManager;
import br.com.infox.epp.turno.component.TurnoBean;
import br.com.infox.epp.turno.component.TurnoHandler;
import br.com.infox.log.Log;
import br.com.infox.log.Logging;

@Scope(ScopeType.CONVERSATION)
@Name(SalaTurnoAction.NAME)
public class SalaTurnoAction implements Serializable {

    @In
    private SalaTurnoManager salaTurnoManager;
    @In
    private ActionMessagesService actionMessagesService;
    @In
    private InfoxMessages infoxMessages;

    private Sala sala;
    private TurnoHandler turnoHandler;

    public static final String NAME = "salaTurnoAction";

    private static final int UMA_HORA_EM_MINUTOS = 60;

    private static final long serialVersionUID = 1L;

    private static final Log LOG = Logging.getLog(SalaTurnoAction.class);

    private void createTurnoHandler() {
        this.turnoHandler = new TurnoHandler(
                SalaTurnoAction.UMA_HORA_EM_MINUTOS);
        for (final SalaTurno salaTurno : this.salaTurnoManager
                .listBySala(this.sala)) {
            this.turnoHandler.addIntervalo(salaTurno.getDiaSemana(),
                    salaTurno.getHoraInicio(), salaTurno.getHoraFim());
        }
    }

    public Sala getSala() {
        return this.sala;
    }

    public TurnoHandler getTurnoHandler() {
        return this.turnoHandler;
    }

    public void gravarTurnos() {
        try {
            this.salaTurnoManager.removerTurnosAnteriores(this.sala);
            inserirTurnosSelecionados();
            FacesMessages.instance().add(infoxMessages.get("entity_updated")); 
        } catch (final DAOException e) {
            this.actionMessagesService.handleDAOException(e);
            SalaTurnoAction.LOG.error(".inserirTurnosSelecionados()", e);
            FacesMessages.instance().add(infoxMessages.get("salaTurno.erroGravacaoTurno"));
        }
    }

    private void inserirTurnosSelecionados() throws DAOException {
        for (final TurnoBean turno : this.turnoHandler.getTurnosSelecionados()) {
            final SalaTurno salaTurno = new SalaTurno();
            salaTurno.setSala(this.sala);
            salaTurno.setDiaSemana(turno.getDiaSemana());
            salaTurno.setHoraInicio(turno.getHoraInicial());
            salaTurno.setHoraFim(turno.getHoraFinal());

            this.salaTurnoManager.persist(salaTurno);
        }
    }

    public void newInstance(final Sala sala) {
        this.sala = sala;
        createTurnoHandler();
    }

    public void setSala(final Sala sala) {
        this.sala = sala;
    }

    public void setTurnoHandler(final TurnoHandler turnoHandler) {
        this.turnoHandler = turnoHandler;
    }

}
