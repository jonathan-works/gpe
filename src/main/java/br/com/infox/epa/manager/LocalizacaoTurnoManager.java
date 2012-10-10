package br.com.infox.epa.manager;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epa.dao.LocalizacaoTurnoDAO;
import br.com.infox.epa.entity.LocalizacaoTurno;
import br.com.infox.ibpm.entity.Localizacao;

@Name(LocalizacaoTurnoManager.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class LocalizacaoTurnoManager extends GenericManager {

	public static final String NAME = "localizacaoTurnoManager";

	@In
	private LocalizacaoTurnoDAO localizacaoTurnoDAO;
	
	public List<LocalizacaoTurno> listByLocalizacao(Localizacao localizacao) {
		return localizacaoTurnoDAO.listByLocalizacao(localizacao);
	}
	
	public int contarTempoUtilDiaByLocalizacaoList(List<Localizacao> localizacaoList) {
		List<LocalizacaoTurno> localizacaoTurnoList = new ArrayList<LocalizacaoTurno>();
		for (Localizacao l : localizacaoList) {
			for (LocalizacaoTurno lt : l.getLocalizacaoTurnoList()) {
				localizacaoTurnoList.add(lt);
			}
		}
		return contarTempoUtilDia(localizacaoTurnoList);
	}
	
	public int contarTempoUtilDia(List<LocalizacaoTurno> localizacaoTurnoList) {
		boolean[] minutosDia = new boolean[1440];
		for (LocalizacaoTurno lt : localizacaoTurnoList) {
			Calendar inicio = Calendar.getInstance();
			Calendar fim = Calendar.getInstance();
			inicio.setTime(lt.getHoraInicio());
			fim.setTime(lt.getHoraFim());
			int minutoInicioDia = (inicio.get(Calendar.HOUR_OF_DAY)*60)+
								   inicio.get(Calendar.MINUTE) - 1;
			int minutoFimDia = (fim.get(Calendar.HOUR_OF_DAY)*60)+
								fim.get(Calendar.MINUTE) - 1;
			for(;minutoInicioDia<minutoFimDia;minutoInicioDia++) {
				minutosDia[minutoInicioDia] = true;
			}
		}
		int minutoUtil = 0;
		for (boolean b : minutosDia) {
			if(b) {
				minutoUtil++;
			}
		}
		return minutoUtil;
	}
	
	/**
	 * 
	 * @param l
	 * @param inicio
	 * @param fim
	 * @return true se existir choque de horario
	 */
	public boolean verificarTurnos(Localizacao l, Time inicio, Time fim) {
		return localizacaoTurnoDAO.	countByHoraInicioFim(l, inicio, fim) > 0;
	}

	/**
	 * Verifica as possibilidades para os intervalos do turno de 
	 * uma determinada localização, para então calcular a diferença 
	 * de horas que deve ser acrescentada ao tempo gasto de uma 
	 * tarefa.
	 * @param fireTime - Hora de disparo da trigger
	 * @param pt - ProcessoEpaTarefa a ser verificado o tempo gasto
	 * @param lt - LocalizacaoTurno da tarefa em verificação.
	 * @return minutos gastos dentro do turno informado
	 */
	public int calcularMinutosGastos(Date fireTime, Date lastFire, LocalizacaoTurno lt) {
		long millisBegin = Math.max(lastFire.getTime(), lt.getHoraInicio().getTime());
		long millisEnd = Math.min(fireTime.getTime(), lt.getHoraFim().getTime());
		
		if (millisBegin < millisEnd) {
			return (int) (millisEnd - millisBegin)/(1000*60);
		}
		return 0;
	}
	
}