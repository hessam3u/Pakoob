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
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.w3c.dom.Text;

import bo.NewClasses.InsUpdRes;
import bo.NewClasses.SimpleRequest;
import bo.NewClasses.StringContentDTO;
import bo.entity.NbLogSearch;
import bo.entity.NbPoi;
import bo.entity.NbWeather;
import bo.entity.SearchRequestDTO;
import bo.sqlite.NbLogSearchSQLite;
import mojafarin.pakoob.MainActivity;
import mojafarin.pakoob.R;
import mojafarin.pakoob.SafeGpxSearch;
import mojafarin.pakoob.WeatherShow;
import mojafarin.pakoob.app;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.MainActivityManager;
import utils.PrjConfig;
import utils.hutilities;
import utils.projectStatics;

public class InfoBottomPoint extends BottomSheetDialogFragment {
    public NbPoi poi;
    View view;
    Button btnOpenWeather, btnRoadRooting, btnDownloadTracks, btnLinkNew, btnEditWaypoint, btnGoInSearch;
    Context context;
    ProgressBar progressFromServer;


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
        progressFromServer = view.findViewById(R.id.progressFromServer);
        readItemInBackground();


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

    boolean isInBackgroundLoading = false;
    void readItemInBackground(){
        if (!hutilities.isInternetConnected(context)) {
            return;
        }
        isInBackgroundLoading = true;

        progressFromServer.setVisibility(View.VISIBLE);

        String strSearch = poi.LatS + "%*%" + poi.LonW;

        SearchRequestDTO search = new SearchRequestDTO();
        search.Filter = strSearch;
        SimpleRequest request = SimpleRequest.getInstance(search);
        Call<ResponseBody> call = app.apiMap.SearchLatLon(request);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!isAdded()) return;
                boolean readCompleted = false;
                try {
                    if (response.isSuccessful()) {
                        InsUpdRes result = InsUpdRes.fromBytes(response.body().bytes());

                        if (!result.isOk) {
                        } else {
                        }
                        if (result.isOk){
                            TextView txtTitleFromServer = view.findViewById(R.id.txtTitleFromServer);
                            TextView txtElevation = view.findViewById(R.id.txtElevation);
                            String[] parts = result.resValue.split("%\\*%");
                            if (parts.length > 0){
                                txtTitleFromServer.setText(parts[0]);
                            }
                            if (parts.length > 1){
                                if (parts[1] != "-1000")
                                    txtElevation.setText("ارتفاع حدودی " + parts[1] + "m");
                            }
                            readCompleted = true;
                        }
                    } else {
                    }
                    progressFromServer.setVisibility(View.INVISIBLE);
                    isInBackgroundLoading = false;

                } catch (Exception ex){
                    projectStatics.ManageCallExceptions(true, PrjConfig.frmInfoBottomOfMapPage,  120, ex, context);
                    progressFromServer.setVisibility(View.INVISIBLE);
                    isInBackgroundLoading = false;
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                projectStatics.ManageCallExceptions(true, PrjConfig.frmInfoBottomOfMapPage,  121, t, context);
                progressFromServer.setVisibility(View.INVISIBLE);
                isInBackgroundLoading = false;
            }
        });
    }
}