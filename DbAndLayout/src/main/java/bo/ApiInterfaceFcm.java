package bo;

import bo.NewClasses.SimpleRequest;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiInterfaceFcm {
    @POST("UpdateFirebaseToken")
    Call<ResponseBody> UpdateFirebaseToken(@Body SimpleRequest req);//MobileInfoDTO.FBTokenUpdateRequest:InsUpdRes

    @POST("GetSides")
    Call<ResponseBody> GetSides(@Body SimpleRequest request);//SearchRequestDTO-> FmSidesList

    @POST("GetMessages")
    Call<ResponseBody> GetMessages(@Body SimpleRequest request);//SearchRequestDTO-> FmSidesList

    @POST("MakeReadMessages")
    Call<ResponseBody> MakeReadMessages(@Body SimpleRequest request);//StringContentDTO format: senderId + "***" + recType+ "***" +anonymosType -> InsUpdRes

    @POST("SendFromClient")
    Call<ResponseBody> SendFromClient(@Body SimpleRequest request);//FmMessage ->InsUpdRes






    //Shared Between Projects
    @POST("SyncExceptions")
    Call<ResponseBody> SyncExceptions(@Body SimpleRequest request);//StringContentDTO of List<TTExceptionLog> -> InsUpdRes


}
