package mojafarin.pakoob.mainactivitymodes;

import static android.content.Context.POWER_SERVICE;
import static utils.HFragment.stktrc2k;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.github.eloyzone.jalalicalendar.JalaliDate;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import bo.entity.NbCurrentTrack;
import bo.entity.NbPoi;
import bo.sqlite.NbCurrentTrackSQLite;
import bo.sqlite.TTExceptionLogSQLite;
import maptools.GPXFile;
import maptools.LocationRepository;
import maptools.LocationTrackingService;
import maptools.TrackData;
import mojafarin.pakoob.MainActivity;
import mojafarin.pakoob.MapPage;
import mojafarin.pakoob.R;
import mojafarin.pakoob.TripComputer;
import mojafarin.pakoob.app;
import utils.MyDate;
import utils.PrjConfig;
import utils.projectStatics;

public class DialogRecordTrack {
    MainActivity activity;
    List<NbCurrentTrack> currentTrack = new ArrayList<>();
    public List<Polyline> polylines = new ArrayList<>();
    MapPage mapPage;
    LocationRepository repo;
    Context context;
    String Tag = "Dialog_Record_Track";

    public DialogRecordTrack(MainActivity mainActivity, MapPage mapPage, LocationRepository repository) {
        activity = mainActivity;
        this.mapPage = mapPage;
        pauseImg = projectStatics.textAsBitmapFontello(PauseChar, 80, Color.GRAY, activity);
        playImg = projectStatics.textAsBitmapFontello(PlayChar, 80, Color.GREEN, activity);
        repo = repository;
        context = mapPage.context.getApplicationContext();
    }

    final String PauseChar = "\uF00E";
    final String PlayChar = "\uF00F";
    final Bitmap pauseImg;
    final Bitmap playImg;
    int IsRecordPanelActive = 0;
    int IsRecording = 0;
    FrameLayout pnlRecordTrack, btnGoToTripComputerParent;
    TextView lblFinishRecording;
    FloatingActionButton btnGoToTripComputer, btnPlayPause, btnFinishRecording;

    public void initializeComponentsOnResume() {
        //btnGoToTripComputer.setImageBitmap(projectStatics.textAsBitmapFontello("\uE811", 80, Color.BLACK, activity));
        //btnGoToTripComputer.setImageResource(R.drawable.ic_trip_computer);
        btnGoToTripComputer.setOnClickListener(view -> {
            ((MainActivity) activity).showFragment(TripComputer.getInstance());
        });
        btnPlayPause.setImageBitmap(pauseImg);
        setViewOfBtnPlay();
        btnPlayPause.setOnClickListener(view -> {
            if (getIsRecording()) {
                //1400-11-04 three lines added:
                NbCurrentTrack pauseObject = NbCurrentTrack.getPauseObject();
                currentTrack.add(pauseObject);
                NbCurrentTrackSQLite.insert(pauseObject);

                Toast.makeText(activity, R.string.MsgRecordingPaused, Toast.LENGTH_LONG);
                setIsRecording(false);
                setViewOfBtnPlay();

                //توقف سرویس خوندن موقعیت
                stopLocationService();


            } else {

                //شروع سرویس خوندن موقعیت
                startLocationService();

                Toast.makeText(activity, R.string.MsgRecordingStartedAgain, Toast.LENGTH_LONG);

                setIsRecording(true);
                setViewOfBtnPlay();
            }
            try {
                checkPowerSavingMode(activity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        //btnFinishRecording.setImageBitmap(projectStatics.textAsBitmapFontello("\uF11E", 80, Color.GREEN, activity));
        btnFinishRecording.setOnClickListener(view -> {
            btnFinishRecording_Click();
        });
    }

    public void stopLocationService() {
        //app.mTrackInBackgroundService.stopFromOutside();
        //context.stopService(app.mServiceIntent);
        Log.e(Tag, "Stop Service Called...");

        if (MainActivity.isTrackingServiceBound && app.mTrackInBackgroundService != null) {
            //context.unbindService(MainActivity.connection);
            app.mTrackInBackgroundService.stopFromOutside();
            MainActivity.isTrackingServiceBound = false;
            Log.e(Tag, "Stop Service and Unbind");
        }
    }

    public void startLocationService() {
        if (app.mServiceIntent == null){
            app.mServiceIntent = new Intent(context, LocationTrackingService.class);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14+
            // از ContextCompat استفاده کن تا رفتار ایمن‌تر و با permission چک انجام شه
            ContextCompat.startForegroundService(context, app.mServiceIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // Android 8 - 13
            context.startForegroundService(app.mServiceIntent);
        } else { // قدیمی‌تر از Android 8
            context.startService(app.mServiceIntent);
        }
        if (!MainActivity.isTrackingServiceBound) {
            context.bindService(app.mServiceIntent, MainActivity.connection, Context.BIND_AUTO_CREATE);
            MainActivity.isTrackingServiceBound = true;
        }
    }

    public void initializeComponents() {
        color_of_currentTrack = Color.CYAN;//Color.parseColor();
        color_of_currentTrack = Color.CYAN;//Color.parseColor();

        pnlRecordTrack = activity.findViewById(R.id.pnlRecordTrack);
        btnGoToTripComputerParent = activity.findViewById(R.id.btnGoToTripComputerParent);
        lblFinishRecording = activity.findViewById(R.id.lblFinishRecording);
        btnGoToTripComputer = activity.findViewById(R.id.btnGoToTripComputer);
        btnPlayPause = activity.findViewById(R.id.btnPlayPause);
        btnFinishRecording = activity.findViewById(R.id.btnFinishRecording);
        initializeComponentsOnResume();
    }

    public void btnFinishRecording_Click() {

        try {
            if (getIsRecordingForceReRead()) {

                projectStatics.showDialog(activity
                        , activity.getResources().getString(R.string.StopTrackRecording_Title)
                        , activity.getResources().getString(R.string.StopTrackRecording_Desc)
                        , activity.getResources().getString(R.string.ok), view1 -> {
                            try {

                                try {
                                    //توقف سرویس خوندن موقعیت
                                    stopLocationService();
                                } catch (Exception serviceEx) {
                                    handleException(serviceEx, activity.getResources().getString(R.string.vali_SaveInFileError), activity.getResources().getString(R.string.vali_SaveInFileError_Desc)
                                            , true, 113);
                                }
                                //شروع ذخیره سازی ترک
                                List<NbCurrentTrack> trackPointsInDb = NbCurrentTrackSQLite.selectAll();
                                int trkSize = trackPointsInDb.size();
                                if (trkSize == 0) {
                                    Toast.makeText(mapPage.getContext(), mapPage.getString(R.string.currentTrackIsTooShortToSave), Toast.LENGTH_LONG).show();
                                    discardTrackPanel();
                                    return;
                                }

                                TrackData data = new TrackData();
                                short poiType = NbPoi.Enums.PoiType_Track;
                                Random rand = new Random();

                                data.Color = GPXFile.RandomColors.get(rand.nextInt(GPXFile.colorCount));
                                Calendar now = Calendar.getInstance();
                                JalaliDate jalaliDate = MyDate.getJalaliDate(now);
                                data.Name = activity.getResources().getString(R.string.Route) + " " + MyDate.CalendarToPersianDateString(now, MyDate.DateToStringFormat.yyyymmdd, "")
                                        + "-" + MyDate.CalendarToTimeString(now, MyDate.TimeToStringFormat.HourMinSec, "");

                                TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
                                for (int i = 0; i < trkSize; i++) {
                                    NbCurrentTrack currentTrack = trackPointsInDb.get(i);

                                    data.Points.add(currentTrack.getLatLon());
                                    data.Elev.add(currentTrack.Elevation);
                                    Calendar cl = Calendar.getInstance();
                                    cl.setTimeInMillis(currentTrack.Time);
                                    cl.setTimeZone(utcTimeZone);
                                    data.Time.add(cl);
                                }
                                if (data.Points.size() < 2) {
                                    discardTrackPanel();
                                    return;
                                }
                                NbPoi saved = null;
                                saved = GPXFile.SaveDesignedRouteToDb(0, poiType, data, activity);

                                if (saved.NbPoiId != 0) {
                                    //Not working after fragmenting...
                                    activity.openEditTrack(saved.NbPoiId, "MainActivity", 0, null);
                                    discardTrackPanel();
                                }
                            } catch (Exception ex) {
                                handleException(ex, activity.getResources().getString(R.string.vali_SaveInFileError), activity.getResources().getString(R.string.vali_SaveInFileError_Desc), true, 112);
                            }
                        }
                        , activity.getResources().getString(R.string.cancel), null);


            } else {
                Context context = mapPage.getContext();

                try {
                    //hutilities.requestIgnoreBatteryOptimizations(context);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.startRecording();
                Toast.makeText(context.getApplicationContext(), "ذخیره مسیر آغاز شد...", Toast.LENGTH_LONG).show();
            }
        } catch (Exception ex) {
            handleException(ex, activity.getResources().getString(R.string.vali_SaveInFileError), activity.getResources().getString(R.string.vali_SaveInFileError_Desc), true, 111);
        }
    }

    private void handleException(Exception ex, String title, String desc, Boolean showExToUser, int InternalCode) {
        ex.printStackTrace();
        projectStatics.showDialog(activity
                , title
                ,  desc  + " ("+InternalCode+") " + (showExToUser?" + " + ex.getMessage():"")
                , activity.getResources().getString(R.string.ok)
                , null
                , ""
                , null);
        Log.e(Tag, "خطای اکسپشن : " + ex.getMessage());
        TTExceptionLogSQLite.insert(ex.getMessage(), stktrc2k(ex), PrjConfig.frmTrackRecording, InternalCode);
    }

    public static void checkPowerSavingMode(Activity activity) {
//        if (1==1) return;
//        boolean isIgnored = hutilities.requestIgnoreBatteryOptimizations(activity.getApplicationContext());
//        if (isIgnored)
//            return;
        PowerManager powerManager = (PowerManager)
                activity.getSystemService(POWER_SERVICE);
        String packageName = activity.getPackageName();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && powerManager.isPowerSaveMode()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (powerManager.isIgnoringBatteryOptimizations(packageName)) {
                    return;
                }
            }
            projectStatics.showDialog(activity, activity.getString(R.string.CheckSavingPowerMode_Title)
                    , activity.getString(R.string.CheckSavingPowerMode_Desc), activity.getString(R.string.OpenBattrySetting),
                    view -> {
                        //openBatteryOptimizationSettings
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                        intent.setData(uri);
                        activity.startActivity(intent);
                        //activity.getApplicationContext().startActivity(new Intent("com.android.settings.widget.SettingsAppWidgetProvider"));
                    }
                    , "", null);
        }
    }

    void discardTrackPanel() {
        lastLocation = null;
        setVisibilityAndEnvironment(false);
        //pnlRecordTrack.setVisibility(View.GONE);
        setIsRecording(false);
        setIsRecordPanelActive(false);
        NbCurrentTrackSQLite.deleteAll();
        int plSize = polylines.size();
        for (int i = 0; i < plSize; i++) {
            polylines.get(i).remove();
        }
        polylines.clear();
        currentTrack.clear();
        veryCurrentRoutePoints.clear();
        mapPage.stopTrackRecordingServiceIfRunning();
    }

    void setViewOfBtnPlay() {
        if (getIsRecording()) {
            btnPlayPause.setImageBitmap(pauseImg);
        } else {
            btnPlayPause.setImageBitmap(playImg);
        }
    }

    public void setVisibilityAndEnvironment(boolean isRecording) {
        pnlRecordTrack.setVisibility(View.VISIBLE);

        btnGoToTripComputer.setVisibility(View.VISIBLE);
        btnGoToTripComputerParent.setVisibility(View.VISIBLE);
        if (isRecording) {
            lblFinishRecording.setText(mapPage.getString(R.string.endSaveTrack));
            btnPlayPause.setVisibility(View.VISIBLE);
            //btnGoToTripComputer.setVisibility(View.VISIBLE);
            //btnGoToTripComputerParent.setVisibility(View.VISIBLE);
        } else {
            lblFinishRecording.setText(mapPage.getString(R.string.startSaveTrack));
            btnPlayPause.setVisibility(View.INVISIBLE);
            //btnGoToTripComputer.setVisibility(View.INVISIBLE);
            //btnGoToTripComputerParent.setVisibility(View.INVISIBLE);
        }
    }

    public void startRecording() {
        try {
            checkPowerSavingMode(activity);
            startLocationService();
            setIsRecording(true);
            //1404-08 added - اولین نقطه رو همین لحظه اضافه کنه - شاید باعث ایجاد نقطه الکی بشه
            if (MainActivity.currentLatLon != null){
                Location loc = new Location("");
                loc.setLatitude(MainActivity.currentLatLon.latitude);
                loc.setLongitude(MainActivity.currentLatLon.longitude);
                loc.setTime(Calendar.getInstance().getTimeInMillis());
                loc.setAltitude(MainActivity.currentElev);
                app.mTrackInBackgroundService.savePointToDB_IfTracking(loc);
                drawNextPoint(MainActivity.currentLatLon, (float) MainActivity.currentElev);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (btnFinishRecording == null)
            initializeComponents();
        lastLocation = null;
        //pnlRecordTrack.setVisibility(View.VISIBLE);
        setVisibilityAndEnvironment(true);
        setIsRecordPanelActive(true);
        Toast.makeText(activity, R.string.MsgRecordingStarted, Toast.LENGTH_LONG);
        setViewOfBtnPlay();
    }

    public void onResumeRecordingPanel(LatLng lastLocation) {
        setIsRecordPanelActive(true);
        if (btnFinishRecording == null)
            initializeComponents();
        this.lastLocation = lastLocation;
        //pnlRecordTrack.setVisibility(View.VISIBLE);
        setVisibilityAndEnvironment(true);
        setViewOfBtnPlay();
    }

    public void drawBackgroundPoints(List<NbCurrentTrack> backCurrentTrack) {
        if (!getIsRecordPanelActive()) {
            return;
        }

        currentTrack.clear();
        currentTrack.addAll(backCurrentTrack);
        int trkSize = backCurrentTrack.size();
        veryCurrentRoutePoints.clear();
        for (int i = 0; i < trkSize; i++) {
            NbCurrentTrack currentTrack = backCurrentTrack.get(i);
            if (currentTrack.IsPause()) //1400-11-04
                continue;
            LatLng latLng = currentTrack.getLatLon();
            veryCurrentRoutePoints.add(latLng);
            //if (i == trkSize - 1) //1400-11-04 Commented
            //    lastLocation = latLng;
            lastLocation = latLng;
        }
        polylines.clear();

        if (activity.map == null) {
            Log.e("Dialog_Track", "نقشه نال بود و بی خیال شدیم");
            return;
        }
        drawVeryCurrentRoute();
    }

    public Polyline getCurrentTrackPolylineDrawer(List<LatLng> points) {
        Polyline route = activity.map.addPolyline(new PolylineOptions()
                .width(8)
                .color(color_of_currentTrack)
                .geodesic(false)
                .jointType(JointType.ROUND)//1400-10-20
                .zIndex(3));
        route.setPoints(points);
        return route;
    }

    public void drawVeryCurrentRoute() {
//    Polyline route = activity.map.addPolyline(new PolylineOptions()
//            .width(8)
//            .color(color_of_currentTrack)
//            .geodesic(false)
//            .jointType(JointType.ROUND)//1400-10-20
//            .zIndex(3));
//    route.setPoints(veryCurrentRoutePoints);
        if (activity.map == null)
            return;

        Polyline route = getCurrentTrackPolylineDrawer(veryCurrentRoutePoints);
        polylines.add(route);
        veryCurrentRoutePoints.clear();
    }

    public List<LatLng> veryCurrentRoutePoints = new ArrayList<>();

    public void drawNextPoint(LatLng currentLatLon, float Elevation) {
        if (currentLatLon == null)
            return;
        if (!getIsRecordPanelActive() || !getIsRecording())
            return;
        NbCurrentTrack trkPt = new NbCurrentTrack();
        trkPt.Latitude = currentLatLon.latitude;
        trkPt.Longitude = currentLatLon.longitude;
        trkPt.Time = System.currentTimeMillis();
        trkPt.Elevation = Elevation;

        if (trkPt.Time - lastTime < 1000)
            return;
        currentTrack.add(trkPt);
        //NbCurrentTrackSQLite.insert(trkPt);


        //veryCurrentRoutePoints = new ArrayList<>();
        if (lastLocation != null) {
            veryCurrentRoutePoints.add(lastLocation);
            veryCurrentRoutePoints.add(currentLatLon);
        }
        lastLocation = currentLatLon;
        lastTime = trkPt.Time;

        if (activity.map == null)
            return;
        drawVeryCurrentRoute();
    }

    LatLng lastLocation = null;
    Long lastTime = 0l;

    public int color_of_currentTrack;

    public boolean getIsRecording() {
        if (IsRecording != 0)
            return IsRecording == 1;
        IsRecording = app.session.getIsTrackRecording();
        return IsRecording == 1;
    }

    public boolean getIsRecordingForceReRead() {
        IsRecording = app.session.getIsTrackRecording();
        return IsRecording == 1;
    }

    public boolean getIsRecordPanelActive() {
        if (IsRecordPanelActive != 0)
            return IsRecordPanelActive == 1;
        IsRecordPanelActive = app.session.getRecordingPanelActive();
        return IsRecordPanelActive == 1;
    }

    public void setIsRecordPanelActive(boolean active) {
        app.session.setRecordingPanelActive(active ? 1 : 2);
        this.IsRecordPanelActive = active ? 1 : 2;
    }

    public void setIsRecording(boolean active) {
        repo.setTrackingActive(active);
        app.session.setIsTrackRecording(active ? 1 : 2);
        this.IsRecording = active ? 1 : 2;
    }

}
