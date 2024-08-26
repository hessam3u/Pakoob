package utils;

import static android.content.Context.POWER_SERVICE;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicLong;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class hutilities {

    public static byte AppId = 1;
    public static Integer VersionCode = 1;
    public static String Session = "";
    public static int CCustomerId = 0;
    public static String CurrencyName = "ریال";


    public static String getPlatform(){return "And";}
    public static int getOsVersion(){return Build.VERSION.SDK_INT;}
    public static String serialNumber = null;
    public static String getPhoneSerial(int maxLength){
        if (serialNumber != null)
            return serialNumber;

        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);

            serialNumber = (String) get.invoke(c, "gsm.sn1");
            if (serialNumber.equals(""))
                serialNumber = (String) get.invoke(c, "ril.serialnumber");
            if (serialNumber.equals(""))
                serialNumber = (String) get.invoke(c, "ro.serialno");
            if (serialNumber.equals(""))
                serialNumber = (String) get.invoke(c, "sys.serialnumber");
            if (serialNumber.equals(""))
                serialNumber = Build.SERIAL;

            // If none of the methods above worked
            if (serialNumber.equals(""))
                serialNumber = "";
        } catch (Exception e) {
            //e.printStackTrace();
            serialNumber = "";
        }
        serialNumber = serialNumber.length() > 0? serialNumber.substring(0, Math.min(maxLength, serialNumber.length()))
                : "85316543";

        return serialNumber;
    }
    public static String getDeviceModel(int maxLength){return Build.MODEL.length() > 0? Build.MODEL.substring(0, Math.min(maxLength, Build.MODEL.length())) : "";}
    public static String getManuIdiom(){return Build.MANUFACTURER.substring(0, 2) + " " + "Pho";}
    public static String getIP(){return "";}

    public static byte[] decrptBytes(byte[] recBytes){
        int size = (int) recBytes.length;
        String keyStr = (CCustomerId > 0)?Integer.toString(CCustomerId): hutilities.getPhoneSerial(12);
        int keyLen = keyStr.length();
        byte[] key = new byte[keyLen];
        for (int i = 0; i < key.length; i++) {
            key[i] = (byte) keyStr.charAt(i);
        }

        for (int i = 0; i < size; i++)
        {
            recBytes[i] ^= key[i % keyLen];
        }

        return  recBytes;
    }
    public static String decryptBytesToString(byte[] recBytes){
        try {
            recBytes = decrptBytes(recBytes);
            return new String(recBytes, "UTF8");}
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static void forceRTLIfSupported(AppCompatActivity activity)
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
            activity.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }

    public static boolean validationNationalCode(String code){
        //check length
        if (code.length() != 10)
            return false;

        long nationalCode = Long.parseLong(code);
        byte[] arrayNationalCode = new byte[10];

        //extract digits from number
        for (int i = 0; i < 10 ; i++) {
            arrayNationalCode[i] = (byte) (nationalCode % 10);
            nationalCode = nationalCode / 10;
        }

        //Checking the control digit
        int sum = 0;
        for (int i = 9; i > 0 ; i--)
            sum += arrayNationalCode[i] * (i+1);
        int temp = sum % 11;
        if (temp < 2)
            return arrayNationalCode[0] == temp;
        else
            return arrayNationalCode[0] == 11 - temp;
    }

    /**
     * Enables https connections
     */
    @SuppressLint("TrulyRandom")
    public static void handleSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        } catch (Exception ignored) {
        }
    }

    //-------------
    public static void showHideLoading(boolean show, ProgressBar loading, Activity activity){
        if (show){
            loading.setVisibility(View.VISIBLE);
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
        else{
            loading.setVisibility(View.INVISIBLE);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }


    public static boolean isServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("Service status", "Running");
                return true;
            }
        }
        Log.i ("Service status", "Not running");
        return false;
    }
    public static void startServiceIfNotRunning(Context context, Intent serviceIntent, Class<?> serviceClass){
        if (!hutilities.isServiceRunning(serviceClass.getClass(), context)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, serviceClass));
            } else {
                context.startService(new Intent(context, serviceClass));
            }
        }
    }
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;

    public static int getConnectivityStatus(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (null != activeNetwork) {

            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static String getConnectivityStatusString(Context context) {

        int conn = getConnectivityStatus(context);

        String status = null;
        if (conn == TYPE_WIFI) {
            //status = "Wifi enabled";
            status="Internet connection available";
        } else if (conn == TYPE_MOBILE) {
            //status = "Mobile data enabled";
            status="Internet connection available";
        } else if (conn == TYPE_NOT_CONNECTED) {
            status = "Not connected to Internet";
        }
        return status;
    }
    public static void hideKeyboard(Context activity, EditText textbox) {
        //hide keyboard
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(textbox.getWindowToken(), 0);
    }
    public static void hideKeyboard(Activity activity) {
        final InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isActive()) {
            if ((activity).getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow((activity).getCurrentFocus().getWindowToken(), 0);
            }
        }

    }
    public static void showKeyboard(Context activity, EditText textbox){
        textbox.requestFocus();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }


    //used for Add Margin Programatically like : vp.setMargins(0, 0, 0, px/4);
    public static int getPixelFromDP(Resources r, float dp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,r.getDisplayMetrics());
    }
    public static int getPixelFromDP(Resources r, int DimenResourceId){
        return getPixelFromDP(r, r.getDimension(DimenResourceId));
    }


    public static Bitmap textAsBitmap(String text, float textSize, int textColor, Typeface font, Context context) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(textColor);
        paint.setTextSize(textSize);
        paint.setTypeface(font);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.0f); // round
        int height = (int) (baseline + paint.descent() + 0.0f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }
    public static BitmapDescriptor bitmapDescriptorFromVectorWithBackground(Context context, @DrawableRes int vectorDrawableResourceId, @DrawableRes int vectorDrawableBackground) {
        //https://stackoverflow.com/questions/42365658/custom-marker-in-google-maps-in-android-with-vector-asset-icon
        Drawable background = ContextCompat.getDrawable(context, vectorDrawableBackground);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(40, 20, vectorDrawable.getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 20);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
    public static BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    //----------------------File Working-----------------------------
    public static boolean moveFile(String oldfilename, String newFolderPath, String newFilename) {
        File folder = new File(newFolderPath);
        if (!folder.exists())
            folder.mkdirs();

        File oldfile = new File(oldfilename);
        File newFile = new File(newFolderPath, newFilename);

        if (!newFile.exists())
            try {
                newFile.createNewFile();
            } catch (IOException e) {
                // Auto-generated catch block
                e.printStackTrace();
                if (newFile.exists())
                    newFile.delete();
            }
        return oldfile.renameTo(newFile);
    }

    public static long getFolderSize(String address) {
        File file = new File(address);
        if (!file.exists())
            return 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final AtomicLong size = new AtomicLong(0);
            try {
                Path path =file.toPath();
                Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {

                        size.addAndGet(attrs.size());
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) {

                        System.out.println("skipped: " + file + " (" + exc + ")");
                        // Skip folders that can't be traversed
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) {

                        if (exc != null)
                            System.out.println("had trouble traversing: " + dir + " (" + exc + ")");
                        // Ignore errors traversing a folder
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                return getFolderSizeRec(file);
            }
            return size.get();
        }
        else{
            return getFolderSizeRec(file);
        }
    }
    private static long getFolderSizeRec(File dir) {
        long size = 0;
        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                System.out.println(file.getName() + " " + file.length());
                size += file.length();
            }
            else
                size += getFolderSizeRec(file);
        }
        return size;
    }
    public static String getStringSizeLengthFile(long size) {

        DecimalFormat df = new DecimalFormat("0.00");

        float sizeKb = 1024.0f;
        float sizeMb = sizeKb * sizeKb;
        float sizeGb = sizeMb * sizeKb;
        float sizeTerra = sizeGb * sizeKb;

        if (size == 0)
            return df.format(0)+ "b";
        else if(size < sizeMb)
            return df.format(size / sizeKb)+ "Kb";
        else if(size < sizeGb)
            return df.format(size / sizeMb) + "Mb";
        else if(size < sizeTerra)
            return df.format(size / sizeGb) + "Gb";

        return "";
    }
    public static void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }
    //----------------------End File Working-----------------------------
    public static float parseFloatPersian(String value){
        if (value.equals(""))
            return 0;
        char ch = value.charAt(0);
        if (ch >='0' && ch <='9')
            return Float.parseFloat(value);
        float result = 0;
        float dec10 = 10;
        boolean seenPoint = false;
        int length = value.length();
        for (int i = 0; i < length; i++) {
            char cc = value.charAt(i);
            if (cc == '.' || cc == '/'){
                seenPoint = true;
                continue;
            }
            byte val = charToNumberPersian(cc);
            if (!seenPoint){
                result = result * 10 + val;
            }
            else{
                result = result + (val / dec10);
                dec10 *= 10;
            }
        }
        return result;
    }
    public static byte charToNumberPersian(char ch){
//        if (ch >= '0' && ch <='9')
//            return (byte)(ch - '0');
        byte val = 0;
        if (ch >='٠' && ch <='٩'){
            val = (byte)(ch - '٠');
        }
        else if (ch >='۰' && ch <='۹'){
            val = (byte)(ch - '۰');
        }
        else if (ch >='0' && ch <='9'){
            val = (byte)(ch - '0');
        }
        return val;
    }
    public static void copyFile(File src, File dst) throws IOException {
        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }


    //for Opening file via INTENT FILTER
    public static String getContentName(ContentResolver resolver, Uri uri){
        Cursor cursor = resolver.query(uri, null, null, null, null);
        cursor.moveToFirst();
        int nameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
        if (nameIndex >= 0) {
            return cursor.getString(nameIndex);
        } else {
            return null;
        }
    }
    public static String getContentFullPath(ContentResolver resolver, Uri uri){
        Cursor cursor = resolver.query(uri, null, null, null, null);
        cursor.moveToFirst();
        int nameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
        if (nameIndex >= 0) {
            return cursor.getString(nameIndex);
        } else {
            return null;
        }
    }


    public static boolean isNotificationChannelEnabled(Context context, @Nullable String channelId){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(!TextUtils.isEmpty(channelId)) {
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationChannel channel = manager.getNotificationChannel(channelId);
                return channel.getImportance() != NotificationManager.IMPORTANCE_NONE;
            }
            return false;
        } else {
            return NotificationManagerCompat.from(context).areNotificationsEnabled();
        }
    }

    public static void showAppSettingToChangePermission(Context context){
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(uri);
        context.startActivity(intent);
    }
    public static void showSettingTpAccessLocation(Context context){
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    public static boolean isInternetConnected(Context ctx) {
        ConnectivityManager connectivityMgr = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivityMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        // Check if wifi or mobile network is available or not. If any of them is
        // available or connected then it will return true, otherwise false;
        if (wifi != null) {
            if (wifi.isConnected()) {
                return true;
            }
        }
        if (mobile != null) {
            if (mobile.isConnected()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isLowMemoryDevice(Context context) {
        if(Build.VERSION.SDK_INT >= 19) {
            return ((ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE)).isLowRamDevice();
        } else {
            return false;
        }
    }
    /**
     * Check if correct Play Services version is available on the device.
     */
    private static byte googlePlayIsActive = 0;
    public static boolean checkGooglePlayServiceAvailability(Context context) {
//        if (1 ==1)
//            return false;

        if (googlePlayIsActive != 0)
            return googlePlayIsActive == 1;
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(context);
        if(result != ConnectionResult.SUCCESS) {
            googlePlayIsActive = 2;
            return false;
        }
        googlePlayIsActive = 1;
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarColor(int color, boolean isWhiteFamily, Activity activity){
        Window window = activity.getWindow();
        //window.setDecorFitsSystemWindows(isWhiteFamily);
// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(color);
    }

    public static void OpenShareBox(String title, String content, String ShareVia_Text, Context context){
        Intent i=new Intent(android.content.Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(android.content.Intent.EXTRA_SUBJECT,title);
        i.putExtra(android.content.Intent.EXTRA_TEXT, content);
        context.startActivity(Intent.createChooser(i,ShareVia_Text));
    }

    public static final String FileNameReservedChars = "|\\?*<\":>+[]/'";
    public static String ReplaceInvalidFileChars(String source, String replaceWith){
        //Tested with https://regex101.com/ at 1401-02-05
        return source.replaceAll("[^ء-ی^a-zA-Z0-9\\.\\-]", replaceWith);
    }
    public static boolean requestIgnoreBatteryOptimizations(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager pm = (PowerManager) context.getSystemService(POWER_SERVICE);
            String packageName = context.getPackageName();
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Add this flag
                context.startActivity(intent);
                return false;
            }
        }
        return true;
    }
}
