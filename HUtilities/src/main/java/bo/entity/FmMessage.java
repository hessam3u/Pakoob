package bo.entity;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import utils.MyDate;

@Entity(indices = {@Index("FmMessageId")})
public class FmMessage {
    @Nullable
    @PrimaryKey()
    @SerializedName(value="id", alternate = {"fmMessageId", "FmMessageId"})
    public Long FmMessageId;
    @Nullable
    @SerializedName(value="se", alternate = {"cCustomerIdSend", "CCustomerIdSend"})
    public Long CCustomerIdSend;
    @Nullable
    @SerializedName(value="re", alternate = {"recieverId", "RecieverId"})
    public Long RecieverId;
    @Nullable
    @SerializedName(value="at")
    public Byte AnonymosType;
    @SerializedName(value="cn", alternate = {"cCustomerNameSend", "CCustomerNameSend"})
    public String CCustomerNameSend;
    @Ignore
    @SerializedName(value="ft", alternate = {"forecedTitle", "ForecedTitle"})
    public String ForecedTitle;
//    @SerializedName(value="tn", alternate = {"topicName", "TopicName"})
//    public String TopicName;
    @Nullable
    @SerializedName(value="rt", alternate = {"recType", "RecType"})
    public Byte RecType;
    @Nullable
    @SerializedName(value="t", alternate = {"fmMessageType", "FmMessageType"})
    public Byte FmMessageType;
    @Nullable
    @SerializedName(value="o", alternate = {"openAction", "OpenAction"})
    public Byte OpenAction;
    @Nullable
    @SerializedName(value="a", alternate = {"actionParam", "ActionParam"})
    public String ActionParam;
    @Nullable
    @SerializedName(value="s", alternate = {"sendDateSqlStr", "SendDateSqlStr"})
    public String SendDate;
    @Nullable
    @SerializedName(value="te", alternate = {"text1", "Text1"})
    public String Text1;
    @Nullable
    @SerializedName(value="ht", alternate = {"hasTextAttach", "HasTextAttach"})
    public Byte HasTextAttach;
    @Nullable
    @SerializedName(value="ha", alternate = {"hasAttach", "HasAttach"})
    public Byte HasAttach;
    @Nullable
    @SerializedName(value="st", alternate = {"status","Status"})
    public Byte Status;
    @Nullable
    @SerializedName(value="r", alternate = {"replyId", "ReplyId"})
    public Long ReplyId;
    @Nullable
    @SerializedName(value="f", alternate = {"fwdId", "FwdId"})
    public Long FwdId;
    @Nullable
    @SerializedName(value="e", alternate = {"editDateSqlStr", "EditDateSqlStr"})
    public String EditDate;
    @Nullable
    @SerializedName(value="c", alternate = {"contentType", "ContentType"})
    public Byte ContentType;
    @Nullable
    @SerializedName(value="cc", alternate = {"contentCat", "ContentCat"})
    public Byte ContentCat;
    @Nullable
    @SerializedName(value="p", alternate = {"alarmPriority", "AlarmPriority"})
    public Byte AlarmPriority;
    @Nullable
    @SerializedName(value="em", alternate = {"extMessageId", "ExtMessageId"})
    public Long ExtMessageId;

    public Calendar getSendDate(){return MyDate.CalendarFromCSharpSQLString(this.SendDate);}
    public String getSendDateView(){return MyDate.sQLStringToPersianSrting(this.SendDate, MyDate.DateToStringFormat.yyyymmdd, MyDate.TimeToStringFormat.None, "");}
    public void setSendDate(Calendar value){this.SendDate = MyDate.CalendarToCSharpSQLString(value);}

    public FmMessage() {
    }
    public FmMessage(JSONObject json) throws JSONException {

        JSONObject data = json.getJSONObject("data");
        String tmp = "";

        this.Text1 = data.has("te")? data.getString("te"):"";
        this.ForecedTitle = data.has("ft")? data.getString("ft"):"";
        this.ActionParam = data.has("a")?data.getString("a"):"";
        this.AnonymosType = (byte)(data.has("at")?data.getInt("at"):0);
        this.RecieverId = (data.has("re")?data.getLong("re"):0);
        tmp = data.has("se")?data.getString("se"):"";
        this.CCustomerIdSend = tmp!= ""? Long.parseLong(tmp):0;
//        this.CCustomerNameSend = data.has("cn")?data.getString("cn"):"";
        tmp = data.has("cc")?data.getString("cc"):"";
        this.ContentCat =tmp!= ""? Byte.parseByte(tmp):0;
        tmp = data.has("c")?data.getString("c"):"";
        this.ContentType =tmp!= ""? Byte.parseByte(tmp):0;
        this.EditDate=data.has("e")?data.getString("e"):"";
        tmp = data.has("id")?data.getString("id"):"";
        this.FmMessageId=tmp!= ""? Long.parseLong(tmp):0;
        tmp = data.has("t")?data.getString("t"):"";
        this.FmMessageType=tmp!= ""? Byte.parseByte(tmp):0;
        tmp = data.has("rt")?data.getString("rt"):"";
        this.RecType=tmp!= ""? Byte.parseByte(tmp):0;
        tmp = data.has("f")?data.getString("f"):"";
        this.FwdId=tmp!= ""? Long.parseLong(tmp):0;
        tmp = data.has("ha")?data.getString("ha"):"";
        this.HasAttach =tmp!= ""? Byte.parseByte(tmp):0;
        tmp = data.has("ht")?data.getString("ht"):"";
        this.HasTextAttach=tmp!= ""? Byte.parseByte(tmp):0;
        tmp = data.has("o")?data.getString("o"):"";
        this.OpenAction =tmp!= ""? Byte.parseByte(tmp):0;
        tmp = data.has("r")?data.getString("r"):"";
        this.ReplyId=tmp!= ""? Long.parseLong(tmp):0;
        this.SendDate = data.has("s")?data.getString("s"):"";
        tmp = data.has("st")?data.getString("st"):"";
        this.Status=tmp!= ""? Byte.parseByte(tmp):0;
//        this.TopicName = data.has("tn")?data.getString("tn"):"";
        tmp = data.has("p")?data.getString("p"):"";
        this.AlarmPriority=tmp!= ""? Byte.parseByte(tmp):0;
    }

    public static final int FmMessageType_None = 0;
    public static final int FmMessageType_User = 1;
    public static final int FmMessageType_Group = 2;
    public static final int FmMessageType_Chanal = 3;
    public static final int FmMessageType_Service_Personal = 4;
    public static final int FmMessageType_Service_Group = 5;
    public static final int FmMessageType_System = 6;


    public static final byte RecTypes_None = 0;
    public static final byte RecTypes_کاربرخاص = 1;
    public static final byte RecTypes_کانال_خاص = 2;

    public static final byte OpenAction_None = 1;
    public static final byte OpenAction_NormalMessage = 2;
    public static final byte OpenAction_OpenInApp =3;
    public static final byte OpenAction_OpenLink = 4;
    public static final byte OpenAction_OpenInApp_OnClick =5;
    public static final byte OpenAction_OpenLink_OnClick = 6;
}
