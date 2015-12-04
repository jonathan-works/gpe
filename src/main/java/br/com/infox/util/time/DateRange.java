package br.com.infox.util.time;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.LocalDate;

/**
 * @author erik
 */
public class DateRange {
    private DateTime start;
    private DateTime end;

    public static final int MILISSECONDS = 0;
    public static final int SECONDS = 1;
    public static final int MINUTES = 2;
    public static final int HOURS = 3;
    public static final int DAYS = 4;
    public static final int WEEKS = 5;
    public static final int YEARS = 5;

    public DateRange() {
        setStart(new DateTime());
        setEnd(new DateTime());
    }

    public DateRange(DateTime date1, DateTime date2) {
        if (date1.isBefore(date2)) {
            setStart(date1);
            setEnd(date2);
        } else {
            setStart(date2);
            setEnd(date1);
        }
    }

    public DateRange(LocalDate date1, LocalDate date2) {
        this(date1.toDateTimeAtStartOfDay(), date2.toDateTimeAtStartOfDay());
    }

    public DateRange(final java.util.Date date1, final java.util.Date date2) {
        this(new DateTime(date1), new DateTime(date2));
    }

    private DateRange(Interval interval) {
        this.start = interval.getStart();
        this.end = interval.getEnd();
    }

    public boolean contains(final java.util.Date date) {
        return toInterval().contains(new DateTime(date));
    }

    public boolean contains(final DateRange range) {
        return toInterval().contains(range.toInterval());
    }

    public Long getDays() {
        return get(DAYS);
    }

    public Long get(final int intervalFormat) {
        switch (intervalFormat) {
        case DAYS:
            return toDuration().getStandardDays();
        case HOURS:
            return toDuration().getStandardHours();
        case MINUTES:
            return toDuration().getStandardMinutes();
        case SECONDS:
            return toDuration().getStandardSeconds();
        default:
            return toDuration().getMillis();
        }
    }

    public Date getEnd() {
        return new Date(this.end);
    }

    public Date getStart() {
        return new Date(this.start);
    }

    public boolean intersectsAny(DateRange... ranges){
        for (DateRange range : ranges) {
            if (intersects(range)){
                return true;
            }
        }
        return false;
    }
    
    public boolean intersects(final DateRange range) {
        if (range == null) {
            return false;
        }
        return toInterval().overlaps(range.toInterval());
    }
    
    public void setEnd(final java.util.Date date) {
        setEnd(new DateTime(date));
    }
    
    private void setEnd(final DateTime instant){
        this.end = instant;
    }
    
    private void setStart(final DateTime instant){
        this.start = instant;
    }
    
    public void setStart(final java.util.Date date) {
        setStart(new DateTime(date));
    }

    @Override
    public int hashCode() {
        return toInterval().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return toInterval().equals(obj);
    }

    public DateRange setStartToStartOfDay(){
        setStart(this.start.withTimeAtStartOfDay());
        return this;
    }
    
    public DateRange setEndToEndOfDay(){
        setStart(this.end.withTime(23, 59, 59, 999));
        return this;
    }
    
    private Duration toDuration() {
        return new Duration(start, end);
    }
    
    private Interval toInterval() {
        return new Interval(start, end);
    }

    public DateRange union(DateRange other) {
        if (!abuts(other) && !intersects(other)) {
            return this;
        }
        DateTime start = this.start.isBefore(other.start) ? this.start : other.start;
        DateTime end = this.end.isAfter(other.end) ? this.end : other.end;
        return new DateRange(start, end);
    }

    public DateRange intersection(DateRange other) {
        Interval overlap = toInterval().overlap(other.toInterval());
        return overlap == null ? null : new DateRange(overlap);
    }

    public DateRange connection(DateRange range) {
        if (range == null){
            return null;
        }
        DateRange result = null;
        if (abuts(range)) {
            result = range;
        } else if (intersects(range)) {
            if (end.compareTo(range.end)<=0) {
                result = range;
            } else {
                result = intersection(range);
            }
        }
        return result;
    }

    public Collection<? extends DateRange> connections(Collection<? extends DateRange> ranges) {
        Collection<DateRange> result = new ArrayList<>();
        for (DateRange dateRange : ranges) {
            DateRange connection = connection(dateRange);
            if (connection != null) {
                result.add(connection);
            }
        }
        return result;
    }

    public boolean abuts(DateRange other) {
        if (other == null){
            return false;
        }
        return toInterval().abuts(other.toInterval());
    }

    /**
     * Incrementa o período a partir do início utilizando objeto daterange como duração
     * 
     * @param dateRange
     * @return
     */
    public DateRange incrementStartByDuration(DateRange dateRange) {
        setInterval(toInterval().withDurationAfterStart(toDuration().plus(dateRange.toDuration())));
        return this;
    }

    private void setInterval(Interval interval) {
        setStart(interval.getStart());
        setStart(interval.getEnd());
    }

    @Override
    public String toString() {
        String pattern = "dd/MM/yyyy kk:mm:ss";
        return MessageFormat.format("{0} to {1} ({2} days)", getStart().toString(pattern), getEnd().toString(pattern), String.valueOf(getDays()));
    }

    public static DateRange merge(Collection<DateRange> ranges) {
        DateRange result = null;
        for (DateRange dateRange : ranges) {
            result = dateRange.union(result);
        }
        return result;
    }
    
}
