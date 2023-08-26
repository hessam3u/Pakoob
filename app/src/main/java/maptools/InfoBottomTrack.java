package maptools;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

import bo.entity.NbLogSearch;
import bo.entity.NbPoi;
import bo.sqlite.NbLogSearchSQLite;
import mojafarin.pakoob.MainActivity;
import mojafarin.pakoob.R;
import mojafarin.pakoob.SafeGpxSearch;
import mojafarin.pakoob.ShowMapDialog;
import mojafarin.pakoob.app;
import utils.MainActivityManager;
import utils.hutilities;
import utils.projectStatics;

public class InfoBottomTrack extends BottomSheetDialogFragment {
    public NbPoi poi;
    View view;
    Button btnDoShowHide, btnRoadRooting, btnShowALlOnMap, btnLinkNew, btnTrackProperties, btnGoToTrack;
    Context context;

    public InfoBottomTrack() {
    }

    public static InfoBottomTrack getInstance(NbPoi poi, Context context) {
        InfoBottomTrack res = new InfoBottomTrack();
        res.poi = poi;
        res.context = context;
        return res;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_infobottom_track, parent, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView txtSelectedName = view.findViewById(R.id.txtSelectedName);
        txtSelectedName.setText(poi.Name);


        btnDoShowHide = view.findViewById(R.id.btnDoShowHide);
        btnDoShowHide.setOnClickListener(view1 -> {
            this.dismiss();

            byte ShowStatus = NbPoi.Enums.ShowStatus_Hide;
            List<NbPoi> res = app.SetShowHide(poi, ShowStatus, true, context);

            //((MainActivityManager) context).showFragment(WeatherShow.getInstance(this.poi, PrjConfig.frmMapPage));
        });
        btnRoadRooting = view.findViewById(R.id.btnRoadRooting);
        btnRoadRooting.setOnClickListener(view1 -> {
            NbLogSearchSQLite.insert(NbLogSearch.getInstance(NbLogSearch.CommandType_OpenCityRouting, "", this.poi.LatS, this.poi.LonW, 0));

            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?saddr=" + this.poi.LatS + "," + this.poi.LonW + "&daddr=" + this.poi.LatS + "," + this.poi.LonW));
            startActivity(intent);
        });
        btnShowALlOnMap = view.findViewById(R.id.btnShowALlOnMap);
        btnShowALlOnMap.setOnClickListener(view1 -> {
            this.dismiss();
            ShowMapDialog showMapDialog = null;
            showMapDialog = ShowMapDialog.getInstance();
            showMapDialog.setForModeShowTrack(poi, vv -> {
                //dialogMap.dismiss();
            });
            if (true || !showMapDialog.isAdded())
                ((MainActivity) context).showFragment(showMapDialog);
            else{
            }
        });

        btnTrackProperties = view.findViewById(R.id.btnTrackProperties);
        btnTrackProperties.setOnClickListener(view1 -> {
            this.dismiss();
            ((MainActivity) context).openEditTrack(this.poi.NbPoiId, "MainActivity", 0, null);
//            if (this.poi.NbPoiId > 0) {
//                ((MainActivity) context).showNbPoiOnMapForEdit(this.poi.NbPoiId, this.poi.Name);
//            } else {
//                ((MainActivity) context).btnAddWaypoint_Click(new LatLng(this.poi.LatS, this.poi.LonW));
//            }
            ((MainActivity) context).mapPage.clearAllSearchResults();
        });
        btnGoToTrack = view.findViewById(R.id.btnGoToTrack);
        btnGoToTrack.setOnClickListener(view1 -> {
            this.dismiss();
            projectStatics.showDialog(context
                    , this.getResources().getString(R.string.featureCommingSoon_Title)
                    , this.getResources().getString(R.string.featureCommingSoon_Desc)
                    , this.getResources().getString(R.string.ok), null, "", null);
//            ((MainActivity) context).mapPage.clearAllSearchResults();
//            if (poi.Name == null || poi.Name.length() == 0)
//                poi.Name = "هدف انتخاب شده";
//
//            ((MainActivity) context).mapPage.goToTargetMode.initNavigateToPoint
//                    (new LatLng(poi.LatS, poi.LonW), poi, 0);
        });

    }
}