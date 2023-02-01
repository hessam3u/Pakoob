package utils;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.Locale;

public class TextFormat {
    public static char[] InvalidNormalTextChars = new char[] { '[', ']', '\'', '*', '&', '^', '%', '$', '#', '@', '!', '~', '/', '\\', '<', '>', '|', '?' };
    public static String ReplaceInvalidPersianChars(String PersianString)
    {
        String res = PersianString;
        res = res.replace('ي', 'ی');
        res = res.replace('ك', 'ک');
        return res;
    }
    public static String ReplacePersianNumbersWithEnglishOne(String PersianString)
    {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < PersianString.length(); i++)
        {
            if (PersianString.charAt(i) == '٠'|| PersianString.charAt(i) == '۰')
            {
                res.append('0');
            }
            else if (PersianString.charAt(i) == '١' || PersianString.charAt(i) == '۱')
            {
                res.append('1');
            }
            else if (PersianString.charAt(i) == '٢' || PersianString.charAt(i) == '۲')
            {
                res.append('2');
            }
            else if (PersianString.charAt(i) == '٣' || PersianString.charAt(i) == '۳')
            {
                res.append('3');
            }
            else if (PersianString.charAt(i) == '٤' || PersianString.charAt(i) == '۴')
            {
                res.append('4');
            }
            else if (PersianString.charAt(i) == '٥' || PersianString.charAt(i) == '۵')
            {
                res.append('5');
            }
            else if (PersianString.charAt(i) == '٦' || PersianString.charAt(i) == '۶')
            {
                res.append('6');
            }
            else if (PersianString.charAt(i) == '٧' || PersianString.charAt(i) == '۷')
            {
                res.append('7');
            }
            else if (PersianString.charAt(i) == '٨' || PersianString.charAt(i) == '۸')
            {
                res.append('8');
            }
            else if (PersianString.charAt(i) == '٩' || PersianString.charAt(i) == '۹')
            {
                res.append('9');
            }
            else
            {
                res.append(PersianString.charAt(i));
            }
        }
        //PersianString = PersianString.replace("٠", "0").replace("١", "1").replace("٢", "2").replace("٣", "3").replace("٤", "4").replace("٥", "5").replace("٦", "6").replace("٧", "7").replace("٨", "8").replace("٩", "9");
        //PersianString = PersianString.replace("۰", "0").replace("۱", "1").replace("۲", "2").replace("۳", "3").replace("۴", "4").replace("۵", "5").replace("۶", "6").replace("۷", "7").replace("۸", "8").replace("۹", "9");

        return res.toString();
    }
    public static String ReplaceEnglishNumbersWithPersianOne(String PersianString)
    {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < PersianString.length(); i++)
        {
            if (PersianString.charAt(i) == '0')
            {
                res.append('٠');
            }
            else if (PersianString.charAt(i) == '1')
            {
                res.append('١');
            }
            else if (PersianString.charAt(i) == '2' )
            {
                res.append('٢');
            }
            else if (PersianString.charAt(i) == '3')
            {
                res.append('٣');
            }
            else if (PersianString.charAt(i) == '4')
            {
                res.append('٤');
            }
            else if (PersianString.charAt(i) == '5' )
            {
                res.append('۵');
            }
            else if ( PersianString.charAt(i) == '6')
            {
                res.append('۶');
            }
            else if (PersianString.charAt(i) == '7')
            {
                res.append('٧');
            }
            else if (PersianString.charAt(i) == '8')
            {
                res.append('٨');
            }
            else if (PersianString.charAt(i) == '9')
            {
                res.append('٩');
            }
            else if (PersianString.charAt(i) == ',')
            {
                res.append(',');
            }
            else
            {
                res.append(PersianString.charAt(i));
            }
        }
        //PersianString = PersianString.replace("٠", "0").replace("١", "1").replace("٢", "2").replace("٣", "3").replace("٤", "4").replace("٥", "5").replace("٦", "6").replace("٧", "7").replace("٨", "8").replace("٩", "9");
        //PersianString = PersianString.replace("۰", "0").replace("۱", "1").replace("۲", "2").replace("۳", "3").replace("۴", "4").replace("۵", "5").replace("۶", "6").replace("۷", "7").replace("۸", "8").replace("۹", "9");

        return res.toString();
    }
    public static String GetStringFromDecimalPrice(Double value)
    {
        return GetStringFromDecimalPrice(value, false);
    }
    public static String GetStringFromDecimalPrice(Double value, boolean VirgooliFloatingPoints)
    {
        return GetStringFromDecimalPrice(value, VirgooliFloatingPoints, "-");
    }
    public static String GetStringFromDecimalPrice(double value, String NegativeSign)
    {
        return GetStringFromDecimalPrice(value, false, NegativeSign);
    }
    public static String GetStringFromDecimalPrice(double value, boolean VirgooliFloatingPoints, String NegativeSign)
    {
        if (value == 0)
            return "0";
        long iPart = (long) value;
        Double fPart = value - iPart;
        String iPartStr = NumberFormat.getNumberInstance(Locale.US).format(value);
        if (fPart == 0) {
            return iPartStr;
        }
        String res =iPartStr + "." + fPart.toString();
        if (value > 0) {
            return res;
        }

        if (value < 0 && NegativeSign == "(")
        {
            res = res.replace("-", "");
            res = "(" + res + ")";
        }
        return res;
    }
    public static Double GetDecimalFromString(String text, Double NullValue)
    {
        return tryParseDouble(text, NullValue);
    }

    public static int tryParseInt(String value, int defaultVal) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }
    public static Double tryParseDouble(String value, Double defaultVal) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    public static String AddVirgoolForPersianMoney(String st, boolean VirgooliFloatingValues)
    {
        //-----------
        String res = "";
        String beforePointPart = "";
        String afterPointPart = "";
        int pointPartIndex = st.indexOf('.');
        beforePointPart = st.substring(0, (pointPartIndex == -1 ? st.length() : pointPartIndex));
        if (pointPartIndex >= 0)
            afterPointPart = st.substring(pointPartIndex + 1);

        //-----------------------------------------
        //قسمت اصلی اجرای عملیات برای بخش صحیح
        //-----------------------------------------
        beforePointPart = Reverse(beforePointPart);
        for (int i = 0; i < beforePointPart.length(); i++)
        {
            if (i % 3 == 0 && i != 0)
            {
                res += ',';
            }
            res += beforePointPart.charAt(i);
        }
        beforePointPart = Reverse(res);
        //-----------------------------------------
        //قسمت اصلی اجرای عملیات برای بخش اعشار
        //-----------------------------------------
        if (!VirgooliFloatingValues)
            res = afterPointPart;
        else
            res = "";
        for (int i = 0; i < afterPointPart.length() && VirgooliFloatingValues; i++)
        {
            if (i % 3 == 0 && i != 0)
            {
                res += ',';
            }
            res += afterPointPart.charAt(i);
        }
        afterPointPart = res;
        //-----------------------------------------
        //اگه نقطه وجود داشت اما هیچی بعدش تایپ نشده بود، یعنی کاربر در حال تایپ قسمت اعشاری هست، پس باید بهش اجازه بدیم تیاپ رو ادامه بده
        if (pointPartIndex >= 0 && afterPointPart.length() == 0)
            return beforePointPart + ".";
        else if (beforePointPart.length() >= 0 && afterPointPart.length() > 0)
            return beforePointPart + "." + afterPointPart;
        else
            return beforePointPart;
    }
    public static String Reverse(String str)
    {
        int len = str.length();
        char[] arr = new char[len];

        for (int i = 0; i < len; i++)
        {
            arr[i] = str.charAt(len - 1 - i);
        }

        return new String(arr);
    }

    public static String ListToString(ArrayList<Integer> list, String delim, String emptyResult){
        String res = emptyResult;
        for (int i = 0; i < list.size(); i++)
            res += (res.length() > 0?delim:"") + list.get(i);
        return res;
    }
    public static String ListToString(ArrayList<Integer> list, String delim){
        return ListToString(list, delim, "");
    }
}
