package pakoob;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pakoob.tara.R;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;
import bo.dbConstantsTara;
import bo.NewClasses.SimpleRequest;
import bo.NewClasses.StringContentDTO;
import bo.entity.TTClubNameDTO;
import bo.entity.TTClubNameDTOList;
import bo.sqlite.TTExceptionLogSQLite;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.HFragment;
import utils.MainActivityManager;
import utils.PicassoCircleTransform;
import utils.PicassoOnScrollListener;
import utils.PrjConfig;
import utils.hutilities;
import utils.projectStatics;

public class ClubView_Home extends HFragment {
    public TTClubNameDTO currentObj;
    int FromPageId;
    int RecTTClubNameId = 0;
    boolean isInBackgroundLoading = false;
    String backgroundMessage = "";

    public static ClubView_Home getInstance(TTClubNameDTO clubNameDTO, int FromPageId){
        ClubView_Home res = new ClubView_Home();
        res.RecTTClubNameId = 0;;
        res.currentObj = clubNameDTO;
        res.FromPageId = FromPageId;
        return res;
    }

    public static ClubView_Home getInstance(Integer ttClubNameId, int FromPageId){
        ClubView_Home res = new ClubView_Home();
        res.RecTTClubNameId = ttClubNameId;;
        res.currentObj = null;
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
        StringContentDTO contentDTO = StringContentDTO.getInstance(RecTTClubNameId + "***" + 0);
        SimpleRequest request = SimpleRequest.getInstance(contentDTO);
        Call<ResponseBody> call = dbConstantsTara.apiTara.GetTTClubName(request);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!isAdded()) return;
                boolean readCompleted = false;
                try {
                    if (response.isSuccessful()) {
                        TTClubNameDTOList result = TTClubNameDTOList.fromBytes(response.body().bytes());
                        if (!result.isOk) {
                            backgroundMessage = result.message != null?result.message:"";
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
                projectStatics.ManageCallExceptions(true, PrjConfig.frmClubView_Home,  100, t, context);
                if (!isAdded()) return;
                progressBarPage_SetVisibility(View.INVISIBLE);
                backgroundMessage = getResources().getString(R.string.NothingToShow);
                txtSearchResult_SetText(backgroundMessage);
                isInBackgroundLoading = false;
            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {//4th Event
        super.onViewCreated(view, savedInstanceState);

        followText = context.getString(pakoob.DbAndLayout.R.string.follow);
        followingText = context.getString(pakoob.DbAndLayout.R.string.following);

        initializeComponents(view);
        if (currentObj != null) {
            showHideEverythingForLoading(false);
            fillForm();
        }
        else{
            readItemInBackground();
            //در تابع خواندن اطلاعات لود انجام میشه
            showHideEverythingForLoading(true);
        }
    }
    private void fillForm() {
        try {
            dbConstantsTara.DoSyncV(currentObj.TTClubNameId, dbConstantsTara.ViewCounterType_Club, Calendar.getInstance(), context);

            txtPageTitle.setText(currentObj.Name);

            Log.e("init stat0", String.valueOf(currentObj.Logo));
            try {

                Picasso builder = Picasso.with(context);
                builder.load(currentObj.Logo).tag(PicassoOnScrollListener.TAG)//.config(Bitmap.Config.RGB_565).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        //.placeholder((R.drawable.ic_launcher_background)) HHH 1400-01-10
                        //.error(R.drawable.ic_launcher_background)  HHH 1400-01-10
                        .transform(new PicassoCircleTransform()).into(txt_ct_ImageLinkUri);
            }
            catch (Exception ex){
                Log.e("00000", ex.getMessage());
            }
            txtClubFullName.setText(currentObj.FullClubName);
            txtDesc.setText(currentObj.Desc);


            rvNextTours.readAndShow(TourListComponent.CacheDBId_None, currentObj.TTClubNameId, TourListComponent.CategoryTypesToShow_AllMinusLearning, TourListComponent.SORT_Default, 1000);
            rvNextLearnings.readAndShow(TourListComponent.CacheDBId_None, currentObj.TTClubNameId, TourListComponent.CategoryTypesToShow_Learning, TourListComponent.SORT_Default, 1000);

            txtTourCountInHome.setText(String.valueOf(currentObj.TourCount));
            txtFanCountInHome.setText(String.valueOf(currentObj.FanCount));
            txtFollowerCountInHome.setText(String.valueOf(currentObj.FollowerCount));

            final String followingText = context.getString(R.string.following);
            final String followText = context.getString(R.string.follow);

            ClubSearch.ReformatBtnFollow(btnFollow, currentObj.FollowingByMe, currentObj.NotificationStatus, btnBell, followingText, followText, context);
            ClubSearch.ReformatBtnIamFan(btnIamFan, currentObj.FanByMe, context);
            if(currentObj.NotificationStatus == 0 || currentObj.NotificationStatus == 1)
                btnBell.setText(dbConstantsTara.BellOn);
            else
                btnBell.setText(dbConstantsTara.BellOff);
        } catch (Exception ex) {
            TTExceptionLogSQLite.insert(ex.getMessage(), ex.getStackTrace().toString(), PrjConfig.frmClubView_Home, 150);
            Log.d("بازکردن", "btnOpenTours_Click: " + ex.getMessage() + ex.getStackTrace());
            //Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    void showHideEverythingForLoading(boolean hide){
        //اگه صفحه هنوز لود نشده بود که بی خیال کلا
        if (linBody == null)
            return;
        if (hide){
            txtPageTitle.setVisibility(View.INVISIBLE);
            btnBell.setVisibility(View.INVISIBLE);
            linHeader.setVisibility(View.GONE);
            linBody.setVisibility(View.GONE);

            linLoadingContainer.setVisibility(View.VISIBLE);
        }
        else{
            txtPageTitle.setVisibility(View.VISIBLE);
            //btnBell.setVisibility(View.VISIBLE); این از جای دیگه تنظیم میشه
            linHeader.setVisibility(View.VISIBLE);
            linBody.setVisibility(View.VISIBLE);

            linLoadingContainer.setVisibility(View.GONE);
        }

    }

    TourListComponent rvNextTours, rvNextLearnings;
    TextView txtPageTitle, btnBack, txtTourCountInHome, txtClubFullName, txtFanCountInHome, txtFollowerCountInHome, btnFollow, btnIamFan;
    TextView btnBell;
    TextView txtDesc;
    LinearLayout btnIamFanParent, linHeader, linBody, linLoadingContainer;
    ImageView txt_ct_ImageLinkUri;
    ProgressBar progressBarTopOfClubViewHome;
    public static String followText;
    public static String followingText;

    //Loading part:
    ProgressBar progressBarPage;
    TextView txtSearchResult;

    @Override
    public void initializeComponents(View v) {

        btnBack =v.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(view -> {((MainActivityManager) context).onBackPressed();});
        txtPageTitle = v.findViewById(R.id.txtPageTitle);

        rvNextTours= v.findViewById(R.id.rvNextTours);
        rvNextTours.setOnItemClickListener((post, Position) -> {context.showFragment(TourShowOne.getInstance(post, Position));});
        rvNextLearnings = v.findViewById(R.id.rvNextLearnings);
        rvNextLearnings.setOnItemClickListener((post, Position) -> {context.showFragment(TourShowOne.getInstance(post, Position));});

        linHeader = v.findViewById(R.id.linHeader);
        linBody = v.findViewById(R.id.linBody);
        linLoadingContainer = v.findViewById(R.id.linLoadingContainer);

        //Loading part:
        progressBarPage = v.findViewById(R.id.progressBarPage);
        txtSearchResult = v.findViewById(R.id.txtSearchResult);

        progressBarTopOfClubViewHome= v.findViewById(R.id.progressBarTopOfClubViewHome);

        btnBell = v.findViewById(R.id.btnBell);
        btnBell.setOnClickListener(view -> {ClubSearch.btnNotificationStatus_Click(currentObj, context,progressBarTopOfClubViewHome, btnBell, this);});

        btnFollow = v.findViewById(R.id.btnFollow);
        btnFollow.setOnClickListener(view -> {ClubSearch.btnFollow_Click(currentObj, context,progressBarTopOfClubViewHome, btnFollow,btnBell, txtFollowerCountInHome, context.getString(R.string.following), context.getString(R.string.follow), this);});
        btnIamFan = v.findViewById(R.id.btnIamFan);
        btnIamFanParent = v.findViewById(R.id.btnIamFanParent);
        btnIamFanParent.setOnClickListener(view -> {ClubSearch.btnIamFan_Click(currentObj, context,progressBarTopOfClubViewHome, btnIamFan, txtFanCountInHome, this);});

        txt_ct_ImageLinkUri = v.findViewById(R.id.txtImage);

        txtTourCountInHome = v.findViewById(R.id.txtTourCountInHome);
        txtFanCountInHome = v.findViewById(R.id.txtFanCountInHome);
        txtFollowerCountInHome = v.findViewById(R.id.txtFollowerCountInHome);
        txtClubFullName = v.findViewById(R.id.txtClubFullName);
        txtDesc = v.findViewById(R.id.txtDesc);
        super.initializeComponents(v);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {//3nd Event
        return inflater.inflate(R.layout.frm_clubview_home, parent, false);
    }
}
