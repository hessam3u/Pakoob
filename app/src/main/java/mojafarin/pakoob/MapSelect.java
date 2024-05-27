package mojafarin.pakoob;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;

import bo.entity.NbLogSearch;
import bo.sqlite.NbLogSearchSQLite;
import utils.HFragment;
import utils.MainActivityManager;
import utils.PrjConfig;
import bo.entity.BuyMapRequestDTO;
import bo.entity.DownloadRequest;
import bo.NewClasses.InsUpdRes;
import bo.entity.MobileInfoDTO;
import bo.entity.NbMap;
import bo.NewClasses.SimpleRequest;
import bo.entity.SearchForMapResult;
import bo.sqlite.NbMapSQLite;
import bo.sqlite.TTExceptionLogSQLite;
import maptools.hMapTools;
import mojafarin.pakoob.mainactivitymodes.DialogMapBuilder;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import user.Register;
import utils.CustomTypeFaceSpan;
import utils.RecyclerTouchListener;
import utils.TextFormat;
import utils.hutilities;
import utils.projectStatics;

public class MapSelect extends HFragment {
    LatLng lastLocation = new LatLng(33, 53.51);
    boolean isDownloading = false;
    boolean reloadMapDatabaseAtPageLoad = false;

    public static MapSelect getInstance(boolean reloadMapDatabaseAtPageLoad){
        MapSelect res = new MapSelect();
        res.reloadMapDatabaseAtPageLoad = reloadMapDatabaseAtPageLoad;
        return res;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {//4th Event
        super.onViewCreated(view, savedInstanceState);
        initializeComponents(view);

        if (lastLocation.longitude == 53.51 && lastLocation.latitude == 33 && MainActivity.cameraLatLon != null
        && MainActivity.cameraLatLon.latitude > 23 && MainActivity.cameraLatLon.longitude > 43){
            lastLocation= MainActivity.cameraLatLon;
        }

        initRecyclerView();
//        initSweepToRefresh();
        initScrollListener();

        if (reloadMapDatabaseAtPageLoad){
            app.syncBuyMapDatabase(pageProgressBar, txtSearchResult, context, isSuccessful -> loadDbAtInit(isSuccessful));
        }
        else{
            loadDbAtInit(true);
        }
    }
    public void loadDbAtInit(boolean isSuccesfull){

        try {
            ReadToursAndFillRecycler(LoadDataTypes.FirstRead);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        dataSource = NbMapSQLite.selectAllLive();
        dataSource.observe(context, posts -> {
            adapterLocal.setData(posts);
            if (posts.size() == 0) {
                lblYourMaps.setText("عنوان شهر، روستا، قله، منطقه یا شماره نقشه را جستجو کنید تا لیست نقشه های قابل انتخاب نمایش داده شود.");
            } else {
                lblYourMaps.setText("نقشه های شما");
            }
        });
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.mapselect);
    }

    private void ReadToursAndFillRecycler(LoadDataTypes firstRead) throws Exception {
    }

    TextView btnBack;
    LinearLayout divSearch;
    Toolbar toolbar;
    TextView btnSearch;
    LinearLayout btnFindByLocation;

    EditText txtSearch;
    private NbMapsAdapter adapterLocal, adapterSearch;
    private RecyclerView recyclerView, rvSearchResult;
    private ProgressBar pageProgressBar;
    boolean isLoadingMore = false;
    final Integer readPageSize = 100;
    public static LiveData<List<NbMap>> dataSource;
    TextView txtSearchResult, txtSelectedNCCIndex, lblYourMaps;

    @Override
    public void initializeComponents(View v) {

        btnSearch = v.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(view -> {
            btnSearch_Click();
        });

        btnBack =v.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(view -> {hutilities.hideKeyboard(context, txtSearch);((AppCompatActivity) context).onBackPressed();});

//        this.dialogMapObj = MainActivity.dialogMapObj;
//        this.dialogMap = MainActivity.dialogMap;
        //reCreateDialogMapObj();

        adapterLocal = new NbMapsAdapter(context, null);

        recyclerView = v.findViewById(R.id.rvContacts);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapterLocal);
        recyclerView.setNestedScrollingEnabled(false);

        pageProgressBar = v.findViewById(R.id.progressBar);

        //1399-12-07 if uncomment, some of works like showing back and setting title will automatically done
//        toolbar = (Toolbar) v.findViewById(R.id.toolbarOfPage);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle("");

        btnFindByLocation = v.findViewById(R.id.btnFindByLocation);
        btnFindByLocation.setOnClickListener(view -> {
            btnFindByLocation_Click();
        });
        divSearch = v.findViewById(R.id.divSearch);
        divSearch.setVisibility(View.GONE);

        //adapterSearch = new NbMapsAdapter(this, null);

        rvSearchResult = v.findViewById(R.id.rvSearchResult);
        rvSearchResult.setLayoutManager(new LinearLayoutManager(context));
        rvSearchResult.setHasFixedSize(true);
        rvSearchResult.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);//TourList.this bayad mibood
        rvSearchResult.setLayoutManager(layoutManager);
//            //baraye namayeshe joda konandeh
//            rvSearchResult.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));//parentActivity -> getActivity -> this bood

        //rvSearchResult.setAdapter(adapterSearch);
        initAdapterSearch(new ArrayList<>());

        txtSelectedNCCIndex = v.findViewById(R.id.txtSelectedNCCIndex);
        lblYourMaps = v.findViewById(R.id.lblYourMaps);

        txtSearchResult = v.findViewById(R.id.txtSearchResult);
        txtSearchResult.setVisibility(View.GONE);

        txtSearch = v.findViewById(R.id.txtSearch);
    }

    private void btnSelectLocationOnMap_Click() {
        showMapDialog.lastLocation = showMapDialog.locationMarker.getPosition();
        showMapDialog.lastLocationZoom = showMapDialog.mapSelectLocation.getCameraPosition().zoom;
        this.lastLocation = showMapDialog.lastLocation;
        txtSearch.setText(String.format(Locale.US, "%.5f", showMapDialog.lastLocation.latitude) + "," + String.format(Locale.US, "%.5f", showMapDialog.lastLocation.longitude));

        //تبدیل دیالوگ به فرگمنت و کامنت کردن در تابستان 1402
        //dialogMap.dismiss();

        //context.changeFragmentVisibility(showMapDialog, false);
        context.onBackPressed();
        //showMapDialog.onBackPressedInChild();

        btnSearch_Click();
    }

    public AlertDialog dialogMap = null;
    DialogMapBuilder dialogMapObj = null;

    ShowMapDialog showMapDialog = null;
    private boolean btnFindByLocation_Click() {
        int step = 0;
        try {
            if (showMapDialog == null) {
                showMapDialog = ShowMapDialog.getInstance();
            }
            showMapDialog.setForModeShowSelect(lastLocation, 10, view -> btnSelectLocationOnMap_Click());
            if (true || !showMapDialog.isAdded())
                context.showFragment(showMapDialog);
            else{
//                context.changeFragmentVisibility(showMapDialog, true);
//                showMapDialog.fillForm();
            }
            return true;

//            dialogMap = null;
//            if (dialogMap == null || MainActivity.appExistsBeforeAndShouldReloadAll_ReReadDialogMap) {
//                step = 100;
//                AlertDialog.Builder alertDialogBuilder = dialogMapObj.GetBuilder();
//                step = 200;
//                dialogMap = alertDialogBuilder.create();
//                step = 300;
//                Window w = dialogMap.getWindow();
//                step = 400;
//                w.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//
//                MainActivity.appExistsBeforeAndShouldReloadAll_ReReadDialogMap = false;
//            }
//            step = 500;
//            dialogMapObj.setForModeShowSelect(lastLocation, 10, view -> btnSelectLocationOnMap_Click());
//            step = 600;
//            dialogMap.show();
//            step = 700;
//            return true;
        }
        catch (Exception ex){
            projectStatics.showDialog(context, getResources().getString(R.string.dialog_UnknownError)
                    , getResources().getString(R.string.dialog_UnknownErrorDescForOpenSearchOnMap)
                    , getResources().getString(R.string.ok), null, "", null);

            TTExceptionLogSQLite.insert(ex.getMessage(), "Step: " + step + "-ex:" + stktrc2k(ex), PrjConfig.frmMapSelect, 120);
            Log.d("جستجو_روی_نقشه", "Step: " + step + "-ex:" + ex.getMessage() + ex.getStackTrace());
            ex.printStackTrace();
            return false;
        }
    }

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
        try {
            if (!hutilities.isInternetConnected(context)) {
                projectStatics.showDialog(context, this.getResources().getString(R.string.NoInternet), this.getResources().getString(R.string.NoInternet_Desc), this.getResources().getString(R.string.ok), view -> {
                }, "", null);
                return;
            }
            pageProgressBar.setVisibility(View.VISIBLE);

            SimpleRequest request = new SimpleRequest();
            request.data = txtSearch.getText().toString();
            request.mobileInfoDTO = MobileInfoDTO.instance();
            Call<ResponseBody> call = app.apiMap.SearchForMap(request);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (!isAdded()) return;
                    try {
                        divSearch.setVisibility(View.VISIBLE);
                        pageProgressBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            SearchForMapResult result = SearchForMapResult.fromBytes(response.body().bytes());

                            if (result.message.length() > 0) {
                                txtSearchResult.setVisibility(View.VISIBLE);
                                txtSearchResult.setText(result.message);
                            } else {
                                txtSearchResult.setVisibility(View.GONE);
                            }
                            if (result.resList != null && result.resList.size() > 0) {
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
                        TTExceptionLogSQLite.insert(ex.getMessage(), stktrc2k(ex), PrjConfig.frmMapSelect, 101);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    if (!isAdded()) return;
                    TTExceptionLogSQLite.insert(t.getMessage(), stktrc2kt(t), PrjConfig.frmMapSelect, 100);
                    divSearch.setVisibility(View.VISIBLE);
                    pageProgressBar.setVisibility(View.GONE);
                    txtSearchResult.setVisibility(View.VISIBLE);
                    txtSearchResult.setText("متاسفانه مشکلی در برقراری ارتباط با سرور به وجود آمده است. لطفا بعدا مجددا تلاش نمایید.");

                }
            });

        }
        catch (Exception ex) {
            ex.printStackTrace();
            projectStatics.showDialog(context, context.getResources().getString(R.string.dialog_UnknownError)
                    , context.getResources().getString(R.string.dialog_UnknownErrorRepeatAndDesc) + ex.getMessage(), context.getResources().getString(R.string.ok), null, "", null);
            TTExceptionLogSQLite.insert(ex.getMessage(), stktrc2k(ex), PrjConfig.frmMapSelect, 105);
        }
    }

    void initAdapterSearch(List<NbMap> result) {
        if (true || adapterSearch == null) {
            adapterSearch = new NbMapsAdapter(context, null);
            adapterSearch.setData(result);
            rvSearchResult.setAdapter(adapterSearch);
        }
        if (rvSearchResult.getAdapter() == null) {

        }
    }

    private void initRecyclerView() {
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(context, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
//                NbMap selectedItem = dataSource.getValue().get(position);
//                SmsEdit se = SmsEdit.newInstance(selectedItem);
//                se.show(((AppCompatActivity)parentActivity).getSupportFragmentManager(), "tag");


//                Intent i = new Intent(context, MainActivity.class);
//                i.putExtra("ix", position);
//                startActivity(i);

                //Toast.makeText(getContext(), selectedTour.getName() + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {
//                NbMap selectedTour = getSource().get(position);
                //Toast.makeText(getContext(), selectedTour.getName() + " is long selected!", Toast.LENGTH_SHORT).show();

            }
        }));
    }

    private void initScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    public enum LoadDataTypes {
        FirstRead, LoadMemory, LoadMore, Refresh
    }

    void initAdapter(List<NbMap> allTours) {
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

    void notifyAdapterToUpdate(List<NbMap> allTours) {
        if (adapterLocal != null)
            adapterLocal.notifyDataSetChanged();
        else
            initAdapter(allTours);
    }

    void finishLoadingEnvironment() {
        isLoadingMore = false;
    }

    int lastFirstVisiblePosition = -1;
    int topOffset = 0;

    @Override
    public void onPause() {
        try {
            LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
            lastFirstVisiblePosition = manager.findFirstVisibleItemPosition();
            View firstItemView = manager.findViewByPosition(lastFirstVisiblePosition);
            topOffset = (int) firstItemView.getTop();
            Log.d("onPauseOrResume", "pause fired" + lastFirstVisiblePosition);
        } catch (Exception ex) {
            ex.printStackTrace();
            lastFirstVisiblePosition = -1;
        }

        super.onPause();
    }

    @Override
    public void onResume() {
        Log.d("onPauseOrResume", "resume fired" + lastFirstVisiblePosition);
        setRecyclerScrollOnBack();
        super.onResume();
    }

    void setRecyclerScrollOnBack() {
        Log.d("onPauseOrResume", "setRecyclerScrollOnBack fired" + lastFirstVisiblePosition);

        if (lastFirstVisiblePosition != -1) {
            try {
                ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(lastFirstVisiblePosition, topOffset);
                lastFirstVisiblePosition = -1;
                Log.d("onPauseOrResume", "resume scroll changed" + lastFirstVisiblePosition);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void clearTemp(Context context) {
        String tempPath = context.getFilesDir() + File.separator + hMapTools.tempFolder + File.separator;
        File dir = new File(tempPath);
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                File innerFile = new File(dir, children[i]);
                if (innerFile.isDirectory()) {
                    String[] innerChilds = innerFile.list();
                    for (int j = 0; j < innerChilds.length; j++) {
                        File innerChildFile = new File(tempPath + children[i], innerChilds[j]);
                        innerChildFile.delete();
                    }
                } else
                    innerFile.delete();

            }
        }
    }
//
//    void hideKeyboard() {
//        //hide keyboard
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(txtSearch.getWindowToken(), 0);
//    }

    public class NbMapsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<NbMap> data;
        private Context context;
        private LayoutInflater layoutInflater;
        private OnDeleteButtonClickListener onDeleteButtonClickListener;

        public NbMapsAdapter(Context context, OnDeleteButtonClickListener listener) {
            this.data = new ArrayList<>();
            this.context = context;
            this.onDeleteButtonClickListener = listener;
        }

        @Override
        public NbMapViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            layoutInflater = LayoutInflater.from(context);
            //this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = layoutInflater.inflate(R.layout.mapselect_item, parent, false);
            return new NbMapViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            if (viewHolder instanceof NbMapViewHolder) {
                ((NbMapViewHolder) viewHolder).bind(data.get(position));
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

        public void setData(List<NbMap> newData) {
            if (data != null) {
//                NbMapDiffCallback postDiffCallback = new NbMapDiffCallback(data, newData);
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
            public abstract void onDeleteButtonClicked(NbMap post);
        }

        public class NbMapViewHolder extends RecyclerView.ViewHolder {
            public View itemView;
            public TextView btnShowHide, btnOrder;
            Button btnMore;
            public TextView txtBlockName, txtPrice, txtIndex, txtTitle, lblIndex;
            public ProgressBar progressBarDet, progressBarIndet;
            LinearLayout txtPriceContainer;

            NbMapViewHolder(View itemView) {
                super(itemView);
                this.itemView = itemView;
                btnShowHide = itemView.findViewById(R.id.btnShowHide);
                txtPriceContainer = itemView.findViewById(R.id.txtPriceContainer);
                txtTitle = itemView.findViewById(R.id.txtTitle);
                txtPrice = itemView.findViewById(R.id.txtPrice);
                txtBlockName = itemView.findViewById(R.id.txtBlockName);
                txtIndex = itemView.findViewById(R.id.txtIndex);
                btnOrder = itemView.findViewById(R.id.btnOrder);
                btnMore = itemView.findViewById(R.id.btnMore);
                lblIndex = itemView.findViewById(R.id.lblIndex);
                progressBarDet = itemView.findViewById(R.id.itemprogressbarDet);
                progressBarIndet = itemView.findViewById(R.id.itemprogressbarIndet);

                Drawable progressDrawable = progressBarIndet.getIndeterminateDrawable().mutate(); //or getProgressDrawable for normal //https://stackoverflow.com/questions/2020882/how-to-change-progress-bars-progress-color-in-android
                progressDrawable.setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
                progressBarIndet.setProgressDrawable(progressDrawable);
                Drawable progressDrawable2 = progressBarDet.getProgressDrawable().mutate(); //or getProgressDrawable for normal //https://stackoverflow.com/questions/2020882/how-to-change-progress-bars-progress-color-in-android
                progressDrawable2.setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
                progressBarDet.setProgressDrawable(progressDrawable2);


                //به خاطر این که استایل ها کار نمیکرد مجبور شدم به صورت دستی فونت ها رو بهش اضافه کنم
                btnOrder.setTypeface(projectStatics.getIranSans_FONT(context));
                txtTitle.setTypeface(projectStatics.getIranSans_FONT(context));
                txtPrice.setTypeface(projectStatics.getIranSans_FONT(context));
                lblIndex.setTypeface(projectStatics.getIranSans_FONT(context));
                txtIndex.setTypeface(projectStatics.getIranSans_FONT(context));
                txtBlockName.setTypeface(projectStatics.getIranSans_FONT(context));
                btnShowHide.setTypeface(ResourcesCompat.getFont(context, R.font.fontello));
                btnMore.setTypeface(ResourcesCompat.getFont(context, R.font.fontello));
                btnMore.setVisibility(View.GONE);
            }

            void bind(final NbMap currentObj) {
                if (currentObj != null) {
                    txtPrice.setText(TextFormat.GetStringFromDecimalPrice(currentObj.Price) + " " + app.CurrencyName);
                    if (currentObj.Name.length() > 0)
                        txtTitle.setText(currentObj.Name);
                    else if (currentObj.OrginalName.length() > 0)
                        txtTitle.setText(currentObj.OrginalName);
                    else if (currentObj.NCCIndex.length() > 0)
                        txtTitle.setText(currentObj.NCCIndex);
                    else {
                        txtTitle.setText("نقشه بدون عنوان");
                    }

                    if (currentObj.BlockName.length() > 0)
                        txtBlockName.setText("بلوک " + currentObj.BlockName);
                    else
                        txtBlockName.setVisibility(View.GONE);
                    if (currentObj.NCCIndex.length() > 0)
                        txtIndex.setText(currentObj.NCCIndex);
                    else {
                        txtIndex.setVisibility(View.GONE);
                        lblIndex.setVisibility(View.GONE);
                    }


                    if (currentObj.RequestStatus == NbMap.Enums.RequestStatusTypes_None || currentObj.RequestStatus == NbMap.Enums.RequestStatusTypes_Done) {
                        switch (currentObj.BuyStatus) {
                            case NbMap.Enums.BuyStatusTypes_Done:
                            case NbMap.Enums.BuyStatusTypes_Wait:
                                txtPriceContainer.setVisibility(View.GONE);
                                btnOrder.setText(getString(R.string.Download));
                                if (currentObj.Extracted == 1) {
                                    btnOrder.setText(getString(R.string.view));
                                    btnMore.setVisibility(View.VISIBLE);
                                    //btnOrder.setVisibility(View.GONE);
                                } else {
                                    //currentObj.LocalFileAddress.equals("")
                                    File f1 = new File(currentObj.LocalFileAddress);
                                    File file = new File(hMapTools.getFileNameAtMapsFolder(currentObj.NCCIndex, context));
                                    if (file.exists() || f1.exists()) {
                                        btnOrder.setText(getString(R.string.Extract));
                                        //btnOrder.setVisibility(View.GONE);
                                    }
                                }
                                break;
//                            case NbMap.Enums.BuyStatusTypes_Wait:
//                                btnOrder.setText("در انتظار");
//                                btnOrder.setEnabled(false);
//                                break;
                            case NbMap.Enums.BuyStatusTypes_Canceled:
                                btnOrder.setText(getString(R.string.Order));
                                break;
                            case NbMap.Enums.BuyStatusTypes_None: {
                                if (currentObj.AvailablityType == NbMap.Enums.AvailablityType_Available) {
                                    btnOrder.setText(getString(R.string.Buy));
                                } else if (currentObj.AvailablityType == NbMap.Enums.AvailablityType_MostOrder || currentObj.AvailablityType == NbMap.Enums.AvailablityType_MostUpload) {
                                    btnOrder.setText(getString(R.string.Order));
                                } else if (currentObj.AvailablityType == NbMap.Enums.AvailablityType_NotAvailable) {
                                    btnOrder.setText(getString(R.string.Unavailable));
                                    btnOrder.setEnabled(false);
                                }
                                break;
                            }
                        }
                    } else if (currentObj.RequestStatus == NbMap.Enums.RequestStatusTypes_Wait) {
                        btnOrder.setText(getString(R.string.InWait));
                        btnOrder.setEnabled(false);
                    } else if (currentObj.RequestStatus == NbMap.Enums.RequestStatusTypes_Canceled) {
                        btnOrder.setText(getString(R.string.Order));
                    }

                    btnOrder.setOnClickListener(view -> {
                        btnOrder_Click(this, currentObj);
                    });

                    btnShowHide.setOnClickListener(view -> {
                        btnShowHide_Click(currentObj);
                    });

                    btnMore.setOnClickListener(view -> {
                        btnMore_Click(currentObj, this);
                    });

//                    btnDelete.setOnClickListener(v -> {
//                        if (onDeleteButtonClickListener != null)
//                            onDeleteButtonClickListener.onDeleteButtonClicked(post);
//                    });

                }
            }


        }
        private void btnMore_Click(NbMap currentObj, NbMapViewHolder holder) {
            //Creating the instance of PopupMenu
            PopupMenu popup = new PopupMenu(context, holder.btnMore);
            //Inflating the Popup using xml file
            popup.getMenuInflater()
                    .inflate(R.menu.mapitem_rightclick, popup.getMenu());
            Menu menu = popup.getMenu();
            for (int i = 0; i < menu.size() ; i++) {
                MenuItem item = menu.getItem(i);
                CustomTypeFaceSpan.applyFontToMenuItem(item, context, Color.BLACK);
            }

            hutilities.hideKeyboard(context, txtSearch);
            //registering popup with OnMenuItemClickListener
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {

                    boolean doCreateForNewerZoom = false;
                    int fromZoom = 16;
                    int toZoom = 16;

                    switch (item.getItemId()){
                        case R.id.menu_reCreate:
                            doCreateForNewerZoom = true;
                            fromZoom = hMapTools.CustomMapMinZoom;
                            toZoom = hMapTools.CustomMapMaxZoomNormal;
                            break;
//                        case R.id.menu_createZoom100:
//                            doCreateForNewerZoom = true;
//                            fromZoom = toZoom = 16;
//                            break;
                        case R.id.menu_creatZoom50:
                            doCreateForNewerZoom = true;
                            fromZoom = 16;
                            toZoom = hMapTools.CustomMapMaxZoomAvailable;
                            break;
                        case R.id.menu_deleteMapTiles:
                            doCreateForNewerZoom = false;
                            deleteMapAsync(currentObj, false, holder.progressBarIndet);
                            break;
                        case R.id.menu_deleteMapTilesAndDownload:
                            doCreateForNewerZoom = false;
                            deleteMapAsync(currentObj, true, holder.progressBarIndet);
                            break;
                    }
                    if (doCreateForNewerZoom){
                        Handler handler = new Handler(Looper.getMainLooper());
                        int finalFromZoom = fromZoom;
                        int finalToZoom = toZoom;
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    handler.post(new Runnable() {
                                        public void run() {
                                            Toast.makeText(context, "در حال استخراج نقشه...", Toast.LENGTH_LONG).show();
                                            holder.progressBarIndet.setVisibility(View.VISIBLE);
                                        }
                                    });
                                    String mapsFolder = hMapTools.createAndRetMapsFolder(context);
                                    String tempDownloadFolder = hMapTools.createAndRetTempDownloadFolder(context);
                                    String fileName = currentObj.NCCIndex + ".jpg";

                                    String output = hMapTools.decryptToTemp(mapsFolder + fileName, context,hutilities.CCustomerId);
                                    List<LatLng> bounds = currentObj.getBounds();
                                    boolean tileCreateRes = hMapTools.craeteTilesAtManyZoom(output, context, bounds, finalFromZoom, finalToZoom);
                                    MapSelect.clearTemp(context);

                                    handler.post(new Runnable() {
                                        public void run() {
                                            Toast.makeText(context, "نقشه قابل استفاده می باشد", Toast.LENGTH_LONG).show();
                                            holder.progressBarIndet.setVisibility(View.INVISIBLE);
                                        }
                                    });
                                }
                                catch (Exception ex){
                                    ex.printStackTrace();
                                }
                            }
                        });
                        thread.start();
                    }
                    return true;
                }
            });

            popup.show(); //showing popup menu
        }
        String TAG = "DDDDD";
        void deleteMapAsync(NbMap currentObj, boolean doDeleteDownloadedFile, ProgressBar progressBarIndet){
            currentObj.Extracted = 2; //Mohemmmmmmm
            NbMapSQLite.update(currentObj);

            Handler handler = new Handler(Looper.getMainLooper());

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        handler.post(new Runnable() {
                            public void run() {
                                Toast.makeText(context, "در حال حذف نقشه...", Toast.LENGTH_LONG).show();
                                progressBarIndet.setVisibility(View.VISIBLE);
                            }
                        });
                        hMapTools.deleteTilesAtManyZoom(context, currentObj, hMapTools.CustomMapMinZoom, hMapTools.CustomMapMaxZoomAvailable, doDeleteDownloadedFile);

                        handler.post(new Runnable() {
                            public void run() {
                                Toast.makeText(context, "نقشه حذف شد", Toast.LENGTH_LONG).show();
                                progressBarIndet.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                    catch (Exception ex){
                        projectStatics.showDialog(context, getResources().getString(R.string.dialog_UnknownError)
                                , getResources().getString(R.string.dialog_UnknownErrorDesc)
                                , getResources().getString(R.string.ok)
                                , null, "", null);

                        ex.printStackTrace();
                    }
                }
            });
            thread.start();
        }

        private void btnShowHide_Click(NbMap currentObj) {
            //hide keyboard
            hutilities.hideKeyboard(context, txtSearch);
            //hideKeyboard();

            if (showMapDialog == null) {
                showMapDialog = ShowMapDialog.getInstance();
            }
            showMapDialog.setForModeShowBounds(currentObj, view -> btnSelectLocationOnMap_Click());
            if (true || !showMapDialog.isAdded())
                ((MainActivity)context).showFragment(showMapDialog);
            else{
//                context.changeFragmentVisibility(showMapDialog, true);
//                showMapDialog.fillForm();
            }
            return ;

            //1402-04 کامنت برای خرابکاری نقشه
//            if (dialogMap == null) {
//                AlertDialog.Builder alertDialogBuilder = dialogMapObj.GetBuilder();
//                dialogMap = alertDialogBuilder.create();
//                Window w = dialogMap.getWindow();
//                w.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//            }
//            dialogMapObj.setForModeShowBounds(currentObj, view -> btnSelectLocationOnMap_Click());
//            dialogMap.show();

//            mapFrameLayoutChild.setVisibility(View.VISIBLE);
        }

        public Intent intent = null;
        public boolean btnOrder_Click(NbMapViewHolder holder, NbMap currentObj) {
            hutilities.hideKeyboard(context, txtSearch);

            if (hutilities.CCustomerId == 0) {
                projectStatics.showDialog(context, getResources().getString(R.string.need_loginOrRegister)
                        , getResources().getString(R.string.need_loginOrRegister_Desc)
                        , getResources().getString(R.string.ok)
                        , view -> {((MainActivityManager)context).showFragment(new Register(""));}, "", null);

                return false;
            }
            Log.d("DOWDINGGGGGGGGGGGG", Integer.toString(isDownloading?1:0));
            if (isDownloading ){
                projectStatics.showDialog(context, getResources().getString(R.string.isProcessing_Title)
                        , getResources().getString(R.string.isProcessing_Desc)
                        , getResources().getString(R.string.ok)
                        , null, "", null);
                return false;
            }
            isDownloading = true;;
            String text = holder.btnOrder.getText().toString();
            if (text.equals(getString(R.string.Download))) {
                //NbLogSearchSQLite.insert(NbLogSearch.getInstance(NbLogSearch.CommandType_MapDownload, "MAP_ID:" +currentObj.NbMapId, 1, 0, 0));
                DoDownload(currentObj, holder);
            } else if (text.equals(getString(R.string.Buy)) || text.equals(getString(R.string.Order))) {
                AlertDialog.Builder alertDialogBuilder = DialogGetDisCopon.GetBuilder(context, view -> {
                    DoBuyRequest(currentObj, DialogGetDisCopon.GetDiscountCode(dialog_getdiscount), holder.progressBarIndet, holder);
                    dialog_getdiscount.dismiss();
                }, null);

                dialog_getdiscount = alertDialogBuilder.create();
                dialog_getdiscount.show();
                projectStatics.SetDialogButtonStylesAndBack(dialog_getdiscount, this.context, projectStatics.getIranSans_FONT(context), 18);

            } else if (text.equals(getString(R.string.view))) {
                intent = new Intent();
                intent.putExtra("latn", currentObj.LatN);
                intent.putExtra("lats", currentObj.LatS);
                intent.putExtra("lone", currentObj.LonE);
                intent.putExtra("lonw", currentObj.LonW);
                intent.putExtra("zoom", 12f);
                intent.putExtra("bounds", false);

                isDownloading = false;;

                MainActivity parent =(MainActivity)getActivity();
                parent.setResultFromMapSelect(intent);
                parent.onBackPressed();
//                setResult(1100, intent);//Activity Method
//                finish();
            } else if (text.equals(getString(R.string.Extract))) {

                int index = -1;
                String localFileAddress = currentObj.LocalFileAddress;
                if (localFileAddress.length() > 0)
                    index = localFileAddress.lastIndexOf(File.separator);
                else {
                    localFileAddress = hMapTools.getFileNameAtMapsFolder(currentObj.NCCIndex, context);
                    File file = new File(localFileAddress);
                    index = localFileAddress.lastIndexOf(File.separator);
                    if (!file.exists() || index == -1) {
                        Toast.makeText(context, getString(R.string.CantExtractTheMap), Toast.LENGTH_LONG);
                        return false;
                    }
                }

                String mapsDirectoryName = localFileAddress.substring(0, index);
                String fileName = localFileAddress.substring(index + 1);
                DecryptAndGenerateMapAndDbUpdate(hMapTools.CustomMapMinZoom, hMapTools.CustomMapMaxZoomNormal, mapsDirectoryName, fileName, currentObj, holder);
            }

            return true;
        }
        public AlertDialog dialog_getdiscount = null;

        public boolean DoBuyRequest(NbMap currentObj, String DiscountCode, ProgressBar progressBar, NbMapViewHolder holder) {
            //Same at : SafeGpxSearch - MapSelect
            if (!hutilities.isInternetConnected(context)) {
                projectStatics.showDialog(context, getResources().getString(R.string.NoInternet), getResources().getString(R.string.NoInternet_Desc), getResources().getString(R.string.ok), view -> {}, "", null);
                return false;
            }
            BuyMapRequestDTO buyRequest = new BuyMapRequestDTO();
            buyRequest.DiscountCode = DiscountCode;
            buyRequest.NBMapId = currentObj.NbMapId;
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
                                    app.syncBuyMapDatabase(progressBar, txtSearchResult, context, null);
                                    holder.btnOrder.setText(getString(R.string.InWait));
                                    break;
                                case STATUS_ERROR:
                                    titleMsg = "خطا";
                                    break;
                                case STATUS_DOWNLOAD_NOW:
                                    titleMsg = getString(R.string.msgMapIsReadyToDownload_Title);
                                    res.message = getString(R.string.msgMapIsReadyToDownload_DescForMapPage);
                                    app.syncBuyMapDatabase(progressBar, txtSearchResult, context, null);
                                    holder.btnOrder.setText(getString(R.string.Download));
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

                            } else {
                                projectStatics.showDialog(context, titleMsg, res.message, getResources().getString(R.string.ok), null, "", null);
                            }
                        } else {
                            projectStatics.showDialog(context, getResources().getString(R.string.dialog_ertebatBaServer_Title)
                                    , getResources().getString(R.string.dialog_ertebatBaServer_Desc)
                                    , getResources().getString(R.string.ok)
                                    , null, "", null);
                            TTExceptionLogSQLite.insert("Server Connect: " + response.code(), response.message(), PrjConfig.frmMapSelect, 300);
                            Log.d(TAG, "ERROR RESPONSE : " + response.code() + " msg: " + response.message());
                        }

                        isDownloading = false;
                        progressBar.setVisibility(View.INVISIBLE);

                    } catch (Exception ex) {
                        TTExceptionLogSQLite.insert("Exception:" + response.message(), stktrc2k(ex), PrjConfig.frmMapSelect, 200);
                        ex.printStackTrace();
                        progressBar.setVisibility(View.INVISIBLE);
                        isDownloading = false;
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    TTExceptionLogSQLite.insert("Fail", t.getMessage(), PrjConfig.frmMapSelect, 100);
                    if (!isAdded()) return;
                    progressBar.setVisibility(View.INVISIBLE);
                    Log.e(TAG, "error");
                    isDownloading = false;
                }
            });
            return true;
        }

        private void DoDownload(NbMap currentObj, NbMapViewHolder holder) {
            if (!hutilities.isInternetConnected(context)) {
                projectStatics.showDialog(context, getResources().getString(R.string.NoInternet), getResources().getString(R.string.NoInternet_Desc), getResources().getString(R.string.ok), view -> {}, "", null);
                return;
            }
            DownloadRequest data = DownloadRequest.getInstance(currentObj.NbMapId, NbMap.Enums.NbBuyType_Map);
            holder.progressBarIndet.setVisibility(View.VISIBLE);
            SimpleRequest request = SimpleRequest.getInstance(data);
            Call<ResponseBody> call = app.apiMap.Download(request);
            Toast.makeText(context, "در حال دانلود نقشه... لطفا تا انتهای دانلود صبر کنید", Toast.LENGTH_LONG).show();
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (!isAdded()) return;
                    try {
                        holder.progressBarIndet.setVisibility(View.INVISIBLE);
                        if (response.isSuccessful()) {
                            InsUpdRes res = InsUpdRes.fromBytes(response.body().bytes());
                            String mapsFolder = hMapTools.createAndRetMapsFolder(context);
                            String tempDownloadFolder = hMapTools.createAndRetTempDownloadFolder(context);
                            String fileName = currentObj.NCCIndex + ".jpg";
                            //progressBar.setVisibility(View.INVISIBLE);
                            if (!res.isOk) {
                                projectStatics.showDialog(context, "خطا در دانلود", res.message, getResources().getString(R.string.ok), null, "", null);
                                isDownloading = false;;
                                return;
                            }
                            doDownloadInBackground2(res.command,  tempDownloadFolder, fileName, mapsFolder, currentObj, holder, context);
//                            boolean writtenToDisk = writeResponseBodyToDisk(response.body(), downloadDirectoryName, fileName);
//                            Log.d(TAG, "file download was a success? " + writtenToDisk);
//
//                            if (writtenToDisk) {
//                                Toast.makeText(context, "دانلود کامل شد ... در حال ساخت نقشه", Toast.LENGTH_LONG).show();
//
//                                //  آغاز عملیات دیکریپت کردن
//                                DecryptAndGenerateMapAndDbUpdate(downloadDirectoryName, fileName, currentObj, null, progressBar);
//                            }


                        } else {
                            TTExceptionLogSQLite.insert("Server Contact Failed", "Code:" + response.code(), PrjConfig.frmMapSelect, 60);
                            Log.d(TAG, "server contact failed");
                        }
                    } catch (IOException e) {
                        TTExceptionLogSQLite.insert("IOException", e.getMessage(), PrjConfig.frmMapSelect, 50);
                        e.printStackTrace();
                        isDownloading = false;;
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    TTExceptionLogSQLite.insert("Fail", t.getMessage(), PrjConfig.frmMapSelect, 12);
                    if (!isAdded()) return;
                    String errorText = t.getMessage();
                    if (errorText.contains("Failed to connect to")){
                        projectStatics.showDialog(context, "خطا در دانلود", "امکان ارتباط با سرور وجود ندارد. این اشکال ممکن است از قطع موقتی سرور یا از قطع اینترنت شما باشد. لطفا بعدا مجددا تلاش نمایید.", getResources().getString(R.string.ok), null, "", null);
                    }
                    else {
                        projectStatics.showDialog(context, "خطا در دانلود", "متاسفانه خطایی ناشناخته در هنگام دانلود به وجود آمده است. لطفا بعدا مجددا تلاش نمایید.", getResources().getString(R.string.ok), null, "", null);
                    }
                    holder.progressBarIndet.setVisibility(View.INVISIBLE);
                    isDownloading = false;;
                    Log.e(TAG, "error");
                }
            });
        }

        //این تابع تست نشده اما با کمک جی پی تی نوشته شده و احتمالا کار میکنه اما به محض این که برنامه نویسش تموم شد، باگ مربوط به
        //Connection Reset هم حل شد!
        //نوشته شده به تاریخ 14020915
        public void doDownloadInBackground2(String remoteAddr, String tempDownloadDirectoryName, String fileName, String mapsFolderName, NbMap currentObj, NbMapViewHolder holder, Context context) {//, Button view, ProgressBar progressBarDet, ProgressBar progressBarIndet,, Button btnMore
            Handler handler = new Handler(Looper.getMainLooper());
            holder.progressBarDet.setProgress(0);
            holder.progressBarDet.setVisibility(View.VISIBLE);
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    int step = 0;
                    try {
                        //                        //connecting to url
//                        String url = "http://pakoob24.ir/ui/temp/aa.jpg";
                        URL url = new URL(remoteAddr);

                        //1401-03-23 : https://stackoverflow.com/questions/59093629/add-a-self-signed-ssl-certificate-to-httpurlconnection
                        //رفع خطای دانلود ناموفق
                        if ( (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)){
                            TrustManager[] trustAllCerts = new TrustManager[]{
                                    new X509ExtendedTrustManager() {
                                        public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) {}
                                        public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) {}
                                        public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) {}
                                        public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) {}
                                        public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                                        public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                                        public X509Certificate[] getAcceptedIssuers() {
                                            return new X509Certificate[0];
                                        }
                                    }
                            };

                            SSLContext sslContext = SSLContext.getInstance("TLS");
                            sslContext.init(null, trustAllCerts, null);

                            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
                        }
                        else {
                            TrustManager[] trustAllCerts = new TrustManager[]{
                                    new X509TrustManager() {
                                        public X509Certificate[] getAcceptedIssuers() {
                                            return new X509Certificate[0];
                                        }

                                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                                        }

                                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                                        }
                                    }
                            };

                            // Install the all-trusting trust manager
                            SSLContext sc = SSLContext.getInstance("TLS");
                            sc.init(null, trustAllCerts, new java.security.SecureRandom());
                            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                        }

                        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
                        int responseCode = httpConn.getResponseCode();
                        // Check if the connection is successful
                        if (responseCode == HttpURLConnection.HTTP_OK) {

//                            InputStream inputStream = httpConn.getInputStream();
//
//                            FileOutputStream outputStream = new FileOutputStream(saveFilePath);
//
//                            int bytesRead;
//                            byte[] buffer = new byte[4096];
//                            while ((bytesRead = inputStream.read(buffer)) != -1) {
//                                outputStream.write(buffer, 0, bytesRead);
//                            }
//
//                            outputStream.close();
//                            inputStream.close();
//                            System.out.println("File downloaded successfully!");

                            int lenghtOfFile = httpConn.getContentLength();//c.getContentLength();
                            step = 30;
                            if (lenghtOfFile <= 0) {
                                handler.post(new Runnable() {
                                    public void run() {
                                        projectStatics.showDialog(context, "خطا در دانلود", "فایل درخواستی قابل دانلود نمی باشد.", getResources().getString(R.string.ok), null, "", null);
                                    }
                                });
                                isDownloading = false;
                                return;
                            }
                            step = 40;

                            int BUFFER_SIZE = 8192;
                            //this is where the file will be seen after the download
                            FileOutputStream output = new FileOutputStream(new File(tempDownloadDirectoryName, fileName));
                            step = 50;
                            //file input is from the url
                            InputStream in = httpConn.getInputStream();//new BufferedInputStream(url.openStream());//c.getInputStream();
                            step = 60;
                            BufferedInputStream bis = new BufferedInputStream(in, BUFFER_SIZE);
                            step = 70;

                            //here’s the download code
                            byte[] buffer = new byte[BUFFER_SIZE];
                            int len1 = 0;
                            long total = 0;

                            while ((len1 = bis.read(buffer, 0, BUFFER_SIZE)) > 0) {
                                total += len1; //total = total + len1
                                long finalTotal = total;
                                handler.post(new Runnable() {
                                    public void run() {
                                        holder.progressBarDet.setProgress((int) ((finalTotal * 100) / lenghtOfFile));
                                    }
                                });
                                output.write(buffer, 0, len1);
                            }
                            step = 80;
                            in.close();
                            output.flush();
                            output.close();
                            step = 90;

                            File tempDownloadFile = new File(tempDownloadDirectoryName, fileName);
                            if (tempDownloadFile.length() != lenghtOfFile) {
                                handler.post(new Runnable() {
                                    public void run() {
                                        projectStatics.showDialog(context, "خطا در دانلود", "فایل درخواستی به شکل نامناسبی دانلود شد. لطفا دوباره تلاش کنید.", getResources().getString(R.string.ok), null, "", null);
                                    }
                                });
                                isDownloading = false;
                                return;
                            }
                            step = 100;
                            boolean moveRes = hutilities.moveFile(tempDownloadDirectoryName + File.separator + fileName, mapsFolderName, fileName);
                            if (!moveRes) {
                                handler.post(new Runnable() {
                                    public void run() {
                                        projectStatics.showDialog(context, "خطا در دانلود", "در حال حاضر امکان ساعت فایل دانلود شده وجود ندارد. لطفا دوباره تلاش کنید.", getResources().getString(R.string.ok), null, "", null);
                                    }
                                });
                                isDownloading = false;
                                return;
                            }
                            step = 110;

                            handler.post(new Runnable() {
                                public void run() {
                                    holder.progressBarDet.setVisibility(View.GONE);
                                }
                            });

                            handler.post(new Runnable() {
                                public void run() {
                                    Toast.makeText(context, "دانلود کامل شد ... در حال ساخت مسیر", Toast.LENGTH_LONG).show();
                                }
                            });
                            handler.post(new Runnable() {
                                public void run() {
                                    // آغاز عملیات دیکریپت کردن
                                    DecryptAndGenerateMapAndDbUpdate(hMapTools.CustomMapMinZoom, hMapTools.CustomMapMaxZoomNormal, mapsFolderName, fileName, currentObj, holder);
                                }
                            });


                        } else {
                            String msgToShow = "اتصال با سرور دچار مشکل است و در حال حاظر این با کد خطای "+ httpConn.getResponseCode() +" مواجه هستیم";
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    projectStatics.showDialog(context, "خطا در دانلود", msgToShow, getResources().getString(R.string.ok), null, "", null);
                                    holder.progressBarDet.setVisibility(View.GONE);
                                }
                            });
                        }
                        httpConn.disconnect();
                        //END of gpt


                    } catch (Exception e) {

                        Log.e("اکسپشن", step + " _ " + remoteAddr + "\n" + e.getMessage());
                        e.printStackTrace();
                        String msg = "یک خطای پیش بینی نشده در هنگام دانلود رخ داده است. لطفا دوباره تلاش کنید.";
                        if (e.getMessage().toLowerCase().contains("time"))
                            msg = "مدت زمان زیادی برای دانلود سپری شد. احتمالا اینترنت شما ضعیف است یا مشکلی در سرور وجود دارد.";
                        String msgToShow = msg;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                projectStatics.showDialog(context, "خطا در دانلود", msgToShow, getResources().getString(R.string.ok), null, "", null);
                                holder.progressBarDet.setVisibility(View.GONE);
                            }
                        });
                        TTExceptionLogSQLite.insert(step + "-" + remoteAddr + "--"+ e.getMessage(),stktrc2k(e) , PrjConfig.frmMapSelect, 400);

                    }
                    isDownloading = false;
                }
            });
            thread.start();
        }


        public void doDownloadInBackground(String remoteAddr, String tempDownloadDirectoryName, String fileName, String mapsFolderName, NbMap currentObj, NbMapViewHolder holder, Context context) {//, Button view, ProgressBar progressBarDet, ProgressBar progressBarIndet,, Button btnMore
            Handler handler = new Handler(Looper.getMainLooper());
            holder.progressBarDet.setProgress(0);
            holder.progressBarDet.setVisibility(View.VISIBLE);
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    int step = 0;
                    try {
                        //                        //connecting to url
//                        String url = "http://pakoob24.ir/ui/temp/aa.jpg";
                        URL url = new URL(remoteAddr);

                        //1401-03-23 : https://stackoverflow.com/questions/59093629/add-a-self-signed-ssl-certificate-to-httpurlconnection
                        //رفع خطای دانلود ناموفق
                        if ( (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)){
                            TrustManager[] trustAllCerts = new TrustManager[]{
                                    new X509ExtendedTrustManager() {
                                        public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) {}
                                        public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) {}
                                        public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) {}
                                        public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) {}
                                        public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                                        public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                                        public X509Certificate[] getAcceptedIssuers() {
                                            return new X509Certificate[0];
                                        }
                                    }
                            };

                            SSLContext sslContext = SSLContext.getInstance("TLS");
                            sslContext.init(null, trustAllCerts, null);

                            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
                        }

                        URLConnection connection = url.openConnection();
                        step = 10;
                        connection.setConnectTimeout(30000);
                        connection.connect();
                        step = 20;

                        // download the file

//                        URL u = new URL(url);
//                        HttpURLConnection c = (HttpURLConnection) u.openConnection();
//                        c.setRequestMethod("GET");
//                        c.setDoOutput(true);
//                        c.connect();

                        int lenghtOfFile = connection.getContentLength();//c.getContentLength();
                        step = 30;
                        if (lenghtOfFile <= 0) {
                            handler.post(new Runnable() {
                                public void run() {
                                    projectStatics.showDialog(context, "خطا در دانلود", "فایل درخواستی قابل دانلود نمی باشد.", getResources().getString(R.string.ok), null, "", null);
                                }
                            });
                            isDownloading = false;
                            return;
                        }
                        step = 40;

                        int BUFFER_SIZE = 8192;
                        //this is where the file will be seen after the download
                        FileOutputStream output = new FileOutputStream(new File(tempDownloadDirectoryName, fileName));
                        step = 50;
                        //file input is from the url
                        InputStream in = connection.getInputStream();//new BufferedInputStream(url.openStream());//c.getInputStream();
                        step = 60;
                        BufferedInputStream bis = new BufferedInputStream(in, BUFFER_SIZE);
                        step = 70;

                        //here’s the download code
                        byte[] buffer = new byte[BUFFER_SIZE];
                        int len1 = 0;
                        long total = 0;

                        while ((len1 = bis.read(buffer, 0, BUFFER_SIZE)) > 0) {
                            total += len1; //total = total + len1
                            long finalTotal = total;
                            handler.post(new Runnable() {
                                public void run() {
                                    holder.progressBarDet.setProgress((int) ((finalTotal * 100) / lenghtOfFile));
                                }
                            });
                            output.write(buffer, 0, len1);
                        }
                        step = 80;
                        in.close();
                        output.flush();
                        output.close();
                        step = 90;

                        File tempDownloadFile = new File(tempDownloadDirectoryName, fileName);
                        if (tempDownloadFile.length() != lenghtOfFile) {
                            handler.post(new Runnable() {
                                public void run() {
                                    projectStatics.showDialog(context, "خطا در دانلود", "فایل درخواستی به شکل نامناسبی دانلود شد. لطفا دوباره تلاش کنید.", getResources().getString(R.string.ok), null, "", null);
                                }
                            });
                            isDownloading = false;
                            return;
                        }
                        step = 100;
                        boolean moveRes = hutilities.moveFile(tempDownloadDirectoryName + File.separator + fileName, mapsFolderName, fileName);
                        if (!moveRes) {
                            handler.post(new Runnable() {
                                public void run() {
                                    projectStatics.showDialog(context, "خطا در دانلود", "در حال حاضر امکان ساعت فایل دانلود شده وجود ندارد. لطفا دوباره تلاش کنید.", getResources().getString(R.string.ok), null, "", null);
                                }
                            });
                            isDownloading = false;
                            return;
                        }
                        step = 110;

                        handler.post(new Runnable() {
                            public void run() {
                                holder.progressBarDet.setVisibility(View.GONE);
                            }
                        });

                        handler.post(new Runnable() {
                            public void run() {
                                Toast.makeText(context, "دانلود کامل شد ... در حال ساخت مسیر", Toast.LENGTH_LONG).show();
                            }
                        });
                        handler.post(new Runnable() {
                            public void run() {
                                // آغاز عملیات دیکریپت کردن
                                DecryptAndGenerateMapAndDbUpdate(hMapTools.CustomMapMinZoom, hMapTools.CustomMapMaxZoomNormal, mapsFolderName, fileName, currentObj, holder);
                            }
                        });
                    } catch (Exception e) {

                        Log.e("اکسپشن", step + " _ " + remoteAddr + "\n" + e.getMessage());
                        e.printStackTrace();
                        String msg = "یک خطای پیش بینی نشده در هنگام دانلود رخ داده است. لطفا دوباره تلاش کنید.";
                        if (e.getMessage().toLowerCase().contains("time"))
                            msg = "مدت زمان زیادی برای دانلود سپری شد. احتمالا اینترنت شما ضعیف است یا مشکلی در سرور وجود دارد.";
                        String msgToShow = msg;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                projectStatics.showDialog(context, "خطا در دانلود", msgToShow, getResources().getString(R.string.ok), null, "", null);
                                holder.progressBarDet.setVisibility(View.GONE);
                            }
                        });
                        TTExceptionLogSQLite.insert(step + "-" + remoteAddr + "--"+ e.getMessage(),stktrc2k(e) , PrjConfig.frmMapSelect, 400);

                    }
                    isDownloading = false;
                }
            });
            thread.start();
        }

        void DecryptAndGenerateMapAndDbUpdate(int fromZoom, int toZoom, String mapsDirectoryName, String fileName, NbMap currentObj, NbMapViewHolder holder) {
            Handler handler = new Handler(Looper.getMainLooper());
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        isDownloading = true;
                        handler.post(new Runnable() {
                            public void run() {
                                Toast.makeText(context, "در حال استخراج نقشه...", Toast.LENGTH_LONG).show();
                            }
                        });

                        handler.post(new Runnable() {
                            public void run() {
                                holder.progressBarIndet.setVisibility(View.VISIBLE);
                            }
                        });
                        String output = hMapTools.decryptToTemp(mapsDirectoryName + fileName, context,hutilities.CCustomerId);
                        List<LatLng> bounds = currentObj.getBounds();

                        boolean tileCreateRes = hMapTools.craeteTilesAtManyZoom(output, context, bounds, fromZoom, toZoom);
                        MapSelect.clearTemp(context);

                        if (tileCreateRes) {
                            currentObj.Extracted = 1;
                            currentObj.LocalFileAddress = mapsDirectoryName + File.separator + fileName;
                            currentObj.IsVisible = 1;
                            NbMapSQLite.update(currentObj);
                        }

                        handler.post(new Runnable() {
                            public void run() {
                                Toast.makeText(context, "نقشه قابل استفاده می باشد", Toast.LENGTH_LONG).show();
                            }
                        });

                        handler.post(new Runnable() {
                            public void run() {
                                holder.progressBarIndet.setVisibility(View.INVISIBLE);

                                if (holder.btnOrder != null && tileCreateRes) {
                                    holder.btnOrder.setText(getString(R.string.view));
                                    holder.btnMore.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        TTExceptionLogSQLite.insert("Decrypt:" + ex.getMessage(),stktrc2k(ex), PrjConfig.frmMapSelect, 253);
                    }
                    isDownloading = false;;
                }
            });

            thread.start();
        }


        private boolean writeResponseBodyToDisk(ResponseBody body, String tempDirectoryName, String fileName) {
            try {
                File tempDirectory = new File(tempDirectoryName);
                if (!tempDirectory.exists()) {
                    tempDirectory.mkdirs();
                }
                // change the file location/name according to your needs
                File futureStudioIconFile = new File(tempDirectoryName + File.separator + fileName);

                InputStream inputStream = null;
                OutputStream outputStream = null;

                try {
                    byte[] fileReader = new byte[4096];

                    long fileSize = body.contentLength();
                    long fileSizeDownloaded = 0;

                    inputStream = body.byteStream();
                    outputStream = new FileOutputStream(futureStudioIconFile);

                    while (true) {
                        int read = inputStream.read(fileReader);

                        if (read == -1) {
                            break;
                        }

                        outputStream.write(fileReader, 0, read);

                        fileSizeDownloaded += read;

                        Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                    }

                    outputStream.flush();

                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }

                    if (outputStream != null) {
                        outputStream.close();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }


        class NbMapDiffCallback extends DiffUtil.Callback {

            private final List<NbMap> oldNbMaps, newNbMaps;

            public NbMapDiffCallback(List<NbMap> oldNbMaps, List<NbMap> newNbMaps) {
                this.oldNbMaps = oldNbMaps;
                this.newNbMaps = newNbMaps;
            }

            @Override
            public int getOldListSize() {
                return oldNbMaps.size();
            }

            @Override
            public int getNewListSize() {
                return newNbMaps.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return oldNbMaps.get(oldItemPosition).NbMapId == newNbMaps.get(newItemPosition).NbMapId;
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return oldNbMaps.get(oldItemPosition).equals(newNbMaps.get(newItemPosition));
            }
        }
    }

    public interface OnMapDbSyncCompleted {
        void onMapDbSyncCompleted(boolean isSuccessful);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {//3nd Event
        return inflater.inflate(R.layout.mapselect, parent, false);
    }
}