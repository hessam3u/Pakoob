package bo.entity;

import com.google.gson.annotations.SerializedName;

public class BuyMapRequestDTO {
    @SerializedName(value="DiscountCode", alternate = {"discountCode"})
    public String DiscountCode;
    @SerializedName(value="NBMapId", alternate = {"nBMapId", "nbMapId"})
    public int NBMapId;
    @SerializedName(value="NbBuyType", alternate = {"nbBuyType"})
    public int NbBuyType;

    public static final short NbBuyType_Map = 1;
    public static final short NbBuyType_Gpx = 2;
    public static final short NbBuyType_Adv = 3;
}
