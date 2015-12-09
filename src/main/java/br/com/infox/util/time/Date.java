package br.com.infox.util.time;

import java.text.SimpleDateFormat;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

public class Date extends java.util.Date {

	public Date(java.util.Date date) {
		super(date.getTime());
	}

	public Date(DateTime dateTime) {
		super(dateTime.getMillis());
	}

	public Date() {
		super();
	}

	public Date(LocalDate localDate) {
		this(localDate.toDateTimeAtStartOfDay());
	}

	public int getDayOfWeek() {
		return new DateTime(this).getDayOfWeek();
	}

	private static boolean isInAny(DateTime date, DateRange... ranges){
		for (DateRange range : ranges) {
			if (range.setStartToStartOfDay().setEndToEndOfDay().contains(date.toDate())){
				return true;
			}
		}
		return false;
	}
	
	public Date nextWeekday(DateRange... periodosNaoUteis) {
		DateTime date = new DateTime(this);
		while(DateTimeConstants.SATURDAY==date.getDayOfWeek() 
				|| DateTimeConstants.SUNDAY==date.getDayOfWeek()
				|| isInAny(date, periodosNaoUteis)
				){
			date = date.plusDays(1);
		}
		return new Date(date);
	}

	public Date withTimeAtEndOfDay() {
		return new Date(new DateTime(this).withTime(23, 59, 59, 999));
	}

	public Date withTimeAtStartOfDay() {
		return new Date(new DateTime(this).withTimeAtStartOfDay());
	}

	public String toString(String string) {
		return new SimpleDateFormat(string).format(this);
	}

	public Date minusDays(int qtdDias) {
		return plusDays(-qtdDias);
	}

	public Date plusDays(int qtdDias) {
		return new Date(new DateTime(this).plusDays(qtdDias));
	}

	public Date minusYears(int years){
		return new Date(new DateTime(this).minusYears(years));
	}
	
	public Date plusYears(int years) {
		return new Date(new DateTime(this).plusYears(years));
	}

	private static final long serialVersionUID = 1L;

}