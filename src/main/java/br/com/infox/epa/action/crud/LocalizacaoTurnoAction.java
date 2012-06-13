package br.com.infox.epa.action.crud;

import java.sql.Time;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

import br.com.infox.epa.entity.LocalizacaoTurno;
import br.com.infox.epa.manager.LocalizacaoTurnoManager;
import br.com.infox.ibpm.component.tree.LocalizacaoTreeHandler;
import br.com.infox.ibpm.entity.Localizacao;
import br.com.infox.ibpm.home.LocalizacaoHome;
import br.com.infox.util.DateUtil;
import br.com.itx.component.AbstractHome;

/**
 * 
 * @author Daniel
 *
 */
@Name(LocalizacaoTurnoAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class LocalizacaoTurnoAction extends AbstractHome<LocalizacaoTurno> {

	private static final long serialVersionUID = 1L;

	public static final String NAME = "localizacaoTurnoAction";

	@In
	private LocalizacaoTurnoManager localizacaoTurnoManager; 
	
	private LocalizacaoTreeHandler localizacaoTreeHandler = new LocalizacaoTreeHandler();
	private List<LocalizacaoTurno> localizacaoTurnoList;
	private Localizacao localizacao;
	
	@Override
	protected boolean beforePersistOrUpdate() {
		getInstance().setLocalizacao(localizacao);
		Time horaInicio = instance.getHoraInicio();
		Time horaFim = instance.getHoraFim();
		boolean intervalo = localizacaoTurnoManager.verificarIntervalo(horaInicio, horaFim);
		if(intervalo) {
			FacesMessages.instance().add("Hora inicio deve ser menor que hora fim.");
			return false;
		}
		boolean choqueTurnos = localizacaoTurnoManager.verificarTurnos
				(localizacao, horaInicio, horaFim);
		if(choqueTurnos) {
			FacesMessages.instance().add("Choque de horário para a localização "+
										 localizacao.getLocalizacao()+".");
		} else {
			Calendar inicio = Calendar.getInstance();
			Calendar fim = Calendar.getInstance();
			inicio.setTime(horaInicio);
			fim.setTime(horaFim);
			int tempoTurno = DateUtil.calculateMinutesBetweenTimes(inicio, fim);
			instance.setTempoTurno(tempoTurno);
		}
		return !choqueTurnos;
	}
	
	@Override
	protected String afterPersistOrUpdate(String ret) {
		newInstance();
		listByLocalizacao();
		return ret;
	}
	
	@Override
	public String remove(LocalizacaoTurno obj) {
		String remove = super.remove(obj);
		if(remove != null) {
			localizacaoTurnoList.remove(obj);
		}
		return remove;
	}

	public void removeAll() {
		for (Iterator<LocalizacaoTurno> iterator = localizacaoTurnoList.iterator(); iterator.hasNext();) {
			LocalizacaoTurno nt = iterator.next();
			getEntityManager().remove(nt);
			iterator.remove();
		}
		FacesMessages.instance().add("Registros removidos com sucesso!");
	}
		
	public void init() {
		localizacao = LocalizacaoHome.instance().getInstance();
		listByLocalizacao();
	}

	private void listByLocalizacao() {
		localizacaoTurnoList = localizacaoTurnoManager.listByLocalizacao(localizacao);
	}	

	public void setLocalizacaoTreeHandler(LocalizacaoTreeHandler localizacaoTreeHandler) {
		this.localizacaoTreeHandler = localizacaoTreeHandler;
	}

	public LocalizacaoTreeHandler getLocalizacaoTreeHandler() {
		return localizacaoTreeHandler;
	}

	public void setLocalizacaoTurnoList(List<LocalizacaoTurno> localizacaoTurnoList) {
		this.localizacaoTurnoList = localizacaoTurnoList;
	}

	public List<LocalizacaoTurno> getLocalizacaoTurnoList() {
		return localizacaoTurnoList;
	}

	
}