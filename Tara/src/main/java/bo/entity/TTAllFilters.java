package bo.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TTAllFilters {
    @SerializedName(value="city")
    public List<CityDTO> city;
    @SerializedName(value="clubNames")
    public List<TTClubNameDTO> clubNames;
    @SerializedName(value="category")
    public List<TTClubTourCategoryDTO> category;
    @SerializedName(value="LastFilterUpdate", alternate = {"lastFilterUpdate"})
    public String LastFilterUpdate;
}
