package br.com.infox.test.component;

import java.sql.Time;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import br.com.infox.component.HorarioBean;
import br.com.infox.component.TurnoBean;
import br.com.infox.component.TurnoHandler;
import br.com.infox.epp.type.DiaSemanaEnum;

public class TurnoHandlerTest {
	@Test
	public void clearTest() {
		TurnoHandler handler = new TurnoHandler(60);
		
		Time begin = handler.getHorarios().get(0);
		Time end = handler.getHorarios().get(2);
		handler.addIntervalo(DiaSemanaEnum.SEG, begin, end);
		
		Time time = handler.getHorarios().get(10);
		HorarioBean bean = handler.getHorarioBean(DiaSemanaEnum.TER, time);
		bean.setSelected(true);
		
		handler.clearIntervalos();
		Assert.assertTrue(handler.getTurnosSelecionados().isEmpty());
	}
	
	@Test
	public void turnosSelecionadosPorIntervaloTest() {
		TurnoHandler handler = new TurnoHandler(60);
		
		Time begin10 = handler.getHorarios().get(10);
		Time end12 = handler.getHorarios().get(12);
		handler.addIntervalo(DiaSemanaEnum.SEG, begin10, end12);
		
		Time first = handler.getHorarios().get(0);
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(first);
		calendar.add(Calendar.HOUR_OF_DAY, 24);
		Time last = new Time(calendar.getTimeInMillis());
		handler.addIntervalo(DiaSemanaEnum.QUA, first, last);
		
		Assert.assertEquals(2, handler.getTurnosSelecionados().size());
		Assert.assertTrue(handler.getHorarioBeanList(DiaSemanaEnum.SEG).get(11).getSelected());
		Assert.assertFalse(handler.getHorarioBeanList(DiaSemanaEnum.SEG).get(12).getSelected());
		for (HorarioBean bean: handler.getHorarioBeanList(DiaSemanaEnum.QUA)) {
		    Assert.assertTrue(bean.getSelected());
		}
	}
	
	@Test
	public void turnosSelecionadosPorCheckHorarioBeanTest() {
		TurnoHandler handler = new TurnoHandler(60);
		
		for (int i = 10; i < 15; i++) {
			Time time = handler.getHorarios().get(i);
			HorarioBean bean = handler.getHorarioBean(DiaSemanaEnum.TER, time);
			bean.setSelected(true);
		}
		
		List<TurnoBean> beanList = handler.getTurnosSelecionados();
		Assert.assertEquals(1, beanList.size());
		
		TurnoBean bean = beanList.get(0);
		Time time10 = handler.getHorarios().get(10);
		Time time15 = handler.getHorarios().get(15);
		Assert.assertEquals(DiaSemanaEnum.TER, bean.getDiaSemana());
		Assert.assertEquals(bean.getHoraInicial(), time10);
		Assert.assertEquals(bean.getHoraFinal(), time15);
	}
}
