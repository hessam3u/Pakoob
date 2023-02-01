package bo.entity;

import com.google.gson.annotations.SerializedName;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index("TTClubTourCategoryId")})
public class TTClubTourCategoryDTO
{
	@PrimaryKey(autoGenerate = false)
	@SerializedName(value="TTClubTourCategoryId", alternate = {"ttClubTourCategoryId"})
	public Integer TTClubTourCategoryId;
	@SerializedName(value="Title", alternate = {"title"})
	public String Title;
	@SerializedName(value="Color", alternate = {"color"})
	public String Color;
	@SerializedName(value="DescUser", alternate = {"descUser"})
	public String DescUser;
	@SerializedName(value="PhotoAddress", alternate = {"photoAddress"})
	public String PhotoAddress;
	@SerializedName(value="UpdateStatus", alternate = {"updateStatus"})
	public Byte UpdateStatus;

	public TTClubTourCategoryDTO() {
    }
}