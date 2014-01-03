package br.com.infox.epp.turno.crud;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.util.DateUtil;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.turno.component.TurnoBean;
import br.com.infox.epp.turno.component.TurnoHandler;
import br.com.infox.epp.turno.entity.LocalizacaoTurno;
import br.com.infox.epp.turno.manager.LocalizacaoTurnoManager;

/**
 * 
 * @author Daniel
 *
 */
@Name(LocalizacaoTurnoAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class LocalizacaoTurnoAction {

	private static final int UMA_HORA_EM_MINUTOS = 60;

    public static final String NAME = "localizacaoTurnoAction";
    private static final Log LOG = Logging.getLog(LocalizacaoTurnoAction.class);

	@In
	private LocalizacaoTurnoManager localizacaoTurnoManager;
	
	private Localizacao localizacao;
	
	private TurnoHandler turnoHandler;
	
	public void newInstance(Localizacao localizacao) {
		this.localizacao = localizacao;
		createTurnoHandler();
	}
	
	private void createTurnoHandler() {
		turnoHandler = new TurnoHandler(UMA_HORA_EM_MINUTOS);
		for (LocalizacaoTurno localizacaoTurno: localizacaoTurnoManager.listByLocalizacao(localizacao)) {
			turnoHandler.addIntervalo(localizacaoTurno.getDiaSemana(), localizacaoTurno.getHoraInicio(), localizacaoTurno.getHoraFim());
		}
	}

	public TurnoHandler getTurnoHandler() {
		return turnoHandler;
	}

	public void setTurnoHandler(TurnoHandler turnoHandler) {
		this.turnoHandler = turnoHandler;
	}
	
	public void gravarTurnos() {
		localizacaoTurnoManager.removerTurnosAnteriores(localizacao);
		inserirTurnosSelecionados();
	}

	private void inserirTurnosSelecionados() {
		boolean houveErro = false;
		for (TurnoBean turno: turnoHandler.getTurnosSelecionados()) {
			LocalizacaoTurno localizacaoTurno = new LocalizacaoTurno();
			localizacaoTurno.setLocalizacao(localizacao);
			localizacaoTurno.setDiaSemana(turno.getDiaSemana());
			localizacaoTurno.setHoraInicio(turno.getHoraInicial());
			localizacaoTurno.setHoraFim(turno.getHoraFinal());
			localizacaoTurno.setTempoTurno(DateUtil.calculateMinutesBetweenTimes(turno.getHoraInicial(), turno.getHoraFinal()));
			
			try {
				localizacaoTurnoManager.persist(localizacaoTurno);
			} catch (DAOException e) {
				houveErro = true;
				LOG.error(".inserirTurnosSelecionados()", e);
			}
		}
		if (!houveErro) {
			FacesMessages.instance().add("#{messages['entity_updated']}");
		} else {
			FacesMessages.instance().add("#{messages['localizacaoTurno.erroGravacaoTurno']}");
		}
	}
}