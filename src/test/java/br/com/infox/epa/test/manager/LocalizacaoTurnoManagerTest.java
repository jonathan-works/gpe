package br.com.infox.epa.test.manager;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import br.com.infox.epp.turno.entity.LocalizacaoTurno;
import br.com.infox.epp.turno.manager.LocalizacaoTurnoManager;
import junit.framework.Assert;

public class LocalizacaoTurnoManagerTest {
	
	private LocalizacaoTurnoManager localizacaoTurnoManager;
	
	@Before
	public void setup() {
		 localizacaoTurnoManager = new LocalizacaoTurnoManager();
	}
	
	@Test
	public void horarioForaTurnoTest() {
		LocalizacaoTurno turnoOitoAsDoze = new LocalizacaoTurno();
		turnoOitoAsDoze.setHoraInicio(buildTime(8, 0, 0));
		turnoOitoAsDoze.setHoraFim(buildTime(12, 0, 0));
		
		int tempoGasto = localizacaoTurnoManager.calcularMinutosGastos(buildTime(16, 0, 0), buildTime(14, 0, 0), turnoOitoAsDoze);
		Assert.assertEquals(0, tempoGasto);
	}
	
	@Test
	public void horarioDentroTurnoTest() {
		LocalizacaoTurno turnoCatorzeAsDezesseteETrinta = new LocalizacaoTurno();
		turnoCatorzeAsDezesseteETrinta.setHoraInicio(buildTime(14, 0, 0));
		turnoCatorzeAsDezesseteETrinta.setHoraFim(buildTime(17, 30, 0));
		
		int tempoGasto = localizacaoTurnoManager.calcularMinutosGastos(buildTime(16, 0, 0), buildTime(14, 0, 0), turnoCatorzeAsDezesseteETrinta);
		Assert.assertEquals(120, tempoGasto);
	}
	
	@Test
	public void horarioSaindoTurnoTest() {
		LocalizacaoTurno turnoCatorzeAsDezesseteETrinta = new LocalizacaoTurno();
		turnoCatorzeAsDezesseteETrinta.setHoraInicio(buildTime(14, 0, 0));
		turnoCatorzeAsDezesseteETrinta.setHoraFim(buildTime(17, 30, 0));
		
		int tempoGasto = localizacaoTurnoManager.calcularMinutosGastos(buildTime(18, 0, 0), buildTime(16, 0, 0), turnoCatorzeAsDezesseteETrinta);
		Assert.assertEquals(90, tempoGasto);
	}
	
	@Test
	public void horarioEntrandoTurnoTest() {
		LocalizacaoTurno turnoCatorzeAsDezesseteETrinta = new LocalizacaoTurno();
		turnoCatorzeAsDezesseteETrinta.setHoraInicio(buildTime(17, 30, 0));
		turnoCatorzeAsDezesseteETrinta.setHoraFim(buildTime(19, 0, 0));
		
		int tempoGasto = localizacaoTurnoManager.calcularMinutosGastos(buildTime(18, 0, 0), buildTime(16, 0, 0), turnoCatorzeAsDezesseteETrinta);
		Assert.assertEquals(30, tempoGasto);
	}
	
	private Time buildTime(int hour, int minute, int second) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 1970);
		calendar.set(Calendar.DAY_OF_YEAR, 1);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		calendar.set(Calendar.MILLISECOND, 0);
		return new Time(calendar.getTimeInMillis());
	}
	
	@Test
	public void contarTempoUtilDiaTest() {
		List<LocalizacaoTurno> localizacaoTurnoList = new ArrayList<LocalizacaoTurno>();
		
		LocalizacaoTurno turnoOitoEMeiaAsDoze = new LocalizacaoTurno();
		turnoOitoEMeiaAsDoze.setHoraInicio(buildTime(8, 30, 0));
		turnoOitoEMeiaAsDoze.setHoraFim(buildTime(12, 0, 0));
		localizacaoTurnoList.add(turnoOitoEMeiaAsDoze);
		
		LocalizacaoTurno turnoTrezeETrintaAsDezoito = new LocalizacaoTurno();
		turnoTrezeETrintaAsDezoito.setHoraInicio(buildTime(13, 30, 0));
		turnoTrezeETrintaAsDezoito.setHoraFim(buildTime(18, 0, 0));
		localizacaoTurnoList.add(turnoTrezeETrintaAsDezoito);
		
		Assert.assertEquals(480, localizacaoTurnoManager.contarTempoUtilTurnos(localizacaoTurnoList));
	}

}
