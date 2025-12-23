package mojafarin.pakoob;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pakoob.TourListComponent;
import pakoob.TourShowOne;
import UI.HFragment;
import utils.PrjConfig;

public class HomeCurrentTourFragment extends HFragment {
    TourListComponent rvNextTours;
    int SortType = 0;
    int CategoryType = 0;
    int PageSize = 0;
    public static HomeCurrentTourFragment getInstance(int CategoryType, int SortType, int PageSize) {
        HomeCurrentTourFragment res = new HomeCurrentTourFragment();
        res.SortType = SortType;
        res.CategoryType = CategoryType;
        res.PageSize = PageSize;
        return res;
    }
    public HomeCurrentTourFragment(){
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {//4th Event
        super.onViewCreated(view, savedInstanceState);

        initializeComponents(view);
        int CacheDbId =TourListComponent.CacheDBId_HomeNewer;
        if (SortType == TourListComponent.SORT_Earlier)
            CacheDbId = TourListComponent.CacheDBId_HomeEarlier;
        else if (SortType == TourListComponent.SORT_Hottest)
            CacheDbId = TourListComponent.CacheDBId_HomeHottest;

        rvNextTours.readAndShow(CacheDbId, 0, CategoryType, SortType, PageSize);
    }
    @Override
    public void initializeComponents(View v) {
        rvNextTours =v.findViewById(R.id.rvNextTours);

        rvNextTours.setOnItemClickListener((post, Position) -> {
            context.showFragment(TourShowOne.getInstance(post, Position));
        });

        super.initializeComponents(v);
    }

//        Context context;
//    @Override
//    public void onAttach(Context _context) { //1st Event
//        super.onAttach(context);
//        this.context = _context;
//    }
//    @Override
//    public void onCreate(Bundle savedInstanceState) {//2nd Event
//        super.onCreate(savedInstanceState);
//    }


    //تنظیمات مربوط به صفحه --------------
    @Override
    protected int getScreenId() {return PrjConfig.frmHome_CurrentTours;}
    @Override
    protected String tag() {return SCREEN_TAG;}
    public static final String SCREEN_TAG = "Home_CurrentTours";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {//3nd Event
        return inflater.inflate(R.layout.frm_home_currenttours, parent, false);
    }
}
