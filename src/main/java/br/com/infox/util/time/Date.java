package br.com.infox.util.time;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

public class Date  extends java.util.Date {
    
    public Date() {
        super();
    }

    public Date(long date) {
        super(date);
    }

    public Date(LocalDate localDate){
        this(localDate.toDateTimeAtStartOfDay());
    }
    
    public Date(DateTime dateTime) {
        super(dateTime.getMillis());
    }

    public int getDayOfWeek(){
        return LocalDate.fromDateFields(this).getDayOfWeek();
    }
    
    public Date nextWeekday(){
        LocalDate result=LocalDate.fromDateFields(this);
        while(DateTimeConstants.SATURDAY==result.getDayOfWeek() || DateTimeConstants.SUNDAY==result.getDayOfWeek()){
            result = result.plusDays(1);
        }
        return new Date(result);
    }
    
    public Date toEndOfDay(){
        return new Date(LocalDate.fromDateFields(this).toDateTimeAtStartOfDay().withTime(23, 59, 59, 999));
    }
    
    public Date toStartOfDay(){
        return new Date(LocalDate.fromDateFields(this).toDateTimeAtStartOfDay());
    }
    
    private static final long serialVersionUID = 1L;
    
}