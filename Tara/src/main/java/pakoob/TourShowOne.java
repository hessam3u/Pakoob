package pakoob;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.pakoob.tara.R;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import androidx.constraintlayout.widget.ConstraintLayout;
import bo.dbConstantsTara;
import bo.NewClasses.SimpleRequest;
import bo.NewClasses.StringContentDTO;
import bo.entity.TTClubTour;
import bo.entity.TourListResult;
import bo.sqlite.TTExceptionLogSQLite;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.HFragment;
import utils.PicassoCircleTransform;
import utils.PicassoTrustAll;
import utils.PrjConfig;
import utils.hutilities;
import utils.projectStatics;

public class TourShowOne extends HFragment {
    int selectedIndex = 0;
    long RecTTClubTourId = 0;
    TTClubTour currentObj = null;
    ScrollView scrollView;

    boolean isInBackgroundLoading = false;
    String backgroundMessage = "";

    public static TourShowOne getInstance(TTClubTour currentObj, int ix){
        TourShowOne res = new TourShowOne();
        res.selectedIndex = ix;
        res.currentObj = currentObj;
        return res;
    }
    public static TourShowOne getInstance(long ClubTourId){
        TourShowOne res = new TourShowOne();
        res.selectedIndex = 0;
        res.currentObj = null;
        res.RecTTClubTourId = ClubTourId;

        return res;
    }
    public TourShowOne() {
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
    void readItemInBackground(){

        isInBackgroundLoading = true;
        backgroundMessage = "";

        showHideEverythingForLoading(true);
        progressBarPage_SetVisibility(View.VISIBLE);
        txtSearchResult_SetText("");

        if (!hutilities.isInternetConnected(context)) {
            projectStatics.showDialog(context, getResources().getString(R.string.NoInternet), getResources().getString(R.string.NoInternet_Desc), getResources().getString(R.string.ok), view -> {}, "", null);
            return;
        }
        StringContentDTO contentDTO = StringContentDTO.getInstance(RecTTClubTourId + "***" + 0);
        SimpleRequest request = SimpleRequest.getInstance(contentDTO);
        Call<ResponseBody> call = dbConstantsTara.apiTara.GetClubTour(request);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!isAdded()) return;
                boolean readCompleted = false;
                try {
                    if (response.isSuccessful()) {
                        TourListResult result = TourListResult.fromBytes(response.body().bytes());
                        if (!result.isOk) {
                            backgroundMessage = result.message;
                        } else {
                            backgroundMessage = "";
                        }
                        if (result.resList.size() > 0) {
                            currentObj = result.resList.get(0);
                            readCompleted = true;
                        } else if (backgroundMessage.length() == 0){
                            backgroundMessage = getResources().getString(R.string.NothingToShow);
                        }
                        txtSearchResult_SetText(backgroundMessage);

                        Log.e("init stat1", String.valueOf(readCompleted && initCompleted));
                        if (readCompleted && initCompleted) {
                            fillForm();
                            showHideEverythingForLoading(false);
                        }
                    } else {
                        projectStatics.ManageCallResponseErrors(true, PrjConfig.frmClubView_Home,  100, context, response.code());
                        backgroundMessage = getResources().getString(R.string.NothingToShow);
                        txtSearchResult_SetText(backgroundMessage);
                    }
                } catch (Exception ex) {ex.printStackTrace();}

                progressBarPage_SetVisibility(View.INVISIBLE);
                if (readCompleted)
                    showHideEverythingForLoading(false);
                isInBackgroundLoading = false;

            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (!isAdded()) return;
                projectStatics.ManageCallExceptions(true, PrjConfig.frmClubView_Home,  100, t, context);
                progressBarPage_SetVisibility(View.INVISIBLE);
                backgroundMessage = getResources().getString(R.string.NothingToShow);
                txtSearchResult_SetText(backgroundMessage);
                isInBackgroundLoading = false;
            }
        });
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Moheeem baraye taghieere actionBar
        //setTheme(R.style.AppTheme_TransparentAction);
        super.onCreate(savedInstanceState);

        initializeComponents(view);

        if (currentObj != null) {
            showHideEverythingForLoading(false);
            fillForm();
        }
        else{
            //در تابع خواندن اطلاعات لود انجام میشه
            readItemInBackground();
            showHideEverythingForLoading(true);
        }
    }

    void showHideEverythingForLoading(boolean hide){
        //اگه صفحه هنوز لود نشده بود که بی خیال کلا
        if (linBody == null)
            return;
        if (hide){
            //txtPageTitle.setVisibility(View.INVISIBLE);
            linHeader.setVisibility(View.GONE);
            linBody.setVisibility(View.GONE);

            linLoadingContainer.setVisibility(View.VISIBLE);
        }
        else{
            //txtPageTitle.setVisibility(View.VISIBLE);
            linHeader.setVisibility(View.VISIBLE);
            linBody.setVisibility(View.VISIBLE);

            linLoadingContainer.setVisibility(View.GONE);
        }
    }

    //Loading part:
    ProgressBar progressBarPage;
    TextView txtSearchResult;

    TextView txtPageTitle;
    LinearLayout linLoadingContainer;
    ConstraintLayout linHeader;
    LinearLayout linBody;

    TextView btnBack, btnShowClub;
    ImageView img;
    Button btnRegister;
    LinearLayout rowDesc_Short_ForReg;
    LinearLayout rowtxtPlaceOfTour, rowtxtTourLengthView, rowtxtLeaderCustomerIdFullName, rowtxtStartToEndDateView
            , rowtxtClubTourCategoryIdView,rowtxtTourFinalPriceView, rowtxtRegEndDateView
            , rowtxtCityName, rowtxtRegDesc, rowbtnRegister, rowtxtPrerequisites, rowtxtDesc_Short, rowtxtNecessaryTools
            , rowtxtSpecialProperty, rowtxtTimeTable;
    TextView txtPrerequisites, txtDesc_Short, txtNecessaryTools, txtSpecialProperty, txtTimeTable;
    Picasso picassoInstance;
    @Override
    public void initializeComponents(View v) {
        btnBack = v.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(view -> {(context).onBackPressed();});
        txtPageTitle =v.findViewById(R.id.txtPageTitle);

        img = v.findViewById(R.id.imgOneTour);

        txtClubName =v.findViewById(R.id.txtClubName);
        txtTourLengthView =v.findViewById(R.id.txtTourLengthView);
        txtPlaceOfTour =v.findViewById(R.id.txtPlaceOfTour);
        txtLeaderCustomerIdFullName =v.findViewById(R.id.txtLeaderCustomerIdFullName);
        txtStartToEndDateView =v.findViewById(R.id.txtStartToEndDateView);
        txtClubTourCategoryIdView =v.findViewById(R.id.txtClubTourCategoryIdView);
        txtTourFinalPriceView =v.findViewById(R.id.txtTourFinalPriceView);
        txtCurrencyName =v.findViewById(R.id.txtCurrencyName);
        txtRegEndDateView =v.findViewById(R.id.txtRegEndDateView);
        txtCityName =v.findViewById(R.id.txtCityName);
        txtRegDesc =v.findViewById(R.id.txtRegDesc);
        txtPrerequisites =v.findViewById(R.id.txtPrerequisites);
        txtDesc_Short =v.findViewById(R.id.txtDesc_Short);
        txtNecessaryTools =v.findViewById(R.id.txtNecessaryTools);
        txtSpecialProperty =v.findViewById(R.id.txtSpecialProperty);
        txtTimeTable =v.findViewById(R.id.txtTimeTable);

        rowtxtPlaceOfTour =v.findViewById(R.id.rowtxtPlaceOfTour);
        rowtxtTourLengthView =v.findViewById(R.id.rowtxtTourLengthView);
        rowtxtLeaderCustomerIdFullName =v.findViewById(R.id.rowtxtLeaderCustomerIdFullName);
        rowtxtStartToEndDateView =v.findViewById(R.id.rowtxtStartToEndDateView);
        rowtxtClubTourCategoryIdView =v.findViewById(R.id.rowtxtClubTourCategoryIdView);
        rowtxtTourFinalPriceView =v.findViewById(R.id.rowtxtTourFinalPriceView);
        rowtxtRegEndDateView =v.findViewById(R.id.rowtxtRegEndDateView);
        rowtxtCityName =v.findViewById(R.id.rowtxtCityName);
        rowtxtRegDesc =v.findViewById(R.id.rowtxtRegDesc);
        rowbtnRegister =v.findViewById(R.id.rowbtnRegister);
        rowtxtPrerequisites =v.findViewById(R.id.rowtxtPrerequisites);
        rowtxtDesc_Short =v.findViewById(R.id.rowtxtDesc_Short);
        rowtxtNecessaryTools =v.findViewById(R.id.rowtxtNecessaryTools);
        rowtxtSpecialProperty =v.findViewById(R.id.rowtxtSpecialProperty);
        rowtxtTimeTable =v.findViewById(R.id.rowtxtTimeTable);

        txtDesc_Short_ForReg =v.findViewById(R.id.txtDesc_Short_ForReg);
        rowDesc_Short_ForReg =v.findViewById(R.id.rowDesc_Short_ForReg);

        btnShowClub =v.findViewById(R.id.btnShowClub);
        btnShowClub.setOnClickListener(view -> {if(currentObj != null) context.showFragment(ClubView_Home.getInstance(currentObj.TTClubNameId, PrjConfig.frmTourShowOne));});

        btnRegister =v.findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(view -> {btnRegisterClick(view);});


        linLoadingContainer =v.findViewById(R.id.linLoadingContainer);
        linBody =v.findViewById(R.id.linBody);
        linHeader =v.findViewById(R.id.linHeader);

        //Loading part:
        progressBarPage =v.findViewById(R.id.progressBarPage);
        txtSearchResult =v.findViewById(R.id.txtSearchResult);


//        // Define ActionBar object and Set Title 1400-01-12 commented
//        ActionBar actionBar;
//        actionBar = getSupportActionBar();
//        actionBar.setTitle(currentObj.getName());


        //1400-03-30 کامنت کردم که شکل جدید و یکسان تولبار رو استفاده کنم. بدون اپ بار
//        scrollView = (NestedScrollView) v.findViewById(R.id.scrollView);
//        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                DoSrollChanged(scrollY);
//                //Log.d("ScrollView","scrollX_"+scrollX+"_scrollY_"+scrollY+"_oldScrollX_"+oldScrollX+"_oldScrollY_"+oldScrollY);
//            }
//        });
        DoSrollChanged(0);
//
//        //force RTL
//        hutilities.forceRTLIfSupported(parentActivity);
        super.initializeComponents(v);
    }
    TextView txtClubName, txtTourLengthView, txtPlaceOfTour, txtLeaderCustomerIdFullName, txtStartToEndDateView;
    TextView txtClubTourCategoryIdView, txtTourFinalPriceView, txtCurrencyName, txtRegEndDateView, txtCityName;
    TextView txtRegDesc, txtDesc_Short_ForReg;
    void fillForm(){
        dbConstantsTara.DoSyncV(currentObj.TTClubTourId, dbConstantsTara.ViewCounterType_Tour, Calendar.getInstance(), context);

        txtPageTitle.setText(currentObj.getName());

        //cmt @ 1403-04-22
//        Picasso builder = Picasso.get();
//        builder.load(currentObj.ImageLink)
//                .into(img);

        if (picassoInstance == null)
            picassoInstance = PicassoTrustAll.getInstance(context);
        picassoInstance.load(currentObj.ImageLink)
                .error(R.drawable.ac_peak2)
              .into(img);

        txtClubName.setText(currentObj.ClubName);
        txtTourLengthView.setText(currentObj.getTourLenghtView());
        txtPlaceOfTour.setText(currentObj.PlaceOfTour);
        rowtxtPlaceOfTour.setVisibility(currentObj.PlaceOfTour.length() > 0?View.VISIBLE:View.GONE);
        txtLeaderCustomerIdFullName.setText(currentObj.LeaderCustomerIdFullName);
        rowtxtLeaderCustomerIdFullName.setVisibility(currentObj.LeaderCustomerIdFullName.length() > 0?View.VISIBLE:View.GONE);
        txtStartToEndDateView.setText(currentObj.getStartDateAndTimeView());
        txtClubTourCategoryIdView.setText(currentObj.ClubTourCategoryIdView);
        rowtxtClubTourCategoryIdView.setVisibility(currentObj.ClubTourCategoryIdView.length() > 0?View.VISIBLE:View.GONE);
        txtTourFinalPriceView.setText(currentObj.getTourFinalPriceView());
        rowtxtTourFinalPriceView.setVisibility(currentObj.TourFinalPrice == -1 ?View.GONE:View.VISIBLE);
        txtCurrencyName.setText(" " + hutilities.CurrencyName);
        txtRegEndDateView.setText(currentObj.getRegEndDateView());
        txtCityName.setText(currentObj.CityName);
        txtRegDesc.setText(currentObj.RegDesc);
        rowtxtRegDesc.setVisibility(currentObj.RegDesc.length() > 0?View.VISIBLE:View.GONE);
        txtPrerequisites.setText(currentObj.Prerequisites);
        rowtxtPrerequisites.setVisibility(currentObj.Prerequisites.length() > 0?View.VISIBLE:View.GONE);
        txtDesc_Short.setText(currentObj.Desc_Short);
        txtNecessaryTools.setText(currentObj.NecessaryTools);
        rowtxtNecessaryTools.setVisibility(currentObj.NecessaryTools.length() > 0?View.VISIBLE:View.GONE);
        txtTimeTable.setText(currentObj.TimeTable);
        rowtxtTimeTable.setVisibility(currentObj.TimeTable.length() > 0?View.VISIBLE:View.GONE);
        txtSpecialProperty.setText(currentObj.SpecialProperty);
        rowtxtSpecialProperty.setVisibility(currentObj.SpecialProperty.length() > 0?View.VISIBLE:View.GONE);

        txtDesc_Short_ForReg.setText(currentObj.Desc_Short);
        if (currentObj.OpenType == TTClubTour.OpenTypes_OpenDesc) {
            rowbtnRegister.setVisibility(View.GONE);
            rowtxtDesc_Short.setVisibility(View.GONE);
            rowDesc_Short_ForReg.setVisibility(View.VISIBLE);//HHH
        }
        else{
            rowtxtDesc_Short.setVisibility(View.VISIBLE);
            rowtxtDesc_Short.setVisibility(currentObj.Desc_Short.length() > 0?View.VISIBLE:View.GONE);
            rowDesc_Short_ForReg.setVisibility(View.GONE);//HHH
        }
    }
        private void DoSrollChanged ( int scrollY){
            int maxScrollY = 800;
            int minAlpha = 100;
            int maxAlpha = 255;
            String alphaStr = Integer.toHexString(scrollY >= maxScrollY ? maxAlpha : (int) ((maxAlpha - minAlpha) / (float) maxScrollY * scrollY) + minAlpha);
            if (alphaStr.length() == 1)
                alphaStr = "0" + alphaStr;


            //1400-01-12 Commented 2 lines after convert to Fragment
//            ActionBar actionBar = getSupportActionBar();
//            ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#" + alphaStr + dbConstantsTara.actionBarColor));
            //عفلا تغییر آلفا رو غیر فعال کردم و بهش نیاز ندارم. در صورتی که خواستم ازش استفاده کنم از کامنت در بیاد
//        actionBar.setBackgroundDrawable(colorDrawable);


//        int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
//        TextView abTitle = (TextView) findViewById(titleId);
//        abTitle.setTextColor(getResources().getColor(R.color.foreground_white));

//        Spannable text = new SpannableString(actionBar.getTitle());
//        text.setSpan(new ForegroundColorSpan(Color.BLUE), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//        actionBar.setTitle(text);
        }

        public void btnRegisterClick (View view){
            try {
                switch (currentObj.OpenType){
                    case TTClubTour.OpenTypes_OpenInPortal: {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(currentObj.UrlProtocol + currentObj.WebsiteAddress + "/t/" + utils.SHEncryptionAlgorithm.HashLong(currentObj.ExtClubTourId)));
                        startActivity(browserIntent);
                        break;
                    }
                    case TTClubTour.OpenTypes_SpecialLink: {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(currentObj.ExtRegLink));
                        startActivity(browserIntent);
                    }
                    case TTClubTour.OpenTypes_OpenDesc: {

                        break;
                    }
                }
            } catch (Exception ex) {
                TTExceptionLogSQLite.insert(ex.getMessage(), stktrc2k(ex), PrjConfig.frmTourShowOne, 100);
                Log.d("بازکردن", "btnOpenTours_Click: " + ex.getMessage() + ex.getStackTrace());
                //Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                ex.printStackTrace();
            }
        }

        @Override
        public View onCreateView (LayoutInflater inflater, ViewGroup parent, Bundle
        savedInstanceState){//3nd Event
            return inflater.inflate(R.layout.tour_show_one, parent, false);
        }
    }
