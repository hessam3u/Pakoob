package maptools;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import androidx.appcompat.widget.MenuItemHoverListener;
import androidx.constraintlayout.solver.widgets.Rectangle;
import utils.MyDate;

public class TrackData {
    public List<LatLng> Points= new ArrayList<>();
    public List<Calendar> Time= new ArrayList<>();
    public List<Float> Elev= new ArrayList<>();
    public List<Float> Temperator= new ArrayList<>();
    public String Name;
    public int Color;
    public String ColorString;
    public String Url;
    public boolean ShowOnMap = false;
    public boolean IsRoute = false;
    List<LatLng> bounds;

    public List<LatLng> calculateBounds(){
        double s = 0;
        double n= 0;
        double e= 0;
        double w= 0;
        int ptCount = this.Points.size();
        for (int i = 0; i < ptCount; i++) {
            LatLng ll = this.Points.get(i);
            //1400-11-25 for preventing Pause Jump added
            if(ll.latitude == 0 && ll.longitude == 0){
                continue;
            }
            if (i == 0){
                s = n = ll.latitude;
                e = w = ll.longitude;
            }
            else{
                if (s > ll.latitude)
                    s = ll.latitude;
                if (n < ll.latitude)
                    n = ll.latitude;
                if (w > ll.longitude)
                    w = ll.longitude;
                if (e < ll.longitude)
                    e = ll.longitude;
            }
        }

        List<LatLng> res = new ArrayList<>();
        res.add(new LatLng(s, w));
        res.add(new LatLng(n, e));
        this.bounds = res;
        return res;
    }
    public StringBuilder getTrackFileContent(){
        int boundsSize = this.Points.size();
        int elevSize = this.Elev.size();
        int timeSize = this.Time.size();
        StringBuilder fileContent = new StringBuilder();
        for (int j = 0; j < boundsSize; j++) {
            String latitude = GPXFile.DoubleToStringForLatLan(this.Points.get(j).latitude);
            String longitude = GPXFile.DoubleToStringForLatLan(this.Points.get(j).longitude);
            long milSeconds = 0;
            if (timeSize > j) {
                Calendar time =this.Time.get(j);
                if (time != null)
                    milSeconds = time.getTimeInMillis();
            }
            String timeToSave= milSeconds != 0 ?String.valueOf(milSeconds):"";
            fileContent.append(latitude+ "," + longitude + "," + (elevSize > j?((int)this.Elev.get(j).floatValue()):"") + "," + timeToSave);
            fileContent.append('\n');
        }
        return fileContent;
    }
    public static TrackData readTrackData(String Address, TimeZone outputTimezoneNotWorkingForNow){
        TrackData res = new TrackData();
        File ff = new File(Address);

        StringBuilder text = new StringBuilder();
//        if (timeZone == null) {
//            //timeZone = TimeZone.getTimeZone("GMT+0330");
//            timeZone = TimeZone.getTimeZone("IST");
//        }
        TimeZone utc = TimeZone.getTimeZone("UTC");

        try {
            BufferedReader br = new BufferedReader(new FileReader(ff));
            String line;
int lineCounter = 0;
            while ((line = br.readLine()) != null) {
                String[] split = line.split(",");
                res.Points.add(new LatLng(Double.parseDouble(split[0]), Double.parseDouble(split[1])));
                if (split.length > 2 && split[2].length() > 0) {
                    res.Elev.add(Float.parseFloat(split[2]));
                }
                else{
                    res.Elev.add(0f);
                }
                long milSeconds = 0;
                if (split.length > 3 && split[3].length() > 0) {
                    milSeconds = Long.parseLong(split[3]);
                }
                if (milSeconds == 0){
                    res.Time.add(null);
                }
                else {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeZone(utc);
                    calendar.setTimeInMillis(milSeconds);
                    res.Time.add(calendar);
                }
                lineCounter++;
            }
            br.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            //You'll need to add proper error handling here
        }
        return res;
    }
    public static List<LatLng> readTrackData_LatLng(String Address){
        List<LatLng> res = new ArrayList<>();
        File ff = new File(Address);

        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(ff));
            String line;

            while ((line = br.readLine()) != null) {
                String[] split = line.split(",");
                LatLng latLngToAdd =new LatLng(Double.parseDouble(split[0]), Double.parseDouble(split[1]));
                //1400-11-25 for Skip Paused Tracks Jumps
                if (latLngToAdd.longitude == 0 && latLngToAdd.latitude == 0){
                    continue;
                }
                res.add(latLngToAdd);
            }
            br.close();
        }
        catch (Exception e) {
            //You'll need to add proper error handling here
        }
        return res;
    }
    public static List<String[]> readTrackData_IntoGrid(String Address){
        List<String[]> res = new ArrayList<>();
        File ff = new File(Address);

        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(ff));
            String line;

            while ((line = br.readLine()) != null) {
                String[] split = line.split(",");
                res.add(split);
            }
            br.close();
        }
        catch (Exception e) {
            //You'll need to add proper error handling here
        }
        return res;
    }
    public double GetDistanceInMeter(){
        int dataSize= Points.size();
        LatLng cPoint = null;
        LatLng pPoint = null;
        long cTime = 0;
        long pTime = 0;
        float cElev = -1000;
        float pElev = -1000;
        boolean hasTime = true;
double distance = 0;
        for (int i = 0; i < dataSize; i++) {
            if (i > 0) {
                pElev = cElev;
                pPoint = cPoint;
                pTime = cTime;
            }
            cElev = this.Elev.get(i);
            cPoint = this.Points.get(i);
            cTime = hasTime && this.Time.get(i) != null ? this.Time.get(i).getTimeInMillis() : 0;
            if (cTime == 0)
                hasTime = false;
            if (i == 0)
                continue;
            Double diffDistance = hMapTools.distanceBetweenMeteres(pPoint.latitude, pPoint.longitude, cPoint.latitude, cPoint.longitude);

            if (diffDistance.isNaN())
                continue;
            float diffElev = cElev - pElev;
            distance += diffDistance;
        }
        return distance;
    }
}
