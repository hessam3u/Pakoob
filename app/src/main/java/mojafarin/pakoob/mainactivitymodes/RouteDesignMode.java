package mojafarin.pakoob.mainactivitymodes;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import bo.entity.NbPoi;
import bo.entity.NbPoiCompact;
import maptools.GPXFile;
import maptools.TrackData;
import mojafarin.pakoob.MainActivity;
import mojafarin.pakoob.MapPage;
import mojafarin.pakoob.R;
import mojafarin.pakoob.app;
import maptools.GeoCalcs;
import utils.MyDate;
import utils.hutilities;
import utils.projectStatics;
import static mojafarin.pakoob.MainActivity.map;
import static mojafarin.pakoob.MapPage.RouteMode;
import static mojafarin.pakoob.MainActivity.cameraLatLon;
import static mojafarin.pakoob.MapPage.MAP_CLICK_MODE_ROUTE;
import static mojafarin.pakoob.MapPage.MAP_CLICK_MODE_NONE;

public class RouteDesignMode {
    MainActivity activity;
    public RouteDesignMode(MainActivity mainActivity){
        activity = mainActivity;
        initializeComponentes();

    }

    private void initializeComponentes() {
        pnlRouteDesign = activity.findViewById(R.id.pnlRouteDesign);
    }
    public List<Marker> routeMarkers = new ArrayList<>();
    public List<String> routeMarkerIds = new ArrayList<>();
    public int routeCurrentIndex = -1;
    public Polyline currentLine = null;
    //List<LatLng> routePoints = new ArrayList<>();
    List<Polyline> routePolys = new ArrayList<>();
    BitmapDescriptor currentRouteIcon = null, oldRouteIcons = null;
    TextView txtRouteDistance, txtArea, txtRoutePointCount, txtYouAreDesigningRoute, btnDeleteLastRoutePoint, btnSaveRoute, btnDiscardRoute, txtRouteDistancePointToPrev, btnAddToRoute;
    public View pnlRouteDesign;
    public long editingRouteId = 0;
    public boolean editingRouteIsVisible = false;
    public NbPoi editingRoute = null;
    public void initRouteDesignPanel() {
        if (txtRouteDistance == null) {
            txtRouteDistance = activity.findViewById(R.id.txtRouteDistance);
            txtArea = activity.findViewById(R.id.txtArea);
            txtRoutePointCount = activity.findViewById(R.id.txtRoutePointCount);
            txtYouAreDesigningRoute = activity.findViewById(R.id.txtYouAreDesigningRoute);
            btnDeleteLastRoutePoint = activity.findViewById(R.id.btnDeleteLastRoutePoint);
            btnSaveRoute = activity.findViewById(R.id.btnSaveRoute);
            btnDiscardRoute = activity.findViewById(R.id.btnDiscardRoute);
            txtRouteDistancePointToPrev = activity.findViewById(R.id.txtRouteDistancePointToPrev);
            btnAddToRoute = activity.findViewById(R.id.btnAddToRoute);
            btnAddToRoute.setOnClickListener(view -> {
                mapOnClick_Route(map.getCameraPosition().target);
            });

            btnDeleteLastRoutePoint.setOnClickListener(view -> {
                deleteRouteCurrent();
            });
            btnDiscardRoute.setOnClickListener(view -> {
                discardRoute(false);
            });
            btnSaveRoute.setOnClickListener(view -> {
                saveRoute();
            });
        }
    }

    public void mapOnClick_Route(LatLng latLng) {
        if (currentRouteIcon == null) {
            currentRouteIcon = hutilities.bitmapDescriptorFromVector(activity, R.drawable.ic_mosalasred);
            oldRouteIcons = hutilities.bitmapDescriptorFromVector(activity, R.drawable.ic_mosalasblue);
        }


        LatLng oldDrawPath = null;//new LatLng(0, 0);
        //routePoints.add(latLng);
        if (routeMarkers.size() == 0) {
            oldDrawPath = latLng;
        } else {
            oldDrawPath = routeMarkers.get(routeCurrentIndex).getPosition();

            //Validation
            if (routeCurrentIndex != -1){
                if (oldDrawPath.latitude == latLng.latitude && oldDrawPath.longitude == latLng.longitude){
                    return;
                }
            }

            List<LatLng> nRoutePoints = new ArrayList<>();
            nRoutePoints.add(oldDrawPath);
            nRoutePoints.add(latLng);
            Polyline pl = getRoutePolyline(nRoutePoints);
            routePolys.add(routeCurrentIndex, pl);
            //Dar soorati ke Dashtim tooye Route INSERT mikardim, bayad Route-e baedi ham taghir kone (dar soorate vojood)
            if (routeCurrentIndex < routePolys.size() - 1){
                Polyline pToRemove = routePolys.get(routeCurrentIndex+1);
                pToRemove.remove();
                List<LatLng> insertedPoints = new ArrayList<>();
                insertedPoints.add(latLng);
                insertedPoints.add(routeMarkers.get(routeCurrentIndex + 1).getPosition());
                Polyline pToInsert = getRoutePolyline(insertedPoints);
                routePolys.set(routeCurrentIndex+1, pToInsert);
            }
        }
        routeCurrentIndex++;

        //added 1401-07-28 for :Attempt to read from field 'double ...
        if (latLng == null)
            return;
        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(latLng.latitude, latLng.longitude))
                .title(String.valueOf(routeMarkers.size() + 1))
                .draggable(true)
                .zIndex(10000)
                .icon(currentRouteIcon)
                .flat(true);
        Marker marker = map.addMarker(markerOptions);
        routeMarkers.add(routeCurrentIndex, marker);
        routeMarkerIds.add(routeCurrentIndex, marker.getId());

        //Taghire Icon-e item-e Ghabli
        if (routeCurrentIndex > 0) {
            routeMarkers.get(routeCurrentIndex - 1).setIcon(oldRouteIcons);
        }
        resetRouteMarkerTitles();
        resetRouteCounters();
        route_CalculateDistanceFromPrev();

        //برای خوشگل کاری طراحی مسیر
        resetCurrentLine();
    }
    public void deleteRouteCurrent(){
        if (routeCurrentIndex == -1)
            return;
        //Har chi ro click kard, bayad Poly-e ghabl va baedesh pak she
        int size = routePolys.size();
        int size_markers = routeMarkers.size();
        Polyline prev = (routeCurrentIndex - 1) < size && routeCurrentIndex - 1 >= 0 ? routePolys.get( routeCurrentIndex - 1) : null;
        Polyline next = routeCurrentIndex < size ? routePolys.get(routeCurrentIndex) : null;
        if (next != null){
            next.remove();
            routePolys.remove(routeCurrentIndex);
        }
        if (prev != null){
            prev.remove();
            routePolys.remove(routeCurrentIndex - 1);
        }
        if (routeCurrentIndex != 0 && routeCurrentIndex != size){
            List<LatLng> nRoutePoints = new ArrayList<>();
            nRoutePoints.add(routeMarkers.get(routeCurrentIndex - 1).getPosition());
            nRoutePoints.add(routeMarkers.get(routeCurrentIndex + 1).getPosition());
            Polyline pl = getRoutePolyline(nRoutePoints);
            routePolys.add(routeCurrentIndex - 1, pl);
        }
        routeMarkers.get(routeCurrentIndex).remove();
        routeMarkers.remove(routeCurrentIndex);
        routeMarkerIds.remove(routeCurrentIndex);

        resetRouteMarkerTitles();
        resetRouteCounters();

        if (routeCurrentIndex == 0 && routeCurrentIndex < size_markers - 1){
            markerClick_Route(routeMarkers.get(routeCurrentIndex), true);
        }
        else if (routeCurrentIndex > 0){
            markerClick_Route(routeMarkers.get(routeCurrentIndex - 1), true);
        }
        else {
            routeCurrentIndex = -1; //No Items Remaining

            //خوشگل کاری رو پاک کنیم
            resetCurrentLine();
        }
        route_CalculateDistanceFromPrev();

    }
    public void saveRoute() {
        //خوشگل کاری طراحی مسیر با حرکت دوربین
        resetCurrentLine();

        TrackData data = new TrackData();
        short poiType = NbPoi.Enums.PoiType_Route;
        Random rand = new Random();
        if (editingRoute != null) {
            poiType = editingRoute.PoiType;
        }

        if (editingRoute == null || editingRoute.Name == null || editingRoute.Name.length() == 0){
            data.Color = GPXFile.RandomColors.get(rand.nextInt(GPXFile.colorCount));
            Calendar now = Calendar.getInstance();
            data.Name = activity.getResources().getString(R.string.Design) + " " + MyDate.CalendarToPersianDateString(now, MyDate.DateToStringFormat.yyyymmdd, "")//Integer.toString(jalaliDate.getYear())+ Integer.toString(jalaliDate.getMonthPersian().getValue())+ Integer.toString(jalaliDate.getDay())
                    +"-"+ MyDate.CalendarToTimeString(now, MyDate.TimeToStringFormat.HourMinSec, "");//Integer.toString(now.get(Calendar.HOUR_OF_DAY))+ Integer.toString(now.get(Calendar.MINUTE))+ Integer.toString(now.get(Calendar.SECOND));
        }
        else{
            data.Color = editingRoute.Color;
            data.Name = editingRoute.Name;
        }
        for (int i = 0; i < routeMarkers.size(); i++) {
            data.Points.add(routeMarkers.get(i).getPosition());
        }
        if (data.Points.size() < 2){
            projectStatics.showDialog(activity
                    , activity.getResources().getString(R.string.vali_GeneralError_Title)
                    , activity.getResources().getString(R.string.vali_RouteMustHavePoints)
                    , activity.getResources().getString(R.string.ok)
                    , null
                    , ""
                    , null);
            return;
        }
        try {
            //ذخیره و اضافه کردن به نقشه در تابع زیر
            NbPoi saved = GPXFile.SaveDesignedRouteToDb(editingRouteId, poiType,data, activity);
            if (saved.NbPoiId != 0){
                //Not working after fragmenting...
                activity.openEditTrack(saved.NbPoiId, "MainActivity", 0, null);

                //حذف نسخه قبلی هم از ظاهر هم از حافظه
                if (editingRoute != null) {
                    app.removeInVisiblePois(editingRoute.NbPoiId);//1404-09-03
                }

                discardRoute(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
            projectStatics.showDialog(activity
                    , activity.getResources().getString(R.string.vali_SaveInFileError)
                    , activity.getResources().getString(R.string.vali_SaveInFileError_Desc) + e.getMessage()
                    , activity.getResources().getString(R.string.ok)
                    , null
                    , ""
                    , null);
        }
    }

    public void discardRoute(boolean firedAfterSave) {
        if (RouteMode != MAP_CLICK_MODE_ROUTE)
            return;
        if (!firedAfterSave && editingRouteId != 0 && editingRouteIsVisible){
            NbPoiCompact compact = app.findInVisiblePois(editingRouteId);
            if (compact != null) {
                compact.polyLine.setVisible(true);
                //MapPage.addPOIToMap(compact, map, true, activity);//1404-09-03
                //
            }
        }
        for (int i = 0; i < routeMarkers.size(); i++) {
            routeMarkers.get(i).remove();
        }
        routeMarkers.clear();
        routeMarkerIds.clear();
        for (int i = 0; i < routePolys.size(); i++) {
            routePolys.get(i).remove();
        }
        editingRoute = null;
        editingRouteId = 0;
        editingRouteIsVisible = false;
        routePolys.clear();
        routeCurrentIndex = -1;
        RouteMode = MAP_CLICK_MODE_NONE;
        pnlRouteDesign.setVisibility(View.GONE);
        txtRoutePointCount.setText("0");
        txtRouteDistance.setText("0m");
        txtArea.setText("0m");
        txtRouteDistancePointToPrev.setText("0m");
        resetCurrentLine();
    }

    public void resetRouteMarkerTitles(){
//        routeMarkerIds.clear();
        for (int i = 0; i < routeMarkers.size(); i++) {
            Marker marker = routeMarkers.get(i);
            marker.setTitle(Integer.toString(i + 1));
//            routeMarkerIds.add(marker.getId());
        }
    }
    public void resetRouteCounters(){
        double distance = 0;
        List<LatLng> latLngs = new ArrayList<>();
        LatLng oldLatLong = null;
        LatLng currentLatLong = null;
        for (int i = 0; i < routeMarkers.size(); i++) {
            oldLatLong = currentLatLong;
            currentLatLong = routeMarkers.get(i).getPosition();
            latLngs.add(currentLatLong);
            if (i > 0){
                LatLng current = currentLatLong;//routeMarkers.get(i).getPosition();
                LatLng prev = oldLatLong;//routeMarkers.get(i - 1).getPosition();
                distance += GeoCalcs.distanceBetweenMeteres(current.latitude, current.longitude, prev.latitude, prev.longitude);
            }
        }
        txtRoutePointCount.setText(Integer.toString(routeMarkers.size()));
        txtRouteDistance.setText(GeoCalcs.distanceBetweenFriendly(distance));
        txtArea.setText(GeoCalcs.areaFriendly(GeoCalcs.calculateArea(latLngs)));
    }

    public Polyline getRoutePolyline(List<LatLng> routePoints) {
        Polyline pl = map.addPolyline(new PolylineOptions().width(10).color(Color.RED).geodesic(true).zIndex(5));
        pl.setPoints(routePoints);
        return pl;
    }
    public void route_CalculateDistanceFromPrev() {
        if (RouteMode == MAP_CLICK_MODE_ROUTE) {
            if (routeCurrentIndex == -1){
                txtRouteDistancePointToPrev.setText("0m");
                return;
            }
            else{
                if (cameraLatLon == null){
                    projectStatics.showDialog(activity, activity.getResources().getString(R.string.map_NotLoadedAtRoutDesign_Title)
                            , activity.getResources().getString(R.string.map_NotLoadedAtRoutDesign_Desc)
                            , activity.getResources().getString(R.string.ok)
                            , null, "", null);
                    return;
                }
                LatLng currentRoutePoint = routeMarkers.get(routeCurrentIndex).getPosition();
                String distanceFromCamera = GeoCalcs.distanceBetweenFriendly(cameraLatLon.latitude, cameraLatLon.longitude, currentRoutePoint.latitude, currentRoutePoint.longitude);
                txtRouteDistancePointToPrev.setText(distanceFromCamera);
            }
        }
    }
    //------------------------------------ Route WORKS Start -------------------------
    public void markerClick_Route(Marker marker, boolean isDeleting) {
        int index = Integer.parseInt(marker.getTitle()) - 1;
        if (index != routeCurrentIndex || isDeleting) {
            if (!isDeleting)
                routeMarkers.get(routeCurrentIndex).setIcon(oldRouteIcons);
            routeMarkers.get(index).setIcon(currentRouteIcon);
            routeCurrentIndex = index;
        }
        resetCurrentLine();
        redrawCurrentLine(map.getCameraPosition().target);
    }

    public void mapDraging_Route(Marker marker) {
        int index = Integer.parseInt(marker.getTitle()) - 1;
        Polyline startPoly = index > 0 ? routePolys.get(index-1) : null;
        Polyline endPoly = index < routePolys.size() ? routePolys.get(index) : null;
        if (startPoly != null) {
            List<LatLng> nRoutePoints = new ArrayList<>();
            nRoutePoints.add(routeMarkers.get(index - 1).getPosition());
            nRoutePoints.add(marker.getPosition());
//            startPoly.setVisible(false);
            startPoly.remove();
            Polyline nPoly = getRoutePolyline(nRoutePoints);
            routePolys.set(index - 1, nPoly);
        }
        if (endPoly != null) {
            List<LatLng> nRoutePoints = new ArrayList<>();
            nRoutePoints.add(marker.getPosition());
            nRoutePoints.add(routeMarkers.get(index + 1).getPosition());
//            endPoly.setVisible(false);
            endPoly.remove();
            Polyline nPoly = getRoutePolyline(nRoutePoints);
            routePolys.set(index, nPoly);
        }
        resetRouteCounters();
        route_CalculateDistanceFromPrev();
    }

    public void mapDragStart_Route(Marker marker) {
        if (RouteMode == MAP_CLICK_MODE_ROUTE) {
            markerClick_Route(marker, false);
        }
    }

    public void mapDragEnd_Route(Marker marker) {

    }


    public void resetCurrentLine(){
        if (currentLine != null){
            currentLine.remove();
            currentLine = null;
        }
    }
    public void redrawCurrentLine(LatLng cameraLatLon) {
        if (routeCurrentIndex == -1)
            return;
        resetCurrentLine();
        List<LatLng> myLine = new ArrayList<>();
        myLine.add(routeMarkers.get(routeCurrentIndex).getPosition());
        myLine.add(cameraLatLon);
        if (routeMarkers.size() > routeCurrentIndex + 1){
            myLine.add(routeMarkers.get(routeCurrentIndex + 1).getPosition());
        }
        currentLine = map.addPolyline(new PolylineOptions()
                .addAll(myLine)
                .width(15)
                .color(Color.GREEN)
                .zIndex(100000));
    }
}
