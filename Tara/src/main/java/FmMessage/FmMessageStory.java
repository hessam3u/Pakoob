package FmMessage;

import android.app.AlertDialog;
import android.content.Context;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import bo.dbConstantsMap;
import bo.entity.FmMessage;
import bo.entity.FmMessageList;
import bo.entity.FmSides;
import bo.NewClasses.InsUpdRes;
import bo.entity.SearchRequestDTO;
import bo.NewClasses.SimpleRequest;
import bo.NewClasses.StringContentDTO;
import bo.sqlite.TTExceptionLogSQLite;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.HFragment;
import utils.MainActivityManager;
import utils.MyDate;
import utils.PicassoCircleTransform;
import utils.PicassoOnScrollListener;
import utils.PrjConfig;
import utils.PrjEnums;
import utils.RecyclerTouchListener;
import utils.hutilities;
import utils.projectStatics;

public class FmMessageStory extends HFragment {
    int currentSelectedClubIndex = -1;
    long SenderId;
    byte RecType;
    FmSides recSide;

    public FmMessageStory(FmSides recSide){
        this.recSide = recSide;
        this.SenderId = recSide.FmChanalId > 0?recSide.FmChanalId:(recSide.UserSide1 == hutilities.CCustomerId?recSide.UserSide2:recSide.UserSide1);
        this.RecType = recSide.FmChanalId == 0? FmMessage.RecTypes_کاربرخاص:FmMessage.RecTypes_کانال_خاص;
        if (recSide.UnreadCount > 0){
            MakeReadMessages(SenderId, RecType, recSide.AnonymosType);
        }
    }

    private void MakeReadMessages(long senderId, byte recType, byte anonymosType) {

        StringContentDTO contentDTO = StringContentDTO.getInstance(senderId + "***" + recType+ "***" +anonymosType);

        SimpleRequest request = SimpleRequest.getInstance(contentDTO);
        Call<ResponseBody> call = dbConstantsMap.apiFcm.MakeReadMessages(request);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!isAdded()) return;
                try {
                    if (response.isSuccessful()) {
                        InsUpdRes res = InsUpdRes.fromBytes(response.body().bytes());
                        if (res.isOk) {

                        } else {
                        }
                    } else {
                        projectStatics.ManageCallResponseErrors(false, PrjConfig.frmClubSearch,  100, context, response.code());
                    }
                } catch (Exception ex) {ex.printStackTrace();}
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                projectStatics.ManageCallExceptions(false, PrjConfig.frmClubSearch,  100, t, context);
                if (!isAdded()) return;
            }
        });
    }

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

    TextView btnBack, txtPageTitle;
    LinearLayout divSearch;
    Toolbar toolbar;
    TextView btnSearch, btnFindByLocation;
    ImageView txtAvatar;

    TextInputEditText txtSearch;
    private FmMessagesAdapter adapterSearch;
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

        txtPageTitle = v.findViewById(R.id.txtPageTitle);
        txtPageTitle.setText(recSide.Name);

        btnBack = v.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(view -> {
            //hutilities.hideKeyboard(context, txtSearch);
            ((AppCompatActivity) context).onBackPressed();
        });

        pageProgressBar = v.findViewById(R.id.progressBar);

        divSearch = v.findViewById(R.id.divSearch);
        divSearch.setVisibility(View.GONE);

        //adapterSearch = new FmMessagesAdapter(this, null);

        rvSearchResult = v.findViewById(R.id.rvSearchResult);
        rvSearchResult.setLayoutManager(new LinearLayoutManager(context));
        rvSearchResult.setHasFixedSize(true);
        rvSearchResult.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);//TourList.this bayad mibood
        rvSearchResult.setLayoutManager(layoutManager);
        layoutManager.setStackFromEnd(true);
//        layoutManager.setReverseLayout(true);


        initRecyclerView();
//            //baraye namayeshe joda konandeh
//            rvSearchResult.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));//parentActivity -> getActivity -> this bood

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

        txtAvatar = v.findViewById(R.id.txtAvatar);
        Picasso builder = Picasso.with(context);
        builder.load(recSide.Avatar).tag(PicassoOnScrollListener.TAG)//.config(Bitmap.Config.RGB_565).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                //.placeholder((R.drawable.ic_launcher_background)) HHH 1400-01-10
                //.error(R.drawable.ic_launcher_background)  HHH 1400-01-10
                .transform(new PicassoCircleTransform()).into(txtAvatar);

        super.initializeComponents(v);
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
        requestDTO.OtherCommand = Long.toString(SenderId) + ";" + Byte.toString(RecType)+";" + Byte.toString(recSide.AnonymosType);
        requestDTO.FromItemIndex = 0;
        requestDTO.PageSize = 50;

        Call<ResponseBody> call = dbConstantsMap.apiFcm.GetMessages(SimpleRequest.getInstance(requestDTO));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!isAdded()) return;
                try {
                    divSearch.setVisibility(View.VISIBLE);
                    pageProgressBar.setVisibility(View.GONE);
                    if (response.isSuccessful()) {

                        FmMessageList result = FmMessageList.fromBytes(response.body().bytes());
                        if (!result.isOk) {
                            txtSearchResult.setVisibility(View.VISIBLE);
                            txtSearchResult.setText(result.message);
                        } else {
                            txtSearchResult.setVisibility(View.GONE);
                        }
                        Log.e("User", "OK:" + result.resList.size());
                        if (result.resList.size() > 0) {
                            rvSearchResult.setVisibility(View.VISIBLE);
                            initAdapterSearch(result.resList);
                            //برای رفتن به آخرین آیتم
                            //scrollToBottom(rvSearchResult);
//                            rvSearchResult.getLayoutManager().scrollToPosition(result.resList.size() - 1);
//                            rvSearchResult.scrollToPosition(result.resList.size() - 1);
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
                    TTExceptionLogSQLite.insert(ex.getMessage(), "", PrjConfig.frmFmMessageStory, 101);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                TTExceptionLogSQLite.insert(t.getMessage(), "", PrjConfig.frmFmMessageStory, 100);
                if (!isAdded()) return;
                divSearch.setVisibility(View.VISIBLE);
                pageProgressBar.setVisibility(View.GONE);
                txtSearchResult.setVisibility(View.VISIBLE);
                txtSearchResult.setText("متاسفانه مشکلی در برقراری ارتباط با سرور به وجود آمده است. لطفا بعدا مجددا تلاش نمایید.");

            }
        });


    }

    void initAdapterSearch(List<FmMessage> result) {
        if (true || adapterSearch == null) {
            adapterSearch = new FmMessagesAdapter(context, null);
            adapterSearch.setData(result);
            rvSearchResult.setAdapter(adapterSearch);
        }
        if (rvSearchResult.getAdapter() == null) {

        }
    }


    void initAdapter(List<FmMessage> allTours) {
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

    public class FmMessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<FmMessage> data;
        private Context context;
        private LayoutInflater layoutInflater;
        private OnDeleteButtonClickListener onDeleteButtonClickListener;

        public FmMessagesAdapter(Context context, OnDeleteButtonClickListener listener) {
            this.data = new ArrayList<>();
            this.context = context;
            this.onDeleteButtonClickListener = listener;
        }

        @Override
        public TTClubViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            layoutInflater = LayoutInflater.from(context);
            //this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = layoutInflater.inflate(R.layout.frm_fmmessagestory_item, parent, false);
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

        public void setData(List<FmMessage> newData) {
            if (data != null) {
//                FmMessageDiffCallback postDiffCallback = new FmMessageDiffCallback(data, newData);
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
            public abstract void onDeleteButtonClicked(FmMessage post);
        }

        class TTClubViewHolder extends RecyclerView.ViewHolder {
            public View itemView;
            public TextView txtText1, txtSendDate, txtCommand;
            ImageView txt_ct_ImageLinkUri;
            public ProgressBar progressBarIndet;
            LinearLayout itemMainPart, itemOuterPart;

            TTClubViewHolder(View itemView) {
                super(itemView);
                this.itemView = itemView;
                txtText1 = itemView.findViewById(R.id.txtText1);
                itemMainPart = itemView.findViewById(R.id.itemMainPart);
                itemOuterPart = itemView.findViewById(R.id.itemOuterPart);
                txtSendDate = itemView.findViewById(R.id.txtSendDate);
                txtCommand = itemView.findViewById(R.id.txtCommand);

                txtSendDate.setTypeface(projectStatics.getIranSans_FONT(context));
                txtText1.setTypeface(projectStatics.getIranSans_FONT(context));
                txtCommand.setTypeface(projectStatics.getIranSans_FONT(context));
            }


            void bind(final FmMessage currentObj, int position) {
                if (currentObj != null) {
                    txtText1.setText(currentObj.Text1);

                    Calendar LastDate = currentObj.getSendDate();

//                    "2021-06-09 20:14:18"
                    if (LastDate != null) {
                        Calendar now = Calendar.getInstance();
                        long totalMillis = now.getTimeInMillis() - LastDate.getTimeInMillis();
                        int hours = (int)(totalMillis / 1000) / 3600;

//                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Log.e("ساعتها", String.valueOf(hours));
//                        Log.e("Time1", dateFormat.format(LastDate.getTime()));
//                        Log.e("Time2", dateFormat.format(now.getTime()));
                        String timeString = MyDate.CalendarToTimeString(LastDate, MyDate.TimeToStringFormat.HourMin, ":");
                        if (hours < 24 )//&& now.get(Calendar.DAY_OF_MONTH)  == LastDate.get(Calendar.DAY_OF_MONTH)
                            txtSendDate.setText(timeString);
                        else if (hours < 168)
                            txtSendDate.setText(MyDate.getDayOfWeekInPersianFromCalendar(LastDate) + " " + timeString);
                        else {
                            txtSendDate.setText(MyDate.CalendarToPersianDateString(LastDate, MyDate.DateToStringFormat.yyyymmdd, "/") + " " + timeString);
                        }
                    }
                    if (currentObj.OpenAction == FmMessage.OpenAction_OpenInApp
                        || currentObj.OpenAction == FmMessage.OpenAction_OpenInApp_OnClick
                        || currentObj.OpenAction == FmMessage.OpenAction_OpenLink
                        || currentObj.OpenAction == FmMessage.OpenAction_OpenLink_OnClick){
                        txtCommand.setVisibility(View.VISIBLE);
                        txtCommand.setOnClickListener(view -> {((MainActivityManager)context).OpenFmMessageCommand(currentObj);});
                    }
                    else
                        txtCommand.setVisibility(View.INVISIBLE);

//                    itemMainPart.setOnClickListener(view -> {
//                        FmMessage currentSelectedClub  = adapterSearch.data.get(position);
//                        hutilities.hideKeyboard((Activity)context);
//                        currentSelectedClubIndex = position;
//                        ClubView_Home currentHomeFragment = new ClubView_Home(currentSelectedClub, PrjConfig.frmClubSearch);
//                        ((MainActivityManager) context).showFragment(currentHomeFragment);
//                    });
//
//                    Picasso builder = Picasso.with(context);
//                    builder.load(currentObj.Avatar).tag(PicassoOnScrollListener.TAG)//.config(Bitmap.Config.RGB_565).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
//                            //.placeholder((R.drawable.ic_launcher_background)) HHH 1400-01-10
//                            //.error(R.drawable.ic_launcher_background)  HHH 1400-01-10
//                            .into(txt_ct_ImageLinkUri);
                }
            }
        }

        final String TAG = "Downloading...";

        class FmMessageDiffCallback extends DiffUtil.Callback {

            private final List<FmMessage> oldFmMessages, newFmMessages;

            public FmMessageDiffCallback(List<FmMessage> oldFmMessages, List<FmMessage> newFmMessages) {
                this.oldFmMessages = oldFmMessages;
                this.newFmMessages = newFmMessages;
            }

            @Override
            public int getOldListSize() {
                return oldFmMessages.size();
            }

            @Override
            public int getNewListSize() {
                return newFmMessages.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return oldFmMessages.get(oldItemPosition).FmMessageId == newFmMessages.get(newItemPosition).FmMessageId;
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return oldFmMessages.get(oldItemPosition).equals(newFmMessages.get(newItemPosition));
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {//3nd Event
        return inflater.inflate(R.layout.frm_fmmessagestory, parent, false);
    }
}