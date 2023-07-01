package maptools;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.GeomagneticField;
import android.telecom.Call;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.app.AlertDialog;
import bo.entity.NbMap;
import bo.sqlite.TTExceptionLogSQLite;
import hmapscaleview.MapScaleModel;
import mojafarin.pakoob.R;
import mojafarin.pakoob.app;
import utils.PrjConfig;
import utils.hutilities;
import utils.projectStatics;

public class hMapTools {
    final static String TAG = "سینک_مپ";//1401-07-30

    public final static String tempFolder = "temp";
    public final static String downloadFolder = "download";
    public final static String mapsFolder = "maps";
    public final static String tilesFolder = "tiles";
    public final static String gpxFolder = "gpx";
    public final static byte NORTH_TRUE = 1;
    public final static byte NORTH_MAG = 2;
    public final static byte DEFAULT_NORTH = NORTH_MAG;


    public final static int CustomMapMinZoom = 8;
    public final static int CustomMapMaxZoomNormal = 16;
    public final static int CustomMapMaxZoomAvailable = 17;


    public static boolean EncryptFile(String orginalFileAddress, String path, int CCustomerId)
    {
        File file = new File(orginalFileAddress);
        int size = (int) file.length();
        byte[] bts = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bts, 0, bts.length);
            buf.close();
        } catch (FileNotFoundException e) {
            // Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            // Auto-generated catch block
            e.printStackTrace();
            return false;
        }

        String keyStr = Integer.toString(CCustomerId);
        int keyLen = keyStr.length();
        byte[] key = new byte[keyLen];
        for (int i = 0; i < key.length; i++) {
            key[i] = (byte)( keyStr.charAt(i) - '0');
        }

        for (int i = 0; i < size; i++)
        {
            bts[i] ^= key[i % keyLen];
        }

        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path));
            bos.write(bts);
            bos.flush();
            bos.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;

        } catch (IOException e) {
            e.printStackTrace();
            return false;

        }
        return  true;
    }

    public static String decryptToTemp(String source, Context context, int CCustomerId){
        return decryptToTemp(source, "", context, CCustomerId);
    }
    public static String decryptToTemp(String source, String fileNameAndExt, Context context, int CCustomerId) {
        String tempDirectoryName = context.getFilesDir() + File.separator + tempFolder + File.separator;
        String filename=fileNameAndExt.isEmpty()?source.substring(source.lastIndexOf("/")+1):fileNameAndExt;
        File tempDirectory = new File(tempDirectoryName);
        if (! tempDirectory.exists()){
            tempDirectory.mkdirs();
        }
        String outputAddress = tempDirectoryName + filename;
        boolean encRes = EncryptFile(source, outputAddress, CCustomerId);
        if (!encRes)
            return "";
        return outputAddress;
    }
    public static boolean craeteTilesAtManyZoom(String source, Context context, List<LatLng> bounds, int fromZoom, int toZoom){
//                String resIdStr = "m78621se_mashhad6s2";//m78621se_mashhad6s2    m78624se_kang_s2   m79624sw_mashhad5
//        int resId = context.getResources().getIdentifier(resIdStr, "drawable", context.getPackageName());
//        List<LatLng> bounds = PersianMapIndex25000.stringToBoundsList(resIdStr);
//        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), resId);

        Bitmap bm = BitmapFactory.decodeFile(source);
        try {
            for (int i = fromZoom; i <= toZoom; i++) {
                MapTile.CreateTilesForZoom( bm, hMapTools.tilesFolder, i, bounds, context );
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public static boolean deleteTilesAtManyZoom(Context context, NbMap item, int fromZoom, int toZoom, boolean DeleteSourceFile){
        int DeleteStep = 100;
        List<LatLng> bounds = item.getBounds();
        try {
            DeleteStep = 200;
            for (int i = fromZoom; i <= toZoom; i++) {
                MapTile.deleteTilesForZoom(hMapTools.tilesFolder, i, bounds, context);
            }
            DeleteStep = 300;
            if (DeleteSourceFile) {
                DeleteStep = 400;
                File file = new File(item.LocalFileAddress);
                if (file.exists())
                    file.delete();
                else{
                    Log.e(TAG, "Source File Not Found...");
                }
                DeleteStep = 450;
            }
            DeleteStep = 500;

        } catch (FileNotFoundException ex) {
            Log.e(TAG, ex.getMessage());
            ex.printStackTrace();
            TTExceptionLogSQLite.insert(ex.getMessage(), "ds is : " + DeleteStep + "_"+ ex.getStackTrace(), PrjConfig.app, 101);
            return false;
        }
        return true;
    }
    public static String getFileNameAtMapsFolder(String NCCIndex, Context context){
        String downloadDirectoryName = context.getFilesDir() + File.separator + mapsFolder +  File.separator;
        String fileName = NCCIndex + ".jpg";
        return downloadDirectoryName + File.separator + fileName;
    }
    public static String createAndRetTempDownloadFolder(Context context){
        String tempDirectoryName = context.getFilesDir() + File.separator + tempFolder + File.separator;
        File tempDirectory = new File(tempDirectoryName);
        if (! tempDirectory.exists()){
            tempDirectory.mkdirs();
        }
        tempDirectoryName = tempDirectoryName + downloadFolder + File.separator;
        tempDirectory = new File(tempDirectoryName);
        if (! tempDirectory.exists()){
            tempDirectory.mkdirs();
        }
        return tempDirectoryName;
    }
    public static String createAndRetMapsFolder(Context context){
        String tempDirectoryName = context.getFilesDir() + File.separator + mapsFolder + File.separator;
        File tempDirectory = new File(tempDirectoryName);
        if (! tempDirectory.exists()){
            tempDirectory.mkdirs();
        }
        return tempDirectoryName;
    }



    private static final CharSequence[] MAP_TYPE_ITEMS = {"راه ها", "ماهواره", "عوارض", "ماهواره و راه"};

    public static void showMapTypeSelectorDialog(GoogleMap map, Context thisContext) {
        // Prepare the dialog by setting up a Builder.
        final String fDialogTitle = "نوع نقشه پایه را انتخاب کنید";
        AlertDialog.Builder builder = new AlertDialog.Builder(thisContext, R.style.MaterialThemeDialog);
        builder.setTitle(fDialogTitle);

        // Find the current map type to pre-check the item representing the current state.
        int checkItem = map.getMapType() - 1;

        // Add an OnClickListener to the dialog, so that the selection will be handled.
        builder.setSingleChoiceItems(
                MAP_TYPE_ITEMS,
                checkItem,
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int item) {
                        // Locally create a finalised object.

                        // Perform an action depending on which item was selected.
                        switch (item) {
                            case 1:
                                map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                                break;
                            case 2:
                                map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                                break;
                            case 3:
                                map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                                break;
                            default:
                                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        }
                        app.session.setLastMapType(map.getMapType());
                        dialog.dismiss();
                    }
                }
        );

        // Build the dialog and show it.
        AlertDialog fMapTypeDialog = builder.create();
        fMapTypeDialog.setCanceledOnTouchOutside(true);
        fMapTypeDialog.show();
    }

    public static long getTileFolderSize(Context context){
        String path = context.getFilesDir() + File.separator + hMapTools.tilesFolder;
        return hutilities.getFolderSize(path);
    }
    public static long getMapsFolderSize(Context context){
        String path = context.getFilesDir() + File.separator + hMapTools.mapsFolder;
        return hutilities.getFolderSize(path);
    }
    public static long getHighZoomSize(Context context){
        String path1 = context.getFilesDir() + File.separator + hMapTools.tilesFolder + File.separator + "16";
        String path2 = context.getFilesDir() + File.separator + hMapTools.tilesFolder + File.separator + "17";
        String path3 = context.getFilesDir() + File.separator + hMapTools.tilesFolder + File.separator + "18";
        String path4 = context.getFilesDir() + File.separator + hMapTools.tilesFolder + File.separator + "19";

        return hutilities.getFolderSize(path1) + hutilities.getFolderSize(path2) + hutilities.getFolderSize(path3) + hutilities.getFolderSize(path4);
    }
    public static void deleteTilesFolder(Context context){
        String path = context.getFilesDir() + File.separator + hMapTools.tilesFolder;
        File file = new File(path);
        if (!file.exists())
            return;
        hutilities.deleteRecursive(file);
    }
    public static void deleteMapsFolder(Context context){
        String path = context.getFilesDir() + File.separator + hMapTools.mapsFolder;
        File file = new File(path);
        if (!file.exists())
            return;
        hutilities.deleteRecursive(file);
    }
    public static void deleteHighZoomFolder(Context context){
        String path1 = context.getFilesDir() + File.separator + hMapTools.tilesFolder + File.separator + "16";
        String path2 = context.getFilesDir() + File.separator + hMapTools.tilesFolder + File.separator + "17";
        String path3 = context.getFilesDir() + File.separator + hMapTools.tilesFolder + File.separator + "18";
        String path4 = context.getFilesDir() + File.separator + hMapTools.tilesFolder + File.separator + "19";

        File file1 = new File(path1);
        if (!file1.exists())
            return;
        hutilities.deleteRecursive(file1);
        File file2 = new File(path2);
        if (!file2.exists())
            return;
        hutilities.deleteRecursive(file2);
        File file3 = new File(path3);
        if (!file3.exists())
            return;
        hutilities.deleteRecursive(file3);
        File file4 = new File(path4);
        if (!file3.exists())
            return;
        hutilities.deleteRecursive(file4);
    }


    public static double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
//        double km = valueResult / 1;
//        DecimalFormat newFormat = new DecimalFormat("####");
//        int kmInDec = Integer.valueOf(newFormat.format(km));
//        double meter = valueResult % 1000;
//        int meterInDec = Integer.valueOf(newFormat.format(meter));
//        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
//                + " Meter   " + meterInDec);

        return valueResult;
    }

    public static final int DegreesMinuteSeconds = 1;
    public static final int DegreesDecimalMinutes = 2;
    public static final int DecimalDegrees = 3;
    public static final int UTM = 4;
    public static final int DecimalDegrees_JustNumber = 5;
    public static final int DEFAULT_POSITION_FORMAT = UTM;

    public enum LocationToStringStyle{
        Inline,
        Twoline,
        InLineSmall,
        TwoLineSmall,
    }

    public static String LocationToString(LatLng location, int destLocation, LocationToStringStyle style){
        String res= "";
        if (destLocation == hMapTools.UTM){
            Deg2UTM utm = new Deg2UTM(location.latitude, location.longitude, app.DegreePrecision, app.UtmPrecision);
            if (style == LocationToStringStyle.Inline || style == LocationToStringStyle.InLineSmall){
                return utm.Zone + Character.toString(utm.Letter) + " " + (int)utm.Easting + " " + (int)utm.Northing;
            }
            else if (style == LocationToStringStyle.Twoline || style == LocationToStringStyle.TwoLineSmall){
                return utm.Zone + Character.toString(utm.Letter) + " " + (int)utm.Easting + (style == LocationToStringStyle.Twoline?"mE":"")
                        + "\n" + (int)utm.Northing+ (style == LocationToStringStyle.Twoline?"mN":"");
            }
        } else if (destLocation == hMapTools.DecimalDegrees){
            return (location.latitude > 0? "N":"S") + ((int)(location.latitude * app.DegreePrecisionTen)/app.DegreePrecisionTen) + (style == LocationToStringStyle.InLineSmall || style == LocationToStringStyle.TwoLineSmall?"":"°")
                    + (style == LocationToStringStyle.Twoline || style == LocationToStringStyle.TwoLineSmall ? "\n":" ")
                    + (location.longitude > 0? "E":"W") + ((int)(location.longitude * app.DegreePrecisionTen)/app.DegreePrecisionTen) + (style == LocationToStringStyle.InLineSmall || style == LocationToStringStyle.TwoLineSmall?"":"°");
        } else if (destLocation == hMapTools.DecimalDegrees_JustNumber){
            return String.format(Locale.ENGLISH,"%.05f", location.latitude) + "," + String.format(Locale.ENGLISH, "%.05f", location.longitude);
        } else if(destLocation == hMapTools.DegreesDecimalMinutes ){
            double absolute_lat = Math.abs(location.latitude);
            int degrees_lat = (int)Math.floor(absolute_lat);
            //double minutesNotTruncated_lat = (int)((absolute_lat - degrees_lat) * 60 * 10000) / 10000d;
            double absolute_lon = Math.abs(location.longitude);
            int degrees_lon = (int)Math.floor(absolute_lon);

            return (location.latitude > 0? "N":"S") + degrees_lat + "°" + ((int)((absolute_lat - degrees_lat) * 60 * 10000) / 10000d)+ "'"
                    + (style == LocationToStringStyle.Twoline || style == LocationToStringStyle.TwoLineSmall ? "\n":" ")
                    + (location.longitude > 0? "E":"W") + degrees_lon + "°" + ((int)((absolute_lon - degrees_lon) * 60 * 10000) / 10000d)+ "'";
        }
        else if (destLocation == hMapTools.DegreesMinuteSeconds){
            double absolute_lat = Math.abs(location.latitude);
            int degrees_lat = (int)Math.floor(absolute_lat);
            double minutesNotTruncated_lat = (absolute_lat - degrees_lat) * 60;
            int minutes_lat = (int)Math.floor(minutesNotTruncated_lat);

            double absolute_lon = Math.abs(location.longitude);
            int degrees_lon = (int)Math.floor(absolute_lon);
            double minutesNotTruncated_lon = (absolute_lon - degrees_lon) * 60;
            int minutes_lon = (int)Math.floor(minutesNotTruncated_lon);

            return (location.latitude > 0? "N":"S") + degrees_lat + "°" + minutes_lat + "'" + (((int)(Math.floor((minutesNotTruncated_lat - minutes_lat) * 60)) * 100000) / 100000d) + "\""
                    + (style == LocationToStringStyle.Twoline || style == LocationToStringStyle.TwoLineSmall ? "\n":" ")
                    + (location.longitude > 0? "E":"W") + degrees_lon + "°" + minutes_lon + "'" +  (((int)(Math.floor((minutesNotTruncated_lon - minutes_lon) * 60)) * 100000) / 100000d) + "\"";
        }
        return res;
    }
    public static double distanceBetweenMeteres(double lat1, double long1, double lat2, double long2) {
        //Important Condition!! Equality
        if (lat1 == lat2 && long1 == long2)
            return 0;

        double PI_RAD = Math.PI / 180.0;
        double phi1 = lat1 * PI_RAD;
        double phi2 = lat2 * PI_RAD;
        double lam1 = long1 * PI_RAD;
        double lam2 = long2 * PI_RAD;

        double res =6371010 * Math.acos(Math.sin(phi1) * Math.sin(phi2) + Math.cos(phi1) * Math.cos(phi2) * Math.cos(lam2 - lam1));
        return res;
    }
    public static String distanceBetweenFriendly(double lat1, double lon1, double lat2, double lon2){
        return distanceBetweenFriendly(distanceBetweenMeteres(lat1, lon1, lat2, lon2));
    }
    public static String distanceBetweenFriendly(double dist){
        int distInt = (int)dist;
        if (distInt < 100000)
            return distInt + "m";
        return distInt / 1000 + "km";
    }

    public static String distanceBetweenFriendlyInKm(double dist){
        int distInt = (int)dist;
        int mtrs = distInt % 1000 / 100;
        return (distInt / 1000) + (mtrs > 0? "." + mtrs:"")+ "km";
    }
    public static double calculateArea(List<LatLng> latLngs) {
        double area = 0.0;
        int n = latLngs.size();
        for (int i = 0; i < n; i++) {
            int j = (i + 1) % n;
            LatLng current_i = latLngs.get(i);
            LatLng current_j = latLngs.get(j);
//            area += Math.toRadians(longitudes.get(j) - longitudes.get(i)) * (2 + Math.sin(Math.toRadians(latitudes.get(i))) + Math.sin(Math.toRadians(latitudes.get(j))));
            area += Math.toRadians(current_j.longitude - current_i.longitude) * (2 + Math.sin(Math.toRadians(current_i.latitude)) + Math.sin(Math.toRadians(current_j.latitude)));
        }
        area *= 6378137.0 * 6378137.0 / 2.0;
        return Math.abs(area);
    }
    public static String areaFriendly(double area){
        if (area < 10000){
            int areaInt = (int)area;
            return Integer.toString(areaInt) + " m2";
        }
        else{
            double nArea = area * 0.0001;
            int areaInt = (int)(nArea);
            int khoorde = ((int)((nArea - areaInt)*10) % 10);
            return Integer.toString(areaInt) + (khoorde > 0? "." + khoorde:"") + " ha";
        }
    }
    public static final int TIME_FRIENDLY_MODE_LONG_EXACT = 1;
    public static final int TIME_FRIENDLY_MODE_SMALL = 2;
    public static String timeBetweenFriendly(long miliStart, long miliEnd, final int compactMode){
        long diff = (miliEnd - miliStart) / 1000;
        return timeFriendly(diff, compactMode);
    }
    public static String timeFriendly(long diff, final int compactMode){
        if (compactMode == TIME_FRIENDLY_MODE_SMALL) {
            if (diff < 100)
                return String.valueOf(diff) + "s";
            if (diff < 3600) {
                return String.valueOf(diff / 60) + "'" + String.valueOf(diff % 60) + "s";
            } else if (diff < 86400) {
                long hour = diff / 3600;
                return String.valueOf(hour) + "h" + String.valueOf((diff - hour * 3600) / 60) + "'" + String.valueOf(diff % 60) + "s";
            }
            return String.valueOf(diff / 86400) + "d";
        } else if (compactMode == TIME_FRIENDLY_MODE_LONG_EXACT) {
            long days = diff / 86400;
            diff = diff - (days * 86400);
            long seconds = diff % 60;
            long hour = diff / 3600;
            long minutes = (diff - hour * 3600) / 60;

            String res = (days > 0? days + "d ":"")
                    + (hour < 10?"0":"") + hour
                    + ":"
                    + (minutes < 10?"0":"") + minutes
                    + ":"
                    + (seconds < 10?"0":"") + seconds;
            return res;
        }
        return "";
    }
    public static String GetSpeedFriendlyKmPh(double mps){
        //added 1401-06-01 به خاطر باگ عجیب سرعت توی هواپیما
        if (mps > 10000)
            mps = 10000;
        double res = (3.6 * mps);
        res = (long)(res * 10);
        return String.valueOf(res / 10);
    }

    public static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    public static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public static double GetAzimuthInDegree(LatLng source, LatLng destination)
    {
//        double longitudinalDifference = destination.longitude - source.longitude;
//        double latitudinalDifference = destination.latitude - source.latitude;
//        double azimuth = (Math.PI * .5d) - Math.atan(latitudinalDifference / longitudinalDifference);
//        if (longitudinalDifference > 0)
//            return rad2deg(azimuth);
//        else if (longitudinalDifference < 0) return rad2deg(azimuth + Math.PI);
//        else if (latitudinalDifference < 0) return rad2deg(Math.PI);
//        return 0d;

        //1404-02 عوضش کردم
        //https://stackoverflow.com/a/9462757/2144698
        double longitude1 = source.longitude;
        double longitude2 = destination.longitude;
        double latitude1 = Math.toRadians(source.latitude);
        double latitude2 = Math.toRadians(destination.latitude);
        double longDiff= Math.toRadians(longitude2-longitude1);
        double y= Math.sin(longDiff)*Math.cos(latitude2);
        double x=Math.cos(latitude1)*Math.sin(latitude2)-Math.sin(latitude1)*Math.cos(latitude2)*Math.cos(longDiff);

        return (Math.toDegrees(Math.atan2(y, x))+360)%360;

    }


    public static int KmToMapZoom(double km){
        //(zl) =  = radikal2( 40000/(km/2) )
        return (int)Math.log(40000/(km/2));
    }
    public static double ZoomToKm(int zoom){
        //km = ( 40000/(2 ^ zl) ) * 2
        return ( 40000/Math.pow(2, zoom) ) * 2;
    }
    public static float GetNewDeclination(float lat, float lon, float elev){
        GeomagneticField field = new GeomagneticField(
                (float) lat,
                (float)lon,
                (float) elev,
                System.currentTimeMillis()
        );
        return field.getDeclination();
    }
    public static float RefreshSavedDelination(float lat, float lon, float elev){
        float declination = GetNewDeclination(lat, lon, elev);
        return declination;
    }
    public static float RefreshSavedDelinationIfNeeded(float lat, float lon, float elev){
        long currentMils = System.currentTimeMillis();
        if (currentMils - app.lastDelinationRead > 60000 * 10) {
            app.lastDelinationRead = currentMils;
            float declination = GetNewDeclination(lat, lon, elev);
            return declination;
        }
        return app.declination;
    }
    public double getPixelsPerMeter(double lat, double zoom, Context context) {
        double pixelsPerTile = 256 * ((double)context.getResources().getDisplayMetrics().densityDpi / 160);
        double numTiles = Math.pow(2,zoom);
        double metersPerTile = Math.cos(Math.toRadians(lat)) * MapScaleModel.EQUATOR_LENGTH_METERS / numTiles;
        return pixelsPerTile / metersPerTile;
    }
    public double getMetersPerPixel(double lat, int zoom){
        return MapScaleModel.EQUATOR_LENGTH_METERS * Math.abs(Math.cos(lat * Math.PI/180)) / Math.pow(2, zoom+8);
    }

    public static LatLng newPointAtDistanceAndDegree(LatLng from, double distanceInMetres, double bearing) {
        double brngRad =  Math.toRadians(bearing);
        double latRad =  Math.toRadians(from.latitude);
        double lonRad =  Math.toRadians(from.longitude);
        int earthRadiusInMetres = 6371000;
        double distFrac = distanceInMetres / earthRadiusInMetres;

        double latitudeResult =  Math.asin( Math.sin(latRad) * Math.cos(distFrac) + Math.cos(latRad) * Math.sin(distFrac) * Math.cos(brngRad));
        double a = Math.atan2(Math.sin(brngRad) * Math.sin(distFrac) * Math.cos(latRad), Math.cos(distFrac) - Math.sin(latRad) * Math.sin(latitudeResult));
        double longitudeResult = (lonRad + a + 3 * Math.PI) % (2 * Math.PI) - Math.PI;

        return new LatLng(Math.toDegrees(latitudeResult), Math.toDegrees(longitudeResult));
    }

//    36.6526,66.45454
//    N36.6526,E66.45454
//    S36.6526,E66.45454
//    52,29
//    S36.6526°,E66.45454    °
//    36.6526°, 66.45454°
//    N 36.6526, E 66.45454
    static final String latLonPatternD_simple1 =  "\\s*(-?\\d+(\\.\\d+)?)\\s*[°]?\\s*[,]\\s*\\s*(-?\\d+(\\.\\d+)?)\\s*[°]?";
    static final String latLonPatternD =  "^([N|S|s|n])*\\s*(-?\\d+(\\.\\d+)?)\\s*[°]?\\s*[,]\\s*([E|W|e|w])*\\s*(-?\\d+(\\.\\d+)?)\\s*[°]?$";
//    N 36° 22.293', w 59° 20.093'
//    N 36° 0.5', E 59° 0.2'
    static final String latLonPatternM =  "^([N|S|s|n]?)\\s*(-?\\d+)\\s*[°]\\s*(\\d+(\\.\\d+)?)\\s*['][,]?\\s*([E|W|e|w]*)\\s*(-?\\d+)\\s*[°]\\s*(\\d+(\\.\\d+)?)\\s*[']?$";
    static final String latLonPatternS =  "^([N|S|s|n]?)\\s*(-?\\d+)\\s*[°]\\s*(\\d+)\\s*[']\\s*(\\d+(\\.\\d+)?)\\s*[\"][,]?\\s*([E|W|e|w]*)\\s*(-?\\d+)\\s*[°]\\s*(\\d+)\\s*[']\\s*(\\d+(\\.\\d+)?)\\s*[\"]$";
    public static LatLng extractLatLonString(String text){
        Pattern mPattern = Pattern.compile(latLonPatternD_simple1);
        Matcher matcher = mPattern.matcher(text);
        if (matcher.find()){
            Double lat = Double.parseDouble(matcher.group(1));
            Double lon = Double.parseDouble(matcher.group(3));
            LatLng res = new LatLng(lat, lon);
            return res;
        }
        mPattern = Pattern.compile(latLonPatternD);
        matcher = mPattern.matcher(text);
        if (matcher.find()){
            Double lat = Double.parseDouble(matcher.group(2));
            Double lon = Double.parseDouble(matcher.group(5));
            if (matcher.group(1) != null){
                if (matcher.group(1).toLowerCase() == "s" && lat > 0)
                    lat = lat * -1;
            }
            if (matcher.group(4) != null){
                if (matcher.group(4).toLowerCase() == "w" && lon > 0)
                    lon = lon * -1;
            }
            LatLng res = new LatLng(lat, lon);
            return res;
        }
        mPattern = Pattern.compile(latLonPatternM);
        matcher = mPattern.matcher(text);
        if (matcher.find()){
            Double ddlat = Double.parseDouble(matcher.group(2));
            Double mmlat = Double.parseDouble(matcher.group(3));

            Double ddlon = Double.parseDouble(matcher.group(6));
            Double mmlon = Double.parseDouble(matcher.group(7));

            Double lat = ddlat + (mmlat / 60);
            Double lon =  ddlon + (mmlon / 60);

            if (matcher.group(1) != null){
                if (matcher.group(1).toLowerCase() == "s" && lat > 0)
                    lat = lat * -1;
            }
            if (matcher.group(5) != null){
                if (matcher.group(5).toLowerCase() == "w" && lon > 0)
                    lon = lon * -1;
            }
            LatLng res = new LatLng(lat, lon);
            return res;
        }
        mPattern = Pattern.compile(latLonPatternS);
        matcher = mPattern.matcher(text);
        if (matcher.find()){
            Double ddlat = Double.parseDouble(matcher.group(2));
            Double mmlat = Double.parseDouble(matcher.group(3));
            Double sslat = Double.parseDouble(matcher.group(4));

            Double ddlon = Double.parseDouble(matcher.group(7));
            Double mmlon = Double.parseDouble(matcher.group(8));
            Double sslon = Double.parseDouble(matcher.group(9));

            mmlat = mmlat + (sslat / 60);
            mmlon = mmlon + (sslon / 60);

            Double lat = ddlat + (mmlat / 60);
            Double lon =  ddlon + (mmlon / 60);

            if (matcher.group(1) != null){
                if (matcher.group(1).toLowerCase() == "s" && lat > 0)
                    lat = lat * -1;
            }
            if (matcher.group(6) != null){
                if (matcher.group(6).toLowerCase() == "w" && lon > 0)
                    lon = lon * -1;
            }
            LatLng res = new LatLng(lat, lon);
            return res;
        }
        return null;
    }
    static final String utmPattern = "^(\\d{1,2})\\s*([A-Za-z]?)\\s+(\\d{6,7})\\s*(me|ME|mE|Me|e|E)?\\s+(\\d{1,7})\\s*(mn|MN|mN|Mn|n|N)?$";
    public static LatLng extractUtmString(String text){
        Pattern mPattern = Pattern.compile(utmPattern);
        Matcher matcher = mPattern.matcher(text);
        if (matcher.find()){
            UTM2Deg utm2Deg = new UTM2Deg(matcher.group(1)+" "+matcher.group(2).toUpperCase() + " " + matcher.group(3)+ " " + matcher.group(5), 6);

            Double lat = utm2Deg.latitude;
            Double lon = utm2Deg.longitude;

            LatLng res = new LatLng(lat, lon);
            return res;
        }
        return null;
    }

}
