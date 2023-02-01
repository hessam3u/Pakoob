package bo.entity;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import utils.hutilities;

public class TourListResult {
    @SerializedName(value="resList", alternate = {"ResList"})
    public List<TTClubTour> resList;
    @SerializedName(value="message", alternate = {"Message"})
    public String message;
    @SerializedName(value="isOk", alternate = {"IsOk"})
    public boolean isOk;

    public static TourListResult fromBytes(byte[] bts){
        Gson gson = new Gson();
        String json = hutilities.decryptBytesToString(bts);
        return gson.fromJson(json, TourListResult.class);
    }
}

