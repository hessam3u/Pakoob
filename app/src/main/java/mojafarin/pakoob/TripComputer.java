package mojafarin.pakoob;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import bo.entity.NbCurrentTrack;
import bo.sqlite.NbCurrentTrackSQLite;
import bo.sqlite.TTExceptionLogSQLite;
import maptools.TrackProperties;
import maptools.GeoCalcs;
import utils.HFragment;
import utils.MainActivityManager;
import utils.PrjConfig;

public class TripComputer extends HFragment {
    public static boolean NewTripDataRecieved = true;
    public static TripComputer getInstance(){
        TripComputer res = new TripComputer();
        return res;
    }

    Timer timer = new Timer();
    Handler handler = new Handler(Looper.getMainLooper());
    TimerTask t;

    public TripComputer(){
        Tag = "محاسبه سفر";
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {//4th Event
        super.onViewCreated(view, savedInstanceState);
        initializeComponents(view);
        fillForm();

        t = new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> {
                    tripComputerTabManager.callTimerTick();
                        }
                );
            }
        };
        timer.schedule(t, 0, 1000);

    }
    @Override
    public void onDetach() {
        timer.cancel();
        super.onDetach();
    }
    private void fillForm() {
        try {
        } catch (Exception ex) {
            Log.e(Tag, "بازکردن" + "fillForm_on_safeGpxView: " + ex.getMessage() + ex.getStackTrace());
            //Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            ex.printStackTrace();
            TTExceptionLogSQLite.insert(ex.getMessage(), stktrc2k(ex), PrjConfig.frmTripComputer, 150);
        }
    }


    TextView txtPageTitle, btnBack;
    ViewPager viewPager;
    TripComputerTabManager tripComputerTabManager;
    @Override
    public void initializeComponents(View v) {

        btnBack =v.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(view -> {((MainActivityManager) context).onBackPressed();});
        txtPageTitle = v.findViewById(R.id.txtPageTitle);

        Log.e("هههه", "ست کردن اولیه تب");
        tripComputerTabManager = new TripComputerTabManager(getChildFragmentManager(), context);
        viewPager = v.findViewById(R.id.viewpagerForTripComputer);
        viewPager.setAdapter(tripComputerTabManager);
        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.tabForTripComputer);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        Log.e("هههه", "پایان ست کردن اولیه تب");


        super.initializeComponents(v);
    }

    public class TripComputerTabManager extends FragmentPagerAdapter {
        final int PAGE_COUNT = 2;
        private String tabTitles[] = new String[]{"آمار مسیر", "اطلاعات پیشرفته"};
        TripComputerPage1 fragment0;
        TripComputerPage2 fragment1;

        private Context context;

        public TripComputerTabManager(FragmentManager fm, Context context) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.context = context;
        }

        public void callTimerTick(){
            if (fragment0 != null)
                fragment0.timerFunctionTick();
//            if (fragment1 != null)
//                fragment1.timerFunctionTick();
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                default:
                case 0: {
                    if (fragment0 == null) {
                        fragment0 = TripComputerPage1.getInstance();
                    }
                    return fragment0;
                }
                case 1: {
                    if (fragment1 == null) {
                        fragment1 = TripComputerPage2.getInstance();
                    }
                    return fragment1;
                }
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            return tabTitles[position];
        }
    }

    public static class TripComputerPage1 extends HFragment {


        TextView txtDistance, txtDuration, txtElevation, txtAscentTotal, txtDescentTotal, txtMaxSpeed, txtAvgSpeed;
        TextView txtMinElevation, txtMaxElevation, txtSpeed;

        MainActivity mainActivity = null;
        long totalTrackSeconds = 0;
        public static TripComputerPage1 getInstance() {
            TripComputerPage1 res = new TripComputerPage1();
            return res;
        }
        public TripComputerPage1(){
        }
        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {//4th Event
            super.onViewCreated(view, savedInstanceState);
            mainActivity = ((MainActivity)context);
            initializeComponents(view);
        }
        @Override
        public void initializeComponents(View v) {

            txtDistance = v.findViewById(R.id.txtDistance);
            txtDuration = v.findViewById(R.id.txtDuration);
            txtElevation = v.findViewById(R.id.txtElevation);
            txtAscentTotal = v.findViewById(R.id.txtAscentTotal);
            txtDescentTotal = v.findViewById(R.id.txtDescentTotal);
            txtMaxSpeed = v.findViewById(R.id.txtMaxSpeed);
            txtAvgSpeed = v.findViewById(R.id.txtAvgSpeed);
            txtMaxElevation = v.findViewById(R.id.txtMaxElevation);
            txtMinElevation = v.findViewById(R.id.txtMinElevation);
            txtSpeed = v.findViewById(R.id.txtSpeed);

            txtDistance.setText("-");
            txtElevation.setText("-");
            txtAscentTotal.setText("-");
            txtDescentTotal.setText("-");
            txtMaxSpeed.setText("-");
            txtAvgSpeed.setText("-");
            txtMaxElevation.setText("-");
            txtMinElevation.setText("-");
            txtDuration.setText("-");
            txtSpeed.setText("-");


            TrackProperties tp = setTrackProperties(true);

            if (mainActivity.mapPage.dialogRecordTrack.getIsRecording())
            {
                if (tp != null && tp.startDateTimeMils > 0)
                    totalTrackSeconds = Calendar.getInstance().getTimeInMillis() - tp.startDateTimeMils;

                //فیلدهایی که خاص هستند
                setSpecialFields(true);

            }
            else{
                txtDistance.setText(context.getString(R.string.StartTrackRecordingToShowDistance));
            }


            super.initializeComponents(v);
        }
        public void timerFunctionTick(){
            totalTrackSeconds += 1000;
            //Log.e(Tag, "NEXT Fired" + ++counter);
            TrackProperties tp = setTrackProperties(false);
            setSpecialFields(false);
        }


        public void setSpecialFields(boolean forceCalculate){
            if (forceCalculate || mainActivity.mapPage.dialogRecordTrack.getIsRecording()){
                txtDuration.setText(GeoCalcs.timeFriendly(totalTrackSeconds / 1000, GeoCalcs.TIME_FRIENDLY_MODE_LONG_EXACT));
            }
        }
        public TrackProperties setTrackProperties(boolean forceCalculate){
            MainActivity mainActivity =((MainActivity)context);
            TrackProperties tp = null;

            txtElevation.setText((int) MainActivity.currentElev + "m");
            txtSpeed.setText((int) (int) (MainActivity.currentSpeed* 3.6) + "km/h ");


            if (forceCalculate || mainActivity.mapPage.dialogRecordTrack.getIsRecording()) {
                List<NbCurrentTrack> oldCurrentTracks = NbCurrentTrackSQLite.selectAll();
                tp = new TrackProperties();
                tp.initFromNbCurrentTrack(oldCurrentTracks);


                txtDistance.setText(GeoCalcs.distanceBetweenFriendly(tp.distance));
                txtAscentTotal.setText(GeoCalcs.distanceBetweenFriendly(tp.totalAscent));
                txtDescentTotal.setText(GeoCalcs.distanceBetweenFriendly(tp.totalDecent));
                txtAvgSpeed.setText(GeoCalcs.GetSpeedFriendlyKmPh(tp.distance / (tp.totalTime/1000) ) + "kmh");
                txtMaxSpeed.setText(GeoCalcs.GetSpeedFriendlyKmPh(tp.maxSpeed ) + "kmh");
                if (tp.maxElevation != TrackProperties.MAX_ELEVATION_DEFAULT)
                    txtMaxElevation.setText(GeoCalcs.distanceBetweenFriendly(tp.maxElevation));
                else
                    txtMaxElevation.setText("-");
                if (tp.minElevation != TrackProperties.MIN_ELEVATION_DEFAULT)
                    txtMinElevation.setText(GeoCalcs.distanceBetweenFriendly(tp.minElevation));
                else
                    txtMinElevation.setText("-");
            }
            return tp;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {//3nd Event
            return inflater.inflate(R.layout.frm_tripcomputer_page1, parent, false);
        }
    }


    public static class TripComputerPage2 extends HFragment {
        TextView txtAccuracy, txtBearing, txtVerticalAccuracy, txtDelination, txtHeading, txtUpDownAngle, txtRotateAngle;

        Timer timer = new Timer();
        Handler handler = new Handler(Looper.getMainLooper());
        TimerTask t;

        MainActivity mainActivity = null;
        public static TripComputerPage2 getInstance() {
            TripComputerPage2 res = new TripComputerPage2();
            return res;
        }
        public TripComputerPage2(){
        }
        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {//4th Event
            super.onViewCreated(view, savedInstanceState);
            mainActivity = ((MainActivity)context);
            initializeComponents(view);

            t = new TimerTask() {
                @Override
                public void run() {
                    handler.post(() -> {
                        timerFunctionTick();
                            }
                    );
                }
            };
            timer.schedule(t, 0, 200);
        }
        @Override
        public void initializeComponents(View v) {
            txtAccuracy = v.findViewById(R.id.txtAccuracy);
            txtBearing = v.findViewById(R.id.txtBearing);
//            txtVerticalAccuracy = v.findViewById(R.id.txtVerticalAccuracy);
            txtDelination = v.findViewById(R.id.txtDelination);
            txtHeading = v.findViewById(R.id.txtHeading);
            txtUpDownAngle = v.findViewById(R.id.txtUpDownAngle);
            txtRotateAngle = v.findViewById(R.id.txtRotateAngle);

            txtAccuracy.setText("-");
            txtBearing.setText("-");
            txtHeading.setText("-");
            txtUpDownAngle.setText("-");
            txtRotateAngle.setText("-");
            txtDelination.setText("-");

           timerFunctionTick();

            super.initializeComponents(v);
        }

        public void timerFunctionTick(){
            if (MapPage.location != null){
                txtAccuracy.setText((int)MapPage.location.getAccuracy() + "m");
                txtDelination.setText(String.format(Locale.ENGLISH, "%d°%02d'", (int)app.declination, (int)((app.declination - (int)app.declination) * 60)));
                //txtHeading.setText(String.format(Locale.ENGLISH, "%d.%02d°", (int)MapPage.angle, (int)((MapPage.angle - (int)MapPage.angle) * 60)));
                float bearing = MapPage.location.getBearing();
                txtBearing.setText(String.format(Locale.ENGLISH, "%d°%02d'", (int)bearing, (int)((bearing - (int)bearing) * 60)));
                double heading = MapPage.angle;
                if (heading < 0){
                    heading = 180 + (heading + 180);
                }
                txtHeading.setText(String.format(Locale.ENGLISH, "%d°%1d'", (int)heading, (int)((heading - (int)heading) * 60)));

//1402-04 برای تست انحراف ها
//                LatLng tmp1 = new LatLng(36.29405,59.50385);
//                LatLng tmp2 = new LatLng(36.31445, 59.51782);
//                double DD= hMapTools.GetAzimuthInDegree(tmp1, tmp2);
//                Log.e("LATLON", "is: " + DD);
//                txtHeading.setText("is: " + DD);

                double angleYInPercent = Math.abs(MapPage.angleY) <89.5 ?(int)(Math.tan(Math.toRadians(MapPage.angleY)) * 100):10000;
                txtUpDownAngle.setText(String.format(Locale.ENGLISH, "%d°%1d' (%%%d)", (int)MapPage.angleY, (int)((MapPage.angleY - (int)MapPage.angleY) * 60), (int)angleYInPercent));
                txtRotateAngle.setText(String.format(Locale.ENGLISH, "%d°%1d'", (int)MapPage.angleZ, (int)((MapPage.angleZ - (int)MapPage.angleZ) * 60)));
//                txtHeading.setText(String.format("%d°%02d'", (int)heading, (int)((heading - (int)heading) * 60)));
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    float verticalAcc = MapPage.location.getVerticalAccuracyMeters();
//                    txtVerticalAccuracy.setText(verticalAcc + "m");
//                }
            }
        }
        @Override
        public void onDetach() {
            timer.cancel();
            super.onDetach();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {//3nd Event
            return inflater.inflate(R.layout.frm_tripcomputer_page2, parent, false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {//3nd Event
        return inflater.inflate(R.layout.frm_tripcomputer, parent, false);
    }
}
