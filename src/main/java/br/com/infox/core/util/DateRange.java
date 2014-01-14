package br.com.infox.core.util;

import java.util.Date;

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
        
        public DateRange(Date date1, Date date2) {
            long _d1 = date1.getTime();
            long _d2 = date2.getTime();
            
            if (_d1 > _d2) {
                this.start = _d2;
                this.end = _d1;
            } else {
                this.start = _d1;
                this.end = _d2;
            }
        }
        
        public Date getStart() {
            return new Date(this.start);
        }
        
        public void setStart(Date date) {
            long _start = date.getTime();
            if (_start <= this.end) {
                this.start = _start;
            }
        }
        
        public Date getEnd() {
            return new Date(this.end);
        }
        
        public void setEnd(Date date) {
            long _end = date.getTime();
            if (_end >= this.start) {
                this.end = _end;
            }
        }
        
        public boolean contains(Date date) {
            long _date = date.getTime();
            return _date >= this.start && _date <= this.end;
        }
        
        public boolean contains(DateRange range) {
            return this.start <= range.start && this.end >= range.end;
        }
        
        public boolean intersects(DateRange range) {
            return (range.start >= this.start && range.start <= this.end) 
                    ||(range.end >= this.start && range.end <= this.end);
        }
        
        public long get(int intervalFormat) {
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
            return (this.end - this.start)/divisor;
        }
    }