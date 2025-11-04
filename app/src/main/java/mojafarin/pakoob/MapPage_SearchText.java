package mojafarin.pakoob;

import static android.content.Context.CONTACT_KEYS_SERVICE;
import static mojafarin.pakoob.MainActivity.currentLatLon;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import bo.NewClasses.SimpleRequest;
import bo.entity.NbPoi;
import bo.entity.NbPoiList;
import bo.entity.SearchRequestDTO;
import bo.sqlite.NbPoiSQLite;
import bo.sqlite.TTExceptionLogSQLite;
import maptools.GPXFile;
import maptools.GeoCalcs;
import maptools.InfoBottomPoint;
import maptools.InfoBottomTrack;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.HFragment;
import utils.MainActivityManager;
import utils.PrjConfig;
import utils.RecyclerTouchListener;
import utils.TextFormat;
import utils.hutilities;
import utils.projectStatics;

public class MapPage_SearchText extends HFragment {
    View.OnClickListener ClickFor_نقطه_داخل_دیتابیس;
    View.OnClickListener ClickFor_نمایش_همه_نقاط_جستجوی_متن_روی_نقشه;
    View.OnClickListener ClickFor_مختصات_مستقیم;
    View.OnClickListener CLickFor_backPressed;
    public String selectedText ;
    public NbPoi selectedPoi;
    public List<NbPoi> ResultList;
    TextView btnBack;

    public static MapPage_SearchText getInstance(String defText, View.OnClickListener ClickFor_مختصات_مستقیم, View.OnClickListener ClickFor_نقطه_داخل_دیتابیس
            , View.OnClickListener ClickFor_نمایش_همه_نقاط_جستجوی_متن_روی_نقشه, View.OnClickListener backPressed) {
        MapPage_SearchText res = new MapPage_SearchText();
        res.ClickFor_نمایش_همه_نقاط_جستجوی_متن_روی_نقشه = ClickFor_نمایش_همه_نقاط_جستجوی_متن_روی_نقشه;
        res.ClickFor_نقطه_داخل_دیتابیس = ClickFor_نقطه_داخل_دیتابیس;
        res.ClickFor_مختصات_مستقیم = ClickFor_مختصات_مستقیم;
        res.CLickFor_backPressed = backPressed;
        res.selectedText = defText;
        return res;
    }

    View view;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {//4th Event
        super.onViewCreated(view, savedInstanceState);
        this.view = view;

        initializeComponents(view);

    }

    //Start------Search Related Objects
    EditText txtSearch;
    private RecyclerView rvSearchResult;
    private MapPage_SearchText.NbPoisAdapter adapterSearch;
    boolean typeSearchMode = false;
    int currentSelectedNbPoiIndex = -1;
    private ScrollView scrollSearchMode;
    LinearLayout divSearch;
    TextView txtSearchResult;
    private ProgressBar pageProgressBar;
    Button btnShowResultOnMap;
    //End------ Search Related Objects

    @Override
    public void initializeComponents(View v) {
        txtSearch = v.findViewById(R.id.txtSearch);
        txtSearch.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                btnSearch_Click();
                return true;
            }
            return true;
        });
        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (txtSearch.getText().length() == 0) {
                    clearAllSearchResults();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        txtSearch.setOnFocusChangeListener((view1, hasFocus) -> {
        });

        //فوکوس کردن روی تکست باکس و باز کردن کیبورد
        txtSearch.requestFocus();
        hutilities.showKeyboardInsideFragment(context, txtSearch);

        btnShowResultOnMap = v.findViewById(R.id.btnShowResultOnMap);
        btnShowResultOnMap.setOnClickListener(view1 -> {
            ResultList = adapterSearch.data;
            selectedText = txtSearch.getText().toString();
            //context.HideChildFragment(this);
            ClickFor_نمایش_همه_نقاط_جستجوی_متن_روی_نقشه.onClick(view);
        });
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
        pageProgressBar = v.findViewById(R.id.progressBar);
        scrollSearchMode = v.findViewById(R.id.scrollSearchMode);
        divSearch = v.findViewById(R.id.divSearch);
        divSearch.setVisibility(View.GONE);
        txtSearchResult = v.findViewById(R.id.txtSearchResult);
        txtSearchResult.setVisibility(View.GONE);



        btnBack = v.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(view -> {
            PerformBeforeBackPressed();
            CLickFor_backPressed.onClick(view);
        });

        super.initializeComponents(v);
    }
    public void PerformBeforeBackPressed(){
        selectedText = txtSearch.getText().toString().trim();
        hutilities.hideKeyboard(context, txtSearch);
    }

    public void clearAllSearchResults() {
        adapterSearch.setData(new ArrayList<>());
    }

    @Override
    public void onResume() {
        super.onResume();
    }
    public void onResumeInChild() {
        try {

        } catch (Exception ex) {
            Log.e(Tag, "خطا" + ex.getMessage());
            ex.printStackTrace();
            TTExceptionLogSQLite.insert(ex.getMessage(), stktrc2k(ex), PrjConfig.frmMapPage_SearchText, 1001);
        }
    }

    @Override
    public boolean onBackPressedInChild() {
        //در تابع مربوطه در رویداد پدر  که میشه MapPage انجام میشه
        return super.onBackPressedInChild();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {//3nd Event
        return inflater.inflate(R.layout.activity_maps_search_text, parent, false);
    }

    //---------------------- شروع قمست های اضافه جستجو کردن ---------------------
    private void btnSearch_Click() {
        String text = txtSearch.getText().toString().trim();
        if (text.length() < 2){
            projectStatics.showDialog(context, getResources().getString(R.string.Attention), getResources().getString(R.string.PleaseEnterLongerTextToSearch), getResources().getString(R.string.btnAccept), view1 -> {txtSearch.requestFocus();}, "", null);
            return;
        }
        try {
            //اول بررسی این که آیا مختصات وارد کرده یا متن. اگه مختصات بود همینجا مشکل رو حل کنیم
            text = TextFormat.ReplacePersianNumbersWithEnglishOne(text);
            //a. lat/lon
            LatLng inpLocation = GeoCalcs.extractLatLonString(text);
            if (inpLocation == null) {
                inpLocation = GeoCalcs.extractUtmString(text);
            }

            if (inpLocation != null) {
                selectedPoi = new NbPoi();
                selectedPoi.NbPoiId = 0l;
                selectedPoi.Name = GPXFile.DoubleToStringForLatLan(inpLocation.latitude) + "," + GPXFile.DoubleToStringForLatLan(inpLocation.longitude);
                selectedPoi.LatBegin = selectedPoi.LatS = inpLocation.latitude;
                selectedPoi.LonBegin = selectedPoi.LonW = inpLocation.longitude;

                //performSearchResultPoiClick(selectedPoi);
                selectedText = txtSearch.getText().toString();
                //context.onBackPressed();
                //context.HideChildFragment(this);
                ClickFor_مختصات_مستقیم.onClick(view);

                return;
            }


            if (!hutilities.isInternetConnected(context)) {
                projectStatics.showDialog(context, this.getResources().getString(R.string.NoInternet), this.getResources().getString(R.string.NoInternet_Desc), this.getResources().getString(R.string.ok), view -> {
                }, "", null);
                return;
            }
            btnShowResultOnMap.setVisibility(View.INVISIBLE);
            hutilities.hideKeyboard(context, txtSearch);
            typeSearchMode = true;
            scrollSearchMode.setVisibility(View.VISIBLE);

            pageProgressBar.setVisibility(View.VISIBLE);

            SearchRequestDTO requestDTO = new SearchRequestDTO();
            requestDTO.Filter = text + "***";
            requestDTO.Filter += "***";//Bounds
            requestDTO.Filter += "***";//PoiType
            if (currentLatLon != null && currentLatLon.latitude != 0 && currentLatLon.longitude != 0) {
                requestDTO.Filter += Double.toString(currentLatLon.latitude) + "," + Double.toString(currentLatLon.longitude) + "***";//myLatLon
            } else
                requestDTO.Filter += "***";//myLatLon
            Call<ResponseBody> call = app.apiMap.SearchNbPoi_1(SimpleRequest.getInstance(requestDTO));

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (!isAdded()) return;
                    try {
                        divSearch.setVisibility(View.VISIBLE);
                        pageProgressBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {

                            NbPoiList result = NbPoiList.fromBytes(response.body().bytes());
                            if (!result.isOk) {
                                txtSearchResult.setVisibility(View.VISIBLE);
                                divSearch.setVisibility(View.GONE);
                                txtSearchResult.setText(result.message);
                            } else {
                                txtSearchResult.setVisibility(View.GONE);
                                divSearch.setVisibility(View.VISIBLE);
                            }
                            int resSize = result.resList.size();
                            if (result.resList.size() > 0) {
                                //Added 1401-07-05 برای رفع باگ نوشتن دکمه ذخیره و ویرایش
                                for (int i = 0; i < resSize; i++) {
                                    result.resList.get(i).NbPoiId = 0l;
                                }
                                btnShowResultOnMap.setVisibility(View.VISIBLE);
                                rvSearchResult.setVisibility(View.VISIBLE);
                                initAdapterSearch(result.resList);
                            } else {
                                txtSearchResult.setVisibility(View.VISIBLE);
                                divSearch.setVisibility(View.GONE);
                                txtSearchResult.setText(R.string.NoItemFoundInSearch);
                            }
                        } else {
                            txtSearchResult.setVisibility(View.VISIBLE);
                            divSearch.setVisibility(View.GONE);
                            txtSearchResult.setText("متاسفانه در برقراری ارتباط با سرور مشکلی به وجود آمده است. لطفا بعدا مجددا تلاش نمایید.");
                            Log.e(Tag, "MY_ERROR" + "ResponseCODE: " + response.code());
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Log.e(Tag, "MY_ERROR" + ex.getMessage());
                        TTExceptionLogSQLite.insert(ex.getMessage(), stktrc2k(ex), PrjConfig.frm_SearchOnMap, 100);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    TTExceptionLogSQLite.insert(t.getMessage(), stktrc2kt(t), PrjConfig.frm_SearchOnMap, 100);
                    if (!isAdded()) return;
                    divSearch.setVisibility(View.VISIBLE);
                    pageProgressBar.setVisibility(View.GONE);
                    txtSearchResult.setVisibility(View.VISIBLE);
                    divSearch.setVisibility(View.GONE);
                    txtSearchResult.setText("متاسفانه مشکلی در برقراری ارتباط با سرور به وجود آمده است. لطفا بعدا مجددا تلاش نمایید.");

                }
            });

        } catch (Exception ex) {
            projectStatics.showDialog(context, getString(R.string.ProblemInSearch), getString(R.string.ProblemInSearch_DESC)
                    , getString(R.string.accept), null, "", null);
            Log.e(Tag, "خطا" + " text " + ex.getMessage());
            TTExceptionLogSQLite.insert(ex.getMessage(), "Searched: " + text + " - " + stktrc2k(ex), PrjConfig.frmMapPage_SearchText, 1006);
            ex.printStackTrace();
        }
    }

    void initAdapterSearch(List<NbPoi> result) {
        if (true || adapterSearch == null) {

            MapPage_SearchText.NbPoisAdapter.OnItemClickListener itemClickListener = (post, Position) -> {
                RecyclerView_ItemClicked(post, Position);
            };
            adapterSearch = new MapPage_SearchText.NbPoisAdapter(context, "full", itemClickListener);
            adapterSearch.setData(result);
            rvSearchResult.setAdapter(adapterSearch);
        }
        if (rvSearchResult.getAdapter() == null) {

        }
    }

    public void RecyclerView_ItemClicked(NbPoi current, int index) {
        NbPoi poi = adapterSearch.data.get(index);
        hutilities.hideKeyboard((Activity) context);
        currentSelectedNbPoiIndex = index;

        //context.onBackPressed();
        //context.HideChildFragment(this);
        selectedText = txtSearch.getText().toString();
        selectedPoi = poi;
        ClickFor_نقطه_داخل_دیتابیس.onClick(view);
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

    public void reInit() {
        if (txtSearch.getText().length() == 0){
            hutilities.showKeyboardInsideFragment(context, txtSearch);
            btnShowResultOnMap.setVisibility(View.GONE);
        }
    }

    public static class NbPoisAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<NbPoi> data;
        private Context context;
        private LayoutInflater layoutInflater;

        MapPage_SearchText.NbPoisAdapter.OnItemClickListener itemClickFunction;
        String mode;

        public NbPoisAdapter(Context context, String _mode, MapPage_SearchText.NbPoisAdapter.OnItemClickListener _itemClickFunction) {
            this.mode = _mode;
            this.data = new ArrayList<>();
            this.context = context;
            this.itemClickFunction = _itemClickFunction;
        }

        @Override
        public MapPage_SearchText.NbPoisAdapter.NbPoiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            layoutInflater = LayoutInflater.from(context);
            //this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = layoutInflater.inflate(R.layout.activity_searchonmap_item, parent, false);
            return new MapPage_SearchText.NbPoisAdapter.NbPoiViewHolder(itemView, this.itemClickFunction);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            if (viewHolder instanceof MapPage_SearchText.NbPoisAdapter.NbPoiViewHolder) {
                ((MapPage_SearchText.NbPoisAdapter.NbPoiViewHolder) viewHolder).bind(data.get(position), position);
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

        public void setData(List<NbPoi> newData) {
            if (data != null) {
//                NbPoiDiffCallback postDiffCallback = new NbPoiDiffCallback(data, newData);
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


        //من اضافه کردم
        public abstract interface OnItemClickListener {
            public abstract void onItemClicked(NbPoi post, int Position);
        }

        class NbPoiViewHolder extends RecyclerView.ViewHolder {
            public View itemView;
            public TextView txtTitle, lblDistance, lblSecondLine, lblProvinceName;
            ImageView txtImage;
            public ProgressBar progressBarIndet;
            LinearLayout itemMainPart;
            MapPage_SearchText.NbPoisAdapter.OnItemClickListener itemClickFunction;

            NbPoiViewHolder(View itemView, MapPage_SearchText.NbPoisAdapter.OnItemClickListener itemClickFunction) {
                super(itemView);
                this.itemView = itemView;
                this.itemClickFunction = itemClickFunction;
                txtTitle = itemView.findViewById(R.id.txtTitle);
                lblProvinceName = itemView.findViewById(R.id.lblProvinceName);
                lblDistance = itemView.findViewById(R.id.lblDistance);
                itemMainPart = itemView.findViewById(R.id.itemMainPart);
                lblSecondLine = itemView.findViewById(R.id.lblSecondLine);
                txtImage = itemView.findViewById(R.id.txtImage);
                progressBarIndet = itemView.findViewById(R.id.itemprogressbarIndet);

                Drawable progressDrawable = progressBarIndet.getIndeterminateDrawable().mutate(); //or getProgressDrawable for normal //https://stackoverflow.com/questions/2020882/how-to-change-progress-bars-progress-color-in-android
                progressDrawable.setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
                progressBarIndet.setProgressDrawable(progressDrawable);

                lblDistance.setTypeface(projectStatics.getIranSans_FONT(context));
                txtTitle.setTypeface(projectStatics.getIranSans_FONT(context));
                lblSecondLine.setTypeface(projectStatics.getIranSans_FONT(context));
            }


            void bind(final NbPoi currentObj, int position) {
                if (currentObj != null) {
                    txtTitle.setText(currentObj.Name + (currentObj.AltName.length() > 0 ? " (" + currentObj.AltName + ")" : ""));
                    lblDistance.setText(currentObj.distanceFromHere);

                    lblSecondLine.setText(currentObj.addedInfo);
                    lblProvinceName.setText(currentObj.ProvinceName);
                    txtImage.setImageResource(NbPoi.PoiTypeToResourcId(currentObj.PoiType, 1));
                    txtImage.setColorFilter(context.getResources().getColor(NbPoi.PoiTypeToResourcId(currentObj.PoiType, 2)));

                    itemMainPart.setOnClickListener(view -> {
                        itemClickFunction.onItemClicked(currentObj, position);
//                        NbPoi currentSelectedClub  = adapterSearch.data.get(position);
//                        hutilities.hideKeyboard((Activity)context);
//                        currentSelectedClubIndex = position;
//                        ClubView_Home currentHomeFragment = new ClubView_Home(currentSelectedClub, PrjConfig.frmClubSearch);
//                        ((MainActivityManager) context).showFragment(currentHomeFragment);
                    });

                    //در صورت نیاز، از نسخه جدید پیکاسو که در همه فرم ها وجود داره استفاده بشه نه این
                    //Picasso builder = Picasso.get();
//                builder.load(currentObj.Logo).tag(PicassoOnScrollListener.TAG)//.config(Bitmap.Config.RGB_565).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
//                        //.placeholder((R.drawable.ic_launcher_background)) HHH 1400-01-10
//                        //.error(R.drawable.ic_launcher_background)  HHH 1400-01-10
//                        .transform(new PicassoCircleTransform()).into(txt_ct_ImageLinkUri);
                }
            }
        }

        class NbPoiDiffCallback extends DiffUtil.Callback {

            private final List<NbPoi> oldNbPois, newNbPois;

            public NbPoiDiffCallback(List<NbPoi> oldNbPois, List<NbPoi> newNbPois) {
                this.oldNbPois = oldNbPois;
                this.newNbPois = newNbPois;
            }

            @Override
            public int getOldListSize() {
                return oldNbPois.size();
            }

            @Override
            public int getNewListSize() {
                return newNbPois.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return oldNbPois.get(oldItemPosition).ServerId == newNbPois.get(newItemPosition).ServerId;
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return oldNbPois.get(oldItemPosition).equals(newNbPois.get(newItemPosition));
            }
        }
    }

    //---------------------- پایان قمست های اضافه جستجو کردن ---------------------

}
