package bo.entity;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import bo.NewClasses.ListItemsResult;
import utils.hutilities;

public class FmMessageList extends ListItemsResult
{
	@SerializedName(value="resList", alternate = {"ResList"})
	public List<FmMessage> resList;

	public static FmMessageList fromBytes(byte[] bts){
		Gson gson = new Gson();
		String json = hutilities.decryptBytesToString(bts);
		Log.e("ROW", json);
		return gson.fromJson(json, FmMessageList.class);
	}
}