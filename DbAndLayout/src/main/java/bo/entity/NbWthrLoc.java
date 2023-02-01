package bo.entity;

import androidx.room.Ignore;

import com.github.eloyzone.jalalicalendar.JalaliDate;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.Calendar;

import utils.MyDate;
import utils.TextFormat;
import utils.hutilities;

public class NbWthrLoc {
    @SerializedName(value="NbWthrLocId", alternate = {"nbWthrLocId"})
    public int NbWthrLocId;
    @SerializedName(value="Name", alternate = {"name"})
    public String Name;
    @SerializedName(value="Lat", alternate = {"lat"})
    public double Lat;
    @SerializedName(value="Lon", alternate = {"lon"})
    public double Lon;
    @SerializedName(value="MontainForecastLink", alternate = {"montainForecastLink"})
    public String MontainForecastLink;
    @SerializedName(value="SnowForecastLink", alternate = {"snowForecastLink"})
    public String SnowForecastLink;
    @SerializedName(value="RelatedPoiId", alternate = {"relatedPoiId"})
    public long RelatedPoiId;
    @SerializedName(value="PoiType", alternate = {"poiType"})
    public short PoiType;
    @SerializedName(value="Elevation", alternate = {"elevation"})
    public int Elevation;
    @SerializedName(value="NbWthrSourceList", alternate = {"nbWthrSourceList"})
    public String NbWthrSourceList;
    @SerializedName(value="LastWeatherUpdate", alternate = {"lastWeatherUpdate"})
    public String LastWeatherUpdate;
    @SerializedName(value="AutoUpdateMins", alternate = {"autoUpdateMins"})
    public int AutoUpdateMins;
    @SerializedName(value="ProvinceId", alternate = {"provinceId"})
    public int ProvinceId;
    @SerializedName(value="Status", alternate = {"status"})
    public byte Status;


    //Added DTO
    @Ignore
    @SerializedName(value="ProvinceName", alternate = {"provinceName"})
    public String ProvinceName;






    public Calendar getLastWeatherUpdate(){return MyDate.CalendarFromCSharpSQLString(this.LastWeatherUpdate);}
    public String getLastWeatherUpdateView(){return MyDate.sQLStringToPersianSrting(this.LastWeatherUpdate, MyDate.DateToStringFormat.yyyymmdd, MyDate.TimeToStringFormat.None, "");}
    public void setLastWeatherUpdate(Calendar value){this.LastWeatherUpdate = MyDate.CalendarToCSharpSQLString(value);}

    public NbWthrLoc() {
    }

    public static NbWthrLoc fromBytes(byte[] bts){
        Gson gson = new Gson();
        String json = hutilities.decryptBytesToString(bts);
        return gson.fromJson(json, NbWthrLoc.class);
    }
    public String toString(){
        return "NbWthrLocId :"+Integer.toString(NbWthrLocId)+", Name :"+Name+", Lat :"+Double.toString(Lat)+", Lon :"+Double.toString(Lon)+", MontainForecastLink :"+MontainForecastLink+", SnowForecastLink :"+SnowForecastLink+", RelatedPoiId :"+Long.toString(RelatedPoiId)+", PoiType :"+Short.toString(PoiType)+", Elevation :"+Integer.toString(Elevation)+", NbWthrSourceList :"+NbWthrSourceList+", LastWeatherUpdate :"+LastWeatherUpdate+", AutoUpdateMins :"+Integer.toString(AutoUpdateMins)+", Status :"+Byte.toString(Status);
    }

    public String getLastWeatherUpdateViewLong(){
        Calendar dt = this.getLastWeatherUpdate();
        JalaliDate jal = MyDate.getJalaliDate(dt);

        return TextFormat.ReplaceEnglishNumbersWithPersianOne(
                MyDate.getDayOfWeek(jal) + " " + jal.getDay() + " " + jal.getMonthPersian().getStringInPersian()
                + " " + jal.getYear() + " ساعت " + MyDate.CalendarToTimeString(dt, MyDate.TimeToStringFormat.HourMin, ":")
        );
    }
}
