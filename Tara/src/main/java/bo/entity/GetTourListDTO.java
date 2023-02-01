package bo.entity;

import com.google.gson.annotations.SerializedName;

public class GetTourListDTO {

    @SerializedName(value="info", alternate = {"Info"})
    public MobileInfoDTO info;
    @SerializedName(value="ClubNameIds", alternate = {"clubNameIds"})
    public String ClubNameIds;
    @SerializedName(value="ProvinceIds", alternate = {"provinceIds"})
    public String ProvinceIds;
    @SerializedName(value="CategoryIds", alternate = {"categoryIds"})
    public String CategoryIds;
    @SerializedName(value="TourLengthIds", alternate = {"tourLengthIds"})
    public String TourLengthIds;
    @SerializedName(value="CityIds", alternate = {"cityIds"})
    public String CityIds;
    @SerializedName(value="Sort", alternate = {"sort"})
    public String Sort;
    @SerializedName(value="FromItemIndex", alternate = {"fromItemIndex"})
    public String FromItemIndex;
    @SerializedName(value="PageSize", alternate = {"pageSize"})
    public String PageSize;
    @SerializedName(value="Key", alternate = {"key"})
    public String Key;
    @SerializedName(value="OtherParams", alternate = {"otherParams"})
    public String OtherParams;
}
