package bo.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import utils.hutilities;

public class NbGpxRequest {
    @SerializedName(value="NbGpxRequestId", alternate = {"nbGpxRequestId"})
    public int NbGpxRequestId;
    @SerializedName(value="CCustomerId", alternate = {"cCustomerId"})
    public int CCustomerId;
    @SerializedName(value="NbPoiId", alternate = {"nbPoiId"})
    public long NbPoiId;
    @SerializedName(value="Lat", alternate = {"lat"})
    public double Lat;
    @SerializedName(value="Lon", alternate = {"lon"})
    public double Lon;
    @SerializedName(value="TitleOfPoi", alternate = {"titleOfPoi"})
    public String TitleOfPoi;
    @SerializedName(value="Desc", alternate = {"desc"})
    public String Desc;
    @SerializedName(value="AdminAns", alternate = {"adminAns"})
    public String AdminAns;
    @SerializedName(value="GpxRequestType", alternate = {"gpxRequestType"})
    public byte GpxRequestType;
    @SerializedName(value="GpxRequestStatus", alternate = {"gpxRequestStatus"})
    public byte GpxRequestStatus;
    @SerializedName(value="UserReadStatus", alternate = {"userReadStatus"})
    public byte UserReadStatus;
    @SerializedName(value="NbSafeGpxIdRelated", alternate = {"nbSafeGpxIdRelated"})
    public int NbSafeGpxIdRelated;

    public NbGpxRequest() {
    }

    public static NbGpxRequest fromBytes(byte[] bts){
        Gson gson = new Gson();
        String json = hutilities.decryptBytesToString(bts);
        return gson.fromJson(json, NbGpxRequest.class);
    }
    public String toString(){
        return "NbGpxRequestId :"+Integer.toString(NbGpxRequestId)+", CCustomerId :"+Integer.toString(CCustomerId)+", NbPoiId :"+Long.toString(NbPoiId)+", Lat :"+Double.toString(Lat)+", Lon :"+Double.toString(Lon)+", TitleOfPoi :"+TitleOfPoi+", Desc :"+Desc+", AdminAns :"+AdminAns+", GpxRequestType :"+Byte.toString(GpxRequestType)+", GpxRequestStatus :"+Byte.toString(GpxRequestStatus)+", UserReadStatus :"+Byte.toString(UserReadStatus)+", NbSafeGpxIdRelated :"+Integer.toString(NbSafeGpxIdRelated);
    }

        public static final byte GpxRequestStatus_Ready = 1;
        public static final byte GpxRequestStatus_JustSent = 2;
        public static final byte GpxRequestStatus_InReview = 3;
        public static final byte GpxRequestStatus_Unable = 4;
        public static final byte GpxRequestStatus_InFeature = 5;

        public static final byte GpxRequestType_GPX = 1;
        public static final byte GpxRequestType_Phone = 2;

        public static final byte UserReadStatus_Read = 1;
        public static final byte UserReadStatus_NOTREAD = 2;

}
