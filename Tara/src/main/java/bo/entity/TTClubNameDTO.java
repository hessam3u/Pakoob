package bo.entity;

import com.google.gson.annotations.SerializedName;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index("TTClubNameId")})
public class TTClubNameDTO
{
	@PrimaryKey(autoGenerate = false)
	@SerializedName(value="a", alternate = {"TTClubNameId"})
	public Integer TTClubNameId;
	@SerializedName(value="b", alternate = {"MojServiceId"})
	public Long MojServiceId;
	@SerializedName(value="c", alternate = {"Name"})
	public String Name;
	@SerializedName(value="d", alternate = {"CCustomerIdRelated"})
	public Integer CCustomerIdRelated;
	@SerializedName(value="e", alternate = {"ExtSiteId"})
	public Integer ExtSiteId;
	@SerializedName(value="f", alternate = {"Logo"})
	public String Logo;
	@SerializedName(value="g", alternate = {"WebsiteAddress"})
	public String WebsiteAddress;
	@SerializedName(value="h", alternate = {"Address"})
	public String Address;
	@SerializedName(value="i", alternate = {"Telephone"})
	public String Telephone;
	@SerializedName(value="j", alternate = {"Desc"})
	public String Desc;
	@SerializedName(value="k", alternate = {"ManagerName"})
	public String ManagerName;
	@SerializedName(value="l", alternate = {"CityId"})
	public Integer CityId;
	@Ignore
	@SerializedName(value="m", alternate = {"HeaderImage"})
	public String HeaderImage;
	@Ignore
	@SerializedName(value="n", alternate = {"FullClubName"})
	public String FullClubName;
	@SerializedName(value="o", alternate = {"CityName"})
	public String CityName;
	@Ignore
	@SerializedName(value="p", alternate = {"ProvinceName"})
	public String ProvinceName;
	@SerializedName(value="q", alternate = {"UpdateStatus"})
	public Byte UpdateStatus;
	@Ignore
	@SerializedName(value="r", alternate = {"FollowingByMe"})
	public Byte FollowingByMe;
	@Ignore
	@SerializedName(value="s", alternate = {"FanByMe"})
	public Byte FanByMe;
	@Ignore
	@SerializedName(value="t", alternate = {"NotificationStatus"})
	public Byte NotificationStatus;
	@Ignore
	@SerializedName(value="u", alternate = {"ClubType"})
	public Byte ClubType;
	@Ignore
	@SerializedName(value="v", alternate = {"TourCount"})
	public int TourCount;
	@Ignore
	@SerializedName(value="w", alternate = {"FanCount"})
	public int FanCount;
	@Ignore
	@SerializedName(value="x", alternate = {"FollowerCount"})
	public int FollowerCount;
	@Ignore
	@SerializedName(value="y", alternate = {"NextTourCount"})
	public int NextTourCount;



	public TTClubNameDTO() {
	}
}