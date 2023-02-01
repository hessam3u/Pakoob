package bo.NewClasses;

import android.media.MediaCasException;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import utils.hutilities;

public class InsUpdRes {
    @SerializedName(value="isOk")
    public Boolean isOk;
    @SerializedName(value="message")
    public String message;
    @SerializedName(value="command")
    public String command;
    @SerializedName(value="status")
    public Integer status;
    @SerializedName(value="resValue")
    public String resValue;

    public InsUpdRes() {
    }

    public static InsUpdRes fromBytes(byte[] bts){
        Gson gson = new Gson();
        String json = hutilities.decryptBytesToString(bts);
        return gson.fromJson(json, InsUpdRes.class);
    }
    public interface HAsyncResponseInsUpdRes{
        void onResponseCompleted(InsUpdRes res);
    }
}
