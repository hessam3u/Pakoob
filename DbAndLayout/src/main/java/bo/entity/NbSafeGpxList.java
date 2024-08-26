package bo.entity;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.List;

import bo.NewClasses.ListItemsResult;
import utils.hutilities;

public class NbSafeGpxList extends ListItemsResult
{
	@SerializedName(value="resList", alternate = {"ResList"})
	public List<NbSafeGpx> resList;

	public static NbSafeGpxList fromBytes(byte[] bts){
		Gson gson = new Gson();
		String json = hutilities.decryptBytesToString(bts);
		return gson.fromJson(json, NbSafeGpxList.class);
	}

}