package bo.NewClasses;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import bo.entity.CityDTO;
import utils.hutilities;

public class CityDTOList extends ListItemsResult
{
	@SerializedName(value="resList", alternate = {"ResList"})
	public List<CityDTO> resList;

	public static CityDTOList fromBytes(byte[] bts){
		Gson gson = new Gson();
		String json = hutilities.decryptBytesToString(bts);
		return gson.fromJson(json, CityDTOList.class);
	}
}