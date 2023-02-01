package pakoob;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.pakoob.tara.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import bo.dbConstantsTara;
import bo.NewClasses.InsUpdRes;
import bo.entity.CityDTO;
import bo.entity.SearchRequestDTO;
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
import utils.PrjEnums;
import utils.RecyclerTouchListener;
import utils.hutilities;
import utils.projectStatics;

public class ClubSearch extends HFragment {
    boolean isDownloading = false;

    int currentSelectedClubIndex = -1;

    public static String followText;
    public static String followingText;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {//4th Event
        super.onViewCreated(view, savedInstanceState);

        followText = context.getString(pakoob.DbAndLayout.R.string.follow);
        followingText = context.getString(pakoob.DbAndLayout.R.string.following);

        initializeComponents(view);

        ReadToursAndFillRecycler(PrjEnums.LoadDataTypes.FirstRead);

        btnSearch_Click();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.mapselect);

    }

    private void ReadToursAndFillRecycler(PrjEnums.LoadDataTypes firstRead)  {
        try {
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    TextView btnBack;
    LinearLayout divSearch;
    Toolbar toolbar;
    TextView btnSearch, btnFindByLocation;

    TextInputEditText txtSearch;
    private TTClubNameDTOsAdapter adapterSearch;
    private RecyclerView rvSearchResult;
    private ProgressBar pageProgressBar;
    boolean isLoadingMore = false;
    final Integer readPageSize = 100;
    TextView txtSearchResult;
    LinearLayout linFilterCity;
    TextView lblFilterCity;
    @Override
    public void initializeComponents(View v) {

        btnSearch = v.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(view -> {
            btnSearch_Click();
        });

        btnBack = v.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(view -> {
            hutilities.hideKeyboard(context, txtSearch);
            ((AppCompatActivity) context).onBackPressed();
        });

        pageProgressBar = v.findViewById(R.id.progressBar);

        divSearch = v.findViewById(R.id.divSearch);
        divSearch.setVisibility(View.GONE);

        //adapterSearch = new TTClubNameDTOsAdapter(this, null);

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

        txtSearch = v.findViewById(R.id.txtSearch);
        txtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    btnSearch_Click();
                    return true;
                }
                return false;
            }
        });

        linFilterCity = v.findViewById(R.id.linFilterCity);
        lblFilterCity = v.findViewById(R.id.lblFilterCity);
        linFilterCity.setOnClickListener(view -> {
            showSelectCityDialog();
        });
    }

    int SelectedCityId = 0;
    int SelectedProvinceId = 0;
    void showSelectCityDialog(){
        SelectCityDialog dialogBuilder = new SelectCityDialog(this);
        AlertDialog.Builder alertDialogBuilder = dialogBuilder.GetBuilder(context
                ,(selected, position) ->{
                    CityDTO cityDTO = (CityDTO) selected;
                    lblFilterCity.setText(cityDTO.Name);
                    SelectedCityId = cityDTO.CityId;
                    btnSearch_Click();
                    dialogBuilder.alertDialog.dismiss();

                }, view -> {
                    int currentSelectedCityId = SelectedCityId;
                    lblFilterCity.setText(getString(R.string.Filter_NoFilter));
                    SelectedCityId = 0;
                    if (currentSelectedCityId != SelectedCityId)
                        btnSearch_Click();
                }, view2 -> {
                    dialogBuilder.alertDialog.dismiss();
                });
        dialogBuilder.alertDialog = alertDialogBuilder.create();
        dialogBuilder.alertDialog.show();
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
        requestDTO.Filter = txtSearch.getText().toString() + "***" + SelectedProvinceId+ "***" + SelectedCityId;
        if (txtSearch.getText().toString().trim().length() > 0 || SelectedCityId > 0 || SelectedProvinceId > 0)
            requestDTO.Sort = "FollowerCount desc, name asc";

        Call<ResponseBody> call = dbConstantsTara.apiTara.GetClubNamesWithFilter(SimpleRequest.getInstance(requestDTO));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!isAdded()) return;
                try {
                    divSearch.setVisibility(View.VISIBLE);
                    pageProgressBar.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        TTClubNameDTOList result = TTClubNameDTOList.fromBytes(response.body().bytes());
                        if (!result.isOk) {
                            txtSearchResult.setVisibility(View.VISIBLE);
                            txtSearchResult.setText(result.message);
                        } else {
                            txtSearchResult.setVisibility(View.GONE);
                        }
                        if (result.resList.size() > 0) {
                            rvSearchResult.setVisibility(View.VISIBLE);
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
                    TTExceptionLogSQLite.insert(ex.getMessage(), "", PrjConfig.frmClubSearch, 101);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                TTExceptionLogSQLite.insert(t.getMessage(), "", PrjConfig.frmClubSearch, 100);
                if (!isAdded()) return;
                divSearch.setVisibility(View.VISIBLE);
                pageProgressBar.setVisibility(View.GONE);
                txtSearchResult.setVisibility(View.VISIBLE);
                txtSearchResult.setText("متاسفانه مشکلی در برقراری ارتباط با سرور به وجود آمده است. لطفا بعدا مجددا تلاش نمایید.");

            }
        });


    }

    void initAdapterSearch(List<TTClubNameDTO> result) {
        if (true || adapterSearch == null) {

            TTClubNameDTOsAdapter.OnItemClickListener itemClickListener = (post, Position) -> {
                RecyclerView_ItemClicked(post, Position);
            };
            adapterSearch = new TTClubNameDTOsAdapter(context, "full",itemClickListener, null, this);
            adapterSearch.setData(result);
            rvSearchResult.setAdapter(adapterSearch);
        }
        if (rvSearchResult.getAdapter() == null) {

        }
    }

    public void RecyclerView_ItemClicked(TTClubNameDTO current, int position){
        TTClubNameDTO currentSelectedClub  = adapterSearch.data.get(position);
        hutilities.hideKeyboard((Activity)context);
        currentSelectedClubIndex = position;
        ClubView_Home currentHomeFragment = ClubView_Home.getInstance(currentSelectedClub, PrjConfig.frmClubSearch);
        ((MainActivityManager) context).showFragment(currentHomeFragment);
    }


    void initAdapter(List<TTClubNameDTO> allTours) {
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
    public static class TTClubNameDTOsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<TTClubNameDTO> data;
        private Context context;
        private LayoutInflater layoutInflater;
        private OnDeleteButtonClickListener onDeleteButtonClickListener;
        OnItemClickListener itemClickFunction;
        String mode;
        Fragment parent;

        public TTClubNameDTOsAdapter(Context context, String _mode, OnItemClickListener _itemClickFunction, OnDeleteButtonClickListener listener, Fragment parent) {
            this.mode = _mode;
            this.parent = parent;
            this.data = new ArrayList<>();
            this.context = context;
            this.onDeleteButtonClickListener = listener;
            this.itemClickFunction = _itemClickFunction;
        }

        @Override
        public TTClubViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            layoutInflater = LayoutInflater.from(context);
            //this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = layoutInflater.inflate(R.layout.frm_clubsearch_item, parent, false);
            return new TTClubViewHolder(itemView, this.itemClickFunction, this.parent);
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

        public void setData(List<TTClubNameDTO> newData) {
            if (data != null) {
//                TTClubNameDTODiffCallback postDiffCallback = new TTClubNameDTODiffCallback(data, newData);
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
            public abstract void onDeleteButtonClicked(TTClubNameDTO post);
        }
        //من اضافه کردم
        public abstract interface OnItemClickListener {
            public abstract void onItemClicked(TTClubNameDTO post, int Position);
        }

        class TTClubViewHolder extends RecyclerView.ViewHolder {
            public View itemView;
            public TextView txtTitle, txtCityAndProvince, btnFollow, btnIamFan;
            ImageView txt_ct_ImageLinkUri;
            public ProgressBar progressBarIndet;
            LinearLayout itemMainPart;
            OnItemClickListener itemClickFunction;
            Fragment parent;
            TTClubViewHolder(View itemView, OnItemClickListener itemClickFunction, Fragment parent) {
                super(itemView);
                this.parent = parent;
                this.itemView = itemView;
                this.itemClickFunction = itemClickFunction;
                txtTitle = itemView.findViewById(R.id.txtTitle);
                itemMainPart = itemView.findViewById(R.id.itemMainPart);
                txtCityAndProvince = itemView.findViewById(R.id.txtCityAndProvince);
                btnIamFan = itemView.findViewById(R.id.btnIamFan);
                btnFollow = itemView.findViewById(R.id.btnFollow);
                txt_ct_ImageLinkUri = itemView.findViewById(R.id.txtImage);
                progressBarIndet = itemView.findViewById(R.id.itemprogressbarIndet);

                Drawable progressDrawable = progressBarIndet.getIndeterminateDrawable().mutate(); //or getProgressDrawable for normal //https://stackoverflow.com/questions/2020882/how-to-change-progress-bars-progress-color-in-android
                progressDrawable.setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
                progressBarIndet.setProgressDrawable(progressDrawable);

                txtCityAndProvince.setTypeface(projectStatics.getIranSans_FONT(context));
                txtTitle.setTypeface(projectStatics.getIranSans_FONT(context));
                btnFollow.setTypeface(projectStatics.getIranSans_FONT(context));
                btnIamFan.setTypeface(projectStatics.getFontello_FONT(context));

                if (mode.equals("simple")){
                    btnFollow.setVisibility(View.GONE);
                    btnIamFan.setVisibility(View.GONE);
                }

            }


            void bind(final TTClubNameDTO currentObj, int position) {
                if (currentObj != null) {
                    txtTitle.setText(currentObj.Name);
                    txtCityAndProvince.setText(currentObj.ProvinceName + (currentObj.CityName.length() > 0 ? "، " + currentObj.CityName : ""));

                    ClubSearch.ReformatBtnFollow(btnFollow, currentObj.FollowingByMe, currentObj.NotificationStatus, null, followingText, followText, context);

                    ClubSearch.ReformatBtnIamFan(btnIamFan, currentObj.FanByMe, context);

                    itemMainPart.setOnClickListener(view -> {
                        itemClickFunction.onItemClicked(currentObj, position);

//                        TTClubNameDTO currentSelectedClub  = adapterSearch.data.get(position);
//                        hutilities.hideKeyboard((Activity)context);
//                        currentSelectedClubIndex = position;
//                        ClubView_Home currentHomeFragment = new ClubView_Home(currentSelectedClub, PrjConfig.frmClubSearch);
//                        ((MainActivityManager) context).showFragment(currentHomeFragment);
                    });

                    btnIamFan.setOnClickListener(view -> {
                        btnIamFan_Click(currentObj, context, progressBarIndet, btnIamFan, null, this.parent);
                    });
                    btnFollow.setOnClickListener(view -> {
                        btnFollow_Click(currentObj, context, progressBarIndet, btnFollow, null, null, followingText, followText, parent);
                    });

                    Picasso builder = Picasso.with(context);
                    builder.load(currentObj.Logo).tag(PicassoOnScrollListener.TAG)//.config(Bitmap.Config.RGB_565).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                            //.placeholder((R.drawable.ic_launcher_background)) HHH 1400-01-10
                            //.error(R.drawable.ic_launcher_background)  HHH 1400-01-10
                            .transform(new PicassoCircleTransform()).into(txt_ct_ImageLinkUri);
                }
            }
        }

        final String TAG = "Downloading...";

        class TTClubNameDTODiffCallback extends DiffUtil.Callback {

            private final List<TTClubNameDTO> oldTTClubNameDTOs, newTTClubNameDTOs;

            public TTClubNameDTODiffCallback(List<TTClubNameDTO> oldTTClubNameDTOs, List<TTClubNameDTO> newTTClubNameDTOs) {
                this.oldTTClubNameDTOs = oldTTClubNameDTOs;
                this.newTTClubNameDTOs = newTTClubNameDTOs;
            }

            @Override
            public int getOldListSize() {
                return oldTTClubNameDTOs.size();
            }

            @Override
            public int getNewListSize() {
                return newTTClubNameDTOs.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return oldTTClubNameDTOs.get(oldItemPosition).TTClubNameId == newTTClubNameDTOs.get(newItemPosition).TTClubNameId;
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return oldTTClubNameDTOs.get(oldItemPosition).equals(newTTClubNameDTOs.get(newItemPosition));
            }
        }
    }

    public static boolean btnFollow_Click(TTClubNameDTO currentObj, Context context, ProgressBar progressBar, TextView btnFollow, TextView btnBell, TextView txtFollowerCount, String followingText, String followText, Fragment parent) {
        if (!hutilities.isInternetConnected(context)) {
            projectStatics.showDialog(context, context.getResources().getString(R.string.NoInternet), context.getResources().getString(R.string.NoInternet_Desc), context.getResources().getString(R.string.ok), view -> {}, "", null);
            return false;
        }
        if (hutilities.CCustomerId == 0) {
            projectStatics.showDialog(context, context.getResources().getString(R.string.need_loginOrRegister)
                    , context.getResources().getString(R.string.need_loginOrRegister_Desc)
                    , context.getResources().getString(R.string.ok)
                    , view -> {((MainActivityManager)context).showLoginProcess("clubsearch");}, "", null);

            return false;
        }
        Object tag = btnFollow.getTag();
        if (tag != null && tag.toString().equals("loading")) {
            return false;
        }
        btnFollow.setTag("loading");
        byte nValueRequest = 0;
        if (currentObj.FollowingByMe == 1) {
            nValueRequest = 2;
        } else
            nValueRequest = 1;

        StringContentDTO contentDTO = StringContentDTO.getInstance(currentObj.TTClubNameId + "***" + nValueRequest);
        progressBar.setVisibility(View.VISIBLE);

        SimpleRequest request = SimpleRequest.getInstance(contentDTO);
        Call<ResponseBody> call = dbConstantsTara.apiTara.DoFollowClub(request);
        byte finalNValueRequest = nValueRequest;
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!parent.isAdded()) return;
                try {
                    if (response.isSuccessful()) {
                        InsUpdRes res = InsUpdRes.fromBytes(response.body().bytes());
                        if (res.isOk) {
                            currentObj.FollowingByMe = finalNValueRequest;
                            if (currentObj.FollowingByMe == 1)
                                currentObj.NotificationStatus = 1;

                            ReformatBtnFollow(btnFollow, currentObj.FollowingByMe, currentObj.NotificationStatus, btnBell, followingText, followText, context);
                            currentObj.FollowerCount = Integer.parseInt(res.resValue.substring(0, res.resValue.indexOf("***")));
                            if (txtFollowerCount != null) {
                                txtFollowerCount.setText(Integer.toString(currentObj.FollowerCount));
                            }
                        } else {
                            projectStatics.showDialog(context, context.getResources().getString(R.string.dialog_UnknownError), res.message, context.getResources().getString(R.string.ok)
                                    , null, "", null);
                        }
                    } else {
                        projectStatics.ManageCallResponseErrors(true, PrjConfig.frmClubSearch,  100, context, response.code());
                    }
                } catch (Exception ex) {ex.printStackTrace();}

                btnFollow.setTag("");
                progressBar.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                projectStatics.ManageCallExceptions(true, PrjConfig.frmClubSearch,  100, t, context);
                if (!parent.isAdded()) return;
                btnFollow.setTag("");
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

        return true;
    }
    public static boolean btnIamFan_Click(TTClubNameDTO currentObj, Context context, ProgressBar progressBar, TextView btnIamFan, TextView txtFanCount, Fragment parent) {
        if (hutilities.CCustomerId == 0) {
            projectStatics.showDialog(context, context.getResources().getString(R.string.need_loginOrRegister)
                    , context.getResources().getString(R.string.need_loginOrRegister_Desc)
                    , context.getResources().getString(R.string.ok)
                    , view -> {((MainActivityManager)context).showLoginProcess("clubsearch");}, "", null);

            return false;
        }
        if (!hutilities.isInternetConnected(context)) {
            projectStatics.showDialog(context, context.getResources().getString(R.string.NoInternet), context.getResources().getString(R.string.NoInternet_Desc), context.getResources().getString(R.string.ok), view -> {}, "", null);
            return false;
        }
        Object tag = btnIamFan.getTag();
        if (tag != null && tag.toString().equals("loading")) {
            return false;
        }
        btnIamFan.setTag("loading");
        byte nValueRequest = 0;
        if (currentObj.FanByMe == 1) {
            nValueRequest = 2;
        } else
            nValueRequest = 1;

        StringContentDTO contentDTO = StringContentDTO.getInstance(currentObj.TTClubNameId + "***" + nValueRequest);
        progressBar.setVisibility(View.VISIBLE);

        SimpleRequest request = SimpleRequest.getInstance(contentDTO);
        Call<ResponseBody> call = dbConstantsTara.apiTara.DoMakeFanOfClub(request);
        byte finalNValueRequest = nValueRequest;
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!parent.isAdded()) return;
                try {
                    if (response.isSuccessful()) {
                        InsUpdRes res = InsUpdRes.fromBytes(response.body().bytes());
                        if (res.isOk) {
                            currentObj.FanByMe = finalNValueRequest;

                            ReformatBtnIamFan(btnIamFan, currentObj.FanByMe, context);
                            currentObj.FanCount = Integer.parseInt(res.resValue.substring(0, res.resValue.indexOf("***")));

                            if (txtFanCount != null) {
                                txtFanCount.setText(Integer.toString(currentObj.FanCount));
                            }
                        } else {
                            projectStatics.showDialog(context, context.getResources().getString(R.string.dialog_UnknownError), res.message, context.getResources().getString(R.string.ok)
                                    , null, "", null);
                        }
                    } else {
                        projectStatics.ManageCallResponseErrors(true, PrjConfig.frmClubSearch,  100, context, response.code());
                    }
                } catch (Exception ex) {ex.printStackTrace();}

                btnIamFan.setTag("");
                progressBar.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                projectStatics.ManageCallExceptions(true, PrjConfig.frmClubSearch,  100, t, context);
                if (!parent.isAdded()) return;
                btnIamFan.setTag("");
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

        return true;
    }
    public static boolean btnNotificationStatus_Click(TTClubNameDTO currentObj, Context context, ProgressBar progressBar, TextView btnBell, Fragment parent) {
        if (hutilities.CCustomerId == 0) {
            projectStatics.showDialog(context, context.getResources().getString(R.string.need_loginOrRegister), context.getResources().getString(R.string.need_loginOrRegister_Desc), context.getResources().getString(R.string.ok), view -> {((MainActivityManager)context).showLoginProcess("clubsearch");}, "", null);
            return false;
        }
        Object tag = btnBell.getTag();
        if (tag != null && tag.toString().equals("loading")) {
            return false;
        }
        btnBell.setTag("loading");
        byte nValueRequest = 0;
        if (currentObj.NotificationStatus == 1) {
            nValueRequest = 2;
        } else
            nValueRequest = 1;

        StringContentDTO contentDTO = StringContentDTO.getInstance(currentObj.TTClubNameId + "***" + nValueRequest);
        progressBar.setVisibility(View.VISIBLE);

        SimpleRequest request = SimpleRequest.getInstance(contentDTO);
        Call<ResponseBody> call = dbConstantsTara.apiTara.DoChangeNotificationsOfClub(request);
        byte finalNValueRequest = nValueRequest;
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!parent.isAdded()) return;
                try {
                    if (response.isSuccessful()) {
                        InsUpdRes res = InsUpdRes.fromBytes(response.body().bytes());
                        if (res.isOk) {
                            currentObj.NotificationStatus = finalNValueRequest;

                            if(currentObj.NotificationStatus == 0 || currentObj.NotificationStatus == 1)
                                btnBell.setText(dbConstantsTara.BellOn);
                            else
                                btnBell.setText(dbConstantsTara.BellOff);

                        } else {
                            projectStatics.showDialog(context, context.getResources().getString(R.string.dialog_UnknownError), res.message, context.getResources().getString(R.string.ok)
                                    , null, "", null);
                        }
                    } else {
                        projectStatics.ManageCallResponseErrors(true, PrjConfig.frmClubSearch,  100, context, response.code());
                    }
                } catch (Exception ex) {ex.printStackTrace();}

                btnBell.setTag("");
                progressBar.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                projectStatics.ManageCallExceptions(true, PrjConfig.frmClubSearch,  100, t, context);
                if (!parent.isAdded()) return;
                btnBell.setTag("");
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {//3nd Event
        return inflater.inflate(R.layout.frm_clubsearch, parent, false);
    }
}