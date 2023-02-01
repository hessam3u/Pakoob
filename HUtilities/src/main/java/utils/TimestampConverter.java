package utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import androidx.room.TypeConverter;


public class TimestampConverter {

    private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @TypeConverter
    public static Calendar fromTimestamp(String value) {
        if (value != null) {
            try {
                Calendar cal = Calendar.getInstance();
//                TimeZone timeZone = TimeZone.getTimeZone("IST");
//                cal.setTimeZone(timeZone);
                cal.setTime(df.parse(value));// all done
                return cal;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        } else {
            return null;
        }
    }



    //    static SimpleDateFormat fmt=  new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);;
    @TypeConverter
    public static String dateToTimestamp(Calendar value) {
        return value == null ? null : df.format(value.getTime());

       // return fmt.format(value);
    }
}