package mojafarin.pakoob.mainactivitymodes;

import static UI.HFragment.stktrc2k;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import bo.entity.NbMap;
import bo.entity.NbPoi;
import bo.sqlite.TTExceptionLogSQLite;
import maptools.GeoCalcs;
import maptools.PersianMapIndex25000;
import maptools.hMapTools;
import mojafarin.pakoob.MapPage;
import mojafarin.pakoob.R;
import mojafarin.pakoob.app;
import utils.PrjConfig;
import utils.projectStatics;

public class DialogMapBuilder {
    private Context context;
    private AlertDialog mAlertDialog;
    TextView txtSelectedNCCIndex;
    public LatLng lastLocation = new LatLng(33, 53.51);
    public float lastLocationZoom = 6f;
    public Marker locationMarker = null;
    String mode = "";
    //View.OnClickListener btnSelectLocationOnMap_Click;
View v;
    AlertDialog.Builder alertDialogBuilder;
    public android.app.AlertDialog.Builder GetBuilder(){
        if (alertDialogBuilder == null) {
            alertDialogBuilder= new AlertDialog.Builder(context);
            alertDialogBuilder.setView(v);
        }

//        btnAccept.setOnClickListener(PosetiveListener);
//        btnCancel.setOnClickListener(NegativeListener);
        return alertDialogBuilder;
    }


    public DialogMapBuilder(Context _context) {
        context = _context;
        LayoutInflater li = LayoutInflater.from(context);
        this.v = li.inflate(R.layout.dialog_selectmap_nccindex, null);

        txtSelectedNCCIndex = v.findViewById(R.id.txtSelectedNCCIndex);
        FragmentManager myFragmentManager = ((AppCompatActivity)context).getSupportFragmentManager();
        mapFragment = (SupportMapFragment) ((AppCompatActivity)context).getSupportFragmentManager().findFragmentById(R.id.mapSelectLocation);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                onMapReady_Event(googleMap);
            }
        });
        mapFrameLayoutChild = v.findViewById(R.id.mapFrameLayoutChild);

        btnSelectLayerChild = v.findViewById(R.id.btnSelectLayerChild);
        btnSelectLayerChild.setImageBitmap(projectStatics.textAsBitmapFontello("\uE820", 85, Color.BLUE, context));
        btnSelectLayerChild.setOnClickListener(view -> {
            hMapTools.showMapTypeSelectorDialog(mapSelectLocation, context);
        });
        btnShowCustomMapsChild = v.findViewById(R.id.btnShowCustomMapsChild);
        btnShowCustomMapsChild.setImageBitmap(projectStatics.textAsBitmapFontello("\uE81B", 40, Color.WHITE, context));
        btnShowCustomMapsChild.setOnClickListener(view -> {
            showCustomMaps = !showCustomMaps;
            if (tileOverlayCustomMap != null)
                tileOverlayCustomMap.clearTileCache();
            if (showCustomMaps) {
                btnShowCustomMapsChild.setImageBitmap(projectStatics.textAsBitmapFontello("\uE81B", 40, Color.WHITE, context));
            } else {
                btnShowCustomMapsChild.setImageBitmap(projectStatics.textAsBitmapFontello("\uE81C", 40, Color.WHITE, context));
            }
        });
        btnSelectLocationOnMap = v.findViewById(R.id.btnSelectLocationOnMap);

    }
//
//    @SuppressLint("InflateParams")
//    @Override
//    public AlertDialog show() {
//
//        txtSelectedNCCIndex = v.findViewById(R.id.txtSelectedNCCIndex);
//
//        FragmentManager myFragmentManager = ((AppCompatActivity)context).getSupportFragmentManager();
//        mapFragment = (SupportMapFragment) ((AppCompatActivity)context).getSupportFragmentManager().findFragmentById(R.id.mapSelectLocation);
//        mapFragment.getMapAsync(new OnMapReadyCallback() {
//            @Override
//            public void onMapReady(GoogleMap googleMap) {
//                onMapReady_Event(googleMap);
//            }
//        });
//        mapFrameLayoutChild = v.findViewById(R.id.mapFrameLayoutChild);
//
//        btnSelectLayerChild = v.findViewById(R.id.btnSelectLayerChild);
//        btnSelectLayerChild.setImageBitmap(hutilities.textAsBitmap("\uE820", 85, Color.BLUE, context));
//        btnSelectLayerChild.setOnClickListener(view -> {
//            hMapTools.showMapTypeSelectorDialog(mapSelectLocation, context);
//        });
//        btnShowCustomMapsChild = v.findViewById(R.id.btnShowCustomMapsChild);
//        btnShowCustomMapsChild.setImageBitmap(hutilities.textAsBitmap("\uE81B", 40, Color.WHITE, context));
//        btnShowCustomMapsChild.setOnClickListener(view -> {
//            showCustomMaps = !showCustomMaps;
//            if (tileOverlayCustomMap != null)
//                tileOverlayCustomMap.clearTileCache();
//            if (showCustomMaps) {
//                btnShowCustomMapsChild.setImageBitmap(hutilities.textAsBitmap("\uE81B", 40, Color.WHITE, context));
//            } else {
//                btnShowCustomMapsChild.setImageBitmap(hutilities.textAsBitmap("\uE81C", 40, Color.WHITE, context));
//            }
//        });
//        btnSelectLocationOnMap = v.findViewById(R.id.btnSelectLocationOnMap);
//        btnSelectLocationOnMap.setOnClickListener(view -> {
//            //btnSelectLocationOnMap_Click();
//        });
//
//        mAlertDialog = super.show();
//        return mAlertDialog;
//    }

    //Set for Mode Show Track
    public void setForModeShowTrack(NbPoi poi, View.OnClickListener btnSelectLocationOnMap_Click){
        mode = "track";
        showHideMapElements(mode);
        displayPoiBoundsOnMap(poi);

        if (btnSelectLocationOnMap_Click != null)
            btnSelectLocationOnMap.setOnClickListener(btnSelectLocationOnMap_Click);
    }

    public void displayPoiBoundsOnMap(NbPoi poi){
        int debugStep = 0;
        try {
            MapPage.addPOIToMapRecursive(poi, mapSelectLocation, context);
            debugStep = 10;

            //show Bounds

            if (NbPoi.Enums.PoiType_Folder == poi.PoiType || NbPoi.Enums.PoiType_Track == poi.PoiType || poi.PoiType == NbPoi.Enums.PoiType_Route) {
                debugStep = 20;
                double wToE = GeoCalcs.distanceBetweenMeteres(poi.LatN, poi.LonW, poi.LatN, poi.LonE);
                debugStep = 30;
                double sToN = GeoCalcs.distanceBetweenMeteres(poi.LatS, poi.LonW, poi.LatN, poi.LonW);
                debugStep = 40;
                int zoom = GeoCalcs.KmToMapZoom(Math.max(wToE, sToN) / 1000) + 3;

                LatLng mid = new LatLng(poi.LatS + (poi.LatN - poi.LatS) / 2, poi.LonW + (poi.LonE - poi.LonW) / 2);
                debugStep = 50;
                mapSelectLocation.moveCamera(CameraUpdateFactory.newLatLngZoom(mid, zoom));

            } else {
                debugStep = 60;
                LatLng sydney = new LatLng(poi.LatS, poi.LonW);
//            map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                debugStep = 70;
                mapSelectLocation.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 14));
            }
        }
        catch (Exception ex){
            TTExceptionLogSQLite.insert("debugStep:" + debugStep+"-"+ex.getMessage(),stktrc2k(ex) , PrjConfig.frmDialogMapBuilder, 100);
            ex.printStackTrace();
        }
    }

    public void setForModeShowSelect(LatLng _lastLocation, float _lastLocationZoom, View.OnClickListener btnSelectLocationOnMap_Click){
        mode = "select";
        showHideMapElements(mode);
        if (_lastLocation != null){
            lastLocation = _lastLocation;
        }
        if (_lastLocationZoom != 0){
            lastLocationZoom = _lastLocationZoom;
        }

        LatLng sydney = new LatLng(lastLocation.latitude, lastLocation.longitude);
        mapSelectLocation.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, lastLocationZoom));

        if (locationMarker != null)
            locationMarker.remove();
        locationMarker = mapSelectLocation.addMarker(new MarkerOptions().position(sydney).draggable(false));
        showBoundsForMarkerSelect(sydney);

        if (btnSelectLocationOnMap_Click != null)
            btnSelectLocationOnMap.setOnClickListener(btnSelectLocationOnMap_Click);
    }
    public void setForModeShowBounds( NbMap currentObj, View.OnClickListener btnSelectLocationOnMap_Click){
        mode = "bound";
        showHideMapElements(mode);

        latN = currentObj.LatN;
        latS = currentObj.LatS;
        lonE = currentObj.LonE;
        lonW = currentObj.LonW;
        zoom = 11f;
        bounds = currentObj.LocalFileAddress == null || currentObj.LocalFileAddress.length() == 0;

        showMapBounds(latN,latS,lonE, lonW, zoom );

        if (btnSelectLocationOnMap_Click != null)
            btnSelectLocationOnMap.setOnClickListener(btnSelectLocationOnMap_Click);

    }
    public GoogleMap mapSelectLocation;
    SupportMapFragment mapFragment;
    FrameLayout mapFrameLayoutChild;
    FloatingActionButton btnSelectLayerChild, btnShowCustomMapsChild;
    Button btnSelectLocationOnMap;

    List<LatLng> mapBoundsToShow = new ArrayList<>();
    Polyline routeBoundsToShow = null;
    public Double latN = 0D;
    public Double latS;
    public Double lonE;
    public Double lonW;
    public float zoom;
    public boolean bounds;

    public void onMapReady_Event(GoogleMap googleMap) {
        mapSelectLocation = googleMap;

        // Add a marker in Sydney and move the camera
        mapSelectLocation.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        else{
            mapSelectLocation.setMyLocationEnabled(true);
        }
        mapSelectLocation.setMapType(app.session.getLastMapType());
        mapSelectLocation.getUiSettings().setCompassEnabled(true);
        mapSelectLocation.getUiSettings().setMyLocationButtonEnabled(true);
//        map.setOnCameraMoveListener(() -> {
//            CameraMoved();
//        });
//        map.setOnCameraIdleListener(() -> {
//            CameraIdle();
//        });
        mapSelectLocation.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker arg0) {
            }

            @SuppressWarnings("unchecked")
            @Override
            public void onMarkerDragEnd(Marker arg0) {
                lastLocation = arg0.getPosition();
//                map.animateCamera(CameraUpdateFactory.newLatLng(arg0.getPosition()));
            }

            @Override
            public void onMarkerDrag(Marker arg0) {
            }
        });


        moveMapButtonTo((View) mapFragment.getView().findViewWithTag("GoogleMapZoomOutButton").getParent(), 40);
        moveMapButtonTo("GoogleMapMyLocationButton", 280);
        moveMapButtonTo("GoogleMapCompass", 400);

        //showMapBounds(); //HHHHHH 1399-07-07

        initTileProvider();

        mapSelectLocation.setOnMapClickListener(latLng -> {
            if (mode == "track")
                return;
            if (locationMarker != null)
                locationMarker.remove();
            locationMarker = mapSelectLocation.addMarker(new MarkerOptions().position(latLng).draggable(false));
            lastLocation = latLng;
            showBoundsForMarkerSelect(latLng);
        });

//
//        if (mode == "bound") {
//            setForModeShowBounds();
//        }
//        else if(mode == "select"){
//            setForModeShowSelect();
//        }
    }
    boolean showCustomMaps = true;
    TileOverlay tileOverlayCustomMap;

    public void initTileProvider() {
        TileProvider tileProvider = new UrlTileProvider(256, 256) {
            @Override
            public URL getTileUrl(int x, int y, int zoom) {
                if (!showCustomMaps)
                    return null;
                /* Define the URL pattern for the tile images */
                String s = app.tileRoot + zoom + "/" + x + "-" + y + ".png"; //String.format(tileRoot+ "%d/%d-%d.png", zoom, x, y);

                //Log.d("Current URL: ", s);

                File file = new File(s);
                if (!file.exists())
                    return null;
                try {
                    return new URL("file:///" + s);
                } catch (MalformedURLException e) {
                    throw new AssertionError(e);
                }

            }
        };
        tileOverlayCustomMap = mapSelectLocation.addTileOverlay(new TileOverlayOptions()
                .tileProvider(tileProvider));

    }
    void showBoundsForMarkerSelect(LatLng latLng){
        PersianMapIndex25000 index = PersianMapIndex25000.FromLaglon(latLng);
        if (index.X != 0){
            txtSelectedNCCIndex.setText(index.ToString("L"));
            List<LatLng> bounds = index.GetBounds();
            drawMapBound(bounds.get(0).latitude, bounds.get(2).latitude, bounds.get(1).longitude, bounds.get(0).longitude);
        }
        else{
            txtSelectedNCCIndex.setText("");
        }
    }

    void moveMapButtonTo(String tag, int marginBot) {
        //based on : https://stackoverflow.com/questions/40371321/how-to-move-google-map-zoom-control-position-in-android
        //and https://stackoverflow.com/questions/36785542/how-to-change-the-position-of-my-location-button-in-google-maps-using-android-st
        View btn = mapFragment.getView().findViewWithTag(tag);//GoogleMapMyLocationButton
        moveMapButtonTo(btn, marginBot);
    }

    void moveMapButtonTo(View btn, int marginBot) {
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 30, marginBot);
        btn.setLayoutParams(rlp);
    }
    public void showMapBounds(Double latN, Double latS, Double lonE, Double lonW, float zoom) {
        showHideMapElements("bound");

        if (latN != 0) {
            drawMapBound(latN, latS, lonE, lonW);

            LatLng sydney = new LatLng((double) ((latN - latS) / 2 + latS), ((lonE - lonW) / 2 + lonW));
//            map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            mapSelectLocation.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, zoom));
        }
    }

    void drawMapBound(Double latN, Double latS, Double lonE, Double lonW) {
        mapBoundsToShow.clear();
        mapBoundsToShow.add(new LatLng(latN, lonW));
        mapBoundsToShow.add(new LatLng(latN, lonE));
        mapBoundsToShow.add(new LatLng(latS, lonE));
        mapBoundsToShow.add(new LatLng(latS, lonW));
        mapBoundsToShow.add(new LatLng(latN, lonW));

        if (routeBoundsToShow != null)
            routeBoundsToShow.remove();

        routeBoundsToShow = mapSelectLocation.addPolyline(new PolylineOptions()
                .width(10)
                .color(Color.rgb(0, 255, 0))
                .geodesic(false)
                .zIndex(3));
        routeBoundsToShow.setPoints(mapBoundsToShow);
    }

    void showHideMapElements(String mode) {
//        if (locationMarker != null)
//            locationMarker.remove();
        if (mode == "select") {
            btnSelectLocationOnMap.setVisibility(View.VISIBLE);
            mapBoundsToShow.clear();
            if (routeBoundsToShow != null)
                routeBoundsToShow.remove();
            btnSelectLocationOnMap.setText(context.getResources().getString(R.string.selectMap));
        } else if (mode == "bound") {
            btnSelectLocationOnMap.setVisibility(View.GONE);
        } else if(mode == "track"){
            btnSelectLocationOnMap.setText(context.getResources().getString(R.string.close));
        }
    }


}
