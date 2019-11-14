package br.com.infox.epp.test.manager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;

import org.junit.Before;
import org.junit.Test;

import br.com.infox.epp.tarefa.manager.ProcessoTarefaManager;
import br.com.infox.util.time.DateRange;

public class ProcessoEpaTarefaManagerTest {

    private ProcessoTarefaManager processoEpaTarefaManager;

    private void assertEquals(final Object val, final Object val2) {
        assert Objects.equals(val, val2);
    }

    private DateRange assertIncrementoByLocalizacaoTurno(final Calendar fim,
            final Calendar inicio, final Calendar inicioTurno,
            final Calendar fimTurno) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        final Method method = ProcessoTarefaManager.class.getDeclaredMethod(
                "getIncrementoLocalizacaoTurno", Calendar.class,
                Calendar.class, Calendar.class, Calendar.class);
        method.setAccessible(true);

        return (DateRange) method.invoke(this.processoEpaTarefaManager, fim,
                inicio, inicioTurno, fimTurno);
    }

    @Test
    public void calcularMinutosEmIntervaloTest() throws NoSuchMethodException,
    IllegalAccessException, InvocationTargetException {
        final Calendar inicioTurno = new GregorianCalendar(2013, 5, 15, 8, 00);
        final Calendar fimTurno = new GregorianCalendar(2013, 5, 15, 12, 00);
        {
            final Calendar inicio = new GregorianCalendar(2013, 5, 15, 7, 45);
            final Calendar fim = new GregorianCalendar(2013, 5, 15, 13, 00);

            final DateRange result = assertIncrementoByLocalizacaoTurno(fim,
                    inicio, inicioTurno, fimTurno);
            assertEquals(new Long(240), result.get(DateRange.MINUTES));
        }
        {
            final Calendar inicio = new GregorianCalendar(2013, 5, 15, 8, 45);
            final Calendar fim = new GregorianCalendar(2013, 5, 15, 13, 00);

            final DateRange result = assertIncrementoByLocalizacaoTurno(fim,
                    inicio, inicioTurno, fimTurno);
            assertEquals(new Long(195), result.get(DateRange.MINUTES));
        }
        {
            final Calendar inicio = new GregorianCalendar(2013, 5, 15, 7, 45);
            final Calendar fim = new GregorianCalendar(2013, 5, 15, 11, 00);

            final DateRange result = assertIncrementoByLocalizacaoTurno(fim,
                    inicio, inicioTurno, fimTurno);
            assertEquals(new Long(180), result.get(DateRange.MINUTES));
        }
        {
            final Calendar inicio = new GregorianCalendar(2013, 5, 15, 8, 45);
            final Calendar fim = new GregorianCalendar(2013, 5, 15, 11, 00);

            final DateRange result = assertIncrementoByLocalizacaoTurno(fim,
                    inicio, inicioTurno, fimTurno);
            assertEquals(new Long(135), result.get(DateRange.MINUTES));
        }
        {
            final Calendar inicio = new GregorianCalendar(2013, 5, 15, 14, 00);
            final Calendar fim = new GregorianCalendar(2013, 5, 15, 18, 00);

            final DateRange result = assertIncrementoByLocalizacaoTurno(fim,
                    inicio, inicioTurno, fimTurno);
            assertEquals(new Long(0), result.get(DateRange.MINUTES));
        }
    }

    @Test
    public void getDisparoIncrementadoTest() throws IllegalArgumentException,
    IllegalAccessException, InvocationTargetException,
    SecurityException, NoSuchMethodException {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(2013, 5, 15, 17, 45);
        final Date ultimoDisparo = calendar.getTime();
        calendar.set(2013, 5, 15, 18, 00);
        final Date disparoAtual = calendar.getTime();
        final Method method = ProcessoTarefaManager.class.getDeclaredMethod(
                "getDisparoIncrementado", Date.class, Date.class, int.class,
                int.class);
        method.setAccessible(true);
        Date result = (Date) method.invoke(this.processoEpaTarefaManager,
                ultimoDisparo, disparoAtual, Calendar.MINUTE, 30);
        assertEquals(disparoAtual, result);

        calendar.set(2013, 5, 15, 17, 55);
        result = (Date) method.invoke(this.processoEpaTarefaManager,
                ultimoDisparo, disparoAtual, Calendar.MINUTE, 10);
        assertEquals(calendar.getTime(), result);
    }

    @Before
    public void setup() {
        this.processoEpaTarefaManager = new ProcessoTarefaManager();
    }
}
