package mojafarin.pakoob;

import static mojafarin.pakoob.MainActivity.BearingTextFormCurrent;
import static mojafarin.pakoob.MainActivity.BearingValueFormCurrent;
import static mojafarin.pakoob.MainActivity.DistanceTextFormCurrent;
import static mojafarin.pakoob.MainActivity.DistanceValueFormCurrent;
import static mojafarin.pakoob.MainActivity.cameraLatLon;
import static mojafarin.pakoob.MainActivity.currentLatLon;
import static mojafarin.pakoob.MainActivity.isLockOnMe;
import static mojafarin.pakoob.MainActivity.isTrackUp;
import static mojafarin.pakoob.MainActivity.map;
import static mojafarin.pakoob.MapPage.TRACK_UP;
import static mojafarin.pakoob.MapPage.NO_LOCATION;
import static mojafarin.pakoob.MapPage.NORTH_UP;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.text.IDNA;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;
import com.takusemba.spotlight.OnSpotlightEndedListener;
import com.takusemba.spotlight.OnSpotlightStartedListener;
import com.takusemba.spotlight.SimpleTarget;
import com.takusemba.spotlight.Spotlight;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import bo.NewClasses.SimpleRequest;
import bo.dbConstantsTara;
import bo.entity.NbPoi;
import bo.entity.NbPoiCompact;
import bo.entity.NbPoiList;
import bo.entity.SearchRequestDTO;
import bo.sqlite.TTExceptionLogSQLite;
import hmapscaleview.MapScaleView;
import maptools.MapCenterPointer;
import maptools.MapMyLocationIcon;
import maptools.TrackData;
import maptools.hMapTools;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.HFragment;
import utils.MainActivityManager;
import utils.MyDate;
import utils.PrjConfig;
import utils.RecyclerTouchListener;
import utils.hutilities;
import utils.projectStatics;

public class SearchOnMap extends HFragment  implements SensorEventListener {

    int currentSelectedNbPoiIndex = -1;
    boolean typeSearchMode = false;

    public static final int MAP_CLICK_MODE_NONE = 0;
    public static final int MAP_CLICK_MODE_ROUTE = 1;
    public static final int MAP_CLICK_MODE_WAYPOINT = 2;

    MapScaleView scaleView;
    SupportMapFragment mapFragment;

    int lastCameraMoveReason = 0;
    boolean showCustomMaps = true;


    boolean firstLocationObserved = false;

    public static boolean IsInSightNGoMode = false;
    public int IsInAddWaypointMode = 0;
    MainActivity mainActivity;

    public SearchOnMap(){
    }
    View view;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {//4th Event
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        initSensors();
        mainActivity = (MainActivity) context;

        initializeComponents(view);

        //initSensors();

        checkLocation(true);
    }

    private RecyclerView rvSearchResult;
    Button btnShowResultOnMap;
    private ProgressBar pageProgressBar;
    LinearLayout divSearch;
    TextView txtSearchResult;
    private NbPoisAdapter adapterSearch;
    private ScrollView scrollSearchMode;

    FloatingActionButton btnSelectMap, btnSelectLayer, btnShowCustomMaps;
    MapCenterPointer viewCenterPointer;
    public TextView btnGotoCurrentLocation, btnZoomOut, btnZoomIn;
    RelativeLayout mainLinearLayout;
    CoordinatorLayout coordinator;
    MapMyLocationIcon mapMyLocationIcon;

    TextView btnBack;
    EditText txtSearch;

    @Override
    public void initializeComponents(View v) {


        mainLinearLayout = v.findViewById(R.id.mainLinearLayout);
        coordinator = v.findViewById(R.id.coordinator);


        mapMyLocationIcon = v.findViewById(R.id.mapMyLocationIcon);

        //1399-12-24 Tooye re-su-me ham ejra mishe dige, niazi behesh nist---------- Felan for test active kardamesh
//        if (dialogRecordTrack.getIsRecordPanelActive()){
//            trackingServiceResume();//Comented at 1400-10-21 در هنگام اصلاح اشکال ترسیم ترک فعلی
//        }
        Toolbar toolbar = v.findViewById(R.id.toolbarOfPage);
        context.setSupportActionBar(toolbar);
        context.getSupportActionBar().setDisplayShowTitleEnabled(false); //remove toolbar Title


        DistanceTextFormCurrent = getResources().getString(R.string.distanceFromCurrentTextAtMainActivity);
        BearingTextFormCurrent = getResources().getString(R.string.bearingFromCurrentTextAtMainActivity);

        btnSelectMap = v.findViewById(R.id.btnSelectMap);
        btnSelectMap.setImageBitmap(projectStatics.textAsBitmapFontello("\uF278", 80, Color.GREEN, context.getApplicationContext()));
        btnSelectMap.setOnClickListener(view -> {
            context.showFragment(new MapSelect());
        });
        btnBack = v.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(view -> {hutilities.hideKeyboard(context, txtSearch);((MainActivityManager)context).onBackPressed();});
        txtSearch = v.findViewById(R.id.txtSearch);
        txtSearch.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                btnSearch_Click();
                return true;
            }
            return true;
        });

        txtSearch.setOnFocusChangeListener((view1, hasFocus) -> {
        });

        btnShowResultOnMap = v.findViewById(R.id.btnShowResultOnMap);
        btnShowResultOnMap.setOnClickListener(view1 -> {AddResultsToMap(adapterSearch.data);GoToMapForAllResults();});

        rvSearchResult = v.findViewById(R.id.rvSearchResult);
        rvSearchResult.setLayoutManager(new LinearLayoutManager(context));
        rvSearchResult.setHasFixedSize(true);
        rvSearchResult.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);//TourList.this bayad mibood
        rvSearchResult.setLayoutManager(layoutManager);
        //For Scrolling inside NestedScrollView
        //rvSearchResult.setNestedScrollingEnabled(false);
        initRecyclerView();
//            //baraye namayeshe joda konandeh
        rvSearchResult.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));//parentActivity -> getActivity -> this bood

        //rvSearchResult.setAdapter(adapterSearch);
        initAdapterSearch(new ArrayList<>());

        pageProgressBar = v.findViewById(R.id.progressBar);
        scrollSearchMode = v.findViewById(R.id.scrollSearchMode);

        divSearch = v.findViewById(R.id.divSearch);
        divSearch.setVisibility(View.GONE);

        txtSearchResult = v.findViewById(R.id.txtSearchResult);
        txtSearchResult.setVisibility(View.GONE);


        btnSelectLayer = v.findViewById(R.id.btnSelectLayer);
        btnSelectLayer.setImageBitmap(projectStatics.textAsBitmapFontello("\uE820", 85, Color.BLUE, context.getApplicationContext()));
        btnSelectLayer.setOnClickListener(view -> {
            hMapTools.showMapTypeSelectorDialog(map, context);
        });
        //1399-09-09 new Version Comment
        btnShowCustomMaps = v.findViewById(R.id.btnShowCustomMaps);
        btnShowCustomMaps.setImageBitmap(projectStatics.textAsBitmapFontello("\uE81B", 40, Color.WHITE, context.getApplicationContext()));
        btnShowCustomMaps.setOnClickListener(view -> {
            showCustomMaps = !showCustomMaps;
            if (tileOverlayCustomMap != null)
                tileOverlayCustomMap.clearTileCache();
//            app.session.setLastLockRotate(lockRotate);
            if (showCustomMaps) {
                btnShowCustomMaps.setImageBitmap(projectStatics.textAsBitmapFontello("\uE81B", 40, Color.WHITE, context.getApplicationContext()));
            } else {
                btnShowCustomMaps.setImageBitmap(projectStatics.textAsBitmapFontello("\uE81C", 40, Color.WHITE, context.getApplicationContext()));
            }
        });
//        btnShowCalcuations = v.findViewById(R.id.btnShowCalcuations);
//        btnShowCalcuations.setImageBitmap(hutilities.textAsBitmap("\uF044", 40, Color.WHITE, getApplicationContext()));


        //map works
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        //if in Activity, call : getSupportFragmentManager
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        //mapFragment.getMapAsync(this);


        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                onMapReady_Event(googleMap);
            }
        });

        scaleView = v.findViewById(R.id.scaleView);
        scaleView.metersOnly();

        viewCenterPointer = v.findViewById(R.id.viewCenterPointer);

        btnGotoCurrentLocation = v.findViewById(R.id.btnGotoCurrentLocation);
        btnGotoCurrentLocation.setOnClickListener(view -> {
            if (currentLatLon != null) {
                if (isLockOnMe){
                    //اگه روی من قفل بود، هدف از ضربه این بوده که ترک آپ و نورث آپ رو عوض کن
                    isTrackUp = !isTrackUp;
                }
                //در هر صورت رو من قفل بشه
                isLockOnMe = true;
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLon, map.getCameraPosition().zoom));
                if (!isTrackUp)
                    updateCamera(0);
                updateMyLocationIcon();
                setBtnGotoCurrentLocation();
            }
        });


        btnZoomIn = v.findViewById(R.id.btnZoomIn);
        btnZoomIn.setOnClickListener(view -> {
            CameraPosition position = map.getCameraPosition();
            if (position.zoom < 21) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(position.target, position.zoom + 1));
                updateMyLocationIcon();
            }
        });
        btnZoomOut = v.findViewById(R.id.btnZoomOut);
        btnZoomOut.setOnClickListener(view -> {
            CameraPosition position = map.getCameraPosition();
            if (position.zoom > 2) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(position.target, position.zoom - 1));
                updateMyLocationIcon();
            }
        });

        super.initializeComponents(v);
    }

    List<Marker> markers = new ArrayList<>();
    LatLngBounds.Builder builder;

    public void AddResultsToMap(List<NbPoi> res) {
        int sz = markers.size();
        for (int i = 0; i < sz; i++) {
            markers.get(i).remove();
        }
        markers.clear();
        sz = res.size();
        BitmapDescriptor currentRouteIcon = hutilities.bitmapDescriptorFromVector(context, R.drawable.ic_mosalasred);
        builder = new LatLngBounds.Builder();

        for (int i = 0; i < sz; i++) {
            NbPoi poi = res.get(i);
            if (NbPoi.Enums.PoiType_Folder == poi.PoiType)
                continue;
            LatLng position = null;
            if (NbPoi.Enums.PoiType_Track == poi.PoiType || poi.PoiType == NbPoi.Enums.PoiType_Route) {
                position = new LatLng(poi.LatBegin, poi.LonBegin);
            }
            else{
                position = new LatLng(poi.LatS, poi.LonW);
            }
            builder.include(position);

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(position)
                    .title(poi.Name)
                    .zIndex(10000)
                    .icon(currentRouteIcon)
                    .flat(true);
            Marker marker = map.addMarker(markerOptions);
            marker.setTag(poi);
            markers.add(marker);
            poi.marker = marker;
        }
    }
    public void GoToMapForAllResults(){
        scrollSearchMode.setVisibility(View.GONE);
        LatLngBounds bounds = builder.build();
        int padding = 80; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        map.moveCamera(cu);
    }

    public boolean poiClicked(Marker marker) {
        NbPoi poi = (NbPoi)marker.getTag();
//        Snackbar snackbar = Snackbar.make(context.getWindow().getDecorView().findViewById(android.R.id.content), poi.Name, Snackbar.LENGTH_LONG);
//        View snackBarView = snackbar.getView();
//        snackBarView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
//        snackbar.show();

        InfoBottom model = new InfoBottom(poi);
        model.show(context.getSupportFragmentManager(), model.getTag());

        return true;
    }


    @Override
    public void onFragmentShown(){
        Log.e("نمایش فرگمنت", "Map is Shown");
//        boolean helpSeen = app.session.getMapHelpSeen();
//        if (!helpSeen){
//            projectStatics.showDialog(context,getResources().getString(R.string.ShowHelpAtMap_Title), getResources().getString(R.string.ShowHelpAtMap_Desc), getResources().getString(R.string.Yes), view -> {
//                ShowHelpSpotlight();
//            }, getResources().getString(R.string.Later), null);
//        }
        //1400-11-16 added, for be sure to update Location Access after resume
        if (locationManager == null || myLocationListener== null) {
            checkLocation(true);
        }
    }

    public void ShowHelpSpotlight(){
        SimpleTarget btnSelectMapTarget = new SimpleTarget.Builder(context)
                .setPoint(btnSelectMap) // position of the Target. setPoint(Point point), setPoint(View view) will work too.
                .setRadius(110f) // radius of the Target
                .setTitle("دانلود نقشه ها") // title
                .setDescription("نقشه های پیشرفته رو از این قسمت دانلود کن") // description
                .build();
        SimpleTarget btnSelectLayerTarget = new SimpleTarget.Builder(context)
                .setPoint(btnSelectLayer) // position of the Target. setPoint(Point point), setPoint(View view) will work too.
                .setRadius(110f) // radius of the Target
                .setTitle("نوع نقشه پایه") // title
                .setDescription("نوع نقشه اصلی رو از این منو میتونی تغییر بدی") // description
                .build();
        SimpleTarget btnShowCustomMapsTarget = new SimpleTarget.Builder(context)
                .setPoint(btnShowCustomMaps) // position of the Target. setPoint(Point point), setPoint(View view) will work too.
                .setRadius(110f) // radius of the Target
                .setTitle("نمایش/عدم نمایش نقشه های تخصصی") // title
                .setDescription("در صورتی که نقشه های تخصصی را تهیه کرده باشی، با این گزینه اون ها رو فعال یا غیر فعال می کنی") // description
                .build();
        SimpleTarget btnGotoCurrentLocationTarget = new SimpleTarget.Builder(context)
                .setPoint(btnGotoCurrentLocation)
                .setRadius(120f) // radius of the Target
                .setTitle("بپر به موقعیت فعلی") // title
                .setDescription("اگه جی پی اس روشن باشه و مکان تثبیت شده باشه، این گزینه نقشه رو به مکان فعلی منتقل میکنه و مکانت رو نشون میده") // description
                .build();
        Spotlight.with(context)
                .setDuration(250L) // duration of Spotlight emerging and disappearing in ms
                .setAnimation(new DecelerateInterpolator(5f)) // animation of Spotlight
                .setTargets(btnSelectMapTarget, btnSelectLayerTarget,
                        btnShowCustomMapsTarget, btnGotoCurrentLocationTarget) // set targes. see below for more info
                .setOnSpotlightStartedListener(new OnSpotlightStartedListener() { // callback when Spotlight starts
                    @Override
                    public void onStarted() {
                    }
                })
                .setOnSpotlightEndedListener(new OnSpotlightEndedListener() { // callback when Spotlight ends
                    @Override
                    public void onEnded() {
                        app.session.setMapHelpSeen(true);
                    }
                })
                .start(); // start Spotlight
    }


//    public void openViewTrackPoints(long NbPoiId, String Sender) {
//        showHideMainActivityOnNavigation(false);
//        Fragment mFragment = new ViewTrackPoints(NbPoiId, Sender);
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().replace(R.id.your_placeholder, mFragment)
//                .addToBackStack(null);
//        transaction.commit();
//    }
//    private void getLocation() {
//        if (!tracker.canGetLocation) {
//            if (Build.VERSION.SDK_INT >= 23) {
//                String[] PermissionsLocation = {android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION};
//                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    requestPermissions(PermissionsLocation, REQ_LOCATION_PERMISSION);
//                } else
//                    showLocationAlert();
//            } else
//                showLocationAlert();
//        } else setMyLocation();
//    }

    public void onMapReady_Event(GoogleMap googleMap) {
        map = googleMap;
        //1399-09-09 Commented:
        //lockRotate = app.session.getLastLockRotate();

        // Add a marker in Sydney and move the camera
        LatLng sydney = app.session.getLastAproxLocation();//new LatLng(36.30, 59.47);
        //map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, app.session.getLastZoom()));
        updateMyLocationIcon();

        //from other mapReady
        map.setMapType(app.session.getLastMapType());
        map.getUiSettings().setAllGesturesEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(false);//To show Navigation on Google Map App
        map.getUiSettings().setZoomGesturesEnabled(true);
        map.setBuildingsEnabled(true);
        map.setIndoorEnabled(true);
        //Classic ones:
//        map.getUiSettings().setMyLocationButtonEnabled(true);
//        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//        map.getUiSettings().setCompassEnabled(true);
//        map.getUiSettings().setAllGesturesEnabled(true);
//        map.getUiSettings().setMapToolbarEnabled(true);
//        map.getUiSettings().setZoomGesturesEnabled(true);
//        map.setBuildingsEnabled(true);
//        map.setIndoorEnabled(true);

        //end  from other mapReady

        map.getUiSettings().setZoomControlsEnabled(false);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        } else {
            map.setMyLocationEnabled(false); //1399-12-07 changed to false, because of MyLocationIcon
        }
        map.getUiSettings().setCompassEnabled(true);//Removed after Custom Zoom and My Location
        map.getUiSettings().setMyLocationButtonEnabled(false);//Removed after Custom Zoom and My Location

        map.setOnCameraMoveListener(() -> {
            CameraMoved();
        });
        map.setOnCameraIdleListener(() -> {
            CameraIdle();
        });
        map.setOnCameraMoveStartedListener(reason -> {
            CameraMoveStarted(reason);
        });
//        map.setOnMyLocationButtonClickListener(() -> {//Removed after Custom Zoom and My Location
//            lockRotate = true;
//            return false;
//        });
        map.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
            }
        });
        map.setOnMapClickListener(latLng -> {
        });
        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
            }

            @SuppressWarnings("unchecked")
            @Override
            public void onMarkerDragEnd(Marker marker) {
            }

            @Override
            public void onMarkerDrag(Marker marker) {
            }
        });
        map.setOnMarkerClickListener(marker -> {
            return poiClicked(marker);
        });


        //Removed after Custom Zoom and My Location:
//        moveMapButtonTo((View) mapFragment.getView().findViewWithTag("GoogleMapZoomOutButton").getParent(), 40);
//        moveMapButtonTo("GoogleMapMyLocationButton", 280);
        moveMapButtonToMargin("GoogleMapCompass", 300, 0, 30, 0);

        //add25000Overlay("m78621se_mashhad6s2");
//        add25000Overlay("m78624se_kang_s2");
        add25000Overlay("m79624sw_mashhad5");
//        add25000Overlay("m64614NE_s2");

        //Set visible POIS
        int poiSizes = app.visiblePOIs.size();
        for (int i = 0; i < poiSizes; i++) {
            try {
                NbPoiCompact poi = app.visiblePOIs.get(i);
                if (poi.ShowStatus != NbPoi.Enums.ShowStatus_Show)
                    continue;
                addPOIToMap(poi, map);

            } catch (Exception ex) {
                TTExceptionLogSQLite.insert(ex.getMessage(), ex.getStackTrace().toString(), PrjConfig.frmMapPage, 1004);
                ex.printStackTrace();
            }
        }

        OnMapReadyCompleted = true;


//        mapOnClick_Route(new LatLng(36.2910, 59.5025));
//        mapOnClick_Route(new LatLng(36.2920, 59.5026));
//        mapOnClick_Route(new LatLng(36.2930, 59.5027));
//        mapOnClick_Route(new LatLng(36.2940, 59.5028));
//        mapOnClick_Route(new LatLng(36.2950, 59.5029));
//        mapOnClick_Route(new LatLng(36.2960, 59.5020));

    }


    public static int RouteMode = MAP_CLICK_MODE_NONE;

    //----------------------------END Route------------------------

    public static void showHidePoiOnMap(NbPoiCompact poi) {
        if (poi.polyLine != null)
            poi.polyLine.setVisible(poi.ShowStatus == NbPoi.Enums.ShowStatus_Show);
        if (poi.marker != null)
            poi.marker.setVisible(poi.ShowStatus == NbPoi.Enums.ShowStatus_Show);
    }

    //In Tab'e be app.visiblePOIs.add(compact); kar nadare, bayad dar kenaresh HATMAN be oon ezafe konimesh
    public static Object addPOIToMap(NbPoiCompact poi, GoogleMap map) {
//        if(map!= null){
//
//        }
        if (poi.PoiType == NbPoi.Enums.PoiType_Folder)
            return null;
        if (poi.PoiType == NbPoi.Enums.PoiType_Track || poi.PoiType == NbPoi.Enums.PoiType_Route) {
            if (poi.polyLine == null ) {
                List<LatLng> track = TrackData.readTrackData_LatLng(poi.Address);
                Polyline line = map.addPolyline(new PolylineOptions()
                        .addAll(track)
                        .width(poi.DisplaySize)
                        .color(poi.Color)
                        .zIndex(100000));

                //line.setClickable(true);
                line.setTag(poi.Name);
                poi.polyLine = line;
                return line;
            } else {
//                poi.polyLine.setVisible(true);
                return poi.polyLine;
            }
        } else {
            if (poi.marker == null) {
                LatLng point = new LatLng(poi.LatS, poi.LonW);
                Marker addedMarker = map.addMarker(new MarkerOptions().position(point).title(poi.Name));
                poi.marker = addedMarker;
                return addedMarker;
            } else {
//                poi.marker.setVisible(true);
                return poi.marker;
            }
        }
    }

    private void CameraMoveStarted(int reason) {
        lastCameraMoveReason = reason;
        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
            isLockOnMe = false;
//            isTrackUp = false;//HHH Lazeme???
        }

//            switch (reason) {
//                case GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE:
//                    reasonText = "GESTURE";
//                    break;
//                case GoogleMap.OnCameraMoveStartedListener.REASON_API_ANIMATION:
//                    reasonText = "API_ANIMATION";
//                    break;
//                case GoogleMap.OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION:
//                    reasonText = "DEVELOPER_ANIMATION";
//                    break;
//            }
    }

    void moveMapButtonTo(String tag, int marginBot) {
        //based on : https://stackoverflow.com/questions/40371321/how-to-move-google-map-zoom-control-position-in-android
        //and https://stackoverflow.com/questions/36785542/how-to-change-the-position-of-my-location-button-in-google-maps-using-android-st
        View btn = mapFragment.getView().findViewWithTag(tag);//GoogleMapMyLocationButton
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 30, marginBot);
        btn.setLayoutParams(rlp);
    }

    void moveMapButtonToMargin(String tag, int marginTop, int marginBot, int marginLeft, int marginRight) {
        View btn = mapFragment.getView().findViewWithTag(tag);//GoogleMapMyLocationButton
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        if (marginTop > 0)
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        else
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        if (marginBot > 0)
            rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        else
            rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
        if (marginLeft > 0)
            rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        if (marginRight > 0)
            rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        rlp.setMargins(marginLeft, marginTop, marginRight, marginBot);
        btn.setLayoutParams(rlp);
    }

    private void CameraIdle() {
        //for showing Scale
        CameraPosition cameraPosition = map.getCameraPosition();
        scaleView.update(cameraPosition.zoom, cameraPosition.target.latitude);
        //End for showing Scale
        cameraLatLon = cameraPosition.target;
        if (lastCameraMoveReason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
            //lockRotate = false;
        } else {
            //lockRotate = true;

            setDownFields();
        }
    }

    private void CameraMoved() {
        //for showing Scale
        CameraPosition cameraPosition = map.getCameraPosition();
        scaleView.update(cameraPosition.zoom, cameraPosition.target.latitude);
        //End for showing Scale

        cameraLatLon = cameraPosition.target;

        setDownFields();

        updateMyLocationIcon();

    }


    //String tileRoot = "";
    TileOverlay tileOverlayCustomMap;

    void add25000Overlay(String resIdStr) {
        //tileRoot = getApplicationContext().getFilesDir() + "/" + hMapTools.tilesFolder + "/";//m79624sw_mashhad5
        //raveshe ghadimi:
/*
        int resId = getResources().getIdentifier(resIdStr, "drawable", getPackageName());

        LatLngBounds bounds = PersianMapIndex25000.stringToBounds(resIdStr);
        GroundOverlayOptions newarkMap = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(resId))
                .positionFromBounds(bounds);
        map.addGroundOverlay(newarkMap);*/
//end raveshe ghadimi
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
        tileOverlayCustomMap = map.addTileOverlay(new TileOverlayOptions()
                .tileProvider(tileProvider));
    }

    private LocationManager locationManager;
    private LocationListener myLocationListener;
    Location location = null;

    public void checkLocation(boolean showErrorIfNoPermission) {
        String serviceString = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) context.getSystemService(serviceString);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (showErrorIfNoPermission){
                projectStatics.showDialog(context,getResources().getString(R.string.FineLocationDenied_Title), getResources().getString(R.string.FineLocationDenied_Desc), getResources().getString(R.string.ok), view -> {
                }, "", null);
            }
            return;
        }
        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!isGpsEnabled){
            projectStatics.showDialog((MainActivity) context
                    , getResources().getString(R.string.locationIsOff)
                    , getResources().getString(R.string.locationIsOff_Desc)
                    , getResources().getString(R.string.ok), view -> {
                        hutilities.showSettingTpAccessLocation((MainActivity) context);
                    }, "", null);
        }
        Log.e("لوک", "checkLocation_Fired Internally " + " and myLocationListener: " + (myLocationListener == null?"NULL":"Not NULL"));

        myLocationListener = new LocationListener() {
            @SuppressLint("MissingPermission")
            public void onLocationChanged(Location locationListener) {
                //1400-01-03 I thinks it is faster than traditional way whick commented in next lines
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                Log.e("لوک", "onLocationChangedCalled: " + (location == null?"LOCATION IS NULL":location.getLatitude()));
                if (location != null) {
                    currentLatLon = new LatLng(location.getLatitude(), location.getLongitude());
                }
                else{
                    currentLatLon = null;
                }

                //1400-01-03 Traditional way that get Approx Location, not Exact ONE USING GPS-Commented:
//                boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//                if (isGpsEnabled) {
//                    if (locationListener != null) {
//                        if (ActivityCompat.checkSelfPermission((MainActivity) context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission((MainActivity) context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                            return;
//                        }
//                        if (locationManager != null) {
//                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                            if (location != null) {
//                                currentLatLon = new LatLng(location.getLatitude(), location.getLongitude());
//                            }
//                        }
//                    }
//                } else if (hutilities.isInternetConnected((MainActivity) context)) {
//                    if (locationManager != null) {
//                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                        if (location != null) {
//                            currentLatLon = new LatLng(location.getLatitude(), location.getLongitude());
//                        }
//                    }
//                }


                locationHasChanged(location);
                mainActivity.locationChangedFromMapPage(location);

            }

            public void onProviderDisabled(String provider) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
        };
        Log.e("لوک", " درخواست به روز رسانی موقعیت در صفحه نقشه- 150");
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, app.MinTrackTime, app.MinTrackDistance, myLocationListener);  //  here the min time interval and min distance
    }

    //float mDeclination = 0;//Add for Autorotate and moved to app.declination
    int locationCounter = 0;

    public void locationHasChanged(Location location) {
        Log.e("لوک", "locationHasChanged Fired and firstLocationObserved is " + firstLocationObserved
        + (map == null?" Map_IS_NULL":" MAP_IS_OK"));
        if (!firstLocationObserved) {
            firstLocationObserved = true;

            currentLatLon = new LatLng(location.getLatitude(), location.getLongitude());

        }

        locationCounter++;
        //HHH 1401-03-05 ظاهرا نیازی بهش نیست چون موقعیت داره توی صفحه نقشه دریافت میشه
//        if (locationCounter % 20 == 0) {
//            app.session.setLastAproxLocation(currentLatLon);
//            app.session.setLastAproxLocationFixTime(MyDate.CalendarToCSharpDateTimeAcceptable(Calendar.getInstance()));
//            app.session.setLastAproxLocationFixType((byte)1);
//            Log.e("لوک", "ذخیره");
//        }

        //Start Map Works ................
        if (map == null)
            return;
        //for Center Screen myself
        if (isLockOnMe) {
            LatLng myLoc = new LatLng(location.getLatitude(), location.getLongitude());
            float zoom = map.getCameraPosition().zoom;
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLoc, zoom));
        }

        updateMyLocationIcon();
        //End for Center Screen myself

        //Add for Autorotate
        // getDeclination returns degrees
        app.declination = hMapTools.RefreshSavedDelinationIfNeeded(
                (float) location.getLatitude(),
                (float) location.getLongitude(),
                (float) location.getAltitude());
        //Log.d("Rotate----", Float.toString(app.declination));
        //End Add for Autorotate

        setDownFields();
        viewCenterPointer.invalidate();
    }

    int counterTestingAndRemoveBeshe = 0;
    private void updateMyLocationIcon() {

        //Get map projection
        if (map == null)
            return;
        counterTestingAndRemoveBeshe++;
//        if (counterTestingAndRemoveBeshe % 20 == 0)
//            Log.e("لوک", "updateMyLocationIcon_CALLED and " + (currentLatLon != null?currentLatLon.latitude:"false"));
        Projection projection = map.getProjection();

//Convert LatLng to on screen location
        Point p1 = new Point();
//projection.toPixels(gp, p1);
        if (currentLatLon != null) {
            mapMyLocationIcon.setVisibility(View.VISIBLE);
            p1 = projection.toScreenLocation(currentLatLon);
            mapMyLocationIcon.setX(p1.x - 45);
            mapMyLocationIcon.setY(p1.y - 35);
            mapMyLocationIcon.bearingRelatedToMap = isTrackUp ? 0 : bearing - map.getCameraPosition().bearing;
            mapMyLocationIcon.invalidate();
        } else {
            mapMyLocationIcon.setVisibility(View.GONE);
        }

        //HHHHHHHHHHHH for Showing Marker at current position
//        BitmapDescriptor defaultMarker =
//                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
//        Marker mapMarker = map.addMarker(new MarkerOptions()
//                .position(latLng)
//                .title("Some title here")
//                .snippet("Some description here")
//                .icon(defaultMarker));
        //END HHHH

    }

    void setDownFields() {
        if (cameraLatLon != null && currentLatLon != null && !isLockOnMe) {
            DistanceValueFormCurrent = hMapTools.distanceBetweenFriendly(cameraLatLon.latitude, cameraLatLon.longitude, currentLatLon.latitude, currentLatLon.longitude);
            //txtDistanceFromCurrent.setText(DistanceTextFormCurrent + DistanceValueFormCurrent);
            BearingValueFormCurrent = /*"°" +(app.currentNorth == hMapTools.NORTH_MAG?"m":"t") +*/ Integer.toString((int) (hMapTools.GetAzimuthInDegree(currentLatLon, cameraLatLon) + (app.currentNorth == hMapTools.NORTH_MAG ? app.declination : 0)));
            //txtBearingFromCurrent.setText(BearingTextFormCurrent + "°" + BearingValueFormCurrent);
        } else {
            //txtDistanceFromCurrent.setText(DistanceTextFormCurrent + "0m");
            //txtBearingFromCurrent.setText(BearingTextFormCurrent + "0° MN");
        }
    }


    //Add for Autorotate part 1
    //https://stackoverflow.com/questions/14320015/android-maps-auto-rotate
    void initSensors() {
        mSensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);
        mRotVectSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    }

    //Add for Autorotate part 2
    SensorManager mSensorManager = null;
    Sensor mRotVectSensor = null;
    private float[] mRotationMatrix = new float[16];
    public static double angle = 0;
    public static float bearing = 0;

    @Override
    public void onSensorChanged(SensorEvent event) {
        //Add for Autorotate part 3
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            if (true || isTrackUp || IsInSightNGoMode ) {
                SensorManager.getRotationMatrixFromVector(
                        mRotationMatrix, event.values);
                float[] orientation = new float[3];
                SensorManager.getOrientation(mRotationMatrix, orientation);
                if (Math.abs(Math.toDegrees(orientation[0]) - angle) > 0) {
                    angle = Math.toDegrees(orientation[0]);
                    bearing = (float) angle + app.declination;

                    if (isTrackUp) {
                        updateCamera(bearing);
                    }
                    else{
                        updateMyLocationIcon();
                    }

                } else
                    angle = Math.toDegrees(orientation[0]);
            }

//        if (counterTestingAndRemoveBeshe % 20 == 0)
//            Log.e("لوک", "updateMyLocationIcon_CALLED inonSensorChanged and " + (currentLatLon != null?currentLatLon.latitude:"false") + " LOCATION MANAGER:" + (locationManager != null?"NOT NULL":"NULL")+" myLocationListener:" + (myLocationListener != null?"NOT NULL":"NULL"));
            updateMyLocationIcon();
        }
        //End Add for Autorotate part 3

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void updateCamera(float bearing) {
        //Add for Autorotate part 4
        if (map == null)
            return;
        CameraPosition oldPos = map.getCameraPosition();

        CameraPosition pos = CameraPosition.builder(oldPos).bearing(bearing).build();
        //if Added at 1400-01-01 because of unwanted camera movement in new Androids
        if (!(oldPos.target.latitude == pos.target.latitude && oldPos.target.longitude == pos.target.longitude && Math.abs(oldPos.bearing - pos.bearing) < 0.1))
            map.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
        //End Add for Autorotate part 5
    }

    //start added for navigation

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    //HHH 1400-08-11 Move from MainActivity,
//    public void backToMapPageAndShowNewTrackDialog(){
//        backToMapPage();
//        if (dialogRecordTrack.getIsRecording()){
//            Toast.makeText(getApplicationContext(), "مسیر از قبل در حال ذخیره سازی می باشد", Toast.LENGTH_LONG).show();
//        }
//        else{
//            btnAdd_Click();
//        }
//    }


    List<LatLng> mapBoundsToShow = new ArrayList<>();
    Polyline routeBoundsToShow = null;

    public void setResultFromMapSelect(Intent data) {
        if (data != null && data.getExtras() != null) {
            Double latN = data.getDoubleExtra("latn", 0);
            Double latS = data.getDoubleExtra("lats", 0);
            Double lonE = data.getDoubleExtra("lone", 0);
            Double lonW = data.getDoubleExtra("lonw", 0);
            float zoom = data.getFloatExtra("zoom", 0);
            boolean showbounds = data.getBooleanExtra("bounds", false);


            if (latN == 0)
                return;

            if (showbounds) {
                mapBoundsToShow.clear();
                mapBoundsToShow.add(new LatLng(latN, lonW));
                mapBoundsToShow.add(new LatLng(latN, lonE));
                mapBoundsToShow.add(new LatLng(latS, lonE));
                mapBoundsToShow.add(new LatLng(latS, lonW));
                mapBoundsToShow.add(new LatLng(latN, lonW));

                if (routeBoundsToShow != null)
                    routeBoundsToShow.remove();

                routeBoundsToShow = map.addPolyline(new PolylineOptions()
                        .width(10)
                        .color(Color.GRAY)
                        .geodesic(false)
                        .zIndex(3));
                routeBoundsToShow.setPoints(mapBoundsToShow);
            }
            LatLng sydney = new LatLng((double) ((latN - latS) / 2 + latS), ((lonE - lonW) / 2 + lonW));
//              map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, zoom));
            updateMyLocationIcon();
        }
    }

    public void onDestroyInChild(){

    }


    public void onResumeInChild() {
        try {
            Log.e("بازگشت", "OnResume Fired at MapPage" + " LOCATION MANAGER:" + (locationManager != null?"NOT NULL":"NULL")+" myLocationListener:" + (myLocationListener != null?"NOT NULL":"NULL"));
            //Add for Autorotate part 6
            mSensorManager.registerListener(this, mRotVectSensor, SensorManager.SENSOR_STATUS_ACCURACY_LOW);
            //End Add for Autorotate part 6

            //1400-11-16 added, for be sure to update Location Access after resume
            checkLocation(false);

        }
        catch (Exception ex){
            Log.e("خطا" , ex.getMessage());
            ex.printStackTrace();
            TTExceptionLogSQLite.insert(ex.getMessage(), ex.getStackTrace().toString(), PrjConfig.frmMapPage, 1001);
        }
    }

    public boolean OnMapReadyCompleted = false;

    @Override
    public boolean onBackPressedInChild() {
        if (scrollSearchMode.getVisibility() == View.VISIBLE){
            scrollSearchMode.setVisibility(View.GONE);
            typeSearchMode = false;
            return false;
        }
        return super.onBackPressedInChild(); //true
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {//3nd Event
        return inflater.inflate(R.layout.activity_searchonmap, parent, false);
    }

    public void onPauseInChild() {
        //Add for Autorotate part 7
        // unregister listener
        mSensorManager.unregisterListener(this);
        //End Add for Autorotate part 7
    }


    //---------------------- شروع قمست های اضافه جستجو کردن ---------------------
    private void btnSearch_Click() {
        if (!hutilities.isInternetConnected(context)) {
            projectStatics.showDialog(context, this.getResources().getString(R.string.NoInternet), this.getResources().getString(R.string.NoInternet_Desc), this.getResources().getString(R.string.ok), view -> {}, "", null);
            return;
        }

        hutilities.hideKeyboard(context, txtSearch);
        typeSearchMode = true;
        scrollSearchMode.setVisibility(View.VISIBLE);

        pageProgressBar.setVisibility(View.VISIBLE);

        SearchRequestDTO requestDTO = new SearchRequestDTO();
        requestDTO.Filter = txtSearch.getText().toString() + "***";
        requestDTO.Filter += "***";//Bounds
        requestDTO.Filter += "***";//PoiType
        if (currentLatLon != null && currentLatLon.latitude != 0 && currentLatLon.longitude != 0){
            requestDTO.Filter += Double.toString(currentLatLon.latitude) + "," +Double.toString(currentLatLon.longitude) +"***";//myLatLon
        }
        else
            requestDTO.Filter += "***";//myLatLon
        Call<ResponseBody> call = app.apiMap.SearchNbPoi(SimpleRequest.getInstance(requestDTO));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!isAdded()) return;
                try {
                    divSearch.setVisibility(View.VISIBLE);
                    pageProgressBar.setVisibility(View.GONE);
                    if (response.isSuccessful()) {

                        NbPoiList result = NbPoiList.fromBytes(response.body().bytes());
                        if (!result.isOk) {
                            txtSearchResult.setVisibility(View.VISIBLE);
                            txtSearchResult.setText(result.message);
                        } else {
                            txtSearchResult.setVisibility(View.GONE);
                        }
                        if (result.resList.size() > 0) {
                            btnShowResultOnMap.setVisibility(View.VISIBLE);
                            rvSearchResult.setVisibility(View.VISIBLE);
                            initAdapterSearch(result.resList);
                            AddResultsToMap(adapterSearch.data);
                        } else {
                            btnShowResultOnMap.setVisibility(View.GONE);
                            rvSearchResult.setVisibility(View.GONE);
                        }
                    } else {
                        txtSearchResult.setVisibility(View.VISIBLE);
                        txtSearchResult.setText("متاسفانه در برقراری ارتباط با سرور مشکلی به وجود آمده است. لطفا بعدا مجددا تلاش نمایید.");
                        Log.e("MY_ERROR", "ResponseCODE: " + response.code());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Log.e("MY_ERROR", ex.getMessage());
                    TTExceptionLogSQLite.insert(ex.getMessage(), "", PrjConfig.frm_SearchOnMap, 100);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                TTExceptionLogSQLite.insert(t.getMessage(), "", PrjConfig.frm_SearchOnMap, 100);
                if (!isAdded()) return;
                divSearch.setVisibility(View.VISIBLE);
                pageProgressBar.setVisibility(View.GONE);
                txtSearchResult.setVisibility(View.VISIBLE);
                txtSearchResult.setText("متاسفانه مشکلی در برقراری ارتباط با سرور به وجود آمده است. لطفا بعدا مجددا تلاش نمایید.");

            }
        });


    }
    void initAdapterSearch(List<NbPoi> result) {
        if (true || adapterSearch == null) {

            NbPoisAdapter.OnItemClickListener itemClickListener = (post, Position) -> {
                RecyclerView_ItemClicked(post, Position);
            };
            adapterSearch = new NbPoisAdapter(context, "full",itemClickListener);
            adapterSearch.setData(result);
            rvSearchResult.setAdapter(adapterSearch);
        }
        if (rvSearchResult.getAdapter() == null) {

        }
    }
    public void RecyclerView_ItemClicked(NbPoi current, int index){
        NbPoi poi  = adapterSearch.data.get(index);
        hutilities.hideKeyboard((Activity)context);
        currentSelectedNbPoiIndex = index;

        LatLng position = null;
        if (NbPoi.Enums.PoiType_Track == poi.PoiType || poi.PoiType == NbPoi.Enums.PoiType_Route) {
            position = new LatLng(poi.LatBegin, poi.LonBegin);
        }
        else{
            position = new LatLng(poi.LatS, poi.LonW);
        }

        scrollSearchMode.setVisibility(View.GONE);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 14));
    }
    private void initRecyclerView() {
        rvSearchResult.addOnItemTouchListener(new RecyclerTouchListener(getContext(), rvSearchResult, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //1400-01-12 commented
//                Intent i = new Intent(getContext(), TourShowOne.class);
//                i.putExtra("ix", position);
//                i.putExtra("isMyClubTours", 0);
//                startActivity(i);

            }

            @Override
            public void onLongClick(View view, int position) {
                //TTClubTour selectedTour = dbConstantsTara.getTours().get(position);
                //Toast.makeText(getContext(), selectedTour.getName() + " is long selected!", Toast.LENGTH_SHORT).show();

            }
        }));
    }
    public static class NbPoisAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<NbPoi> data;
        private Context context;
        private LayoutInflater layoutInflater;

        OnItemClickListener itemClickFunction;
        String mode;

        public NbPoisAdapter(Context context, String _mode, OnItemClickListener _itemClickFunction) {
            this.mode = _mode;
            this.data = new ArrayList<>();
            this.context = context;
            this.itemClickFunction = _itemClickFunction;
        }

        @Override
        public NbPoiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            layoutInflater = LayoutInflater.from(context);
            //this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = layoutInflater.inflate(R.layout.activity_searchonmap_item, parent, false);
            return new NbPoiViewHolder(itemView, this.itemClickFunction);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            if (viewHolder instanceof NbPoiViewHolder) {
                ((NbPoiViewHolder) viewHolder).bind(data.get(position), position);
            } else if (1 == 2) {
                //private void showLoadingView(LoadingViewHolder viewHolder, int position) {
                //        //ProgressBar would be displayed
                //
                //    }
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public void setData(List<NbPoi> newData) {
            if (data != null) {
//                NbPoiDiffCallback postDiffCallback = new NbPoiDiffCallback(data, newData);
//                DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(postDiffCallback);

                data.clear();
                data.addAll(newData);
                //diffResult.dispatchUpdatesTo(this);
                notifyDataSetChanged();
            } else {
                // first initialization
                data = newData;
            }
        }


        //من اضافه کردم
        public abstract interface OnItemClickListener {
            public abstract void onItemClicked(NbPoi post, int Position);
        }
        class NbPoiViewHolder extends RecyclerView.ViewHolder {
            public View itemView;
            public TextView txtTitle, lblDistance, lblSecondLine;
            ImageView txt_ct_ImageLinkUri;
            public ProgressBar progressBarIndet;
            LinearLayout itemMainPart;
            OnItemClickListener itemClickFunction;

            NbPoiViewHolder(View itemView, OnItemClickListener itemClickFunction) {
                super(itemView);
                this.itemView = itemView;
                this.itemClickFunction = itemClickFunction;
                txtTitle = itemView.findViewById(R.id.txtTitle);
                lblDistance = itemView.findViewById(R.id.lblDistance);
                itemMainPart = itemView.findViewById(R.id.itemMainPart);
                lblSecondLine = itemView.findViewById(R.id.lblSecondLine);
                txt_ct_ImageLinkUri = itemView.findViewById(R.id.txtImage);
                progressBarIndet = itemView.findViewById(R.id.itemprogressbarIndet);

                Drawable progressDrawable = progressBarIndet.getIndeterminateDrawable().mutate(); //or getProgressDrawable for normal //https://stackoverflow.com/questions/2020882/how-to-change-progress-bars-progress-color-in-android
                progressDrawable.setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
                progressBarIndet.setProgressDrawable(progressDrawable);

                lblDistance.setTypeface(projectStatics.getIranSans_FONT(context));
                txtTitle.setTypeface(projectStatics.getIranSans_FONT(context));
                lblSecondLine.setTypeface(projectStatics.getIranSans_FONT(context));
            }


            void bind(final NbPoi currentObj, int position) {
                if (currentObj != null) {
                    txtTitle.setText(currentObj.Name + (currentObj.AltName.length() > 0?" ("+currentObj.AltName + ")":""));
                    lblDistance.setText(currentObj.distanceFromHere);
                    lblSecondLine.setText(currentObj.addedInfo);

                    itemMainPart.setOnClickListener(view -> {
                        itemClickFunction.onItemClicked(currentObj, position);

//                        NbPoi currentSelectedClub  = adapterSearch.data.get(position);
//                        hutilities.hideKeyboard((Activity)context);
//                        currentSelectedClubIndex = position;
//                        ClubView_Home currentHomeFragment = new ClubView_Home(currentSelectedClub, PrjConfig.frmClubSearch);
//                        ((MainActivityManager) context).showFragment(currentHomeFragment);
                    });

                    Picasso builder = Picasso.with(context);
//                builder.load(currentObj.Logo).tag(PicassoOnScrollListener.TAG)//.config(Bitmap.Config.RGB_565).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
//                        //.placeholder((R.drawable.ic_launcher_background)) HHH 1400-01-10
//                        //.error(R.drawable.ic_launcher_background)  HHH 1400-01-10
//                        .transform(new PicassoCircleTransform()).into(txt_ct_ImageLinkUri);
                }
            }
        }

        class NbPoiDiffCallback extends DiffUtil.Callback {

            private final List<NbPoi> oldNbPois, newNbPois;

            public NbPoiDiffCallback(List<NbPoi> oldNbPois, List<NbPoi> newNbPois) {
                this.oldNbPois = oldNbPois;
                this.newNbPois = newNbPois;
            }

            @Override
            public int getOldListSize() {
                return oldNbPois.size();
            }

            @Override
            public int getNewListSize() {
                return newNbPois.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return oldNbPois.get(oldItemPosition).ServerId == newNbPois.get(newItemPosition).ServerId;
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return oldNbPois.get(oldItemPosition).equals(newNbPois.get(newItemPosition));
            }
        }
    }
    //---------------------- پایان قمست های اضافه جستجو کردن ---------------------

public static class InfoBottom extends BottomSheetDialogFragment{
        public NbPoi poi;
        View view;
        public InfoBottom(){
        }
        public InfoBottom(NbPoi poi){
            this.poi = poi;
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
            view =  inflater.inflate(R.layout.activity_searchonmap_bottom, parent, false);
            return view;
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            TextView txtSelectedName = view.findViewById(R.id.txtSelectedName);
            txtSelectedName.setText(poi.Name);
        }
    }

    public void setBtnGotoCurrentLocation(){
        if (currentLatLon == null || (currentLatLon.latitude == 0 && currentLatLon.longitude == 0))
            btnGotoCurrentLocation.setText(NO_LOCATION);
        if (isTrackUp)
            btnGotoCurrentLocation.setText(TRACK_UP);
        else
            btnGotoCurrentLocation.setText(NORTH_UP);
    }
}
