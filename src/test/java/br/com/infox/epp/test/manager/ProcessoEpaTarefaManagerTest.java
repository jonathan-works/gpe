package br.com.infox.epp.test.manager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import br.com.infox.epp.tarefa.manager.ProcessoEpaTarefaManager;

public class ProcessoEpaTarefaManagerTest {

	private ProcessoEpaTarefaManager processoEpaTarefaManager;
	
	@Before
	public void setup() {
		processoEpaTarefaManager = new ProcessoEpaTarefaManager();
	}
	
	@Test
	public void getDisparoIncrementadoTest() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException {
		Calendar calendar = Calendar.getInstance();
		calendar.set(2013, 5, 15, 17, 45);
		Date ultimoDisparo = calendar.getTime();
		calendar.set(2013, 5, 15, 18, 00);
		Date disparoAtual = calendar.getTime();
		Method method = ProcessoEpaTarefaManager.class.getDeclaredMethod("getDisparoIncrementado", Date.class, Date.class, int.class, int.class);
		method.setAccessible(true);
		Date result = (Date) method.invoke(processoEpaTarefaManager, ultimoDisparo, disparoAtual, Calendar.MINUTE, 30);
		Assert.assertEquals(disparoAtual, result);
		
		calendar.set(2013, 5,15,17,55);
		result = (Date) method.invoke(processoEpaTarefaManager, ultimoDisparo, disparoAtual, Calendar.MINUTE, 10);
		Assert.assertEquals(calendar.getTime(), result);
	}
	
	@Test
	public void calcularMinutosEmIntervaloTest() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException {
		Calendar calendar = Calendar.getInstance();
		calendar.set(2013, 5, 15, 8, 00);
		Date inicioTurno = calendar.getTime();
		calendar.set(2013, 5, 17, 12, 00);
		Date fimTurno = calendar.getTime();
		
		calendar.set(Calendar.HOUR_OF_DAY, 7);
		calendar.set(Calendar.MINUTE, 45);
		Date inicio = calendar.getTime();
		calendar.set(Calendar.HOUR_OF_DAY, 13);
		calendar.set(Calendar.MINUTE, 00);
		Date fim = calendar.getTime();
		Method method =ProcessoEpaTarefaManager.class.getDeclaredMethod("calcularMinutosEmIntervalo", Date.class, Date.class, Date.class, Date.class);
		method.setAccessible(true);

		Float result = (Float) method.invoke(processoEpaTarefaManager, inicio, fim, inicioTurno, fimTurno);
		Assert.assertEquals(240.0f, result);
		
		calendar.set(Calendar.HOUR_OF_DAY, 8);
		calendar.set(Calendar.MINUTE, 45);
		inicio = calendar.getTime();
		calendar.set(Calendar.HOUR_OF_DAY, 13);
		calendar.set(Calendar.MINUTE, 00);
		fim = calendar.getTime();
		result = (Float) method.invoke(processoEpaTarefaManager, inicio, fim, inicioTurno, fimTurno);
		Assert.assertEquals(195.0f, result);
		
		calendar.set(Calendar.HOUR_OF_DAY, 7);
		calendar.set(Calendar.MINUTE, 45);
		inicio = calendar.getTime();
		calendar.set(Calendar.HOUR_OF_DAY, 11);
		calendar.set(Calendar.MINUTE, 00);
		fim = calendar.getTime();
		result = (Float) method.invoke(processoEpaTarefaManager, inicio, fim, inicioTurno, fimTurno);
		Assert.assertEquals(180.0f, result);
		
		calendar.set(Calendar.HOUR_OF_DAY, 8);
		calendar.set(Calendar.MINUTE, 45);
		inicio = calendar.getTime();
		calendar.set(Calendar.HOUR_OF_DAY, 11);
		calendar.set(Calendar.MINUTE, 00);
		fim = calendar.getTime();
		result = (Float) method.invoke(processoEpaTarefaManager, inicio, fim, inicioTurno, fimTurno);
		Assert.assertEquals(135.0f, result);
	}
}
