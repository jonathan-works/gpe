package br.com.infox.core.util;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;

@Named
@RequestScoped
public class DateUtil {

    public static final int MILESIMOS_DO_SEGUNDO = 1000;

    public static final int SEGUNDOS_DO_MINUTO = 60;

    public static final int MINUTOS_DA_HORA = 60;

    public static final int HORAS_DO_DIA = 24;

    public static final int QUANTIDADE_DIAS_SEMANA = 7;
    public static final int QUANTIDADE_MESES_ANO = 12;

    private static DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

    public DateUtil() {

    }

    /**
     * Converte uma instância de Date para uma String.
     *
     * @param data
     * @return String no formato dd/MM/yyyy
     */
    public static String formatarData(Date data) {
        try {
            return formatter.format(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Converte uma instância de Date para uma String no formato informado.
     *
     * @param data
     * @param formato
     * @return String no formato informado
     */
    public static String formatarData(Date data, String formato) {
        try {
            if(StringUtil.isEmpty(formato)) {
                formato = "dd/MM/yyyy";
            }
            SimpleDateFormat sdf = new SimpleDateFormat(formato);
            return sdf.format(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Converte uma instância de Date para uma String em formato por extenso.
     *
     * @param data
     * @return String no formato dd de mesPorExtenso de yyyy
     */
    public static String formatarDataPorExtenso(Date data) {
        try {
            Locale br = new Locale("pt", "BR");
            DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, br);
            return df.format(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Adiciona/Subtrai dias de uma data
     * @param dias
     * @param data
     * @return
     */
    public static Date adicionarDias(int dias, Date data) {
		Calendar c = Calendar.getInstance();
		c.setTime(data);
		c.add(Calendar.DAY_OF_MONTH, dias);
		return c.getTime();
	}


    /**
     * Retorna um {@link java.util.Date} sem a informação da hora.
     * É utilizado em comparações de datas que devem desconsiderar o horário.
     * @param data
     * @return
     */
    public static Date getDateHoraZerada(Date data) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(data);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

    /**
     * Retorna um {@link java.util.Date} sem a informação da data (setado para 01/01/1970).
     * É utilizado em comparações de horas que devem desconsiderar a data.
     * @param data
     * @return
     */
    public static Time getTimeDataZerada(Date data) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(data);
		calendar.set(1970, 0, 1);
		return new Time(calendar.getTime().getTime());
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
        return getEndOfDay(new DateTime(date.getTime())).toDate();
    }

    public static DateTime getEndOfDay(DateTime date) {
        if (date == null) {
            return null;
        }
        return date.withTime(23, 59, 59, 0);
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
        return getBeginningOfDay(new DateTime(date.getTime())).toDate();
    }

    public static DateTime getBeginningOfDay(DateTime data) {
        return data.withTime(0, 0, 0, 0);
    }

    @Produces
    @Named("currentDateTime")
    public DateTime currentDateTime(){
    	return new DateTime();
    }

    @Produces
    @Named("currentDate")
    public Date currentDate(){
    	return new Date();
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

    public static boolean isDataMaiorIgual(Date date1, Date date2) {
        date1 = DateUtil.getBeginningOfDay(date1);
        date2 = DateUtil.getBeginningOfDay(date2);
        return date1.getTime() >= date2.getTime();
    }

    public static boolean isDataMenorIgual(Date date1, Date date2) {
        date1 = DateUtil.getBeginningOfDay(date1);
        date2 = DateUtil.getBeginningOfDay(date2);
        return date1.getTime() <= date2.getTime();
    }

    public static Boolean isFinalDeSemana(DateTime dia) {
        return DateTimeConstants.SATURDAY == dia.getDayOfWeek() || DateTimeConstants.SUNDAY == dia.getDayOfWeek();
    }

    public static Date getInicioAno(Date data) {
        DateTime dateTime = new DateTime(data).withZone(DateTimeZone.UTC);
        return dateTime.withDate(dateTime.getYear(), 1, 1).withTimeAtStartOfDay().toDate();
    }

    public static Date getFimAno(Date data) {
        DateTime dateTime = new DateTime(data).withZone(DateTimeZone.UTC);
        return dateTime.withDate(dateTime.getYear(), 12, 31).withTime(23, 59, 59, 999).toDate();
    }

    public static Date parseDate(String date, String formato) {
        try {
            if (date == null || date == "") {
                return null;
            }
            SimpleDateFormat sdf = new SimpleDateFormat(formato);
            sdf.setLenient(false);
            return sdf.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * Converte intervalo entre duas datas para uma String em formato por extenso.
     * @param dataIni, dataFim
     * @return String no formato X anos(s), X mes(es) e X dia(s)
     */
    public static String formatarIntervaloDataPorExtenso(Date dataIni, Date dataFim) {
        String msg = "0 ano(s), 0 mes(es) e 0 dia(s)";
        GregorianCalendar gcIni = new GregorianCalendar();
        gcIni.setTime(dataIni);

        GregorianCalendar gcFim = new GregorianCalendar();
        gcFim.setTime(dataFim);

        long dif = gcFim.getTimeInMillis() - gcIni.getTimeInMillis();
        if (dif > 0) {
            GregorianCalendar gcDif = new GregorianCalendar();
            gcDif.setTimeInMillis(dif);
            long ano = gcFim.get(GregorianCalendar.YEAR) - gcIni.get(GregorianCalendar.YEAR);
            long mes = gcDif.get(GregorianCalendar.MONTH);
            long dia = gcDif.get(GregorianCalendar.DAY_OF_MONTH);
            msg = ano + " ano(s), " + mes + " mes(es) e " + dia + " dia(s) ";
        }

        return msg;
    }

}
