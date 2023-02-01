package bo.entity;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import bo.NewClasses.InsUpdRes;
import utils.hutilities;

public class NbWeather extends InsUpdRes {
    @SerializedName(value="Loc", alternate = {"loc"})
    public NbWthrLoc Loc;
    @SerializedName(value="Current", alternate = {"current"})
    public NbWthrNow Current;
    @SerializedName(value="Daily", alternate = {"daily"})
    public List<NbWthrDaily> Daily;
    @SerializedName(value="Hourly", alternate = {"hourly"})
    public List<NbWthrHourly> Hourly;

    public static NbWeather fromBytes(byte[] bts){
        Gson gson = new Gson();
        String json = hutilities.decryptBytesToString(bts);
        //Log.e("ADDDDD", json);
        return gson.fromJson(json, NbWeather.class);
    }




}
