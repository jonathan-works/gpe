package br.com.infox.test.component;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

import org.junit.Test;

import br.com.infox.epp.turno.component.HorarioBean;
import br.com.infox.epp.turno.component.TurnoBean;
import br.com.infox.epp.turno.component.TurnoHandler;
import br.com.infox.epp.turno.type.DiaSemanaEnum;

public class TurnoHandlerTest {

    private void assertEquals(final Object obj1, final Object obj2) {
        assert Objects.equals(obj1, obj2);
    }

    private void assertFalse(final Boolean isValid) {
        assert (isValid != null) && !isValid;
    }

    private void assertTrue(final Boolean isValid) {
        assert (isValid != null) && isValid;
    }

    @Test
    public void clearTest() {
        final TurnoHandler handler = new TurnoHandler(60);

        final Date begin = handler.getHorarios().get(0);
        final Date end = handler.getHorarios().get(2);
        handler.addIntervalo(DiaSemanaEnum.SEG, begin, end);

        final Date time = handler.getHorarios().get(10);
        final HorarioBean bean = handler
                .getHorarioBean(DiaSemanaEnum.TER, time);
        bean.setSelected(true);

        handler.clearIntervalos();
        assertTrue(handler.getTurnosSelecionados().isEmpty());
    }

    @Test
    public void turnosSelecionadosPorCheckHorarioBeanTest() {
        final TurnoHandler handler = new TurnoHandler(60);

        for (int i = 10; i < 15; i++) {
            final Date time = handler.getHorarios().get(i);
            final HorarioBean bean = handler.getHorarioBean(DiaSemanaEnum.TER,
                    time);
            bean.setSelected(true);
        }

        final List<TurnoBean> beanList = handler.getTurnosSelecionados();
        assertEquals(1, beanList.size());

        final TurnoBean bean = beanList.get(0);
        final Date time10 = handler.getHorarios().get(10);
        final Date time15 = handler.getHorarios().get(15);
        assertEquals(DiaSemanaEnum.TER, bean.getDiaSemana());
        assertEquals(bean.getHoraInicial(), time10);
        assertEquals(bean.getHoraFinal(), time15);
    }

    @Test
    public void turnosSelecionadosPorIntervaloTest() {
        final TurnoHandler handler = new TurnoHandler(60);

        final Date begin10 = handler.getHorarios().get(10);
        final Date end12 = handler.getHorarios().get(12);
        handler.addIntervalo(DiaSemanaEnum.SEG, begin10, end12);

        final Date first = handler.getHorarios().get(0);
        final GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(first);
        calendar.add(Calendar.HOUR_OF_DAY, 24);
        final Date last = new Date(calendar.getTimeInMillis());
        handler.addIntervalo(DiaSemanaEnum.QUA, first, last);

        assertEquals(2, handler.getTurnosSelecionados().size());
        assertTrue(handler.getHorarioBeanList(DiaSemanaEnum.SEG).get(11)
                .getSelected());
        assertFalse(handler.getHorarioBeanList(DiaSemanaEnum.SEG).get(12)
                .getSelected());
        for (final HorarioBean bean : handler
                .getHorarioBeanList(DiaSemanaEnum.QUA)) {
            assertTrue(bean.getSelected());
        }
    }
}
