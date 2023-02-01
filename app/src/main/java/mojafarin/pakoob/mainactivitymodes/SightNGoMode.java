package mojafarin.pakoob.mainactivitymodes;

import android.graphics.Color;
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
import java.util.Random;

import bo.entity.NbPoi;
import maptools.GPXFile;
import maptools.TrackData;
import maptools.hMapTools;
import mojafarin.pakoob.MainActivity;
import mojafarin.pakoob.MapPage;
import mojafarin.pakoob.R;
import mojafarin.pakoob.app;
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
    public TextView btnCaptureSightNGo, btnSaveSightNGo, btnDiscardSightNGo, btnSightNGoSettings;
    MainActivity activity;
    View pnlSightNGo;
    public int distanceInMeters = 55000;
    MapPage mapPage;

    public SightNGoMode(MainActivity mainActivity, MapPage mapPage) {
        activity = mainActivity;
        this.mapPage = mapPage;
        initializeComponentes();

    }

    private void initializeComponentes() {
        btnCaptureSightNGo = activity.findViewById(R.id.btnCaptureSightNGo);
        btnSaveSightNGo = activity.findViewById(R.id.btnSaveSightNGo);
        btnDiscardSightNGo = activity.findViewById(R.id.btnDiscardSightNGo);
        pnlSightNGo = activity.findViewById(R.id.pnlSightNGo);
        btnSightNGoSettings = activity.findViewById(R.id.btnSightNGoSettings);

        btnCaptureSightNGo.setOnClickListener(view -> {
            btnCaptureSightNGo_Click();
        });
        btnDiscardSightNGo.setOnClickListener(view -> {
            btnDiscardSightNGo_Click();
        });
        btnSaveSightNGo.setOnClickListener(view -> {
            btnSaveSightNGo_Click();
        });
        btnSightNGoSettings.setOnClickListener(view -> {
            btnSightNGoSettings_Click();
        });
    }

    private void btnSightNGoSettings_Click() {

        projectStatics.showEnterTextDialog(activity, activity.getResources().getString(R.string.EnterNewDistance_Title)
                , activity.getResources().getString(R.string.EnterNewDistance_Desc)
                , Integer.toString(distanceInMeters), activity.getResources().getString(R.string.btnAccept)
                , view -> {
                    View parent = (View) view.getParent().getParent();

                    EditText txtInput = parent.findViewById(R.id.txtInput);
                    String text = txtInput.getText().toString();
                    if (text.trim().length() == 0) {
                        projectStatics.showDialog(activity
                                , activity.getResources().getString(R.string.vali_GeneralError_Title)
                                , activity.getResources().getString(R.string.vali_PleaseEnterName)
                                , activity.getResources().getString(R.string.ok)
                                , null
                                , ""
                                , null);
                        return;
                    }
                    this.distanceInMeters = Integer.parseInt(text);
                    if (isCaptured)
                        drawSightNGo(true);
                }
                , activity.getResources().getString(R.string.btnCancel), null, TYPE_CLASS_NUMBER
                , true);
    }

    private void btnSaveSightNGo_Click() {
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
    }

    private void btnCaptureSightNGo_Click() {
        if (!isCaptured) {
            drawSightNGo(false);
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
        //drawSightNGo();
    }

    List<LatLng> points;
    double currentAngle = 0;

    public void drawSightNGo(boolean redrawOldLine) {
        if (currentLatLon == null)
            return;
        if (redrawOldLine && isCaptured) {
            LatLng pt1 = points.get(0);
            points.clear();
            points.add(pt1);
            points.add(hMapTools.newPointAtDistanceAndDegree(currentLatLon, distanceInMeters, currentAngle + app.declination));
        } else {
            points = new ArrayList<>();
            points.add(currentLatLon);
            points.add(hMapTools.newPointAtDistanceAndDegree(currentLatLon, distanceInMeters, angle + app.declination));
            currentAngle = angle;
        }
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
