package com.myapp.storing;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>Created by MontolioV on 11.09.18.
 */
public class DateInterval {
    public static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss Z");
    private Date fromDate = new Date();
    private Date toDate = new Date();

    public DateInterval() {
    }

    public DateInterval(Date fromDate, Date toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    @Override
    public String toString() {
        return "between " + FORMAT.format(fromDate) + " and " + FORMAT.format(toDate);
    }
}

