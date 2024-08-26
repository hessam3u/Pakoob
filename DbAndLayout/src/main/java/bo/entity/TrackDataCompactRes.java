package bo.entity;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import bo.NewClasses.InsUpdRes;
import utils.hutilities;

public class TrackDataCompactRes extends InsUpdRes {
    @SerializedName(value="Obj", alternate = {"obj"})
    public TrackDataCompact obj;

    public static TrackDataCompactRes fromBytes(byte[] bts){
        Gson gson = new GsonBuilder().registerTypeAdapter(LatLng.class, new LatLngJsonTypeAdapter()).create();
        String json = hutilities.decryptBytesToString(bts);
        //Log.e("ADDDDD", json);
        return gson.fromJson(json, TrackDataCompactRes.class);
    }




}
