package br.com.infox.epp.action.crud;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.component.TurnoBean;
import br.com.infox.component.TurnoHandler;
import br.com.infox.epp.entity.LocalizacaoTurno;
import br.com.infox.epp.manager.LocalizacaoTurnoManager;
import br.com.infox.ibpm.entity.Localizacao;
import br.com.infox.util.DateUtil;

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
		for (TurnoBean turno: turnoHandler.getTurnosSelecionados()) {
			LocalizacaoTurno localizacaoTurno = new LocalizacaoTurno();
			localizacaoTurno.setLocalizacao(localizacao);
			localizacaoTurno.setDiaSemana(turno.getDiaSemana());
			localizacaoTurno.setHoraInicio(turno.getHoraInicial());
			localizacaoTurno.setHoraFim(turno.getHoraFinal());
			localizacaoTurno.setTempoTurno(DateUtil.calculateMinutesBetweenTimes(turno.getHoraInicial(), turno.getHoraFinal()));
			
			localizacaoTurnoManager.persist(localizacaoTurno);
		}
	}
}