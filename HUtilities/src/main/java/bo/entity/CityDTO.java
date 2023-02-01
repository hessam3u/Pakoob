package bo.entity;

import com.google.gson.annotations.SerializedName;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index("CityId")})
public class CityDTO
{
	@PrimaryKey(autoGenerate = false)
	@SerializedName(value="CityId", alternate = {"cityId"})
	public Integer CityId;
	@SerializedName(value="Name", alternate = {"name"})
	public String Name;
	@SerializedName(value="ProvinceId", alternate = {"provinceId"})
	public Integer ProvinceId;
	@SerializedName(value="Latitude", alternate = {"latitude"})
	public Double Latitude;
	@SerializedName(value="Longtitude", alternate = {"longtitude"})
	public Double Longtitude;
	@SerializedName(value="UpdateStatus", alternate = {"updateStatus"})
	public Byte UpdateStatus;
	@SerializedName(value="ProvinceName", alternate = {"provinceName"})
	public String ProvinceName;


	public CityDTO() {
    }
}