package br.com.infox.util.time;

import java.util.Date;

/**
 * @author erik
 *
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

    public DateRange(final Date date1, final Date date2) {
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

    public boolean contains(final Date date) {
        final long _date = date.getTime();
        return (_date >= this.start) && (_date <= this.end);
    }

    public boolean contains(final DateRange range) {
        return (this.start <= range.start) && (this.end >= range.end);
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

    public boolean intersects(final DateRange range) {
        return ((range.start >= this.start) && (range.start <= this.end))
                || ((range.end >= this.start) && (range.end <= this.end));
    }

    public void setEnd(final Date date) {
        final long _end = date.getTime();
        if (_end >= this.start) {
            this.end = _end;
        }
    }

    public void setStart(final Date date) {
        final long _start = date.getTime();
        if (_start <= this.end) {
            this.start = _start;
        }
    }
}
