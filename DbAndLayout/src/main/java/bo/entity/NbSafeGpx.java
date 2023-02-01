package bo.entity;

import androidx.room.Ignore;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.Calendar;
import utils.MyDate;
import utils.TextFormat;
import utils.hutilities;

public class NbSafeGpx
{
    @SerializedName(value="NbSafeGPXId", alternate = {"nbSafeGPXId"})
    public int NbSafeGPXId;
    @SerializedName(value="Name", alternate = {"name"})
    public String Name;
    @SerializedName(value="ImageAddress", alternate = {"imageAddress"})
    public String ImageAddress;
    @SerializedName(value="ScreenshotAddress", alternate = {"screenshotAddress"})
    public String ScreenshotAddress;
    @SerializedName(value="Tag", alternate = {"tag"})
    public String Tag;
    @SerializedName(value="FileAddress1", alternate = {"fileAddress1"})
    public String FileAddress1;
    @SerializedName(value="FileAddress2", alternate = {"fileAddress2"})
    public String FileAddress2;
    @SerializedName(value="FileAddress3", alternate = {"fileAddress3"})
    public String FileAddress3;
    @SerializedName(value="FIleVersion", alternate = {"fIleVersion"})
    public int FIleVersion;
    @SerializedName(value="FileVersionDate", alternate = {"fileVersionDate"})
    public String FileVersionDate;
    @SerializedName(value="PrevNbSafeGPXId", alternate = {"prevNbSafeGPXId"})
    public int PrevNbSafeGPXId;
    @SerializedName(value="LatN", alternate = {"latN"})
    public double LatN;
    @SerializedName(value="LatS", alternate = {"latS"})
    public double LatS;
    @SerializedName(value="LonE", alternate = {"lonE"})
    public double LonE;
    @SerializedName(value="LonW", alternate = {"lonW"})
    public double LonW;
    @SerializedName(value="PriceMode", alternate = {"priceMode"})
    public byte PriceMode;
    @SerializedName(value="Price", alternate = {"price"})
    public double Price;
    @SerializedName(value="NonDiscountPrice", alternate = {"nonDiscountPrice"})
    public double NonDiscountPrice;
    @SerializedName(value="Status", alternate = {"status"})
    public byte Status;
    @SerializedName(value="AcServiceId", alternate = {"acServiceId"})
    public long AcServiceId;
    @SerializedName(value="Validity", alternate = {"validity"})
    public byte Validity;
    @SerializedName(value="WriterFullName", alternate = {"writerFullName"})
    public String WriterFullName;
    @SerializedName(value="CCustomerId_Writer", alternate = {"cCustomerId_Writer"})
    public int CCustomerId_Writer;
    @SerializedName(value="GPXEditor", alternate = {"gPXEditor"})
    public String GPXEditor;
    @SerializedName(value="CCustomerId_GPXEditor", alternate = {"cCustomerId_GPXEditor"})
    public int CCustomerId_GPXEditor;
    @SerializedName(value="Desc", alternate = {"desc"})
    public String Desc;
    @SerializedName(value="VoteAvg", alternate = {"voteAvg"})
    public double VoteAvg;
    @SerializedName(value="VoteCount", alternate = {"voteCount"})
    public int VoteCount;
    @SerializedName(value="DownloadCount", alternate = {"downloadCount"})
    public int DownloadCount;
    @SerializedName(value="BuyCount", alternate = {"buyCount"})
    public int BuyCount;
    @SerializedName(value="HardnessLevel", alternate = {"hardnessLevel"})
    public byte HardnessLevel;
    @SerializedName(value="ActivityType", alternate = {"activityType"})
    public byte ActivityType;
    @SerializedName(value="ImportantNotes", alternate = {"importantNotes"})
    public String ImportantNotes;
    @SerializedName(value="TrackLength", alternate = {"trackLength"})
    public int TrackLength;
    @SerializedName(value="Priority", alternate = {"priority"})
    public int Priority;
    @SerializedName(value="ValidityDesc", alternate = {"validityDesc"})
    public String ValidityDesc;
    @SerializedName(value="ContentType1", alternate = {"contentType1"})
    public byte ContentType1;
    @SerializedName(value="ContentType2", alternate = {"contentType2"})
    public byte ContentType2;
    @SerializedName(value="ContentType3", alternate = {"contentType3"})
    public byte ContentType3;
    public Calendar getFileVersionDate(){return MyDate.CalendarFromCSharpSQLString(this.FileVersionDate);}
    public String getFileVersionDateView(){return MyDate.sQLStringToPersianSrting(this.FileVersionDate, MyDate.DateToStringFormat.yyyymmdd, MyDate.TimeToStringFormat.None, "");}
    public void setFileVersionDate(Calendar value){this.FileVersionDate = MyDate.CalendarToCSharpSQLString(value);}


    @SerializedName(value="RequestStatus", alternate = {"requestStatus", "rs"})
    public Byte RequestStatus;
    @SerializedName(value="BuyStatus", alternate = {"buyStatus", "bs"})
    public Byte BuyStatus;
    @SerializedName(value="BuyDesc", alternate = {"buyDesc", "bd"})
    public String BuyDesc;
    @Ignore
    @SerializedName(value="Expired", alternate = {"expired", "ex"})
    public byte Expired;
    @Ignore
    @SerializedName(value="ExpireDate", alternate = {"expireDate", "ed"})
    public String ExpireDate;

    @Ignore
    @SerializedName(value="imf")
    public byte IsMapOrGpxOrOther;

    public NbSafeGpx() {
    }

    public static NbSafeGpx fromBytes(byte[] bts){
        Gson gson = new Gson();
        String json = hutilities.decryptBytesToString(bts);
        return gson.fromJson(json, NbSafeGpx.class);
    }
    public String toString(){
        return "NbSafeGPXId :"+Integer.toString(NbSafeGPXId)+", Name :"+Name+", ImageAddress :"+ImageAddress+", ScreenshotAddress :"+ScreenshotAddress+", Tag :"+Tag+", FileAddress1 :"+FileAddress1+", FileAddress2 :"+FileAddress2+", FileAddress3 :"+FileAddress3+", FIleVersion :"+Integer.toString(FIleVersion)+", FileVersionDate :"+FileVersionDate+", PrevNbSafeGPXId :"+Integer.toString(PrevNbSafeGPXId)+", LatN :"+Double.toString(LatN)+", LatS :"+Double.toString(LatS)+", LonE :"+Double.toString(LonE)+", LonW :"+Double.toString(LonW)+", PriceMode :"+Byte.toString(PriceMode)+", Price :"+Double.toString(Price)+", NonDiscountPrice :"+Double.toString(NonDiscountPrice)+", Status :"+Byte.toString(Status)+", AcServiceId :"+Long.toString(AcServiceId)+", Validity :"+Byte.toString(Validity)+", WriterFullName :"+WriterFullName+", CCustomerId_Writer :"+Integer.toString(CCustomerId_Writer)+", GPXEditor :"+GPXEditor+", CCustomerId_GPXEditor :"+Integer.toString(CCustomerId_GPXEditor)+", Desc :"+Desc+", VoteAvg :"+Double.toString(VoteAvg)+", VoteCount :"+Integer.toString(VoteCount)+", DownloadCount :"+Integer.toString(DownloadCount)+", BuyCount :"+Integer.toString(BuyCount)+", HardnessLevel :"+Byte.toString(HardnessLevel)+", ActivityType :"+Byte.toString(ActivityType)+", ImportantNotes :"+ImportantNotes + ",TrackLength:" + TrackLength;
    }

    public static final byte SafeGpxContentTypes_None = 0;
    public static final byte SafeGpxContentTypes_Gpx = 1;
    public static final byte SafeGpxContentTypes_Image = 2;
}