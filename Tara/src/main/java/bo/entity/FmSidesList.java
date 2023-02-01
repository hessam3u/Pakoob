package bo.entity;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import bo.NewClasses.ListItemsResult;
import utils.hutilities;

public class FmSidesList extends ListItemsResult
{
	@SerializedName(value="resList", alternate = {"ResList"})
	public List<FmSides> resList;

	public static FmSidesList fromBytes(byte[] bts){
		Gson gson = new Gson();
		String json = hutilities.decryptBytesToString(bts);
		Log.e("ROW", json);
		return gson.fromJson(json, FmSidesList.class);
	}
}