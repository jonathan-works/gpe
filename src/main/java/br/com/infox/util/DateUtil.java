/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informação Ltda.

 Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; versão 2 da Licença.
 Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 
 Consulte a GNU GPL para mais detalhes.
 Você deve ter recebido uma cópia da GNU GPL junto com este programa; se não, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.util;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.util.Strings;



@Name("dateUtil")
@BypassInterceptors
public class DateUtil {
	
    public static final int MILESIMOS_DO_SEGUNDO = 1000;

    public static final int SEGUNDOS_DO_MINUTO = 60;

    public static final int MINUTOS_DA_HORA = 60;

    public static final int HORAS_DO_DIA = 24;

    private static final LogProvider LOG = Logging.getLogProvider(DateUtil.class);
    
	public static final int QUANTIDADE_DIAS_SEMANA = 7;
	public static final int QUANTIDADE_MESES_ANO = 12;
	
	/**
	 * Retorna a diferencia em dias entre a data inicial e final informadas.
	 * @param dataFim - Data final
	 * @param dataIni - Data Inicial
	 * @return A diferencas em dias das datas informadas.
	 */
	public static long diferencaDias(Date dataFim, Date dataIni) {
		return (dataFim.getTime() - dataIni.getTime()) / (MILESIMOS_DO_SEGUNDO*SEGUNDOS_DO_MINUTO*MINUTOS_DA_HORA*HORAS_DO_DIA);
	}
	
	/**
	 * Metodo retorna um calendar com o horario IGUAL a '23:59:59'
	 * @param date
	 * @return
	 */
	public static Calendar getEndOfDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(1970, 0, 1, 23, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar;
	}
	
	/**
	 * Metodo que recebe uma data e retorna essa data com as horas modificadas para
	 * '23:59:59'
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
		return dt.getTime();		
	}
	
	/**
	 * Metodo retorna um calendar com o horario IGUAL a '00:00:00'
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
	 * Metodo que recebe uma data e retorna essa data com as horas modificadas para
	 * '00:00:00'
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
		return dt.getTime();		
	}	
	
	/**
	 * Retorna a data atual no formato informado.
	 * @param formato - Formato que deseja receber a data.
	 * @return Data atual.
	 */
	public String getDataAtual(String formato) {
		SimpleDateFormat fm = new SimpleDateFormat(formato);
		String data = null;
		try {
			data = fm.format(new Date());
		} catch (Exception e) {
			LOG.error(".getDataAtual()", e);
		}
		return data;
	}
	
	/**
	 * Testa se a data informada está entre a data inicio e a data fim
	 * @param data - Data que deseja testar se está no intervalo
	 * @param dataInicio - Data inicio do intervalo
	 * @param dataFim - Data fim do intervalo
	 * @return  Verdadeiro se a data estiver no intervalo / 
	 * 			Falso se a data não estiver no intervalo
	 */
	public static Boolean isBetweenDates(Date data, Date dataInicio, Date dataFim){
		return (data.equals(dataInicio) || (data.after(dataInicio) && data.before(dataFim)) || data.equals(dataFim) );
	} 
	
	/**
	 * Testa se a hora informada está entre a hora inicio e a hora fim
	 * @param hora - Hora que deseja testar se está no intervalo
	 * @param horaInicio - Hora inicio do intervalo
	 * @param horaFim - Hora fim do intervalo
	 * @return  Verdadeiro se a hora estiver no intervalo / 
	 * 			Falso se a hora não estiver no intervalo
	 */
	public static Boolean isBetweenHours(Time hora, Time horaInicio, Time horaFim){
		return (hora.equals(horaInicio) || (hora.after(horaInicio) && hora.before(horaFim)) || hora.equals(horaFim));
	}
	
	/**
	 * Valida se a hora final está depois da hora inicial
	 * @param horaInicio
	 * @param horaFim
	 * @return
	 */
	public static boolean validateHour(Time horaInicio, Time horaFim) {
		return horaInicio != null && horaFim != null && horaFim.after(horaInicio);
	}
	
	/**
	 * Metodo onde retorna a data no formato informado
	 * @param data
	 * @param formato
	 * @return
	 */
	public static String getDataFormatada(Date data, String formato){
		if (data == null || Strings.isEmpty(formato)){
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(formato);
		return sdf.format(data);
	}
	
	public static Calendar dataProximoDiaSemana(Calendar data, int diaSemana) {
		while(data.get(Calendar.DAY_OF_WEEK) != diaSemana){
			data.add(Calendar.DAY_OF_MONTH, 1);
		}
		return data;
	}
	
	public static Calendar dataProximoMes(Calendar data, int mes) {
		int diferenca = (mes - data.get(Calendar.MONTH));
		if (diferenca < 0) {
			diferenca += QUANTIDADE_MESES_ANO;
		}
		data.add(Calendar.MONTH, diferenca);
		return data;
	}
	
	public static boolean isFimDeSemana(Calendar calendar) {
		return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || 
			   calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY; 
	}

	public static String getMesExtenso(Integer mes) {
		DateFormat dfmt = new SimpleDateFormat("MMM");  
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MONTH, mes-1);
		
		return dfmt.format(calendar.getTime());
	}
	
	/**
	 * Calcula a diferença em minutos entre as datas (Calendar) informadas
	 * nos parametros.
	 * @param dataInicial
	 * @param dataFim
	 * @return diferença em minutos entre as duas datas.
	 */
	public static int calculateMinutesBetweenTimes(Calendar dataInicial, Calendar dataFim) {
		long dataInicialMilli = dataInicial.getTimeInMillis();
		long dataFimMilli = dataFim.getTimeInMillis();
		return (int) (dataFimMilli-dataInicialMilli)/(MILESIMOS_DO_SEGUNDO*SEGUNDOS_DO_MINUTO);
	}
	
	/**
	 * Calcula a diferença em minutos entre as datas (Date) informadas
	 * nos parametros.
	 * @param dataInicial
	 * @param dataFim
	 * @return diferença em minutos entre as duas datas.
	 */
	public static int calculateMinutesBetweenTimes(Date dataInicial, Date dataFim) {
		long dataInicialMilli = dataInicial.getTime();
		long dataFimMilli = dataFim.getTime();
		return (int) (dataFimMilli-dataInicialMilli)/(MILESIMOS_DO_SEGUNDO*SEGUNDOS_DO_MINUTO);
	}
	
	public static int minutesToHour(int minutes){
	    return minutes / MINUTOS_DA_HORA;
	}
	
}