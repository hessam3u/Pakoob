package bo.entity;

import com.google.gson.annotations.SerializedName;

public class SearchRequestDTO {
    @SerializedName(value="f", alternate = {"filter", "Filter"})
    public String Filter;
    @SerializedName(value="i", alternate = {"fromItemIndex", "FromItemIndex"})
    public int FromItemIndex;
    @SerializedName(value="t", alternate = {"pageSize", "PageSize"})
    public int PageSize;
    @SerializedName(value="s", alternate = {"Sort", "sort"})
    public String Sort;
    @SerializedName(value="o", alternate = {"otherCommand", "OtherCommand"})
    public String OtherCommand;
}
