package bo.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.Calendar;

import utils.MyDate;
import utils.hutilities;

@Entity(
        tableName = "nb_screen_time",
        indices = {
                @Index("screenId"),
                @Index("startTime"),
                @Index("endTime")
        }
)
public class NbScreenTime {
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    public long NbScreenTimeId;

    @SerializedName("si")
    public int screenId;

    @SerializedName("cm")
    @NonNull
    public String command;

    @SerializedName("st")
    public long startTime; // millis

    @SerializedName("et")
    public long endTime;   // millis

    public static NbScreenTime getInstance(int screenId, String Command, long startTime, long endTime){
        NbScreenTime res = new NbScreenTime();
        res.screenId = screenId;
        res.command = Command;
        res.startTime = startTime;
        res.endTime = endTime;
        return res;
    }

    public NbScreenTime() {
    }

    public static NbScreenTime fromBytes(byte[] bts){
        Gson gson = new Gson();
        String json = hutilities.decryptBytesToString(bts);
        return gson.fromJson(json, NbScreenTime.class);
    }
    public String toString(){
        return "";
    }

}
