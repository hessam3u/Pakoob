package bo.entity;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.Calendar;
import utils.MyDate;
import utils.TextFormat;
import utils.hutilities;

public class NbWthrHourly
{
    @SerializedName(value="NbWthrHourlyId", alternate = {"nbWthrHourlyId"})
    public long NbWthrHourlyId;
    @SerializedName(value="HourIndex", alternate = {"hourIndex"})
    public byte HourIndex;
    @SerializedName(value="DurationMins", alternate = {"durationMins"})
    public int DurationMins;
    @SerializedName(value="NbWthrLocId", alternate = {"nbWthrLocId"})
    public int NbWthrLocId;
    @SerializedName(value="NbWthrSourceId", alternate = {"nbWthrSourceId"})
    public int NbWthrSourceId;
    @SerializedName(value="UpdateDate", alternate = {"updateDate"})
    public String UpdateDate;
    @SerializedName(value="Dt", alternate = {"dt"})
    public String Dt;
    @SerializedName(value="temp", alternate = {"Temp"})
    public double temp;
    @SerializedName(value="feels_like", alternate = {"Feels_like"})
    public double feels_like;
    @SerializedName(value="pressure", alternate = {"Pressure"})
    public int pressure;
    @SerializedName(value="humidity", alternate = {"Humidity"})
    public byte humidity;
    @SerializedName(value="dew_point", alternate = {"Dew_point"})
    public double dew_point;
    @SerializedName(value="uvi", alternate = {"Uvi"})
    public double uvi;
    @SerializedName(value="clouds", alternate = {"Clouds"})
    public double clouds;
    @SerializedName(value="visibility", alternate = {"Visibility"})
    public int visibility;
    @SerializedName(value="wind_speed", alternate = {"Wind_speed"})
    public double wind_speed;
    @SerializedName(value="wind_gust", alternate = {"Wind_gust"})
    public double wind_gust;
    @SerializedName(value="wind_deg", alternate = {"Wind_deg"})
    public int wind_deg;
    @SerializedName(value="pop", alternate = {"Pop"})
    public double pop;
    @SerializedName(value="rain", alternate = {"Rain"})
    public double rain;
    @SerializedName(value="snow", alternate = {"Snow"})
    public double snow;
    @SerializedName(value="weather_id", alternate = {"Weather_id"})
    public int weather_id;
    public Calendar getUpdateDate(){return MyDate.CalendarFromCSharpSQLString(this.UpdateDate);}
    public Calendar DtCalendar = null;
    public String getUpdateDateView(){return MyDate.sQLStringToPersianSrting(this.UpdateDate, MyDate.DateToStringFormat.yyyymmdd, MyDate.TimeToStringFormat.None, "");}
    public void setUpdateDate(Calendar value){this.UpdateDate = MyDate.CalendarToCSharpSQLString(value);}
    public Calendar getDt(){
        if (DtCalendar == null)
            DtCalendar = MyDate.CalendarFromCSharpSQLString(this.Dt);
        return DtCalendar;
    }
    public String getDtView(){return MyDate.sQLStringToPersianSrting(this.Dt, MyDate.DateToStringFormat.yyyymmdd, MyDate.TimeToStringFormat.None, "");}
    public void setDt(Calendar value){
        this.Dt = MyDate.CalendarToCSharpSQLString(value);
        this.DtCalendar = value;
    }

    public NbWthrHourly() {
    }

    public static NbWthrHourly fromBytes(byte[] bts){
        Gson gson = new Gson();
        String json = hutilities.decryptBytesToString(bts);
        return gson.fromJson(json, NbWthrHourly.class);
    }
    public String toString(){
        return "NbWthrHourlyId :"+Long.toString(NbWthrHourlyId)+", HourIndex :"+Byte.toString(HourIndex)+", DurationMins :"+Integer.toString(DurationMins)+", NbWthrLocId :"+Integer.toString(NbWthrLocId)+", NbWthrSourceId :"+Integer.toString(NbWthrSourceId)+", UpdateDate :"+UpdateDate+", Dt :"+Dt+", temp :"+Double.toString(temp)+", feels_like :"+Double.toString(feels_like)+", pressure :"+Integer.toString(pressure)+", humidity :"+Byte.toString(humidity)+", dew_point :"+Double.toString(dew_point)+", uvi :"+Double.toString(uvi)+", clouds :"+Double.toString(clouds)+", visibility :"+Integer.toString(visibility)+", wind_speed :"+Double.toString(wind_speed)+", wind_gust :"+Double.toString(wind_gust)+", wind_deg :"+Integer.toString(wind_deg)+", pop :"+Double.toString(pop)+", rain :"+Double.toString(rain)+", snow :"+Double.toString(snow)+", weather_id :"+Integer.toString(weather_id);
    }

}