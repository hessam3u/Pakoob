package mojafarin.pakoob;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.takusemba.spotlight.OnSpotlightEndedListener;
import com.takusemba.spotlight.OnSpotlightStartedListener;
import com.takusemba.spotlight.SimpleTarget;
import com.takusemba.spotlight.Spotlight;

import java.io.File;
import java.util.List;

import FmMessage.SideList;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import bo.NewClasses.SimpleRequest;
import bo.NewClasses.StringContentDTO;
import bo.dbConstantsTara;
import bo.entity.NbAdv;
import bo.entity.NbAdvList;
import bo.sqlite.NbAdvSQLite;
import bo.sqlite.TTExceptionLogSQLite;
import okhttp3.ResponseBody;
import pakoob.ClubSearch;
import pakoob.ClubView_Home;
import pakoob.SelectClubDialogForMyClub;
import pakoob.TourList;
import pakoob.TourListComponent;
import pakoob.TourShowOne;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import user.CompleteRegister;
import user.Register;
import UI.HFragment;
import utils.ImageTools;
import utils.PrjConfig;
import utils.hutilities;
import utils.projectStatics;

public class Home extends HFragment {
    String pageKey = "";

    public Home() {
        app.session.setOpenHomeAtStartup(1);
    }
    public static Home getInstance(){
        Home res = new Home();
        return res;
    }

    Location myHomeLastLocation = null;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {//4th Event
        super.onViewCreated(view, savedInstanceState);

        initializeComponents(view);
        try {
            fillForm();

            //رفت به onResume
//            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(context, new String[]{
//                        Manifest.permission.POST_NOTIFICATIONS
//                }, PrjConfig.POST_NOTIFICATIONS_REQUEST_CODE);
//            }

        } catch (Exception ex) {
            Log.e("خطا در لود", ex.getMessage());
            ex.printStackTrace();
            TTExceptionLogSQLite.insert("خطا در لود " + ex.getMessage(), stktrc2k(ex), PrjConfig.frmHome, 1501);
        }

//        if (showHelpAfterLoad) {
//            view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override public void onGlobalLayout() {
//                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                    ShowHelpSpotlight();
//                }
//            });
//        }
    }
    private void askNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                // نمایش دیالوگ توضیحی قبل از درخواست واقعی
                projectStatics.showDialog(context, getResources().getString(R.string.notifPermission_DeniedTitle)
                        , getResources().getString(R.string.notifPermission_Desc_Before)
                        , getResources().getString(R.string.ok)
                        , view -> {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                        }, context.getString(R.string.Later), view -> {
                            app.session.setAsked_notification_permission(true);
                        });

            } else {
                // قبلاً اجازه داده شده
                app.session.setAsked_notification_permission(true);
            }
        } else {
            // برای اندرویدهای پایین نیازی نیست
            app.session.setAsked_notification_permission(true);
        }
    }

    private void showGoToSettingsDialog() {
        projectStatics.showDialog(context, getResources().getString(R.string.notifPermission_DeniedTitle)
                , getResources().getString(R.string.notifPermission_Desc_After)
                , getResources().getString(R.string.OpenSetting)
                , view -> {
                    openAppNotificationSettings();
                }, context.getString(R.string.Later), view -> {});
    }

    private void openAppNotificationSettings() {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().getPackageName());
        } else {
            // برای اندرویدهای پایین‌تر
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + requireContext().getPackageName()));
        }
        startActivity(intent);
    }
    private final ActivityResultLauncher<String> notificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                app.session.setAsked_notification_permission(true);

                if (isGranted) {
                    Toast.makeText(requireContext(), "دسترسی اعلان فعال شد ✅", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "اعلان‌ها غیرفعال ماندند ⚠️", Toast.LENGTH_SHORT).show();
                    showGoToSettingsDialog();
                }
            });


    private void fillForm() {
        setMyClubNameAndIcon();

        int currentClubId = dbConstantsTara.session.getMyClubNameIds();
        readNextToursForMyClub(currentClubId);
        loadAdvs();
    }

    public void readNextToursForMyClub(int currentClubId){
        if (currentClubId == 0) {
            rvNextTours.setVisibility(View.GONE);
            lblMyClubMessage.setVisibility(View.VISIBLE);
            lblMyClubMessage.setText("لطفا با ضربه زدن روی این پیام، باشگاه خود را انتخاب نمایید.");
            lblMyClubMessage.setOnClickListener(view -> {
                btnGotoMyClub_Click(false);
            });
        } else {
            rvNextTours.setVisibility(View.VISIBLE);
            lblMyClubMessage.setVisibility(View.GONE);
            lblMyClubMessage.setOnClickListener(null);
            rvNextTours.readAndShow(TourListComponent.CacheDBId_MyClub, currentClubId, TourListComponent.CategoryTypesToShow_All, TourListComponent.SORT_Default, 100);
        }
    }
    boolean askForHelpAfterLoading = false;
    @Override
    public void onFragmentShown(){
        Log.e(tag(), "Home is Shown");
        boolean homeHelpSeen = app.session.getHomeHelpSeen();
        askForHelpAfterLoading = !homeHelpSeen;



//        if (initCompleted && showHelpAfterLoad){
//            ShowHelpSpotlight();
//            showHelpAfterLoad = false;
//        }
//        else{
//            showHelpAfterLoad = true;
//        }
    }

    public void ShowHelpSpotlight(){
        SimpleTarget btnGotoMapTarget = new SimpleTarget.Builder(context)
                .setPoint(btnGotoMap) // position of the Target. setPoint(Point point), setPoint(View view) will work too.
                .setRadius(240f) // radius of the Target
                .setTitle("به سادگی مسیریابی کن!") // title
                .setDescription("امکاناتی مثل دانلود نقشه ها، ثبت ترک و نقطه و ده ها قابلیت مرتبط به مسیریابی در این منو پیدا میشه") // description
                .build();
        SimpleTarget btnGotoClassesTarget = new SimpleTarget.Builder(context)
                .setPoint(btnGotoClasses) // position of the Target. setPoint(Point point), setPoint(View view) will work too.
                .setRadius(240f) // radius of the Target
                .setTitle("دوره هات رو بگذرون!") // title
                .setDescription("دوره ها و کلاس های در حال ثبت نام باشگاه های معتبر کشور در این منو قرار گرفته") // description
                .build();
        SimpleTarget btnGotoToursTarget = new SimpleTarget.Builder(context)
                .setPoint(btnGotoTours) // position of the Target. setPoint(Point point), setPoint(View view) will work too.
                .setRadius(240f) // radius of the Target
                .setTitle("بریم برنامه؟") // title
                .setDescription("برنامه های آتی باشگاه ها و گروه های معتبر سراسر کشور در این صفحه قرار گرفته.") // description
                .build();
        SimpleTarget btnHome_ClubsTarget = new SimpleTarget.Builder(context)
                .setPoint(btnHome_Clubs) // position of the Target. setPoint(Point point), setPoint(View view) will work too.
                .setRadius(240f) // radius of the Target
                .setTitle("باشگاه یاب") // title
                .setDescription("هر باشگاه معتبر اینجا یک صفحه داره. کافیه اون رو جستجو و دنبال کنی، یا طرفدارش بشی تا پیام های مربوط به برنامه هاش برات بیاد") // description
                .build();
//        SimpleTarget lblMyClubNameTarget = new SimpleTarget.Builder(context)
//                .setPoint(lblMyClubName) // position of the Target. setPoint(Point point), setPoint(View view) will work too.
//                .setRadius(240f) // radius of the Target
//                .setTitle("باشگاه اصلیت کدومه؟") // title
//                .setDescription("باشگاه محبوبت رو در اینجا اضافه کن تا برنامه ها و کلاس هاش رو همیشه ببینی") // description
//                .build();
        Spotlight.with(context)
                .setDuration(250L) // duration of Spotlight emerging and disappearing in ms
                .setAnimation(new DecelerateInterpolator(5f)) // animation of Spotlight
                .setTargets(btnGotoMapTarget ,btnGotoClassesTarget, btnGotoToursTarget, btnHome_ClubsTarget
                ) // set targes. see below for more info
        .setOnSpotlightStartedListener(new OnSpotlightStartedListener() { // callback when Spotlight starts
            @Override
            public void onStarted() {
            }
        })
                .setOnSpotlightEndedListener(new OnSpotlightEndedListener() { // callback when Spotlight ends
                    @Override
                    public void onEnded() {
                        app.session.setHomeHelpSeen(true);
                        projectStatics.showDialog(context,getResources().getString(R.string.YouCanReadHelp_Title), getResources().getString(R.string.YouCanReadHelp_Desc), getResources().getString(R.string.ok),null, null, null);
                    }
                })
                .start(); // start Spotlight
    }

    private void loadAdvs() {
        if (!hutilities.isInternetConnected(context)) {
            offlineLoadAdv();
            return;
        }
        SimpleRequest request = SimpleRequest.getInstance(StringContentDTO.getInstance("AdvBoxNo < 10***"));
        Call<ResponseBody> call = app.apiMap.GetAdvs(request);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!isAdded()) return;
                try {
                    Log.e("Add_Cache", "شروع " + response.code());
                    if (response.isSuccessful()) {
                        NbAdvList result = NbAdvList.fromBytes(response.body().bytes());
                        Log.e("Add_Cache", "نتیجه سرور " + result.resList.size() + " RES:" + result.isOk);

                        if (result.isOk) {
                            //int size =result.resList.size();
                            updateCache(result.resList, getResources());
                        }
                    } else {
                        offlineLoadAdv();
                    }
                } catch (Exception ex) {
                    Log.e("Add_Cache", "خخخخ " + ex.getMessage() + " " + ex.getStackTrace());
                    TTExceptionLogSQLite.insert(ex.getMessage(), stktrc2k(ex), PrjConfig.frmHome, 101);
                    offlineLoadAdv();
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                TTExceptionLogSQLite.insert(t.getMessage(), stktrc2kt(t), PrjConfig.frmHome, 100);
                if (!isAdded()) return;
                offlineLoadAdv();
            }
        });
    }

    private void offlineLoadAdv() {
        List<NbAdv> oldItems = NbAdvSQLite.selectAllForHome();
        int oldSize = oldItems.size();
        Resources r = getResources();
        for (int i = 0; i < oldSize; i++) {
            showOldAdv(oldItems.get(i), r);
        }
    }

    public void updateCache(List<NbAdv> recItems, Resources r) {
        List<NbAdv> oldItems = NbAdvSQLite.selectAllForHome();
        int oldSize = oldItems.size();
        int nSize = recItems.size();
        boolean[] oldItemsToDelete = new boolean[oldSize];
        for (int i = 0; i < oldSize; i++) {
            oldItemsToDelete[i] = true;
        }
        for (int i = 0; i < nSize; i++) {
            NbAdv rec = recItems.get(i);
            if (rec.AdvShowType != NbAdv.AdvShowType_Photo)
                continue;
            boolean find = false;
            for (int j = 0; j < oldSize; j++) {
                NbAdv old = oldItems.get(j);
                if (rec.NbAdvId == old.NbAdvId) {
                    long nMils =rec.getRecUpdateDate().getTimeInMillis();
                    long oMils = old.getRecUpdateDate().getTimeInMillis();
                    if (nMils > oMils
                            || !ImageTools.fileExists(context.getFilesDir() + "/" + PrjConfig.AdvFolder +"/" +rec.PhotoAddress.substring(rec.PhotoAddress.lastIndexOf("/") + 1))) {
                        Log.e("Add_Cache", "نیاز به به روز رسانی " + rec.NbAdvId + "- " + nMils + "-" + oMils);
                        SaveAndShowNewAdv(rec, r);
                    } else {
                        Log.e("Add_Cache", "بدون تغییر " + rec.NbAdvId);
                        oldItemsToDelete[j] = false;
                        showOldAdv(rec, r);
                    }
                    find = true;
                    break;
                }
            }
            if (!find) {
                Log.e("Add_Cache", "جدید است " + rec.NbAdvId);
                SaveAndShowNewAdv(rec, r);
            }
        }
        for (int i = 0; i < oldSize; i++) {
            NbAdv oldItem = oldItems.get(i);
            if (oldItemsToDelete[i] == true) {
                NbAdvSQLite.delete(oldItem);
                String path = getContext().getFilesDir().getAbsolutePath() + "/" + PrjConfig.AdvFolder + "/" + oldItem.PhotoAddress.substring(oldItem.PhotoAddress.lastIndexOf("/") + 1);
                File file = new File(path);
                boolean isDeleted = file.delete();
                Log.e("Add_Cache", "حذف شد " + oldItem.NbAdvId + " RES:" + isDeleted);
            }
        }
    }

    public void SaveAndShowNewAdv(NbAdv rec, Resources r) {
        NbAdvSQLite.insert(rec);
        String filename = rec.PhotoAddress.substring(rec.PhotoAddress.lastIndexOf("/") + 1);
        LinearLayout advBoxToShow = advBox1;
        if (rec.AdvBoxNo == 2)
            advBoxToShow = advBox2;
        else if (rec.AdvBoxNo == 3)
            advBoxToShow = advBox3;
        ImageView imageView = getAdvBoxImageView(advBoxToShow, rec, r);
        Log.e("Add_Cache", "Try Load " + rec.PhotoAddress + " With file name : " + filename);

        final Target target = ImageTools.picassoImageTarget(context, context.getFilesDir() + "/" + PrjConfig.AdvFolder, filename, imageView);
        imageView.setTag(target);
        Picasso.get().load(rec.PhotoAddress).into(target);//#PicassoUpdate140303 with(context)
    }

    public void showOldAdv(NbAdv rec, Resources r) {
        String filename = rec.PhotoAddress.substring(rec.PhotoAddress.lastIndexOf("/") + 1);
        LinearLayout advBoxToShow = advBox1;
        if (rec.AdvBoxNo == 2)
            advBoxToShow = advBox2;
        else if (rec.AdvBoxNo == 3)
            advBoxToShow = advBox3;
        ImageView imageView = getAdvBoxImageView(advBoxToShow, rec, r);
        String path = getContext().getFilesDir().getAbsolutePath() + "/" + PrjConfig.AdvFolder
                + "/" + filename;
        File myImageFile = new File(path);
        imageView.setImageBitmap(BitmapFactory.decodeFile(myImageFile.getPath()));
    }

    public void clearAdvCache() {
        List<NbAdv> list = NbAdvSQLite.selectAllForHome();
        String path = getContext().getFilesDir().getAbsolutePath() + "/" + PrjConfig.AdvFolder
                + "/" ;

        for (int i = 0; i < list.size(); i++) {
            NbAdv adv = list.get(i);
            NbAdvSQLite.delete(adv);
            final File file = new File(path + adv.PhotoAddress);
            file.delete();
        }
    }

    private ImageView getAdvBoxImageView(LinearLayout linToAdd, NbAdv adv, Resources r) {
        ImageView image = new ImageView(context);
//        Bitmap bm = BitmapFactory.decodeResource(getResources(),R.drawable.advsample1);
//        image.setImageBitmap(bm);
        LinearLayout.LayoutParams vp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        vp.setMargins(0, 0, 0, hutilities.getPixelFromDP(r, R.dimen.leftrightMargin) / 4);
        image.setAdjustViewBounds(true);
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        image.setLayoutParams(vp);
        linToAdd.addView(image);
        linToAdd.setVisibility(View.VISIBLE);
        linToAdd.setOnClickListener(view -> {
            if (adv.Link.toLowerCase().startsWith("http")){
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(adv.Link));
                startActivity(browserIntent);
            }
            else{
                context.OpenInAppCommand(adv.Link);
            }
        });
        return image;
    }

    TourListComponent rvNextTours;
    LinearLayout btnGotoMap, btnGotoTours, btnGotoClasses, btnGotoMyClub, btnHome_Profile, btnHome_Clubs, btnHomeWeatherAndTracks, btnHome_messages, btnHome_settings, btnHome_StartRecording;
    LinearLayout btnMyTracks, btnMapSelect, btnHelp, btnContactUs;
    ImageView btnMyTracks_img, btnMapSelect_img, btnHelp_img, btnContactUs_img;
    TextView lblMyClubName, imgMyClubLogo_Empty, btnChangeClub;
    ImageView imgMyClubLogo;
    TextView lblMyClubMessage;
    BottomNavigationView bottomNavigation;
    ViewPager viewPager;

    LinearLayout advBox1, advBox2, advBox3;
    View pageView;

    @Override
    public void initializeComponents(View v) {
        hutilities.forceRTLIfSupported((AppCompatActivity) context);
        pageView = v;
        //For Tab:
        Log.d("Home_Tab", "vp شد");
        viewPager = v.findViewById(R.id.viewpagerForHomeTours);
        viewPager.setAdapter(new HomeCurrentTourTabManager(getFragmentManager(), context));
        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.tabForHomeTours);
        tabLayout.setupWithViewPager(viewPager);

        advBox1 = v.findViewById(R.id.advBox1);
        advBox2 = v.findViewById(R.id.advBox2);
        advBox3 = v.findViewById(R.id.advBox3);
        advBox1.setVisibility(View.GONE);
        advBox2.setVisibility(View.GONE);
        advBox3.setVisibility(View.GONE);

        bottomNavigation = v.findViewById(R.id.bott_nav_view);
        bottomNavigation.setItemIconTintList(null);//******************** برای نمایش رنگی آیکون ها
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            return BottomMenuClick(item);
        });

        rvNextTours = v.findViewById(R.id.rvNextTours);
        rvNextTours.setOnItemClickListener((post, Position) -> {
            context.showFragment(TourShowOne.getInstance(post, Position));
        });

        lblMyClubMessage = v.findViewById(R.id.lblMyClubMessage);

        btnGotoMap = v.findViewById(R.id.btnGotoMap);
        btnGotoMap.setOnClickListener(view -> {
            ((MainActivity) context).backToMapPage();
        });
        btnGotoTours = v.findViewById(R.id.btnGotoTours);
        btnGotoTours.setOnClickListener(view -> {
            context.showFragment(new TourList(TourList.MODE_Tour, ""));
        });
        btnGotoClasses = v.findViewById(R.id.btnGotoClasses);
        btnGotoClasses.setOnClickListener(view -> {
//            SafeGpxView frm = SafeGpxView.getInstance(8, 1);
//            context.showFragment(frm);
            context.showFragment(new TourList(TourList.MODE_Class, ""));
        });

        btnHome_Clubs = v.findViewById(R.id.btnHome_Clubs);
        btnHome_Clubs.setOnClickListener(view -> {
            context.showFragment(new ClubSearch());
        });
        btnHomeWeatherAndTracks = v.findViewById(R.id.btnHomeWeatherAndTracks);
        btnHomeWeatherAndTracks.setOnClickListener(view -> {
            projectStatics.showDialog(context,getResources().getString(R.string.Home_WeatherAndTracks_Title)
                    , getResources().getString(R.string.Home_WeatherAndTracks_Desc), getResources().getString(R.string.ok)
                    , view1 ->{((MainActivity)context).backToMapPage();}
                    , "", null);

        });

//        btnHome_messages = v.findViewById(R.id.btnHome_messages);
//        btnHome_messages.setOnClickListener(view -> {context.showFragment(new SideList());});
//        btnHome_settings = v.findViewById(R.id.btnHome_settings);
//        btnHome_settings.setOnClickListener(view -> {context.showFragment(new SettingsActivity());});

//        btnHome_StartRecording = v.findViewById(R.id.btnHome_StartRecording);
//        btnHome_StartRecording.setOnClickListener(view -> {context.backToMapPageAndShowNewTrackDialog();});

//        btnMyTracks = v.findViewById(R.id.btnMyTracks);
//        btnMyTracks_img = v.findViewById(R.id.btnMyTracks_img);
//        View.OnClickListener btnMyTracks_click = view ->{context.showFragment(new MyTracks("menu", "", null));};
//        btnMyTracks.setOnClickListener(btnMyTracks_click);
//        btnMyTracks_img.setOnClickListener(btnMyTracks_click);

        btnGotoMyClub = v.findViewById(R.id.btnGotoMyClub);
        btnGotoMyClub.setOnClickListener(view -> {
            btnGotoMyClub_Click(false);
        });
//        btnGotoMyClub.setLongClickable(true);//1400-10-21 commented
//        btnGotoMyClub.setOnLongClickListener(view -> {
//            return false;
//        });


//        btnMapSelect = v.findViewById(R.id.btnMapSelect);
//        btnMapSelect_img = v.findViewById(R.id.btnMapSelect_img);
//        View.OnClickListener btnMapSelect_click = view ->{context.showFragment(new MapSelect());};
//        btnMapSelect.setOnClickListener(btnMapSelect_click);
//        btnMapSelect_img.setOnClickListener(btnMapSelect_click);

//        btnHelp = v.findViewById(R.id.btnHelp);
//        btnHelp_img = v.findViewById(R.id.btnHelp_img);
//        View.OnClickListener btnHelp_click =view -> {context.showFragment(new InfoPage("help"));};
//        btnHelp.setOnClickListener(btnHelp_click);
//        btnHelp_img.setOnClickListener(btnHelp_click);

//        btnContactUs = v.findViewById(R.id.btnContactUs);
//        btnContactUs_img = v.findViewById(R.id.btnContactUs_img);
//        View.OnClickListener btnContactUs_click =view -> {context.showFragment(new InfoPage("contactUs"));};
//        btnContactUs.setOnClickListener(btnContactUs_click);
//        btnContactUs_img.setOnClickListener(btnContactUs_click);

        lblMyClubName = v.findViewById(R.id.lblMyClubName);
        imgMyClubLogo = v.findViewById(R.id.imgMyClubLogo);
        imgMyClubLogo_Empty = v.findViewById(R.id.imgMyClubLogo_Empty);
        btnChangeClub = v.findViewById(R.id.btnChangeClub);

        if (askForHelpAfterLoading){
            projectStatics.showDialog(context,getResources().getString(R.string.ShowHelpAtHome_Title), getResources().getString(R.string.ShowHelpAtHome_Desc), getResources().getString(R.string.Yes), view -> {
                ShowHelpSpotlight();
            }, getResources().getString(R.string.Later), null);
        }
        super.initializeComponents(v);
    }

    private boolean BottomMenuClick(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_profile:
                btnProfile_Click();
                break;
            case R.id.navigation_Messages:
                context.showFragment(new SideList());
                break;
            case R.id.navigation_Map:
                ((MainActivity) context).backToMapPage();
                break;
            case R.id.menu_Masirha:
                context.showFragment(MyTracks.getInstance("menu", "", "", null));
                break;
            case R.id.menu_settings:
                context.showFragment(new SettingsActivity());
                break;
        }
        return false;
    }

    private void btnGotoMyClub_Click(boolean isLongClick) {
        int currentClubId = dbConstantsTara.session.getMyClubNameIds();
        if (isLongClick || currentClubId == 0) {
            SelectClubDialogForMyClub dialogBuilder = new SelectClubDialogForMyClub(this);

            AlertDialog.Builder alertDialogBuilder = dialogBuilder.GetBuilder(context
                    , (selected, position) -> {
                        setMyClubNameAndIcon();
                        Integer nCurrentMyClubId = dbConstantsTara.session.getMyClubNameIds();
                        readNextToursForMyClub(nCurrentMyClubId);
                    }, null, view -> {
                        dialogBuilder.alertDialog.dismiss();
                    });
            // create alert dialog
            dialogBuilder.alertDialog = alertDialogBuilder.create();
            // show it
            dialogBuilder.alertDialog.show();
        } else if (currentClubId > 0) {
            ClubView_Home currentHomeFragment = ClubView_Home.getInstance(currentClubId, 0);
            context.showFragment(currentHomeFragment);
        }
    }

    void setMyClubNameAndIcon() {
        int id = dbConstantsTara.session.getMyClubNameIds();
        String name = dbConstantsTara.session.getMyClubName();
        String logo = dbConstantsTara.session.getMyClubLogo();
        if (id > 0) {
            lblMyClubName.setText(name);
        } else {
            lblMyClubName.setText(context.getString(R.string.MyClub));
        }
        Log.e(tag(), " خواندن عکس"+ logo);
        if (id > 0 && logo.length() > 0) {
            imgMyClubLogo_Empty.setVisibility(View.GONE);
            imgMyClubLogo.setVisibility(View.VISIBLE);

            //باز کردن و نمایش عکس توسط پیکاسو

            String path = getContext().getFilesDir().getAbsolutePath() + "/" + PrjConfig.PrivateFolder;

            int index = logo.lastIndexOf("/");
            File directory = new File(path);
            File myImageFile = new File(directory, logo.substring(index + 1));
            if (!myImageFile.exists()) {
                showDefaultClubLogo();
                Log.e(tag(), " خواندن عکس" + myImageFile.getName() + " پیدا نشد ");
            } else {
                Picasso.get().load(myImageFile).into(imgMyClubLogo);//#PicassoUpdate140303 with(context)
                Log.e(tag(), "A  خواندن عکس : " + myImageFile.getName());
            }

        } else {
            showDefaultClubLogo();
        }
        btnChangeClub.setOnClickListener(view -> {
            btnGotoMyClub_Click(true);
        });
    }

    void showDefaultClubLogo() {
        imgMyClubLogo_Empty.setVisibility(View.VISIBLE);
        imgMyClubLogo.setVisibility(View.GONE);
    }

    private void btnProfile_Click() {
        if (app.session.isLoggedIn())
            context.showFragment(new CompleteRegister("hasback"));
        else
            context.showFragment(new Register("menu"));
    }

    public class HomeCurrentTourTabManager extends FragmentPagerAdapter {
        final int PAGE_COUNT = 2;
        private String tabTitles[] = new String[]{"تازه ترین برنامه ها", "نزدیک ترین برنامه ها"};
        Fragment fragment0;
        Fragment fragment1;

        private Context context;

        public HomeCurrentTourTabManager(FragmentManager fm, Context context) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.context = context;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            Log.d("Home_Tab", "فراخوانی شد");
            switch (position) {
                default:
                case 0: {
                    if (fragment0 == null) {
                        fragment0 = HomeCurrentTourFragment.getInstance(TourListComponent.CategoryTypesToShow_AllMinusLearning
                                , TourListComponent.SORT_Newer, 50);
                    }
                    return fragment0;
                }
                case 1: {
                    if (fragment1 == null) {
                        fragment1 = HomeCurrentTourFragment.getInstance(TourListComponent.CategoryTypesToShow_AllMinusLearning
                                , TourListComponent.SORT_Earlier, 50);
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


    private boolean hasAskedThisSession = false; // تا توی همون سشن دوبار نیاد

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            if (!hasAskedThisSession) { // فقط بار اول در این سشن
                hasAskedThisSession = true;
                boolean askedBefore = app.session.getAsked_notification_permission();
                if (!askedBefore) {
                    askNotificationPermissionIfNeeded();
                } else {
                    // اگر قبلاً رد شده و هنوز دسترسی نداره
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                            ContextCompat.checkSelfPermission(requireContext(),
                                    Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        showGoToSettingsDialog();
                    }
                }
            }


        }

    }
        @Override
    public void onResume() {
        super.onResume();
    }

    //تنظیمات مربوط به صفحه --------------
    @Override
    protected int getScreenId() {return PrjConfig.frmHome;}
    @Override
    protected String tag() {return SCREEN_TAG;}
    public static final String SCREEN_TAG = "Home";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {//3nd Event
        return inflater.inflate(R.layout.frm_home, parent, false);
    }
}
