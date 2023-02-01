package bo;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;


import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import HMultiSelect.MultiSelectModel;
import bo.entity.CityDTO;
import bo.NewClasses.SimpleRequest;
import bo.NewClasses.StringContentDTO;
import bo.entity.TTAllFilters;
import bo.entity.TTClubNameDTO;
import bo.entity.TTClubTour;
import bo.entity.TTClubTourCategoryDTO;
import bo.sqlite.AppDatabaseTara;
import okhttp3.ResponseBody;
import pakoob.sessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.MyDate;
import utils.hutilities;

public class dbConstantsTara {
    public static AppDatabaseTara appDB;

    public static String key = "af391-dLDLKJ2da-39#*20D)@dlcnLDp";
    public static Calendar LastToursRead;
    public static String actionBarColor = "000000";//lightblue 200 az https://material.io/design/color/the-color-system.html#tools-for-picking-colors
    public static String CurrencyName = "ریال";
    public static sessionManager session;

    private static List<TTClubTour> tours;
    private static List<TTClubTour> my_club_tours;
    //    public static TTAllFilters allFilters;
    public static List<CityDTO> cities;
    public static List<TTClubTourCategoryDTO> categories;
    public static List<TTClubNameDTO> clubNames;
    public static ApiInterfaceTara apiTara = ApiClientTara.getClient(60).create(ApiInterfaceTara.class);

    public static final byte ViewCounterType_Club = 1;
    public static final byte ViewCounterType_Tour = 2;
    public static final byte ViewCounterType_Person = 3;
    public static final byte ViewCounterType_SafeGpx = 4;

    public static final String IamNotFan_Char = "\uE816";
    public static final String IamFan_Char = "\uE815";
    public static final String BellOn_Char = "\uE833";
    public static final String BellOff_Char = "\uF1F7";
    public static final int IamFan_Color = Color.parseColor("#F8BF13");
    public static final int IamNotFan_Color = Color.GRAY;
    public static final int Follow_TextColor = Color.WHITE;
    public static final int Following_TextColor = Color.BLACK;
    public static final String BellOn = "\uE833";
    public static final String BellOff = "\uF1F7";


    public static void initValues(Context context, boolean BuildConfig_DEBUG){

        session = new sessionManager(context);

        appDB = AppDatabaseTara.getDatabase(context);//Room.databaseBuilder(getApplicationContext(), AppDatabase.class, AppDatabase.DATABASE_NAME).allowMainThreadQueries().build();


//        ArrayList<Integer> ms = new ArrayList<Integer>();
//session.setMyClubNameIds(ms);
        //تنظیمات مربوط به پیکاسو
        Picasso.Builder picassoBuilder = new Picasso.Builder(context);
        picassoBuilder.downloader(new OkHttp3Downloader(context));
        if(BuildConfig_DEBUG) { picassoBuilder.loggingEnabled(true); }
        if(hutilities.isLowMemoryDevice(context)) { picassoBuilder.defaultBitmapConfig(Bitmap.Config.RGB_565); }
        Picasso.setSingletonInstance(picassoBuilder.build());
    }


    public static boolean loadingFilters = false;
    public static boolean filtersInited = false;

    public static void initFiltersAsync(){
        if (loadingFilters || filtersInited)
            return;

        loadingFilters = true;
        Call<TTAllFilters> call = dbConstantsTara.apiTara.GetTTAllFilters(dbConstantsTara.session.getLastFilterUpdate());
        call.enqueue(new Callback<TTAllFilters>() {
            @Override
            public void onResponse(Call<TTAllFilters> call, Response<TTAllFilters> response) {
                if (response.code() == 200) {
                    TTAllFilters recFilters = response.body();
                    for (int i = 0; i < recFilters.city.size(); i++) {
                        dbConstantsTara.appDB.CityDao().insert(recFilters.city.get(i));
                    }
                    for (int i = 0; i < recFilters.category.size(); i++) {
                        dbConstantsTara.appDB.TTClubTourCategoryDao().insert(recFilters.category.get(i));
                    }
//                    for (int i = 0; i < recFilters.clubNames.size(); i++) {
//                        dbConstantsTara.appDB.TTClubNameDao().insert(recFilters.clubNames.get(i));
//                    }
                    dbConstantsTara.session.setLastFilterUpdate(recFilters.LastFilterUpdate);
                }
                readFiltersFromDB();
                loadingFilters = false;
                filtersInited = true;
            }

            @Override
            public void onFailure(Call<TTAllFilters> call, Throwable t) {
                readFiltersFromDB();
                loadingFilters = false;
//                progressDoalog.dismiss();
                //Toast.makeText(getActivity(), "یه جای کار می لنگه، لطفا دوباره تلاش کنید...!", Toast.LENGTH_SHORT).show();//bayad TourList.this bejaye getActivity() mibood

            }
        });
    }
    public static void readFiltersFromDB(){
        //dbConstantsTara.cities = dbConstantsTara.appDB.CityDao().selectAllActives();
        //dbConstantsTara.clubNames = dbConstantsTara.appDB.TTClubNameDao().selectAllActives();
        dbConstantsTara.categories = dbConstantsTara.appDB.TTClubTourCategoryDao().selectAllActives();
    }




    public static List<TTClubTour> getTours(){
        //برای جلوگیری از خطاها در هنگامی که اینترنت قطع هست و تورها دریافت نشده اند
        if (tours == null)
            tours = new ArrayList<>();
        return tours;
    }
    public static void setTours(List<TTClubTour> value){tours = value;LastToursRead = Calendar.getInstance();}
    public static List<TTClubTour> getMy_club_tours(){
        //برای جلوگیری از خطاها در هنگامی که اینترنت قطع هست و تورها دریافت نشده اند
        if (my_club_tours == null)
            my_club_tours = new ArrayList<>();
        return my_club_tours;
    }
    public static void setMy_club_tours(List<TTClubTour> value){my_club_tours = value;}
    // Called by the system when the device configuration changes while your component is running.
    // Overriding this method is totally optional!
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//    }
//
//    @Override
//    public void onLowMemory() {
//        super.onLowMemory();
//    }
    public static ArrayList<MultiSelectModel> getCitiesForMutliSelect(){
        ArrayList<MultiSelectModel> res = new ArrayList<MultiSelectModel>();

        for (int i = 0;i < cities.size();i++) {
            res.add(new MultiSelectModel(cities.get(i).CityId, cities.get(i).Name));
        }
        return res;
    }
    public static ArrayList<MultiSelectModel> getCategoriesForMutliSelect(){
        ArrayList<MultiSelectModel> res = new ArrayList<MultiSelectModel>();

        for (int i = 0;i < categories.size();i++) {
            res.add(new MultiSelectModel(categories.get(i).TTClubTourCategoryId, categories.get(i).Title));
        }
        return res;
    }
    public static ArrayList<MultiSelectModel> getClubNamesForMutliSelect(){
        ArrayList<MultiSelectModel> res = new ArrayList<MultiSelectModel>();

        for (int i = 0;i < clubNames.size();i++) {
            res.add(new MultiSelectModel(clubNames.get(i).TTClubNameId, clubNames.get(i).Name));
        }
        return res;
    }
    public static ArrayList<MultiSelectModel> getTourLengthForMutliSelect(){
        ArrayList<MultiSelectModel> res = new ArrayList<MultiSelectModel>();
        res.add(new MultiSelectModel(1, "کمتر از یک روز"));
        res.add(new MultiSelectModel(2, "یک و یک و نیم روزه"));
        res.add(new MultiSelectModel(3, "چند روزه"));
        return res;
    }


    public static boolean DoSyncV(long Id, byte ViewCounterType, Calendar ViewDate, Context context){
        try {
            if (!hutilities.isInternetConnected(context))
                return false;

            String content = String.valueOf(Id) + "*" + String.valueOf(ViewCounterType) + "*" + MyDate.CalendarToCSharpDateTimeAcceptable(ViewDate) + "*" + "1";

            SimpleRequest request = SimpleRequest.getInstance(StringContentDTO.getInstance(content));

            Call<ResponseBody> call = dbConstantsTara.apiTara.SyncV(request);
            call.enqueue(new Callback<ResponseBody>(){
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response){
                    //if (!isAdded()) return;
                    try {
                        if (response.isSuccessful()) {
//                            InsUpdRes result = InsUpdRes.fromBytes(response.body().bytes());
//                            if (result.isOk)
//                                //llalalal
                        }
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    //if (!isAdded()) return;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

}
