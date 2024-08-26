package mojafarin.pakoob;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import MorphingButton.MorphingButton;
import MorphingButton.IndeterminateProgressButton;
import bo.NewClasses.InsUpdRes;
import bo.NewClasses.SimpleRequest;
import bo.dbConstantsTara;
import bo.entity.BuyMapRequestDTO;
import bo.entity.NbGpxRequest;
import bo.entity.NbPoi;
import bo.entity.SearchRequestDTO;
import bo.entity.NbSafeGpx;
import bo.entity.NbSafeGpxList;
import bo.sqlite.TTExceptionLogSQLite;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import maptools.GeoCalcs;
import utils.HFragment;
import utils.MainActivityManager;
import utils.PicassoTrustAll;
import utils.PrjConfig;
import utils.RecyclerTouchListener;
import utils.hutilities;
import utils.projectStatics;

public class SafeGpxSearch extends HFragment {
    int currentSelectedClubIndex = -1;
    String searchParse = "";
    NbPoi NbPoiToSearch = null;
    public static SafeGpxSearch getInstance(String searchParse, NbPoi NbPoiToSearch){
        SafeGpxSearch res = new SafeGpxSearch();
        res.searchParse= searchParse;
        res.NbPoiToSearch= NbPoiToSearch;
        if (res.NbPoiToSearch == null){
            res.NbPoiToSearch = new NbPoi();
        }
        return res;
    }
    public SafeGpxSearch(){
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {//4th Event
        super.onViewCreated(view, savedInstanceState);

        initializeComponents(view);

        btnSearch_Click();
    }

    TextView btnBack, txtPageTitle;
    LinearLayout divSearch;
    Toolbar toolbar;
    TextView btnSearch;
    EditText txtDescOfRequest, txtTitleOfRequest;

    private NbSafeGpxsAdapter adapterSearch;
    private RecyclerView rvSearchResult;
    private ProgressBar pageProgressBar;
    boolean isLoadingMore = false;
    final Integer readPageSize = 100;
    TextView txtSearchResult;
    TextView lblTitleOfRequest;
    @Override
    public void initializeComponents(View v) {
        txtPageTitle = v.findViewById(R.id.txtPageTitle);
        String title = "";
        if (NbPoiToSearch.Name.length() > 0)
            title = getString(R.string.Masirhay) + " " + NbPoiToSearch.Name;
        else if (NbPoiToSearch.LatS != 0 && NbPoiToSearch.LonW != 0)
            title = getString(R.string.Masirhay) + " " + String.format(getString(R.string.Position) + " " + "%.4f,%.4f", NbPoiToSearch.LatS, NbPoiToSearch.LonW);
        else
            title = getString(R.string.Masirhay) + " '" + searchParse + "'";
        txtPageTitle.setText(title);

        btnBack = v.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(view -> {
            ((AppCompatActivity) context).onBackPressed();
        });

        lblTitleOfRequest = v.findViewById(R.id.lblTitleOfRequest);
        txtDescOfRequest = v.findViewById(R.id.txtDescOfRequest);
        txtTitleOfRequest = v.findViewById(R.id.txtTitleOfRequest);

        final IndeterminateProgressButton btnRequestTrackToPakoob = v.findViewById(R.id.btnRequestTrackToPakoob);
        btnRequestTrackToPakoob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMorphButton1Clicked(btnRequestTrackToPakoob);
            }
        });
        morphToSquare(btnRequestTrackToPakoob, 0);


        pageProgressBar = v.findViewById(R.id.progressBar);

        divSearch = v.findViewById(R.id.divSearch);
        divSearch.setVisibility(View.GONE);

        //adapterSearch = new NbSafeGpxsAdapter(this, null);

        rvSearchResult = v.findViewById(R.id.rvSearchResult);
        rvSearchResult.setLayoutManager(new LinearLayoutManager(context));
        rvSearchResult.setHasFixedSize(true);
        rvSearchResult.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);//TourList.this bayad mibood
        rvSearchResult.setLayoutManager(layoutManager);
        //For Scrolling inside NestedScrollView
        //rvSearchResult.setNestedScrollingEnabled(false);
        initRecyclerView();
//            //baraye namayeshe joda konandeh
            rvSearchResult.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));//parentActivity -> getActivity -> this bood

        //rvSearchResult.setAdapter(adapterSearch);
        initAdapterSearch(new ArrayList<>());

        txtSearchResult = v.findViewById(R.id.txtSearchResult);
        txtSearchResult.setVisibility(View.GONE);
    }

    private void onMorphButton1Clicked(final IndeterminateProgressButton btnMorph) {
        if (btnMorph.state == MorphingButton.State.IDLE_End) {
            morphToSquare(btnMorph, integer(R.integer.mb_animation));
        } else{
            simulateProgress1(btnMorph);
        }
    }

    private void simulateProgress1(@NonNull final IndeterminateProgressButton button) {
        if (!hutilities.isInternetConnected(context)) {
            projectStatics.showDialog(context, this.getResources().getString(com.pakoob.tara.R.string.NoInternet), this.getResources().getString(com.pakoob.tara.R.string.NoInternet_Desc), this.getResources().getString(com.pakoob.tara.R.string.ok), view -> {}, "", null);
            return;
        }
        int progressColor1 = color(R.color.holo_blue_bright);
        int progressColor2 = color(R.color.holo_green_light);
        int progressColor3 = color(R.color.holo_orange_light);
        int progressColor4 = color(R.color.holo_red_light);
        int color = color(R.color.mb_gray);
        int progressCornerRadius = dimen(R.dimen.mb_corner_radius_4);
        int width = dimen(R.dimen.mb_width_200);
        int height = dimen(R.dimen.mb_height_8);
        int duration = integer(R.integer.mb_animation);

        NbGpxRequest req = new NbGpxRequest();
        req.CCustomerId = app.session.getCCustomer().CCustomerId;
        req.TitleOfPoi = txtTitleOfRequest.getText().toString();
        if (req.TitleOfPoi.isEmpty()){
            projectStatics.showDialog(context, getString(R.string.pleaseEnterTitle), getString(R.string.pleaseEnterTitle_Desc), getResources().getString(R.string.ok), null, "", null);
            txtTitleOfRequest.requestFocus();
            return;
        }
        req.Desc = txtDescOfRequest.getText().toString();
        req.GpxRequestType = NbGpxRequest.GpxRequestType_GPX;
        req.Lat = NbPoiToSearch.LatS;
        req.Lon = NbPoiToSearch.LonW;
        req.NbPoiId = NbPoiToSearch.NbPoiId;


        button.state = MorphingButton.State.PROGRESS;
        button.blockTouch(); // prevent user from clicking while button is in progress
        button.morphToProgress(color, progressCornerRadius, width, height, duration, progressColor1, progressColor2,
                progressColor3, progressColor4);

        //progressBar.setVisibility(View.VISIBLE);

        SimpleRequest request = SimpleRequest.getInstance(req);
        Call<ResponseBody> call = app.apiMap.NbGpxRequestCreate(request);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!isAdded()) return;
                try {
                    if (response.isSuccessful()) {
                        InsUpdRes res = InsUpdRes.fromBytes(response.body().bytes());
                        String titleMsg = "";

                        if (res.isOk){
                            Handler handler = new Handler();
                            handler.postDelayed(() -> {morphToSuccess(button);button.unblockTouch();}, 1000);
                            Toast.makeText(context, getString(R.string.RequestSentMessage), Toast.LENGTH_LONG).show();
                        } else {
                            Handler handler = new Handler();
                            handler.postDelayed(() -> {morphToFailure(button, 500);button.unblockTouch();}, 1000);

                            projectStatics.showDialog(context, titleMsg, res.message, getResources().getString(R.string.ok), null, "", null);
                        }
                    } else {
                        Handler handler = new Handler();
                        handler.postDelayed(() -> {morphToFailure(button, 500);button.unblockTouch();}, 1000);

                        projectStatics.showDialog(context, getResources().getString(R.string.dialog_ertebatBaServer_Title)
                                , getResources().getString(R.string.dialog_ertebatBaServer_Desc)
                                , getResources().getString(R.string.ok)
                                , null, "", null);
                        TTExceptionLogSQLite.insert("Server Connect: " + response.code(), response.message(), PrjConfig.frmSafeGpxSearch, 300);
                        Log.d(Tag, "ERROR RESPONSE : " + response.code() + " msg: " + response.message());
                    }

                    //progressBar.setVisibility(View.GONE);

                } catch (Exception ex) {
                    TTExceptionLogSQLite.insert("Exception:" +response.message(),stktrc2k(ex) , PrjConfig.frmSafeGpxView, 200);
                    ex.printStackTrace();
                    Handler handler = new Handler();
                    handler.postDelayed(() -> {morphToFailure(button, 500);button.unblockTouch();}, 1000);

                    //progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                TTExceptionLogSQLite.insert("Fail:"+t.getMessage(),stktrc2kt(t) , PrjConfig.frmSafeGpxView, 100);
                if (!isAdded()) return;
                morphToFailure(button, 500);
                Log.e(Tag, "error" + " " + t.getMessage());
                t.printStackTrace();
            }
        });

    }
    private void morphToSquare(final MorphingButton btnMorph, int duration) {
        btnMorph.state  = MorphingButton.State.IDLE_Start;
        MorphingButton.Params square = MorphingButton.Params.create()
                .duration(duration)
                .cornerRadius(dimen(R.dimen.mb_corner_radius_2))
                .width(dimen(R.dimen.mb_width_200))
                .height(dimen(R.dimen.mb_height_56))
                .color(color(R.color.mb_blue))
                .colorPressed(color(R.color.mb_blue_dark))
                .text(getString(R.string.btnRequestTrackToPakoob));
        btnMorph.morph(square);
    }

    private void morphToSuccess(final MorphingButton btnMorph) {
        btnMorph.state = MorphingButton.State.IDLE_End;
        MorphingButton.Params circle = MorphingButton.Params.create()
                .duration(integer(R.integer.mb_animation))
                .cornerRadius(dimen(R.dimen.mb_height_56))
                .width(dimen(R.dimen.mb_height_56))
                .height(dimen(R.dimen.mb_height_56))
                .color(color(R.color.mb_green))
                .colorPressed(color(R.color.mb_green_dark))
                .icon(R.drawable.ic_done_24px);
        btnMorph.morph(circle);
    }

    private void morphToFailure(final MorphingButton btnMorph, int duration) {
        btnMorph.state = MorphingButton.State.IDLE_End;
        MorphingButton.Params circle = MorphingButton.Params.create()
                .duration(duration)
                .cornerRadius(dimen(R.dimen.mb_height_56))
                .width(dimen(R.dimen.mb_height_56))
                .height(dimen(R.dimen.mb_height_56))
                .color(color(R.color.mb_red))
                .colorPressed(color(R.color.mb_red_dark))
                .icon(R.drawable.ic_privacy_tip_black_48dp);
        btnMorph.morph(circle);
    }

    private void initRecyclerView() {
        rvSearchResult.addOnItemTouchListener(new RecyclerTouchListener(getContext(), rvSearchResult, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //1400-01-12 commented
//                Intent i = new Intent(getContext(), TourShowOne.class);
//                i.putExtra("ix", position);
//                i.putExtra("isMyClubTours", 0);
//                startActivity(i);

            }

            @Override
            public void onLongClick(View view, int position) {
                //TTClubTour selectedTour = dbConstantsTara.getTours().get(position);
                //Toast.makeText(getContext(), selectedTour.getName() + " is long selected!", Toast.LENGTH_SHORT).show();

            }
        }));
    }

    public AlertDialog dialogMap = null;


    public boolean onBackPressed() {
        if (dialogMap != null && dialogMap.isShowing()) {
            dialogMap.dismiss();
            return false;
        } else {
//            Intent intent = new Intent();
//            setResult(1100, intent);
//            finish();
            return true;
        }
    }


    private void btnSearch_Click() {
        if (!hutilities.isInternetConnected(context)) {
            projectStatics.showDialog(context, this.getResources().getString(R.string.NoInternet), this.getResources().getString(R.string.NoInternet_Desc), this.getResources().getString(R.string.ok), view -> {}, "", null);
            return;
        }
        pageProgressBar.setVisibility(View.VISIBLE);

        SearchRequestDTO requestDTO = new SearchRequestDTO();
        requestDTO.Filter = NbPoiToSearch.ServerId + "***" + NbPoiToSearch.LatS+ "***" + NbPoiToSearch.LonW+ "***" + searchParse.replace("***", "");

        Call<ResponseBody> call = app.apiMap.SearchSafeGpx(SimpleRequest.getInstance(requestDTO));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!isAdded()) return;
                try {
                    divSearch.setVisibility(View.VISIBLE);
                    pageProgressBar.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        NbSafeGpxList result = NbSafeGpxList.fromBytes(response.body().bytes());
                        if (!result.isOk) {
                            txtSearchResult.setVisibility(View.VISIBLE);
                            txtSearchResult.setText(result.message);
                        } else {
                            txtSearchResult.setVisibility(View.GONE);
                        }
                        if (result.resList.size() > 0) {
                            lblTitleOfRequest.setText(R.string.title_RequestTrackFromPakoob_Similar);
                            rvSearchResult.setVisibility(View.VISIBLE);
//                            List<NbSafeGpx> added = new ArrayList<>();
//                            for (int i = 0; i < 30; i++) {
//                                added.add(result.resList.get(0));
//                            }
                            initAdapterSearch(result.resList);
                        } else {
                            rvSearchResult.setVisibility(View.GONE);
                        }
                    } else {
                        txtSearchResult.setVisibility(View.VISIBLE);
                        txtSearchResult.setText("متاسفانه در برقراری ارتباط با سرور مشکلی به وجود آمده است. لطفا بعدا مجددا تلاش نمایید.");
                        Log.e("MY_ERROR", "ResponseCODE: " + response.code());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Log.e("MY_ERROR", ex.getMessage());
                    TTExceptionLogSQLite.insert(ex.getMessage(), stktrc2k(ex), PrjConfig.frmSafeGpxSearch, 101);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                TTExceptionLogSQLite.insert(t.getMessage(), stktrc2kt(t), PrjConfig.frmSafeGpxSearch, 100);
                if (!isAdded()) return;
                divSearch.setVisibility(View.VISIBLE);
                pageProgressBar.setVisibility(View.GONE);
                txtSearchResult.setVisibility(View.VISIBLE);
                txtSearchResult.setText("متاسفانه مشکلی در برقراری ارتباط با سرور به وجود آمده است. لطفا بعدا مجددا تلاش نمایید.");

            }
        });


    }

    void initAdapterSearch(List<NbSafeGpx> result) {
        if (true || adapterSearch == null) {

            NbSafeGpxsAdapter.OnItemClickListener itemClickListener = (post, Position, itemProgressbar) -> {
                RecyclerView_ItemClicked(post, Position, itemProgressbar);
            };
            adapterSearch = new NbSafeGpxsAdapter(context, "full",itemClickListener, null);
            adapterSearch.setData(result);
            rvSearchResult.setAdapter(adapterSearch);
        }
        if (rvSearchResult.getAdapter() == null) {

        }
    }

    public void RecyclerView_ItemClicked(NbSafeGpx current, int position, ProgressBar itemProgressbar){
        NbSafeGpx currentSelectedClub  = adapterSearch.data.get(position);
        hutilities.hideKeyboard((Activity)context);
        currentSelectedClubIndex = position;
        if (currentSelectedClub.IsMapOrGpxOrOther == 2) {
            SafeGpxView view = SafeGpxView.getInstance(currentSelectedClub, PrjConfig.frmSafeGpxSearch);
            ((MainActivityManager) context).showFragment(view);
        }
        else if(currentSelectedClub.IsMapOrGpxOrOther == 1){
            AlertDialog.Builder alertDialogBuilder = DialogGetDisCopon.GetBuilder(context, view -> {
                DoBuyRequest(currentSelectedClub, DialogGetDisCopon.GetDiscountCode(dialog_getdiscount),itemProgressbar );
                dialog_getdiscount.dismiss();
            }, null);

            dialog_getdiscount = alertDialogBuilder.create();
            dialog_getdiscount.show();
            projectStatics.SetDialogButtonStylesAndBack(dialog_getdiscount, this.context, projectStatics.getIranSans_FONT(context), 18);


        }
    }

    public AlertDialog dialog_getdiscount = null;
    boolean isDownloading = false;
    public boolean DoBuyRequest(NbSafeGpx currentObj, String DiscountCode, ProgressBar progressBar) {
        //Same at : SafeGpxSearch - MapSelect
        if (!hutilities.isInternetConnected(context)) {
            projectStatics.showDialog(context, getResources().getString(R.string.NoInternet), getResources().getString(R.string.NoInternet_Desc), getResources().getString(R.string.ok), view -> {}, "", null);
            return false;
        }
        BuyMapRequestDTO buyRequest = new BuyMapRequestDTO();
        buyRequest.DiscountCode = DiscountCode;
        buyRequest.NBMapId = currentObj.NbSafeGPXId;
        buyRequest.NbBuyType = BuyMapRequestDTO.NbBuyType_Map;
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
                                titleMsg =getString(R.string.msgMapIsUnavailable_Title);
                                res.message =getString(R.string.msgMapIsUnavailable_Desc);
                                break;
                            case STATUS_REQUESTED_BEFORE:
                                titleMsg =getString(R.string.msgMapIsInProcess_Title);
                                res.message =getString(R.string.msgMapIsInProcess_Desc);
                                break;
                            case STATUS_ERROR:
                                titleMsg = "خطا";
                                break;
                            case STATUS_DOWNLOAD_NOW:
                                titleMsg = getString(R.string.msgMapIsReadyToDownload_Title);
                                res.message = getString(R.string.msgMapIsReadyToDownload_DescForSafeGpx);
                                app.syncBuyMapDatabase(progressBar, txtSearchResult, context, null);
                                //holder.btnOrder.setText(getString(R.string.Download));
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
                            if (res.command.length() > 0)
                            {
                                String[] parts = res.command.split(";;");
                                if(parts.length > 0)
                                    ((MapSelect_Dialog_GotoBank) dialogFragment).price = Double.valueOf(parts[0]);
                                if(parts.length > 1 && parts[1].length() > 1)
                                    ((MapSelect_Dialog_GotoBank) dialogFragment).discountMsg = parts[1];
                            }
                            ((MapSelect_Dialog_GotoBank) dialogFragment).message = res.message;

                            dialogFragment.show(getFragmentManager(), "dialog");

//                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(res.resValue));
//                                startActivity(browserIntent);
                        } else {
                            projectStatics.showDialog(context, titleMsg, res.message, getResources().getString(R.string.ok), null, "", null);
                        }
                    } else {
                        projectStatics.showDialog(context, getResources().getString(R.string.dialog_ertebatBaServer_Title)
                                , getResources().getString(R.string.dialog_ertebatBaServer_Desc)
                                , getResources().getString(R.string.ok)
                                , null, "", null);
                        TTExceptionLogSQLite.insert("Server Connect: " + response.code(), response.message(), PrjConfig.frmMapSelect, 300);
                        Log.d(Tag, "ERROR RESPONSE : " + response.code() + " msg: " + response.message());
                    }

                    isDownloading = false;
                    progressBar.setVisibility(View.GONE);

                } catch (Exception ex) {
                    TTExceptionLogSQLite.insert("Exception:"+response.message(), stktrc2k(ex), PrjConfig.frmSafeGpxSearch, 201);
                    ex.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                    isDownloading = false;
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                TTExceptionLogSQLite.insert("Fail:"+t.getMessage(), stktrc2kt(t), PrjConfig.frmSafeGpxSearch, 100);
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                Log.e(Tag, "error");
                isDownloading = false;
            }
        });
        return true;
    }



    void initAdapter(List<NbSafeGpx> allTours) {
//        if (adapter == null) {
//            adapter = new MyClubTourListAdapter(allTours, R.layout.sms_list_item);
//
//            if(parentActivity == null)
//                Log.d("INIT ERROR", "Context is null");
//            else
//                Log.d("INIT ERROR", "Context is OK");
//
//            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(parentActivity);//TourList.context bayad mibood
//            recyclerView.setLayoutManager(layoutManager);
//
//            //baraye namayeshe joda konandeh
//            recyclerView.addItemDecoration(new DividerItemDecoration(parentActivity, LinearLayoutManager.VERTICAL));//parentActivity -> getActivity -> va ghabl taresh context bood
//
//            recyclerView.setAdapter(adapter);
//        }
    }

    void finishLoadingEnvironment() {
        isLoadingMore = false;
    }

    int lastFirstVisiblePosition = -1;
    int topOffset = 0;

    @Override
    public void onPause() {
        //1400-01-13 commented
//        try {
//            LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
//            lastFirstVisiblePosition = manager.findFirstVisibleItemPosition();
//            View firstItemView = manager.findViewByPosition(lastFirstVisiblePosition);
//            topOffset = (int) firstItemView.getTop();
//            Log.d("onPauseOrResume", "pause fired" + lastFirstVisiblePosition);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            lastFirstVisiblePosition = -1;
//        }

        super.onPause();
    }

    @Override
    public void onResume() {
        Log.d("onPauseOrResume", "resume fired=" + currentSelectedClubIndex);

        if (currentSelectedClubIndex != -1){
            adapterSearch.notifyItemChanged(currentSelectedClubIndex);// NOT WORKING, So I used currentViewHolder

            currentSelectedClubIndex = -1;
        }
        setRecyclerScrollOnBack();
        super.onResume();
    }

    void setRecyclerScrollOnBack() {
        //1400-01-13 commented

//        Log.d("onPauseOrResume", "setRecyclerScrollOnBack fired" + lastFirstVisiblePosition);
//
//        if (lastFirstVisiblePosition != -1) {
//            try {
//                ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(lastFirstVisiblePosition, topOffset);
//                lastFirstVisiblePosition = -1;
//                Log.d("onPauseOrResume", "resume scroll changed" + lastFirstVisiblePosition);
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        }
    }
//
//    void hideKeyboard() {
//        //hide keyboard
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(txtSearch.getWindowToken(), 0);
//    }
public static void ReformatBtnFollow(TextView btnFollow, byte FollowingByMe, byte NotiStatus, TextView btnBell, String followingText, String followText, Context context){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        btnFollow.setBackground(FollowingByMe == 1 ? context.getDrawable(R.drawable.bg_roundcorner_gray_backwhite5) : context.getDrawable(R.drawable.bg_roundcorner_greenfill));
    }

    btnFollow.setText(FollowingByMe == 1 ? followingText : followText);
    btnFollow.setTextColor(FollowingByMe == 1 ? dbConstantsTara.Following_TextColor : dbConstantsTara.Follow_TextColor);

    if (btnBell != null) {

        if (FollowingByMe == 1 ) {
            btnBell.setVisibility(View.VISIBLE);
            if(NotiStatus == 0 || NotiStatus == 1)
                btnBell.setText(dbConstantsTara.BellOn);
            else
                btnBell.setText(dbConstantsTara.BellOff);
        }
        else {
            btnBell.setVisibility(View.GONE);
        }
    }
}
    public static void ReformatBtnIamFan(TextView btnIamFan, byte FanByMe, Context context){
        btnIamFan.setText(FanByMe == 1 ? dbConstantsTara.IamFan_Char : dbConstantsTara.IamNotFan_Char);
        btnIamFan.setTextColor(FanByMe == 1 ? dbConstantsTara.IamFan_Color : dbConstantsTara.IamNotFan_Color);
    }
    public static class NbSafeGpxsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<NbSafeGpx> data;
        private Context context;
        private LayoutInflater layoutInflater;
        private OnDeleteButtonClickListener onDeleteButtonClickListener;
        OnItemClickListener itemClickFunction;
        String mode;

        public NbSafeGpxsAdapter(Context context, String _mode, OnItemClickListener _itemClickFunction, OnDeleteButtonClickListener listener) {
            this.mode = _mode;
            this.data = new ArrayList<>();
            this.context = context;
            this.onDeleteButtonClickListener = listener;
            this.itemClickFunction = _itemClickFunction;
        }

        @Override
        public TTClubViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            layoutInflater = LayoutInflater.from(context);
            //this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = layoutInflater.inflate(R.layout.frm_safegpxsearch_item, parent, false);
            return new TTClubViewHolder(itemView, this.itemClickFunction);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            if (viewHolder instanceof TTClubViewHolder) {
                ((TTClubViewHolder) viewHolder).bind(data.get(position), position);
            } else if (1 == 2) {
                //private void showLoadingView(LoadingViewHolder viewHolder, int position) {
                //        //ProgressBar would be displayed
                //
                //    }
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public void setData(List<NbSafeGpx> newData) {
            if (data != null) {
//                NbSafeGpxDiffCallback postDiffCallback = new NbSafeGpxDiffCallback(data, newData);
//                DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(postDiffCallback);

                data.clear();
                data.addAll(newData);
                //diffResult.dispatchUpdatesTo(this);
                notifyDataSetChanged();
            } else {
                // first initialization
                data = newData;
            }
        }

        public abstract class OnDeleteButtonClickListener {
            public abstract void onDeleteButtonClicked(NbSafeGpx post);
        }
        //من اضافه کردم
        public interface OnItemClickListener {
            void onItemClicked(NbSafeGpx post, int Position, ProgressBar itemProgressbar);
        }

        class TTClubViewHolder extends RecyclerView.ViewHolder {
            public View itemView;
            public TextView txtTitle, lblDistance;
            ImageView imgIcon, imgScreen;
            public ProgressBar progressBarIndet;
            LinearLayout itemMainPart;
            OnItemClickListener itemClickFunction;

            TTClubViewHolder(View itemView, OnItemClickListener itemClickFunction) {
                super(itemView);
                this.itemView = itemView;
                this.itemClickFunction = itemClickFunction;
                txtTitle = itemView.findViewById(R.id.txtTitle);
                itemMainPart = itemView.findViewById(R.id.itemMainPart);
                lblDistance = itemView.findViewById(R.id.lblDistance);
                imgIcon = itemView.findViewById(R.id.imgIcon);
                imgScreen = itemView.findViewById(R.id.imgScreen);

                progressBarIndet = itemView.findViewById(R.id.itemprogressbarIndet);

                Drawable progressDrawable = progressBarIndet.getIndeterminateDrawable().mutate(); //or getProgressDrawable for normal //https://stackoverflow.com/questions/2020882/how-to-change-progress-bars-progress-color-in-android
                progressDrawable.setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
                progressBarIndet.setProgressDrawable(progressDrawable);

                txtTitle.setTypeface(projectStatics.getIranSans_FONT(context));

                if (mode.equals("simple")){
                }

            }


            void bind(final NbSafeGpx currentObj, int position) {
                if (currentObj != null) {
                    if (currentObj.IsMapOrGpxOrOther == 2){
                        //اگه جی پی ایکس بود
                        txtTitle.setText(currentObj.Name);
                        lblDistance.setText(GeoCalcs.distanceBetweenFriendlyInKm(currentObj.TrackLength));
                        itemMainPart.setOnClickListener(view -> {
                            itemClickFunction.onItemClicked(currentObj, position, progressBarIndet);
                        });
                        imgIcon.setImageResource(ActivityTypeToImageResource(currentObj.ActivityType));

                        PicassoTrustAll.getInstance(context)
                                .load(currentObj.ScreenshotAddress)
                                .placeholder( R.drawable.progress_animation )
                                .into(imgScreen);
                    }
                    else if(currentObj.IsMapOrGpxOrOther == 1){
                        //اگه نقشه بود
                        String name = "";
                        if (currentObj.Name.length() > 0){
                            name = currentObj.Name;
                        }
                        else if (currentObj.FileAddress3.length() > 0){
                            name = currentObj.FileAddress3;
                        }
                        else if (currentObj.FileAddress1.length() > 0){
                            name = "بلوک " + currentObj.FileAddress1 + " " + currentObj.FileAddress2;
                        }

                        txtTitle.setText(context.getString(R.string.Map) + " " + name);
                        lblDistance.setVisibility(View.GONE);
                        itemMainPart.setOnClickListener(view -> {
                            itemClickFunction.onItemClicked(currentObj, position, progressBarIndet);
                        });
                        imgIcon.setImageResource(R.drawable.ic_map_black_48dp);

                        PicassoTrustAll.getInstance(context)
                                .load(R.drawable.pakoob_logo_1400)
                                .placeholder( R.drawable.progress_animation )
                                .into(imgScreen);

                    }
                }
            }
        }

        final String TAG = "Downloading...";

        class NbSafeGpxDiffCallback extends DiffUtil.Callback {

            private final List<NbSafeGpx> oldNbSafeGpxs, newNbSafeGpxs;

            public NbSafeGpxDiffCallback(List<NbSafeGpx> oldNbSafeGpxs, List<NbSafeGpx> newNbSafeGpxs) {
                this.oldNbSafeGpxs = oldNbSafeGpxs;
                this.newNbSafeGpxs = newNbSafeGpxs;
            }

            @Override
            public int getOldListSize() {
                return oldNbSafeGpxs.size();
            }

            @Override
            public int getNewListSize() {
                return newNbSafeGpxs.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return oldNbSafeGpxs.get(oldItemPosition).NbSafeGPXId == newNbSafeGpxs.get(newItemPosition).NbSafeGPXId;
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return oldNbSafeGpxs.get(oldItemPosition).equals(newNbSafeGpxs.get(newItemPosition));
            }
        }
    }

    public static String[] HardnessLevelNames = new String[]{"", "بسیار آسان", "آسان", "متوسط", "نیمه دشوار", "دشوار", "بسیار دشوار", "فنی"};
    public static String getHardnessLevelName(byte HardnessLevel){
        if (HardnessLevel < 8)
            return HardnessLevelNames[HardnessLevel];
        return "";
    }
    public static String[] ActivityTypeNames = new String[] { "", "پیاده روی", "کوهپیمایی", "کوهنوردی", "دو", "دوی کوهستان", "کوهنوردی ترکیبی"
            , "", "", "", "", "", "", "", "", ""
            , "دوچرخه کوهستان", "دوچرخه راه", "سایکل توریست"
            , "", "", "", "", "", "", "", "", "", "", "", ""
            , "سنگنوردی", "دیواره نوردی", "یخ نوردی", "دره نوردی", "غارنوردی", "ویافراتا"
            , "", "", "", "", "", "", "", "", ""
            , "اسکی آلپاین", "اسکی کراس کانتری", "اسکی بک کانتری", "", "", ""
            , "", "", "", "", "", "", "", "", ""
            , "ماشین", "آفرود", "موتور", "موتور کوهستان", "", ""
            , "", "", "", "", "", "", "", "", ""
    };
    public static String getActivityTypeNames(byte ActivityType){
        if (ActivityType < 75)
            return ActivityTypeNames[ActivityType];
        return "";
    }
    public static int ActivityTypeToImageResource(byte ActivityType){
        if (ActivityType == 1)
            return R.drawable.ac_walk;
        if (ActivityType == 2)
            return R.drawable.ac_hiking;
        if (ActivityType == 3)
            return R.drawable.ac_mountaineering;
        if (ActivityType == 4)
            return R.drawable.ac_running;
        if (ActivityType == 5)
            return R.drawable.ac_trailrun;
        if (ActivityType == 6)
            return R.drawable.ac_mountaineering;
        if (ActivityType == 16)
            return R.drawable.ac_cyclemountain;
        if( ActivityType == 17 || ActivityType == 18)
            return R.drawable.ac_cycle;
        if (ActivityType == 31 || ActivityType == 32 || ActivityType == 35 || ActivityType == 36)
            return R.drawable.ac_rock;
        if (ActivityType == 33)
            return R.drawable.ac_iceclimbing;
        if (ActivityType == 34)
            return R.drawable.ac_canion;
        if (ActivityType >= 46 && ActivityType <= 60)
            return R.drawable.ac_ski;
        if (ActivityType == 61)
            return R.drawable.ac_car;
        if (ActivityType == 63 || ActivityType == 64)
            return R.drawable.ac_motortrail;
        if (ActivityType >= 62 && ActivityType <= 75)
            return R.drawable.ac_offroad;
        return R.drawable.ac_walk;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {//3nd Event
        return inflater.inflate(R.layout.frm_safegpxsearch, parent, false);
    }
}