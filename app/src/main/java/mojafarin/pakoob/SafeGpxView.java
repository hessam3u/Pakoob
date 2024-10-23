package mojafarin.pakoob;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;

import bo.NewClasses.InsUpdRes;
import bo.NewClasses.SimpleRequest;
import bo.dbConstantsTara;
import bo.entity.BuyMapRequestDTO;
import bo.entity.DownloadRequest;
import bo.entity.NbMap;
import bo.entity.NbSafeGpx;
import bo.entity.NbSafeGpxList;
import bo.entity.SearchRequestDTO;
import bo.entity.TrackDataCompact;
import bo.entity.TrackDataCompactRes;
import bo.sqlite.TTExceptionLogSQLite;
import kotlin.coroutines.Continuation;
import maptools.GPXFile;
import maptools.hMapTools;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import user.Register;
import maptools.GeoCalcs;
import utils.HFragment;
import utils.MainActivityManager;
import utils.PicassoTrustAll;
import utils.PrjConfig;
import utils.TextFormat;
import utils.hutilities;
import utils.projectStatics;


public class SafeGpxView extends HFragment {
    public NbSafeGpx currentObj;
    int FromPageId;
    boolean isInBackgroundLoading = false;
    String backgroundMessage = "";
    int recId = 0;

    public static SafeGpxView getInstance(NbSafeGpx gpx, int FromPageId) {
        SafeGpxView res = new SafeGpxView();
        res.currentObj = gpx;
        res.FromPageId = FromPageId;
        return res;
    }

    public static SafeGpxView getInstance(Integer Id, int FromPageId) {
        SafeGpxView res = new SafeGpxView();
        res.FromPageId = FromPageId;
        res.currentObj = null;
        res.recId = Id;
        return res;
    }

    public SafeGpxView() {
        Tag = "نمایش_جی_پی_ایکس";
    }

    void progressBarPage_SetVisibility(int Visibility) {
        if (progressBarPage == null)
            return;
        progressBarPage.setVisibility(Visibility);
    }

    void txtSearchResult_SetText(String st) {
        if (txtSearchResult == null)
            return;
        if (st != null && st.length() > 0) {
            txtSearchResult.setVisibility(View.VISIBLE);
        } else {
            txtSearchResult.setVisibility(View.GONE);
        }
        txtSearchResult.setText(st);
    }

    void readItemInBackground(int IdToSearch) {
        if (!hutilities.isInternetConnected(context)) {
            projectStatics.showDialog(context, this.getResources().getString(com.pakoob.tara.R.string.NoInternet), this.getResources().getString(com.pakoob.tara.R.string.NoInternet_Desc), this.getResources().getString(com.pakoob.tara.R.string.ok), view -> {
            }, "", null);
            return;
        }
        isInBackgroundLoading = true;
        backgroundMessage = "";

        showHideEverythingForLoading(true);
        progressBarPage_SetVisibility(View.VISIBLE);
        txtSearchResult_SetText("");


        SearchRequestDTO requestDTO = new SearchRequestDTO();
        requestDTO.Filter = IdToSearch + "";

        Call<ResponseBody> call = app.apiMap.ReadOneSafeGpx(SimpleRequest.getInstance(requestDTO));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!isAdded()) return;
                boolean readCompleted = false;
                try {
                    if (response.isSuccessful()) {
                        NbSafeGpxList result = NbSafeGpxList.fromBytes(response.body().bytes());
                        if (!result.isOk) {
                            backgroundMessage = result.message != null ? result.message : "";
                        } else {
                            backgroundMessage = "";
                        }
                        if (result.resList.size() > 0) {
                            currentObj = result.resList.get(0);
                            readCompleted = true;
                        } else if (backgroundMessage.length() == 0) {
                            backgroundMessage = getResources().getString(R.string.NothingToShow);
                        }
                        txtSearchResult_SetText(backgroundMessage);

                        if (readCompleted && initCompleted) {
                            fillForm();
                            //Log.e("DDDDD", String.valueOf(readCompleted && initCompleted) + " ---Size:  "+ result.resList.size());
                            showHideEverythingForLoading(false);
                        }
                    } else {
                        projectStatics.ManageCallResponseErrors(true, PrjConfig.frmSafeGpxView, 100, context, response.code());
                        backgroundMessage = getResources().getString(R.string.NothingToShow);
                        txtSearchResult_SetText(backgroundMessage);
                    }
                } catch (Exception ex) {
                    Log.e(Tag, "Exception22 On First Read: " + ex.getMessage());
                    ex.printStackTrace();
                }

                progressBarPage_SetVisibility(View.GONE);
                if (readCompleted)
                    showHideEverythingForLoading(false);
                isInBackgroundLoading = false;

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                projectStatics.ManageCallExceptions(true, PrjConfig.frmSafeGpxView, 100, t, context);
                if (!isAdded()) return;
                Log.e(Tag, "Exception On First Read: " + t.getMessage());
                t.printStackTrace();
                progressBarPage_SetVisibility(View.GONE);
                backgroundMessage = getResources().getString(R.string.NothingToShow);
                txtSearchResult_SetText(backgroundMessage);
                isInBackgroundLoading = false;
            }
        });
    }

    TrackDataCompact trkData;

    void readTrackDataInBackground(int IdToSearch) {
        if (!hutilities.isInternetConnected(context)) {
//            projectStatics.showDialog(context, this.getResources().getString(com.pakoob.tara.R.string.NoInternet), this.getResources().getString(com.pakoob.tara.R.string.NoInternet_Desc), this.getResources().getString(com.pakoob.tara.R.string.ok), view -> {
//            }, "", null);
            chart.setNoDataText("برای نمایش اطلاعات ارتفاعی، نیاز به اتصال اینترنت است");
            return;
        }
        isInBackgroundLoading = true;
        backgroundMessage = "";

//        showHideEverythingForLoading(true);
        progressBarPage_SetVisibility(View.VISIBLE);
        txtSearchResult_SetText("");

        SearchRequestDTO requestDTO = new SearchRequestDTO();
        requestDTO.Filter = IdToSearch + "";
        requestDTO.OtherCommand = 500 + "";

        Call<ResponseBody> call = app.apiMap.ReadOneSafeGpxDataPoints(SimpleRequest.getInstance(requestDTO));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!isAdded()) return;
                boolean readCompleted = false;
                try {
                    if (response.isSuccessful()) {
                        TrackDataCompactRes result = TrackDataCompactRes.fromBytes(response.body().bytes());
                        if (!result.isOk) {
                            backgroundMessage = result.message != null ? result.message : "";
                        } else {
                            backgroundMessage = "";
                        }
                        if (result.obj.Points.size() > 0) {
                            trkData = result.obj;
                            readCompleted = true;
                        } else if (backgroundMessage.length() == 0) {
                            backgroundMessage = getResources().getString(R.string.NothingToShow);
                        }
                        txtSearchResult_SetText(backgroundMessage);

                        if (readCompleted && initCompleted) {
                            setDataForElevationChart(chart, trkData, context);
                            //Log.e("DDDDD", String.valueOf(readCompleted && initCompleted) + " ---Size:  "+ result.resList.size());
                            //showHideEverythingForLoading(false);
                        }
                    } else {
                        projectStatics.ManageCallResponseErrors(true, PrjConfig.frmSafeGpxView, 100, context, response.code());
                        backgroundMessage = getResources().getString(R.string.NothingToShow);
                        txtSearchResult_SetText(backgroundMessage);
                    }
                } catch (Exception ex) {
                    Log.e(Tag, "Exception22 On First Read: " + ex.getMessage());
                    ex.printStackTrace();
                }

                progressBarPage_SetVisibility(View.GONE);
                if (readCompleted)
                    showHideEverythingForLoading(false);
                isInBackgroundLoading = false;

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                projectStatics.ManageCallExceptions(true, PrjConfig.frmSafeGpxView, 100, t, context);
                if (!isAdded()) return;
                Log.e(Tag, "Exception On First Read: " + t.getMessage());
                t.printStackTrace();
                progressBarPage_SetVisibility(View.GONE);
                backgroundMessage = getResources().getString(R.string.NothingToShow);
                txtSearchResult_SetText(backgroundMessage);
                isInBackgroundLoading = false;
            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {//4th Event
        super.onViewCreated(view, savedInstanceState);

        initializeComponents(view);
        if (currentObj != null) {
            showHideEverythingForLoading(false);
            fillForm();
            readTrackDataInBackground(currentObj.NbSafeGPXId);
        } else {
            //در تابع خواندن اطلاعات لود انجام میشه
            readItemInBackground(recId);
            readTrackDataInBackground(recId);

        }
    }

    private LineChart chart;
    private SeekBar seekBarX, seekBarY;


    private void fillForm() {
        try {
            dbConstantsTara.DoSyncV(currentObj.NbSafeGPXId, dbConstantsTara.ViewCounterType_SafeGpx, Calendar.getInstance(), context);

            //txtPageTitle.setText(currentObj.Name);

            lblName.setText(currentObj.Name);
            lblDistance.setText(GeoCalcs.distanceBetweenFriendlyInKm(currentObj.TrackLength));
            lblElevGain.setText(TextFormat.GetStringFromDecimalPrice(Double.parseDouble(Float.toString(currentObj.ElevGain))) + "m");
            lblElevLoss.setText(TextFormat.GetStringFromDecimalPrice(Double.parseDouble(Float.toString(currentObj.ElevLoss))) + "m");
            imgIcon.setImageResource(SafeGpxSearch.ActivityTypeToImageResource(currentObj.ActivityType));

            Log.d(Tag, "currentObj.ScreenshotAddress: " + currentObj.ScreenshotAddress);

            lblDesc.setText(currentObj.Desc);
            lblImportantNotes.setText(currentObj.ImportantNotes);
            lblHardnessLevel.setText(SafeGpxSearch.getHardnessLevelName(currentObj.HardnessLevel));
            lblValidity.setText(currentObj.Validity + " از 10");
            lblValidityDesc.setText(currentObj.ValidityDesc);
            linValidity.setBackgroundColor(getValidityColor(currentObj.Validity));
            lblActivityTypeName.setText(SafeGpxSearch.getActivityTypeNames(currentObj.ActivityType));
            if (currentObj.Price != 0)
                lblPrice.setText(TextFormat.GetStringFromDecimalPrice(currentObj.Price) + " " + app.CurrencyName);
            else
                lblPrice.setText(getString(R.string.free));
            if (currentObj.Desc.length() == 0) {
                linDesc.setVisibility(View.GONE);
            }
            if (currentObj.ImportantNotes.length() == 0) {
                linImportantNotes.setVisibility(View.GONE);
            }

            SetVisibilityOfBottoms();

            PicassoTrustAll.getInstance(context)
                    .load(currentObj.ScreenshotAddress)
                    .placeholder(R.drawable.progress_animation)
                    .into(imgScreen);


        } catch (Exception ex) {
            Log.d(Tag, "بازکردن" + "fillForm_on_safeGpxView: " + ex.getMessage() + ex.getStackTrace());
            //Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            ex.printStackTrace();
            TTExceptionLogSQLite.insert(ex.getMessage(), stktrc2k(ex), PrjConfig.frmSafeGpxView, 150);
        }
    }

    void SetVisibilityOfBottoms() {
        if (currentObj.BuyStatus == NbMap.Enums.BuyStatusTypes_Done) {
            btnBuyAndDownload.setVisibility(View.GONE);
            btnDownload.setVisibility(View.VISIBLE);
        } else {
            btnBuyAndDownload.setVisibility(View.VISIBLE);
            btnDownload.setVisibility(View.GONE);
        }
    }

    void showHideEverythingForLoading(boolean hide) {
        //اگه صفحه هنوز لود نشده بود که بی خیال کلا
        if (linHeader == null)
            return;
        if (hide) {
            txtPageTitle.setVisibility(View.INVISIBLE);
            linHeader.setVisibility(View.GONE);
            //linBody.setVisibility(View.GONE);
            linLoadingContainer.setVisibility(View.VISIBLE);
        } else {
            txtPageTitle.setVisibility(View.VISIBLE);
            //btnBell.setVisibility(View.VISIBLE); این از جای دیگه تنظیم میشه
            linHeader.setVisibility(View.VISIBLE);
            //linBody.setVisibility(View.VISIBLE);

            linLoadingContainer.setVisibility(View.GONE);
        }

    }

    TextView lblName, lblDistance, lblActivityTypeName, lblHardnessLevel, lblValidity, lblValidityDesc, lblDesc, lblImportantNotes, lblPrice, lblElevGain, lblElevLoss;
    LinearLayout btnDownload, btnBuyAndDownload;
    LinearLayout linValidity, linDesc, linImportantNotes;
    ImageView imgIcon, imgScreen;
    ProgressBar progressBarIndet, progressBarDet;

    TextView txtPageTitle, btnBack;
    LinearLayout linHeader, linLoadingContainer;

    //Loading part:
    ProgressBar progressBarPage;
    TextView txtSearchResult;

    private TextView tvX, tvY;


    @Override
    public void initializeComponents(View v) {

        btnBack = v.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(view -> {
            ((MainActivityManager) context).onBackPressed();
        });
        txtPageTitle = v.findViewById(R.id.txtPageTitle);

        imgIcon = v.findViewById(R.id.imgIcon);
        lblName = v.findViewById(R.id.lblName);
        imgScreen = v.findViewById(R.id.imgScreen);
        lblDistance = v.findViewById(R.id.lblDistance);
        lblElevLoss = v.findViewById(R.id.lblElevLoss);
        lblElevGain = v.findViewById(R.id.lblElevGain);
        lblActivityTypeName = v.findViewById(R.id.lblActivityTypeName);
        lblHardnessLevel = v.findViewById(R.id.lblHardnessLevel);
        lblValidity = v.findViewById(R.id.lblValidity);
        lblValidityDesc = v.findViewById(R.id.lblValidityDesc);
        linValidity = v.findViewById(R.id.linValidity);
        lblDesc = v.findViewById(R.id.lblDesc);
        linDesc = v.findViewById(R.id.linDesc);
        linImportantNotes = v.findViewById(R.id.linImportantNotes);
        lblImportantNotes = v.findViewById(R.id.lblImportantNotes);
        lblPrice = v.findViewById(R.id.lblPrice);

        btnDownload = v.findViewById(R.id.btnDownload);
        btnDownload.setVisibility(View.GONE);
        btnDownload.setOnClickListener(view -> {
            btnOrder_Click(currentObj);
        });
        btnBuyAndDownload = v.findViewById(R.id.btnBuyAndDownload);
        btnBuyAndDownload.setOnClickListener(view -> {
            btnOrder_Click(currentObj);
        });

        linHeader = v.findViewById(R.id.linHeader);
        //linBody = v.findViewById(R.id.linBody);
        linLoadingContainer = v.findViewById(R.id.linLoadingContainer);

        //Loading part:
        progressBarPage = v.findViewById(R.id.progressBarPage);
        txtSearchResult = v.findViewById(R.id.txtSearchResult);

        progressBarIndet = v.findViewById(R.id.progressBarIndet);
        progressBarDet = v.findViewById(R.id.progressBarDet);

        Drawable progressDrawable = progressBarIndet.getIndeterminateDrawable().mutate(); //or getProgressDrawable for normal //https://stackoverflow.com/questions/2020882/how-to-change-progress-bars-progress-color-in-android
        progressDrawable.setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
        progressBarIndet.setProgressDrawable(progressDrawable);
        Drawable progressDrawable2 = progressBarDet.getProgressDrawable().mutate(); //or getProgressDrawable for normal //https://stackoverflow.com/questions/2020882/how-to-change-progress-bars-progress-color-in-android
        progressDrawable2.setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
        progressBarDet.setProgressDrawable(progressDrawable2);


        tvX = v.findViewById(R.id.tvXMax);
        tvY = v.findViewById(R.id.tvYMax);

        seekBarX = v.findViewById(R.id.seekBar1);
//        seekBarX.setOnSeekBarChangeListener(this);

        seekBarY = v.findViewById(R.id.seekBar2);
        seekBarY.setMax(180);
//        seekBarY.setOnSeekBarChangeListener();

        chart = v.findViewById(R.id.chart1);
        chart = setupLineChartForElevation(chart);

        seekBarX.setProgress(45);
        seekBarY.setProgress(180);
        //setData();//45, 180
        super.initializeComponents(v);
    }

    public static LineChart setupLineChartForElevation(LineChart chart){
        {
            chart.setNoDataText("در حال پردازش اطلاعات ارتفاعی");

            // // Chart Style // //
            // background color
            chart.setBackgroundColor(Color.WHITE);

            // disable description text
            chart.getDescription().setEnabled(false);

            // enable touch gestures
            chart.setTouchEnabled(true);

            // set listeners
//                chart.setOnChartValueSelectedListener(this);
            chart.setDrawGridBackground(false);

            // create marker to display box when values are selected
//                MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
//
//                // Set the marker to the chart
//                mv.setChartView(chart);
//                chart.setMarker(mv);

            // enable scaling and dragging
            chart.setDragEnabled(true);
            chart.setScaleEnabled(true);
            // chart.setScaleXEnabled(true);
            // chart.setScaleYEnabled(true);

            // force pinch zoom along both axis
            chart.setPinchZoom(true);
        }

        XAxis xAxis;
        {   // // X-Axis Style // //
            xAxis = chart.getXAxis();

            // vertical grid lines
            xAxis.enableGridDashedLine(10f, 10f, 0f);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//نمایش محور ایکس در پایین
            xAxis.setTypeface(projectStatics.IranSans_FONT);
        }

        YAxis yAxis;
        {   // // Y-Axis Style // //
            yAxis = chart.getAxisLeft();
            // disable dual axis (only use LEFT axis)
            chart.getAxisRight().setEnabled(false);
            // horizontal grid lines
            yAxis.enableGridDashedLine(10f, 10f, 0f);
            yAxis.setTypeface(projectStatics.IranSans_FONT);
            // axis range
            yAxis.setAxisMaximum(6000f);
            yAxis.setAxisMinimum(0f);
        }

        // draw points over time
        chart.animateX(500);

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();
        // draw legend entries as lines
        l.setDirection(Legend.LegendDirection.RIGHT_TO_LEFT);
        l.setForm(Legend.LegendForm.NONE);//پایین به شکل یک خط نشون داده بشه یا کلا نشه
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setTypeface(projectStatics.IranSans_FONT);

        return chart;
    }

    public static void setDataForElevationChart(LineChart chart, TrackDataCompact trkData, Context context) {//int count, float range

        ArrayList<Entry> values = new ArrayList<>();

        trkData.calcSmoothedElevation(true);
        float pElev = trkData.ElevSmoothed.get(0);
        LatLng pLoc = trkData.Points.get(0);

        float totalDistance = 0;
        float minElev = pElev;
        float maxElev = pElev;

        values.add(new Entry(totalDistance, pElev));

        for (int i = 1; i < trkData.Points.size(); i++) {
            float elev = trkData.ElevSmoothed.get(i);
            LatLng loc = trkData.Points.get(i);
            if (loc.latitude == 0 && loc.longitude == 0){
                continue;
            }
            double cDistance = GeoCalcs.distanceBetweenMeteres(loc.latitude, loc.longitude, pLoc.latitude, pLoc.longitude);
            totalDistance += cDistance;
            if (elev > maxElev)
                maxElev = elev;
            if (elev < minElev)
                minElev = elev;

            pElev = elev;
            pLoc = loc;
            values.add(new Entry(totalDistance, pElev));
        }
        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMaximum(maxElev + (maxElev - minElev) / 10);
        yAxis.setAxisMinimum(minElev - (maxElev - minElev) / 10);

        LineDataSet set1;

        if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.notifyDataSetChanged();
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, "ارتفاع (متر) به مسافت (متر)");//میتونست لیبل داشته باشه، مثلا "DataSet 1"
            set1.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
            set1.setDrawIcons(false);
            set1.setDrawValues(false);
            //set1.enableDashedLine(10f, 5f, 0f);draw dashed line

            // black lines and points
            set1.setColor(Color.GRAY);
            set1.setCircleColor(Color.TRANSPARENT);

            // line thickness and point size
            set1.setLineWidth(1f);
            set1.setCircleRadius(0f);

            // draw points as solid circles
            set1.setDrawCircleHole(false);

            // customize legend entry
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);

            // text size of values
            set1.setValueTextSize(9f);

            // draw selection line as dashed
            set1.enableDashedHighlightLine(10f, 5f, 0f);

            // set the filled area
            set1.setDrawFilled(true);
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return chart.getAxisLeft().getAxisMinimum();
                }
            });

            // set color of filled area
            if (Utils.getSDKInt() >= 18) {
                // drawables only supported on api level 18 and above
                Drawable drawable = ContextCompat.getDrawable(context, R.drawable.chart_fade_red);
                set1.setFillDrawable(drawable);
            } else {
                set1.setFillColor(Color.BLACK);
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1); // add the data sets

            // create a data object with the data sets
            LineData data = new LineData(dataSets);

            // set data
            chart.setData(data);
        }

    }

    public static int getValidityColor(int value) {
        int res = 0;
        int index = -1;
        //E2F3D1, 5EBB00, 0
        int[] minColors = {14873553, 6208256};
        int[] maxColors = {6208256, 0};
        int[] minTemps = {1, 10};
        int[] maxTemps = {10, 10};
        final int counts = 2;
        for (int i = 0; i < counts && index == -1; i++) {
            if (value >= minTemps[i] && value < maxTemps[i])
                index = i;
        }
        if (index == -1 && value >= maxTemps[counts - 1]) {
            index = counts - 1;
            value = maxTemps[counts - 1];
        }
        if (index == -1 && value < minTemps[0]) {
            index = 0;
            value = minTemps[0];
        }
        int blueMin = Color.blue(minColors[index]);
        int blueMax = Color.blue(maxColors[index]);
        int redMin = Color.red(minColors[index]);
        int redMax = Color.red(maxColors[index]);
        int greenMin = Color.green(minColors[index]);
        int greenMax = Color.green(maxColors[index]);

        //prev max //cr min
        double valueDiff = (maxTemps[index] - minTemps[index]);
        double fractBetween = (valueDiff == 0) ? 0 : (value - minTemps[index]) / valueDiff;
        double red = (redMax - redMin) * fractBetween + redMin;
        double green = (greenMax - greenMin) * fractBetween + greenMin;
        double blue = (blueMax - blueMin) * fractBetween + blueMin;

        return Color.rgb((int) red, (int) green, (int) blue);
    }

    public void reOpenForDownloadAfterBuy() {
        btnBuyAndDownload.setVisibility(View.GONE);
        btnDownload.setVisibility(View.VISIBLE);
        currentObj.BuyStatus = NbMap.Enums.BuyStatusTypes_Done;
        Toast.makeText(context, getString(R.string.downloadStarted), Toast.LENGTH_LONG).show();
        btnOrder_Click(currentObj);
    }

    public AlertDialog dialog_getdiscount = null;
    boolean isDownloading = false;
    public Intent intent = null;

    public boolean btnOrder_Click(NbSafeGpx currentObj) {
        if (hutilities.CCustomerId == 0) {
            projectStatics.showDialog(context, getResources().getString(R.string.need_loginOrRegister)
                    , getResources().getString(R.string.need_loginOrRegister_Desc)
                    , getResources().getString(R.string.ok)
                    , view -> {
                        ((MainActivityManager) context).showFragment(new Register(""));
                    }, "", null);

            return false;
        }
        Log.d(Tag, Integer.toString(isDownloading ? 1 : 0));
        if (isDownloading) {
            projectStatics.showDialog(context, getResources().getString(R.string.isProcessing_Title)
                    , getResources().getString(R.string.isProcessing_Desc)
                    , getResources().getString(R.string.ok)
                    , null, "", null);
            return false;
        }
        isDownloading = true;
        ;

        if (currentObj.BuyStatus == NbMap.Enums.BuyStatusTypes_Done) {
            //NbLogSearchSQLite.insert(NbLogSearch.getInstance(NbLogSearch.CommandType_GPXDownload, "MAP_ID:" +currentObj.NbSafeGPXId, 2, 0, 0));
            DoDownload(currentObj);
        } else if (currentObj.BuyStatus == NbMap.Enums.BuyStatusTypes_None) {
            AlertDialog.Builder alertDialogBuilder = DialogGetDisCopon.GetBuilder(context, view -> {
                DoBuyRequest(currentObj, DialogGetDisCopon.GetDiscountCode(dialog_getdiscount), progressBarIndet);
                dialog_getdiscount.dismiss();
            }, null);

            dialog_getdiscount = alertDialogBuilder.create();
            dialog_getdiscount.show();
            projectStatics.SetDialogButtonStylesAndBack(dialog_getdiscount, this.context, projectStatics.getIranSans_FONT(context), 18);
        }

        return true;
    }

    private boolean DoBuyRequest(NbSafeGpx currentObj, String DiscountCode, ProgressBar progressBar) {
        if (!hutilities.isInternetConnected(context)) {
            projectStatics.showDialog(context, this.getResources().getString(com.pakoob.tara.R.string.NoInternet), this.getResources().getString(com.pakoob.tara.R.string.NoInternet_Desc), this.getResources().getString(com.pakoob.tara.R.string.ok), view -> {
            }, "", null);
            return false;
        }
        BuyMapRequestDTO buyRequest = new BuyMapRequestDTO();
        buyRequest.DiscountCode = DiscountCode;
        buyRequest.NBMapId = currentObj.NbSafeGPXId;
        buyRequest.NbBuyType = BuyMapRequestDTO.NbBuyType_Gpx;
        progressBar.setVisibility(View.VISIBLE);

        SimpleRequest request = SimpleRequest.getInstance(buyRequest);
        Call<ResponseBody> call = app.apiMap.RequestBuyMap(request);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!isAdded()) return;
                final int STATUS_DOWNLOAD_NOW = 1;
                final int STATUS_NOTAVAILABLE = 2;
                final int STATUS_GOTO_PAYMENT = 3;
                final int STATUS_REQUESTED_BEFORE = 4;
                final int STATUS_ERROR = 5;
                try {
                    if (response.isSuccessful()) {
                        InsUpdRes res = InsUpdRes.fromBytes(response.body().bytes());
                        String titleMsg = "";
                        String okText = "تایید";

                        switch (res.status) {
                            case STATUS_NOTAVAILABLE:
                                titleMsg = "فایل ناموجود";
                                break;
                            case STATUS_REQUESTED_BEFORE:
                                titleMsg = "در حال پردازش";
                                break;
                            case STATUS_ERROR:
                                titleMsg = "خطا";
                                break;
                            case STATUS_DOWNLOAD_NOW:
                                titleMsg = "فایل آماده دانلود";
                                currentObj.BuyStatus = NbMap.Enums.BuyStatusTypes_Done;
                                SetVisibilityOfBottoms();
                                break;
                        }

                        if (res.status == STATUS_GOTO_PAYMENT) {
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                            if (prev != null) {
                                ft.remove(prev);
                            }
                            ft.addToBackStack(null);
                            DialogFragment dialogFragment = new MapSelect_Dialog_GotoBank();
                            ((MapSelect_Dialog_GotoBank) dialogFragment).link = res.resValue;
                            String discountMsg = "";
                            ((MapSelect_Dialog_GotoBank) dialogFragment).price = currentObj.Price;
                            if (res.command.length() > 0) {
                                String[] parts = res.command.split(";;");
                                if (parts.length > 0)
                                    ((MapSelect_Dialog_GotoBank) dialogFragment).price = Double.valueOf(parts[0]);
                                if (parts.length > 1 && parts[1].length() > 1)
                                    ((MapSelect_Dialog_GotoBank) dialogFragment).discountMsg = parts[1];
                            }
                            ((MapSelect_Dialog_GotoBank) dialogFragment).message = res.message;

                            dialogFragment.show(getFragmentManager(), "dialog");

                        } else {
                            projectStatics.showDialog(context, titleMsg, res.message, getResources().getString(R.string.ok), null, "", null);
                        }
                    } else {
                        projectStatics.showDialog(context, getResources().getString(R.string.dialog_ertebatBaServer_Title)
                                , getResources().getString(R.string.dialog_ertebatBaServer_Desc)
                                , getResources().getString(R.string.ok)
                                , null, "", null);
                        TTExceptionLogSQLite.insert("Server Connect: " + response.code(), response.message(), PrjConfig.frmSafeGpxView, 300);
                        Log.d(Tag, "ERROR RESPONSE : " + response.code() + " msg: " + response.message());
                    }

                    isDownloading = false;
                    progressBar.setVisibility(View.GONE);

                } catch (Exception ex) {
                    TTExceptionLogSQLite.insert("Exception", response.message(), PrjConfig.frmSafeGpxView, 200);
                    ex.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                    isDownloading = false;
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                TTExceptionLogSQLite.insert("Fail", t.getMessage(), PrjConfig.frmSafeGpxView, 100);
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                Log.e(Tag, "error" + " " + t.getMessage());
                t.printStackTrace();
                isDownloading = false;
            }
        });
        return true;
    }

    private void DoDownload(NbSafeGpx currentObj) {
        if (!hutilities.isInternetConnected(context)) {
            projectStatics.showDialog(context, this.getResources().getString(com.pakoob.tara.R.string.NoInternet), this.getResources().getString(com.pakoob.tara.R.string.NoInternet_Desc), this.getResources().getString(com.pakoob.tara.R.string.ok), view -> {
            }, "", null);
            return;
        }
        DownloadRequest data = DownloadRequest.getInstance(currentObj.NbSafeGPXId, NbMap.Enums.NbBuyType_GPX);
        progressBarIndet.setVisibility(View.VISIBLE);
        SimpleRequest request = SimpleRequest.getInstance(data);
        Call<ResponseBody> call = app.apiMap.Download(request);
        Toast.makeText(context, "در حال دانلود مسیر... لطفا تا انتهای دانلود صبر کنید", Toast.LENGTH_LONG).show();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!isAdded()) return;
                try {
                    progressBarIndet.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        InsUpdRes res = InsUpdRes.fromBytes(response.body().bytes());
                        String tempDownloadFolder = hMapTools.createAndRetTempDownloadFolder(context);
                        String downloadedFileName = currentObj.Name + ".tmp";
                        //progressBar.setVisibility(View.INVISIBLE);
                        if (!res.isOk) {
                            projectStatics.showDialog(context, "خطا در دانلود", res.message, getResources().getString(R.string.ok), null, "", null);
                            isDownloading = false;
                            ;
                            return;
                        }
                        doDownloadInBackground(res.command, tempDownloadFolder, downloadedFileName, currentObj, context);
                    } else {
                        TTExceptionLogSQLite.insert("Server Contact Failed", "Code:" + response.code(), PrjConfig.frmSafeGpxView, 60);
                        Log.d(Tag, "server contact failed");
                    }
                } catch (IOException e) {
                    TTExceptionLogSQLite.insert("IOException»" + e.getMessage(), stktrc2k(e), PrjConfig.frmSafeGpxView, 50);
                    e.printStackTrace();
                    isDownloading = false;
                    ;
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                TTExceptionLogSQLite.insert("Fail", t.getMessage(), PrjConfig.frmSafeGpxView, 12);
                if (!isAdded()) return;
                String errorText = t.getMessage();
                if (errorText.contains("Failed to connect to")) {
                    projectStatics.showDialog(context, "خطا در دانلود", "امکان ارتباط با سرور وجود ندارد. این اشکال ممکن است از قطع موقتی سرور یا از قطع اینترنت شما باشد. لطفا بعدا مجددا تلاش نمایید.", getResources().getString(R.string.ok), null, "", null);
                } else {
                    projectStatics.showDialog(context, "خطا در دانلود", "متاسفانه خطایی ناشناخته در هنگام دانلود به وجود آمده است. لطفا بعدا مجددا تلاش نمایید.", getResources().getString(R.string.ok), null, "", null);
                }
                progressBarIndet.setVisibility(View.GONE);
                isDownloading = false;
                ;
                Log.e(Tag, "error");
            }
        });
    }

    public void doDownloadInBackground2(String remoteAddr, String tempDownloadDirectoryName, String downloadedFileName, NbSafeGpx currentObj, Context context) {//, Button view, ProgressBar progressBarDet, ProgressBar progressBarIndet,, Button btnMore
        Handler handler = new Handler(Looper.getMainLooper());
        progressBarDet.setProgress(0);
        progressBarDet.setVisibility(View.VISIBLE);
        Thread thread = new Thread(() -> {
            int step = 0;
            try {
                //                        //connecting to url
//                        String url = "http://pakoob24.ir/ui/temp/aa.jpg";
                URL url = new URL(remoteAddr);

                //1401-03-23 : https://stackoverflow.com/questions/59093629/add-a-self-signed-ssl-certificate-to-httpurlconnection
                //رفع خطای دانلود ناموفق
                if ((android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)) {
                    TrustManager[] trustAllCerts = new TrustManager[]{
                            new X509ExtendedTrustManager() {
                                public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) {
                                }

                                public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) {
                                }

                                public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) {
                                }

                                public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) {
                                }

                                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                                }

                                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                                }

                                public X509Certificate[] getAcceptedIssuers() {
                                    return new X509Certificate[0];
                                }
                            }
                    };

                    SSLContext sslContext = SSLContext.getInstance("TLS");
                    sslContext.init(null, trustAllCerts, null);

                    HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
                } else {
                    TrustManager[] trustAllCerts = new TrustManager[]{
                            new X509TrustManager() {
                                public X509Certificate[] getAcceptedIssuers() {
                                    return new X509Certificate[0];
                                }

                                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                                }

                                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                                }
                            }
                    };

                    // Install the all-trusting trust manager
                    SSLContext sc = SSLContext.getInstance("TLS");
                    sc.init(null, trustAllCerts, new java.security.SecureRandom());
                    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                }


                HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
                int responseCode = httpConn.getResponseCode();
                // Check if the connection is successful
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    int lenghtOfFile = httpConn.getContentLength();//c.getContentLength();
                    step = 30;
                    if (lenghtOfFile <= 0) {
                        handler.post(new Runnable() {
                            public void run() {
                                projectStatics.showDialog(context, "خطا در دانلود", "فایل درخواستی قابل دانلود نمی باشد.", getResources().getString(R.string.ok), null, "", null);
                                progressBarDet.setVisibility(View.GONE);
                                isDownloading = false;
                            }
                        });
                        return;
                    }
                    step = 40;

                    int BUFFER_SIZE = 8192;
                    //this is where the file will be seen after the download
                    FileOutputStream output = new FileOutputStream(new File(tempDownloadDirectoryName, downloadedFileName));
                    step = 50;
                    //file input is from the url
                    InputStream in = httpConn.getInputStream();//new BufferedInputStream(url.openStream());//c.getInputStream();
                    step = 60;
                    BufferedInputStream bis = new BufferedInputStream(in, BUFFER_SIZE);
                    step = 70;

                    //here’s the download code
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int len1 = 0;
                    long total = 0;

                    while ((len1 = bis.read(buffer, 0, BUFFER_SIZE)) > 0) {
                        total += len1; //total = total + len1
                        long finalTotal = total;
                        handler.post(new Runnable() {
                            public void run() {
                                progressBarDet.setProgress((int) ((finalTotal * 100) / lenghtOfFile));
                            }
                        });
                        output.write(buffer, 0, len1);
                    }
                    step = 80;
                    in.close();
                    output.flush();
                    output.close();
                    step = 90;

                    File tempDownloadFile = new File(tempDownloadDirectoryName, downloadedFileName);
                    if (tempDownloadFile.length() != lenghtOfFile) {
                        handler.post(() -> {
                            projectStatics.showDialog(context, "خطا در دانلود", "فایل درخواستی به شکل نامناسبی دانلود شد. لطفا دوباره تلاش کنید.", getResources().getString(R.string.ok), null, "", null);
                            progressBarDet.setVisibility(View.GONE);
                            isDownloading = false;
                        });
                        return;
                    }
                    step = 100;

                    step = 110;

                    handler.post(() -> progressBarDet.setVisibility(View.GONE));

                    handler.post(() -> Toast.makeText(context, "دانلود کامل شد ... در حال استخراج مسیر", Toast.LENGTH_LONG).show());
                    handler.post(() -> {
                        //  آغاز عملیات دیکریپت کردن
                        DecryptAndGenerateMapAndDbUpdate(tempDownloadDirectoryName, downloadedFileName, currentObj);
                    });
                } else {
                    String msgToShow = "اتصال با سرور دچار مشکل است و در حال حاظر این با کد خطای " + httpConn.getResponseCode() + " مواجه هستیم";
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            projectStatics.showDialog(context, "خطا در دانلود", msgToShow, getResources().getString(R.string.ok), null, "", null);
                            progressBarDet.setVisibility(View.GONE);
                        }
                    });
                }


            } catch (Exception e) {

                Log.e(Tag, "اکسپشن" + step + " _ " + remoteAddr + "\n" + e.getMessage());
                e.printStackTrace();
                String msg = "یک خطای پیش بینی نشده در هنگام دانلود رخ داده است. لطفا دوباره تلاش کنید.";
                if (e.getMessage().toLowerCase().contains("time"))
                    msg = "مدت زمان زیادی برای دانلود سپری شد. احتمالا اینترنت شما ضعیف است یا مشکلی در سرور وجود دارد.";
                String msgToShow = msg;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        projectStatics.showDialog(context, "خطا در دانلود", msgToShow, getResources().getString(R.string.ok), null, "", null);
                        progressBarDet.setVisibility(View.GONE);
                    }
                });
                TTExceptionLogSQLite.insert(e.getMessage(), step + "-" + remoteAddr, PrjConfig.frmSafeGpxView, 400);
            }
            isDownloading = false;
        });
        thread.start();
    }

    public void doDownloadInBackground(String remoteAddr, String tempDownloadDirectoryName, String downloadedFileName, NbSafeGpx currentObj, Context context) {//, Button view, ProgressBar progressBarDet, ProgressBar progressBarIndet,, Button btnMore
        Handler handler = new Handler(Looper.getMainLooper());
        progressBarDet.setProgress(0);
        progressBarDet.setVisibility(View.VISIBLE);
        Thread thread = new Thread(() -> {
            int step = 0;
            try {
                //                        //connecting to url
//                        String url = "http://pakoob24.ir/ui/temp/aa.jpg";
                URL url = new URL(remoteAddr);

                //1401-03-23 : https://stackoverflow.com/questions/59093629/add-a-self-signed-ssl-certificate-to-httpurlconnection
                //رفع خطای دانلود ناموفق
                if ((android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)) {
                    TrustManager[] trustAllCerts = new TrustManager[]{
                            new X509ExtendedTrustManager() {
                                public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) {
                                }

                                public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) {
                                }

                                public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) {
                                }

                                public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) {
                                }

                                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                                }

                                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                                }

                                public X509Certificate[] getAcceptedIssuers() {
                                    return new X509Certificate[0];
                                }
                            }
                    };

                    SSLContext sslContext = SSLContext.getInstance("TLS");
                    sslContext.init(null, trustAllCerts, null);

                    HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
                }

                URLConnection connection = url.openConnection();
                step = 10;
                connection.setConnectTimeout(30000);
                connection.connect();
                step = 20;

                // download the file

//                        URL u = new URL(url);
//                        HttpURLConnection c = (HttpURLConnection) u.openConnection();
//                        c.setRequestMethod("GET");
//                        c.setDoOutput(true);
//                        c.connect();

                int lenghtOfFile = connection.getContentLength();//c.getContentLength();
                step = 30;
                if (lenghtOfFile <= 0) {
                    handler.post(new Runnable() {
                        public void run() {
                            projectStatics.showDialog(context, "خطا در دانلود", "فایل درخواستی قابل دانلود نمی باشد.", getResources().getString(R.string.ok), null, "", null);
                            progressBarDet.setVisibility(View.GONE);
                            isDownloading = false;
                        }
                    });
                    return;
                }
                step = 40;

                int BUFFER_SIZE = 8192;
                //this is where the file will be seen after the download
                FileOutputStream output = new FileOutputStream(new File(tempDownloadDirectoryName, downloadedFileName));
                step = 50;
                //file input is from the url
                InputStream in = connection.getInputStream();//new BufferedInputStream(url.openStream());//c.getInputStream();
                step = 60;
                BufferedInputStream bis = new BufferedInputStream(in, BUFFER_SIZE);
                step = 70;

                //here’s the download code
                byte[] buffer = new byte[BUFFER_SIZE];
                int len1 = 0;
                long total = 0;

                while ((len1 = bis.read(buffer, 0, BUFFER_SIZE)) > 0) {
                    total += len1; //total = total + len1
                    long finalTotal = total;
                    handler.post(new Runnable() {
                        public void run() {
                            progressBarDet.setProgress((int) ((finalTotal * 100) / lenghtOfFile));
                        }
                    });
                    output.write(buffer, 0, len1);
                }
                step = 80;
                in.close();
                output.flush();
                output.close();
                step = 90;

                File tempDownloadFile = new File(tempDownloadDirectoryName, downloadedFileName);
                if (tempDownloadFile.length() != lenghtOfFile) {
                    handler.post(() -> {
                        projectStatics.showDialog(context, "خطا در دانلود", "فایل درخواستی به شکل نامناسبی دانلود شد. لطفا دوباره تلاش کنید.", getResources().getString(R.string.ok), null, "", null);
                        progressBarDet.setVisibility(View.GONE);
                        isDownloading = false;
                    });
                    return;
                }
                step = 100;

                step = 110;

                handler.post(() -> progressBarDet.setVisibility(View.GONE));

                handler.post(() -> Toast.makeText(context, "دانلود کامل شد ... در حال استخراج مسیر", Toast.LENGTH_LONG).show());
                handler.post(() -> {
                    //  آغاز عملیات دیکریپت کردن
                    DecryptAndGenerateMapAndDbUpdate(tempDownloadDirectoryName, downloadedFileName, currentObj);
                });
            } catch (Exception e) {

                Log.e(Tag, "اکسپشن" + step + " _ " + remoteAddr + "\n" + e.getMessage());
                e.printStackTrace();
                String msg = "یک خطای پیش بینی نشده در هنگام دانلود رخ داده است. لطفا دوباره تلاش کنید.";
                if (e.getMessage().toLowerCase().contains("time"))
                    msg = "مدت زمان زیادی برای دانلود سپری شد. احتمالا اینترنت شما ضعیف است یا مشکلی در سرور وجود دارد.";
                String msgToShow = msg;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        projectStatics.showDialog(context, "خطا در دانلود", msgToShow, getResources().getString(R.string.ok), null, "", null);
                        progressBarDet.setVisibility(View.GONE);
                    }
                });
                TTExceptionLogSQLite.insert(e.getMessage(), step + "-" + remoteAddr, PrjConfig.frmSafeGpxView, 400);
            }
            isDownloading = false;
        });
        thread.start();
    }

    void DecryptAndGenerateMapAndDbUpdate(String mapsDirectoryName, String downloadedFileName, NbSafeGpx currentObj) {
        Handler handler = new Handler(Looper.getMainLooper());
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    isDownloading = true;
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(context, "در حال استخراج مسیر...", Toast.LENGTH_LONG).show();
                        }
                    });

                    handler.post(new Runnable() {
                        public void run() {
                            progressBarIndet.setVisibility(View.VISIBLE);
                        }
                    });
                    Random rand = new Random();

                    String finalFileName = currentObj.Name + "-" + rand.nextInt() + ".gpx";
                    String output = hMapTools.decryptToTemp(mapsDirectoryName + downloadedFileName, finalFileName, context, hutilities.CCustomerId);
                    int currentLevel = 0;
                    if (currentObj.ContentType1 == NbSafeGpx.SafeGpxContentTypes_Gpx) {
                        GPXFile importRes = GPXFile.ImportGpxFileIntoMapbaz(output, currentObj.Name, context, 0, (byte) currentLevel, true, handler);

                        MapSelect.clearTemp(context);

                        handler.post(() -> {
                            if (importRes != null) {
                                projectStatics.showDialog(context, context.getResources().getString(R.string.dialog_FileImported_title)
                                        , context.getResources().getString(R.string.dialog_fileImported)
                                        , context.getResources().getString(R.string.ok)
                                        , view -> {
                                            ((MainActivityManager) context).showFragment(MyTracks.getInstance("menu", "", "", null));
                                        }, "", null);
                            } else {
                                //Extract Failed
                                projectStatics.showDialog(context, context.getResources().getString(R.string.dialog_File_NOT_Imported_title)
                                        , context.getResources().getString(R.string.dialog_file_NOT_Imported)
                                        , context.getResources().getString(R.string.ok)
                                        , null, "", null);
                            }
                        });
                    } else {
                        projectStatics.showDialog(context, context.getResources().getString(R.string.dialog_FileNotSupported_Title)
                                , context.getResources().getString(R.string.dialog_FileNotSupported_Desc)
                                , context.getResources().getString(R.string.ok)
                                , null, "", null);
                    }

                    handler.post(() -> progressBarIndet.setVisibility(View.GONE));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    TTExceptionLogSQLite.insert("Decrypt", ex.getMessage(), PrjConfig.frmSafeGpxView, 253);
                }
                isDownloading = false;
                ;
            }
        });

        thread.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {//3nd Event
        return inflater.inflate(R.layout.frm_safegpx_view, parent, false);
    }
}
