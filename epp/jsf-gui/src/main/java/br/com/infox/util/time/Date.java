package br.com.infox.util.time;

import java.text.SimpleDateFormat;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

public class Date {
	
	private final java.util.Date date;
	
	public Date(java.util.Date date) {
		if (date == null) {
			throw new NullPointerException();
		}
		this.date = date;
	}
	public Date(DateTime dateTime) {
		this(dateTime.toDate());
	}
	public Date() {
		this(new java.util.Date());
	}

	public Date(LocalDate localDate) {
		this(localDate.toDate());
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

	public Date prevWeekday(DateRange... periodosNaoUteis) {
		DateTime date = new DateTime(this.date);
		while(DateTimeConstants.SATURDAY==date.getDayOfWeek() 
				|| DateTimeConstants.SUNDAY==date.getDayOfWeek()
				|| isInAny(date, periodosNaoUteis)
				){
			date = date.minusDays(1);
		}
		return new Date(date);
	}

	public Date nextWeekday(DateRange... periodosNaoUteis) {
		DateTime date = new DateTime(this.date);
		while(DateTimeConstants.SATURDAY==date.getDayOfWeek() 
				|| DateTimeConstants.SUNDAY==date.getDayOfWeek()
				|| isInAny(date, periodosNaoUteis)
				){
			date = date.plusDays(1);
		}
		return new Date(date);
	}

	public Date withTimeAtEndOfDay() {
		return new Date(new DateTime(this.date).withTime(23, 59, 59, 0));
	}

	public Date withTimeAtStartOfDay() {
		return new Date(new DateTime(this.date).withTimeAtStartOfDay());
	}

	public String toString(String string) {
		return new SimpleDateFormat(string).format(this.date);
	}

	public Date minusDays(int qtdDias) {
		return plusDays(-qtdDias);
	}

	public Date plusDays(int qtdDias) {
		return new Date(new DateTime(this.date).plusDays(qtdDias));
	}

	public Date minusYears(int years){
		return new Date(new DateTime(this.date).minusYears(years));
	}
	
	public Date plusYears(int years) {
		return new Date(new DateTime(this.date).plusYears(years));
	}

	public DateRange toDateRangeWithEnd(Date end){
		return new DateRange(this.toDate(), end.toDate());
	}
	
	public java.util.Date toDate(){
		return this.date;
	}
	
	public boolean after(java.util.Date other) {
		return this.date.after(other);
	}
	public int compareTo(Date other) {
		return this.date.compareTo(other.toDate());
	}
	
}