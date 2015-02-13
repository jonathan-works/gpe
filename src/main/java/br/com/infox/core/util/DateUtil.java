package br.com.infox.core.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public final class DateUtil {

    public static final int MILESIMOS_DO_SEGUNDO = 1000;

    public static final int SEGUNDOS_DO_MINUTO = 60;

    public static final int MINUTOS_DA_HORA = 60;

    public static final int HORAS_DO_DIA = 24;

    public static final int QUANTIDADE_DIAS_SEMANA = 7;
    public static final int QUANTIDADE_MESES_ANO = 12;

    private DateUtil() {

    }

    /**
     * Retorna a diferencia em dias entre a data inicial e final informadas.
     * 
     * @param dataFim - Data final
     * @param dataIni - Data Inicial
     * @return A diferencas em dias das datas informadas.
     */
    public static long diferencaDias(Date dataFim, Date dataIni) {
        return (dataFim.getTime() - dataIni.getTime())
                / (MILESIMOS_DO_SEGUNDO * SEGUNDOS_DO_MINUTO * MINUTOS_DA_HORA * HORAS_DO_DIA);
    }

    /**
     * Metodo retorna um calendar com o horario IGUAL a '23:59:59.999'
     * 
     * @param date
     * @return
     */
    public static Calendar getEndOfDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(1970, 0, 1, 23, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar;
    }

    /**
     * Metodo que recebe uma data e retorna essa data com as horas modificadas
     * para '23:59:59.999'
     * 
     * @param date
     * @return
     */
    public static Date getEndOfDay(Date date) {
        if (date == null) {
            return null;
        }
        Calendar dt = new GregorianCalendar();
        dt.setTime(date);
        dt.set(Calendar.HOUR_OF_DAY, 23);
        dt.set(Calendar.MINUTE, 59);
        dt.set(Calendar.SECOND, 59);
        dt.set(Calendar.MILLISECOND, 999);
        return dt.getTime();
    }

    /**
     * Metodo retorna um calendar com o horario IGUAL a '00:00:00'
     * 
     * @param date
     * @return
     */
    public static Calendar getBeginningOfDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(1970, 0, 1, 0, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    /**
     * Metodo que recebe uma data e retorna essa data com as horas modificadas
     * para '00:00:00.000'
     * 
     * @param date
     * @return
     */
    public static Date getBeginningOfDay(Date date) {
        if (date == null) {
            return null;
        }
        Calendar dt = new GregorianCalendar();
        dt.setTime(date);
        dt.set(Calendar.HOUR_OF_DAY, 0);
        dt.set(Calendar.MINUTE, 0);
        dt.set(Calendar.SECOND, 0);
        dt.set(Calendar.MILLISECOND, 0);
        return dt.getTime();
    }

    /**
     * Calcula a diferença em minutos entre as datas (Date) informadas nos
     * parametros.
     * 
     * @param dataInicial
     * @param dataFim
     * @return diferença em minutos entre as duas datas.
     */
    public static int calculateMinutesBetweenTimes(Date dataInicial,
            Date dataFim) {
        long dataInicialMilli = dataInicial.getTime();
        long dataFimMilli = dataFim.getTime();
        return (int) (dataFimMilli - dataInicialMilli)
                / (MILESIMOS_DO_SEGUNDO * SEGUNDOS_DO_MINUTO);
    }

}
