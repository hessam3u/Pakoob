package bo.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

//ver 29:
public class PakoobSync {
    @SerializedName(value="llt")
    public double LastLat;
    @SerializedName(value="llo")
    public double LastLon;
    @SerializedName(value="ftp")
    public byte LastAproxLocationFixType;
    @SerializedName(value="ftm")
    public String LastAproxLocationFixTime;
    @SerializedName(value="ex")
    public List<TTExceptionLog> exceptions;
    @SerializedName(value="sl")
    public List<NbLogSearch> searchlog;
    @SerializedName(value="st")
    public List<NbScreenTime> screenTimes;

    public static PakoobSync getInstance(double LastLat, double LastLon, String LastAproxLocationFixTime, byte LastAproxLocationFixType, List<TTExceptionLog> exceptions, List<NbLogSearch> searchlog, List<NbScreenTime> screenTimes) {
        PakoobSync res = new PakoobSync();
        res.LastLat = LastLat;
        res.LastLon = LastLon;
        res.LastAproxLocationFixType = LastAproxLocationFixType;
        res.LastAproxLocationFixTime = LastAproxLocationFixTime;
        res.exceptions = exceptions;
        res.searchlog = searchlog;
        res.screenTimes = screenTimes;
        return res;
    }
}
