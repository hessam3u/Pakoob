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

@Entity
public class NbPoiCompact
{
    public Long NbPoiId;
    public String Name;
    public long ParentId;
    public String Address;
    public Double LatS;
    public Double LonW;
    public Double LatN;
    public Double LonE;
    public int Color;
    public byte ShowStatus;
    public short PoiType;
    public byte ZoomMin;
    public byte ZoomMax;
    public byte DisplaySize;

    public Polyline polyLine = null;
    public Marker marker = null;

    public NbPoi getNbPoi(){
        NbPoi res = new NbPoi();
//        res.NbPoiId = 0l; //1401-05-13 تغییر دادم که بتونم بفهمم کدوم نقطه در حال ویرایش هست
        res.NbPoiId = NbPoiId;
        res.PoiType = this.PoiType;
        res.Name = this.Name;
        res.ShowStatus = this.ShowStatus;
        res.Address = this.Address;
        res.Color = this.Color;
        res.LatS = this.LatS;
        res.LatN = this.LatN;
        res.LonE = this.LonE;
        res.LonW = this.LonW;
        res.ZoomMin = this.ZoomMin;
        res.ZoomMax = this.ZoomMax;
        res.DisplaySize = this.DisplaySize;
        res.ParentId = this.ParentId;

        return res;
    }

    public static NbPoiCompact getInstance(NbPoi inp){
        NbPoiCompact res= new NbPoiCompact();
        res.NbPoiId = inp.NbPoiId;
        res.Name = inp.Name;
        res.ParentId = inp.ParentId;
        res.Address = inp.Address;
        res.Color = inp.Color;
        res.ShowStatus = inp.ShowStatus;
        res.PoiType = inp.PoiType;
        res.ZoomMin = inp.ZoomMin;
        res.ZoomMax = inp.ZoomMax;
        res.DisplaySize = inp.DisplaySize;
        res.LatS = inp.LatS;
        res.LatN = inp.LatN;
        res.LonE = inp.LonE;
        res.LonW = inp.LonW;

        return res;
    }
}