package mojafarin.pakoob.mainactivitymodes;

import android.graphics.Color;
import android.view.View;

import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bo.entity.NbPoi;
import maptools.MapNavigateToPoint;
import mojafarin.pakoob.MainActivity;
import mojafarin.pakoob.MapPage;
import mojafarin.pakoob.R;
import maptools.GeoCalcs;
import utils.projectStatics;

public class GoToTargetMode {
    MainActivity activity;
    MapNavigateToPoint viewNagivateTo;
    Marker destMarker = null;
    Polyline destLine = null;
    List<PatternItem> destLinePattern;

    //Navigating To Target:
    public static double TargetNext_DegreeToWithDeltaAngle = 0;
    public static double TargetNext_DegreeTo = 0;
    public static LatLng TargetNext_LatLon = null;//new LatLng(36.25, 59.5);
    public static boolean Navigating = false;
    public static NbPoi TargetPoi;
    public static int TargetIndex = 0;
    public static String TargetNext_DistanceFriendly = "";

    public GoToTargetMode(MainActivity mainActivity){
        activity = mainActivity;

        viewNagivateTo = activity.findViewById(R.id.viewNagivateTo);
        viewNagivateTo.setOnClickListener(view -> {
            viewNagivateTo_Click();
        });
    }

    public void initNavigateToPoint(LatLng targetNext_LatLon, NbPoi targetPoi, int targetIndex) {
        TargetNext_LatLon = targetNext_LatLon;
        TargetPoi = targetPoi;
        TargetIndex = targetIndex;
        viewNagivateTo.setVisibility(View.VISIBLE);
        Navigating = true;
        if (destMarker != null)
            destMarker.remove();
        destMarker = MainActivity.map.addMarker(new MarkerOptions().position(TargetNext_LatLon)
                .title(TargetPoi!= null && TargetPoi.Name != null && TargetPoi.Name.length() > 0?TargetPoi.Name:"هدف"));
        if (destLinePattern == null)
            destLinePattern = Arrays.asList(new Dot(), new Gap(20), new Dash(30), new Gap(20));
        showNavigation();
        viewNagivateTo.invalidate();
        redrawPolyline();
    }

    public void showNavigation() {
        TargetNext_DistanceFriendly = GeoCalcs.distanceBetweenFriendly(MainActivity.currentLatLon.latitude, MainActivity.currentLatLon.longitude, TargetNext_LatLon.latitude, TargetNext_LatLon.longitude);
        TargetNext_DegreeTo = GeoCalcs.GetAzimuthInDegree(MainActivity.currentLatLon, TargetNext_LatLon);
        TargetNext_DegreeToWithDeltaAngle = TargetNext_DegreeTo - MapPage.angle  /*+ app.declination1400-01-03 added*/;
        viewNagivateTo.invalidate();
        redrawPolyline();
    }

    void redrawPolyline(){
        List<LatLng> track = new ArrayList<>();
        track.add(MainActivity.currentLatLon);
        track.add(TargetNext_LatLon);

        if (destLine == null) {
            destLine = MainActivity.map.addPolyline(new PolylineOptions()
                    .addAll(track)
                    .width(10)
                    .color(Color.MAGENTA)
                    .pattern(destLinePattern)
                    .zIndex(100000));
        }
        else{
            destLine.setPoints(track);
        }
    }

    public void viewNagivateTo_Click() {

        projectStatics.showDialog(activity
                , activity.getResources().getString(R.string.StopNavigation_Title)
                , activity.getResources().getString(R.string.StopNavigation_Desc)
                , activity.getResources().getString(R.string.ok), view -> {
                    discardNavigateToPoint();
                }
                , activity.getResources().getString(R.string.cancel), null);
    }

    public void discardNavigateToPoint() {
        Navigating = false;
        viewNagivateTo.setVisibility(View.GONE);
        TargetIndex = 0;
        TargetNext_LatLon = null;
        TargetPoi = null;
        destMarker.remove();
        destMarker = null;
        destLine.remove();
        destLine = null;
    }

}
