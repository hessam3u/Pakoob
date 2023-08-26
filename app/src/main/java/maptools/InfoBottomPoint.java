package maptools;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import bo.entity.NbLogSearch;
import bo.entity.NbPoi;
import bo.sqlite.NbLogSearchSQLite;
import mojafarin.pakoob.MainActivity;
import mojafarin.pakoob.R;
import mojafarin.pakoob.SafeGpxSearch;
import mojafarin.pakoob.WeatherShow;
import utils.MainActivityManager;
import utils.PrjConfig;

public class InfoBottomPoint extends BottomSheetDialogFragment {
    public NbPoi poi;
    View view;
    Button btnOpenWeather, btnRoadRooting, btnDownloadTracks, btnLinkNew, btnEditWaypoint, btnGoInSearch;
    Context context;

    public InfoBottomPoint() {
    }

    public static InfoBottomPoint getInstance(NbPoi poi, Context context) {
        InfoBottomPoint res = new InfoBottomPoint();
        res.poi = poi;
        res.context = context;
        return res;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_infobottom_point, parent, false);
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
                    Uri.parse("http://maps.google.com/maps?saddr=" + this.poi.LatS + "," + this.poi.LonW + "&daddr=" + this.poi.LatS + "," + this.poi.LonW));
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
            if (this.poi.NbPoiId > 0) {
                ((MainActivity) context).showNbPoiOnMapForEdit(this.poi.NbPoiId, this.poi.Name);
            } else {
                ((MainActivity) context).btnAddWaypoint_Click(new LatLng(this.poi.LatS, this.poi.LonW));
            }
            ((MainActivity) context).mapPage.clearAllSearchResults();
        });
        btnGoInSearch = view.findViewById(R.id.btnGoInSearch);
        btnGoInSearch.setOnClickListener(view1 -> {
            this.dismiss();
            ((MainActivity) context).mapPage.clearAllSearchResults();
            if (poi.Name == null || poi.Name.length() == 0)
                poi.Name = "هدف انتخاب شده";

            ((MainActivity) context).mapPage.goToTargetMode.initNavigateToPoint
                    (new LatLng(poi.LatS, poi.LonW), poi, 0);
        });
        Log.e("شششش", "ID of POI: " + this.poi.NbPoiId + "-" + this.poi.ServerId);
        if (this.poi.NbPoiId > 0)
            btnEditWaypoint.setText(getString(R.string.btnSaveOrEdit_Edit));
        else
            btnEditWaypoint.setText(getString(R.string.btnSaveOrEdit_Save));

    }
}