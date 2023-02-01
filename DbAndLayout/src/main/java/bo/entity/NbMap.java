package bo.entity;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.util.HttpUtils;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import bo.sqlite.NbMapSQLite;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.hutilities;

@Entity
public class NbMap
{
    @PrimaryKey
    @SerializedName(value="NbMapId", alternate = {"nbMapId", "im"})
    public Integer NbMapId;
    @SerializedName(value="Name", alternate = {"name", "n"})
    public String Name;
    @SerializedName(value="OrginalName", alternate = {"orginalName", "on"})
    public String OrginalName;
    @SerializedName(value="NCCIndex", alternate = {"nCCIndex", "in","nccIndex"})// ********** نکته خیلی مهم. cc must be small letter
    public String NCCIndex;
    @SerializedName(value="BlockName", alternate = {"blockName", "bm"})
    public String BlockName;
    @SerializedName(value="LatS", alternate = {"latS", "ls"})
    public Double LatS;
    @SerializedName(value="LatN", alternate = {"latN", "ln"})
    public Double LatN;
    @SerializedName(value="LonE", alternate = {"lonE", "le"})
    public Double LonE;
    @SerializedName(value="LonW", alternate = {"lonW","lw"})
    public Double LonW;
    @Ignore
    @SerializedName(value="Tag", alternate = {"tag"})
    public String Tag;
    @SerializedName(value="Desc", alternate = {"desc", "d"})
    public String Desc;
    @SerializedName(value="Price", alternate = {"price", "p"})
    public Double Price;
    @SerializedName(value="AcServiceId", alternate = {"acServiceId", "si"})
    public Long AcServiceId;
    @Ignore
    @SerializedName(value="Status", alternate = {"status"})
    public Byte Status;
    @SerializedName(value="AvailablityType", alternate = {"availablityType", "at"})
    public Byte AvailablityType;
    @Ignore
    @SerializedName(value="AdminBuyType", alternate = {"adminBuyType"})
    public Byte AdminBuyType;
    @Ignore
    @SerializedName(value="FileAddress", alternate = {"fileAddress"})
    public String FileAddress;
    @SerializedName(value="EncType", alternate = {"encType", "et"})
    public Byte EncType;
    @SerializedName(value="FileType", alternate = {"fileType", "ft"})
    public Byte FileType;
    @SerializedName(value="NCCBlock", alternate = {"nCCBlock", "nb", "nccBlock"})
    public String NCCBlock;
    @SerializedName(value="Scale", alternate = {"scale", "sc"})
    public Double Scale;
    @Ignore
    @SerializedName(value="AdminDesc", alternate = {"adminDesc"})
    public String AdminDesc;
    @SerializedName(value="Version", alternate = {"version", "v"})
    public Integer Version;
    @SerializedName(value="Year", alternate = {"year", "y"})
    public Integer Year;
    @SerializedName(value="NbPublisherTypeId", alternate = {"nbPublisherTypeId", "pt"})
    public Integer NbPublisherTypeId;
    @SerializedName(value="DemoLatS", alternate = {"demolatS", "dls"})
    public Double DemoLatS;
    @SerializedName(value="DemoLatN", alternate = {"demolatN", "dln"})
    public Double DemoLatN;
    @SerializedName(value="DemoLonE", alternate = {"demolonE", "dle"})
    public Double DemoLonE;
    @SerializedName(value="DemoLonW", alternate = {"demolonW","dlw"})
    public Double DemoLonW;
    @SerializedName(value="DemoFileAddress", alternate = {"demoFileAddress", "dfs"})
    public String DemoFileAddress;
    @SerializedName(value="DemoStatus", alternate = {"demoStatus", "ds"})
    public byte DemoStatus;
    @SerializedName(value="MapCategory", alternate = {"mapCategory", "mc"})
    @NonNull
    public byte MapCategory;
    @SerializedName(value="MapType", alternate = {"mapType", "mt"})
    @NonNull
    public byte MapType;
    @SerializedName(value="PreviewImage", alternate = {"previewImage", "pi"})
    @NonNull
    public String PreviewImage;


    //Added Fields From NbMapBuy:

    @SerializedName(value="RequestStatus", alternate = {"requestStatus", "rs"})
    public Byte RequestStatus;
    @SerializedName(value="BuyStatus", alternate = {"buyStatus", "bs"})
    public Byte BuyStatus;
    @SerializedName(value="BuyDesc", alternate = {"buyDesc", "bd"})
    public String BuyDesc;
    @Ignore
    @SerializedName(value="Expired", alternate = {"expired", "ex"})
    public byte Expired;
    @Ignore
    @SerializedName(value="ExpireDate", alternate = {"expireDate", "ed"})
    public String ExpireDate;

    //Added fields for working in app
    @SerializedName(value="LocalFileAddress", alternate = {"localFileAddress"})
    public String LocalFileAddress = "";
    @SerializedName(value="Extracted", alternate = {"extracted"})
    public byte Extracted = 2;
    @SerializedName(value="IsVisible", alternate = {"isVisible"})
    public byte IsVisible = 1;





    public List<LatLng> getBounds(){
        List<LatLng> res = new ArrayList<>();
        res.add(new LatLng(LatN, LonW));
        res.add(new LatLng(LatN, LonE));
        res.add(new LatLng(LatS, LonE));
        res.add(new LatLng(LatS, LonW));

        return res;
    }

    public NbMap() {
    }
    public class Enums{
        public static final byte BuyStatusTypes_None = 0;
        public static final byte BuyStatusTypes_Done = 1;
        public static final byte BuyStatusTypes_Wait = 2;
        public static final byte BuyStatusTypes_Canceled = 3;
        public static final byte BuyStatusTypes_PendingRequest = 4;


        public static final byte RequestStatusTypes_None = 0;
        public static final byte RequestStatusTypes_Done = 1;
        public static final byte RequestStatusTypes_Wait = 2;
        public static final byte RequestStatusTypes_Canceled = 3;


        public static final byte AvailablityType_None = 0;
        public static final byte AvailablityType_Available = 1;
        public static final byte AvailablityType_MostOrder = 2;
        public static final byte AvailablityType_NotAvailable = 3;
        public static final byte AvailablityType_MostUpload = 4;

        public static final byte NbBuyType_None = 0;
        public static final byte NbBuyType_Map = 1;
        public static final byte NbBuyType_GPX = 2;
        public static final byte NbBuyType_Adv = 3;


    }

}