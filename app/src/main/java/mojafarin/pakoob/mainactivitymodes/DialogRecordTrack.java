package mojafarin.pakoob.mainactivitymodes;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.PowerManager;
import android.telecom.ConnectionService;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.github.eloyzone.jalalicalendar.JalaliDate;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
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
import maptools.TrackData;
import mojafarin.pakoob.MainActivity;
import mojafarin.pakoob.MapPage;
import mojafarin.pakoob.R;
import mojafarin.pakoob.TripComputer;
import mojafarin.pakoob.app;
import utils.MyDate;
import utils.PrjConfig;
import utils.hutilities;
import utils.projectStatics;

public class DialogRecordTrack {
    MainActivity activity;
    List<NbCurrentTrack> currentTrack = new ArrayList<>();
    public List<Polyline> polylines = new ArrayList<>();
    MapPage mapPage;

    public DialogRecordTrack(MainActivity mainActivity, MapPage mapPage) {
        activity = mainActivity;
        this.mapPage = mapPage;
        pauseImg = projectStatics.textAsBitmapFontello(PauseChar, 80, Color.GRAY, activity);
        playImg = projectStatics.textAsBitmapFontello(PlayChar, 80, Color.GREEN, activity);
    }

    final String PauseChar = "\uF00E";
    final String PlayChar = "\uF00F";
    final Bitmap pauseImg;
    final Bitmap playImg;
    int IsRecordPanelActive = 0;
    int IsRecording = 0;
    FrameLayout pnlRecordTrack, btnGoToTripComputerParent;
    TextView lblFinishRecording;
    FloatingActionButton btnGoToTripComputer, btnPlayPause,btnFinishRecording;

    public void initializeComponentes() {
        color_of_currentTrack = Color.CYAN;//Color.parseColor();

        pnlRecordTrack = activity.findViewById(R.id.pnlRecordTrack);
        btnGoToTripComputerParent = activity.findViewById(R.id.btnGoToTripComputerParent);
        lblFinishRecording = activity.findViewById(R.id.lblFinishRecording);
        btnGoToTripComputer = activity.findViewById(R.id.btnGoToTripComputer);
        btnPlayPause = activity.findViewById(R.id.btnPlayPause);
        btnFinishRecording = activity.findViewById(R.id.btnFinishRecording);

        //btnGoToTripComputer.setImageBitmap(projectStatics.textAsBitmapFontello("\uE811", 80, Color.BLACK, activity));
        //btnGoToTripComputer.setImageResource(R.drawable.ic_trip_computer);
        btnGoToTripComputer.setOnClickListener(view -> {
            ((MainActivity)activity).showFragment(TripComputer.getInstance());
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
            } else {
                Toast.makeText(activity, R.string.MsgRecordingStartedAgain, Toast.LENGTH_LONG);
                setIsRecording(true);
                setViewOfBtnPlay();
            }
            checkPowerSavingMode(activity);
        });
        //btnFinishRecording.setImageBitmap(projectStatics.textAsBitmapFontello("\uF11E", 80, Color.GREEN, activity));
        btnFinishRecording.setOnClickListener(view -> {

            try {
                if (getIsRecordingForceReRead()) {
                    List<NbCurrentTrack> trackPointsInDb = NbCurrentTrackSQLite.selectAll();
                    int trkSize =trackPointsInDb.size();
                    if ( trkSize== 0){
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
                    NbPoi saved = GPXFile.SaveDesignedRouteToDb(0, poiType, data, activity);
                    if (saved.NbPoiId != 0) {
                        //Not working after fragmenting...
                        activity.openEditTrack(saved.NbPoiId, "MainActivity", 0, null);
                        discardTrackPanel();
                    }
                }
                else{
                    Context context = mapPage.getContext();
                    if (Build.VERSION.SDK_INT >= 29) {
                        if (ActivityCompat.checkSelfPermission((Activity) context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            if (Build.VERSION.SDK_INT == 29) {
                                ActivityCompat.requestPermissions((Activity) context, new String[]{
                                        android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                                }, PrjConfig.Location_BACKGROUND_PERMISSION_REQUEST_CODE);
                            } else {
                                projectStatics.showDialog((MainActivity) context
                                        , context.getResources().getString(R.string.RequestBackgroundLocationInAndroid29_Title)
                                        , context.getResources().getString(R.string.RequestBackgroundLocationInAndroid29_Title_Desc)
                                        , context.getResources().getString(R.string.ok), view1 -> {
                                            hutilities.showAppSettingToChangePermission(context);
                                        }
                                        , "", null);
                            }
                        }
                    }
                    this.startRecording();
                    Toast.makeText(context.getApplicationContext(), "ذخیره مسیر آغاز شد...", Toast.LENGTH_LONG).show();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                projectStatics.showDialog(activity
                        , activity.getResources().getString(R.string.vali_SaveInFileError)
                        , activity.getResources().getString(R.string.vali_SaveInFileError_Desc) + ex.getMessage()
                        , activity.getResources().getString(R.string.ok)
                        , null
                        , ""
                        , null);
                Log.e("خطا", ex.getMessage());
                ex.printStackTrace();
                TTExceptionLogSQLite.insert(ex.getMessage(), ex.getStackTrace().toString(), PrjConfig.frmMapPage, 111);
            }
        });
    }

    public static void checkPowerSavingMode(Activity activity) {
        Log.e("شششش", "ترک زدن"+"پنل فعال است - 251");
        PowerManager powerManager = (PowerManager)
                activity.getSystemService(Context.POWER_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && powerManager.isPowerSaveMode()){
            Log.e("شششش", "ترک زدن"+"پنل فعال است - 252");
            projectStatics.showDialog(activity, activity.getString(R.string.CheckSavingPowerMode_Title)
                    , activity.getString(R.string.CheckSavingPowerMode_Desc), activity.getString(R.string.ok),
                    view -> {
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

    public void setVisibilityAndEnvironment(boolean isRecording){
        pnlRecordTrack.setVisibility(View.VISIBLE);

        btnGoToTripComputer.setVisibility(View.VISIBLE);
        btnGoToTripComputerParent.setVisibility(View.VISIBLE);
        if (isRecording){
            lblFinishRecording.setText(mapPage.getString(R.string.endSaveTrack));
            btnPlayPause.setVisibility(View.VISIBLE);
            //btnGoToTripComputer.setVisibility(View.VISIBLE);
            //btnGoToTripComputerParent.setVisibility(View.VISIBLE);
        }
        else{
            lblFinishRecording.setText(mapPage.getString(R.string.startSaveTrack));
            btnPlayPause.setVisibility(View.INVISIBLE);
            //btnGoToTripComputer.setVisibility(View.INVISIBLE);
            //btnGoToTripComputerParent.setVisibility(View.INVISIBLE);
        }
    }
    public void startRecording() {
        checkPowerSavingMode(activity);

        if (btnFinishRecording == null)
            initializeComponentes();
        lastLocation = null;
        //pnlRecordTrack.setVisibility(View.VISIBLE);
        setVisibilityAndEnvironment(true);
        setIsRecording(true);
        setIsRecordPanelActive(true);
        Toast.makeText(activity, R.string.MsgRecordingStarted, Toast.LENGTH_LONG);
        setViewOfBtnPlay();
    }

    public void onResumeRecordingPanel(LatLng lastLocation) {
        setIsRecordPanelActive(true);
        if (btnFinishRecording == null)
            initializeComponentes();
        this.lastLocation = lastLocation;
        //pnlRecordTrack.setVisibility(View.VISIBLE);
        setVisibilityAndEnvironment(true);
        setViewOfBtnPlay();
    }

    public void drawBackgroundPoints(List<NbCurrentTrack> backCurrentTrack) {
        if (!getIsRecordPanelActive()) {
            Log.e("ترک زدن", "HHH");
            return;
        }
        Log.e("ترک زدن", "III");

        currentTrack.clear();
        currentTrack.addAll(backCurrentTrack);
        int trkSize = backCurrentTrack.size();
        Log.e("ترک زدن", "تعداد نقطه های بک گراند" + trkSize);
        veryCurrentRoutePoints.clear();
        for (int i = 0; i < trkSize; i++) {
            NbCurrentTrack currentTrack =backCurrentTrack.get(i);
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
            Log.e("ترک زدن", "نقشه نال بود و بی خیال شدیم");
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
        Log.e("ترک زدن", "پنل فعال است - 300");

        Polyline route = getCurrentTrackPolylineDrawer(veryCurrentRoutePoints);
        polylines.add(route);
        Log.e("ترک زدن", "پلی لاین ترسیم شد" + "With Size: " + veryCurrentRoutePoints.size() + " and PolyLines Size:" + polylines.size());
        veryCurrentRoutePoints.clear();
    }

    public List<LatLng> veryCurrentRoutePoints = new ArrayList<>();

    public void drawNextPoint(LatLng currentLatLon, float Elevation) {
        if (currentLatLon == null)
            return;
        Log.e("ترک", "Start drawNextPoint()");
        if (!getIsRecordPanelActive() || !getIsRecording())
            return;
        Log.e("ترک", "100- drawNextPoint()");
        NbCurrentTrack trkPt = new NbCurrentTrack();
        trkPt.Latitude = currentLatLon.latitude;
        trkPt.Longitude = currentLatLon.longitude;
        trkPt.Time = System.currentTimeMillis();
        trkPt.Elevation = Elevation;

        if (trkPt.Time - lastTime < 1000)
            return;
        currentTrack.add(trkPt);
        NbCurrentTrackSQLite.insert(trkPt);


        //veryCurrentRoutePoints = new ArrayList<>();
        if (lastLocation != null) {
            veryCurrentRoutePoints.add(lastLocation);
            veryCurrentRoutePoints.add(currentLatLon);
        }
        lastLocation = currentLatLon;
        lastTime = trkPt.Time;

        if (activity.map == null)
            return;
        Log.e("ترک", "200- drawNextPoint()");
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
        app.session.setIsTrackRecording(active ? 1 : 2);
        this.IsRecording = active ? 1 : 2;
    }
}
