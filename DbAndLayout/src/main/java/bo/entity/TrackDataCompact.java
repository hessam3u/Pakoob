package bo.entity;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import maptools.GeoCalcs;

public class TrackDataCompact {
    @SerializedName(value="Points", alternate = {"points"})
    public List<LatLng> Points= new ArrayList<>();
    @SerializedName(value="Elev", alternate = {"elev"})
    public List<Float> Elev= new ArrayList<>();
    public List<Float> ElevSmoothed = null;
    public List<LatLng> bounds;

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
//        boolean hasTime = true;
double distance = 0;
        for (int i = 0; i < dataSize; i++) {
            if (i > 0) {
                pElev = cElev;
                pPoint = cPoint;
                pTime = cTime;
            }
            cElev = this.Elev.get(i);
            cPoint = this.Points.get(i);
            //نمیدونم داشتن زمان به چه درد میخورد، من کامنتش کردم در شهریور 1403 ولی شاید لازم باشه
//            cTime = hasTime && this.Time.get(i) != null ? this.Time.get(i).getTimeInMillis() : 0;
//            if (cTime == 0)
//                hasTime = false;
            if (i == 0)
                continue;
            Double diffDistance = GeoCalcs.distanceBetweenMeteres(pPoint.latitude, pPoint.longitude, cPoint.latitude, cPoint.longitude);

            if (diffDistance.isNaN())
                continue;
            float diffElev = cElev - pElev;
            distance += diffDistance;
        }
        return distance;
    }

    public List<Float> calcSmoothedElevation(boolean forceRecalculate){
        if (!forceRecalculate && ElevSmoothed != null)
            return ElevSmoothed;
        this.ElevSmoothed = smoothElevation(this.Elev, 5, true);
        return this.ElevSmoothed;
    }
    public static List<Float> smoothElevation(List<Float> elevations, int windowSize, boolean skipZeros) {
        List<Float> smoothed = new ArrayList<>();
        int halfWindowSize = windowSize / 2;
        int elevSize = elevations.size();
        for (int i = 0; i < elevations.size(); i++) {
            float sum = 0;
            int count = 0;

            for (int j = Math.max(0, i - halfWindowSize); j <= Math.min(elevSize - 1, i + halfWindowSize); j++) {
                float elev = elevations.get(j);
                if (skipZeros && elev == 0){
                    continue;
                }
                else {
                    sum += elev;
                    count++;
                }
            }
            smoothed.add(count> 0?(sum / count): 0);
        }
        return smoothed;
    }
    public boolean hasZeroElevation(){
        int ptSize = Points.size();
        if (ptSize > 0 && ptSize != Elev.size())
            return true;
        boolean hasZero = false;
        int counter = 0;
        int maxCounter = 0;
        for (int i = 0; i < Elev.size(); i++) {
            if (counter > 5)
                return true;
            if (Elev.get(i) == 0)
                counter++;
            else{
                if (counter > maxCounter)
                    maxCounter = counter;
                counter = 0;
            }
        }
        if (counter > maxCounter)
            maxCounter = counter;
        if (maxCounter > 5 || maxCounter > (ptSize/100f))
            return true;
        return false;
    }

}
