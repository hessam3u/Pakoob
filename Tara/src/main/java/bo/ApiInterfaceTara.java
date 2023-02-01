package bo;

import java.util.List;

import bo.NewClasses.SimpleRequest;
import bo.entity.TTAllFilters;
import bo.entity.TTClubTour;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterfaceTara {

//    @GET("GetTourList")
//    Call<List<TTClubTour>> GetTourList(@Query("ClubNameIds") String ClubNameIds, @Query("CategoryIds") String CategoryIds
//            , @Query("TourLengthIds") String TourLengthIds, @Query("CityIds") String CityIds
//            , @Query("Sort") String Sort
//            , @Query("FromItemIndex") String FromItemIndex, @Query("PageSize") String PageSize, @Query("Key") String Key);
    @POST("GetTourList")
    Call<ResponseBody> GetTourList(@Body SimpleRequest request);//GetTourListDTO-> TourListResult
    @POST("GetNewTours")
    Call<List<TTClubTour>> GetNewTours(@Body String lastRead);
    //Merged so Commented:
    //    @POST("CheckVersion")
//    Call<String> CheckVersion(@Body MobileInfoDTO info);
    @GET("GetTTAllFilters")
    Call<TTAllFilters> GetTTAllFilters(@Query("LastFilterUpdate") String LastFilterUpdate);

    @POST("GetClubNamesWithFilter")
    Call<ResponseBody> GetClubNamesWithFilter(@Body SimpleRequest request);//SearchRequestDTO:ListItemsResult<TTClubNameDTO>, Filter: ClubName***CityId***

    @POST("DoChangeNotificationsOfClub")
    Call<ResponseBody> DoChangeNotificationsOfClub(@Body SimpleRequest request);//StringContentDTO format: int TTCLubNameId***byte nNotificationStatus -> InsUpdRes
    @POST("DoFollowClub")
    Call<ResponseBody> DoFollowClub(@Body SimpleRequest request);//StringContentDTO format: int TTCLubNameId***byte nFollowStatus -> InsUpdRes
    @POST("DoMakeFanOfClub")
    Call<ResponseBody> DoMakeFanOfClub(@Body SimpleRequest request);//StringContentDTO format: int TTCLubNameId***byte nFanStatus -> InsUpdRes
    @POST("DoSelectMainClub")
    Call<ResponseBody> DoSelectMainClub(@Body SimpleRequest request);//StringContentDTO format: int TTCLubNameId***byte nSelectStatus -> InsUpdRes

    @POST("GetClubTour")
    Call<ResponseBody> GetClubTour(@Body SimpleRequest request);//StringContentDTO format: int ClubTourId -> TourListResult
    @POST("GetTTClubName")
    Call<ResponseBody> GetTTClubName(@Body SimpleRequest request);//StringContentDTO format: int ClubNameId -> ListItemsResult<TTClubName>

    @POST("SyncV")//:SearchForMapResult
    Call<ResponseBody> SyncV(@Body SimpleRequest inp);//StringContentDTO of Id*ViewCounterType*CSDate*Platform -> void


    @POST("GetCitiesByFilter")
    Call<ResponseBody> GetCitiesByFilter(@Body SimpleRequest request);//SearchRequestDTO:ListItemsResult<City>, Filter: CityName***ProvinceId***


}
