package bo.entity;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import bo.sqlite.NbMapSQLite;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Entity
public class NbCurrentTrack
{
    @PrimaryKey(autoGenerate = true)
    @SerializedName(value="NbCurrentTrackId", alternate = {"nbCurrentTrackId", "id"})
    public Integer NbCurrentTrackId;
    @SerializedName(value="Latitude", alternate = {"latitude", "la"})
    @NonNull
    public Double Latitude;
    @SerializedName(value="Longitude", alternate = {"longitude", "lo"})
    @NonNull
    public Double Longitude;
    @SerializedName(value="Time", alternate = {"time", "t"})
    @NonNull
    public Long Time;
    @SerializedName(value="Elevation", alternate = {"elevation", "e"})
    @NonNull
    public Float Elevation;

    public LatLng getLatLon(){
        return new LatLng(Latitude, Longitude);
    }
    public boolean IsPause(){
        return Latitude == 0 && Longitude == 0 && Time == 0 && Elevation == 0;
    }
    public static NbCurrentTrack getPauseObject(){
        NbCurrentTrack currentTrack = new NbCurrentTrack();
        currentTrack.Elevation = 0f;
        currentTrack.Time = 0l;
        currentTrack.Latitude = 0d;
        currentTrack.Longitude = 0d;
        return currentTrack;
    }
}