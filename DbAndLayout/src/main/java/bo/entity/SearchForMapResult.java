package bo.entity;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import utils.hutilities;

public class SearchForMapResult {
    @SerializedName(value="resList", alternate = {"ResList"})
    public List<NbMap> resList;
    @SerializedName(value="message", alternate = {"Message"})
    public String message;

    public static SearchForMapResult fromBytes(byte[] bts){
        Gson gson = new Gson();
        String json = hutilities.decryptBytesToString(bts);
        return gson.fromJson(json, SearchForMapResult.class);
    }
}
