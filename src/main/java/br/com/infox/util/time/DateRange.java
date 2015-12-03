package br.com.infox.util.time;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;

/**
 * @author erik
 */
public class DateRange {
    private long start;
    private long end;

    public static final int MILISSECONDS = 0;
    public static final int SECONDS = 1;
    public static final int MINUTES = 2;
    public static final int HOURS = 3;
    public static final int DAYS = 4;
    public static final int WEEKS = 5;
    public static final int YEARS = 5;

    public DateRange() {
        this.start = 0;
        this.end = 0;
    }

    public DateRange(DateTime date1, DateTime date2) {
        if (date1.isBefore(date2)) {
            this.start = date1.getMillis();
            this.end = date2.getMillis();
        } else {
            this.start = date2.getMillis();
            this.end = date1.getMillis();
        }
    }

    public DateRange(LocalDate date1, LocalDate date2) {
        if (date1.isBefore(date2)) {
            setStart(date1);
            setEnd(date2);
        } else {
            setStart(date2);
            setEnd(date1);
        }
    }

    public DateRange(final java.util.Date date1, final java.util.Date date2) {
        final long _d1 = date1.getTime();
        final long _d2 = date2.getTime();

        if (_d1 > _d2) {
            this.start = _d2;
            this.end = _d1;
        } else {
            this.start = _d1;
            this.end = _d2;
        }
    }

    private DateRange(Interval interval) {
        this.start = interval.getStartMillis();
        this.end = interval.getEndMillis();
    }

    public boolean contains(final java.util.Date date) {
        final long _date = date.getTime();
        return (_date >= this.start) && (_date <= this.end);
    }

    public boolean contains(final DateRange range) {
        return (this.start <= range.start) && (this.end >= range.end);
    }

    public Long getDays() {
        return get(DAYS);
    }

    public Long get(final int intervalFormat) {
        long divisor = 1;
        switch (intervalFormat) {
        case DAYS:
            divisor *= 24;
        case HOURS:
            divisor *= 60;
        case MINUTES:
            divisor *= 60;
        case SECONDS:
            divisor *= 1000;
            break;
        default:
            divisor = 1;
            break;
        }
        return new Long((this.end - this.start) / divisor);
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
        return ((range.start >= this.start) && (range.start <= this.end))
                || ((range.end >= this.start) && (range.end <= this.end));
    }

    private void setEnd(LocalDate end){
        setEnd(end.toDateTimeAtStartOfDay().withTime(23, 59, 59, 999).getMillis());
    }
    
    private void setEnd(long _end){
        if (_end >= this.start) {
            this.end = _end;
        }
    }
    
    public void setEnd(final java.util.Date date) {
        setEnd(LocalDate.fromDateFields(date));
    }

    private void setStart(final LocalDate date){
        setStart(date.toDateTimeAtStartOfDay().getMillis());
    }
    
    public void setStart(long start){
        if (start <= this.end) {
            this.start = start;
        }
    }
    
    public void setStart(final java.util.Date date) {
        setStart(LocalDate.fromDateFields(date));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (end ^ (end >>> 32));
        result = prime * result + (int) (start ^ (start >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DateRange other = (DateRange) obj;
        if (end != other.end)
            return false;
        if (start != other.start)
            return false;
        return true;
    }

    private Interval toInterval() {
        return new Interval(LocalDate.fromDateFields(getStart()).toDateTimeAtStartOfDay(), LocalDate
                .fromDateFields(getEnd()).toDateTimeAtStartOfDay().toDateTime().withTime(23, 59, 59, 999));
    }

    public DateRange union(DateRange other) {
        Interval interval1 = toInterval();
        Interval interval2 = other.toInterval();
        if (!interval1.abuts(interval2) && !interval1.overlaps(interval2)) {
            throw new IllegalStateException("Can't unite intervals that can't connect");
        }
        DateTime start = interval1.getStart().isBefore(interval2.getStart()) ? interval1.getStart() : interval2
                .getStart();
        DateTime end = interval1.getEnd().isAfter(interval2.getEnd()) ? interval1.getEnd() : interval2.getEnd();
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
            if (end <= range.end) {
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
        return toInterval().abuts(other.toInterval());
    }

    /**
     * Incrementa o período a partir do início utilizando objeto daterange como duração
     * 
     * @param dateRange
     * @return
     */
    public DateRange incrementStartByDuration(DateRange dateRange) {
        this.end = this.end + dateRange.end - dateRange.start;
        return this;
    }

    @Override
    public String toString() {
        return MessageFormat.format("{0,date,dd/MM/yyyy kk:mm:ss} to {1,date,dd/MM/yyyy kk:mm:ss} ({2} days)",
                getStart(), getEnd(), String.valueOf(getDays()));
    }
    
}
