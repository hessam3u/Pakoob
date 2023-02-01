package bo.entity;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import bo.NewClasses.ListItemsResult;
import utils.hutilities;

public class NbAdvList extends ListItemsResult {
    @SerializedName(value="resList", alternate = {"ResList"})
    public List<NbAdv> resList;

    public static NbAdvList fromBytes(byte[] bts){
        Gson gson = new Gson();
        String json = hutilities.decryptBytesToString(bts);
        return gson.fromJson(json, NbAdvList.class);
    }
}
