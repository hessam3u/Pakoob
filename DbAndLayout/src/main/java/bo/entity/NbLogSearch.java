package bo.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.Calendar;

import utils.MyDate;
import utils.hutilities;

@Entity
public class NbLogSearch {
    @PrimaryKey(autoGenerate = true)
    @SerializedName(value="si")
    public long NbLogSearchId;
//    @SerializedName(value="CCustomerId", alternate = {"cCustomerId"})
//    public int CCustomerId;
    @SerializedName(value="ct")
    @NonNull
    public byte NbCommandType;
    @SerializedName(value="st")
    @NonNull
    public String SearchText;
    @SerializedName(value="lat")
    @NonNull
    public double Lat;
    @SerializedName(value="lon")
    @NonNull
    public double Lon;
    @SerializedName(value="ri")
    @NonNull
    public long RelatedId;
    @SerializedName(value="sd")
    @NonNull
    public String SearchDateStr;

    public static NbLogSearch getInstance(byte NbCommandType, String  SearchText, double Lat, double Lon, long RelatedId){
        NbLogSearch res = new NbLogSearch();
        res.NbCommandType = NbCommandType;
        res.SearchText = SearchText;
        res.SearchText = SearchText;
        res.Lat = Lat;
        res.Lon = Lon;
        res.RelatedId = RelatedId;
        res.SearchDateStr = MyDate.CalendarToCSharpDateTimeAcceptable(Calendar.getInstance());
        return res;
    }

    public NbLogSearch() {
    }

    public static NbLogSearch fromBytes(byte[] bts){
        Gson gson = new Gson();
        String json = hutilities.decryptBytesToString(bts);
        return gson.fromJson(json, NbLogSearch.class);
    }
    public String toString(){
        return "NbLogSearchId :" + Long.toString(NbLogSearchId) + ", NbCommandType :" + Byte.toString(NbCommandType) + ", SearchText :" + SearchText + ", Lat :" + Double.toString(Lat) + ", Lon :" + Double.toString(Lon) + ", RelatedId :" + Long.toString(RelatedId);
        //+", CCustomerId :"+Integer.toString(CCustomerId)
    }

    public static final byte CommandType_SearchPOI = 1;
    public static final byte CommandType_ClickPointOnMap = 2;
    public static final byte CommandType_ClubSearch = 3;
    public static final byte CommandType_MapSearch = 4;
    public static final byte CommandType_OpenCityRouting = 5;
    public static final byte CommandType_OpenTrackPage = 6;
    public static final byte CommandType_OpenWeatherShow = 7;
    public static final byte CommandType_SafeGpxSearch = 8;

}
