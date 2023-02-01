package mojafarin.pakoob;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.health.HealthStats;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.function.DoubleUnaryOperator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import bo.entity.NbPoi;
import bo.sqlite.NbPoiSQLite;
import maptools.TrackData;
import maptools.TrackProperties;
import maptools.hMapTools;

public class TrackInfo extends Fragment {
    long NbPoiId = 0;
    NbPoi currentObj = null;
    TrackData data = null;
    String Sender = "";
    public TrackInfo(long nbPoiId, String sender){
//        Intent intent = getIntent();
//        NbPoiId = intent.getLongExtra("NbPoiId", 0);
//        Sender = intent.getStringExtra("Sender");
        NbPoiId = nbPoiId;
        Sender = sender;
        currentObj = NbPoiSQLite.select(NbPoiId);
        data = TrackData.readTrackData(currentObj.Address, null);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {//4th Event
        super.onViewCreated(view, savedInstanceState);

        initializeComponents(view);
        fillForm(currentObj);
    }
    private void fillForm(NbPoi currentObj) {
        txtPageTitle.setText(currentObj.Name);

        int dataSize = data.Points.size();

        //1:
//        LatLng cPoint = null;
//        LatLng pPoint = null;
//        long cTime = 0;
//        long pTime = 0;
//        float cElev = -1000;
//        float pElev = -1000;
//        double totalAscent = 0;
//        double totalDecent = 0;
//        long totalTime = 0;
//        long movingMs = 0;
//        long stoppedMs = 0;
//        double cSpeed = 0;
//        double maxSpeed = 0;
//        double minSpeed = 0;
//        long ascentMs = 0;
//        long decentMs = 0;
//        double distance = 0;
//        double ascentDistance = 0;
//        double descentDistance = 0;
//        float maxElevation = -1000000;
//        float minElevation = 1000000;

        TrackProperties tp = new TrackProperties();
        tp.initFromTrackData(data);

        txtNumberOfPoints.setText(String.valueOf(dataSize));
        txtDistance.setText(hMapTools.distanceBetweenFriendly(tp.distance));
        txtAscentTotal.setText(hMapTools.distanceBetweenFriendly(tp.totalAscent));
        txtDescentTotal.setText(hMapTools.distanceBetweenFriendly(tp.totalDecent));
        txtAscentTotal1.setText(hMapTools.distanceBetweenFriendly(tp.totalAscent));
        txtDescentTotal1.setText(hMapTools.distanceBetweenFriendly(tp.totalDecent));
        txtDistanceAscent.setText(hMapTools.distanceBetweenFriendly(tp.ascentDistance));
        txtDistanceDescent.setText(hMapTools.distanceBetweenFriendly(tp.descentDistance));
        txtMaxElevation.setText(hMapTools.distanceBetweenFriendly(tp.maxElevation));
        txtMinElevation.setText(hMapTools.distanceBetweenFriendly(tp.minElevation));

        txtDuration.setText(hMapTools.timeFriendly(tp.totalTime / 1000, hMapTools.TIME_FRIENDLY_MODE_LONG_EXACT));
        txtDurationAscent.setText(hMapTools.timeFriendly(tp.ascentMs / 1000, hMapTools.TIME_FRIENDLY_MODE_LONG_EXACT));
        txtDurationDescent.setText(hMapTools.timeFriendly(tp.decentMs / 1000, hMapTools.TIME_FRIENDLY_MODE_LONG_EXACT));

        txtAvgSpeed.setText(hMapTools.GetSpeedFriendlyKmPh(tp.distance / (tp.totalTime/1000) ) + "kmh");
        txtMaxSpeed.setText(hMapTools.GetSpeedFriendlyKmPh(tp.maxSpeed ) + "kmh");

        double ascentSlope = Math.atan(tp.totalAscent / (tp.ascentDistance * 1d)) * 180 / Math.PI;
        double descentSlope = Math.atan(tp.totalDecent / (tp.descentDistance* 1d)) * 180 / Math.PI;
        txtSlopeAscent.setText(((int)(ascentSlope * 100) / 100d ) + "°");
        txtSlopeDescent.setText(((int)(descentSlope * 100) / 100d ) +"°");
    }

    TextView txtPageTitle, btnBack;
    TextView txtNumberOfPoints, txtDistance, txtDuration, txtDurationMoving, txtDurationStopped, txtAvgSpeed, txtMaxSpeed, txtAvgSpeedMoving
            , txtDescentTotal, txtAscentTotal, txtMaxElevation, txtMinElevation, txtAvgPace, txtMaxPace;
    TextView txtAscentTotal1, txtDescentTotal1, txtDurationAscent, txtDistanceAscent, txtAvgSpeedAscent, txtSlopeAscent
            , txtDistanceDescent, txtDurationDescent, txtAvgSpeedDescent, txtSlopeDescent;
    private void initializeComponents(View v) {

        btnBack =v.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(view -> {((AppCompatActivity) context).onBackPressed();});

        txtPageTitle = v.findViewById(R.id.txtPageTitle);

        txtAscentTotal1 = v.findViewById(R.id.txtAscentTotal1);
        txtDescentTotal1 = v.findViewById(R.id.txtDescentTotal1);
        txtDurationAscent = v.findViewById(R.id.txtDurationAscent);
        txtDistanceAscent = v.findViewById(R.id.txtDistanceAscent);
        txtAvgSpeedAscent = v.findViewById(R.id.txtAvgSpeedAscent);
        txtSlopeAscent = v.findViewById(R.id.txtSlopeAscent);
        txtDistanceDescent = v.findViewById(R.id.txtDistanceDescent);
        txtDurationDescent = v.findViewById(R.id.txtDurationDescent);
        txtAvgSpeedDescent = v.findViewById(R.id.txtAvgSpeedDescent);
        txtSlopeDescent = v.findViewById(R.id.txtSlopeDescent);

        txtNumberOfPoints = v.findViewById(R.id.txtNumberOfPoints);
        txtDistance = v.findViewById(R.id.txtDistance);
        txtDuration = v.findViewById(R.id.txtDuration);
        txtDurationMoving = v.findViewById(R.id.txtDurationMoving);
        txtDurationStopped = v.findViewById(R.id.txtDurationStopped);
        txtAvgSpeed = v.findViewById(R.id.txtAvgSpeed);
        txtMaxSpeed = v.findViewById(R.id.txtMaxSpeed);
        txtAvgSpeedMoving = v.findViewById(R.id.txtAvgSpeedMoving);
        txtDescentTotal = v.findViewById(R.id.txtDescentTotal);
        txtAscentTotal = v.findViewById(R.id.txtAscentTotal);
        txtMaxElevation = v.findViewById(R.id.txtMaxElevation);
        txtMinElevation = v.findViewById(R.id.txtMinElevation);
        txtAvgPace = v.findViewById(R.id.txtAvgPace);
        txtMaxPace = v.findViewById(R.id.txtMaxPace);
    }



    Context context;
    @Override
    public void onAttach(Context _context) { //1st Event
        super.onAttach(context);
        this.context = _context;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {//2nd Event
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {//3nd Event
        return inflater.inflate(R.layout.mytracks_trackinfo, parent, false);
    }

}
