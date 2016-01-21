package br.com.infox.util.time;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.ReadableDuration;

/**
 * @author erik
 */
public class DateRange {
    private Interval interval;

    public static final int MILISSECONDS = 0;
    public static final int SECONDS = 1;
    public static final int MINUTES = 2;
    public static final int HOURS = 3;
    public static final int DAYS = 4;
    public static final int WEEKS = 5;
    public static final int YEARS = 5;

    public DateRange() {
        this(new DateTime(), new DateTime());
    }

    public DateRange(DateTime date1, DateTime date2) {
        if (date1.equals(date2) || date1.isBefore(date2)) {
            setInterval(new Interval(date1,date2));
        } else {
        	setInterval(new Interval(date2,date1));
        }
    }

    public DateRange(final java.util.Date date1, final java.util.Date date2) {
        this(new DateTime(date1), new DateTime(date2));
    }

    private DateRange(Interval interval) {
        setInterval(interval);
    }

    public DateRange(DateRange periodo) {
    	this(periodo.getStart().toDate(), periodo.getEnd().toDate());
	}

	private void setInterval(Interval interval) {
        this.interval = interval;
    }

    private Interval getInterval() {
        return this.interval;
    }

    public boolean contains(final java.util.Date date) {
    	long thisStart = interval.getStartMillis();
        long thisEnd = interval.getEndMillis();
        long millisInstant = date.getTime();
        return (millisInstant >= thisStart && millisInstant <= thisEnd);
    }

    public boolean contains(final DateRange range) {
        return contains(range.getStart()) && contains(range.getEnd());
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
        return new Date(getInterval().getEnd());
    }

    public Date getStart() {
        return new Date(getInterval().getStart());
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
        return getInterval().overlaps(range.getInterval());
    }
    
    public void setEnd(final java.util.Date date) {
        setEnd(new DateTime(date));
    }
    
    private void setEnd(final DateTime instant){
        setInterval(getInterval().withEnd(instant));
    }
    
    private void setStart(final DateTime instant){
        setInterval(getInterval().withStart(instant));
    }
    
    public void setStart(final java.util.Date date) {
        setStart(new DateTime(date));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((interval == null) ? 0 : interval.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DateRange)) {
            return false;
        }
        DateRange other = (DateRange) obj;
        if (interval == null) {
            if (other.interval != null) {
                return false;
            }
        } else if (!interval.equals(other.interval)) {
            return false;
        }
        return true;
    }

    public DateRange setStartToStartOfDay(){
        setInterval(getInterval().withStart(getInterval().getStart().withTimeAtStartOfDay()));
        return this;
    }
    
    public DateRange setEndToEndOfDay(){
        setInterval(getInterval().withEnd(getInterval().getEnd().withTime(23, 59, 59, 999)));
        return this;
    }
    
    private Duration toDuration() {
        return getInterval().toDuration();
    }
    
    public DateRange union(Collection<DateRange> others){
        DateRange result = this;
        for (DateRange range : others) {
            result = result.union(range);
        }
        return result;
    }
    
    public DateRange union(DateRange other) {
        if (!abuts(other) && !intersects(other)) {
            return this;
        }
        DateTime start = getInterval().getStart().isBefore(other.getInterval().getStart()) ? getInterval().getStart() : other.getInterval().getStart();
        DateTime end = getInterval().getEnd().isAfter(other.getInterval().getEnd()) ? getInterval().getEnd() : other.getInterval().getEnd();
        return new DateRange(start, end);
    }

    public DateRange intersection(DateRange other) {
        if (other == null){
            return null;
        }
        Interval overlap = getInterval().overlap(other.getInterval());
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
            if (getInterval().getEnd().isBefore(range.getInterval().getEnd())) {
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
        return getInterval().abuts(other.getInterval());
    }

    /**
     * Incrementa o período a partir do início utilizando objeto daterange como duração
     * 
     * @param periodo
     * @return
     */
    public DateRange incrementStartByDuration(DateRange periodo) {
        Duration duracaoPeriodo = periodo.toDuration().plus(Days.days(1).toStandardDuration());
		setInterval(getInterval().withDurationAfterStart(duracaoPeriodo.plus(getInterval().toDuration())));
        return this;
    }

    @Override
    public String toString() {
        final String pattern = "dd/MM/yyyy";
        final String startDate = getStart().toString(pattern);
        final String endDate = getEnd().toString(pattern);
        if (startDate.equals(endDate)){
        	return startDate;
        }
		return MessageFormat.format("[{0} - {1}]", startDate, endDate);
    }

    public static Collection<DateRange> reduce(Collection<DateRange> ranges){
        Set<DateRange> collection = new HashSet<>();
        for (DateRange dateRange : new HashSet<>(ranges)) {
            collection.add(dateRange.union(ranges));
        }
        return collection;
    }

	public DateRange withSuspensoes(List<DateRange> suspensoes) {
		DateRange result = new DateRange(getStart().toDate(), getEnd().toDate());
		Set<DateRange> applied = new HashSet<>();
		boolean changed=false;
		do {
			changed = false;
			for (DateRange suspensao : DateRange.reduce(suspensoes)) {
				DateRange connection = result.connection(suspensao);
	    		if (connection != null && applied.add(suspensao)){
	    			result = result.incrementStartByDuration(connection);
	    			changed = true;
	    		}
			}
		} while(changed);
		return result.setStartToStartOfDay().setEndToEndOfDay();
    	
	}

	public DateRange withDurationBeforeEnd(DateRange dateRange){
		return new DateRange(getInterval().withDurationBeforeEnd(dateRange.toDuration()));
	}
	
	public DateRange withDurationAfterStart(ReadableDuration duration){
		return new DateRange(getInterval().withDurationAfterStart(duration));
	}
	
	public DateRange withDurationAfterStart(DateRange dateRange){
		return new DateRange(getInterval().withDurationAfterStart(dateRange.toDuration()));
	}
	
	public DateRange withExtendedStart(Date start){
		return new DateRange(start.toDate(), getEnd().toDate());
	}
	public DateRange withExtendedEnd(Date end){
		return new DateRange(getStart().toDate(), end.toDate());
	}
	
	public DateRange withStart(Date start){
		return new DateRange(toDuration().toIntervalFrom(new DateTime(start.toDate())));
	}
	public DateRange withEnd(Date end){
		return new DateRange(toDuration().toIntervalTo(new DateTime(end.toDate())));
	}

	public boolean contains(Date date) {
		return contains(date.toDate());
	}
}
