package mojafarin.pakoob.mainactivitymodes;

import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.github.eloyzone.jalalicalendar.JalaliDate;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import bo.entity.NbPoi;
import maptools.GPXFile;
import maptools.TrackData;
import mojafarin.pakoob.MainActivity;
import mojafarin.pakoob.MapPage;
import mojafarin.pakoob.R;
import mojafarin.pakoob.app;
import maptools.GeoCalcs;
import utils.MyDate;
import utils.projectStatics;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static mojafarin.pakoob.MainActivity.isTrackUp;
import static mojafarin.pakoob.MainActivity.map;
import static mojafarin.pakoob.MapPage.angle;
import static mojafarin.pakoob.MainActivity.currentLatLon;
import static mojafarin.pakoob.MapPage.IsInSightNGoMode;

public class SightNGoMode {
    public boolean isCaptured = false;
    Polyline sightnNGoLine = null;
    public TextView btnCaptureSightNGo, btnSaveSightNGo, btnGotoSightNGo, btnDiscardSightNGo, btnSightNGoSettings, txtHeading, btnManualChangeAzimuth;
    MainActivity activity;
    View pnlSightNGo;
    public int distanceInMeters = 5000;
    MapPage mapPage;

    public Timer timer;
    Handler handler = new Handler(Looper.getMainLooper());
    TimerTask t;

    public SightNGoMode(MainActivity mainActivity, MapPage mapPage) {
        activity = mainActivity;
        this.mapPage = mapPage;
        initializeComponentes();

    }

    private void initializeComponentes() {
        btnCaptureSightNGo = activity.findViewById(R.id.btnCaptureSightNGo);
        btnSaveSightNGo = activity.findViewById(R.id.btnSaveSightNGo);
        btnGotoSightNGo = activity.findViewById(R.id.btnGotoSightNGo);
        btnDiscardSightNGo = activity.findViewById(R.id.btnDiscardSightNGo);
        pnlSightNGo = activity.findViewById(R.id.pnlSightNGo);
        btnSightNGoSettings = activity.findViewById(R.id.btnSightNGoSettings);
        btnManualChangeAzimuth = activity.findViewById(R.id.btnManualChangeAzimuth);
        txtHeading = activity.findViewById(R.id.txtHeading);

        btnCaptureSightNGo.setOnClickListener(view -> {
            btnCaptureSightNGo_Click(distanceInMeters, angle);
        });
        btnDiscardSightNGo.setOnClickListener(view -> {
            btnDiscardSightNGo_Click();
        });
        btnSaveSightNGo.setOnClickListener(view -> {
            btnSaveSightNGo_Click();
        });
        btnGotoSightNGo.setOnClickListener(view -> {
            btnGotoSightNGo_Click();
        });
        btnSightNGoSettings.setOnClickListener(view -> {
            btnSightNGoSettings_Click();
        });
        btnManualChangeAzimuth.setOnClickListener(view -> {
            btnManualChangeAzimuth_Click();
        });
    }

    private void btnManualChangeAzimuth_Click() {
        projectStatics.showEnterTextDialog(activity, activity.getResources().getString(R.string.SightNGo_EnterNewAzimuth_Title)
                , activity.getResources().getString(R.string.SightNGo_EnterNewAzimuth_Desc)
                , Integer.toString((int)capturedAngle), activity.getResources().getString(R.string.btnAccept)
                , view -> {
                    View parent = (View) view.getParent().getParent();

                    EditText txtInput = parent.findViewById(projectStatics.showEnterTextDialog_EditTextId);
                    String text = txtInput.getText().toString();
                    if (text.trim().length() == 0) {
                        projectStatics.showDialog(activity, activity.getResources().getString(R.string.vali_GeneralError_Title), activity.getResources().getString(R.string.SightNGo_vali_PleaseEnterDistance), activity.getResources().getString(R.string.ok), null, "", null);
                        return;
                    }
                    double tmpAngle = Integer.parseInt(text);
                    if (tmpAngle < 0 || tmpAngle > 359){
                        projectStatics.showDialog(activity, activity.getResources().getString(R.string.vali_GeneralError_Title), activity.getResources().getString(R.string.SightNGo_vali_PleaseValidAzimuth_Desc), activity.getResources().getString(R.string.ok), null, "", null);
                        return;
                    }
                    this.capturedAngle = tmpAngle;
                    isCaptured = false;
                    btnCaptureSightNGo_Click(distanceInMeters, capturedAngle);
                }
                , activity.getResources().getString(R.string.btnCancel), null, TYPE_CLASS_NUMBER
                , true);
    }

    private void btnSightNGoSettings_Click() {
        projectStatics.showEnterTextDialog(activity, activity.getResources().getString(R.string.SightNGo_EnterNewDistance_Title)
                , activity.getResources().getString(R.string.SightNGo_EnterNewDistance_Desc)
                , Integer.toString(distanceInMeters), activity.getResources().getString(R.string.btnAccept)
                , view -> {
                    View parent = (View) view.getParent().getParent();

                    EditText txtInput = parent.findViewById(projectStatics.showEnterTextDialog_EditTextId);
                    String text = txtInput.getText().toString();
                    if (text.trim().length() == 0) {
                        projectStatics.showDialog(activity
                                , activity.getResources().getString(R.string.vali_GeneralError_Title)
                                , activity.getResources().getString(R.string.SightNGo_vali_PleaseEnterDistance)
                                , activity.getResources().getString(R.string.ok)
                                , null
                                , ""
                                , null);
                        return;
                    }
                    this.distanceInMeters = Integer.parseInt(text);
                    if (isCaptured)
                        drawSightNGo(this.distanceInMeters, capturedAngle);
                }
                , activity.getResources().getString(R.string.btnCancel), null, TYPE_CLASS_NUMBER
                , true);
    }
    private void btnGotoSightNGo_Click() {

        double tmpAngle = angle;
        if (isCaptured)
            tmpAngle = angle;
        LatLng dest = GeoCalcs.newPointAtDistanceAndDegree(currentLatLon, distanceInMeters, tmpAngle + app.declination);//

        NbPoi poi = new NbPoi();
        poi.Name = "هدف خط نگاه";

        mapPage.goToTargetMode.initNavigateToPoint(new LatLng(dest.latitude, dest.longitude), poi, 0);

        btnDiscardSightNGo_Click();
    }

    private void btnSaveSightNGo_Click() {
        if (!isCaptured){
            projectStatics.showDialog(activity
                    , activity.getResources().getString(R.string.SightNG_ovali_PleaseDraw_Title)
                    , activity.getResources().getString(R.string.SightNGo_vali_PleaseDraw_Desc)
                    , activity.getResources().getString(R.string.ok)
                    , null
                    , ""
                    , null);
            return;
        }
        TrackData data = new TrackData();
        short poiType = NbPoi.Enums.PoiType_Route;
        Random rand = new Random();

        data.Color = GPXFile.RandomColors.get(rand.nextInt(GPXFile.colorCount));
        Calendar now = Calendar.getInstance();
        JalaliDate jalaliDate = MyDate.getJalaliDate(now);
        data.Name = activity.getResources().getString(R.string.SightNGo) + " " + Integer.toString(jalaliDate.getYear()) + Integer.toString(jalaliDate.getMonthPersian().getValue()) + Integer.toString(jalaliDate.getDay())
                + "-" + Integer.toString(now.get(Calendar.HOUR_OF_DAY)) + Integer.toString(now.get(Calendar.MINUTE)) + Integer.toString(now.get(Calendar.SECOND));
        data.Points.addAll(points);
        try {
            NbPoi saved = GPXFile.SaveDesignedRouteToDb(0, poiType, data, activity);
            if (saved != null && saved.NbPoiId != 0) {
                activity.openEditTrack(saved.NbPoiId, "MainActivity", 0, null);

//            Intent it = new Intent(activity, EditTrack.class);
//            it.putExtra("NbPoiId", saved.NbPoiId);
//            it.putExtra("Sender", "MainActivity");
//            activity.startActivity(it);

                btnDiscardSightNGo_Click();
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

    public void btnDiscardSightNGo_Click() {
        if (sightnNGoLine != null) {
            sightnNGoLine.setVisible(false);
        }
        pnlSightNGo.setVisibility(View.GONE);
        IsInSightNGoMode = false;
        isCaptured = false;
        timer.cancel();
    }

    private void btnCaptureSightNGo_Click(int distanceToDraw, double angleToDraw) {
        if (!isCaptured) {
            drawSightNGo(distanceToDraw, angleToDraw);
            btnCaptureSightNGo.setText(activity.getResources().getString(R.string.btnReset));
        } else {
            sightnNGoLine.setVisible(false);
            btnCaptureSightNGo.setText(activity.getResources().getString(R.string.btnCaptureSightNGo));
        }
        isCaptured = !isCaptured;
    }

    public void initSightNGoPanel() {
        if (currentLatLon == null) {
            projectStatics.showDialog(activity
                    , activity.getResources().getString(R.string.vali_NoCurrectLocation)
                    , activity.getResources().getString(R.string.vali_NoCurrectLocation_Desc)
                    , activity.getResources().getString(R.string.ok), null, "", null);
            return;
        }
        isTrackUp = false; //این رو الکی اینطوری میکنم که وقتی به تابع زیر رفت، حتما ترک آپ بشه 1401-09-07
        mapPage.btnGotoCurrentLocation.callOnClick();
        pnlSightNGo.setVisibility(View.VISIBLE);
        IsInSightNGoMode = true;
        //1401-11-25 two lines:
        btnCaptureSightNGo.setText(activity.getResources().getString(R.string.btnCaptureSightNGo));
        isCaptured = false;
        txtHeading.setText("-");

        //drawSightNGo();
        timer = new Timer();
        t = new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> {
                    double heading = MapPage.angle;
                    if (heading < 0){
                        heading = 180 + (heading + 180);
                    }
                    txtHeading.setText(String.format(Locale.ENGLISH, "%d°%1d'", (int)heading, (int)((heading - (int)heading) * 60)));
                    }
                );
            }
        };
        timer.schedule(t, 0, 200);
    }

    List<LatLng> points;
    double capturedAngle = 0;

    public void drawSightNGo(int distanceToDraw, double angleToDraw) {
        if (currentLatLon == null)
            return;
        points = new ArrayList<>();
        points.add(currentLatLon);
        points.add(GeoCalcs.newPointAtDistanceAndDegree(currentLatLon, distanceToDraw, angleToDraw + app.declination));
        capturedAngle = angleToDraw;

        if (sightnNGoLine == null) {
            sightnNGoLine = map.addPolyline(new PolylineOptions()
                    .width(10)
                    .color(Color.MAGENTA)
                    .geodesic(false)
                    .zIndex(30));
            sightnNGoLine.setPoints(points);
        } else {
            sightnNGoLine.setVisible(true);
            sightnNGoLine.setPoints(points);
        }
    }

}
