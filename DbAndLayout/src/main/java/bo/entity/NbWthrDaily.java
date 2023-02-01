package bo.entity;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.Calendar;
import utils.MyDate;
import utils.TextFormat;
import utils.hutilities;

public class NbWthrDaily
{
    @SerializedName(value="NbWthrDailyId", alternate = {"nbWthrDailyId"})
    public long NbWthrDailyId;
    @SerializedName(value="NbWthrLocId", alternate = {"nbWthrLocId"})
    public int NbWthrLocId;
    @SerializedName(value="NbWthrSourceId", alternate = {"nbWthrSourceId"})
    public int NbWthrSourceId;
    @SerializedName(value="UpdateDate", alternate = {"updateDate"})
    public String UpdateDate;
    @SerializedName(value="HasAlert", alternate = {"hasAlert"})
    public byte HasAlert;
    @SerializedName(value="Dt", alternate = {"dt"})
    public String Dt;
    @SerializedName(value="temp_morn", alternate = {"Temp_morn"})
    public double temp_morn;
    @SerializedName(value="temp_day", alternate = {"Temp_day"})
    public double temp_day;
    @SerializedName(value="temp_eve", alternate = {"Temp_eve"})
    public double temp_eve;
    @SerializedName(value="temp_night", alternate = {"Temp_night"})
    public double temp_night;
    @SerializedName(value="temp_min", alternate = {"Temp_min"})
    public double temp_min;
    @SerializedName(value="temp_max", alternate = {"Temp_max"})
    public double temp_max;
    @SerializedName(value="feels_like_morn", alternate = {"Feels_like_morn"})
    public double feels_like_morn;
    @SerializedName(value="feels_like_day", alternate = {"Feels_like_day"})
    public double feels_like_day;
    @SerializedName(value="feels_like_eve", alternate = {"Feels_like_eve"})
    public double feels_like_eve;
    @SerializedName(value="feels_like_night", alternate = {"Feels_like_night"})
    public double feels_like_night;
    @SerializedName(value="pressure", alternate = {"Pressure"})
    public int pressure;
    @SerializedName(value="humidity", alternate = {"Humidity"})
    public byte humidity;
    @SerializedName(value="dew_point", alternate = {"Dew_point"})
    public double dew_point;
    @SerializedName(value="wind_speed", alternate = {"Wind_speed"})
    public double wind_speed;
    @SerializedName(value="wind_gust", alternate = {"Wind_gust"})
    public double wind_gust;
    @SerializedName(value="wind_deg", alternate = {"Wind_deg"})
    public int wind_deg;
    @SerializedName(value="clouds", alternate = {"Clouds"})
    public double clouds;
    @SerializedName(value="uvi", alternate = {"Uvi"})
    public double uvi;
    @SerializedName(value="pop", alternate = {"Pop"})
    public double pop;
    @SerializedName(value="rain", alternate = {"Rain"})
    public double rain;
    @SerializedName(value="snow", alternate = {"Snow"})
    public double snow;
    @SerializedName(value="weather_id", alternate = {"Weather_id"})
    public int weather_id;
    public Calendar getUpdateDate(){return MyDate.CalendarFromCSharpSQLString(this.UpdateDate);}
    public String getUpdateDateView(){return MyDate.sQLStringToPersianSrting(this.UpdateDate, MyDate.DateToStringFormat.yyyymmdd, MyDate.TimeToStringFormat.None, "");}
    public void setUpdateDate(Calendar value){this.UpdateDate = MyDate.CalendarToCSharpSQLString(value);}
    public Calendar getDt(){return MyDate.CalendarFromCSharpSQLString(this.Dt);}
    public String getDtView(){return MyDate.sQLStringToPersianSrting(this.Dt, MyDate.DateToStringFormat.yyyymmdd, MyDate.TimeToStringFormat.None, "");}
    public void setDt(Calendar value){this.Dt = MyDate.CalendarToCSharpSQLString(value);}

    public NbWthrDaily() {
    }

    public static NbWthrDaily fromBytes(byte[] bts){
        Gson gson = new Gson();
        String json = hutilities.decryptBytesToString(bts);
        return gson.fromJson(json, NbWthrDaily.class);
    }
    public String toString(){
        return "NbWthrDailyId :"+Long.toString(NbWthrDailyId)+", NbWthrLocId :"+Integer.toString(NbWthrLocId)+", NbWthrSourceId :"+Integer.toString(NbWthrSourceId)+", UpdateDate :"+UpdateDate+", HasAlert :"+Byte.toString(HasAlert)+", Dt :"+Dt+", temp_morn :"+Double.toString(temp_morn)+", temp_day :"+Double.toString(temp_day)+", temp_eve :"+Double.toString(temp_eve)+", temp_night :"+Double.toString(temp_night)+", temp_min :"+Double.toString(temp_min)+", temp_max :"+Double.toString(temp_max)+", feels_like_morn :"+Double.toString(feels_like_morn)+", feels_like_day :"+Double.toString(feels_like_day)+", feels_like_eve :"+Double.toString(feels_like_eve)+", feels_like_night :"+Double.toString(feels_like_night)+", pressure :"+Integer.toString(pressure)+", humidity :"+Byte.toString(humidity)+", dew_point :"+Double.toString(dew_point)+", wind_speed :"+Double.toString(wind_speed)+", wind_gust :"+Double.toString(wind_gust)+", wind_deg :"+Integer.toString(wind_deg)+", clouds :"+Double.toString(clouds)+", uvi :"+Double.toString(uvi)+", pop :"+Double.toString(pop)+", rain :"+Double.toString(rain)+", snow :"+Double.toString(snow)+", weather_id :"+Integer.toString(weather_id);
    }

}