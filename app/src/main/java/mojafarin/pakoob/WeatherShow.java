package mojafarin.pakoob;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.eloyzone.jalalicalendar.JalaliDate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import bo.NewClasses.SimpleRequest;
import bo.NewClasses.StringContentDTO;
import bo.entity.NbPoi;
import bo.entity.NbWeather;
import bo.entity.NbWthrDaily;
import bo.entity.NbWthrHourly;
import bo.entity.NbWthrLoc;
import bo.entity.NbWthrNow;
import bo.sqlite.TTExceptionLogSQLite;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import UI.HFragment;
import utils.MainActivityManager;
import utils.MyDate;
import utils.PrjConfig;
import utils.hutilities;
import utils.projectStatics;

public class WeatherShow extends HFragment {
    public NbPoi recievedPoi;
    public NbWeather currentObj;
    int FromPageId;
    int recievedNbPoiId = 0;
    boolean isInBackgroundLoading = false;
    String backgroundMessage = "";
    public static WeatherShow getInstance(NbPoi poi, int FromPageId){
        WeatherShow res = new WeatherShow();
        res.recievedPoi = poi;
        res.FromPageId = FromPageId;
        return res;
    }
    public static WeatherShow getInstance(Integer nbPoiId, int FromPageId){
        WeatherShow res = new WeatherShow();
        res.recievedNbPoiId = nbPoiId;
        res.FromPageId = FromPageId;
        return res;
    }

    void progressBarPage_SetVisibility(int Visibility){
        if (progressBarPage == null)
            return;
        progressBarPage.setVisibility(Visibility);
    }
    void txtSearchResult_SetText(String st){
        if (txtSearchResult == null)
            return;
        if (st != null && st.length() > 0){
            txtSearchResult.setVisibility(View.VISIBLE);
        }
        else{
            txtSearchResult.setVisibility(View.GONE);
        }
        txtSearchResult.setText(st);
    }
    public static final String WeatherCommand_Show = "Show";
    public static final String WeatherCommand_LimitExceeeded = "LimitExceeeded";
    public static final String WeatherCommand_RemoteNotRespond = "RemoteNotRespond";
    public static final String WeatherCommand_NotFound = "NotFound";
    public static final String WeatherCommand_InternalError = "InternalError";

    void readItemInBackground(){
        if (!hutilities.isInternetConnected(context)) {
            projectStatics.showDialog(context
                    , this.getResources().getString(com.pakoob.tara.R.string.NoInternet)
                    , this.getResources().getString(com.pakoob.tara.R.string.NoInternet_Desc)
                    , this.getResources().getString(com.pakoob.tara.R.string.ok), view -> {
                    }, "", null);
            return;
        }
        isInBackgroundLoading = true;
        backgroundMessage = "";

        showHideEverythingForLoading(true);
        progressBarPage_SetVisibility(View.VISIBLE);
        txtSearchResult_SetText("");

        String strContent = "";
        if (recievedNbPoiId > 0){
            strContent = Long.toString(recievedNbPoiId);
        } else if(recievedPoi.ServerId > 0){
            strContent = Long.toString(recievedPoi.ServerId);
        } else{
            strContent = recievedPoi.LatS + "," + recievedPoi.LonW;
        }
        StringContentDTO contentDTO = StringContentDTO.getInstance(strContent);
        SimpleRequest request = SimpleRequest.getInstance(contentDTO);
        Call<ResponseBody> call = app.apiMap.RequestWeatherForLoc(request);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!isAdded()) return;
                boolean readCompleted = false;
                try {
                    if (response.isSuccessful()) {
                        NbWeather result = NbWeather.fromBytes(response.body().bytes());
                        backgroundMessage = !result.message.isEmpty()?result.message:"";
                        if (!result.isOk) {
                        } else {
                        }
                        if (result.isOk){
                            currentObj = result;
                            readCompleted = true;
                            Log.e(tag(), String.format("Daily and Hourly Size: %d, %d", result.Daily.size(), result.Hourly.size()));
                        } else if (backgroundMessage.length() == 0){
                            backgroundMessage = getResources().getString(R.string.NothingToShow);
                        }
                        Log.e(tag(), String.format("Result.Command=%s and initCompleted=%s", result.command != null?result.command:"NULL", initCompleted));

                        if (result.command.equals(WeatherCommand_Show)){
                            linAllContent.setVisibility(View.VISIBLE);
                        }
                        else{
                            linAllContent.setVisibility(View.GONE);
                        }

                        Log.e("ششش", "backgroundMessage:" + backgroundMessage + "-------" + result.message + "----- " + result.command);
                        txtSearchResult_SetText(backgroundMessage);

                        //Log.e("ADDDDD", String.valueOf(readCompleted && initCompleted));
                        if (readCompleted && initCompleted) {
                            fillForm();
                            showHideEverythingForLoading(false);
                        }
                    } else {
                        projectStatics.ManageCallResponseErrors(true, PrjConfig.frmWeatherShow,  100, context, response.code());
                        backgroundMessage = getResources().getString(R.string.NothingToShow);
                        txtSearchResult_SetText(backgroundMessage);
                    }

                    progressBarPage_SetVisibility(View.INVISIBLE);
                    isInBackgroundLoading = false;

                } catch (Exception ex){
                    manageException(getResources().getString(R.string.NothingToShow), 103, ex);
//                    projectStatics.ManageCallExceptions(true, PrjConfig.frmWeatherShow,  103, ex, context);
//                    if (!isAdded()) return;
//                    progressBarPage_SetVisibility(View.INVISIBLE);
//                    backgroundMessage = getResources().getString(R.string.NothingToShow);
//                    txtSearchResult_SetText(backgroundMessage);
//                    isInBackgroundLoading = false;
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                manageException(getResources().getString(R.string.NothingToShow), 100, t);
                //projectStatics.ManageCallExceptions(true, PrjConfig.frmWeatherShow,  100, t, context);
//                if (!isAdded()) return;
//                progressBarPage_SetVisibility(View.INVISIBLE);
//                backgroundMessage = getResources().getString(R.string.NothingToShow);
//                txtSearchResult_SetText(backgroundMessage);
//                isInBackgroundLoading = false;
            }
        });
    }
    void manageException(String msgToShow, int idOfPart, Throwable ex){
        projectStatics.ManageCallExceptions(true, PrjConfig.frmWeatherShow,  idOfPart, ex, context);
        if (!isAdded()) return;
        if (initCompleted)
            showHideEverythingForLoading(true);
        progressBarPage_SetVisibility(View.INVISIBLE);
        backgroundMessage = getResources().getString(R.string.NothingToShow);
        txtSearchResult_SetText(backgroundMessage);
        isInBackgroundLoading = false;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {//4th Event
        super.onViewCreated(view, savedInstanceState);
        readItemInBackground();
        initializeComponents(view);
//        if (1 == 1){
//            showHideEverythingForLoading(false);
//            WeatherDayItemView wdr = WeatherDayItemView.getInstance(context);
//            liDays.removeAllViews();
//            liDays.addView(wdr);
//            WeatherDayItemView wdr2 = WeatherDayItemView.getInstance(context);
//            liDays.addView(wdr2);
//            return;
//        }

        if (currentObj != null) {
            showHideEverythingForLoading(false);
            fillForm();
        }
        else{
            //در تابع خواندن اطلاعات لود انجام میشه
            showHideEverythingForLoading(true);
        }
    }
    private void fillForm() {
        try {
            NbWthrLoc loc = currentObj.Loc;
            NbWthrNow wdrNow = currentObj.Current;

            String name = currentObj.Loc.Name;
            if (name.isEmpty()){
                name = String.format(getString(R.string.Position) + " " + "%.4f,%.4f", currentObj.Loc.Lat, currentObj.Loc.Lon);
            }
            txtPageTitle.setText(getString(R.string.WeatherOf) + " " + name);
            btnBell.setVisibility(View.INVISIBLE);

            imgWeatherIcon.setBackgroundResource(weatherConditionToResId(wdrNow.weather_id, wdrNow.getDt(), 0));
            lblDate.setText(loc.getLastWeatherUpdateViewLong());
            lblCurrentWeatherTitle.setText(WeatherIdToName(wdrNow.weather_id, wdrNow.wind_speed));
            lblCurrentTemp.setText(Integer.toString((int) wdrNow.temp));
            lblFeelsLike.setText(Integer.toString((int) wdrNow.feels_like));
            lblWindSpeed.setText(Integer.toString((int) wdrNow.wind_speed)+ "kmh");
            lblCloudCover.setText(Integer.toString((int) wdrNow.clouds)+"%");
            lblHumidity.setText(Integer.toString((int) wdrNow.humidity)+"%");

            liDays.removeAllViews();

            int dailySize = currentObj.Daily.size();
            int hourSize=  currentObj.Hourly.size();
//            Log.d("ADDDDD", "dailySize : " + dailySize);
//            Log.d("ADDDDD",  "hourSize: " + hourSize);
            for (int i = 0; i < dailySize; i++) {
                NbWthrDaily tmpDailyObj = currentObj.Daily.get(i);
                List<NbWthrHourly> hourly = new ArrayList<>();
                int currentDayOfMonth = tmpDailyObj.getDt().get(Calendar.DAY_OF_MONTH);
                for (int j = 0; j < hourSize; j++) {
                    NbWthrHourly tmpHourlyObj =currentObj.Hourly.get(j);
                    if (tmpHourlyObj.getDt().get(Calendar.DAY_OF_MONTH) == currentDayOfMonth)
                        hourly.add(tmpHourlyObj);
                }
//                Log.d("ADDDDD", String.format("DAILYY-Start with hours: %d", hourly.size()));
                WeatherDayItemView item = WeatherDayItemView.getInstance(context, tmpDailyObj, hourly);
//                Log.d("ADDDDD", "DAILYY-End");
                liDays.addView(item);
                item.fill();
            }


        } catch (Exception ex) {
            TTExceptionLogSQLite.insert(ex.getMessage(), stktrc2k(ex), PrjConfig.frmWeatherShow, 150);
            Log.d("ADDDDD", "WeatherShow_FillForm: " + ex.getMessage() + ex.getStackTrace());
            //Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    void showHideEverythingForLoading(boolean hide){
        //اگه صفحه هنوز لود نشده بود که بی خیال کلا
        if (linBody == null)
            return;
        if (hide){
            linAllContent.setVisibility(View.GONE);
            txtPageTitle.setVisibility(View.INVISIBLE);
            btnBell.setVisibility(View.INVISIBLE);
            linHeader.setVisibility(View.GONE);
            linBody.setVisibility(View.GONE);

            linLoadingContainer.setVisibility(View.VISIBLE);
        }
        else{
            linAllContent.setVisibility(View.VISIBLE);
            txtPageTitle.setVisibility(View.VISIBLE);
            //btnBell.setVisibility(View.VISIBLE); این از جای دیگه تنظیم میشه
            linHeader.setVisibility(View.VISIBLE);
            linBody.setVisibility(View.VISIBLE);

            linLoadingContainer.setVisibility(View.GONE);
        }
    }

    TextView txtPageTitle, btnBack;
    TextView btnBell;
    LinearLayout linHeader, linBody, linLoadingContainer, liDays, linAllContent;
    ProgressBar progressBarTopOfClubViewHome;

    //Loading part:
    ProgressBar progressBarPage;
    TextView txtSearchResult;
    ImageView imgWeatherIcon;
    TextView lblDate, lblCurrentWeatherTitle, lblCurrentTemp, lblFeelsLike, lblWindSpeed, lblCloudCover, lblHumidity;

    @Override
    public void initializeComponents(View v) {

        btnBack =v.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(view -> {((MainActivityManager) context).onBackPressed();});
        txtPageTitle = v.findViewById(R.id.txtPageTitle);

        liDays = v.findViewById(R.id.liDays);
        linHeader = v.findViewById(R.id.linHeader);
        linBody = v.findViewById(R.id.linBody);
        linLoadingContainer = v.findViewById(R.id.linLoadingContainer);
        linAllContent = v.findViewById(R.id.linAllContent);

        //Loading part:
        progressBarPage = v.findViewById(R.id.progressBarPage);
        txtSearchResult = v.findViewById(R.id.txtSearchResult);

        progressBarTopOfClubViewHome= v.findViewById(R.id.progressBarTopOfClubViewHome);

        btnBell = v.findViewById(R.id.btnBell);

        imgWeatherIcon = v.findViewById(R.id.imgWeatherIcon);
        lblDate = v.findViewById(R.id.lblDate);
        lblCurrentWeatherTitle = v.findViewById(R.id.lblCurrentWeatherTitle);
        lblCurrentTemp = v.findViewById(R.id.lblCurrentTemp);
        lblFeelsLike = v.findViewById(R.id.lblFeelsLike);
        lblWindSpeed = v.findViewById(R.id.lblWindSpeed);
        lblCloudCover = v.findViewById(R.id.lblCloudCover);
        lblHumidity = v.findViewById(R.id.lblHumidity);

        super.initializeComponents(v);
    }


    public static class WeatherDayItemView extends LinearLayout{
        Context context;
        View view;
        TextView lblColDate;
        LinearLayout liInDayItems;
        NbWthrDaily daily;
        List<NbWthrHourly> hourly;
        List<Integer> durations;
        int DurationMinsStep = 0;
        public static WeatherDayItemView getInstance(Context context, NbWthrDaily daily, List<NbWthrHourly> hourly){
            WeatherDayItemView res = new WeatherDayItemView(context);

            res.daily = daily;
            res.hourly = hourly;
            res.DurationMinsStep = 0;

            int hourlySize = res.hourly.size();
            res.durations = new ArrayList<Integer>();
            //find all Available Durations
            for (int i = 0; i < hourlySize; i++) {
                NbWthrHourly hh = res.hourly.get(i);
                boolean find = false;
                for (int j = 0; j < res.durations.size() && !find; j++) {
                    if (hh.DurationMins == res.durations.get(j)){
                        find = true;
                        break;
                    }
                }
                if (!find)
                    res.durations.add(hh.DurationMins);
            }
//            Log.d("ADDDDD", String.format("Durations: %d", res.durations.size()));
//            Log.d("ADDDDD", Arrays.toString(res.durations.toArray()));

            Collections.sort(res.durations);
            Collections.reverse(res.durations);

            return res;
        }
        public WeatherDayItemView(Context context) {
            super(context);
            this.context = context;
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.frm_weather_show_day_item, this, true);
            this.setOnClickListener(view1 -> { DayClicked();});

            lblColDate = findViewById(R.id.lblColDate);
            liInDayItems = findViewById(R.id.liInDayItems);
        }

        public void DayClicked() {
            int hourlySize = this.hourly.size();
            int durationSize = durations.size();
            //Log.d("DDDDDDDDDDDD", String.format("Day CLICKED Hourly %d and durations %d", hourlySize, durationSize));
            if (hourlySize == 0 || durationSize == 0)
                return;
            boolean find = false;
            if (DurationMinsStep == 0){
                DurationMinsStep = durations.get(0);
            } else {
                for (int i = 0; i < durationSize && !find; i++) {
                    if (durations.get(i) == DurationMinsStep) {
                        find = true;
                        if (i + 1 < durationSize)
                            DurationMinsStep = durations.get(i + 1);
                        else{
                            DurationMinsStep = 0;
                        }
                    }
                }
                if (!find){
                    Log.e("ADDDDD", "Duration Not Found Array:" + Arrays.toString(durations.toArray()) + " and DurationMinsStep is " + DurationMinsStep);
                    DurationMinsStep = 0;
                }
            }

            fill();

        }

        public void fill(){
            Calendar dt =daily.getDt();
            JalaliDate jal = MyDate.getJalaliDate(dt);
            lblColDate.setText(MyDate.getDayOfWeek(jal) + "\n" + jal.getDay() + " " + jal.getMonthPersian().getStringInPersian());

            liInDayItems.removeAllViews();
            if (DurationMinsStep == 0){
                //Show Daily
                WeatherHourItemView hour1 = WeatherHourItemView.getInstance(context, daily, null, true);
                liInDayItems.addView(hour1);
                hour1.fill();
            } else{
                int hourlySize = hourly.size();
                int availableCount = 0;
                for (int i = 0; i < hourlySize; i++) {
                    NbWthrHourly hh = hourly.get(i);
                    if (hh.DurationMins == DurationMinsStep)
                        availableCount++;
                }
                int itemsAdded = 0;
                for (int i = 0; i < hourlySize; i++) {
                    NbWthrHourly hh = hourly.get(i);
                    if (DurationMinsStep == hh.DurationMins){
                        //Show Hourly
                        itemsAdded++;
                        WeatherHourItemView hour1 = WeatherHourItemView.getInstance(context, null, hh, availableCount == itemsAdded);
                        liInDayItems.addView(hour1);
                        hour1.fill();
                    }
                }
            }
        }


    }
    public static class WeatherHourItemView extends LinearLayout{
        Context context;
        View view;
        ImageView imgColWeatherIcon;
        LinearLayout liSplitor, liMinTemp, liMaxTemp, liSnow, liRain;
        NbWthrDaily daily;
        NbWthrHourly hourly;
        TextView lblTime, lblColCondition, lblTempratureMax, lblTempratureMin, lblWindSpeed, lblWindGust
                , lblRain, lblSnow, lblPop, lblMaxTempIcon;
        boolean hideSplitor;
        public static WeatherHourItemView getInstance(Context context, NbWthrDaily daily, NbWthrHourly hourly, boolean hideSplitor){
            WeatherHourItemView res = new WeatherHourItemView(context);
            res.hideSplitor = hideSplitor;
            res.daily = daily;
            res.hourly = hourly;
            return res;
        }
        public WeatherHourItemView(Context context) {
            super(context);
            this.context = context;
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.frm_weather_show_hour_item, this, true);

            liSplitor = findViewById(R.id.liSplitor);

            lblTime = findViewById(R.id.lblTime);
            imgColWeatherIcon = findViewById(R.id.imgColWeatherIcon);
            lblColCondition = findViewById(R.id.lblColCondition);
            lblTempratureMax = findViewById(R.id.lblTempratureMax);
            lblTempratureMin = findViewById(R.id.lblTempratureMin);
            lblWindSpeed = findViewById(R.id.lblWindSpeed);
            lblWindGust = findViewById(R.id.lblWindGust);
            lblRain = findViewById(R.id.lblRain);
            lblSnow = findViewById(R.id.lblSnow);
            lblPop = findViewById(R.id.lblPop);
            liMinTemp = findViewById(R.id.liMinTemp);
            lblMaxTempIcon = findViewById(R.id.lblMaxTempIcon);
            liMaxTemp = findViewById(R.id.liMaxTemp);
            liSnow = findViewById(R.id.liSnow);
            liRain = findViewById(R.id.liRain);
        }
        public void fill(){
            if (hideSplitor)
                liSplitor.setVisibility(GONE);

            if (daily != null){
                Calendar dt = daily.getDt();
                int condResId = weatherConditionToResId(daily.weather_id, dt, daily.wind_speed);
                Log.d("ADDDDD", condResId + "cond");
                lblTime.setText("");
                imgColWeatherIcon.setBackgroundResource(condResId);
                lblColCondition.setText(WeatherIdToName(daily.weather_id, daily.wind_speed));
                lblTempratureMax.setText((int)Math.round(daily.temp_max) + "°");
                lblTempratureMin.setText((int)Math.round(daily.temp_min) + "°");
                lblWindGust.setText(daily.wind_gust == -1?"-":Integer.toString((int) daily.wind_gust));
                lblWindSpeed.setText(Integer.toString((int) daily.wind_speed));
                if (daily.wind_gust >= 20)
                    lblWindGust.setTextColor(GetWindColor(daily.wind_gust));
                if (daily.wind_speed >= 20)
                    lblWindSpeed.setTextColor(GetWindColor(daily.wind_speed));
                lblRain.setText(daily.rain == 0?"0":Integer.toString((int) Math.ceil(daily.rain)));
                if (daily.rain > 0)
                    liRain.setBackgroundColor(Color.parseColor("#04a3ff"));
                if (daily.snow > 0)
                    liSnow.setBackgroundColor(Color.parseColor("#04a3ff"));
                lblSnow.setText(daily.snow == 0?"0":Integer.toString((int) Math.ceil(daily.snow)));
                lblPop.setText(Integer.toString((int) (daily.pop * 100)));

                liMaxTemp.setBackgroundColor(GetTemperatorColor(daily.temp_max));
                lblTempratureMax.setTextColor(GetTemperatorTextColor(daily.temp_max));
                liMinTemp.setBackgroundColor(GetTemperatorColor(daily.temp_min));
                lblTempratureMin.setTextColor(GetTemperatorTextColor(daily.temp_min));
            }
            else {
                Calendar dt = hourly.getDt();
                lblTime.setVisibility(VISIBLE);
                lblTime.setText(MyDate.CalendarToTimeString(dt, MyDate.TimeToStringFormat.HourMin, ":"));
                imgColWeatherIcon.setBackgroundResource(weatherConditionToResId(hourly.weather_id, hourly.getDt(), hourly.wind_speed));
                lblColCondition.setText(WeatherIdToName(hourly.weather_id, hourly.wind_speed));
                lblTempratureMax.setText((int)Math.round(hourly.temp) + "°");
                lblTempratureMin.setText("");
                lblWindGust.setText(hourly.wind_gust == -1?"-":Integer.toString((int) hourly.wind_gust));
                if (hourly.wind_gust >= 20)
                    lblWindGust.setTextColor(GetWindColor(hourly.wind_gust));
                if (hourly.wind_speed >= 20)
                    lblWindSpeed.setTextColor(GetWindColor(hourly.wind_speed));
                lblWindSpeed.setText(Integer.toString((int) hourly.wind_speed));
                if (hourly.rain > 0)
                    liRain.setBackgroundColor(Color.parseColor("#04a3ff"));
                if (hourly.snow > 0)
                    liSnow.setBackgroundColor(Color.parseColor("#04a3ff"));
                lblRain.setText(Integer.toString((int) Math.ceil(hourly.rain)));
                lblSnow.setText(hourly.snow == 0?"0":Integer.toString((int) Math.ceil(hourly.snow)));
                lblPop.setText(hourly.pop == 0?"0":Integer.toString((int) (hourly.pop * 100)));

                liMinTemp.setVisibility(INVISIBLE);
                lblMaxTempIcon.setVisibility(INVISIBLE);
//                liMinTemp.setBackgroundColor(GetTemperatorColor());

                liMaxTemp.setBackgroundColor(GetTemperatorColor(hourly.temp));
                lblTempratureMax.setTextColor(GetTemperatorTextColor(hourly.temp));
            }
        }
    }

    public static int weatherConditionToResId(int id, Calendar dt, double windSpeed){
        boolean isDay = true;
        int hour = dt.get(Calendar.HOUR_OF_DAY);
        if (hour > 18 || hour < 6)
        {
            isDay = false;
        }
        switch (id)
        {
            case 200: return R.drawable.wd_thunderrain1;
            case 201: return R.drawable.wd_thunderrain2;
            case 202: return R.drawable.wd_thunderrain3;
            case 210: return R.drawable.wd_thuner1;
            case 211: return R.drawable.wd_thuner2;
            case 212: return R.drawable.wd_thuner3;
            case 221: return R.drawable.wd_thuner3;
            case 230: return R.drawable.wd_thunerdrizzle1;
            case 231: return R.drawable.wd_thunerdrizzle3;
            case 232: return R.drawable.wd_thunerdrizzle3;

            case 300: return R.drawable.wd_drizzle3;//bayad dorost beshe drizzle1
            case 301: return R.drawable.wd_drizzle3;//bayad dorost beshe drizzle2
            case 302: return R.drawable.wd_drizzle3;
            case 310: return R.drawable.wd_drizzlerain1;
            case 311: return R.drawable.wd_drizzlerain2;
            case 312: return R.drawable.wd_drizzlerain3;
            case 313: return R.drawable.wd_drizzlerain3;
            case 314: return R.drawable.wd_drizzlerain3;
            case 321: return R.drawable.wd_drizzle3;

            case 500: return R.drawable.wd_rain1;
            case 501: return R.drawable.wd_rain2;
            case 502: return R.drawable.wd_rain3;
            case 503: return R.drawable.wd_rain4;
            case 504: return R.drawable.wd_rain5;
            case 511: return R.drawable.wd_rain1;
            case 520: return R.drawable.wd_rain1;
            case 521: return R.drawable.wd_rain3;
            case 522: return R.drawable.wd_rain3;
            case 531: return R.drawable.wd_rain2;

            case 600: return R.drawable.wd_snow1;
            case 601: return R.drawable.wd_snow2;
            case 602: return R.drawable.wd_snow3;
            case 611: return R.drawable.wd_snowrain1;
            case 612: return R.drawable.wd_snowrain1;
            case 613: return R.drawable.wd_snowrain2;
            case 615: return R.drawable.wd_snowrain1;
            case 616: return R.drawable.wd_snowrain2;
            case 620: return R.drawable.wd_snow1;
            case 621: return R.drawable.wd_snow2;
            case 622: return R.drawable.wd_snow3;
            case 701: return R.drawable.wd_fog1;
            case 711: return R.drawable.wd_fog1;
            case 721: return R.drawable.wd_haze1;
            case 731: return R.drawable.wd_haze1;
            case 741: return R.drawable.wd_haze1;
            case 751: return R.drawable.wd_haze1;
            case 761: return R.drawable.wd_haze1;
            case 762: return R.drawable.wd_haze1;
            case 771: return R.drawable.wd_wind3;
            case 781: return R.drawable.wd_windcload3;
            case 800: return isDay?R.drawable.wd_clear_d:R.drawable.wd_clear_n;
            case 801: return isDay?R.drawable.wd_cload1_d:R.drawable.wd_cload1_n;
            case 802: return isDay?R.drawable.wd_cload2_d:R.drawable.wd_cload2_n;
            case 803: return isDay?R.drawable.wd_cload3_d:R.drawable.wd_cload3_n;
            case 804: return isDay?R.drawable.wd_cload4_d:R.drawable.wd_cload4_n;
            default:
                break;
        }
        return isDay?R.drawable.wd_clear_d:R.drawable.wd_clear_n;
    }
    public static String WeatherIdToName(int id, double windSpeed){
        switch (id)
        {
            case 200: return "بارش سبک و رعد و برق";
            case 201: return "بارش و رعد و برق";
            case 202: return "بارش سنگین و رعد و برق";
            case 210: return "رعد و برق کم";
            case 211: return "رعد و برق";
            case 212: return "رعد و برق سنگین";
            case 221: return "رعد و برق شدید";
            case 230: return "باران ریز سبک و رعد و برق";
            case 231: return "باران ریز و رعد و برق";
            case 232: return "باران ریز سنگین و رعد و برق";

            case 300: return "باران ریز سبک";
            case 301: return "باران ریز";
            case 302: return "باران ریز سنگین";
            case 310: return "باران ریز و بارش";
            case 311: return "بارش و باران ریز";
            case 312: return "بارش و باران ریز شدید";
            case 313: return "باران و باران ریز دوش مانند";
            case 314: return "باران شدید و باران ریز";
            case 321: return "باران ریز دوش مانند";

            case 500: return "باران سبک";
            case 501: return "باران متوسط";
            case 502: return "باران شدید";
            case 503: return "باران خیلی شدید";
            case 504: return "طوفان باران";
            case 511: return "باران یخی";
            case 520: return "باران سبک دوش";
            case 521: return "باران شدید و دوش";
            case 522: return "باران شدید و دوش";
            case 531: return "باران متوسط دوش";

            case 600: return "برف کم";
            case 601: return "برف";
            case 602: return "برف سنگین";
            case 611: return "برف و باران";
            case 612: return "برف و باران سبک";
            case 613: return "برف و باران سنگین";
            case 615: return "برف و باران سبک";
            case 616: return "برف و باران";
            case 620: return "برف کم";
            case 621: return "برف";
            case 622: return "برف سنگین";
            case 701: return "غبار محلی (مه)";
            case 711: return "غبار محلی (مه)";
            case 721: return "مه";
            case 731: return "گرد و خاک";
            case 741: return "مه";
            case 751: return "گرد و خاک";
            case 761: return "گرد و خاک";
            case 762: return "غبار آتشفشانی";
            case 771: return "باد شدید";
            case 781: return "طوفان شدید";
            case 800: return "صاف" ;
            case 801: return "ابرهای پراکنده";
            case 802: return "کمی تا قسمتی ابری";
            case 803: return "نیمه ابری تا ابری";
            case 804: return "تمام ابری";
            default:
                break;
        }
        return "صاف";
    }
    public static int GetTemperatorTextColor(double value){
        if (value < -3)
            return Color.WHITE;
        else
            return Color.BLACK;
    }
    public static int GetTemperatorColor(double value){
        int res = 0;
        int index = -1;
        int[] minColors = {0, 255, 63487, 65290, 16449280, 16711680};
        int[] maxColors = {255, 63487, 65290, 16449280, 16711680, 0};
        int[] minTemps = {-20, -10, 0, 10, 20, 35};
        int[] maxTemps = {-10, 0, 10, 20, 35, 50};
        final int counts = 6;
        for (int i = 0; i < counts && index == -1; i++) {
            if (value >= minTemps[i] && value <maxTemps[i])
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
        double fractBetween = (valueDiff==0) ? 0 : (value - minTemps[index]) / valueDiff;
        double red   = (redMax - redMin)*fractBetween + redMin;
        double green = (greenMax - greenMin)*fractBetween + greenMin;
        double blue  = (blueMax - blueMin)*fractBetween + blueMin;

        int cl = Color.rgb((int)red, (int)green, (int)blue);
        return cl;
    }
    public static int GetWindColor(double value){
        int res = 0;
        int index = -1;
        //327d00, ff9c00, ff0505, 0
        int[] minColors = {3308800, 16751616, 16712965};
        int[] maxColors = {16751616, 16712965, 0};
        int[] minTemps = {20, 45, 70};
        int[] maxTemps = {45, 70, 110};
        final int counts = 3;
        for (int i = 0; i < counts && index == -1; i++) {
            if (value >= minTemps[i] && value <maxTemps[i])
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
        double fractBetween = (valueDiff==0) ? 0 : (value - minTemps[index]) / valueDiff;
        double red   = (redMax - redMin)*fractBetween + redMin;
        double green = (greenMax - greenMin)*fractBetween + greenMin;
        double blue  = (blueMax - blueMin)*fractBetween + blueMin;

        return Color.rgb((int)red, (int)green, (int)blue);
    }

    //تنظیمات مربوط به صفحه --------------
    @Override
    protected int getScreenId() {return PrjConfig.frmWeatherShow;}
    @Override
    protected String tag() {return SCREEN_TAG;}
    public static final String SCREEN_TAG = "WatherShow";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {//3nd Event
        return inflater.inflate(R.layout.frm_weather_show, parent, false);
    }

}
