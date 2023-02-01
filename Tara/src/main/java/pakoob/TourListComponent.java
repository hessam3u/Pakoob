package pakoob;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pakoob.tara.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import bo.PageIds_PA;
import bo.dbConstantsTara;
import bo.entity.Adapter_TTClubTour;
import bo.entity.GetTourListDTO;
import bo.entity.MobileInfoDTO;
import bo.NewClasses.SimpleRequest;
import bo.entity.TTClubTour;
import bo.entity.TourListResult;
import bo.sqlite.TTClubTourSQLite;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.PrjConfig;
import utils.hutilities;
import utils.projectStatics;

public class TourListComponent extends LinearLayout {
    public List<TTClubTour> recItems = null;
    public void myConstructor(){
        View v = inflate(getContext(), R.layout.tour_list_component_hor, this);
        recyclerView = v.findViewById(R.id.rvTourList);
        progressBarTourList = v.findViewById(R.id.progressBarTourList);
        lblMessageTourList = v.findViewById(R.id.lblMessageTourList);
    }

    public static final int CategoryTypesToShow_All = 0;
    public static final int CategoryTypesToShow_AllMinusLearning = 1;
    public static final int CategoryTypesToShow_Learning = 2;

    public static final int SORT_Default = 0;
    public static final int SORT_Newer = 1;
    public static final int SORT_Earlier = 2;
    public static final int SORT_Hottest = 3;

    public int CurrentCacheDBId = 0;
    public static final int CacheDBId_None = 0;
    public static final int CacheDBId_MyClub = 1;
    public static final int CacheDBId_HomeNewer = 2;
    public static final int CacheDBId_HomeEarlier = 3;
    public static final int CacheDBId_HomeHottest = 4;

    public void readAndShow(int CacheDBId, int ClubNameId, int CategoryTypesToShow, int SortType, int PageSize){
        CurrentCacheDBId = CacheDBId;
        if (!hutilities.isInternetConnected(context) && CurrentCacheDBId > 0) {
            List<TTClubTour> res = TTClubTourSQLite.SelectWithCache(CurrentCacheDBId);
            Log.e("کش سایز" + CacheDBId, Integer.toString(res.size()));
            ShowItems(res);
            return;
        }
        GetTourListDTO reqObj = new GetTourListDTO();
        reqObj.info = MobileInfoDTO.instance();
        reqObj.OtherParams = Integer.toString(CategoryTypesToShow);
        reqObj.ClubNameIds = Integer.toString(ClubNameId);
        reqObj.CategoryIds = "";
        reqObj.TourLengthIds = "";
        reqObj.CityIds = "";
        reqObj.Sort = "";
        reqObj.FromItemIndex =  "0";
        reqObj.PageSize = Integer.toString(PageSize);
        reqObj.Key = dbConstantsTara.key;

        switch (SortType){
            case SORT_Earlier:
                reqObj.Sort = "StartDate asc";
                break;
            case SORT_Newer:
                reqObj.Sort = "RecInsDate desc";
                break;
            case SORT_Hottest:
                reqObj.Sort = "ViewCount desc";
                break;
        }

        readAndShow(reqObj);
    }
    public void readAndShow(GetTourListDTO reqObj){
        if (!hutilities.isInternetConnected(context)) {
            //projectStatics.showDialog(context, getResources().getString(R.string.NoInternet), getResources().getString(R.string.NoInternet_Desc), getResources().getString(R.string.ok), view -> {}, "", null);
            return;
        }
        recyclerView.setVisibility(GONE);
        progressBarTourList.setVisibility(VISIBLE);
        lblMessageTourList.setVisibility(GONE);
        recItems = new ArrayList<>();
        Call<ResponseBody> call = dbConstantsTara.apiTara.GetTourList(SimpleRequest.getInstance(reqObj));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //if (!isAdded()) return;
                try {
                    //progressBar.setVisibility(View.GONE);
                    if (response.code() == 200) {
                        TourListResult dataResult = TourListResult.fromBytes(response.body().bytes());
                        recItems = dataResult.resList;
                        ShowItems(recItems);
                        if (CurrentCacheDBId > 0){
                            TTClubTourSQLite.DeleteWithCache(CurrentCacheDBId);
                            for (int i = 0; i < recItems.size(); i++) {
                                TTClubTour tour = recItems.get(i);
                                tour.SiteId = CurrentCacheDBId;
                                TTClubTourSQLite.insert(recItems.get(i));
                            }
                        }
                    }
                    else{
                        progressBarTourList.setVisibility(GONE);
//                        setVisibilityOfRefreshLoading(View.VISIBLE);
                        String msg = projectStatics.ManageCallResponseErrors(false, PrjConfig.frmTourList, 210, getContext(), response.code());
                        lblMessageTourList.setText(msg);
                    }
//                    generateAdapter_Or_NotifyChange(dbConstantsTara.getTours());
                } catch (Throwable e) {
                    progressBarTourList.setVisibility(GONE);
                    String msg = projectStatics.ManageCallExceptions(false, PrjConfig.frmTourList, 210, e, getContext());
                    lblMessageTourList.setText(msg);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                try {
                    String msg = projectStatics.ManageCallExceptions(false, PrjConfig.frmClubSearch,  100, t, getContext());
                    //if (!parent.isAdded()) return;
                    progressBarTourList.setVisibility(GONE);
                    lblMessageTourList.setText(msg);
                    Log.d("MyERROR", t.getLocalizedMessage());
                }
                catch (Exception ex){
                    //Unhandlled-Amdi
                }

            }
        });
    }

    private void ShowItems(List<TTClubTour> recItems) {
        adapter = new Adapter_TTClubTour(recItems, R.layout.tour_list_item_hor, onItemClickListener, null);
        recyclerView.setAdapter(adapter);

        if (recItems.size() == 0){
            lblMessageTourList.setVisibility(VISIBLE);
            lblMessageTourList.setText(getResources().getString(R.string.NoTourToShow));
            recyclerView.setVisibility(GONE);
        }
        else{
            lblMessageTourList.setVisibility(GONE);
            recyclerView.setVisibility(VISIBLE);
        }
        progressBarTourList.setVisibility(GONE);

        //setVisibilityOfRefreshLoading(View.GONE);

//                        //show hide No ITEM Exists
//                        if (recItems.size() == 0 && dbConstantsTara.getTours().size() == 0)
//                            divNoItemExists.setVisibility(View.VISIBLE);
//                        else
//                            divNoItemExists.setVisibility(View.GONE);
//                        Log.d("adap1", "call back 120");
    }

    ProgressBar progressBarTourList;
    TextView lblMessageTourList;
    Adapter_TTClubTour adapter;
    private RecyclerView recyclerView;
    Adapter_TTClubTour.OnItemClickListener onItemClickListener;
    Context context;
    public void setOnItemClickListener(Adapter_TTClubTour.OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
    public TourListComponent(Context context) {
        super(context);
        this.context = context;
        myConstructor();
    }

    public TourListComponent(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        myConstructor();
    }

    public TourListComponent(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        myConstructor();
    }
}
