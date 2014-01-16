package br.com.infox.epp.test.manager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import br.com.infox.core.util.DateRange;
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
	public void calcularMinutosEmIntervaloTest() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Calendar inicioTurno = new GregorianCalendar(2013, 5, 15, 8, 00);
        Calendar fimTurno = new GregorianCalendar(2013, 5, 15, 12, 00);
	    {
            Calendar inicio = new GregorianCalendar(2013, 5, 15, 7, 45);
    	    Calendar fim = new GregorianCalendar(2013, 5, 15, 13, 00);
            
    	    DateRange result = assertIncrementoByLocalizacaoTurno(fim, inicio, inicioTurno, fimTurno);
    	    Assert.assertEquals(240, result.get(DateRange.MINUTES));
	    }
        {
            Calendar inicio = new GregorianCalendar(2013, 5, 15, 8, 45);
            Calendar fim = new GregorianCalendar(2013, 5, 15, 13, 00);
            
            DateRange result = assertIncrementoByLocalizacaoTurno(fim, inicio, inicioTurno, fimTurno);
            Assert.assertEquals(195, result.get(DateRange.MINUTES));
        }
        {
            Calendar inicio = new GregorianCalendar(2013, 5, 15, 7, 45);
            Calendar fim = new GregorianCalendar(2013, 5, 15, 11, 00);
            
            DateRange result = assertIncrementoByLocalizacaoTurno(fim, inicio, inicioTurno, fimTurno);
            Assert.assertEquals(180, result.get(DateRange.MINUTES));
        }
        {
            Calendar inicio = new GregorianCalendar(2013, 5, 15, 8, 45);
            Calendar fim = new GregorianCalendar(2013, 5, 15, 11, 00);
            
            DateRange result = assertIncrementoByLocalizacaoTurno(fim, inicio, inicioTurno, fimTurno);
            Assert.assertEquals(135, result.get(DateRange.MINUTES));
        }
	}

    private DateRange assertIncrementoByLocalizacaoTurno(Calendar fim, Calendar inicio, Calendar inicioTurno,
            Calendar fimTurno) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method method = ProcessoEpaTarefaManager.class.getDeclaredMethod("getIncrementoLocalizacaoTurno", Calendar.class, Calendar.class, Calendar.class, Calendar.class);
        method.setAccessible(true);

        return (DateRange) method.invoke(processoEpaTarefaManager, fim, inicio, inicioTurno, fimTurno);
    }
}
