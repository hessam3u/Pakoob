package mojafarin.pakoob;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
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
import android.location.GnssStatus;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
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
import com.google.android.material.navigation.NavigationView;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bo.NewClasses.SimpleRequest;
import bo.entity.NbCurrentTrack;
import bo.entity.NbLogSearch;
import bo.entity.NbPoi;
import bo.entity.NbPoiCompact;
import bo.entity.NbPoiList;
import bo.entity.SearchRequestDTO;
import bo.sqlite.NbCurrentTrackSQLite;
import bo.sqlite.NbLogSearchSQLite;
import bo.sqlite.NbPoiSQLite;
import bo.sqlite.TTExceptionLogSQLite;
import hmapscaleview.MapScaleView;
import maptools.GPXFile;
import maptools.MapCenterPointer;
import maptools.MapMyLocationIcon;
import maptools.TrackData;
import maptools.TrackInBackgroundService;
import maptools.TrackInBackgroundService_Restarter;
import maptools.TrackProperties;
import maptools.hMapTools;
import mojafarin.pakoob.mainactivitymodes.AddWaypointMode;
import mojafarin.pakoob.mainactivitymodes.DialogRecordTrack;
import mojafarin.pakoob.mainactivitymodes.GoToTargetMode;
import mojafarin.pakoob.mainactivitymodes.RouteDesignMode;
import mojafarin.pakoob.mainactivitymodes.SightNGoMode;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import user.CompleteRegister;
import user.Register;
import utils.HFragment;
import utils.MainActivityManager;
import utils.MyDate;
import utils.PrjConfig;
import utils.RecyclerTouchListener;
import utils.TextFormat;
import utils.hutilities;
import utils.projectStatics;

import static mojafarin.pakoob.MainActivity.isLockOnMe;
import static mojafarin.pakoob.MainActivity.isTrackUp;
import static mojafarin.pakoob.MainActivity.currentLatLon;
import static mojafarin.pakoob.MainActivity.currentElev;
import static mojafarin.pakoob.MainActivity.currentSpeed;
import static mojafarin.pakoob.MainActivity.cameraLatLon;
import static mojafarin.pakoob.MainActivity.BearingTextFormCurrent;
import static mojafarin.pakoob.MainActivity.DistanceTextFormCurrent;
import static mojafarin.pakoob.MainActivity.BearingValueFormCurrent;
import static mojafarin.pakoob.MainActivity.DistanceValueFormCurrent;
import static mojafarin.pakoob.MainActivity.map;

public class MapPage extends HFragment implements SensorEventListener, NavigationView.OnNavigationItemSelectedListener {
    public static TrackProperties trackProperties;

    public static final int MAP_CLICK_MODE_NONE = 0;
    public static final int MAP_CLICK_MODE_ROUTE = 1;
    public static final int MAP_CLICK_MODE_WAYPOINT = 2;

    MapScaleView scaleView;
    SupportMapFragment mapFragment;

    int lastCameraMoveReason = 0;
    boolean showCustomMaps = true;
    MenuItem nv_exit_user, menu_profile;

    boolean firstLocationObserved = false;

    GoToTargetMode goToTargetMode;
    SightNGoMode sightNGoMode;
    RouteDesignMode routeDesignMode;
    AddWaypointMode addWaypointMode;
    DialogRecordTrack dialogRecordTrack;
    public static boolean IsInSightNGoMode = false;
    public int IsInAddWaypointMode = 0;
    MainActivity mainActivity;

    //Commented at 1401-04-16
//    public MapPage(MainActivityManager context) {
//        this.context = context;
//        mainActivity = (MainActivity) context;
//        initSensors();
//        dialogRecordTrack = new DialogRecordTrack((MainActivity) context, this);
//    }
    public MapPage() {
        Tag = "????????_????????";
    }

    public static MapPage getInstance(MainActivityManager context) {
        MapPage res = new MapPage();
        //res.context = context;
        //moved to Initialize Components at 1401-05-10
//        res.mainActivity = (MainActivity) context;
//        res.initSensors();
//        res.dialogRecordTrack = new DialogRecordTrack((MainActivity) context, res);
        return res;
    }

    View view;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {//4th Event
        super.onViewCreated(view, savedInstanceState);
        this.view = view;

        initializeComponents(view);

        //Also called after login
        setMenuVisibility();

        //initSensors();

        checkLocation(true);
    }

    DrawerLayout nav_drawer;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;

    FloatingActionButton btnSelectMap, btnAdd, btnSelectLayer, btnShowCustomMaps, btnShowCalcuations;
    TextView lblNav_header_subtitle;
    MapCenterPointer viewCenterPointer;
    TextView txtPosition, txtSpeed, txtDistanceFromCurrent, txtBearingFromCurrent;
    public TextView btnGotoCurrentLocation, btnZoomOut, btnZoomIn;
    LinearLayout mainLinearLayout;
    CoordinatorLayout coordinator;
    MapMyLocationIcon mapMyLocationIcon;

    TextView btnMainActivity_GotoHome;

    //Start------Search Related Objects
    EditText txtSearch;
    private RecyclerView rvSearchResult;
    private MapPage.NbPoisAdapter adapterSearch;
    boolean typeSearchMode = false;
    int currentSelectedNbPoiIndex = -1;
    private ScrollView scrollSearchMode;
    LinearLayout divSearch;
    TextView txtSearchResult;
    private ProgressBar pageProgressBar;
    Button btnShowResultOnMap;
    //End------ Search Related Objects

    @Override
    public void initializeComponents(View v) {
        //Moved here at 1401-05-10 from constructor
        mainActivity = (MainActivity) context;
        initSensors();
        dialogRecordTrack = new DialogRecordTrack((MainActivity) context, this);
        dialogRecordTrack.initializeComponentes();


        sightNGoMode = new SightNGoMode((MainActivity) context, this);
        routeDesignMode = new RouteDesignMode((MainActivity) context);
        addWaypointMode = new AddWaypointMode((MainActivity) context, this);
        goToTargetMode = new GoToTargetMode((MainActivity) context);


        mainLinearLayout = v.findViewById(R.id.mainLinearLayout);
        coordinator = v.findViewById(R.id.coordinator);


        mapMyLocationIcon = v.findViewById(R.id.mapMyLocationIcon);

        //1399-12-24 Tooye re-su-me ham ejra mishe dige, niazi behesh nist---------- Felan for test active kardamesh
//        if (dialogRecordTrack.getIsRecordPanelActive()){
//            trackingServiceResume();//Comented at 1400-10-21 ???? ?????????? ?????????? ?????????? ?????????? ?????? ????????
//        }
        Toolbar toolbar = v.findViewById(R.id.toolbar);
        context.setSupportActionBar(toolbar);
        nav_drawer = v.findViewById(R.id.drawer_layout);
        navigationView = v.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(context, nav_drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        nav_drawer.addDrawerListener(toggle);
        toolbar.getBackground().setAlpha(0);
        context.getSupportActionBar().setDisplayShowTitleEnabled(false); //remove toolbar Title

        nv_exit_user = navigationView.getMenu().findItem(R.id.nv_exit_user);
        menu_profile = navigationView.getMenu().findItem(R.id.menu_profile);

        DistanceTextFormCurrent = getResources().getString(R.string.distanceFromCurrentTextAtMainActivity);
        BearingTextFormCurrent = getResources().getString(R.string.bearingFromCurrentTextAtMainActivity);

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        lblNav_header_subtitle = navigationView.getHeaderView(0).findViewById(R.id.lblNav_header_subtitle);


        btnSelectMap = v.findViewById(R.id.btnSelectMap);
        btnSelectMap.setImageBitmap(projectStatics.textAsBitmapFontello("\uF278", 80, Color.GREEN, context.getApplicationContext()));
        btnSelectMap.setOnClickListener(view -> {
            context.showFragment(new MapSelect());
        });
        btnMainActivity_GotoHome = v.findViewById(R.id.btnMainActivity_GotoHome);
        btnMainActivity_GotoHome.setOnClickListener(view -> {
//            backToMapPage();
            //context.showFragment(new Home());
            context.backToHome();
        });
        txtSearch = v.findViewById(R.id.txtSearch);
        txtSearch.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                btnSearch_Click();
                return true;
            }
            return true;
        });
        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (txtSearch.getText().length() == 0) {
                    clearAllSearchResults();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        txtSearch.setOnFocusChangeListener((view1, hasFocus) -> {
        });
        btnShowResultOnMap = v.findViewById(R.id.btnShowResultOnMap);
        btnShowResultOnMap.setOnClickListener(view1 -> {
            AddResultsToMap(adapterSearch.data);
            GoToMapForAllResults();
        });
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
        btnAdd = v.findViewById(R.id.btnAdd);
        btnAdd.setImageBitmap(projectStatics.textAsBitmapFontello("\uE817", 70, Color.RED, context.getApplicationContext()));
        btnAdd.setOnClickListener(view -> {
            btnAdd_Click();
        });
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


        routeDesignMode.initRouteDesignPanel();
//
        addWaypointMode.initAddWaypointPanel();

        scaleView = v.findViewById(R.id.scaleView);
        scaleView.metersOnly();

        viewCenterPointer = v.findViewById(R.id.viewCenterPointer);
        txtPosition = v.findViewById(R.id.txtPosition);
        txtPosition.setOnClickListener(view1 -> {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(getString(R.string.PositionOfCenter), txtPosition.getText());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context.getApplicationContext(), getString(R.string.PositionOfCenter_Copied), Toast.LENGTH_LONG).show();
        });
        txtSpeed = v.findViewById(R.id.txtSpeed);
        txtBearingFromCurrent = v.findViewById(R.id.txtBearingFromCurrent);
        txtDistanceFromCurrent = v.findViewById(R.id.txtDistanceFromCurrent);
        txtPosition.setText("");
        txtSpeed.setText(getResources().getString(R.string.SearchingSatellite));
        txtBearingFromCurrent.setText("");
        txtDistanceFromCurrent.setText("");

        btnGotoCurrentLocation = v.findViewById(R.id.btnGotoCurrentLocation);
        btnGotoCurrentLocation.setOnClickListener(view -> {
            if (currentLatLon != null) {
                if (isLockOnMe) {
                    //?????? ?????? ???? ?????? ???????? ?????? ???? ???????? ?????? ???????? ???? ?????? ???? ?? ???????? ???? ???? ?????? ????
                    isTrackUp = !isTrackUp;
                }
                //???? ???? ???????? ???? ???? ?????? ??????
                isLockOnMe = true;
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLon, map.getCameraPosition().zoom));
                if (!isTrackUp)
                    updateCamera(0);
                updateMyLocationIcon();
                setBtnGotoCurrentLocation();

            } else {
                projectStatics.showDialog(context, getResources().getString(R.string.WaitUntilSatelliteFound_Title)
                        , getResources().getString(R.string.WaitUntilSatelliteFound_Desc), getResources().getString(R.string.ok), null, "", null);
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

    public void clearAllSearchResults() {
        int sz = searchMarkers.size();
        for (int i = 0; i < sz; i++) {
            searchMarkers.get(i).remove();
        }
        adapterSearch.setData(new ArrayList<>());
    }

    @Override
    public void onFragmentShown() {
        Log.e(Tag, "?????????? ????????????" + "Map is Shown");
        boolean helpSeen = app.session.getMapHelpSeen();
        if (!helpSeen) {
            projectStatics.showDialog(context, getResources().getString(R.string.ShowHelpAtMap_Title), getResources().getString(R.string.ShowHelpAtMap_Desc), getResources().getString(R.string.Yes), view -> {
                ShowHelpSpotlight();
            }, getResources().getString(R.string.Later), null);
        }
        //1400-11-16 added, for be sure to update Location Access after resume
        if ((hutilities.checkGooglePlayServiceAvailability(context) && fusedLocationClient == null)
                || (!hutilities.checkGooglePlayServiceAvailability(context) && (locationManager == null || myLocationListener == null)))
         {
            checkLocation(true);
        }
    }

    public void ShowHelpSpotlight() {
        SimpleTarget btnAddTarget = new SimpleTarget.Builder(context)
                .setPoint(btnAdd) // position of the Target. setPoint(Point point), setPoint(View view) will work too.
                .setRadius(110f) // radius of the Target
                .setTitle("?????????? ???????? ???????????? ????????") // title
                .setDescription("?????? ???????? ?????? ?????? ???????? ???????? ???????? ?????????? ???? ??... ?????? ???? ?????? ?????????? ???????? ????????") // description
                .build();
        SimpleTarget btnSelectMapTarget = new SimpleTarget.Builder(context)
                .setPoint(btnSelectMap) // position of the Target. setPoint(Point point), setPoint(View view) will work too.
                .setRadius(110f) // radius of the Target
                .setTitle("???????????? ???????? ????") // title
                .setDescription("???????? ?????? ?????????????? ???? ???? ?????? ???????? ???????????? ????") // description
                .build();
        SimpleTarget btnSelectLayerTarget = new SimpleTarget.Builder(context)
                .setPoint(btnSelectLayer) // position of the Target. setPoint(Point point), setPoint(View view) will work too.
                .setRadius(110f) // radius of the Target
                .setTitle("?????? ???????? ????????") // title
                .setDescription("?????? ???????? ???????? ???? ???? ?????? ?????? ???????????? ?????????? ??????") // description
                .build();
        SimpleTarget btnShowCustomMapsTarget = new SimpleTarget.Builder(context)
                .setPoint(btnShowCustomMaps) // position of the Target. setPoint(Point point), setPoint(View view) will work too.
                .setRadius(110f) // radius of the Target
                .setTitle("??????????/?????? ?????????? ???????? ?????? ??????????") // title
                .setDescription("???? ?????????? ???? ???????? ?????? ?????????? ???? ???????? ???????? ?????????? ???? ?????? ?????????? ?????? ???? ???? ???????? ???? ?????? ???????? ???? ??????") // description
                .build();
        SimpleTarget btnGotoCurrentLocationTarget = new SimpleTarget.Builder(context)
                .setPoint(btnGotoCurrentLocation)
                .setRadius(120f) // radius of the Target
                .setTitle("?????? ???? ???????????? ????????") // title
                .setDescription("?????? ???? ???? ???? ???????? ???????? ?? ???????? ?????????? ?????? ?????????? ?????? ?????????? ???????? ???? ???? ???????? ???????? ?????????? ?????????? ?? ?????????? ???? ???????? ????????") // description
                .build();
        SimpleTarget txtPositionTarget = new SimpleTarget.Builder(context)
                .setPoint(txtPosition) // position of the Target. setPoint(Point point), setPoint(View view) will work too.
                .setRadius(240f) // radius of the Target
                .setTitle("???????????? ???????? ????????") // title
                .setDescription("???????????? ???????? ???????? ?????????? ???? ????????. ?????? ???????? ???? ???? ?????? ??????")
                .build();
        SimpleTarget txtSpeedTarget = new SimpleTarget.Builder(context)
                .setPoint(txtSpeed) // position of the Target. setPoint(Point point), setPoint(View view) will work too.
                .setRadius(240f) // radius of the Target
                .setTitle("???????? ?? ???????????? ????????") // title
                .setDescription("???????? ???????????? ???? ???? ???? ?????????? ?????? ?????????? ?????? ???????? ???????? ?? ???????????? ?????????? ???? ???????? ????????")
                .build();
        Spotlight.with(context)
                .setDuration(250L) // duration of Spotlight emerging and disappearing in ms
                .setAnimation(new DecelerateInterpolator(5f)) // animation of Spotlight
                .setTargets(btnAddTarget, btnSelectMapTarget, btnSelectLayerTarget,
                        btnShowCustomMapsTarget, btnGotoCurrentLocationTarget, txtPositionTarget
                        , txtSpeedTarget) // set targes. see below for more info
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
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        } else {
            map.setMyLocationEnabled(false); //1399-12-07 changed to false, because of MyLocationIcon
        }
        map.getUiSettings().setCompassEnabled(true);//Removed after Custom Zoom and My Location
        ImageView compass = (ImageView) mapFragment.getView().findViewWithTag("GoogleMapCompass");
        compass.setOnClickListener(view1 ->
        {
            compass_Clicked();
        });
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
                //Toast.makeText((MainActivity) context, polyline.getTag().toString(), Toast.LENGTH_LONG);
                txtPosition.setText(getResources().getString(R.string.TrackWithNameOf) + " " +
                        polyline.getTag().toString());
            }
        });
        map.setOnMapLongClickListener(latLng -> {
            //BBBB
            NbPoi poi = NbPoi.getPoiForPosition(latLng.latitude, latLng.longitude, getString(R.string.Position));
//            NbPoi poi = new NbPoi();
//            poi.NbPoiId = 0l;
//            poi.LatS = latLng.latitude;
//            poi.LonW = latLng.longitude;
//            poi.Name = String.format(getString(R.string.Position) + " " + "%.4f,%.4f", latLng.latitude, latLng.longitude);
            ArrayList<NbPoi> list = new ArrayList<>();
            list.add(poi);
            AddResultsToMap(list);
            poiClicked(searchMarkers.get(0));
        });
        map.setOnMapClickListener(latLng -> {
            if (IsInAddWaypointMode == MAP_CLICK_MODE_WAYPOINT) {
                addWaypointMode.mapOnClick_Waypoint(latLng, false);
                return;
            }
//            if (RouteMode == MAP_CLICK_MODE_ROUTE)//1399-12-02
//                mapOnClick_Route(latLng);
        });
        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                if (RouteMode == MAP_CLICK_MODE_ROUTE)
                    routeDesignMode.mapDragStart_Route(marker);
            }

            @SuppressWarnings("unchecked")
            @Override
            public void onMarkerDragEnd(Marker marker) {
                if (RouteMode == MAP_CLICK_MODE_ROUTE)
                    routeDesignMode.mapDragEnd_Route(marker);
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                if (RouteMode == MAP_CLICK_MODE_ROUTE)
                    routeDesignMode.mapDraging_Route(marker);
            }
        });
        map.setOnMarkerClickListener(marker -> {
            if (IsInAddWaypointMode == MAP_CLICK_MODE_WAYPOINT) {
                addWaypointMode.mapOnClick_Waypoint(marker.getPosition(), false);
                return true;

            } else if (RouteMode == MAP_CLICK_MODE_ROUTE) {
                //Find and Select Route Marker if Exists
                String clickedId = marker.getId();
                int routeSize = routeDesignMode.routeMarkerIds.size();
                boolean find = false;
                for (int i = 0; i < routeSize && !find; i++) {
                    if (routeDesignMode.routeMarkerIds.get(i).equals(clickedId)) {
                        find = true;
                    }
                }
                if (find) {
                    routeDesignMode.markerClick_Route(marker, false);
                }

                return true;

            } else { //if normal Mode
                return poiClicked(marker);
            }
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
                addPOIToMap(poi, map, context);

            } catch (Exception ex) {
                TTExceptionLogSQLite.insert(ex.getMessage(), ex.getStackTrace().toString(), PrjConfig.frmMapPage, 1004);
                ex.printStackTrace();
            }
        }

        Log.e(Tag, "?????? ??????" + "100 - getIsRecordPanelActive: " + dialogRecordTrack.getIsRecordPanelActive());
        if (dialogRecordTrack.getIsRecordPanelActive()) {
            Log.e(Tag, "?????? ??????" + "?????? ???????? ?????? - 100");
            if (!trackResumeIsDone) {
                Log.e(Tag, "?????? ??????" + "?????? ???????? ?????? - 200");
                trackingServiceResume();
            }
//            if (dialogRecordTrack.veryCurrentRoutePoints.size() > 0) { //1400-10-21 commented
//                dialogRecordTrack.drawVeryCurrentRoute();
//            }
        }

        OnMapReadyCompleted = true;


//        mapOnClick_Route(new LatLng(36.2910, 59.5025));
//        mapOnClick_Route(new LatLng(36.2920, 59.5026));
//        mapOnClick_Route(new LatLng(36.2930, 59.5027));
//        mapOnClick_Route(new LatLng(36.2940, 59.5028));
//        mapOnClick_Route(new LatLng(36.2950, 59.5029));
//        mapOnClick_Route(new LatLng(36.2960, 59.5020));

    }

    private void compass_Clicked() {
        isTrackUp = false;
        updateCamera(0);
        updateMyLocationIcon();
        setBtnGotoCurrentLocation();
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
    public static Object addPOIToMap(NbPoiCompact poi, GoogleMap map, Context context) {
//        if(map!= null){
//
//        }
        if (poi.PoiType == NbPoi.Enums.PoiType_Folder)
            return null;
        if (poi.PoiType == NbPoi.Enums.PoiType_Track || poi.PoiType == NbPoi.Enums.PoiType_Route) {
            if (poi.polyLine == null) {
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
                LatLng position = new LatLng(poi.LatS, poi.LonW);

                int ResourceId = NbPoi.PoiTypeToResourcId(poi.PoiType, 3);
                BitmapDescriptor currentRouteIcon = hutilities.bitmapDescriptorFromVector(context, ResourceId);

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(position)
                        .title(poi.Name)
                        .zIndex(10000)
                        .icon(currentRouteIcon)
                        .flat(true);
                Marker addedMarker = map.addMarker(markerOptions);//(new MarkerOptions().position(point).title(poi.Name));
                addedMarker.setTag(poi.getNbPoi());
                poi.marker = addedMarker;
                return addedMarker;
            } else {
//                poi.marker.setVisible(true);
                return poi.marker;
            }
        }
    }

    // 1- Baraye Namayeshe ye Folder rooye Naghshe - SHOW_ON_MAP_CLICK
    // 2- Download Kardane Track
    public static List<Object> addPOIToMapRecursive(NbPoi poi, GoogleMap map, Context context) {
        List<Object> res = new ArrayList<>();

        if (poi.PoiType == NbPoi.Enums.PoiType_Folder) {
            List<NbPoi> childs = NbPoiSQLite.selectByLevel(poi.Level + 1, poi.NbPoiId);
            for (int i = 0; i < childs.size(); i++) {
                List<Object> added = addPOIToMapRecursive(childs.get(i), map, context);
                res.addAll(added);
            }
        } else if (true || poi.PoiType == NbPoi.Enums.PoiType_Track || poi.PoiType == NbPoi.Enums.PoiType_Route) {
            res.add(addPOIToMap(NbPoiCompact.getInstance(poi), map, context));
            //No Need to app.visiblePOIs.add(compact);
        }
        return res;
    }

    private void CameraMoveStarted(int reason) {
        lastCameraMoveReason = reason;
        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
            isLockOnMe = false;
            //isTrackUp = false;//HHH Lazeme???
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

            showLocation();
            setDownFields();
        }
    }

    private void CameraMoved() {
        //for showing Scale
        CameraPosition cameraPosition = map.getCameraPosition();
        scaleView.update(cameraPosition.zoom, cameraPosition.target.latitude);
        //End for showing Scale

        cameraLatLon = cameraPosition.target;

        showLocation();
        setDownFields();

        routeDesignMode.route_CalculateDistanceFromPrev();

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
    private android.location.LocationListener myLocationListener;
    public static Location location = null;
    GnssStatus.Callback gnssCallback;
    FusedLocationProviderClient fusedLocationClient;
    LocationRequest fusedLocationRequest;
    LocationCallback locationCallback;

    public void checkLocation(boolean showErrorIfNoPermission) {
        String serviceString = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) context.getSystemService(serviceString);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (showErrorIfNoPermission) {
                projectStatics.showDialog(context, getResources().getString(R.string.FineLocationDenied_Title), getResources().getString(R.string.FineLocationDenied_Desc), getResources().getString(R.string.ok), view -> {
                }, "", null);
            }
            return;
        }

        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!isGpsEnabled) {
            projectStatics.showDialog((MainActivity) context
                    , getResources().getString(R.string.locationIsOff)
                    , getResources().getString(R.string.locationIsOff_Desc)
                    , getResources().getString(R.string.ok), view -> {
                        hutilities.showSettingTpAccessLocation((MainActivity) context);
                    }, getResources().getString(R.string.cancel), null);
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            //changed at 1401-07-27 ???????? ?????? ???? ???????? ???????? ?????????? ???? ??????
            gnssCallback = new GnssStatus.Callback() {
                @Override
                public void onStarted() {
                    super.onStarted();
                    Log.e(Tag, "GGGGGGGGGGG - GPS STARTED: ");
                    setBtnGotoCurrentLocation();
                }

                @Override
                public void onStopped() {
                    super.onStopped();
                    currentLatLon = null;
                    location = null;
                    currentSpeed = 0;

                    Log.e(Tag, "GGGGGGGGGGG - GPS STOPPED: ");
                    locationHasChanged(location);
                    mainActivity.locationChangedFromMapPage(location);
//                    setBtnGotoCurrentLocation();
                }

                @Override
                public void onFirstFix(int ttffMillis) {
                    super.onFirstFix(ttffMillis);
                    Log.e(Tag, "GGGGGGGGGGG - GPS onFirstFix: ");
                }

                @Override
                public void onSatelliteStatusChanged(@NonNull GnssStatus status) {
                    super.onSatelliteStatusChanged(status);
                    //Log.e(Tag,"GGGGGGGGGGG - GPS onSatelliteStatusChanged: ");
                }
            };
            locationManager.registerGnssStatusCallback(gnssCallback);
        }

        //added 1401-09-10Fused
        if (hutilities.checkGooglePlayServiceAvailability(context)) {
            Log.e(Tag, "??????" + "F:checkLocation_Fired Internally " + " and myLocationListener: " + (myLocationListener == null ? "NULL" : "Not NULL"));
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
            fusedLocationRequest = LocationRequest.create();//new version will be Builder()
            fusedLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            fusedLocationRequest.setInterval(1000);
            fusedLocationRequest.setFastestInterval(1000);
            fusedLocationRequest.setSmallestDisplacement(3);
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }
                    location = locationResult.getLastLocation();
                    LocationChangedEvent_HUB(location);
                }

                @Override
                public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
                    super.onLocationAvailability(locationAvailability);
                }
            };

        }
        else {
            Log.e(Tag, "??????" + "N:checkLocation_Fired Internally " + " and myLocationListener: " + (myLocationListener == null ? "NULL" : "Not NULL"));
            myLocationListener = new LocationListener() {
                @SuppressLint("MissingPermission")
                public void onLocationChanged(Location locationListener) {
                    //1400-01-03 I thinks it is faster than traditional way whick commented in next lines
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    LocationChangedEvent_HUB(location);
                }

                public void onProviderDisabled(String provider) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N) {
                        switch (status) {
                            case GpsStatus.GPS_EVENT_STARTED:
                                Log.e(Tag, "GGGGGGGGGGG - GPS searching11111: ");
                                break;
                            case GpsStatus.GPS_EVENT_STOPPED:
                                Log.e(Tag, "GGGGGGGGGGG - GPS Stopped111111: ");
                                break;
                            case GpsStatus.GPS_EVENT_FIRST_FIX:
                                /* * GPS_EVENT_FIRST_FIX Event is called when GPS is locked */
                                Log.e(Tag, "GGGGGGGGGGG - GPS GPS_LOCKED11111: ");
                                break;
                            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                                Log.e(Tag, "GGGGGGGGGGG - GPS GPS_EVENT_SATELLITE_STATUS11111: ");
                                //                 System.out.println("TAG - GPS_EVENT_SATELLITE_STATUS");
                                break;
                        }
                    }
                }
            };

            //
            //added 1401-09-10Fused Moved to startLocationUpdates
//            Log.e(Tag, "??????" + " ?????????????? ???? ?????? ?????????? ???????????? ???? ???????? ????????- 150");
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, app.MinTrackTime, app.MinTrackDistance, myLocationListener);  //  here the min time interval and min distance
        }
        startLocationUpdates();
    }

    //added 1401-09-10Fused
    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        if (hutilities.checkGooglePlayServiceAvailability(context)) {
            Log.e(Tag, "??????" + " ?????????????? ???? ?????? ?????????? ???????????? ???? ???????? ????????- 150F");
            fusedLocationClient.requestLocationUpdates(fusedLocationRequest,
                    locationCallback
                    , Looper.getMainLooper());
        }
        else{
            Log.e(Tag, "??????" + " ?????????????? ???? ?????? ?????????? ???????????? ???? ???????? ????????- 150N");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, app.MinTrackTime, app.MinTrackDistance, myLocationListener);  //  here the min time interval and min distance
        }
    }

    //added 1401-09-10Fused
    public void LocationChangedEvent_HUB(Location location){
        //All Moved From Traditional Event
        Log.e(Tag, "??????" + "onLocationChangedCalled: " + (location == null ? "LOCATION IS NULL" : location.getLatitude()));
        if (location != null) {
            currentLatLon = new LatLng(location.getLatitude(), location.getLongitude());
            currentElev = location.getAltitude();
            currentSpeed = location.getSpeed();
        } else {
            currentLatLon = null;
            currentSpeed = 0;
        }

        setBtnGotoCurrentLocation();

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


    //float mDeclination = 0;//Add for Autorotate and moved to app.declination
    int locationCounter = 0;

    public void locationHasChanged(Location location) {
        Log.e(Tag, "??????" + "locationHasChanged Fired and firstLocationObserved is " + firstLocationObserved
                + (map == null ? " Map_IS_NULL" : " MAP_IS_OK"));
        if (location != null) {
            if (!firstLocationObserved) {
                firstLocationObserved = true;

                currentLatLon = new LatLng(location.getLatitude(), location.getLongitude());
                currentElev = location.getAltitude();
                currentSpeed= location.getSpeed();

                float zoom = 15f;
                app.session.setLastZoom(zoom);
                if (map != null) {
                    if (isLockOnMe)
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLon, zoom));
                    else
                        Toast.makeText(context, "???????????? ?????? ???????? ??????????...", Toast.LENGTH_LONG).show();
                }
            }

            locationCounter++;
            if (locationCounter % 20 == 0) {
                app.session.setLastAproxLocation(currentLatLon);
                app.session.setLastAproxLocationFixTime(MyDate.CalendarToCSharpDateTimeAcceptable(Calendar.getInstance()));
                app.session.setLastAproxLocationFixType((byte) 1);
                Log.e(Tag, "??????" + "??????????");
            }

            if (dialogRecordTrack.getIsRecording()) {
                dialogRecordTrack.drawNextPoint(currentLatLon, (float) location.getAltitude());
                Log.e(Tag,"??????" + "?????????? ???????? ????????");
            }

            //Start Map Works ................
            if (map == null)
                return;
            //for Center Screen myself
            if (isLockOnMe) {
                LatLng myLoc = new LatLng(location.getLatitude(), location.getLongitude());
                float zoom = map.getCameraPosition().zoom;
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLoc, zoom));
            }

            //Add for Autorotate
            // getDeclination returns degrees
            app.declination = hMapTools.RefreshSavedDelinationIfNeeded(
                    (float) location.getLatitude(),
                    (float) location.getLongitude(),
                    (float) location.getAltitude());
            //Log.d("Rotate----", Float.toString(app.declination));
            //End Add for Autorotate

        }//end if Location is not NULL

        updateMyLocationIcon();
        setBtnGotoCurrentLocation();
        //End for Center Screen myself
        showSpeedAndElev();
        setDownFields();

        viewCenterPointer.invalidate();
        if (goToTargetMode.Navigating && currentLatLon != null) {
            goToTargetMode.showNavigation();
        }
    }

    private void updateMyLocationIcon() {
        //Get map projection
        if (map == null)
            return;
//        if (counterTestingAndRemoveBeshe % 20 == 0)
//            Log.e("??????", "updateMyLocationIcon_CALLED and " + (currentLatLon != null?currentLatLon.latitude:"false"));
        Projection projection = map.getProjection();

//Convert LatLng to on screen location
        Point p1 = new Point();
//projection.toPixels(gp, p1);
        if (currentLatLon != null) {
            mapMyLocationIcon.setVisibility(View.VISIBLE);
            p1 = projection.toScreenLocation(currentLatLon);
            mapMyLocationIcon.setX(p1.x - mapMyLocationIcon.XMid);//45
            mapMyLocationIcon.setY(p1.y - mapMyLocationIcon.YMid);//35
            mapMyLocationIcon.bearingRelatedToMap = isTrackUp && isLockOnMe ? 0 : bearing - map.getCameraPosition().bearing;
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

    void showSpeedAndElev() {
        if (location == null || (location.getLatitude()== 0 && location.getLongitude() == 0))
            txtSpeed.setText(context.getResources().getString(R.string.SearchingSatellite));
        else
            txtSpeed.setText("????????: " + (int) (location.getSpeed() * 3.6) + "km/h " + "????????????: " + (int) location.getAltitude() + "m");
    }

    void showLocation() {
        String infoText = hMapTools.LocationToString(cameraLatLon, app.CurrentPositionFormat, hMapTools.LocationToStringStyle.Inline);
        txtPosition.setText(infoText);
    }

    void setDownFields() {
        if (cameraLatLon != null && currentLatLon != null && !isLockOnMe) {
            DistanceValueFormCurrent = hMapTools.distanceBetweenFriendly(cameraLatLon.latitude, cameraLatLon.longitude, currentLatLon.latitude, currentLatLon.longitude);
            //txtDistanceFromCurrent.setText(DistanceTextFormCurrent + DistanceValueFormCurrent);
            BearingValueFormCurrent = /*"??" +(app.currentNorth == hMapTools.NORTH_MAG?"m":"t") +*/ Integer.toString((int) (hMapTools.GetAzimuthInDegree(currentLatLon, cameraLatLon) + (app.currentNorth == hMapTools.NORTH_MAG ? app.declination : 0)));
            //txtBearingFromCurrent.setText(BearingTextFormCurrent + "??" + BearingValueFormCurrent);
        } else {
            DistanceValueFormCurrent = "";
            BearingValueFormCurrent = "";
            //txtDistanceFromCurrent.setText(DistanceTextFormCurrent + "0m");
            //txtBearingFromCurrent.setText(BearingTextFormCurrent + "0?? MN");
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
            //Log.e(Tag, "onSensorChanged Called");
            if (true || isTrackUp || IsInSightNGoMode || goToTargetMode.Navigating) {
                SensorManager.getRotationMatrixFromVector(
                        mRotationMatrix, event.values);
                float[] orientation = new float[3];
                SensorManager.getOrientation(mRotationMatrix, orientation);
                if (Math.abs(Math.toDegrees(orientation[0]) - angle) > 0) {
                    angle = Math.toDegrees(orientation[0]);
                    bearing = (float) angle + app.declination;

                    if (isTrackUp && isLockOnMe) {// ?????? ???????????? ???? ???????? ?????? ?????? ?????? ???? ???????? ?????????? ?????????? ?????????? isLockOnMe ?????? ??????
                        updateCamera(bearing);
                    } else {
                        updateMyLocationIcon();
                    }

                } else
                    angle = Math.toDegrees(orientation[0]);
            }

            if (goToTargetMode.Navigating && currentLatLon != null) {
                goToTargetMode.showNavigation();
            }

//        if (counterTestingAndRemoveBeshe % 20 == 0)
//            Log.e("??????", "updateMyLocationIcon_CALLED inonSensorChanged and " + (currentLatLon != null?currentLatLon.latitude:"false") + " LOCATION MANAGER:" + (locationManager != null?"NOT NULL":"NULL")+" myLocationListener:" + (myLocationListener != null?"NOT NULL":"NULL"));
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

        CameraPosition pos = null;
        if (true) {
            pos = CameraPosition.builder(oldPos).bearing(bearing).build();
        } else {
            pos = CameraPosition.builder(oldPos).build();
        }
        //if Added at 1400-01-01 because of unwanted camera movement in new Androids
        if (!(oldPos.target.latitude == pos.target.latitude && oldPos.target.longitude == pos.target.longitude && Math.abs(oldPos.bearing - pos.bearing) < 0.1))
            map.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
        //End Add for Autorotate part 5
    }

    public void btnGo_Click() {
        if (addWaypointMode.AddWaypointMarker != null) {
            LatLng target = addWaypointMode.AddWaypointMarker.getPosition();
            goToTargetMode.initNavigateToPoint(new LatLng(target.latitude, target.longitude), addWaypointMode.editingWaypoint
                    , 0);

            addWaypointMode.discardWaypoint_Click(false);
        }
    }

    //------------------------------------------- Add DIALOG----------------------------------
    LinearLayout btnDialog_PlotRoute, btnDialog_AddPOI, btnDialog_SightNGo, btnDialog_RecordTrack;

    AlertDialog btnAddDialog = null;
    private void btnAdd_Click() {
        if (btnAddDialog != null && btnAddDialog.isShowing())
            return;

        AlertDialog.Builder builder = new AlertDialog.Builder((MainActivity) context);
        ViewGroup viewGroup = view.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from((MainActivity) context).inflate(R.layout.dialog_add_on_activitymain, viewGroup, false);
        builder.setView(dialogView);
        btnAddDialog = builder.create();
        btnDialog_PlotRoute = dialogView.findViewById(R.id.btnDialog_PlotRoute);
        btnDialog_AddPOI = dialogView.findViewById(R.id.btnDialog_AddPOI);
        btnDialog_SightNGo = dialogView.findViewById(R.id.btnDialog_SightNGo);
        btnDialog_RecordTrack = dialogView.findViewById(R.id.btnDialog_RecordTrack);

        //true||
        if (dialogRecordTrack.getIsRecordPanelActive() ) {
            btnDialog_RecordTrack.setVisibility(View.GONE);
        } else {
            btnDialog_RecordTrack.setVisibility(View.VISIBLE);
        }

        btnAddDialog.show();

        btnDialog_PlotRoute.setOnClickListener(view -> {
            if (RouteMode == MAP_CLICK_MODE_ROUTE)
                return;
            else {
                RouteMode = MAP_CLICK_MODE_ROUTE;
                routeDesignMode.pnlRouteDesign.setVisibility(View.VISIBLE);
                routeDesignMode.initRouteDesignPanel();
            }
            btnAddDialog.dismiss();
        });
        btnDialog_AddPOI.setOnClickListener(view -> {
            btnAddWaypoint_Click(cameraLatLon);
            btnAddDialog.dismiss();
        });
        btnDialog_SightNGo.setOnClickListener(view -> {
            sightNGoMode.initSightNGoPanel();
            btnAddDialog.dismiss();
        });
        btnDialog_RecordTrack.setOnClickListener(view -> {
            //************* Same in  DialogRecordTrack.btnFinishRecording.setOnClickListener, MapPage. btnDialog_RecordTrack.setOnClickListener
            if (Build.VERSION.SDK_INT >= 29) {
                if (ActivityCompat.checkSelfPermission((Activity) context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT == 29) {
                        ActivityCompat.requestPermissions((Activity) context, new String[]{
                                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        }, PrjConfig.Location_BACKGROUND_PERMISSION_REQUEST_CODE);
                    } else {
                        projectStatics.showDialog((MainActivity) context
                                , getResources().getString(R.string.RequestBackgroundLocationInAndroid29_Title)
                                , getResources().getString(R.string.RequestBackgroundLocationInAndroid29_Title_Desc)
                                , getResources().getString(R.string.ok), view1 -> {
                                    hutilities.showAppSettingToChangePermission(context);
                                }
                                , "", null);
                    }
                }
            }
            dialogRecordTrack.startRecording();
            Toast.makeText(context.getApplicationContext(), "?????????? ???????? ???????? ????...", Toast.LENGTH_LONG).show();
            btnAddDialog.dismiss();
        });
    }

    public void btnAddWaypoint_Click(LatLng point) {
        if (IsInAddWaypointMode == MAP_CLICK_MODE_WAYPOINT) {
            //alertDialog.dismiss();
            return;
        } else {
            IsInAddWaypointMode = MAP_CLICK_MODE_WAYPOINT;
            addWaypointMode.pnlAddWaypoint.setVisibility(View.VISIBLE);
            addWaypointMode.initAddWaypointPanel();
            addWaypointMode.mapOnClick_Waypoint(point, true);
        }
        //alertDialog.dismiss();
    }

    void discardAllClickModes() {
        routeDesignMode.discardRoute(false);
        addWaypointMode.discardWaypoint_Click(false);
    }

//------------------------------------------------END Add DIALOG

    //Also called after login
    public void setMenuVisibility() {
        if (app.session.isLoggedIn()) {
            lblNav_header_subtitle.setText("?????????? " + app.session.getCCustomer().Mobile);
            nv_exit_user.setVisible(true);
            menu_profile.setVisible(false);
        } else {
            lblNav_header_subtitle.setText(R.string.nav_header_subtitle);
            nv_exit_user.setVisible(false);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        boolean result = false;//****************** mohem -> baraye Unselect shodane Item. item.setChecked(false) ham shayad ok bashe
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.menu_help: {
                context.showFragment(new InfoPage("help"));
                break;
            }
            case R.id.menu_contactus: {
                context.showFragment(new InfoPage("contactUs"));
                break;
            }
            case R.id.menu_privacypolicy: {
                context.showFragment(new InfoPage("privacypolicy"));
                break;
            }
            case R.id.menu_profile: {
                if (false && hutilities.CCustomerId != 0) {
                    context.showFragment(new CompleteRegister("menu"));
//                    Intent intent = new Intent(getApplicationContext(), CompleteRegister.class);
//                    intent.putExtra("mode", "menu");
//                    startActivity(intent);
                } else {
                    context.showFragment(new Register("menu"));
//                    Intent intent = new Intent(getApplicationContext(), Register.class);
//                    intent.putExtra("mode", "menu");
//                    startActivity(intent);
                }
                break;
            }
            case R.id.menu_settings: {
                context.showFragment(new SettingsActivity());
                break;
            }
            case R.id.menu_maps: {
                context.showFragment(new MapSelect());
                break;
            }
            case R.id.menu_mytracks: {
                context.showFragment(MyTracks.getInstance("menu", "", "", null));

//                Intent intent = new Intent(getApplicationContext(), MyTracks.class);
//                intent.putExtra("mode", "menu");
//                startActivityForResult(intent, ResultCode_ForMyTracks);
                break;
            }

            case R.id.nv_exit_user: {
                app.session.logoutUser((MainActivity) context);
                break;
            }
            case R.id.nv_exit_app: {
                projectStatics.showDialog((MainActivity) context
                        , getResources().getString(R.string.AreYouSureToExit_Title)
                        , getResources().getString(R.string.AreYouSureToExit_Desc)
                        , getResources().getString(R.string.ok), view -> {
                            ((MainActivity) context).finish();
                        }
                        , getResources().getString(R.string.cancel), null);
//                hessamTools.areYouSureToExitActivity(getApplicationContext(), this);
                break;
            }

        }
        //close navigation drawer
        nav_drawer.closeDrawer(GravityCompat.START);
        return result;
    }

    //start added for navigation

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //HHH 1400-08-11 Move from MainActivity,
        //    @Override
        //    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        //        super.onPostCreate(savedInstanceState);
        //        toggle.syncState();
        //    }
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //HHH 1400-08-11 Move from MainActivity,
//    public void backToMapPageAndShowNewTrackDialog(){
//        backToMapPage();
//        if (dialogRecordTrack.getIsRecording()){
//            Toast.makeText(getApplicationContext(), "???????? ???? ?????? ???? ?????? ?????????? ???????? ???? ????????", Toast.LENGTH_LONG).show();
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
            isLockOnMe = false;
            updateMyLocationIcon();
        }
    }

    public void showNbPoiOnMapForEdit(long NbPoiId, String DefaultName) {
        int debugStep = 0;
        try {
            if (NbPoiId > 0) {
                debugStep = 10;
                NbPoi poi = NbPoiSQLite.select(NbPoiId);
                if (poi == null)
                    return;
                debugStep = 20;
                NbPoiCompact compact = app.findInVisiblePois(NbPoiId);
                debugStep = 30;
                if (poi.PoiType == NbPoi.Enums.PoiType_Folder) {

                }
                if (poi.PoiType == NbPoi.Enums.PoiType_Track || poi.PoiType == NbPoi.Enums.PoiType_Route) {
                    debugStep = 40;
                    discardAllClickModes();
                    debugStep = 50;

                    routeDesignMode.editingRouteId = poi.NbPoiId;
                    routeDesignMode.editingRoute = poi;

                    if (compact != null && compact.ShowStatus == NbPoi.Enums.ShowStatus_Show) {
                        routeDesignMode.editingRouteIsVisible = true;
                        compact.polyLine.setVisible(false);
                    }
                    debugStep = 60;
                    List<LatLng> track = TrackData.readTrackData_LatLng(poi.Address);
                    debugStep = 70;
                    RouteMode = MAP_CLICK_MODE_ROUTE;
                    routeDesignMode.pnlRouteDesign.setVisibility(View.VISIBLE);
                    debugStep = 80;
                    routeDesignMode.initRouteDesignPanel();
                    debugStep = 90;
                    int track_size = track.size();
                    int dbg_ReadCount = 0;
                    for (int i = 0; i < track_size; i++) {
                        //added 1401-05-14 for ex reported
                        LatLng latlon = track.get(i);
                        if (latlon == null)
                            continue;
                        debugStep = 91;
                        dbg_ReadCount++;
                        routeDesignMode.mapOnClick_Route(latlon);
                    }
                    if (dbg_ReadCount != track_size){
                        TTExceptionLogSQLite.insert("?????????? ???????? ?????? ????????????", "dbg_ReadCount: " + dbg_ReadCount + "and track_size: " + track_size , PrjConfig.frmMapPage, 1012);
                    }
                    debugStep = 100;
                    if (track_size > 0) {

                        //1401-05-14 added for ex report
                        LatLng latlon = track.get(track_size - 1);
                        if (latlon != null) {
                            debugStep = 110;
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlon, map.getCameraPosition().zoom));
                            isLockOnMe = false;
                            debugStep = 120;
                            updateMyLocationIcon();
                        }
                        else{
                            //1401-05-14 added for ex report
                            TTExceptionLogSQLite.insert("?????????? ???????? ?????? ????????????", "latlon NULL booood and dbg_ReadCount: " + dbg_ReadCount + "and track_size: " + track_size , PrjConfig.frmMapPage, 1013);
                        }
                    }
                }
                else {
                    //Edit Waypoint Mode:
                    discardAllClickModes();
                    if (compact != null && compact.ShowStatus == NbPoi.Enums.ShowStatus_Show) {
                        addWaypointMode.editingWaypointWasVisible = true;
                        compact.marker.setVisible(false);
                    }
                    poi.Name = DefaultName; //1401-07-05

                    addWaypointMode.editingWaypoint = poi;
                    IsInAddWaypointMode = MAP_CLICK_MODE_WAYPOINT;
                    addWaypointMode.pnlAddWaypoint.setVisibility(View.VISIBLE);
                    addWaypointMode.initAddWaypointPanel();
                    addWaypointMode.mapOnClick_Waypoint(new LatLng(poi.LatS, poi.LonW), true);
                }
            }
        } catch (Exception ex) {
            projectStatics.showDialog(context, getString(R.string.CantLoadTrackToEdit), getString(R.string.CantLoadTrackToEdit_Desc)
                    , getString(R.string.accept), null, "", null);
            Log.e(Tag, "??????"+ex.getMessage());
            TTExceptionLogSQLite.insert(ex.getMessage(), "DEBUGSTEP:" + debugStep + "--" + ex.getStackTrace().toString(), PrjConfig.frmMapPage, 1003);
            ex.printStackTrace();
        }
    }

    public void onDestroyInChild() {
        //HHH ya inja bayad run she ya tooye Khode Service
        if (dialogRecordTrack.getIsRecording()) {
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("restartservice");
            broadcastIntent.setClass(context, TrackInBackgroundService_Restarter.class);
            context.sendBroadcast(broadcastIntent);
        } else {
            stopTrackRecordingServiceIfRunning();

        }
    }

    //1399-12-09

    Intent mServiceIntent;
    private TrackInBackgroundService mTrackInBackgroundService;

    public void stopTrackRecordingServiceIfRunning() {
        try {
            if (mTrackInBackgroundService == null)
                mTrackInBackgroundService = new TrackInBackgroundService();
            if (mServiceIntent == null)
                mServiceIntent = new Intent(context, mTrackInBackgroundService.getClass());
            if (isMyServiceRunning(mTrackInBackgroundService.getClass())) {
                context.stopService(mServiceIntent);
                Log.e(Tag, "Service status" + "STOPPED");
            } else {
                Log.e(Tag, "Service status"+"Not Running");
            }
        } catch (Exception exception) {
            String st = exception.getStackTrace().toString();
            Log.e(Tag,"Service status"+ st);
            exception.printStackTrace();
        }

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.e(Tag, "Service status"+"Running");
                return true;
            }
        }
        Log.e(Tag, "Service status"+"Not running");
        return false;
    }

    public void onResumeInChild() {
        try {
            Log.e(Tag, "????????????" + "OnResume Fired at MapPage" + " LOCATION MANAGER:" + (locationManager != null ? "NOT NULL" : "NULL") + " myLocationListener:" + (myLocationListener != null ? "NOT NULL" : "NULL"));
            //Add for Autorotate part 6
            mSensorManager.registerListener(this, mRotVectSensor, SensorManager.SENSOR_STATUS_ACCURACY_LOW);
            //End Add for Autorotate part 6

            //1399-12-09
            stopTrackRecordingServiceIfRunning();

            //1400-11-16 added, for be sure to update Location Access after resume
            checkLocation(false);

            //1401-03-24 added
            //HHH ?????????? ?????? ???? ???????? ???????? ??????????. ???? ?????? ????????
            updateCamera(bearing);
            setBtnGotoCurrentLocation();
            updateMyLocationIcon();

            //1400-11-04 ???????? ?????? ?????????? ???????? ???? ???????? ???????? ?????? ??????
//            if (map != null)
//                OnMapReadyCompleted = true;
            Log.e(Tag, "?????? ??????"+"OnMapReadyCompleted: " + OnMapReadyCompleted + " and trackResumeIsDone: " + trackResumeIsDone);
            //???? ?????????? ???? ???????? ?????? ?????????? ???????????? ?????????? ?????????? ????. ?????????? ???? ?????????? ???????? ???????? ?????????? ??????
            if (OnMapReadyCompleted && dialogRecordTrack != null && dialogRecordTrack.getIsRecordPanelActive() && !trackResumeIsDone) {
                Log.e(Tag, "?????? ??????"+"?????? ???????? ?????? - 250");

                trackingServiceResume();
            }
        } catch (Exception ex) {
            Log.e(Tag, "??????"+ ex.getMessage());
            ex.printStackTrace();
            TTExceptionLogSQLite.insert(ex.getMessage(), ex.getStackTrace().toString(), PrjConfig.frmMapPage, 1001);
        }
    }

    public boolean trackResumeIsDone = false;
    public boolean OnMapReadyCompleted = false;

    public void trackingServiceResume() {
        trackResumeIsDone = true;

        //1400-11-16 moved to here
        if ((hutilities.checkGooglePlayServiceAvailability(context) && fusedLocationClient == null)
            || (!hutilities.checkGooglePlayServiceAvailability(context) && (locationManager == null || myLocationListener == null))
        ) {
            //1400-11-04 added checkLocation and Commented requestLocationUpdates for test
            checkLocation(false);
            //                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, app.MinTrackTime, app.MinTrackDistance, myLocationListener);  //  here the min time interval and min distance
        }
        if (dialogRecordTrack.getIsRecording()) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                projectStatics.showDialog(context, getResources().getString(R.string.FineLocationDenied_Title), getResources().getString(R.string.FineLocationDenied_Desc), getResources().getString(R.string.ok), view -> {
                }, "", null);
                return;
            }

            //trackingService was running in background
//                stopTrackRecordingServiceIfRunning();
        }
        Log.e(Tag,"?????? ??????"+ "AAAA ");
        List<NbCurrentTrack> oldCurrentTracks = NbCurrentTrackSQLite.selectAll();
        int trkPts = oldCurrentTracks.size();
        LatLng lastLatLon = null;
        NbCurrentTrack lastCurrentTrack = null;
        Log.e(Tag,"?????? ??????"+ "???????????? ???? ?????? ?????? - ?????????? ???????? ????????: " + Integer.toString(trkPts));
        Log.e(Tag,"?????? ??????"+ "AAAA66 ");
        if (trkPts > 0) {
            //1400-11-04 Find Last Point and Skip Paused Points
            for (int i = trkPts - 1; i >= 0; i--) {
                lastCurrentTrack = oldCurrentTracks.get(i);
                if (lastCurrentTrack.IsPause()) {
                    continue;
                }
            }
        }
        Log.e(Tag, "?????? ??????"+"BBB");

        if (lastCurrentTrack != null)
            lastLatLon = new LatLng(lastCurrentTrack.Latitude, lastCurrentTrack.Longitude);
        Log.e(Tag,"?????? ??????"+ "CCC");
        trackProperties = new TrackProperties();
        trackProperties.initFromNbCurrentTrack(oldCurrentTracks);
        Log.e(Tag, "?????? ??????"+ "EEE");

        dialogRecordTrack.onResumeRecordingPanel(lastLatLon);
        Log.e(Tag, "?????? ??????"+ "FFF");
        dialogRecordTrack.drawBackgroundPoints(oldCurrentTracks);
        Log.e(Tag, "?????? ??????"+ "GGG");
        DialogRecordTrack.checkPowerSavingMode(context);
    }

    @Override
    public boolean onBackPressedInChild() {
        Log.e("AAAAAA", "addWaypointMode" + " is " + (addWaypointMode != null) + " and " + (addWaypointMode.dialog_position!= null ) );
        if (nav_drawer != null && nav_drawer.isDrawerOpen(GravityCompat.START)) {
            nav_drawer.closeDrawer(GravityCompat.START);
            return false;
        } else if (addWaypointMode != null && addWaypointMode.dialog_position != null && addWaypointMode.dialog_position.isShowing()) {
            addWaypointMode.dialog_position.dismiss();
            return false;
        } else if (addWaypointMode != null&& addWaypointMode.AddWaypointMarker != null) {
            addWaypointMode.discardWaypoint_Click(false);
            return false;
        } else if (sightNGoMode != null&& IsInSightNGoMode) {
            sightNGoMode.btnDiscardSightNGo_Click();
            return false;
        } else if (scrollSearchMode.getVisibility() == View.VISIBLE){
            scrollSearchMode.setVisibility(View.GONE);
            typeSearchMode = false;
            return false;
        }
        return super.onBackPressedInChild(); //true
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {//3nd Event
        return inflater.inflate(R.layout.activity_maps, parent, false);
    }

    public void onPauseInChild() {
        //1399-12-09
        if (dialogRecordTrack.getIsRecording()) {
            Log.e(Tag, "?????? ??????"+ "???? ?????? ?????? ?????????? ??????????");

            mTrackInBackgroundService = new TrackInBackgroundService();
            mServiceIntent = new Intent(context, mTrackInBackgroundService.getClass());
            if (!isMyServiceRunning(mTrackInBackgroundService.getClass())) {
                Log.e(Tag, "?????????? ???????? ?????????? ??????"+"?????? ?????? " );
                context.startService(mServiceIntent);
            }
        }

        //Add for Autorotate part 7
        // unregister listener
        if (mSensorManager != null)
            mSensorManager.unregisterListener(this);
        //End Add for Autorotate part 7

        //moved here at 1401-07-28 ???????? ???????? ?????? ?????? ?????? ????????
        //commented at 1401-09-08 ?????????? ?????? ???????? ?? ???????? ?????? ?????? ?????????? ???????? ???????????? ???? ???? ???? ???? ?????????? ???? ???? ??????!
        //locationManager.removeUpdates(myLocationListener);
        //added at 1401-07-28 ???????? ?????? ???????? ???????? ??????????. ?????????? ???? ?????? ?????????? ?????? ??????????
        //commented at 1401-09-08 ?????????? ?????? ???????? ?? ???????? ?????? ?????? ?????????? ???????? ???????????? ???? ???? ???? ???? ?????????? ???? ???? ??????!
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            locationManager.unregisterGnssStatusCallback(gnssCallback);
//        }
    }

    //------------------ Show Go TO My Location---------------
    public static final String NO_LOCATION = "\uE835";
    public static final String TRACK_UP = "\uF21D";
    public static final String NORTH_UP = "\uE82E";

    public void setBtnGotoCurrentLocation() {
        if (currentLatLon == null || (currentLatLon.latitude == 0 && currentLatLon.longitude == 0)) {
            btnGotoCurrentLocation.setText(NO_LOCATION);
//            setDownFields();
//            showSpeedAndElev();
        }
        else if (isTrackUp)
            btnGotoCurrentLocation.setText(TRACK_UP);
        else
            btnGotoCurrentLocation.setText(NORTH_UP);

    }
    //------------------ End Show Go TO My Location---------------

    public final int ZoomToShowPoint = 14;
    //---------------------- ???????? ???????? ?????? ?????????? ?????????? ???????? ---------------------
    private void btnSearch_Click() {
        String text = txtSearch.getText().toString().trim();
        try {
            //?????? ?????????? ?????? ???? ?????? ???????????? ???????? ???????? ???? ??????. ?????? ???????????? ?????? ???????????? ???????? ???? ???? ????????
            text = TextFormat.ReplacePersianNumbersWithEnglishOne(text);
            //a. lat/lon
            LatLng inpLocation = hMapTools.extractLatLonString(text);
            if (inpLocation == null){
                inpLocation = hMapTools.extractUtmString(text);
            }

            if (inpLocation != null){
                NbPoi poi = new NbPoi();
                poi.NbPoiId = 0l;
                poi.Name = GPXFile.DoubleToStringForLatLan(inpLocation.latitude) + "," + GPXFile.DoubleToStringForLatLan(inpLocation.longitude);
                poi.LatBegin = poi.LatS = inpLocation.latitude;
                poi.LonBegin = poi.LonW = inpLocation.longitude;

                performSearchResultPoiClick(poi);
                return;
            }


            if (!hutilities.isInternetConnected(context)) {
                projectStatics.showDialog(context, this.getResources().getString(R.string.NoInternet), this.getResources().getString(R.string.NoInternet_Desc), this.getResources().getString(R.string.ok), view -> {}, "", null);
                return;
            }
            btnShowResultOnMap.setVisibility(View.INVISIBLE);
            hutilities.hideKeyboard(context, txtSearch);
            typeSearchMode = true;
            scrollSearchMode.setVisibility(View.VISIBLE);

            pageProgressBar.setVisibility(View.VISIBLE);

            SearchRequestDTO requestDTO = new SearchRequestDTO();
            requestDTO.Filter = text + "***";
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
                                divSearch.setVisibility(View.GONE);
                                txtSearchResult.setText(result.message);
                            } else {
                                txtSearchResult.setVisibility(View.GONE);
                                divSearch.setVisibility(View.VISIBLE);
                            }
                            int resSize= result.resList.size();
                            if (result.resList.size() > 0) {
                                //Added 1401-07-05 ???????? ?????? ?????? ?????????? ???????? ?????????? ?? ????????????
                                for (int i = 0; i < resSize; i++) {
                                    result.resList.get(i).NbPoiId = 0l;
                                }
                                btnShowResultOnMap.setVisibility(View.VISIBLE);
                                rvSearchResult.setVisibility(View.VISIBLE);
                                initAdapterSearch(result.resList);
                                AddResultsToMap(adapterSearch.data);
                            } else {
                                txtSearchResult.setVisibility(View.VISIBLE);
                                divSearch.setVisibility(View.GONE);
                                txtSearchResult.setText(R.string.NoItemFoundInSearch);
                            }
                        } else {
                            txtSearchResult.setVisibility(View.VISIBLE);
                            divSearch.setVisibility(View.GONE);
                            txtSearchResult.setText("???????????????? ???? ?????????????? ???????????? ???? ???????? ?????????? ???? ???????? ???????? ??????. ???????? ???????? ?????????? ???????? ????????????.");
                            Log.e(Tag, "MY_ERROR"+"ResponseCODE: " + response.code());
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Log.e(Tag, "MY_ERROR"+ex.getMessage());
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
                    divSearch.setVisibility(View.GONE);
                    txtSearchResult.setText("???????????????? ?????????? ???? ?????????????? ???????????? ???? ???????? ???? ???????? ???????? ??????. ???????? ???????? ?????????? ???????? ????????????.");

                }
            });

        }
        catch (Exception ex){
            projectStatics.showDialog(context, getString(R.string.ProblemInSearch), getString(R.string.ProblemInSearch_DESC)
                    , getString(R.string.accept), null, "", null);
            Log.e(Tag, "??????" + " text " +ex.getMessage());
            TTExceptionLogSQLite.insert(ex.getMessage(), "Searched: " + text + " - " + ex.getStackTrace().toString(), PrjConfig.frmMapPage, 1006);
            ex.printStackTrace();
        }
    }
    void initAdapterSearch(List<NbPoi> result) {
        if (true || adapterSearch == null) {

            MapPage.NbPoisAdapter.OnItemClickListener itemClickListener = (post, Position) -> {
                RecyclerView_ItemClicked(post, Position);
            };
            adapterSearch = new MapPage.NbPoisAdapter(context, "full",itemClickListener);
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

        performSearchResultPoiClick(poi);
    }
    public void performSearchResultPoiClick(NbPoi poi){

        LatLng position = null;
        if (NbPoi.Enums.PoiType_Track == poi.PoiType || poi.PoiType == NbPoi.Enums.PoiType_Route) {
            position = new LatLng(poi.LatBegin, poi.LonBegin);
        }
        else{
            position = new LatLng(poi.LatS, poi.LonW);
        }

        scrollSearchMode.setVisibility(View.GONE);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, ZoomToShowPoint));
        isLockOnMe = false;

        poiClicked(poi);
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

        MapPage.NbPoisAdapter.OnItemClickListener itemClickFunction;
        String mode;

        public NbPoisAdapter(Context context, String _mode, MapPage.NbPoisAdapter.OnItemClickListener _itemClickFunction) {
            this.mode = _mode;
            this.data = new ArrayList<>();
            this.context = context;
            this.itemClickFunction = _itemClickFunction;
        }

        @Override
        public MapPage.NbPoisAdapter.NbPoiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            layoutInflater = LayoutInflater.from(context);
            //this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = layoutInflater.inflate(R.layout.activity_searchonmap_item, parent, false);
            return new MapPage.NbPoisAdapter.NbPoiViewHolder(itemView, this.itemClickFunction);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            if (viewHolder instanceof MapPage.NbPoisAdapter.NbPoiViewHolder) {
                ((MapPage.NbPoisAdapter.NbPoiViewHolder) viewHolder).bind(data.get(position), position);
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


        //???? ?????????? ????????
        public abstract interface OnItemClickListener {
            public abstract void onItemClicked(NbPoi post, int Position);
        }
        class NbPoiViewHolder extends RecyclerView.ViewHolder {
            public View itemView;
            public TextView txtTitle, lblDistance, lblSecondLine, lblProvinceName;
            ImageView txtImage;
            public ProgressBar progressBarIndet;
            LinearLayout itemMainPart;
            MapPage.NbPoisAdapter.OnItemClickListener itemClickFunction;

            NbPoiViewHolder(View itemView, MapPage.NbPoisAdapter.OnItemClickListener itemClickFunction) {
                super(itemView);
                this.itemView = itemView;
                this.itemClickFunction = itemClickFunction;
                txtTitle = itemView.findViewById(R.id.txtTitle);
                lblProvinceName = itemView.findViewById(R.id.lblProvinceName);
                lblDistance = itemView.findViewById(R.id.lblDistance);
                itemMainPart = itemView.findViewById(R.id.itemMainPart);
                lblSecondLine = itemView.findViewById(R.id.lblSecondLine);
                txtImage = itemView.findViewById(R.id.txtImage);
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
                    lblProvinceName.setText(currentObj.ProvinceName);
                    txtImage.setImageResource(NbPoi.PoiTypeToResourcId(currentObj.PoiType, 1));
                    txtImage.setColorFilter(context.getResources().getColor(NbPoi.PoiTypeToResourcId(currentObj.PoiType, 2)));

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
    public static List<Marker> searchMarkers = new ArrayList<>();
    LatLngBounds.Builder builder;

    public void AddResultsToMap(List<NbPoi> res) {
        int sz = searchMarkers.size();
        for (int i = 0; i < sz; i++) {
            searchMarkers.get(i).remove();
        }
        searchMarkers.clear();
        sz = res.size();
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

            //1401-05-01 commented
//            int ResourceId = NbPoi.PoiTypeToResourcId(poi.PoiType, 1);
//            int Color = NbPoi.PoiTypeToResourcId(poi.PoiType, 2);
//            BitmapDescriptor currentRouteIcon = hutilities.bitmapDescriptorFromVector(context, ResourceId, Color);
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(position)
                    .title(poi.Name)
                    .zIndex(10000)
                    //.icon(currentRouteIcon)
                    .flat(true);
            Marker marker = map.addMarker(markerOptions);
            marker.setTag(poi);
            searchMarkers.add(marker);
            poi.marker = marker;
        }
    }
    public void GoToMapForAllResults(){
        try {
            scrollSearchMode.setVisibility(View.GONE);
            LatLngBounds bounds = builder.build();
            int padding = 80; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 600, 800, padding);
            compass_Clicked();

            map.moveCamera(cu);
            isLockOnMe = false;
        }
        catch (Exception ex){
            ex.printStackTrace();
            Log.e(Tag, "HH_ERRORR_CAMERA"+ex.getMessage());
        }
    }

    NbPoi selectedPoiInInfoButtom = null;
    InfoBottom objInfoBottom = null;
    public boolean poiClicked(Marker marker) {
        NbPoi poi = (NbPoi)marker.getTag();
        if(poi == null){
            poi = NbPoi.getPoiForPosition(marker.getPosition().latitude, marker.getPosition().longitude, getString(R.string.Position));
//            poi.NbPoiId = 0l;
//            poi.LatS = marker.getPosition().latitude;
//            poi.LonW = marker.getPosition().longitude;
//            poi.Name = String.format(getString(R.string.Position) + " " + "%.4f,%.4f", poi.LatS, poi.LonW);
        }
        Log.e("????????", "ID of POI222: " + poi.NbPoiId);

        return poiClicked(poi);
    }
    public boolean poiClicked(NbPoi poi) {
        objInfoBottom = InfoBottom.getInstance(poi, context);
        selectedPoiInInfoButtom = poi;
        objInfoBottom.show(context.getSupportFragmentManager(), objInfoBottom.getTag());

        return true;
    }

    public static class InfoBottom extends BottomSheetDialogFragment {
        public NbPoi poi;
        View view;
        Button btnOpenWeather, btnRoadRooting, btnDownloadTracks, btnLinkNew, btnEditWaypoint;
        Context context;
        public InfoBottom(){
        }

        public static InfoBottom getInstance(NbPoi poi, Context context){
            InfoBottom res= new InfoBottom();
            res.poi = poi;
            res.context = context;
            return res;
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


            btnOpenWeather = view.findViewById(R.id.btnOpenWeather);
            btnOpenWeather.setOnClickListener(view1 -> {
                this.dismiss();
                ((MainActivityManager) context).showFragment(WeatherShow.getInstance(this.poi, PrjConfig.frmMapPage));
            });
            btnRoadRooting = view.findViewById(R.id.btnRoadRooting);
            btnRoadRooting.setOnClickListener(view1 -> {
                NbLogSearchSQLite.insert(NbLogSearch.getInstance(NbLogSearch.CommandType_OpenCityRouting, "", this.poi.LatS, this.poi.LonW, 0));

                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr="+this.poi.LatS+","+this.poi.LonW+"&daddr="+this.poi.LatS+","+this.poi.LonW));
                startActivity(intent);
            });
            btnDownloadTracks = view.findViewById(R.id.btnDownloadTracks);
            btnDownloadTracks.setOnClickListener(view1 -> {
                this.dismiss();
                ((MainActivityManager) context).showFragment(SafeGpxSearch.getInstance("", this.poi));
            });

            btnEditWaypoint = view.findViewById(R.id.btnEditWaypoint);
            btnEditWaypoint.setOnClickListener(view1 -> {
                this.dismiss();
                if (this.poi.NbPoiId > 0){
                    ((MainActivity) context).showNbPoiOnMapForEdit(this.poi.NbPoiId, this.poi.Name);
                }
                else{
                    ((MainActivity) context).btnAddWaypoint_Click(new LatLng(this.poi.LatS, this.poi.LonW));
                }
                ((MainActivity) context).mapPage.clearAllSearchResults();
            });
            Log.e("????????", "ID of POI: " + this.poi.NbPoiId + "-" + this.poi.ServerId);
            if (this.poi.NbPoiId > 0)
                btnEditWaypoint.setText(getString(R.string.btnSaveOrEdit_Edit));
            else
                btnEditWaypoint.setText(getString(R.string.btnSaveOrEdit_Save));

        }
    }
    //---------------------- ?????????? ???????? ?????? ?????????? ?????????? ???????? ---------------------

}
