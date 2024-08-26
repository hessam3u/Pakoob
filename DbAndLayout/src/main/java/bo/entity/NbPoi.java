package bo.entity;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import pakoob.DbAndLayout.R;

@Entity
public class NbPoi
{
    @PrimaryKey(autoGenerate = true)
    @SerializedName(value="NbPoiId", alternate = {"nbPoiId", "ip"})
    public Long NbPoiId;
    @SerializedName(value="Name", alternate = {"name", "n"})
    @NonNull
    public String Name;
    @SerializedName(value="AltName", alternate = {"altName", "an"})
    @NonNull
    public String AltName;
    @SerializedName(value="Level", alternate = {"level", "lv"})
    @NonNull
    public byte Level;
    @SerializedName(value="ParentId", alternate = {"parentId", "pi"})
    @NonNull
    public long ParentId;
    @SerializedName(value="Address", alternate = {"address", "a"})
    @NonNull
    public String Address;
    @SerializedName(value="LatS", alternate = {"latS", "ls"})
    @NonNull
    public Double LatS;
    @SerializedName(value="LonW", alternate = {"lonW","lw"})
    @NonNull
    public Double LonW;
    @SerializedName(value="LatN", alternate = {"latN", "ln"})
    @NonNull
    public Double LatN;
    @SerializedName(value="LonE", alternate = {"lonE", "le"})
    @NonNull
    public Double LonE;
    @SerializedName(value="Color", alternate = {"color", "c"})
    @NonNull
    public int Color;
    @SerializedName(value="ShowStatus", alternate = {"showStatus", "s"})
    @NonNull
    public byte ShowStatus;
    @SerializedName(value="PoiType", alternate = {"poiType", "p"})
    @NonNull
    public short PoiType;
    @SerializedName(value="ServerId", alternate = {"serverId", "si"})
    @NonNull
    public long ServerId;
    @SerializedName(value="CreatorType", alternate = {"creatorType", "cr"})
    @NonNull
    public byte CreatorType;
    @SerializedName(value="ValidityLevel", alternate = {"validityLevel", "v"})
    @NonNull
    public byte ValidityLevel;
    @SerializedName(value="ZoomMin", alternate = {"zoomMin", "zn"})
    @NonNull
    public byte ZoomMin;
    @SerializedName(value="ZoomMax", alternate = {"zoomMax", "zx"})
    @NonNull
    public byte ZoomMax;
    @SerializedName(value="IsPolygon", alternate = {"isPolygon", "pn"})
    @NonNull
    public byte IsPolygon ;
    @SerializedName(value="Elevation", alternate = {"elevation", "e"})
    @NonNull
    public short Elevation;
    @SerializedName(value="ActivityType", alternate = {"activityType", "at"})
    @NonNull
    public byte ActivityType;
    @SerializedName(value="DisplaySize", alternate = {"displaySize", "ds"})
    @NonNull
    public byte DisplaySize;
    @SerializedName(value="Priority", alternate = {"priority", "pr"})
    @NonNull
    public int Priority;
    @SerializedName(value="LatBegin", alternate = {"latBegin", "lab"})
    @NonNull
    public Double LatBegin;
    @SerializedName(value="LonBegin", alternate = {"lonBegin","lob"})
    @NonNull
    public Double LonBegin;

    @Ignore
    public boolean isSelected = false;
    @NonNull
    @SerializedName(value="addedInfo", alternate = {"ai"})
    public String addedInfo = "";
    @Ignore
    @SerializedName(value="distanceFromHere", alternate = {"df"})
    public String distanceFromHere = "";

    @Ignore
    @SerializedName(value="ProvinceId", alternate = {"pv"})
    public int ProvinceId;
    @Ignore
    @SerializedName(value="ProvinceName", alternate = {"pe"})
    public String ProvinceName;


    @Ignore
    public Polyline polyLine = null;
    @Ignore
    public Marker marker = null;

    public List<LatLng> getBounds(){
        List<LatLng> res = new ArrayList<>();
        res.add(new LatLng(LatN, LonW));
        res.add(new LatLng(LatN, LonE));
        res.add(new LatLng(LatS, LonE));
        res.add(new LatLng(LatS, LonW));

        return res;
    }

    public String toString(){
        return "NbPoiId :"+Long.toString(NbPoiId)+", Name :"+Name+", AltName :"+AltName+", Level :"+Integer.toString(Level)+", ParentId :"+Long.toString(ParentId)+", Address :"+Address+", LatS :"+Double.toString(LatS)+", LonW :"+Double.toString(LonW)+", LatN :"+Double.toString(LatN)+", LonE :"+Double.toString(LonE)+", Color :"+Integer.toString(Color)+", ShowStatus :"+Byte.toString(ShowStatus)+", PoiType :"+Short.toString(PoiType)+", CreatorType :"+Byte.toString(CreatorType)+", ValidityLevel :"+Byte.toString(ValidityLevel)+", ZoomMin :"+Byte.toString(ZoomMin)+", ZoomMax :"+Byte.toString(ZoomMax)+", IsPolygon :"+Byte.toString(IsPolygon)+", Elevation :"+Integer.toString(Elevation)+", ActivityType :"+Byte.toString(ActivityType)+", Priority :"+Integer.toString(Priority)+", LatBegin :"+Double.toString(LatBegin)+", LonBegin :"+Double.toString(LonBegin)+", ProvinceId :"+Integer.toString(ProvinceId);
    }
    public static NbPoi getPoiForPosition(double latitude, double longitude, String positionText){
        NbPoi res = new NbPoi();
        res.NbPoiId = 0l;
        res.LatS = latitude;
        res.LonW = longitude;
        res.Name = String.format(positionText + " " + "%.4f,%.4f", res.LatS, res.LonW);
        return res;
    }

    public static NbPoi getInstance(long NbPoiId, String Name, String AltName, byte Level, long ParentId, String Address, Double LatS, Double LonW, Double LatN, Double LonE
            , int Color, byte ShowStatus, short PoiType, long ServerId, byte CreatorType, byte ValidityLevel
            , byte ZoomMin, byte ZoomMax, byte ActivityType, byte DisplaySize, String addedInfo, int Priority, Double LatBegin, Double LonBegin){
        NbPoi res = new NbPoi();
        res.NbPoiId = NbPoiId;
        res.AltName = AltName;
        res.Name = Name;
        res.Level = Level;
        res.ParentId = ParentId;
        res.Address = Address;
        res.LatS = LatS;
        res.LatN = LatN;
        res.LonE = LonE;
        res.LonW = LonW;
        res.Color = Color;
        res.ShowStatus = ShowStatus;
        res.PoiType = PoiType;
        res.ServerId = ServerId;
        res.CreatorType = CreatorType;
        res.ValidityLevel = ValidityLevel;
        res.ZoomMin = ZoomMin;
        res.ZoomMax = ZoomMax;
        res.ActivityType = ActivityType;
        res.DisplaySize = DisplaySize;
        res.addedInfo = addedInfo;
        res.Priority = Priority;
        res.LatBegin = LatBegin;
        res.LonBegin = LonBegin;
        return res;
    }
    public static NbPoi getInstance(String Name, String AltName, byte Level, long ParentId, String Address, Double LatS, Double LonW, Double LatN, Double LonE
            , int Color, byte ShowStatus, short PoiType, long ServerId, byte CreatorType, byte ValidityLevel
            , byte ZoomMin, byte ZoomMax, byte ActivityType, byte DisplaySize, String addedInfo, int Priority, Double LatBegin, Double LonBegin){
        NbPoi res = new NbPoi();
        res.Name = Name;
        res.AltName = AltName;
        res.Level = Level;
        res.ParentId = ParentId;
        res.Address = Address;
        res.LatS = LatS;
        res.LatN = LatN;
        res.LonE = LonE;
        res.LonW = LonW;
        res.Color = Color;
        res.ShowStatus = ShowStatus;
        res.PoiType = PoiType;
        res.ServerId = ServerId;
        res.CreatorType = CreatorType;
        res.ValidityLevel = ValidityLevel;
        res.ZoomMin = ZoomMin;
        res.ZoomMax = ZoomMax;
        res.ActivityType = ActivityType;
        res.DisplaySize = DisplaySize;
        res.addedInfo = addedInfo;
        res.Priority = Priority;
        res.LatBegin = LatBegin;
        res.LonBegin = LonBegin;
        return res;
    }
    public NbPoi() {
    }
    public static int PoiTypeToResourcId(short PoiType, int rType){
        switch (PoiType){
            case Enums.PoiType_None: return rType == 1?R.drawable.ac_waypoint:rType == 2?R.color.colorOrange1:R.drawable.ic_circle_redyellow_line;
            case Enums.PoiType_Folder: return rType == 1?R.drawable.ac_twoway:rType == 2? R.color.colorGold:R.drawable.ic_circle_redyellow_line;//??????????
            case Enums.PoiType_Track: return rType == 1?R.drawable.ac_twoway:rType == 2?R.color.colorGold:R.drawable.ic_circle_redyellow_line;//??????????
            case Enums.PoiType_Route: return rType == 1?R.drawable.ac_twoway:rType == 2?R.color.colorGold:R.drawable.ic_circle_redyellow_line;//??????????
            case Enums.PoiType_Map: return rType == 1?R.drawable.ac_map_pointer:rType == 2?R.color.colorGold:R.drawable.ac_map_pointer;//??????????
            case Enums.PoiType_Waypoint: return rType == 1?R.drawable.ac_waypoint:rType == 2?R.color.colorOrange1:R.drawable.ic_circle_redyellow_line;
            case Enums.PoiType_Peak: return rType == 1?R.drawable.ac_peak1:rType == 2?R.color.colorBlue1:R.drawable.ac_peak1;
            case Enums.PoiType_Shelter1: return rType == 1?R.drawable.ac_shelter1:rType == 2?R.color.colorBrownDark:R.drawable.ac_chador;
            case Enums.PoiType_Shelter2: return rType == 1?R.drawable.ac_shelter2:rType == 2?R.color.colorBrownLight:R.drawable.ac_chador;
            case Enums.PoiType_Shelter3: return rType == 1?R.drawable.ac_shelter2:rType == 2?R.color.colorBrownMid:R.drawable.ac_chador;
            case Enums.PoiType_SkyResort: return rType == 1?R.drawable.ac_ski:rType == 2?R.color.colorBlue2:R.drawable.ic_circle_redyellow_line;
            case Enums.PoiType_Camp: return rType == 1?R.drawable.ac_chador:rType == 2?R.color.colorOrange1:R.drawable.ac_chador;
            case Enums.PoiType_GelFeshan: return rType == 1?R.drawable.ac_waypoint:rType == 2?R.color.colorOrange1:R.drawable.ic_circle_redyellow_line;//??????????
            case Enums.PoiType_AtashFeshan: return rType == 1?R.drawable.ac_waypoint:rType == 2?R.color.colorOrange1:R.drawable.ic_circle_redyellow_line;//??????????
            case Enums.PoiType_Ghar: return rType == 1?R.drawable.ac_cave:rType == 2?R.color.colorBlackLight:R.drawable.ic_circle_redyellow_line;
            case Enums.PoiType_Pass: return rType == 1?R.drawable.ac_pass:rType == 2?R.color.colorBlackLight:R.drawable.ic_circle_redyellow_line;
            case Enums.PoiType_Bridge: return rType == 1?R.drawable.ac_bridge:rType == 2?R.color.colorBrownMid:R.drawable.ic_circle_redyellow_line;

            case Enums.PoiType_Danger_General: return rType == 1?R.drawable.ac_danger:rType == 2?R.color.red:R.drawable.ic_circle_redyellow_line;//??????????
            case Enums.PoiType_Danger_Avalanche: return rType == 1?R.drawable.ac_danger:rType == 2?R.color.red:R.drawable.ic_circle_redyellow_line;//??????????
            case Enums.PoiType_Danger_Stone: return rType == 1?R.drawable.ac_danger:rType == 2?R.color.red:R.drawable.ic_circle_redyellow_line;//??????????
            case Enums.PoiType_Danger_Lost: return rType == 1?R.drawable.ac_danger:rType == 2?R.color.red:R.drawable.ic_circle_redyellow_line;//??????????
            case Enums.PoiType_Danger_Fall: return rType == 1?R.drawable.ac_danger:rType == 2?R.color.red:R.drawable.ic_circle_redyellow_line;//??????????
            case Enums.PoiType_Danger_Flood: return rType == 1?R.drawable.ac_danger:rType == 2?R.color.red:R.drawable.ic_circle_redyellow_line;//??????????
            case Enums.PoiType_WaterSource: return rType == 1?R.drawable.ac_watershir:rType == 2?R.color.colorBlue:R.drawable.ac_watershir;
            case Enums.PoiType_Manbae: return rType == 1?R.drawable.ac_watershir:rType == 2?R.color.colorBlue:R.drawable.ac_watershir;//??????????
            case Enums.PoiType_Jooy: return rType == 1?R.drawable.ac_watershir:rType == 2?R.color.colorBlue:R.drawable.ac_watershir;//??????????
            case Enums.PoiType_Rood: return rType == 1?R.drawable.ac_watershir:rType == 2?R.color.colorBlue:R.drawable.ac_watershir;//??????????
            case Enums.PoiType_Berkeh: return rType == 1?R.drawable.ac_watershir:rType == 2?R.color.colorBlue:R.drawable.ac_watershir;//??????????
            case Enums.PoiType_Daryache: return rType == 1?R.drawable.ac_watershir:rType == 2?R.color.colorBlue:R.drawable.ac_watershir;//??????????
            case Enums.PoiType_Abshar: return rType == 1?R.drawable.ac_waterfall:rType == 2?R.color.colorBlue2:R.drawable.ic_circle_blueblue;

            case Enums.PoiType_City: return rType == 1?R.drawable.ac_city:rType == 2?R.color.colorGrey:R.drawable.ic_circle_redyellow_line;
            case Enums.PoiType_Town: return rType == 1?R.drawable.ac_city:rType == 2?R.color.colorGreyLight:R.drawable.ic_circle_redyellow_line;
            case Enums.PoiType_Villate: return rType == 1?R.drawable.ac_village:rType == 2?R.color.colorBlackLight:R.drawable.ic_circle_redyellow_line;
            case Enums.PoiType_Mahaleh: return rType == 1?R.drawable.ac_house:rType == 2?R.color.colorGrey:R.drawable.ic_circle_redyellow_line;
            case Enums.PoiType_Kolbeh: return rType == 1?R.drawable.ac_house:rType == 2?R.color.colorGrey:R.drawable.ic_circle_redyellow_line;


        }

        return rType == 1?R.drawable.ac_waypoint:R.color.colorOrange1;
    }

    public class Enums{
        public static final byte ShowStatus_None = 0;
        public static final byte ShowStatus_Show = 1;
        public static final byte ShowStatus_Hide = 2;

        public static final short PoiType_None = 0;
        public static final short PoiType_Folder = 1;
        public static final short PoiType_Track = 2;
        public static final short PoiType_Route = 3;
        public static final short PoiType_Map = 4;
        public static final short PoiType_Waypoint = 100;
        public static final short PoiType_Peak = 101;
        public static final short PoiType_Shelter1 = 103;//Janpanah
        public static final short PoiType_Shelter2 = 104;//Panahgah
        public static final short PoiType_Shelter3 = 105;//Gharargah
        public static final short PoiType_SkyResort = 106;
        public static final short PoiType_Camp = 107;
        public static final short PoiType_GelFeshan = 108;
        public static final short PoiType_AtashFeshan = 109;
        public static final short PoiType_Ghar = 110;
        public static final short PoiType_Pass = 111;
        public static final short PoiType_Bridge = 112;

        public static final short PoiType_Danger_General = 200;
        public static final short PoiType_Danger_Avalanche = 201;
        public static final short PoiType_Danger_Stone = 202;
        public static final short PoiType_Danger_Lost = 203;
        public static final short PoiType_Danger_Fall = 204;
        public static final short PoiType_Danger_Flood = 205;

        public static final short PoiType_WaterSource = 900;
        public static final short PoiType_Manbae = 901;
        public static final short PoiType_Jooy = 902;
        public static final short PoiType_Rood = 903;
        public static final short PoiType_Berkeh = 904;
        public static final short PoiType_Daryache = 905;
        public static final short PoiType_Abshar = 906;

        public static final short PoiType_City = 1000;
        public static final short PoiType_Town = 1001;
        public static final short PoiType_Villate = 1002;
        public static final short PoiType_Mahaleh = 1003;
        public static final short PoiType_Kolbeh = 1004;

    }


}