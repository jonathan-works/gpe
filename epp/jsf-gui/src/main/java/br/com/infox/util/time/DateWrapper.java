package br.com.infox.util.time;

import java.util.Date;

import org.joda.time.DateTime;

public class DateWrapper extends Date {

	private static final long serialVersionUID = -8804017900837857225L;
	
	public DateWrapper(Date date) {
		super(date.getTime());
	}
	
	public String toString(String pattern) {
		DateTime dateTime = new DateTime(getTime());
		return dateTime.toString(pattern);
	}
	
	public DateWrapper plusDays(int dias) {
		DateTime dateTime = new DateTime(getTime());
		if ( dias < 0 ) {
			dateTime = dateTime.minusDays(dias);
		} else if ( dias > 0 ) {
			dateTime = dateTime.plusDays(dias);
		}
		setTime(dateTime.toDate().getTime());
		return this;
	}
    
    public DateWrapper plusMonths(int meses) {
    	DateTime dateTime = new DateTime(getTime());
		if ( meses < 0 ) {
			dateTime = dateTime.minusMonths(meses);
		} else if ( meses > 0 ) {
			dateTime = dateTime.plusMonths(meses);
		}
		setTime(dateTime.toDate().getTime());
		return this;
    }
    
    public DateWrapper plusYears(int anos) {
    	DateTime dateTime = new DateTime(getTime());
		if ( anos < 0 ) {
			dateTime = dateTime.minusYears(anos);
		} else if ( anos > 0 ) {
			dateTime = dateTime.plusYears(anos);
		}
		setTime(dateTime.toDate().getTime());
		return this;
    }
}
