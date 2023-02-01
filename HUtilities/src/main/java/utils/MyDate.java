package utils;

import android.content.Intent;

import com.github.eloyzone.jalalicalendar.DateConverter;
import com.github.eloyzone.jalalicalendar.JalaliDate;
import com.github.eloyzone.jalalicalendar.JalaliDateFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

//1399-12-24 edit
//1399-04-21 edit
public class MyDate {
    public static final Calendar SqlMaxDateTime = Calendar.getInstance();
    public static final Calendar SqlMinDateTime = Calendar.getInstance();
    public static final String[] PersianMonths = new String[]{"فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور", "مهر", "آبان", "آذر", "دی", "بهمن","اسفند"};
    public static final String[] PersianWeekdays = new String[]{"شنبه", "یکشنبه", "دوشنبه", "سه\u200Cشنبه", "چهارشنبه", "پنج شنبه", "جمعه"};

    public MyDate(){
        SqlMaxDateTime.set(9999, 12, 31, 23, 59, 59);
        SqlMinDateTime.set(1753, 1, 1, 12, 0, 0);
    }
    static DateConverter dateConverter = new DateConverter();
    public static JalaliDate getJalaliDate(Calendar dt){
        return dateConverter.gregorianToJalali(dt.get(Calendar.YEAR), dt.get(Calendar.MONTH) + 1, dt.get(Calendar.DAY_OF_MONTH));
    }
    public static String getDayOfWeek(JalaliDate jd){
        return jd.getDayOfWeek().getStringInPersian(); // دوشنبه
    }
    public static boolean isLeapYearPersian(int year){
        JalaliDate jd = new JalaliDate(year, 1, 1);
        return jd.isLeapYear();
    }
    public static String getDayOfWeekInPersianFromCalendar(Calendar calendar){
        int wd = calendar.get(Calendar.DAY_OF_WEEK);
        wd = (wd) % 7;

        return PersianWeekdays[wd];
    }

    //---------------------- Kar ba C# API--------------------------
    //Kar ba C# API - Calendar to String
    public static final DateFormat cSSqlFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
    public static final SimpleDateFormat cSJSONFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
    public static final SimpleDateFormat cSDateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);

    public static String CalendarToCSharpJSONAcceptable(Calendar cl){
        return cSJSONFormatter.format(cl.getTime());
    }
    public static String CalendarToCSharpDateTimeAcceptable(Calendar cl){
        return cSDateTimeFormatter.format(cl.getTime());
    }
    public static String CalendarToCSharpSQLString(Calendar cl){
        return cSSqlFormatter.format(cl.getTime());
    }
    //Kar ba C# API - String to Calendar
    public static  Calendar CalendarFromCSharpSQLString(String st){
        //input format : 2020-03-22T18:00:00
        int year = 0, month = 0, day = 0, hour = 0, min = 0, sec = 0;
        for (int i = 0;i < 4; i++) {year = (st.charAt(i) - '0') + year * 10;}
        for (int i = 5;i < 7; i++) {month = (st.charAt(i) - '0') + month * 10;}
        for (int i = 8;i < 10; i++) {day = (st.charAt(i) - '0') + day * 10;}
        for (int i = 11;i < 13; i++) {hour = (st.charAt(i) - '0') + hour * 10;}
        for (int i = 14;i < 16; i++) {min = (st.charAt(i) - '0') + min * 10;}
        for (int i = 17;i < 19; i++) {sec = (st.charAt(i) - '0') + sec * 10;}

        Calendar res = Calendar.getInstance();
        //HHH 1400-03-22, -1 added. نمیدونم درسته یا نه
        res.set(year, month - 1, day, hour, min, sec);
        return res;

    }
    public static final JalaliDateFormatter per_yyyymmdd = new JalaliDateFormatter("yyyy/mm/dd", JalaliDateFormatter.FORMAT_IN_PERSIAN);
    public static final JalaliDateFormatter per_yymmdd = new JalaliDateFormatter("yy/mm/dd", JalaliDateFormatter.FORMAT_IN_PERSIAN);
    public static final JalaliDateFormatter per_yyyyMdd = new JalaliDateFormatter("yyyy M dd", JalaliDateFormatter.FORMAT_IN_PERSIAN);
    public static final JalaliDateFormatter per_yyMdd = new JalaliDateFormatter("yy M dd", JalaliDateFormatter.FORMAT_IN_PERSIAN);
    public static String sQLStringToPersianSrting(String st, DateToStringFormat dformat, TimeToStringFormat tformat, String NullRes){
        if (st == null || st.length() ==0|| st.length() !=19)
            return NullRes;
        //input format : 2020-03-22T18:00:00
        int year = 0, month = 0, day = 0, hour = 0, min = 0, sec = 0;
        for (int i = 0;i < 4; i++) {year = (st.charAt(i) - '0') + year * 10;}
        for (int i = 5;i < 7; i++) {month = (st.charAt(i) - '0') + month * 10;}
        for (int i = 8;i < 10; i++) {day = (st.charAt(i) - '0') + day * 10;}
        for (int i = 11;i < 13; i++) {hour = (st.charAt(i) - '0') + hour * 10;}
        for (int i = 14;i < 16; i++) {min = (st.charAt(i) - '0') + min * 10;}
        for (int i = 17;i < 19; i++) {sec = (st.charAt(i) - '0') + sec * 10;}

        JalaliDate jalaliDate = dateConverter.gregorianToJalali(year, month, day);
        String res = "";
        switch (dformat){
            case yyyymmdd:
                res = jalaliDate.format(per_yyyymmdd);break;
            case yymmdd:
                res = jalaliDate.format(per_yymmdd);break;
            case yyyyMdd:
                res = jalaliDate.format(per_yyyyMdd);break;
            case yyMdd:
                res = jalaliDate.format(per_yyMdd);break;
        }
        if (tformat != TimeToStringFormat.None){
            res = (res.length() > 0? res + " ": "") + CalendarToTimeString(hour, min, sec, tformat, ":");
        }
        return res;
    }
    //Kar ba C# API - Calendar to String
//    public static String SQLStringFromCalendar(Calendar cal){SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");return format.format(cal.getTime());}
//---------------------End kar ba C# API---------------------------
    public static String CalendarToPersianDateString(Calendar ca, DateToStringFormat dts, String seperator){
        if (dts == DateToStringFormat.None)
            return "";
        JalaliDate jd = getJalaliDate(ca);
        if (dts == DateToStringFormat.yyyymmdd){
            int month = jd.getMonthPersian().getValue();
            int day = jd.getDay();

            return jd.getYear() + seperator+ (month < 10?"0":"") + month + seperator +(day < 10?"0":"") + day;
        }
        else if (dts == DateToStringFormat.yyyyMdd){
            int day = jd.getDay();
            return jd.getYear() + seperator +jd.getMonthPersian().getStringInPersian() + seperator +(day < 10?"0":"") + day;
        }
        else if (dts == DateToStringFormat.yymmdd){
            int month = jd.getMonthPersian().getValue();
            int day = jd.getDay();
            int year =jd.getYear() % 100;

            return (year < 10?"0":"") + year + seperator+ (month < 10?"0":"") + month + seperator +(day < 10?"0":"") + day;
        }
        else if (dts == DateToStringFormat.yyMdd){
            int day = jd.getDay();
            int year =jd.getYear() % 100;

            return (year < 10?"0":"") + year  + seperator+jd.getMonthPersian().name() + seperator +(day < 10?"0":"") + day;
        }
        return "";
    }
    public static String CalendarToTimeString(Calendar ca, TimeToStringFormat tts, String seperator){
        if (tts == TimeToStringFormat.None)
            return "";
        Integer hour = ca.get(Calendar.HOUR_OF_DAY);
        Integer min = ca.get(Calendar.MINUTE);
        Integer sec = ca.get(Calendar.SECOND);
        return CalendarToTimeString(hour, min, sec, tts, seperator);
    }
    public static String CalendarToTimeString(Integer hour, Integer min, Integer sec, TimeToStringFormat tts, String seperator) {
        if (tts == TimeToStringFormat.None)
            return "";
        String hStr = ((hour < 10)?"0":"") + hour.toString();
        String mStr = ((min < 10)?"0":"") + min.toString();
        String sStr = ((sec < 10)?"0":"") + sec.toString();

        if (tts == TimeToStringFormat.HourMin) {
            return hStr +seperator+ mStr;
        }
        return hStr + seperator + mStr + seperator + sStr;
    }
    public static String persianStringFromInt(int date, String emptyValue){
        if (date == 0)
            return emptyValue;
        int month = date % 10000 / 100;
        int day = date % 100;
        return Integer.toString(date / 10000) + "/"
                + (month < 10? "0":"") + Integer.toString(month) +"/"
                + (day < 10? "0":"") + Integer.toString(day);
    }
    public static int intFromPersianString(String date, int emptyValue){
        String[] arrOfStr = date.split("/");
        if (arrOfStr.length != 3)
            return emptyValue;
        return Integer.parseInt(arrOfStr[0]) * 10000 + Integer.parseInt(arrOfStr[1]) * 100 +Integer.parseInt(arrOfStr[2]) ;
    }
    public enum TimeToStringFormat{
        None,
        HourMin,
        HourMinSec
    }
    public enum DateToStringFormat{
        None,
        yyyymmdd,
        yymmdd,
        yyyyMdd,
        yyMdd
    }

}
//Link : https://github.com/eloyzone/jalali-calendar
// Create an object of DateConverter, its the main class that converts calendars
//DateConverter dateConverter = new DateConverter();
//
//    // Convert Jalali date to Gregorian
//    LocalDate localdate1 = dateConverter.jalaliToGregorian(1370, 11, 28);
//    LocalDate localdate2 = dateConverter.jalaliToGregorian(1386, MonthPersian.ESFAND, 29);
//
//    // Convert Gregorian date to Jalali
//    JalaliDate jalaliDate1 = dateConverter.gregorianToJalali(1992, 2, 17);
//    JalaliDate jalaliDate2 = dateConverter.gregorianToJalali(2019, 3, 20);
//
//    // checking for leapyer of Jalali Date
//    boolean leapyer1 = new JalaliDate(1370, 11, 28).isLeapYear());
//        boolean leapyer2 = dateConverter.gregorianToJalali(1992, 2, 17).isLeapYear();
//
//// Day of week
//        String dayOfWeek1 = new JalaliDate(1370, 11, 28).getDayOfWeek().getStringInPersian(); // Doshanbe
//        String dayOfWeek2 = new JalaliDate(1370, 11, 28).getDayOfWeek().getStringInEnglish(); // دوشنبه

//DateConverter dateConverter = new DateConverter();
//    JalaliDate jalaliDate = dateConverter.gregorianToJalali(1992, Month.FEBRUARY, 17);
//    String result = jalaliDate.format(new JalaliDateFormatter("yyyy/mm/dd", JalaliDateFormatter.FORMAT_IN_PERSIAN);
//// result will be: ١٣٧٠/١١/٢٨
//
//    String result2 = jalaliDate.format(new JalaliDateFormatter("yyyy- M dd", JalaliDateFormatter.FORMAT_IN_PERSIAN);
//// result2 will be: ٢٨ بهمن -١٣٧٠