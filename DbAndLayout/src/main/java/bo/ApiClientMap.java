package bo;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.security.cert.CertificateException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import utils.PrjConfig;

//1399-06-25 add timeout for getClient
public class ApiClientMap {
       public static final String BASE_URL = PrjConfig.WebApiAddress + "/api1/Nb/";

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


    //Because of this error: java.security.cert.certpathvalidatorexception : trust anchor for certification path not found
    //related note: https://stackoverflow.com/questions/6825226/trust-anchor-not-found-for-android-ssl-connection
    public static OkHttpClient.Builder getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
