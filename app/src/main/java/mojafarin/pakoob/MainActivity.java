package mojafarin.pakoob;

import static UI.HFragment.stktrc2k;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import bo.NewClasses.InsUpdRes;
import bo.dbConstantsMap;
import bo.dbConstantsTara;
import bo.entity.FmMessage;
import bo.entity.NbMap;
import bo.entity.NbPoi;
import bo.sqlite.TTExceptionLogSQLite;
import maptools.LocationTrackingService;
import pakoob.ClubView_Home;
import pakoob.TourShowOne;
import UI.HFragment;
import utils.MyDate;
import utils.PrjConfig;
import bo.entity.MobileInfoDTO;
import mojafarin.pakoob.mainactivitymodes.DialogMapBuilder;
import FmMessage.SideList;
import maptools.hMapTools;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import user.PleaseRegister;
import user.Register;
import utils.MainActivityManager;
import utils.hutilities;
import utils.projectStatics;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

//import com.airbnb.deeplinkdispatch.DeepLink;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Stack;

//@DeepLink("naghshebaz://payok/{id}")
public class MainActivity extends MainActivityManager {
    public static GoogleMap map;
    public static AlertDialog dialogMap = null;
    public static DialogMapBuilder dialogMapObj;


    public static boolean isTrackUp = true;
    public static boolean isLockOnMe = true;

    //1402-03-31 برای زمانی که اپ یه لحظه بسته میشه و دوباره باز میشه لازمه این کار انجام بشه برای لود مجدد همه چیز
    public static boolean appExistsBeforeAndShouldReloadAll_ReReadPois = true;
    public static boolean appExistsBeforeAndShouldReloadAll_OpenMapFirst = true;
    public static boolean appExistsBeforeAndShouldReloadAll_ReReadDialogMap = true;
    boolean onCreateIsCalling = true;

    public static LatLng currentLatLon;
    public static double currentElev = 0;
    public static float currentSpeed = 0;
    public static LatLng cameraLatLon;

    public static String BearingTextFormCurrent = "";
    public static String DistanceTextFormCurrent = "";
    public static String BearingValueFormCurrent = "";
    public static String DistanceValueFormCurrent = "";


    public String recievedFileName = "";
    public InputStream inputStream = null;
    boolean mapIsShowingOnTop = false;

    byte buyType = 0;
    String goodId_NbMapId, buyId;
    boolean goDirectlyToMapPage = false;

    void processIntent(Intent intent) {
        if (intent.getData() != null) {

            String data = intent.getDataString();
            if (data.contains("payok")) {
                try {
                    //format is : naghshebaz://payok/ID_OF_BUY/TYPE_OF_BUY
                    String urlData = data.substring(data.indexOf("payok") + 6);
                    String[] parts = urlData.split("/");
                    buyType = NbMap.Enums.NbBuyType_Map;
                    goodId_NbMapId = "";
                    buyId = "";
                    if (parts.length > 0)
                        buyId = parts[0];
                    if (parts.length > 1) {
                        buyType = Byte.parseByte(parts[1]);
                    }
                    if (parts.length > 2) {
                        goodId_NbMapId = parts[2];
                    }

                    Log.e("BBBBBB", data + "--" + "values: " + buyType + "--" + buyId + "--" + goodId_NbMapId);

                    if (buyType == NbMap.Enums.NbBuyType_Map) {
                        //1401-05-15 Commented and moved to MapSelect Page
                        //app.syncBuyMapDatabase(null, null, MainActivity.this, null);
                        projectStatics.showDialog(MainActivity.this
                                , getResources().getString(R.string.afterBuyMap_title)
                                , getResources().getString(R.string.afterBuyMap_desc)
                                , getResources().getString(R.string.ok)
                                , view -> {
                                    //1401-05-15 added
                                    MapSelect page = MapSelect.getInstance(true);
                                    showFragment(page);
                                }
                                , ""
                                , null);
                    } else if (buyType == NbMap.Enums.NbBuyType_GPX) {
                        projectStatics.showDialog(MainActivity.this
                                , getResources().getString(R.string.afterBuyGPX_title)
                                , getResources().getString(R.string.afterBuyGPX_desc)
                                , getResources().getString(R.string.ok)
                                , view -> {
                                    if (currentFragment instanceof SafeGpxView){
                                        SafeGpxView tmp = (SafeGpxView) currentFragment;
                                        tmp.reOpenForDownloadAfterBuy();
                                    }
                                    else{
                                        SafeGpxView frm = SafeGpxView.getInstance(Integer.valueOf(goodId_NbMapId), PrjConfig.frmMainActivity);
                                        showFragment(frm);
                                    }
                                }
                                , ""
                                , null);
                    }
                } catch (Exception ex) {
                    int x = 0;
                }
            }
            else if (data.contains("content")) {
                String action = intent.getAction();
                recievedFileName = "";
                inputStream = null;

                try {
                    if (action.compareTo(Intent.ACTION_VIEW) == 0) {
                        String scheme = intent.getScheme();
                        ContentResolver resolver = getContentResolver();

                        if (scheme.compareTo(ContentResolver.SCHEME_CONTENT) == 0) {
                            Uri uri = intent.getData();
                            recievedFileName = hutilities.getContentFullPath(resolver, uri);

                            if (recievedFileName == null) {
                                inputStream = resolver.openInputStream(uri);
                                recievedFileName = uri.toString();

                            } else {
                                inputStream = null;
                            }
                            //inputStream = resolver.openInputStream(uri);
                        } else if (scheme.compareTo(ContentResolver.SCHEME_FILE) == 0) {
                            Uri uri = intent.getData();
                            recievedFileName = uri.getLastPathSegment(); // mine-not tested
//                            String name = uri.getLastPathSegment();
//                            inputStream = resolver.openInputStream(uri);
                        } else if (scheme.compareTo("http") == 0) {
                            // TODO Import from HTTP!
                        } else if (scheme.compareTo("ftp") == 0) {
                            // TODO Import from FTP!
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                intent.setData(null);
            }
            else{

            }
        } if ("Location_Tracking_Service".equals(intent.getAction())){
            //در حال ثبت مسیر یا فرایندی مشابه هست، صاف بره به صفحه نقشه و مسیریابی
            goDirectlyToMapPage = true;
        }
        if (intent.getExtras() != null && intent.getExtras().containsKey("ChanalId")) {
            //برای زمانی که روی نوتیفیکیشن مربوط به ثبت ترک کلیک میکنه میخوایم که ترک ها لود بشه حتما
            String st = intent.getExtras().getString("ChanalId");
            //appExistsBeforeAndShouldReloadAll_ReReadPois = true; 1402-09-14 POIHIDE
            appExistsBeforeAndShouldReloadAll_ReReadDialogMap = true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreateIsCalling = true;

        //Check prev versions POIHIDE
        if (savedInstanceState != null) {
            savedInstanceState.getInt(REDRAW_POIS_ON_STATE, 1);
        }
        appExistsBeforeAndShouldReloadAll_ReReadPois = true; //POIHIDE

        //GPXFile.DeleteAllNbPois();
        //GPXFile.ParseFile("",MainActivity.this);
        Intent intent = getIntent();
        processIntent(intent);

        app.session.setVisitCounter(app.session.getVisitCounter() + 1);

        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //روشن نگه داشتن صفحه نمایش
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            hutilities.setStatusBarColor(ContextCompat.getColor(this,pakoob.DbAndLayout.R.color.StatusBarColor), true, this);
//        }

        //force RTL
        hutilities.forceRTLIfSupported(this);

        initializeComponents();


        //Init Home and Map Page - Start ------------------------
        //داستان این بود که فرگمنت منیجیر، بعد از بازگشت از خواب، باز هم نقشه های قبل رو داخل خود شدش داشت. در نتیجه وقتی من صفحه فرگمنت جدید
        //رو ایجاد می کردم و اضافه می کردم، می رفت روی صفحه قبلی می شست و اصلا یه وضع گندی میشد
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        boolean needToAddHome = true;
        boolean needToAddMap = true;
        for (Fragment fragment : fragments) {
            if (fragment instanceof Home) {
                home =  (Home) fragment;
                needToAddHome = false;
                Log.e( "پرپرفر ها", " CALLED0 ");
            }
            else if (fragment instanceof MapPage) {
                mapPage =  (MapPage) fragment;
                needToAddMap = false;
                Log.e( "پرپرفر ها", " CALLED1 ");

            }
        }

        if (needToAddHome || needToAddMap){
            if (home == null)
                home = Home.getInstance();
            if (mapPage == null)
                mapPage = MapPage.getInstance(this);

            //fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            if (needToAddHome)
                ft.add(R.id.your_placeholder, home);
            if (needToAddMap)
                ft.add(R.id.your_placeholder, mapPage);
            ft.commit();
        }
        //Init Home and Map Page - Start ------------------------

        //3 - Show Other Pages of needed
        int visitCounter = app.session.getVisitCounter();


        //1404-08 درست کار نکرد، فعلا کامنتش کردم. خطا میداد که کانتکست نال هست
//        if(goDirectlyToMapPage){
//            backToMapPage();
//        }
//        else
            if (!app.session.isLoggedIn()) {
            showFragment(new PleaseRegister());
        }
        else if (app.session.getOpenHomeAtStartup() == 1) {
            backToHome();
        }

        if (!app.isFirstTimeRunning_ForLocationReadingInMapPage)
            saveAndSendInitLocation(getApplicationContext());

        //همزمان در oncreate-onNewIntent
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("NotificationMessage")) {
                showNotificationAtMain(extras);
            }
        }

        doCheckVersion();

        //1402-04 انتقال دسترسی به مکان به صفحه نقشه

        //Also call when login
        initFirebase();

        //initTrackService();


        Log.e("BBBBBB", "values again: " + buyType + "--" + buyId + "--" + goodId_NbMapId);

        //همزمان در onCreate-onNewIntent
        if ((recievedFileName != null && recievedFileName.length() > 0) || inputStream != null) {
            showFragment(MyTracks.getInstance("start", recievedFileName, "", inputStream));
            inputStream = null;
            recievedFileName = "";
        } else if (buyType != 0 && !goodId_NbMapId.isEmpty()) {
            Log.e("BBBBBB", "Goto Show Fragment");
            showFragment(SafeGpxView.getInstance(Integer.parseInt(goodId_NbMapId), PrjConfig.frmHome));
        }

        //1402-04 توی رزیوم فرزند فراخوانی میشه
//        if (mapPage != null)
//            mapPage.stopTrackRecordingServiceIfRunning();

        //Toast.makeText(MainActivity.this, getResources().getString(R.id.google_maps_key), Toast.LENGTH_SHORT).show();

//
//        byte[] request = new byte[10];
//        request[0] = 93;
//        request[1] = 29;
//        request[3] = 30;
//        SimpleRequest inp = new SimpleRequest();
//        inp.data=  android.util.Base64.encodeToString(request, android.util.Base64.DEFAULT);
//        inp.mobileInfoDTO = MobileInfoDTO.instance();
//        //RequestBody body = RequestBody.create(MediaType.parse("application/octet-stream"), request);
//        Call<String> call = app.apiService.BuyMap(inp);
//
//        call.enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(Call<String> call, Response<String> response) {
//                int x = 0;
//
//                if (response.isSuccessful()) {
//                }
//            }
//            @Override
//            public void onFailure(Call<String> call, Throwable t) {
//                int x = 0;
//            }
//        });

    }

    public static void saveAndSendInitLocation(Context context) {

        // check permission and get permission
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            projectStatics.showDialog(context
                    , context.getString(R.string.locationPermission_Denied)
                    , context.getString(R.string.locationPermission_Desc)
                    , context.getString(R.string.ok)
                    , view -> {
                        ActivityCompat.requestPermissions((Activity) context, new String[]{
                                android.Manifest.permission.ACCESS_FINE_LOCATION
                        }, PrjConfig.Location_FINE_PERMISSION_REQUEST_CODE);
                    }
                    , ""
                    , null);
        }

        //HHH 1400-09-10 FOR COARSE LOCATION
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        LocationListener listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    app.session.setLastAproxLocation(new LatLng(location.getLatitude(), location.getLongitude()));
                    app.session.setLastAproxLocationFixTime(MyDate.CalendarToCSharpDateTimeAcceptable(Calendar.getInstance()));
                    app.session.setLastAproxLocationFixType((byte) 2);
                    app.DoSyncL(context);
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
            }
        };

        if (!(ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            locationManager.requestSingleUpdate(criteria, listener, null);
        }
    }

    private void initializeComponents() {
        //reCreateDialogMapObj(this);
    }

    public void reCreateDialogMapObj(Context context) {
        dialogMapObj = new DialogMapBuilder(context);
        if (true || dialogMap == null) { //یه کاری کردم که همش اجرا بشه ببینم کار میکنه کوفتی یا نه 1402-04
            AlertDialog.Builder alertDialogBuilder = dialogMapObj.GetBuilder();
            dialogMap = alertDialogBuilder.create();
            Window w = dialogMap.getWindow();
            w.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }

    }


    //-------------------------------------- END WAYPOINT DIALOG WORKS------------------------------------------


    public static final int ResultCode_ForMyTracks = 1200;


    @Override
    public void changeFragmentVisibility(final Fragment fragment, boolean makeVisible) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.animator.fade_in,
                android.R.animator.fade_out);
        if (makeVisible) {
            ft.hide(myFragments.get(myFragments.size() - 1)); //مخفی کردن فرگمنت فعلی
            ft.show(fragment); // نمایش فرگمنت درخواستی
            myFragments.push(fragment);
            Log.d("hidden", "Show");

        } else {
            ft.hide(fragment); //مخفی  کردن فرگمنت درخواستی
            ft.show(myFragments.get(myFragments.size() - 2)); // نمایش فرگمنتی که در پشت سر این فرگمنت قرار داشت
            myFragments.pop();
            Log.d("Shown", "Hide");
        }

        ft.commit();
    }

    public void backToMapPage() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();

        int backStackCount = myFragments.size();

        int counter = backStackCount;
        Log.e(Tag, "Back Stack Count : " + Integer.toString(backStackCount));
        while (counter > 0) {
            counter--;
            Fragment top = myFragments.pop();
            ft.remove(top);
            if (top.getClass() == HFragment.class)
                ((HFragment) top).onFragmentRemoved();

        }

        ft.show(mapPage);

        if (!home.isHidden()) {
            ft.hide(home);
            home.onFragmentHided();
        }
        currentFragment = mapPage;
        mapIsShowingOnTop = true;
        ft.commit();
        ((HFragment) currentFragment).onFragmentShown();
    }

    @Override
    public void backToHome() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();

        int backStackCount = myFragments.size();

        int counter = backStackCount;
        Log.e(Tag, "Back Stack Count : " + Integer.toString(backStackCount));
        while (counter > 0) {
            counter--;
            ft.remove(myFragments.pop());
        }

        ft.show(home);
        if (!mapPage.isHidden()) {
            ft.hide(mapPage);
            mapPage.onFragmentHided();
        }
        currentFragment = home;
        mapIsShowingOnTop = false;
        ft.commit();
        ((HFragment) currentFragment).onFragmentShown();
    }

    Fragment currentFragment = null;
    Stack<Fragment> myFragments = new Stack<>();

    @Override
    public void showFragment(Fragment mFragment) {
        showFragment(mFragment, false);
    }

    //توجههههههههههههههههه - توابع onFragmentShowen در این تابع اعمال نشده
    public void hideMapFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        int backStackCount = fragmentManager.getBackStackEntryCount();
        Log.e("تعداد صفحات", Integer.toString(backStackCount));
        transaction.hide(mapPage);
        mapPage.onFragmentHided();
        transaction.show(home);
        backStackCount = fragmentManager.getBackStackEntryCount();
        Log.e("تعداد صفحات", Integer.toString(backStackCount));
        transaction.commit();
        home.onFragmentShown();
    }

    //برای نمایش مجدد فرگمنت هایی که حالت پاپ آپ دارن استفاده میشه
    //توجههههههههههههههههه - توابع onFragmentShowen در این تابع اعمال نشده
    @Override
    public void ShowChidFragmentAgain(Fragment mFragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.show(mFragment);
        ft.commit();
    }
    //برای مخفی کردن فرگمنت هایی که حالت پاپ آپ دارن استفاده میشه
    //توجههههههههههههههههه - توابع onFragmentShowen در این تابع اعمال نشده هنوز
    @Override
    public void HideChildFragment(Fragment mFragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.hide(mFragment);
        ft.commit();
    }

    @Override
    public void showFragment(Fragment mFragment, boolean closeCurrent) {
        showHideMainActivityOnNavigation(false);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();

        if (closeCurrent) {
            doBackOnFragmentStacks(fragmentManager);
        }
        int count = myFragments.size();
        //اگه صفحه دیگه ای باز بود، اون رو مخفی کنیم
        if (count > 0) {
            Fragment peak = myFragments.peek();
            ft.hide(peak);
            if (peak instanceof HFragment) ((HFragment) peak).onFragmentHided();//1404-10-02
        } else if (count == 0) {
            if (!home.isHidden()) {
                ft.hide(home);
                home.onFragmentHided();
            }
            if (!mapPage.isHidden()) {
                ft.hide(mapPage);
                mapPage.onFragmentHided();
            }
        }
        ft.add(R.id.your_placeholder, mFragment);
        myFragments.push(mFragment);
        if (mFragment instanceof HFragment) ((HFragment) mFragment).onFragmentShown();

        ft.commit();
        currentFragment = mFragment;
        //مشخص نیست این بهش نیاز هست یا قبلا فراخوانی شده؟
        if (currentFragment instanceof HFragment) ((HFragment) currentFragment).onFragmentHided();//1404-10-02
    }

    void doBackOnFragmentStacks(FragmentManager fragmentManager) {
        int backStackCount = myFragments.size();

        FragmentTransaction ft = fragmentManager.beginTransaction();

        if (backStackCount > 0) {
            if (currentFragment.getClass() == HFragment.class)
                ((HFragment) currentFragment).onFragmentRemoved();
            ft.remove(currentFragment);
            if (currentFragment instanceof HFragment) ((HFragment) currentFragment).onFragmentHided();//1404-10-02
            myFragments.pop();
        }
        if (backStackCount > 1) {
            currentFragment = myFragments.peek();
            currentFragment.onResume();//**** Manual Call because of not loosing State - 1400-01-24 added for ClubSearch
            if (currentFragment.isHidden()) {
                ft.show(currentFragment);
            }
            if (currentFragment instanceof HFragment) ((HFragment) currentFragment).onFragmentShown();//1404-10-02
        }
        if (backStackCount <= 1) {
            //اگر همین الان توی صفحه نقشه بودیم
            if (currentFragment != null && currentFragment == mapPage) {
                mapIsShowingOnTop = false;
                ft.hide(mapPage);
                mapPage.onFragmentHided();
                home.onFragmentShown();//1404-10-02
            }
            //Home and MapPage
            if (mapIsShowingOnTop) {
                ft.show(mapPage);
                mapPage.onFragmentShown();//1404-10-02
                if (!home.isHidden()) {
                    ft.hide(home);
                    home.onFragmentHided();
                }
                currentFragment = mapPage;
            } else {
                ft.show(home);
                home.onFragmentShown();//1404-10-02
                if (!mapPage.isHidden()) {
                    ft.hide(mapPage);
                    mapPage.onFragmentHided();
                }
                currentFragment = home;
            }
            currentFragment.onResume();
        }
        ft.commit();

        //خط زیر مشخص نیست نیازی بهش هست یا خیر؟
        if (currentFragment instanceof HFragment) ((HFragment) currentFragment).onFragmentShown();

    }

    @Override
    public void onBackPressed() {
        try {

            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fr = currentFragment;//fragmentManager.findFragmentById(R.id.your_placeholder);
            int backStackCount = myFragments.size();
            Log.e(Tag, "BackCount(B): " + Integer.toString(backStackCount));
            if (backStackCount > 0) {
                boolean backAllowed = true;
                if (fr instanceof HFragment) {
                    backAllowed = ((HFragment) fr).onBackPressedInChild();
                } else if (fr instanceof MapSelect) {
                    backAllowed = ((MapSelect) fr).onBackPressed();
                } else if (fr instanceof MyTracks) {
                    backAllowed = ((MyTracks) fr).onBackPressed();
                } else if (fr instanceof PleaseRegister) {
                    backAllowed = ((PleaseRegister) fr).onBackPressed();
                } else if (fr instanceof Register) {
                    backAllowed = ((Register) fr).onBackPressed();
                }
                if (backAllowed) {
                    doBackOnFragmentStacks(fragmentManager);
                }
            } else {
                if (fr instanceof Home) {
                    projectStatics.showDialog(MainActivity.this, getResources().getString(R.string.AreYouSureToExit_Title)
                            , getResources().getString(R.string.AreYouSureToExit_Desc),
                            getResources().getString(R.string.ok), view -> {
                                this.finish();
                            }, getResources().getString(R.string.cancel)
                            , null);
                } else if (fr instanceof MapPage) {
                    boolean backAllowed = ((MapPage) fr).onBackPressedInChild();
                    if (backAllowed) {
                        doBackOnFragmentStacks(fragmentManager);
                    }
                }
            }

        } catch (Exception ex) {
            Log.e(Tag, "خطای اکسپشن : " + ex.getMessage());
            ex.printStackTrace();
            TTExceptionLogSQLite.insert(ex.getMessage(),stktrc2k(ex), PrjConfig.frmMainActivity, 1500);
        }
    }

    void showHideMainActivityOnNavigation(boolean show) {
        //HHH1400-08-11
//        if (mainLinearLayout != null) {
//            mainLinearLayout.setVisibility(show ? View.VISIBLE : View.GONE);
//            coordinator.setVisibility(show ? View.VISIBLE : View.GONE);
//        }
    }
    //end added for navigation


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2

        //کد زیر بعد از تفکیک مین اکتیویتی کامنت شد چون اصلا فراخوانی نمی شد
//        try {
//
//            //Open in Showing Bounds on Map:
//            if (requestCode == 1100) {
//                setResultFromMapSelect(data);
//            }
//            //Open in Edit Modes:
//            if (requestCode == ResultCode_ForMyTracks) {
//                //Not working after fragmenting...
//                if (data != null && data.getExtras() != null) {
//                    if (data.hasExtra("NbPoiId")) {
//                        //Open for Edit mode
//                        long NbPoiId = data.getLongExtra("NbPoiId", 0);
//                        showNbPoiOnMapForEdit(NbPoiId);
//                    }
//                }
//            }
//
//        } catch (Exception ex) {
//            int x = 0;
//        }
    }

    public void openEditTrack(long NbPoiId, String Sender, int positionInParent, MyTracks.NbPoisAdapter _adapter) {
        EditTrack mFragment = EditTrack.getInstance(NbPoiId, Sender, positionInParent, _adapter);
        showFragment(mFragment);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (mapPage != null)
                mapPage.onResumeInChild();


            if (app.session.getIsTrackRecording() == 1) {
                if (!MainActivity.isTrackingServiceRunning) {
                    Intent intent = new Intent(this, LocationTrackingService.class);
                    app.startLocationService(getApplicationContext());
                }
            }

        } catch (Exception ex) {
            Log.e(Tag, "خطای اکسپشن : " + ex.getMessage());
            ex.printStackTrace();
            TTExceptionLogSQLite.insert(ex.getMessage(), stktrc2k(ex), PrjConfig.frmMainActivity, 1501);
        }
    }

    @Override
    protected void onDestroy() {
        if (mapPage != null)
            mapPage.onDestroyInChild();
        super.onDestroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_UP) {

            // do your work here
            return true;
        } else {
            return false;
        }
    }


    @Override
    protected void onPause() {
        try {
            Log.d(Tag, "رفتن به بک" + "برنامه pause شد - ");

            super.onPause();
            if (mapPage != null)
                mapPage.onPauseInChild();
        } catch (Exception ex) {
            Log.e(Tag, "خطای اکسپشن : " + ex.getMessage());
            ex.printStackTrace();
            TTExceptionLogSQLite.insert(ex.getMessage(), stktrc2k(ex), PrjConfig.frmMainActivity, 1506);
        }
    }
//1400-01-03
//    public boolean isGPSEnabled(Context mContext) {
//        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
//        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//    }


    public void initFirebase() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(MainActivity.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String token = instanceIdResult.getToken();
                Log.i("Firebase Token", token);
                app.session.setFirebaseRegId(token);

                app.updateServerFirebaseToken(token, getApplicationContext());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PrjConfig.Location_FINE_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        //Do something...
                        //از اینجا به بعد آزاد هست که هر کاری بکنه
                        app.isFirstTimeRunning_ForLocationReadingInMapPage = false;
                        Log.e(Tag, "   رفتن به چک لوکیشن");
                        mapPage.checkLocation(false);
                    } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        //if permission denied previously
                        projectStatics.showDialog(this
                                , this.getResources().getString(R.string.locationPermission_Denied)
                                , this.getResources().getString(R.string.locationPermission_Denied_Desc)
                                , this.getResources().getString(R.string.ok), view -> {
                                    hutilities.showAppSettingToChangePermission(this);
                                }, "", null);
                    }
                }
                break;
            }
            case PrjConfig.MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE:
            case PrjConfig.MY_PERMISSIONS_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        //Do something...
                    } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        //if permission denied previously
                        projectStatics.showDialog(this
                                , this.getResources().getString(R.string.StoragePermission_Denied)
                                , this.getResources().getString(R.string.StoragePermission_Denied_Desc)
                                , this.getResources().getString(R.string.ok), view -> {
                                    hutilities.showAppSettingToChangePermission(this);
                                }, "", null);
                    }
                }
                break;
            }
//            case PrjConfig.POST_NOTIFICATIONS_REQUEST_CODE: {
//                if (grantResults.length > 0) {
//                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                        //Do something...
//                    } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
//                        //if permission denied previously
//                        projectStatics.showDialog(this
//                                , this.getResources().getString(R.string.notifPermission_DeniedTitle)
//                                , this.getResources().getString(R.string.notifPermission_Desc)
//                                , this.getResources().getString(R.string.ok), view -> {
//                                    hutilities.showAppSettingToChangePermission(this);
//                                }, "", null);
//                    }
//                }
//                break;
//            }
        }
        //New Record Track:
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void doCheckVersion() {
        MobileInfoDTO info = MobileInfoDTO.instance();
        Call<InsUpdRes> call = app.apiMap.CheckVersion2(info);
        call.enqueue(new Callback<InsUpdRes>() {
            @Override
            public void onResponse(Call<InsUpdRes> call, Response<InsUpdRes> response) {
                try {
                    if (response.isSuccessful()) {
                        InsUpdRes res = response.body();
                        if (res != null && res.isOk == false) {
                            String[] commandParts = res.command.split(";;;");

                            String title = res.resValue;
                            String message = res.message;

                            if (commandParts[0].equals("1")) {
                                //1: Open Message Box
                                projectStatics.showDialog(MainActivity.this
                                        , title//getResources().getString(R.string.updateAvailable)
                                        , message
                                        , commandParts[1]//getResources().getString(R.string.ok)
                                        , view -> {
                                            String whatToDo = commandParts[2];
                                            String data = commandParts[3];
                                            if (whatToDo.equals("2")) {
                                                //Open Link
                                                String link = data;
                                                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                                        Uri.parse(link));
                                                startActivity(browserIntent);
                                            } else if (whatToDo.equals("3")) {
                                                //Open "THIS APP" In Google Play
                                                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                                try {
                                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                                } catch (
                                                        android.content.ActivityNotFoundException anfe) {
                                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                                }
                                            } else {
                                                //Not Imp Yet
                                            }
                                        }
                                        , commandParts.length > 4 ? commandParts[4] : ""
                                        , view -> {
                                            if (commandParts.length > 4) {
                                                String whatToDo = commandParts[5];
                                                String data = commandParts[6];
                                                if (whatToDo.equals("2")) {
                                                    //Open Link
                                                    String link = data;
                                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                                            Uri.parse(link));
                                                    startActivity(browserIntent);
                                                } else if (whatToDo.equals("3")) {
                                                    //Open "THIS APP" In Google Play
                                                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                                    try {
                                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                                    } catch (
                                                            android.content.ActivityNotFoundException anfe) {
                                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                                    }
                                                } else {
                                                    //Not Imp Yet
                                                }
                                            }
                                        });
                            } else {
                                //Not Imp in this Vesion
                            }

                        }
                    }
                } catch (Exception ex) {
                    Log.e("خطا چک_به_روز_رسانی", ex.getMessage());
                    ex.printStackTrace();
                    TTExceptionLogSQLite.insert(ex.getMessage(), stktrc2k(ex), PrjConfig.frmMainActivity, 1503);
                }
            }

            @Override
            public void onFailure(Call<InsUpdRes> call, Throwable t) {
                int x = 0;
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //همزمان در oncreate-onNewIntent
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("NotificationMessage")) {
                showNotificationAtMain(extras);
            }
        }
        processIntent(intent);
        //همزمان در onCreate-onNewIntent
        if ((recievedFileName != null && recievedFileName.length() > 0) || inputStream != null) {
            showFragment(MyTracks.getInstance("start", recievedFileName, "", inputStream));
            inputStream = null;
            recievedFileName = "";
        }
    }


    private void showNotificationAtMain(Bundle extras) {
        try {
            Gson gson = new Gson();
            FmMessage message = gson.fromJson(extras.getString("FmMessage"), FmMessage.class);
            String msg = extras.getString("NotificationMessage");
            String title = extras.getString("NotificationTitle");
            String ChanalId = extras.getString("ChanalId");

            if (ChanalId == null || (ChanalId != null && !ChanalId.equals(PrjConfig.NOTIF_CHANAL_SYSTEM_ID))) {

                //اگه لاگ این نبود، ساید وجود نداره در نتیجه فقط باید پیام رو نشون بدیم و خلاص
                if (app.session.isLoggedIn()) {
                    if (message.OpenAction == FmMessage.OpenAction_OpenLink) {
                        OpenFmMessageCommand(message);
                        showFragment(new SideList());
                    } else if (message.OpenAction == FmMessage.OpenAction_OpenInApp) {
                        OpenFmMessageCommand(message);
                    } else {
                        showFragment(new SideList());
                    }
                } else {
                    projectStatics.showDialog(MainActivity.this, title, msg
                            , message.OpenAction == FmMessage.OpenAction_NormalMessage
                                    || message.OpenAction == FmMessage.OpenAction_None ? getResources().getString(R.string.ok) : getResources().getString(R.string.view)
                            , view -> {
                                OpenFmMessageCommand(message);
                            }
                            , "", null);
                }
            }
            Log.e("NewMessageOnMain", msg);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void logOutUser(MainActivityManager current) {
        Handler handler = new Handler(Looper.getMainLooper());
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    dbConstantsMap.appDB.NbMapDao().deleteAll();
                    app.session.setVisitCounter(0);
                    dbConstantsTara.appDB.TTClubNameDao().deleteAll();
                    dbConstantsTara.appDB.TTClubTourCategoryDao().deleteAll();
                    dbConstantsTara.appDB.CityDao().deleteAll();
                    dbConstantsTara.appDB.FmMessageDao().deleteAll();
                    dbConstantsMap.appDB.NbPoiDao().deleteAll();
                    dbConstantsMap.appDB.NbCurrentTrackDao().deleteAll();
                    dbConstantsMap.appDB.NbMapDao().deleteAll();


                    //Start Deleting Files
                    hMapTools.deleteMapsFolder(current);
                    hMapTools.deleteTilesFolder(current);

                    //Delete Private Folder
//                    ContextWrapper cw = new ContextWrapper(getApplicationContext());
//                    File directory = cw.getDir(PrjConfig.PrivateFolder, Context.MODE_PRIVATE);
                    File directory = getApplicationContext().getFilesDir();
                    Log.e("حذف حافظه", directory.getAbsolutePath());

                    if (directory.exists()) {
                        hutilities.deleteRecursive(directory);
                    }
                } catch (Exception ex) {
                }
            }
        });
        thread.start();

        if (current.getClass() == MainActivity.class) {
            MainActivity mainActivity = (MainActivity) current;
            FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
            int backStackCount = fragmentManager.getBackStackEntryCount();
            while (backStackCount > 0) {
                backStackCount--;
                doBackOnFragmentStacks(fragmentManager);
            }
            mainActivity.showFragment(new PleaseRegister());
        }
//        Intent i = new Intent(_context, PleaseRegister.class);
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        if (current != null)
//            current.finish();
//        _context.startActivity(i);

    }

    @Override
    public void OpenInAppCommand(String command) {
        String[] parts = command.split(";");
        if (parts.length >= 2) {

            long param1 = 0l;
            try {
                param1 = Long.parseLong(parts[0]);
            } catch (NumberFormatException e) {
                param1 = 0l;
            }
            long param2 = 0l;
            try {
                param2 = Long.parseLong(parts[1]);
            } catch (NumberFormatException e) {
                param2 = 0l;
            }
            if (param1 == PrjConfig.frmTourShowOne) {
                if (param2 > 0) {
                    showFragment(TourShowOne.getInstance(param2));
                }
            } else if (param1 == PrjConfig.frmClubView_Home) {
                if (param2 > 0) {
                    showFragment(ClubView_Home.getInstance((int) param2, PrjConfig.frmMainActivity));
                }
            } else if (param1 == PrjConfig.frmMapSelect) {
                //if (param2 > 0){
                MapSelect page = MapSelect.getInstance(true);
                showFragment(page);
                //}
            } else if (param1 == PrjConfig.frmSafeGpxView) {
                if (param2 > 0) {
                    SafeGpxView frm = SafeGpxView.getInstance((int) param2, 1);
                    showFragment(frm);
                }
            } else if (param1 == PrjConfig.frmSafeGpxSearch) {
                if (parts.length > 2) {
                    try {
                        double latitude = Double.parseDouble(parts[1]);
                        double longitude = Double.parseDouble(parts[2]);
                        NbPoi poi = NbPoi.getPoiForPosition(latitude, longitude, getString(R.string.Position));

                        SafeGpxSearch frm = SafeGpxSearch.getInstance("", poi);
                        showFragment(frm);
                    } catch (Exception ex) {
                        Log.e("خطا بازکردن_ترکها", ex.getMessage());
                        ex.printStackTrace();
                        TTExceptionLogSQLite.insert(ex.getMessage(), stktrc2k(ex), PrjConfig.frmMainActivity, 1501);
                    }
                } else {
                    Log.e("خطا بازکردن_ترکها", "بخش ها ناکافی");
                    TTExceptionLogSQLite.insert("بخش ها ناکافی", "پارامتر ارسالی : " + command, PrjConfig.frmMainActivity, 1502);
                }
            }
        }
    }

    @Override
    public void OpenFmMessageCommand(FmMessage currentObj) {
        Log.e("NewMessageOnMain", "OpenFmMessageCommand_OpenAction :" + currentObj.OpenAction + " currentObj.ActionParam:" + currentObj.ActionParam);
        if (currentObj.OpenAction == FmMessage.OpenAction_OpenInApp) {
            String command = currentObj.ActionParam;
            //Check if it is a tour or something like that
            OpenInAppCommand(command);
        }
        if (currentObj.OpenAction == FmMessage.OpenAction_OpenLink) {
            String command = currentObj.ActionParam;
            String[] parts = command.split("\\*" + "\\*" + "\\*");
            if (parts.length > 0) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(parts[0]));
                startActivity(browserIntent);
            }
        }
    }

    @Override
    public void showLoginProcess(String mode) {
        showFragment(new Register(mode), false);
    }

    public MapPage mapPage;
    Home home;

    public void setResultFromMapSelect(Intent data) {
        if (mapPage != null)
            mapPage.setResultFromMapSelect(data);
    }

    public void showNbPoiOnMapForEdit(long NbPoiId, String DefaultName) {
        if (mapPage != null)
            mapPage.showNbPoiOnMapForEdit(NbPoiId, DefaultName);
    }

    public void btnAddWaypoint_Click(LatLng point) {
        if (mapPage != null)
            mapPage.btnAddWaypoint_Click(point);
    }

    public void setMenuVisibility(boolean reloadHome) {
        if (mapPage != null)
            mapPage.setMenuVisibility();
        if (reloadHome) {
            home.setMyClubNameAndIcon();
            int currentClubId = dbConstantsTara.session.getMyClubNameIds();
            home.readNextToursForMyClub(currentClubId);
        }
    }

    public void locationChangedFromMapPage(Location location) {

    }
    private static final String REDRAW_POIS_ON_STATE = "redraw";
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(REDRAW_POIS_ON_STATE, 1);//POIHIDE
//        getSupportFragmentManager().putFragment(savedInstanceState, "mapPage", mapPage);
//        getSupportFragmentManager().putFragment(savedInstanceState, "home", home);

        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
//        savedInstanceState.putBoolean("MyBoolean", true);
//        savedInstanceState.putDouble("myDouble", 1.9);
//        savedInstanceState.putInt("MyInt", 1);
//        savedInstanceState.putString("MyString", "Welcome back to Android");
//        savedInstanceState.putParcelable("parcelable", "");
//        savedInstanceState.putSerializable("serializable", "");
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
//        mapPage = (MapPage) getSupportFragmentManager().getFragment(savedInstanceState, "mapPage");
//        home = (Home) getSupportFragmentManager().getFragment(savedInstanceState, "home");

        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
//        boolean myBoolean = savedInstanceState.getBoolean("MyBoolean");
//        double myDouble = savedInstanceState.getDouble("myDouble");
//        int myInt = savedInstanceState.getInt("MyInt");
//        String myString = savedInstanceState.getString("MyString");
    }

    public MainActivity(){
        Tag = "MainActiviry";
    }


    //------ قسمت های مربوط به سرویس لوکیشن در 1404-08 ----------

    public static boolean isTrackingServiceBound = false;
    //متغیری برای این که مشخص بکنه که سرویس شروع به کار کرده یا نه
    public static boolean isTrackingServiceRunning = false;

    public static ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            LocationTrackingService.LocalBinder binder = (LocationTrackingService.LocalBinder) service;
            app.mTrackInBackgroundService = binder.getService();
            Log.e("TrkService", "سریس سابق شناسایی شد");
            isTrackingServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isTrackingServiceBound = false;
        }
    };
    @Override
    protected void onStart() {
        super.onStart();
        // سعی کن دوباره bind بشی در صورت وجود سرویس
        if (!MainActivity.isTrackingServiceBound) {
            Intent intent = new Intent(this, LocationTrackingService.class);
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            MainActivity.isTrackingServiceBound = true;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isTrackingServiceBound && !isTrackingServiceRunning) {
            Log.e(Tag, "Unbound Service in onStop اجراشد");
            unbindService(serviceConnection);//این خط منجر به بسته شدن کامل سرویس میشه
            isTrackingServiceBound = false;
        }
    }

    //------ اتمام قسمت های مربوط به سرویس لوکیشن در 1404-08 ----------
}
