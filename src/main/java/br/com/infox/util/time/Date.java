package br.com.infox.util.time;

import java.text.SimpleDateFormat;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

public class Date  extends java.util.Date {
    
    public Date(LocalDate localDate){
        this(localDate.toDateTimeAtStartOfDay());
    }
    
    public Date(DateTime dateTime) {
        super(dateTime.getMillis());
    }

    public int getDayOfWeek(){
        return new DateTime(this).getDayOfWeek();
    }
    
    public Date nextWeekday(){
        LocalDate result=LocalDate.fromDateFields(this);
        while(DateTimeConstants.SATURDAY==result.getDayOfWeek() || DateTimeConstants.SUNDAY==result.getDayOfWeek()){
            result = result.plusDays(1);
        }
        return new Date(result);
    }
    
    public Date toEndOfDay(){
        return new Date(new DateTime(this).withTime(23, 59, 59, 999));
    }
    
    public Date toStartOfDay(){
        return new Date(new DateTime(this).withTimeAtStartOfDay());
    }
    
    private static final long serialVersionUID = 1L;

    public String toString(String string) {
        return new SimpleDateFormat(string).format(this);
    }
    
}