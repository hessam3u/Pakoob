package bo.entity;

import com.google.gson.annotations.SerializedName;

import java.util.Calendar;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import utils.MyDate;

@Entity(indices = {@Index("IdLocal")})
public class FmSides {
    //Added Fields:
    @Nullable
    @PrimaryKey()
    @SerializedName(value="i")
    public Long IdLocal;
    @Nullable
    @SerializedName(value="n")
    public String Name;
    @Nullable
    @SerializedName(value="a")
    public String Avatar;
    @Nullable
    @SerializedName(value="u")
    public String Username;
    @Nullable
    @SerializedName(value="t")
    public Byte UserType;

    //ORGINAL FIELDS:
    @Nullable
    @SerializedName(value="FmSidesId", alternate = {"fmSidesId"})
    public long FmSidesId;
    @Nullable
    @SerializedName(value="UserSide1", alternate = {"userSide1"})
    public long UserSide1;
    @Nullable
    @SerializedName(value="UserSide2", alternate = {"userSide2"})
    public long UserSide2;
    @Nullable
    @SerializedName(value="FmChanalId", alternate = {"fmChanalId"})
    public long FmChanalId;
    @Nullable
    @SerializedName(value="AnonymosType", alternate = {"anonymosType"})
    public byte AnonymosType;
    @Nullable
    @SerializedName(value="FmMessageIdLast", alternate = {"fmMessageIdLast"})
    public long FmMessageIdLast;
    @Nullable
    @SerializedName(value="Text1", alternate = {"text1"})
    public String Text1;
    @Nullable
    @SerializedName(value="LastDate", alternate = {"lastDate"})
    public String LastDate;
    @Nullable
    @SerializedName(value="LastSenderSide", alternate = {"lastSenderSide"})
    public byte LastSenderSide;
    @Nullable
    @SerializedName(value="UnreadCount", alternate = {"unreadCount"})
    public int UnreadCount;
    @Nullable
    @SerializedName(value="LastContentType", alternate = {"lastContentType"})
    public byte LastContentType;
//    @SerializedName(value="SiteId", alternate = {"siteId"})
//    public int SiteId;
    public Calendar getLastDate(){return MyDate.CalendarFromCSharpSQLString(this.LastDate);}
    public String getLastDateView(){return MyDate.sQLStringToPersianSrting(this.LastDate, MyDate.DateToStringFormat.yyyymmdd, MyDate.TimeToStringFormat.None, "");}
    public void setLastDate(Calendar value){this.LastDate = MyDate.CalendarToCSharpSQLString(value);}


    public FmSides() {
    }


}
