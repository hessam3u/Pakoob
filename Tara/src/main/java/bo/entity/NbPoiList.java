package bo.entity;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import bo.NewClasses.ListItemsResult;
import utils.hutilities;

public class NbPoiList extends ListItemsResult
{
	@SerializedName(value="resList", alternate = {"ResList"})
	public List<NbPoi> resList;

	public static NbPoiList fromBytes(byte[] bts){
		Gson gson = new Gson();
		String json = hutilities.decryptBytesToString(bts);
		Log.e("RECIEVED",json);
		return gson.fromJson(json, NbPoiList.class);
	}
}