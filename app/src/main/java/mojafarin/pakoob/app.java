package mojafarin.pakoob;


import android.app.ActivityManager;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.RequiresApi;
import bo.ApiClientMap;
import bo.ApiInterfaceMap;
import bo.dbConstantsTara;
import bo.NewClasses.InsUpdRes;
import bo.entity.MobileInfoDTO;
import bo.entity.NbLogSearch;
import bo.entity.PakoobSync;
import bo.entity.SearchForMapResult;
import bo.NewClasses.SimpleRequest;
import bo.NewClasses.StringContentDTO;
import bo.entity.TTExceptionLog;
import bo.sqlite.NbLogSearchSQLite;
import bo.sqlite.NbMapSQLite;
import bo.sqlite.TTExceptionLogSQLite;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.MyDate;
import utils.PrjConfig;
import bo.entity.NbMap;
import bo.entity.NbPoi;
import bo.entity.NbPoiCompact;
import bo.sqlite.AppDatabaseMap;
import bo.sqlite.NbPoiSQLite;
import bo.dbConstantsMap;
import maptools.hMapTools;
import utils.sessionManager;
import utils.hutilities;

public class app extends Application {
    public static byte APPID = 1;
    public static String key = "af391-dLDLKJ2da-39#*20D)@dlcnLDp";

    public static int ReadTimeOutInSecond = 900;
    public static String actionBarColor = "000000";//lightblue 200 az https://material.io/design/color/the-color-system.html#tools-for-picking-colors
    public static String CurrencyName = "ریال";
    public static sessionManager session;
    public static int CurrentPositionFormat = hMapTools.UTM;
    public static int currentNorth = hMapTools.NORTH_MAG;

    public static ApiInterfaceMap apiMap = ApiClientMap.getClient(60).create(ApiInterfaceMap.class);
    public static List<NbPoiCompact> visiblePOIs = new ArrayList<>();
    public static String tileRoot = "";
    public static int DegreePrecision = 6;
    public static float DegreePrecisionTen = 1000000;
    public static int MinutePrecision = 4;
    public static float MinutePrecisionTen = 10000;
    public static int UtmPrecision = 1;
    public static float declination = 0;
    public static long lastDelinationRead = 0;

    public static int MinTrackDistance = 5;
    public static int MinTrackTime = 2000;
    public static int CalendarType = 1; //1 means Jalali, 2 means Miladi

    public static boolean FirebaseInited = false;

    public static boolean isFirstTimeRunning_ForLocationReadingInMapPage = false;
    @Override
    public void onCreate() {
        super.onCreate();
        hutilities.AppId = APPID;
        hutilities.VersionCode = BuildConfig.VERSION_CODE;
        Context context = getApplicationContext();
        session = new sessionManager(context);
        isFirstTimeRunning_ForLocationReadingInMapPage = app.session.getVisitCounter() == 0;

        if (!PrjConfig.IsDebugMode)
            PrjConfig.WebApiAddress = session.getWebApiAddress();

        Log.e("اپی",  PrjConfig.WebApiAddress);
        app.session.getCCustomer(); // Need to fill hutilities.CCustomerId
        // Required initialization logic here!

        dbConstantsMap.appDB = AppDatabaseMap.getDatabase(getApplicationContext());//Room.databaseBuilder(getApplicationContext(), AppDatabase.class, AppDatabase.DATABASE_NAME).allowMainThreadQueries().build();
        dbConstantsTara.initValues(getApplicationContext(), BuildConfig.DEBUG);

        app.tileRoot = getApplicationContext().getFilesDir() + "/" + hMapTools.tilesFolder + "/";

        if(hutilities.CCustomerId > 0)
            app.syncBuyMapDatabase(null, null, getApplicationContext(), null);


        visiblePOIs = app.getVisiblePOIs();

        currentNorth = app.session.getNorthReference(hMapTools.DEFAULT_NORTH);
        CurrentPositionFormat = app.session.getPositionFormat(hMapTools.DEFAULT_POSITION_FORMAT);


        //TTExceptionLogSQLite.insert("صرفا برای تست تاریخ", "سلام داره تاریخ تست میشه", PrjConfig.frmMapPage, 1010);


//        NbMapSQLite.insert(1768, "مشهد 6", "مشهد 6", "7862 I SE", "مشهد", 36.25,	36.375,	59.5,	59.375
//                , "", "", 25000d, 0, (byte)1, (byte)1, (byte)1
//                , "", (byte)1, (byte)1, "", 25000d, "", 1,	1380,	1
//                , 0d,0d,0d,0d, "", (byte)0, NbMap.Enums.RequestStatusTypes_None, NbMap.Enums.BuyStatusTypes_Done, "" , "", false);
//        NbMapSQLite.insert(1770, "مشهد 5", "مشهد 5", "7962 IV SW", "مشهد", 36.25,	36.375,	59.625,	59.500
//                , "", "", 25000d, 0, (byte)1, (byte)1, (byte)1
//                , "", (byte)1, (byte)1, "", 25000d, "", 1,	1380,	1
//                , 0d,0d,0d,0d, "", (byte)0, NbMap.Enums.RequestStatusTypes_None, NbMap.Enums.BuyStatusTypes_Done, "" , "", false);
//        NbMapSQLite.insert(1764, "کنگ و زشک", "کنگ", "7862 IV SE", "مشهد", 36.25,	36.375,	59.25,	59.125
//                , "", "", 25000d, 0, (byte)1, (byte)1, (byte)1
//                , "", (byte)1, (byte)1, "", 25000d, "", 1,	1380,	1
//                , 0d,0d,0d,0d, "", (byte)0, NbMap.Enums.RequestStatusTypes_None, NbMap.Enums.BuyStatusTypes_Done, "" , "", false);

//        ArrayList<Integer> ms = new ArrayList<Integer>();
//session.setMyClubNameIds(ms);


        //تنظیمات مربوط به پیکاسو - Done in app-no need here
//        Picasso.Builder picassoBuilder = new Picasso.Builder(this);
//        picassoBuilder.downloader(new OkHttp3Downloader(this));
//        if(BuildConfig.DEBUG) { picassoBuilder.loggingEnabled(true); }
//        if(isLowMemoryDevice()) { picassoBuilder.defaultBitmapConfig(Bitmap.Config.RGB_565); }
//        Picasso.setSingletonInstance(picassoBuilder.build());

        createNotificationChannels();

        DoSyncPakoobSyncObj(context);

        //DoSyncL(context);
    }

    private void createNotificationChannels() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            addNotifChanel(getString(R.string.NOTIF_CHANAL_Adv), getString(R.string.NOTIF_CHANAL_Adv_Desc), PrjConfig.NOTIF_CHANAL_ADV_ID, NotificationManager.IMPORTANCE_DEFAULT);
            addNotifChanel(getString(R.string.NOTIF_CHANAL_SYSTEM), getString(R.string.NOTIF_CHANAL_SYSTEM_Desc), PrjConfig.NOTIF_CHANAL_SYSTEM_ID, NotificationManager.IMPORTANCE_DEFAULT);
            addNotifChanel(getString(R.string.NOTIF_CHANAL_PrivateMessage), getString(R.string.NOTIF_CHANAL_PrivateMessage_Desc), PrjConfig.NOTIF_CHANAL_PrivateMessage_ID, NotificationManager.IMPORTANCE_DEFAULT);
            addNotifChanel(getString(R.string.NOTIF_CHANAL_PublicMessage), getString(R.string.NOTIF_CHANAL_PublicMessage_Desc), PrjConfig.NOTIF_CHANAL_PublicMessage_ID, NotificationManager.IMPORTANCE_DEFAULT);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addNotifChanel(CharSequence name, String description, String id, int importance){
        NotificationChannel channel = new NotificationChannel(id, name, importance);
        channel.setDescription(description);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    private boolean isLowMemoryDevice() {
        if(Build.VERSION.SDK_INT >= 19) {
            return ((ActivityManager) getSystemService(ACTIVITY_SERVICE)).isLowRamDevice();
        } else {
            return false;
        }
    }

    public static NbPoiCompact findInVisiblePois(long NbPoiId){
        int visiSize =app.visiblePOIs.size();
        for (int i = 0; i < visiSize ; i++) {
            NbPoiCompact item =app.visiblePOIs.get(i);
            if (item.NbPoiId == NbPoiId) {
                return item;
            }
        }
        return null;
    }


    public static List<NbPoi> SetShowHide(NbPoi current, byte ShowStatus, boolean addToVisibileItemsAndMap, Context context){
        List<NbPoi> res = new ArrayList<>();
        current.ShowStatus = ShowStatus;
        res.add(current);
        if (addToVisibileItemsAndMap) {
            //1- Find this in VisibleItems
            NbPoiCompact compactItem = app.findInVisiblePois(current.NbPoiId);
//            int visiSize =app.visiblePOIs.size();
//            for (int i = 0; i < visiSize && compactItem == null; i++) {
//                NbPoiCompact item =app.visiblePOIs.get(i);
//                if (item.NbPoiId == current.NbPoiId) {
//                    compactItem = item;
//                }
//            }
            if (compactItem != null)
                compactItem.ShowStatus = ShowStatus;


            if (ShowStatus == NbPoi.Enums.ShowStatus_Hide){
                //2- Hide Item if fined in prev step
                if (compactItem != null)
                    MapPage.showHidePoiOnMap(compactItem);
            }
            else {
                if (compactItem != null)
                    MapPage.showHidePoiOnMap(compactItem);
                else {
                    if (ShowStatus == NbPoi.Enums.ShowStatus_Show) {
                        compactItem = NbPoiCompact.getInstance(current);
                        MapPage.addPOIToMap(compactItem, MainActivity.map, false, context);
                        app.visiblePOIs.add(compactItem);
                    }
                }
            }
        }

        dbConstantsMap.appDB.NbPoiDao().update(current.NbPoiId, ShowStatus);
        if (current.PoiType == NbPoi.Enums.PoiType_Folder){
            List<NbPoi> childs = NbPoiSQLite.selectByLevel(current.Level + 1, current.NbPoiId);
            for (int i = 0; i < childs.size(); i++) {
                List<NbPoi> updated = SetShowHide(childs.get(i), ShowStatus, addToVisibileItemsAndMap, context);
                res.addAll(updated);
            }
        }
        return  res;
    }
    public static List<NbPoiCompact> getVisiblePOIs(){
        List<NbPoiCompact> res = new ArrayList<>();
        List<NbPoi> temp = dbConstantsMap.appDB.NbPoiDao().getVisiblePOIs(NbPoi.Enums.ShowStatus_Show, 100000);
        int poiSize = temp.size();
        for (int i = 0; i < temp.size(); i++) {
            res.add(NbPoiCompact.getInstance(temp.get(i)));
        }
        return  res;
    }

    public static boolean doSyncsAndRedesignAfterLogin(Context context, MainActivity mainActivity, boolean reloadHome){
        syncBuyMapDatabase(null, null, context, null);

        String token = app.session.getFirebaseRegId();
        app.updateServerFirebaseToken(token, context);

        mainActivity.setMenuVisibility(reloadHome);

        return true;
    }

    public static void updateServerFirebaseToken(String token, Context context) {

        if (FirebaseInited || !hutilities.isInternetConnected(context)){
            return;
        }
        //به روز رسانی سرور و اطلاع از توکن دریافتی
        MobileInfoDTO.FBTokenUpdateRequest req = new MobileInfoDTO.FBTokenUpdateRequest(hutilities.CCustomerId, token);
        SimpleRequest request = SimpleRequest.getInstance(req);

        Call<ResponseBody> call = dbConstantsMap.apiFcm.UpdateFirebaseToken(request);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //if (!isAdded()) return;
                if (response.code() == 200) {
//                            ResponseBody res = response.body();
                    Log.d("Firebase Token", "Server Updated");
                    app.FirebaseInited = true;
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                if (!isAdded()) return;
            }
        });
    }

    public static boolean DoSyncPakoobSyncObj(Context context){
        try {
            if (!hutilities.isInternetConnected(context))
                return false;

            List<NbLogSearch> searchlog = NbLogSearchSQLite.selectAll();
            List<TTExceptionLog> exceptions = TTExceptionLogSQLite.selectAll();
            for (int i = 0; i < exceptions.size(); i++) {
                TTExceptionLog exLog = exceptions.get(i);
                //1401-07-27 برای این که تاریخ به درستی به سرور ارسال بشه و خطا نده
                exLog.ExDateStr = MyDate.CalendarToCSharpJSONAcceptable(exLog.ExDate);
                exLog.ExDate = null;
            }

//            NbLogSearch tt = new NbLogSearch();
//            tt.Lat = 1000;
//            tt.Lon = 2000;s
//            tt.SearchText = "salaaaam سلام";
//            NbLogSearch tt2 = new NbLogSearch();
//            tt.Lat = 222;
//            tt.Lon = 444;
//            tt.SearchText = "خدا قوتت بده سلام";
//            tt.SearchDateStr = MyDate.CalendarToCSharpDateTimeAcceptable(Calendar.getInstance());
//
//            searchlog.add(tt);
//            searchlog.add(tt2);
//
//            TTExceptionLog ex1 = new TTExceptionLog();
//            ex1.AppId = 1;
//            ex1.CCustomerId = 2999l;
//            ex1.ExText = "سلامس سییم";
//            ex1.ExDate = Calendar.getInstance();
//            ex1.ExDateStr = MyDate.CalendarToCSharpDateTimeAcceptable(ex1.ExDate);
//            ex1.ExDate = null;
//            TTExceptionLog ex2 = new TTExceptionLog();
//            ex2.AppId = 11;
//            ex2.CCustomerId = 39l;
//            ex2.ExText = "khoobied تو؟";
//            ex2.ExDate = Calendar.getInstance();
//            ex2.ExDateStr = MyDate.CalendarToCSharpDateTimeAcceptable(ex2.ExDate);
//            ex2.ExDate = null;
//            exceptions.add(ex1);
//            exceptions.add(ex2);

            Log.e("SYNCCC", "سینک شروع شد");
            LatLng lastLocation = app.session.getLastAproxLocation();
            PakoobSync sync = PakoobSync.getInstance(lastLocation.latitude, lastLocation.longitude, app.session.getLastAproxLocationFixTime(), app.session.getLastAproxLocationFixType(), exceptions, searchlog);
            SimpleRequest request = SimpleRequest.getInstance(sync);

            Call<ResponseBody> call = apiMap.SyncPakoob(request);
            call.enqueue(new Callback<ResponseBody>(){
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response){
                    //if (!isAdded()) return;
                    try {
                        if (response.isSuccessful()) {
                            NbLogSearchSQLite.deleteAll();
                            TTExceptionLogSQLite.deleteAll();

                            Log.e("SYNCCC", "همه چی سینک شد");
//                            InsUpdRes result = InsUpdRes.fromBytes(response.body().bytes());
//                            if (result.isOk)
//                                //llalalal
                        }
                    }catch (Exception ex){
                        Log.e("SYNCCM", "خراب سینک شد");

                        ex.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    //                if (!isAdded()) return;
                }
            });
        } catch (Exception e) {
            Log.e("SYNCCCP", e.getMessage());
            e.printStackTrace();
        }

        return true;
    }
    public static boolean syncBuyMapDatabase(ProgressBar progressBar, TextView txtSearchResult, Context context, MapSelect.OnMapDbSyncCompleted onMapDbSyncCompleted){
        final String TAG = "سینک_مپ";
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        List<NbMap> local = NbMapSQLite.selectAll();

        SimpleRequest request = SimpleRequest.getInstance(StringContentDTO.getInstance(Calendar.getInstance().getTime().toString() + "*" + hutilities.CCustomerId));
        Call<ResponseBody> call = app.apiMap.SyncMyMaps(request);
        Log.e(TAG, "Call Sync...");
        call.enqueue(new Callback<ResponseBody>(){
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, Response<ResponseBody> response)
            {
                try {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);

                    Log.e(TAG, "Check Response...");
                    if (response.isSuccessful()) {
                        Log.e(TAG, "Response OK...");
                        SearchForMapResult result = SearchForMapResult.fromBytes(response.body().bytes());

                        if (result.message.length() > 0) {
                            if (txtSearchResult != null) txtSearchResult.setVisibility(View.VISIBLE);
                            if (txtSearchResult != null) txtSearchResult.setText(result.message);
                        } else {
                            if (txtSearchResult != null) txtSearchResult.setVisibility(View.GONE);
                        }
                        Log.e(TAG, "Check ResList #1..." + (result.resList == null?"result.resList is NULL" : "result.resList IS OK"));
                        if (result.resList != null) {// BUG at 1401-08-01 : && result.resList.size() > 0در صورتی که باید اگه خالی می بود هم دیتابیس داخلی چک می شد
                            Log.e(TAG, "GET ResList OK");
                            List<NbMap> live = result.resList;
                            int resSize = live.size();
                            int locSize = local.size();
                            Log.e(TAG, "Reslist Size is " + resSize + " and LocSize: " + locSize);
                            Boolean [] localFinds = new Boolean[locSize];
                            Arrays.fill(localFinds, Boolean.FALSE);
                            for (int i = 0; i < resSize; i++) {
                                NbMap liveCurrent = live.get(i);
                                for (int j = 0; j < locSize; j++) {
                                    NbMap localCurrent = local.get(j);

                                    if (liveCurrent.NbMapId.intValue() == localCurrent.NbMapId.intValue()){
                                        liveCurrent.Extracted = localCurrent.Extracted;
                                        liveCurrent.LocalFileAddress = localCurrent.LocalFileAddress;
                                        liveCurrent.IsVisible = localCurrent.IsVisible;
                                        localFinds[j] = true;
                                        Log.e(TAG, "Find One localFinds...");
                                        break;
                                    }
                                }
                            }
                            Log.e(TAG, "Search Local... and Locsize: " + locSize);
                            for (int i = 0; i < locSize; i++) {
                                if (!localFinds[i] ){
                                    NbMap item = local.get(i);
                                    Log.e(TAG, "Start Deleting...");
                                    hMapTools.deleteTilesAtManyZoom(context, item, hMapTools.CustomMapMinZoom, hMapTools.CustomMapMaxZoomAvailable, true);
                                    Log.e(TAG, "End Deleting...");
                                }
                            }
                            //Hazf Ghabli haa
                            NbMapSQLite.deleteAll();
                            //Sabte Jadid haa
                            for (int i = 0; i < resSize; i++) {
                                NbMapSQLite.insert(live.get(i));
                            }
//                            rvSearchResult.setVisibility(View.VISIBLE);
//                            initAdapterSearch(result.resList);
                        } else {
//                            rvSearchResult.setVisibility(View.GONE);
                        }
                    } else {
                        if (txtSearchResult != null) txtSearchResult.setVisibility(View.VISIBLE);
                        if (txtSearchResult != null) txtSearchResult.setText("متاسفانه در برقراری ارتباط با سرور مشکلی به وجود آمده است. لطفا بعدا مجددا تلاش نمایید.");
                        Log.e(TAG, "ResponseCODE: " + response.code());
                    }
                    if (onMapDbSyncCompleted != null) {
                        Log.e(TAG, "Call onMapDbSyncCompleted");
                        onMapDbSyncCompleted.onMapDbSyncCompleted(response.isSuccessful());
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                    Log.e(TAG, ex.getMessage());
                    TTExceptionLogSQLite.insert(ex.getMessage(), "", PrjConfig.app, 101);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //if (!isAdded()) return;
                Log.e(TAG, "Response Failed..." + t.getMessage());
                try {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    if (txtSearchResult != null) txtSearchResult.setVisibility(View.VISIBLE);
                    if (txtSearchResult != null)
                        txtSearchResult.setText("متاسفانه مشکلی در برقراری ارتباط با سرور به وجود آمده است. لطفا بعدا مجددا تلاش نمایید.");
                }catch (Exception ex){
                    //Unhandlled-Amdi

                }
            }
        });

        return true;
    }

    //retired at 29
    public static boolean DoSyncExceptions(Context context){
        try {
            if (!hutilities.isInternetConnected(context))
                return false;
            List<TTExceptionLog> local = TTExceptionLogSQLite.selectAll();
            if (local.size() == 0)
                return true;

//            Gson gson = new Gson();
//            String json = gson.toJson(local);

        String json = "";
        int size = local.size();
        for (int i = 0; i < size; i++) {
            json += (i == 0?"[":"") + local.get(i).toJSON()+ (i != size - 1?",":"") +  (i == size - 1?"]":"");
        }

            SimpleRequest request = SimpleRequest.getInstance(StringContentDTO.getInstance(json));

            Call<ResponseBody> call = dbConstantsMap.apiFcm.SyncExceptions(request);
            call.enqueue(new Callback<ResponseBody>(){
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response){
                    //if (!isAdded()) return;
                    try {
                        if (response.isSuccessful()) {
                            InsUpdRes result = InsUpdRes.fromBytes(response.body().bytes());
                            Log.e("اکسپشنها", "ثبت شد");
                            if (result.isOk) {
                                TTExceptionLogSQLite.deleteAll();
                                Log.e("اکسپشنها", "پاک شد");
                            }
                        }
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    //                if (!isAdded()) return;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    public static boolean DoSyncL(Context context){
        try {
            if (!hutilities.isInternetConnected(context))
                return false;


            String lastLocationFixTime = app.session.getLastAproxLocationFixTime();
            if (lastLocationFixTime.equals(""))
                return true;

            LatLng lastLocation = app.session.getLastAproxLocation();
            String content = String.valueOf(lastLocation.latitude) + "*" + String.valueOf(lastLocation.longitude) + "*" + lastLocationFixTime+ "*" + app.session.getLastAproxLocationFixType();

            SimpleRequest request = SimpleRequest.getInstance(StringContentDTO.getInstance(content));

            Call<ResponseBody> call = app.apiMap.SyncL(request);
            call.enqueue(new Callback<ResponseBody>(){
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response){
                    //if (!isAdded()) return;
                    try {
                        if (response.isSuccessful()) {
                            InsUpdRes result = InsUpdRes.fromBytes(response.body().bytes());
                            if (result.isOk)
                                app.session.setLastAproxLocationFixTime("");
                        }
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    //                if (!isAdded()) return;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }


    public static boolean DoSendProblem(int ProblemCode, String Content, Context context, InsUpdRes.HAsyncResponseInsUpdRes responseFunction){
        try {
            if (!hutilities.isInternetConnected(context)) {
                InsUpdRes res = new InsUpdRes();
                res.isOk = false;
                res.command = "-1";
                responseFunction.onResponseCompleted(res);
                return false;
            }

            String content = String.valueOf(ProblemCode) + PrjConfig.MessageSplitor + Content;

            SimpleRequest request = SimpleRequest.getInstance(StringContentDTO.getInstance(content));

            Call<ResponseBody> call = app.apiMap.NeedHelpIn(request);
            call.enqueue(new Callback<ResponseBody>(){
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response){
                    //if (!isAdded()) return;
                    InsUpdRes res = new InsUpdRes();
                    res.isOk = false;
                    try {
                        Log.e("ارسال مشکل", "001");
                        if (response.isSuccessful()) {
                            res = InsUpdRes.fromBytes(response.body().bytes());
                            Log.e("ارسال مشکل", "112");
                            responseFunction.onResponseCompleted(res);
                            Log.e("ارسال مشکل", "113");
                            return;
                        }
                        Log.e("ارسال مشکل", "114");
                        res.command = String.valueOf(response.code());
                    }catch (Exception ex){
                        Log.e("ارسال مشکل", "115");
                        TTExceptionLogSQLite.insert(ex.getMessage(), "", PrjConfig.frm_FunctionSendProblem, 220);
                        ex.printStackTrace();
                        res.command = "0";
                    }
                    Log.e("ارسال مشکل", "116");
                    responseFunction.onResponseCompleted(res);
                    Log.e("ارسال مشکل", "002");
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    try {
                        TTExceptionLogSQLite.insert(t.getMessage(), "", PrjConfig.frm_FunctionSendProblem, 221);
                        //if (!isAdded()) return;
                        Log.e("ارسال مشکل", "003");
                        t.printStackTrace();
                        responseFunction.onResponseCompleted(null);
                    } catch (Exception ex){
                        //Unhandlled-Amdi
                    }
                }
            });
        } catch (Exception ex) {
            Log.e("ارسال مشکل", "134");
            TTExceptionLogSQLite.insert(ex.getMessage(), "", PrjConfig.frm_FunctionSendProblem, 222);
            ex.printStackTrace();
        }

        return true;
    }

    // Called by the system when the device configuration changes while your component is running.
    // Overriding this method is totally optional!
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//    }
//
//    @Override
//    public void onLowMemory() {
//        super.onLowMemory();
//    }


}