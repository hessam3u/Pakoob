package mojafarin.pakoob;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.icu.text.CaseMap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

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
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import bo.entity.NbCurrentTrack;
import bo.entity.NbMap;
import bo.entity.NbPoi;
import bo.sqlite.NbCurrentTrackSQLite;
import bo.sqlite.TTExceptionLogSQLite;
import maptools.PersianMapIndex25000;
import maptools.TrackProperties;
import maptools.hMapTools;
import utils.HFragment;
import utils.MainActivityManager;
import utils.PrjConfig;
import utils.projectStatics;

public class ShowMapDialog extends HFragment {
    TextView txtSelectedNCCIndex, lblMapNo, lblTitleOfMap;
    public LatLng lastLocation = new LatLng(33, 53.51);
    public float lastLocationZoom = 6f;
    public Marker locationMarker = null;
    String mode = "";

    public static ShowMapDialog getInstance() {
        ShowMapDialog res = new ShowMapDialog();
        return res;
    }

    public ShowMapDialog() {
        Tag = "نقشه نگاه";
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {//4th Event
        super.onViewCreated(view, savedInstanceState);
        initializeComponents(view);
        //fillForm();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void fillMap(){
        try {
            if (mode.equals("select")) {
                LatLng sydney = new LatLng(lastLocation.latitude, lastLocation.longitude);
                mapSelectLocation.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, lastLocationZoom));

                if (locationMarker != null)
                    locationMarker.remove();
                locationMarker = mapSelectLocation.addMarker(new MarkerOptions().position(sydney).draggable(false));
                showBoundsForMarkerSelect(sydney);

            } else if (mode.equals("track")) {
                displayPoiBoundsOnMap(poiToShow);
            } else if (mode.equals("bound")) {
                showMapBounds(latN, latS, lonE, lonW, zoom);
            }
        } catch (Exception ex) {
            Log.e(Tag, "بازکردن" + "fillForm_on_safeGpxView: " + ex.getMessage() + ex.getStackTrace());
            //Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            ex.printStackTrace();
            TTExceptionLogSQLite.insert(ex.getMessage(), stktrc2k(ex), PrjConfig.frmTripComputer, 150);
        }
    }

    public void fillForm() {
        try {
            if (btnSelectLocationOnMap_Click != null)
                btnSelectLocationOnMap.setOnClickListener(btnSelectLocationOnMap_Click);

            if (mode.equals("select")) {
                btnSelectLocationOnMap.setText(context.getResources().getString(R.string.selectMap));
            } else if (mode.equals("track")) {
                btnSelectLocationOnMap.setText(context.getResources().getString(R.string.close));
                this.txtPageTitle.setText(getString(R.string.title_ShowMapDialog_ShowTrack));
            }else if (mode.equals("bound")) {
                btnSelectLocationOnMap.setText(context.getResources().getString(R.string.close));
                this.txtPageTitle.setText(getString(R.string.title_ShowMapDialog_ShowBound));
            }
        } catch (Exception ex) {
            Log.e(Tag, "بازکردن" + "fillForm: " + ex.getMessage() + ex.getStackTrace());
            ex.printStackTrace();
            TTExceptionLogSQLite.insert(ex.getMessage(), stktrc2k(ex), PrjConfig.frmTripComputer, 150);
        }
    }


    TextView txtPageTitle, btnBack;

    @Override
    public void initializeComponents(View v) {
        super.initializeComponents(v);

        btnBack = v.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(view -> {
            ((MainActivityManager) context).onBackPressed();
            //context.changeFragmentVisibility(this, false);
        });
        txtPageTitle = v.findViewById(R.id.txtPageTitle);

        txtSelectedNCCIndex = v.findViewById(R.id.txtSelectedNCCIndex);
        lblMapNo = v.findViewById(R.id.lblMapNo);
        lblTitleOfMap = v.findViewById(R.id.lblTitleOfMap);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapSelectLocation);
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
        showHideMapElements(mode);
        fillForm();
    }


    public void displayPoiBoundsOnMap(NbPoi poi) {
        int debugStep = 0;
        try {
            MapPage.addPOIToMapRecursive(poi, mapSelectLocation, context);
            debugStep = 10;

            //show Bounds

            if (NbPoi.Enums.PoiType_Folder == poi.PoiType || NbPoi.Enums.PoiType_Track == poi.PoiType || poi.PoiType == NbPoi.Enums.PoiType_Route) {
                debugStep = 20;
                double wToE = hMapTools.distanceBetweenMeteres(poi.LatN, poi.LonW, poi.LatN, poi.LonE);
                debugStep = 30;
                double sToN = hMapTools.distanceBetweenMeteres(poi.LatS, poi.LonW, poi.LatN, poi.LonW);
                debugStep = 40;
                int zoom = hMapTools.KmToMapZoom(Math.max(wToE, sToN) / 1000) + 3;

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
        } catch (Exception ex) {
            TTExceptionLogSQLite.insert("debugStep:" + debugStep+"-"+ex.getMessage(), stktrc2k(ex), PrjConfig.frmDialogMapBuilder, 100);
            ex.printStackTrace();
        }
    }

    View.OnClickListener btnSelectLocationOnMap_Click;
    NbPoi poiToShow;
    public void setForModeShowSelect(LatLng _lastLocation, float _lastLocationZoom, View.OnClickListener btnSelectLocationOnMap_Click) {
        mode = "select";
        if (_lastLocation != null) {
            lastLocation = _lastLocation;
        }
        if (_lastLocationZoom != 0) {
            lastLocationZoom = _lastLocationZoom;
        }
        this.btnSelectLocationOnMap_Click = btnSelectLocationOnMap_Click;
    }

    //Set for Mode Show Track
    public void setForModeShowTrack(NbPoi poi, View.OnClickListener btnSelectLocationOnMap_Click) {
        mode = "track";

        this.poiToShow = poi;
        if (btnSelectLocationOnMap_Click != null)
            this.btnSelectLocationOnMap_Click = btnSelectLocationOnMap_Click;
    }

    public void setForModeShowBounds(NbMap currentObj, View.OnClickListener btnSelectLocationOnMap_Click) {
        mode = "bound";
        latN = currentObj.LatN;
        latS = currentObj.LatS;
        lonE = currentObj.LonE;
        lonW = currentObj.LonW;
        zoom = 11f;
        bounds = currentObj.LocalFileAddress == null || currentObj.LocalFileAddress.length() == 0;

        this.btnSelectLocationOnMap_Click = btnSelectLocationOnMap_Click;
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
        fillMap();

        // Add a marker in Sydney and move the camera
        mapSelectLocation.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        } else {
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
            if (mode.equals("track"))
                return;
            if (locationMarker != null)
                locationMarker.remove();
            locationMarker = mapSelectLocation.addMarker(new MarkerOptions().position(latLng).draggable(false));
            lastLocation = latLng;
            showBoundsForMarkerSelect(latLng);
        });
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

    void showBoundsForMarkerSelect(LatLng latLng) {
        PersianMapIndex25000 index = PersianMapIndex25000.FromLaglon(latLng);
        if (index.X != 0) {
            txtSelectedNCCIndex.setText(index.ToString("L"));
            List<LatLng> bounds = index.GetBounds();
            drawMapBound(bounds.get(0).latitude, bounds.get(2).latitude, bounds.get(1).longitude, bounds.get(0).longitude);
        } else {
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
        if (mode.equals("select")) {
            btnSelectLocationOnMap.setVisibility(View.VISIBLE);
            txtSelectedNCCIndex.setVisibility(View.VISIBLE);
            lblTitleOfMap.setVisibility(View.VISIBLE);
            lblMapNo.setVisibility(View.VISIBLE);
            mapBoundsToShow.clear();
            if (routeBoundsToShow != null)
                routeBoundsToShow.remove();

        } else if (mode.equals("bound")) {
            txtSelectedNCCIndex.setVisibility(View.GONE);
            lblMapNo.setVisibility(View.GONE);
            lblTitleOfMap.setVisibility(View.GONE);
            btnSelectLocationOnMap.setVisibility(View.GONE);
        } else if (mode.equals("track")) {
            txtSelectedNCCIndex.setVisibility(View.GONE);
            lblMapNo.setVisibility(View.GONE);
            lblTitleOfMap.setVisibility(View.GONE);
            btnSelectLocationOnMap.setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {//3nd Event
        return inflater.inflate(R.layout.frm_showmapdialog, parent, false);
    }
}
