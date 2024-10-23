package maptools;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;
import java.util.List;

import bo.entity.NbCurrentTrack;
import bo.entity.TrackDataCompact;

public class TrackProperties {
    public int dataSize = 0;
    public LatLng cPoint = null;
    public LatLng pPoint = null;
    public long cTime = 0;
    public long pTime = 0;
    public float cElev = -1000;
    public float pElev = -1000;
    public double totalAscent = 0;
    public double totalDecent = 0;
    public long totalTime = 0;
    public long startDateTimeMils = 0;
    public long movingMs = 0;
    public long stoppedMs = 0;
    public double cSpeed = 0;
    public double maxSpeed = 0;
    public double minSpeed = 0;
    public long ascentMs = 0;
    public long decentMs = 0;
    public double distance = 0;
    public double ascentDistance = 0;
    public double descentDistance = 0;
    public float maxElevation = MAX_ELEVATION_DEFAULT;
    public float minElevation = MIN_ELEVATION_DEFAULT;

    public static final int MIN_ELEVATION_DEFAULT = 1000000;
    public static final int MAX_ELEVATION_DEFAULT = -1000000;

    public void AddNewPoint(LatLng nPoint, Calendar nTime, float nElev){
        int i = dataSize + 1;
        if (i > 0){
            pElev = cElev;
            pPoint = cPoint;
            pTime = cTime;
        }
        boolean hasTime = nTime != null;
        boolean hasElev = false;
        cElev = nElev;
        cPoint = nPoint;
        cTime = hasTime && nTime != null ? nTime.getTimeInMillis():0;
        if (cElev != 0)
            hasElev = true;
        if (i == 0)
            return;
        if (cElev > maxElevation)
            maxElevation = cElev;
        if (cElev < minElevation)
            minElevation = cElev;

        Double diffDistance = GeoCalcs.distanceBetweenMeteres(pPoint.latitude, pPoint.longitude, cPoint.latitude, cPoint.longitude);

        if (diffDistance.isNaN())
            return;
        float diffElev = cElev - pElev;
        long diffTime = cTime - pTime;
        if (diffTime < 1000)//1401-10
            diffTime = 0;

        totalTime += diffTime;

//            if (Double.isNaN(diffElev) || Double.isNaN(distance)){
//                int x = 0;
//                continue;
//            }
        distance += diffDistance;
        if (diffElev > 0) {
            totalAscent += diffElev;
            ascentDistance += diffDistance;
            ascentMs += diffTime;
        }
        else if(diffElev < 0){
            diffElev *= -1;
            totalDecent += diffElev;
            descentDistance += diffDistance;
            decentMs += diffTime;
        }
        else{
            //???
        }
        if (diffTime > 0){
            cSpeed = diffDistance / (diffTime / 1000);
            if (cSpeed > maxSpeed)
                maxSpeed = cSpeed;
        }
    }

    private void initFromAnySource(List<NbCurrentTrack> currentTracks, TrackData data){
        dataSize = currentTracks == null? data.Points.size(): currentTracks.size();
        boolean hasTime = currentTracks == null?(data.Time.size() > 0): true;
        boolean hasElev = false;
        boolean TrackIsPaused = false;
        for (int i = 0; i < dataSize; i++) {
            NbCurrentTrack currentTrack = null;
            if (currentTracks != null)
                currentTrack = currentTracks.get(i);
            if (i > 0 && !TrackIsPaused){ //1400-11-04  && !TrackIsPaused
                pElev = cElev;
                pPoint = cPoint;
                pTime = cTime;
            }

            List<Float> elevS ;
            elevS = currentTrack == null? TrackDataCompact.smoothElevation(data.Elev, 5, true):null;

            cElev = currentTrack == null?elevS.get(i):currentTrack.Elevation;
            cPoint = currentTrack == null?data.Points.get(i):currentTrack.getLatLon();
            cTime = hasTime &&  currentTrack == null?(data.Time.get(i) != null ? data.Time.get(i).getTimeInMillis():0) :(currentTrack.Time) ;

            if (cElev != 0)
                hasElev = true;
            //1400-11-04 find Pause Track
            if (cElev == 0 && cPoint.longitude == 0 && cPoint.latitude == 0 && cTime == 0){
                Log.e("پراپرتیس", "رکورد خالی و توقف");
                TrackIsPaused = true;//1400-11-04
                continue;
            }
            if (TrackIsPaused) {
                Log.e("پراپرتیس", "Track Pause Bood");
                TrackIsPaused = false;//1400-11-04
                continue;
            }
            if (hasTime && startDateTimeMils == 0 && cTime != 0)
                startDateTimeMils = cTime;
            if (i == 0)
                continue;

            if (cElev > maxElevation)
                maxElevation = cElev;
            if (cElev < minElevation)
                minElevation = cElev;

            Double diffDistance = GeoCalcs.distanceBetweenMeteres(pPoint.latitude, pPoint.longitude, cPoint.latitude, cPoint.longitude);
            if (diffDistance.isNaN())
                continue;
            float diffElev = cElev - pElev;
            long diffTime = cTime - pTime;
            if (diffTime < 1000)//1401-10
                diffTime = 0;

            totalTime += diffTime;

//            if (Double.isNaN(diffElev) || Double.isNaN(distance)){
//                int x = 0;
//                continue;
//            }
            distance += diffDistance;
            if (diffElev > 0) {
                totalAscent += diffElev;
                ascentDistance += diffDistance;
                ascentMs += diffTime;
            }
            else if(diffElev < 0){
                diffElev *= -1;
                totalDecent += diffElev;
                descentDistance += diffDistance;
                decentMs += diffTime;
            }
            else{
                //???
            }
            if (diffTime > 0){
                cSpeed = diffDistance / (diffTime / 1000);
                if (cSpeed > maxSpeed)
                    maxSpeed = cSpeed;
            }
        }
    }

    public void initFromNbCurrentTrack(List<NbCurrentTrack> currentTracks){
        initFromAnySource(currentTracks, null);
    }
    public void initFromTrackData(TrackData data){
        initFromAnySource(null, data);
    }

}
