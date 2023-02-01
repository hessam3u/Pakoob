package bo.entity;

import com.github.eloyzone.jalalicalendar.JalaliDate;
import com.github.eloyzone.jalalicalendar.JalaliDateFormatter;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Calendar;

import utils.MyDate;
import utils.TextFormat;

import static java.lang.Math.random;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;


@Entity(indices = {@Index("TTClubTourIdLocal")})
public class TTClubTour {
    @Nullable
    @PrimaryKey(autoGenerate = true)
    public long TTClubTourIdLocal;
    @Nullable
    @SerializedName(value = "a", alternate = {"ttClubTourId"})
    public long TTClubTourId;
    @Nullable
    @SerializedName(value = "b", alternate = {"tTClubNameId"})
    public Integer TTClubNameId;
    @Nullable
    @SerializedName(value = "c", alternate = {"extClubTourId"})
    public Long ExtClubTourId;
    @Nullable
    @SerializedName(value = "d", alternate = {"name"})
    public String Name;
    @Nullable
    @SerializedName(value = "e", alternate = {"placeOfTour"})
    public String PlaceOfTour;
    @Nullable
    @SerializedName(value = "f", alternate = {"startDate"})
    public String StartDate;
    @Nullable
    @SerializedName(value = "g", alternate = {"endDate"})
    public String EndDate;
    @Nullable
    @SerializedName(value = "h", alternate = {"regStartDate"})
    public String RegStartDate;
    @Nullable
    @SerializedName(value = "i", alternate = {"regEndDate"})
    public String RegEndDate;
    @Nullable
    @SerializedName(value = "j", alternate = {"tourFinalPrice"})
    public Double TourFinalPrice;
    @Nullable
    @SerializedName(value = "k", alternate = {"clubTourCategoryId"})
    public Integer ClubTourCategoryId;
    @Nullable
    @SerializedName(value = "l", alternate = {"tourLength"})
    public Double TourLength;
    @Nullable
    @SerializedName(value = "m", alternate = {"tourLengthUnit"})
    public Byte TourLengthUnit;
    @Nullable
    @SerializedName(value = "n", alternate = {"cCustomerIdLeader"})
    public Integer CCustomerIdLeader;
    @Nullable
    @SerializedName(value = "o", alternate = {"cityId"})
    public Integer CityId;
    @Nullable
    @SerializedName(value = "p", alternate = {"imageLink"})
    public String ImageLink;
    @Nullable
    @SerializedName(value = "q", alternate = {"openType"})
    public Byte OpenType;
    @Nullable
    @SerializedName(value = "r", alternate = {"status"})
    public Byte Status;
    @Nullable
    @SerializedName(value = "s", alternate = {"showInPublic"})
    public Byte ShowInPublic;
    @Nullable
    @SerializedName(value = "t", alternate = {"regDesc"})
    public String RegDesc;
    @Nullable
    @SerializedName(value = "u", alternate = {"tourHadnesssLevel"})
    public Integer TourHadnesssLevel;
    @Nullable
    @SerializedName(value = "v", alternate = {"desc_Short"})
    public String Desc_Short;
    @Nullable
    @SerializedName(value = "w", alternate = {"timeTable"})
    public String TimeTable;
    @Nullable
    @SerializedName(value = "x", alternate = {"beginLocation"})
    public String BeginLocation;
    @Nullable
    @SerializedName(value = "y", alternate = {"prerequisites"})
    public String Prerequisites;
    @Nullable
    @SerializedName(value = "z", alternate = {"necessaryTools"})
    public String NecessaryTools;
    @Nullable
    @SerializedName(value = "aa", alternate = {"specialProperty"})
    public String SpecialProperty;
    @Nullable
    @SerializedName(value = "ab", alternate = {"participantLimit"})
    public Integer ParticipantLimit;
    @Nullable
    @SerializedName(value = "ac", alternate = {"allowOverflowSignUp"})
    public Byte AllowOverflowSignUp;
    @Nullable
    @SerializedName(value = "ad", alternate = {"participantCount"})
    public Integer ParticipantCount;
    @Nullable
    @SerializedName(value = "ae", alternate = {"extRegLink"})
    public String ExtRegLink;
    @Nullable
    @SerializedName(value = "af", alternate = {"publishDate"})
    public String PublishDate;
    @Nullable
    @SerializedName(value = "ag", alternate = {"siteId"})
    public Integer SiteId; // It is CacheDbId in Sqlite
    @Ignore
    @SerializedName(value = "ah", alternate = {"recInsDate"})
    public String RecInsDate;
    @Ignore
    @SerializedName(value = "ai", alternate = {"insPersonId"})
    public Integer InsPersonId;
    @Ignore
    @SerializedName(value = "aj", alternate = {"updPersonId"})
    public Integer UpdPersonId;
    @Ignore
    @SerializedName(value = "ak", alternate = {"recUpdateDate"})
    public String RecUpdateDate;
    @Ignore
    @SerializedName(value = "al", alternate = {"updateStatus"})
    public Byte UpdateStatus;

    public Calendar getStartDate() {
        return MyDate.CalendarFromCSharpSQLString(this.StartDate);
    }

    //public String getStartDateView(){return MyDate.sQLStringToPersianSrting(this.StartDate, MyDate.DateToStringFormat.yyyymmdd, MyDate.TimeToStringFormat.None, "");}
    public void setStartDate(Calendar value) {
        this.StartDate = MyDate.CalendarToCSharpSQLString(value);
    }

    public Calendar getEndDate() {
        return MyDate.CalendarFromCSharpSQLString(this.EndDate);
    }

    public String getEndDateView() {
        return MyDate.sQLStringToPersianSrting(this.EndDate, MyDate.DateToStringFormat.yyyymmdd, MyDate.TimeToStringFormat.None, "");
    }

    public void setEndDate(Calendar value) {
        this.EndDate = MyDate.CalendarToCSharpSQLString(value);
    }

    public Calendar getRegStartDate() {
        return MyDate.CalendarFromCSharpSQLString(this.RegStartDate);
    }

    public String getRegStartDateView() {
        return MyDate.sQLStringToPersianSrting(this.RegStartDate, MyDate.DateToStringFormat.yyyymmdd, MyDate.TimeToStringFormat.None, "");
    }

    public void setRegStartDate(Calendar value) {
        this.RegStartDate = MyDate.CalendarToCSharpSQLString(value);
    }

    public Calendar getRegEndDate() {
        return MyDate.CalendarFromCSharpSQLString(this.RegEndDate);
    }

    //public String getRegEndDateView(){return MyDate.sQLStringToPersianSrting(this.RegEndDate, MyDate.DateToStringFormat.yyyymmdd, MyDate.TimeToStringFormat.None, "");}
    public void setRegEndDate(Calendar value) {
        this.RegEndDate = MyDate.CalendarToCSharpSQLString(value);
    }

    public Calendar getRecInsDate() {
        return MyDate.CalendarFromCSharpSQLString(this.RecInsDate);
    }

    public String getRecInsDateView() {
        return MyDate.sQLStringToPersianSrting(this.RecInsDate, MyDate.DateToStringFormat.yyyymmdd, MyDate.TimeToStringFormat.None, "");
    }

    public void setRecInsDate(Calendar value) {
        this.RecInsDate = MyDate.CalendarToCSharpSQLString(value);
    }

    public Calendar getRecUpdateDate() {
        return MyDate.CalendarFromCSharpSQLString(this.RecUpdateDate);
    }

    public String getRecUpdateDateView() {
        return MyDate.sQLStringToPersianSrting(this.RecUpdateDate, MyDate.DateToStringFormat.yyyymmdd, MyDate.TimeToStringFormat.None, "");
    }

    public void setRecUpdateDate(Calendar value) {
        this.RecUpdateDate = MyDate.CalendarToCSharpSQLString(value);
    }


    @SerializedName(value = "am", alternate = {"leaderCustomerIdFullName"})
    public String LeaderCustomerIdFullName;
    @SerializedName(value = "an", alternate = {"cityName"})
    public String CityName;
    @SerializedName(value = "ao", alternate = {"clubTourCategoryIdView"})
    public String ClubTourCategoryIdView;
    @SerializedName(value = "ap", alternate = {"clubName"})
    public String ClubName;
    @SerializedName(value = "aq", alternate = {"websiteAddress"})
    public String WebsiteAddress;
    @SerializedName(value = "ar", alternate = {"urlProtocol"})
    public String UrlProtocol;

    public TTClubTour() {
    }

    public String getName() {
        return Name;
    }

    public void setName(String _Name) {
        Name = _Name;
    }

    public String getStartDateView() {
        boolean hasStart = false;
        JalaliDate startJalali = MyDate.getJalaliDate(this.getStartDate());
        if (this.getStartDate() != MyDate.SqlMinDateTime && this.getStartDate() != MyDate.SqlMaxDateTime) {
            hasStart = true;
        }
        boolean isInOneDay = false;
        if (this.TourLength <= 1) {
            isInOneDay = true;
        }
        return TextFormat.ReplaceEnglishNumbersWithPersianOne(
                (hasStart ? ((isInOneDay ? " در " : " از ") +
                        //1400-04-19 Not working in Old Androids:
                        //startJalali.format(new JalaliDateFormatter("M dd", JalaliDateFormatter.FORMAT_IN_PERSIAN))
                        //1400-04-19 I replaced above with this
                        (startJalali.getDay() + " " + startJalali.getMonthPersian().getStringInPersian())
                ) : "")
        );
    }

    public String getStartDateAndTimeView() {
        boolean hasStart = false;
        boolean hasEnd = false;

        Calendar startDate = this.getStartDate();
        Calendar endDate = this.getEndDate();

        JalaliDate startJalali = MyDate.getJalaliDate(startDate);
        JalaliDate endJalali = MyDate.getJalaliDate(endDate);

        if (startDate != MyDate.SqlMinDateTime && this.getStartDate() != MyDate.SqlMaxDateTime) {
            hasStart = true;
        }
        if (this.getEndDate() != MyDate.SqlMinDateTime && endDate != MyDate.SqlMaxDateTime) {
            hasEnd = true;
        }
        boolean isInOneDay = false;
        if (this.TourLength <= 1) {
            isInOneDay = true;
        }
        String startWeekDay = MyDate.getDayOfWeek(startJalali);//MyDate.PersianDayOfWeeks[(((byte)pc.GetDayOfWeek(this.StartDate)-1)+7)%7];

        return TextFormat.ReplaceEnglishNumbersWithPersianOne(
                (hasStart ? ((isInOneDay ? " در " : " از ") +
                        startWeekDay + " " +
                        //1400-04-19 Not working in Old Androids:
                        //startJalali.format(new JalaliDateFormatter("M dd", JalaliDateFormatter.FORMAT_IN_PERSIAN))
                        (startJalali.getDay() + " " + startJalali.getMonthPersian().getStringInPersian())
                ) : "") + " ساعت " + MyDate.CalendarToTimeString(startDate, MyDate.TimeToStringFormat.HourMin, ":")
        );
    }

    public String getRegEndDateView() {
        Calendar value = getRegEndDate();
        if (value == MyDate.SqlMinDateTime) {
            return "مشخص نشده";
        } else {
            JalaliDate jd =MyDate.getJalaliDate(value);
            String res = (jd.getDay() + " " + jd.getMonthPersian().getStringInPersian());//.format(new JalaliDateFormatter("M dd", JalaliDateFormatter.FORMAT_IN_PERSIAN));
            return TextFormat.ReplaceEnglishNumbersWithPersianOne(res + " ساعت " + MyDate.CalendarToTimeString(value, MyDate.TimeToStringFormat.HourMin, ":"));
        }
    }

    public String getTourFinalPriceView() {
        return TextFormat.ReplaceEnglishNumbersWithPersianOne(TextFormat.GetStringFromDecimalPrice(TourFinalPrice));
    }

    public String getTourLenghtView() {
        String res = TourLength.toString();
        if (TourLength == 0.5)
            res = "نیم";
        else if (TourLength == 1)
            res = "یک";
        else if (TourLength == 1.5)
            res = "یک و نیم";
        else if (TourLength == 2)
            res = "دو";
        else if (TourLength == 2.5)
            res = "دو و نیم";
        else if (TourLength == 3)
            res = "سه";
        else if (TourLength == 3.5)
            res = "سه و نیم";
        else if (TourLength == 4)
            res = "چهار";
        else if (TourLength == 4.5)
            res = "چهار و نیم";
        else if (TourLength == 5)
            res = "پنج";
        else if (TourLength == 5.5)
            res = "پنج و نیم";
        else if (TourLength == 6)
            res = "شش";
        else if (TourLength == 6.5)
            res = "شش و نیم";
        else if (TourLength == 7)
            res = "هفت";
        else if (TourLength == 7.5)
            res = "هفت و نیم";
        else if (TourLength == 8)
            res = "هشت";
        else if (TourLength == 8.5)
            res = "هشت و نیم";
        else if (TourLength == 9)
            res = "نه";
        else if (TourLength == 9.5)
            res = "نه و نیم";
        else if (TourLength == 10)
            res = "ده";
        else if (TourLength == 10.5)
            res = "ده و نیم";
        else if (TourLength == 11)
            res = "یازده";
        else if (TourLength == 11.5)
            res = "ده و یازده";
        return TextFormat.ReplaceEnglishNumbersWithPersianOne(res + " " + TourLengthUnitNamesView[TourLengthUnit]);
    }

    public static String[] TourLengthUnitNamesView = new String[]{
            "-",
            "روزه"
            , "هفته"
            , "ساله"
            , "جلسه"
            , "ساعته"
            , "دقیقه"
            , "نیم روز"
    };


    public static ArrayList<TTClubTour> createSampleList(int numContacts) {
        ArrayList<TTClubTour> contacts = new ArrayList<TTClubTour>();

        for (int i = 1; i <= numContacts; i++) {
            TTClubTour tt = new TTClubTour();
            tt.setName("Tour " + random());
            contacts.add(tt);
        }

        return contacts;
    }

    public static final int OpenTypes_OpenInPortal = 1;
    public static final int OpenTypes_SpecialLink = 2; // in ExtRegLink
    public static final int OpenTypes_OpenDesc = 3; // in Desc_Short

}
