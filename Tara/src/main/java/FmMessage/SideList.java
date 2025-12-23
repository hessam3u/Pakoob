package FmMessage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.pakoob.tara.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import bo.dbConstantsMap;
import bo.entity.FmSidesList;
import bo.entity.SearchRequestDTO;
import bo.NewClasses.SimpleRequest;
import bo.entity.FmSides;
import bo.sqlite.TTExceptionLogSQLite;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import UI.HFragment;
import utils.MainActivityManager;
import utils.MyDate;
import utils.PicassoCircleTransform;
import utils.PicassoTrustAll;
import utils.PrjConfig;
import utils.PrjEnums;
import utils.RecyclerTouchListener;
import utils.hutilities;
import utils.projectStatics;

public class SideList extends HFragment {
    int currentSelectedClubIndex = -1;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {//4th Event
        super.onViewCreated(view, savedInstanceState);

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
    private FmSidessAdapter adapterSearch;
    private RecyclerView rvSearchResult;
    private ProgressBar pageProgressBar;
    boolean isLoadingMore = false;
    final Integer readPageSize = 100;
    TextView txtSearchResult;

    @Override
    public void initializeComponents(View v) {

//        btnSearch = v.findViewById(R.id.btnSearch);
//        btnSearch.setOnClickListener(view -> {
//            btnSearch_Click();
//        });

        btnBack = v.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(view -> {
            if (txtSearch != null)
                hutilities.hideKeyboard(context, txtSearch);
            ((AppCompatActivity) context).onBackPressed();
        });

        pageProgressBar = v.findViewById(R.id.progressBar);

        divSearch = v.findViewById(R.id.divSearch);
        divSearch.setVisibility(View.GONE);

        //adapterSearch = new FmSidessAdapter(this, null);

        rvSearchResult = v.findViewById(R.id.rvSearchResult);
        rvSearchResult.setLayoutManager(new LinearLayoutManager(context));
        rvSearchResult.setHasFixedSize(true);
        rvSearchResult.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);//TourList.this bayad mibood
        rvSearchResult.setLayoutManager(layoutManager);
        initRecyclerView();
//            //baraye namayeshe joda konandeh
            rvSearchResult.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));//parentActivity -> getActivity -> this bood

        //rvSearchResult.setAdapter(adapterSearch);
        initAdapterSearch(new ArrayList<>());

        txtSearchResult = v.findViewById(R.id.txtSearchResult);
        txtSearchResult.setVisibility(View.GONE);

//        txtSearch = v.findViewById(R.id.txtSearch);
//        txtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    btnSearch_Click();
//                    return true;
//                }
//                return false;
//            }
//        });
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
        requestDTO.FromItemIndex = 0;
        requestDTO.PageSize = 300;

        Call<ResponseBody> call = dbConstantsMap.apiFcm.GetSides(SimpleRequest.getInstance(requestDTO));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!isAdded()) return;
                try {
                    divSearch.setVisibility(View.VISIBLE);
                    pageProgressBar.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        FmSidesList result = FmSidesList.fromBytes(response.body().bytes());
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
                            txtSearchResult.setVisibility(View.VISIBLE);
                            txtSearchResult.setText(context.getString(R.string.NoMessageToShow));
                        }
                    } else {
                        txtSearchResult.setVisibility(View.VISIBLE);
                        txtSearchResult.setText("متاسفانه در برقراری ارتباط با سرور مشکلی به وجود آمده است. لطفا بعدا مجددا تلاش نمایید.");
                        Log.e("MY_ERROR", "ResponseCODE: " + response.code());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Log.e("MY_ERROR", ex.getMessage());
                    TTExceptionLogSQLite.insert(ex.getMessage(), stktrc2k(ex), PrjConfig.frmSideList, 101);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                TTExceptionLogSQLite.insert(t.getMessage(), stktrc2kt(t), PrjConfig.frmSideList, 100);
                if (!isAdded()) return;
                divSearch.setVisibility(View.VISIBLE);
                pageProgressBar.setVisibility(View.GONE);
                txtSearchResult.setVisibility(View.VISIBLE);
                txtSearchResult.setText("متاسفانه مشکلی در برقراری ارتباط با سرور به وجود آمده است. لطفا بعدا مجددا تلاش نمایید.");

            }
        });


    }

    void initAdapterSearch(List<FmSides> result) {
        if (true || adapterSearch == null) {
            adapterSearch = new FmSidessAdapter(context, null);
            adapterSearch.setData(result);
            rvSearchResult.setAdapter(adapterSearch);
        }
        if (rvSearchResult.getAdapter() == null) {

        }
    }


    void initAdapter(List<FmSides> allTours) {
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
    public void onResume() {
        Log.d("onPauseOrResume", "resume fired=" + currentSelectedClubIndex);

        if (currentSelectedClubIndex != -1){
            adapterSearch.notifyItemChanged(currentSelectedClubIndex);// NOT WORKING, So I used currentViewHolder

            currentSelectedClubIndex = -1;
        }
        super.onResume();
    }

    public class FmSidessAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<FmSides> data;
        private Context context;
        private LayoutInflater layoutInflater;
        private OnDeleteButtonClickListener onDeleteButtonClickListener;

        public FmSidessAdapter(Context context, OnDeleteButtonClickListener listener) {
            this.data = new ArrayList<>();
            this.context = context;
            this.onDeleteButtonClickListener = listener;
        }

        @Override
        public TTClubViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            layoutInflater = LayoutInflater.from(context);
            //this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = layoutInflater.inflate(R.layout.frm_fmsides_item, parent, false);
            return new TTClubViewHolder(itemView);
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

        public void setData(List<FmSides> newData) {
            if (data != null) {
//                FmSidesDiffCallback postDiffCallback = new FmSidesDiffCallback(data, newData);
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
            public abstract void onDeleteButtonClicked(FmSides post);
        }

        class TTClubViewHolder extends RecyclerView.ViewHolder {
            public View itemView;
            public TextView txtTitle, txtLastMessageText, txtLastMessageDate, txtUnreadCount;
            ImageView txt_ct_ImageLinkUri;
            public ProgressBar progressBarIndet;
            LinearLayout itemMainPart;

            TTClubViewHolder(View itemView) {
                super(itemView);
                this.itemView = itemView;
                txtTitle = itemView.findViewById(R.id.txtTitle);
                itemMainPart = itemView.findViewById(R.id.itemMainPart);
                txtLastMessageText = itemView.findViewById(R.id.txtLastMessageText);
                txt_ct_ImageLinkUri = itemView.findViewById(R.id.txtImage);
                progressBarIndet = itemView.findViewById(R.id.itemprogressbarIndet);
                txtLastMessageDate = itemView.findViewById(R.id.txtLastMessageDate);
                txtUnreadCount = itemView.findViewById(R.id.txtUnreadCount);

                Drawable progressDrawable = progressBarIndet.getIndeterminateDrawable().mutate(); //or getProgressDrawable for normal //https://stackoverflow.com/questions/2020882/how-to-change-progress-bars-progress-color-in-android
                progressDrawable.setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
                progressBarIndet.setProgressDrawable(progressDrawable);

                txtLastMessageText.setTypeface(projectStatics.getIranSans_FONT(context));
                txtTitle.setTypeface(projectStatics.getIranSans_FONT(context));
            }


            void bind(final FmSides currentObj, int position) {
                if (currentObj != null) {
                    txtTitle.setText(currentObj.Name);
                    txtLastMessageText.setText(currentObj.Text1.length() > 50? currentObj.Text1.substring(0, 50) + "...": currentObj.Text1);
                    Calendar LastDate = currentObj.getLastDate();
                    if (LastDate != null) {
                        Calendar now = Calendar.getInstance();
                        long totalMillis = now.getTimeInMillis() - LastDate.getTimeInMillis();
                        int hours = (int)(totalMillis / 1000) / 3600;

                        String timeString = MyDate.CalendarToTimeString(LastDate, MyDate.TimeToStringFormat.HourMin, ":");
                        if (hours < 24 && now.get(Calendar.DAY_OF_MONTH)  == LastDate.get(Calendar.DAY_OF_MONTH))
                            txtLastMessageDate.setText(timeString);
                        else if (hours < 168)
                            txtLastMessageDate.setText(MyDate.getDayOfWeekInPersianFromCalendar(LastDate) + " " + timeString);
                        else {
                            txtLastMessageDate.setText(MyDate.CalendarToPersianDateString(LastDate, MyDate.DateToStringFormat.yyyymmdd, "/") + " " + timeString);
                        }
                    }
                    else {
                        txtLastMessageDate.setText("بدون پیام");
                    }

                    if (currentObj.UnreadCount > 0)
                        txtUnreadCount.setText(String.valueOf(currentObj.UnreadCount));
                    else
                        txtUnreadCount.setText("");

                    if (currentObj.UnreadCount > 0){
                        itemMainPart.setBackgroundColor(getResources().getColor(R.color.UnreadMessageBackground));
                    }
                    else{
                        itemMainPart.setBackgroundColor(getResources().getColor(R.color.NormalMessageBackground));
                    }
                    itemMainPart.setOnClickListener(view -> {
                        FmSides cObj  = adapterSearch.data.get(position);
                        hutilities.hideKeyboard((Activity)context);
                        currentSelectedClubIndex = position;
                        //این دو خط میتونه اضافه محسوب بشه
                        itemMainPart.setBackgroundColor(getResources().getColor(R.color.NormalMessageBackground));
                        txtUnreadCount.setText("");
                        ((MainActivityManager) context).showFragment( new FmMessageStory(cObj));
                        currentObj.UnreadCount = 0;
                        adapterSearch.notifyItemChanged(position);
                    });

                    //cmt at 1403-04-22
//                    Picasso builder = Picasso.get();//#PicassoUpdate140303 with(context)
//                    builder.load(currentObj.Avatar).tag(PicassoOnScrollListener.TAG)//.config(Bitmap.Config.RGB_565).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
//                            .transform(new PicassoCircleTransform()).into(txt_ct_ImageLinkUri);

                    if (picassoInstance == null)
                        picassoInstance = PicassoTrustAll.getInstance(context);
                    picassoInstance.load(currentObj.Avatar)
                            .error(R.drawable.ac_hiking)
                            .transform(new PicassoCircleTransform()).into(txt_ct_ImageLinkUri);

                }
            }
        }
        Picasso picassoInstance;
        final String TAG = "Downloading...";

        class FmSidesDiffCallback extends DiffUtil.Callback {

            private final List<FmSides> oldFmSidess, newFmSidess;

            public FmSidesDiffCallback(List<FmSides> oldFmSidess, List<FmSides> newFmSidess) {
                this.oldFmSidess = oldFmSidess;
                this.newFmSidess = newFmSidess;
            }

            @Override
            public int getOldListSize() {
                return oldFmSidess.size();
            }

            @Override
            public int getNewListSize() {
                return newFmSidess.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return oldFmSidess.get(oldItemPosition).FmSidesId == newFmSidess.get(newItemPosition).FmSidesId;
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return oldFmSidess.get(oldItemPosition).equals(newFmSidess.get(newItemPosition));
            }
        }
    }

    Context context;

    @Override
    public void onAttach(Context _context) { //1st Event
        super.onAttach(context);
        this.context = _context;
    }


    //تنظیمات مربوط به صفحه --------------
    @Override
    protected int getScreenId() {return PrjConfig.frmSideList;}
    @Override
    protected String tag() {return SCREEN_TAG;}
    public static final String SCREEN_TAG = "SideList";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {//3nd Event
        return inflater.inflate(R.layout.frm_fmsides, parent, false);
    }
}