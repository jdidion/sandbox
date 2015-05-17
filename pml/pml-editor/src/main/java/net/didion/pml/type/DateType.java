package net.didion.pml.type;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateType extends AbstractType {
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-ddZ");
    public static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ssZ");
    public static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    
    private DateFormat format;
    
    public DateType() {
        this(true, true);
    }
    
    public DateType(boolean date, boolean time) {
        super("date", Calendar.class);
        if (date && time) {
            this.format = DATE_TIME_FORMAT;
        }
        else if (date) {
            this.format = DATE_FORMAT;
        }
        else if (time) {
            this.format = TIME_FORMAT;
        }
        else {
            throw new IllegalArgumentException("At least one of 'date' and 'time' must be true");
        }
    }

    @Override
    public Object doParse(String value, String format) throws TypeValidationException {
        final DateFormat dateFormat = (null == format) ? this.format : new SimpleDateFormat(format);
        try {
            return dateFormat.parse(value);
        }
        catch (ParseException ex) {
            throw new TypeValidationException("Error parsing date " + value, ex);
        }
    }
}
