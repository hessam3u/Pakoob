package bo.NewClasses;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.nio.charset.StandardCharsets;

import bo.entity.MobileInfoDTO;
import utils.hutilities;
public class SimpleRequest {
    @SerializedName(value="MobileInfoDTO", alternate = {"mobileInfoDTO"})
    public MobileInfoDTO mobileInfoDTO;
    @SerializedName(value="data", alternate = {"Data"})
    public String data;

    // if object type == String, make it json before sending to this
    public static SimpleRequest getInstance(Object objectToSend){
        SimpleRequest res = new SimpleRequest();
        res.mobileInfoDTO = MobileInfoDTO.instance();
        String temp = new Gson().toJson(objectToSend);
        byte[] buffer = temp.getBytes(StandardCharsets.UTF_8);
        buffer = hutilities.decrptBytes(buffer);
        res.data =  android.util.Base64.encodeToString(buffer, android.util.Base64.DEFAULT);
        return res;
    }
}
