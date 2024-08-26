package bo;

import bo.NewClasses.InsUpdRes;
import bo.entity.MobileInfoDTO;
import bo.NewClasses.SimpleRequest;
import bo.NewClasses.StringContentDTO;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiInterfaceMap {
    @POST("GenerateConfirmCode")
    Call<ResponseBody> GenerateConfirmCode(@Body SimpleRequest request);//ConfirmSms2DTO:ConfirmSms2DTO
    @POST("CheckConfirmCode")
    Call<ResponseBody> CheckConfirmCode(@Body SimpleRequest obj);//ConfirmSms2DTO:PersonalInfoDTO
    @POST("UpdatePersonalInfo")
    Call<ResponseBody> UpdatePersonalInfo(@Body SimpleRequest req);//PersonalInfoDTO.PersonalInfoDTORequest:InsUpdRes
    @POST("CheckVersion2")
    Call<InsUpdRes> CheckVersion2(@Body MobileInfoDTO info);

    @POST("Download")
    Call<ResponseBody> Download(@Body SimpleRequest request);//DownloadRequest:InsUpdRes //based on : https://futurestud.io/tutorials/retrofit-2-how-to-download-files-from-server
    @GET("Test")
    Call<String> Test();
    @POST("SearchForMap")//:SearchForMapResult
    Call<ResponseBody> SearchForMap(@Body SimpleRequest request);// //also gson to Convert a List: https://stackoverflow.com/questions/16723158/json-string-to-java-object-using-gson

    @POST("SyncMyMaps")//:SearchForMapResult
    Call<ResponseBody> SyncMyMaps(@Body SimpleRequest request);// //also gson to Convert a List: https://stackoverflow.com/questions/16723158/json-string-to-java-object-using-gson

    @POST("GetPoi")
    Call<ResponseBody> ListPoi();

    @POST("RequestBuyMap")//:SearchForMapResult
    Call<ResponseBody> RequestBuyMap(@Body SimpleRequest inp);// //also gson to Convert a List: https://stackoverflow.com/questions/16723158/json-string-to-java-object-using-gson

    @POST("SyncL")//:SearchForMapResult
    Call<ResponseBody> SyncL(@Body SimpleRequest inp);// StringContentDTO of Lat*Lon*JSONCalendar -> void

    @POST("NeedHelpIn")//:SearchForMapResult
    Call<ResponseBody> NeedHelpIn(@Body SimpleRequest inp);// StringContentDTO of ProblemCode\*\*\\Mobile -> InsUpdRes ProblemCode

    @POST("GetAdvs")//:NbAdvList
    Call<ResponseBody> GetAdvs(@Body SimpleRequest inp);// SimpleRequest:ListItemsResult<NbAdv>

    @POST("SearchNbPoi_1")
    Call<ResponseBody> SearchNbPoi_1(@Body SimpleRequest request);//SearchRequestDTO:ListItemsResult<NbPoi>, Filter: Name***LatS,LonW,LatN,LonE***PoiTypeForSearch***MyLat,MyLon***
    @POST("SearchLatLon")
    Call<ResponseBody> SearchLatLon(@Body SimpleRequest request);//SearchRequestDTO:Filter: Lat + %*% + Lon: title + %*% + elev + %*%
    @POST("ReadOneSafeGpxDataPoints")
    Call<ResponseBody> ReadOneSafeGpxDataPoints(@Body SimpleRequest request);//SearchRequestDTO (data.Filter=> Id, data.OtherCommand=> count of points or ) :InsUpdResGen<TrackDataCompact>
    @POST("GetElevationForTrackList")
    Call<ResponseBody> GetElevationForTrackList(@Body SimpleRequest request);//SearchRequestDTO (data.Filter=> lat,lon;lat,lon;..., data.OtherCommand=>ReadMidlePoints (1 true - 2 false) %;% :InsUpdResGen<TrackDataCompact>

    @POST("RequestWeatherForLoc")
    Call<ResponseBody> RequestWeatherForLoc(@Body SimpleRequest request);//SimpleRequest:NbWeather, Request Text: Lat,Lon --- or NbPoiId

    @POST("SearchSafeGpx")
    Call<ResponseBody> SearchSafeGpx(@Body SimpleRequest request);//SearchRequestDTO:ListItemsResult<NbSafeGpx>, Filter: NbPoiId***Lat***Lon***searchParse

    @POST("ReadOneSafeGpx")
    Call<ResponseBody> ReadOneSafeGpx(@Body SimpleRequest request);//SearchRequestDTO:ListItemsResult<NbSafeGpx>, Filter: SafeGpxId

    @POST("NbGpxRequestCreate")
    Call<ResponseBody> NbGpxRequestCreate(@Body SimpleRequest request);//SearchRequestDTO:ListItemsResult<NbSafeGpx>, Filter: SafeGpxId


    @POST("SyncPakoob")//:SearchForMapResult
    Call<ResponseBody> SyncPakoob(@Body SimpleRequest inp);//PakoobSync-> void


}
