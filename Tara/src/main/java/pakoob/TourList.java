package pakoob;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pakoob.tara.R;

import java.util.ArrayList;
import java.util.List;

import HMultiSelect.MultiSelectDialog;
import HMultiSelect.MultiSelectModel;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import bo.PageIds_PA;
import bo.dbConstantsTara;
import bo.entity.GetTourListDTO;
import bo.entity.MobileInfoDTO;
import bo.NewClasses.SimpleRequest;
import bo.entity.TTClubTour;
import bo.entity.Adapter_TTClubTour;
import bo.entity.TourListResult;
import bo.sqlite.TTExceptionLogSQLite;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.HFragment;
import utils.MainActivityManager;
import utils.PrjConfig;
import utils.PrjEnums;
import utils.RecyclerTouchListener;
import utils.TextFormat;
import utils.hutilities;
import utils.projectStatics;

public class TourList extends HFragment {
    public static final int MODE_ALL = 1;
    public static final int MODE_Tour = 2;
    public static final int MODE_Class = 3;
    public static final int MODE_Custom = 4;

    public int Mode = MODE_ALL;
    public String Param = "";
    public TourList(int TourListMode, String TourListParam){
        this.Mode = TourListMode;
        this.Param = TourListParam;
    }
    private Adapter_TTClubTour adapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeContainer;
    private RelativeLayout divNoItemExists, divContent;
    LinearLayout divNoInternet;
    LinearLayout layFilters;

    Button btnRefreshInternet;
    View _root;
    boolean isLoadingMore = false;
    final Integer readPageSize = 100;
    MainActivityManager parentActivity;//use it instead of getActivity() or getContext()
    public PrjEnums.LoadDataTypes defaultLoadType = PrjEnums.LoadDataTypes.FirstRead;

    TextView miFilter, miSort;
    TextView btnBack;
    TextView txtPageTitle;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        //1400-04-18
        //dbConstantsTara.initFiltersAsync();

        View root = inflater.inflate(R.layout.tour_list, container, false);
        _root = root;

        btnBack = _root.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(view -> {(parentActivity).onBackPressed();});

        txtPageTitle = _root.findViewById(R.id.txtPageTitle);
        if (Mode == MODE_Class){
            txtPageTitle.setText(getString(R.string.title_all_tours_Classes));
        }
        recyclerView = _root.findViewById(R.id.rvContacts);//_root be khatere fragment ezafe shod. in tab'e bayad baed az onViewCreated estefade she va tooye onCreate ya OnCreateView estefade she
        progressBar = _root.findViewById(R.id.progressBar_clubtour);
        swipeContainer = _root.findViewById(R.id.swipeContainer);
        divNoInternet = _root.findViewById(R.id.divNoInternet);
        divNoItemExists = _root.findViewById(R.id.divNoItemExists);
        divContent = _root.findViewById(R.id.divContent);
        btnRefreshInternet = _root.findViewById(R.id.btnRefreshInternet);
        btnRefreshInternet.setOnClickListener(view -> {setVisibilityOfRefreshLoading(View.GONE);ReadToursAndFillRecycler(PrjEnums.LoadDataTypes.FirstRead);});
        miFilter = _root.findViewById(R.id.miFilter);
        miFilter.setOnClickListener(view -> {changeFilterVisibility(true);});
        miSort = _root.findViewById(R.id.miSort);
        miSort.setOnClickListener(view -> {showSortBox();});

        layFilters =  _root.findViewById(R.id.layFilters);
        layFilters.setVisibility(View.INVISIBLE);
        initRecyclerView();
        initSweepToRefresh();
        initScrollListener();
        ReadToursAndFillRecycler(defaultLoadType);

        //Ghesmat haye marboot be namayeshe FILTER haaaaaaaaaaaaaaaaaa
       initFilters();
        //End namayeshe FITLER Haaaaaaaaaaa

        //((MainActivity)parentActivity).highlightFilter();

        return root;
    }

    private void initFilters() {
        //dar soorati ke hadeaghal yek filter vojood dasht, box-e marbooteh namayesh dade beshe
        if( (dbConstantsTara.session.get_FilterCityIds().size() > 0 && dbConstantsTara.session.get_FilterCityIds().get(0) != 0)
                || (dbConstantsTara.session.get_FilterCategoryIds().size() > 0 && dbConstantsTara.session.get_FilterCategoryIds().get(0) != 0)
                || (dbConstantsTara.session.get_FilterClubNameIds().size() > 0 && dbConstantsTara.session.get_FilterClubNameIds().get(0) != 0)
                || (dbConstantsTara.session.get_FilterTourLengths().size() > 0 && dbConstantsTara.session.get_FilterTourLengths().get(0) != 0)
        ) {
            changeFilterVisibility(false);
        }

        btnCityFilter =  _root.findViewById(R.id.btnCityFilter);
        setFilterBoxSelected(btnCityFilter, dbConstantsTara.session.get_FilterCityIds().size() > 0 && dbConstantsTara.session.get_FilterCityIds().get(0) != 0);
        btnCityFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canShowFilterBox()) return;
                showDialogForFilters("لطفا شهر مورد نظر را انتخاب نمایید", dbConstantsTara.getCitiesForMutliSelect(), dbConstantsTara.session.get_FilterCityIds(), "city");
            }
        });
        btnCatFilter =  _root.findViewById(R.id.btnCatFilter);
        setFilterBoxSelected(btnCatFilter, dbConstantsTara.session.get_FilterCategoryIds().size() > 0 && dbConstantsTara.session.get_FilterCategoryIds().get(0) != 0);
        btnCatFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canShowFilterBox()) return;
                showDialogForFilters("لطفا دسته بندی مورد نظر را انتخاب نمایید", dbConstantsTara.getCategoriesForMutliSelect(), dbConstantsTara.session.get_FilterCategoryIds(), "cat");

            }
        });
        btnClubNameFilter =  _root.findViewById(R.id.btnClubNameFilter);
        setFilterBoxSelected(btnClubNameFilter, dbConstantsTara.session.get_FilterClubNameIds().size() > 0 && dbConstantsTara.session.get_FilterClubNameIds().get(0) != 0);
        btnClubNameFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ClubName", "Clicked");
                if (!canShowFilterBox()) return;
                showDialogForFilters("لطفا باشگاههای مورد نظر را انتخاب نمایید", dbConstantsTara.getClubNamesForMutliSelect(), dbConstantsTara.session.get_FilterClubNameIds(), "club");
            }
        });
        btnLengthFilter =  _root.findViewById(R.id.btnLengthFilter);
        setFilterBoxSelected(btnLengthFilter, dbConstantsTara.session.get_FilterTourLengths().size() > 0 && dbConstantsTara.session.get_FilterTourLengths().get(0) != 0);
        btnLengthFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canShowFilterBox()) return;
                showDialogForFilters("لطفا مدت برنامه را انتخاب نمایید", dbConstantsTara.getTourLengthForMutliSelect(), dbConstantsTara.session.get_FilterTourLengths(), "len");
            }
        });
    }

    private void setVisibilityOfRefreshLoading(int gone) {
        divNoInternet.setVisibility(gone);
        divContent.setVisibility(gone == View.GONE ? View.VISIBLE : View.GONE);
    }

    LinearLayout btnCityFilter;
    LinearLayout btnCatFilter;
    LinearLayout btnClubNameFilter;
    LinearLayout btnLengthFilter;

    void setFilterBoxSelected(LinearLayout app_layer, boolean selected) {
        Drawable unwrappedDrawable = null;
        Drawable wrappedDrawable = null;
        if (unwrappedDrawable == null)
            unwrappedDrawable = AppCompatResources.getDrawable(parentActivity, R.drawable.button_round);

        if (!selected) {

            DrawableCompat.setTint(unwrappedDrawable, ContextCompat.getColor(parentActivity, pakoob.DbAndLayout.R.color.filter_tour_bg));
            app_layer.setBackground(unwrappedDrawable);
        } else {
            if (wrappedDrawable == null) {
                wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
//              DrawableCompat.setTint(wrappedDrawable, 0xff00ccff);
                DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(parentActivity, pakoob.DbAndLayout.R.color.filter_tour_bg_selected));
            }
            app_layer.setBackground(wrappedDrawable);
        }
        //GradientDrawable border = (GradientDrawable)app_layer.getBackground();
        //border.setColor(0x668eccFF); //white background
        //border.setStroke(1, 0xFFDC0000);
        //border.setStroke(1, 0xFF000000); //black border with full opacity
//        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
//            app_layer.setBackgroundDrawable(border);
//        } else {
//            app_layer.setBackground(border);
//        }
    }

    boolean canShowFilterBox() {
        if (dbConstantsTara.loadingFilters) {
            Toast.makeText(getContext(), "در حال دریافت اطلاعات...لطفا کمی بعد تلاش کنید", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!dbConstantsTara.loadingFilters && dbConstantsTara.cities == null) {
            Toast.makeText(getContext(), "در حال دریافت اطلاعات...لطفا کمی بعد تلاش کنید", Toast.LENGTH_SHORT).show();
            //1400-04-18
            //dbConstantsTara.initFiltersAsync();
            return false;
        }
        return true;
    }

    MultiSelectDialog showDialogForFilters(String Title, ArrayList<MultiSelectModel> items, ArrayList<Integer> preSelected, String FilterType) {
        MultiSelectDialog multiSelectDialog = new MultiSelectDialog()
                .title(Title)//getResources().getString(R.string.multi_select_dialog_title) //setting title for dialog
                .titleSize(16)
                .positiveText("انتخاب")
                .negativeText("انصراف")
                .setMinSelectionLimit(0) //you can set minimum checkbox selection limit (Optional)
                //.setMaxSelectionLimit(listOfCountries.size()) //you can set maximum checkbox selection limit (Optional)
                .preSelectIDsList(preSelected) //List of ids that you need to be selected
                .multiSelectList(items) // the multi select model list with ids and name
                .onSubmit(new MultiSelectDialog.SubmitCallbackListener() {
                    @Override
                    public void onSelected(ArrayList<Integer> selectedIds, ArrayList<String> selectedNames, String dataString) {
                        //در صورتی که عناصر تغیییر کرده بود
                        //اول ذخیره سازی عناصر
                        if (FilterType == "city") {
                            Log.d("Filter", "city - " + (dbConstantsTara.session.get_FilterCityIds().size() > 0 && dbConstantsTara.session.get_FilterCityIds().get(0) != 0));
                            dbConstantsTara.session.set_FilterCityIds(selectedIds);
                            setFilterBoxSelected(btnCityFilter, dbConstantsTara.session.get_FilterCityIds().size() > 0 && dbConstantsTara.session.get_FilterCityIds().get(0) != 0);
                        }
                        if (FilterType == "cat") {
                            Log.d("Filter", "cat - " + (dbConstantsTara.session.get_FilterCategoryIds().size() > 0 && dbConstantsTara.session.get_FilterCategoryIds().get(0) != 0));
                            dbConstantsTara.session.set_FilterCategoryIds(selectedIds);
                            setFilterBoxSelected(btnCatFilter, dbConstantsTara.session.get_FilterCategoryIds().size() > 0 && dbConstantsTara.session.get_FilterCategoryIds().get(0) != 0);
                        }
                        if (FilterType == "club") {
                            Log.d("Filter", "club - " + (dbConstantsTara.session.get_FilterClubNameIds().size() > 0 && dbConstantsTara.session.get_FilterClubNameIds().get(0) != 0));
                            dbConstantsTara.session.set_FilterClubNameIds(selectedIds);
                            setFilterBoxSelected(btnClubNameFilter, dbConstantsTara.session.get_FilterClubNameIds().size() > 0 && dbConstantsTara.session.get_FilterClubNameIds().get(0) != 0);
                        }
                        if (FilterType == "len") {
                            Log.d("Filter", "len - " + (dbConstantsTara.session.get_FilterTourLengths().size() > 0 && dbConstantsTara.session.get_FilterTourLengths().get(0) != 0));
                            dbConstantsTara.session.set_FilterTourLengths(selectedIds);
                            setFilterBoxSelected(btnLengthFilter, dbConstantsTara.session.get_FilterTourLengths().size() > 0 && dbConstantsTara.session.get_FilterTourLengths().get(0) != 0);
                        }

                        ReadToursAndFillRecycler(PrjEnums.LoadDataTypes.Refresh);
                    }

                    @Override
                    public void onCancel() {
                    }
                });

        multiSelectDialog.show(getFragmentManager(), "multiSelectDialog");
        return multiSelectDialog;
    }

    private void initSweepToRefresh() {
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (divNoItemExists.getVisibility() == View.VISIBLE)
                    divNoItemExists.setVisibility(View.GONE);
                // Make sure you call swipeContainer.setRefreshing(false) once the network request has completed successfully.
                ReadToursAndFillRecycler(PrjEnums.LoadDataTypes.Refresh);
                swipeContainer.setRefreshing(false);
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void initRecyclerView() {
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
//1400-04-14 moved to event
                //                TTClubTour selectedTour = dbConstantsTara.getTours().get(position);
//
//                parentActivity.showFragment(new TourShowOne(position, 0));

                //1400-01-12 commented
//                Intent i = new Intent(getContext(), TourShowOne.class);
//                i.putExtra("ix", position);
//                i.putExtra("isMyClubTours", 0);
//                startActivity(i);

            }

            @Override
            public void onLongClick(View view, int position) {
                TTClubTour selectedTour = dbConstantsTara.getTours().get(position);
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
                if (dx == 0 && dy == 0)
                    return;// for stop duplicatie loading

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoadingMore) {
                    if (linearLayoutManager != null
                            && linearLayoutManager.findLastCompletelyVisibleItemPosition() == dbConstantsTara.getTours().size() - 1
                            && dbConstantsTara.getTours().size() >= readPageSize - 1
                    ) {
                        //bottom of list!
                        loadMore();
                        isLoadingMore = true;
                    }
                }
            }
        });
    }

    private void loadMore() {
        List<TTClubTour> tmpTours = dbConstantsTara.getTours();
        tmpTours.add(null);
        adapter.notifyItemInserted(tmpTours.size() - 1);

        ReadToursAndFillRecycler(PrjEnums.LoadDataTypes.LoadMore);
    }

    public void ReadToursAndFillRecycler(PrjEnums.LoadDataTypes loadType) {
        if (!hutilities.isInternetConnected(context)) {
            projectStatics.showDialog(context, context.getResources().getString(R.string.NoInternet), context.getResources().getString(R.string.NoInternet_Desc), context.getResources().getString(R.string.ok), view -> {}, "", null);
            return;
        }
        Log.d("adap1", "after load1");

        if (loadType == PrjEnums.LoadDataTypes.FirstRead
                //|| (loadType == LoadDataTypes.LoadMemory && (Calendar.getInstance().getTime().getTime() - dbConstantsTara.LastToursRead.getTime().getTime()) / 1000 > dbConstantsTara.ReadTimeOutInSecond)
                || loadType == PrjEnums.LoadDataTypes.LoadMore
                || loadType == PrjEnums.LoadDataTypes.Refresh) {
            Log.d("adap1", "first Read1 and loadtype is " + loadType);

            Integer pageIndex = 0;
            //if first load, show loading box
            if (loadType == PrjEnums.LoadDataTypes.FirstRead || loadType == PrjEnums.LoadDataTypes.LoadMemory || loadType == PrjEnums.LoadDataTypes.Refresh)
                progressBar.setVisibility(View.VISIBLE);
            else if (loadType == PrjEnums.LoadDataTypes.LoadMore) {
                pageIndex = dbConstantsTara.getTours().size();
            }
            // Log.d("ReadToursAndFillR", "Read From DB");

            /*Create handle for the RetrofitInstance interface*/
            //ApiInterface service = ApiClient.getClient().create(ApiInterface.class);
            GetTourListDTO reqObj = new GetTourListDTO();
            reqObj.info = MobileInfoDTO.instance();
            reqObj.ClubNameIds = TextFormat.ListToString(dbConstantsTara.session.get_FilterClubNameIds(), ",", "0");
            switch (Mode){
                case MODE_ALL:
                case MODE_Custom:
                    reqObj.CategoryIds = TextFormat.ListToString(dbConstantsTara.session.get_FilterCategoryIds(), ",", "0");
                    break;
                case MODE_Class:
                    reqObj.CategoryIds = "CLASS";
                    break;
                case MODE_Tour:
                    reqObj.CategoryIds = "TOUR";
                    break;
            }

            reqObj.TourLengthIds = TextFormat.ListToString(dbConstantsTara.session.get_FilterTourLengths(), ",", "0");
            reqObj.CityIds = TextFormat.ListToString(dbConstantsTara.session.get_FilterCityIds(), ",", "0");
            reqObj.Sort = dbConstantsTara.session.getSort_of_tour();
            reqObj.FromItemIndex =  pageIndex.toString();
            reqObj.PageSize = readPageSize.toString();
            reqObj.Key = dbConstantsTara.key;
            Call<ResponseBody> call = dbConstantsTara.apiTara.GetTourList(SimpleRequest.getInstance(reqObj));

            Log.d("adap1", "call");

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (!isAdded()) return;
                    try {
                        progressBar.setVisibility(View.GONE);
                        if (response.code() == 200) {
                            Log.d("adap1", "call back 110");

                            TourListResult dataResult = TourListResult.fromBytes(response.body().bytes());
                            List<TTClubTour> recItems = dataResult.resList;
                            setVisibilityOfRefreshLoading(View.GONE);
                            dbConstantsTara.session.setLastPublishRead_FromRecievedTours(recItems);

                            //show hide No ITEM Exists
                            if (recItems.size() == 0 && dbConstantsTara.getTours().size() == 0)
                                divNoItemExists.setVisibility(View.VISIBLE);
                            else
                                divNoItemExists.setVisibility(View.GONE);
                            Log.d("adap1", "call back 120");

                            Log.d("adap1", "call back 121 - loadType : "+loadType);

                            if (loadType == PrjEnums.LoadDataTypes.LoadMore) {
                                Log.d("adap1", "call back 130");

                                List<TTClubTour> nList = dbConstantsTara.getTours();

                                //Init if needed
                                initAdapter(nList);
                                Log.d("adap1", "call back 135");

                                //hazfe LOADING az view
                                int itemToRemoveIndex = nList.size() - 1;
                                nList.remove(itemToRemoveIndex);
                                Log.d("adap1", "call back 140");

                                adapter.notifyItemRemoved(itemToRemoveIndex + 1);
                                Log.d("adap1", "call back 150");

                                if (recItems != null && recItems.size() > 0) {
                                    Log.d("adap1", "call back 160");

                                    nList.addAll(recItems);
                                    notifyAdapterToUpdate(nList);
                                    Log.d("adap1", "call back 170");

                                } else {
                                    Log.d("adap1", "call back 175");

                                    Toast.makeText(parentActivity, "همه اطلاعات دریافتی همین بود!", Toast.LENGTH_SHORT).show();//bayad TourList.this bejaye getActivity() ya parentActivity mibood
                                }

                                finishLoadingEnvironment();
                                Log.d("adap1", "call back 180");

                            } else if (loadType == PrjEnums.LoadDataTypes.FirstRead) {
                                Log.d("adap1", "call back 190");

                                dbConstantsTara.setTours(recItems);
                                Log.d("adap1", "call back 200");

                                initAdapter(dbConstantsTara.getTours());
                                Log.d("adap1", "call back 210");

                                finishLoadingEnvironment();
                            } else if (loadType == PrjEnums.LoadDataTypes.Refresh) {
                                Log.d("adap1", "call back 220");

                                List<TTClubTour> nList = dbConstantsTara.getTours();
                                nList.clear();
                                nList.addAll(recItems);
                                Log.d("adap1", "call back 230");

                                //Init if needed
                                initAdapter(recItems);
                                Log.d("adap1", "call back 240");

                                notifyAdapterToUpdate(nList);
                                finishLoadingEnvironment();
                                Log.d("adap1", "call back 250");

                            }
                        }
                        else{
                            setVisibilityOfRefreshLoading(View.VISIBLE);
                            projectStatics.ManageCallResponseErrors(true, PrjConfig.frmTourList, 210, getContext(), response.code());
                        }
//                    generateAdapter_Or_NotifyChange(dbConstantsTara.getTours());
                    } catch (Throwable e) {
                        projectStatics.ManageCallExceptions(true, PrjConfig.frmTourList, 210, e, getContext());
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
//                progressDoalog.dismiss();
                    TTExceptionLogSQLite.insert(t.getMessage(), stktrc2kt(t), PrjConfig.frmTourList, 100);
                    if (!isAdded()) return;
                    progressBar.setVisibility(View.GONE);
                    setVisibilityOfRefreshLoading(View.VISIBLE);
                    Log.d("MyERROR", t.getLocalizedMessage());
                    //Toast.makeText(parentActivity, "یه جای کار می لنگه، لطفا دوباره تلاش کنید...!", Toast.LENGTH_SHORT).show();//bayad TourList.this bejaye getActivity() mibood

                    finishLoadingEnvironment();
                }
            });
        } else if (loadType == PrjEnums.LoadDataTypes.LoadMemory) {
            Log.d("adap1", "load mem");

            progressBar.setVisibility(View.VISIBLE);
            Log.d("ReadToursAndFillR", "NOT Read From DB");
            //generateAdapter_Or_NotifyChange(dbConstantsTara.getTours());
            initAdapter(dbConstantsTara.getTours());
            finishLoadingEnvironment();

            progressBar.setVisibility(View.GONE);
        }
    }

    void initAdapter(List<TTClubTour> allTours) {
        Log.d("adap", "init");
        Log.d("adap", "allTours size : " + allTours.size());
        if (adapter == null) {
            adapter = new Adapter_TTClubTour(allTours, R.layout.tour_list_item, (post, Position) ->{
                parentActivity.showFragment(TourShowOne.getInstance(post, Position));
            }, null);
            Log.d("adap", "null bood");
        }
        if(recyclerView.getAdapter()== null){
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(parentActivity);//TourList.this bayad mibood
            recyclerView.setLayoutManager(layoutManager);
            //baraye namayeshe joda konandeh
            recyclerView.addItemDecoration(new DividerItemDecoration(parentActivity, LinearLayoutManager.VERTICAL));//parentActivity -> getActivity -> this bood
            recyclerView.setAdapter(adapter);
            Log.d("adap", "rec.adap null bood");
        }
        Log.d("adap", "tamoom");
    }

    void notifyAdapterToUpdate(List<TTClubTour> allTours) {
        if (adapter != null)
            adapter.notifyDataSetChanged();
        else
            initAdapter(allTours);
    }

    void finishLoadingEnvironment() {
        isLoadingMore = false;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        parentActivity = (MainActivityManager)activity;
    }

    //start --------------Namayeshe Filter Box ba CLICK rooye dokmeye filter --------------------------
    public void changeFilterVisibility(boolean showHideWithAnim){
        ValueAnimator anim;
        int currentHeight = layFilters.getMeasuredHeight();
        int maxHeight = dpToPx(25);

        if (currentHeight > 0){
            anim = ValueAnimator.ofInt(0);
        }
        else{
            anim = ValueAnimator.ofInt(maxHeight);
        }
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = layFilters.getLayoutParams();
                layoutParams.height = val;
                layFilters.setLayoutParams(layoutParams);

                //Tanzime Margin-e divContent
                LinearLayout.LayoutParams contentLayoutParams = (LinearLayout.LayoutParams) divContent.getLayoutParams();
                contentLayoutParams.setMargins(contentLayoutParams.leftMargin,val, contentLayoutParams.rightMargin, contentLayoutParams.bottomMargin);
                divContent.setLayoutParams(contentLayoutParams);
            }
        });
        anim.setDuration(showHideWithAnim?100:0);
        anim.start();
    }
    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }
    //end --------------Namayeshe Filter Box ba CLICK rooye dokmeye filter --------------------------
    public void showSortBox(){
        CharSequence items[] = new CharSequence[] {"نزدیک ترین برنامه ها", "جدیدترین برنامه ها", "ساده ترین برنامه ها", "مدت برنامه کم تر", "مدت برنامه بیشتر", "مبلغ برنامه کم تر", "مبلغ برنامه بیشتر"};
        final CharSequence sortValues[] = new CharSequence[] {"StartDate asc", "TTClubtourId desc", "TourHadnesssLevel asc", "TourLength asc", "TourLength desc", "TourFinalPrice asc", "TourFinalPrice desc"};
        String lastSort = dbConstantsTara.session.getSort_of_tour();
        int lastSelectedIndex = 0;
        if (lastSort == "") {
            lastSort = sortValues[0].toString();
            lastSelectedIndex = 0;
        }
        else {
            for (int i = 0; i < sortValues.length ; i++) {
                if (sortValues[i].toString() == lastSort){
                    lastSelectedIndex = i;
                    break;
                }
            }
        }

        int finalLastSelectedIndex = lastSelectedIndex;
        final AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity,  R.style.AlertDialogCustom)
                .setTitle(getResources().getString(R.string.please_select_tour_sort))
                .setSingleChoiceItems(items, lastSelectedIndex, (dialog, i) -> {
                    dialog.dismiss();
                    int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                    if (finalLastSelectedIndex != selectedPosition) {
                        dbConstantsTara.session.setSort_of_tour(sortValues[selectedPosition].toString());
                        ReadToursAndFillRecycler(PrjEnums.LoadDataTypes.Refresh);
                    }
                })
                .setNegativeButton("", (dialogInterface, i) -> {})
                .setPositiveButton("", (dialogInterface, i) -> {});
        final AlertDialog dialog = builder.create();
        dialog.show();
//        ((AlertDialog)dialog).getListView().setOnItemClickListener((adapterView, view, i, l) -> {});

    }



}
