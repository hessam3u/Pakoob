package bo;

import static bo.ApiClientMap.getUnsafeOkHttpClient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import utils.PrjConfig;

//1399-06-25 add timeout for getClient
public class ApiClientFcm {
    public static final String BASE_URL = PrjConfig.WebApiAddress + "/api1/Fcm/";
    private static Retrofit retrofit = null;


    public static Retrofit getClient(int timeOutInSec) {
        if (retrofit==null) {

//            FieldNamingStrategy hessamLowerCase = new FieldNamingStrategy() {
//                @Override
//                public String translateName(Field f) {
//                    String res = f.getName();
//                    return Character.toLowerCase(res.charAt(0)) + res.substring(1);
//                }
//            };
//            Gson gson = new GsonBuilder().setFieldNamingStrategy(hessamLowerCase).create();

            //baraye hale moshkele tabdile tarikh
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Date.class, new DateDeserializer());

            Gson gson = gsonBuilder.create();

//            if (timeOutInSec == 0) {
//                retrofit = new Retrofit.Builder()
//                        .baseUrl(BASE_URL)
//                        .addConverterFactory(GsonConverterFactory.create(gson))
//                        .addConverterFactory(ScalarsConverterFactory.create())
//                        .build();
//            }
//            else{
            if (timeOutInSec == 0)
                timeOutInSec = 60;

            final OkHttpClient okHttpClient = getUnsafeOkHttpClient()// new OkHttpClient.Builder() on 1400-09-03
                        .readTimeout(timeOutInSec, TimeUnit.SECONDS)
                        .connectTimeout(timeOutInSec, TimeUnit.SECONDS)
                        .build();
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .client(okHttpClient)
                        .build();
//            }
        }
        return retrofit;
    }

    //baraye hale moshkele tabdile tarikh, classe zir lazem bood
    //Kolan Hazfesh kardam va Date ro be soorate String Zakhire Mikonam
    public static class DateDeserializer implements JsonDeserializer<Date> {

        @Override
        public Date deserialize(JsonElement element, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
            String date = element.getAsString();

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            format.setTimeZone(TimeZone.getTimeZone("GMT"));

            try {
                return format.parse(date);
            } catch (ParseException exp) {
                System.err.println(exp.getMessage());
                return null;
            }
        }
    }
}
