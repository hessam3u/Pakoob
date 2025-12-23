package maptools;

import static UI.HFragment.stktrc2k;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import androidx.appcompat.app.AlertDialog;

import bo.entity.NbMap;
import bo.sqlite.TTExceptionLogSQLite;
import mojafarin.pakoob.R;
import mojafarin.pakoob.app;
import utils.PrjConfig;
import utils.hutilities;

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


    public static boolean EncryptFile(String orginalFileAddress, String path, int CCustomerId) {
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
            key[i] = (byte) (keyStr.charAt(i) - '0');
        }

        for (int i = 0; i < size; i++) {
            bts[i] ^= key[i % keyLen];
        }

        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path));
            bos.write(bts);
            bos.flush();
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;

        } catch (IOException e) {
            e.printStackTrace();
            return false;

        }
        return true;
    }

    public static String decryptToTemp(String source, Context context, int CCustomerId) {
        return decryptToTemp(source, "", context, CCustomerId);
    }

    public static String decryptToTemp(String source, String fileNameAndExt, Context context, int CCustomerId) {
        String tempDirectoryName = context.getFilesDir() + File.separator + tempFolder + File.separator;
        String filename = fileNameAndExt.isEmpty() ? source.substring(source.lastIndexOf("/") + 1) : fileNameAndExt;
        File tempDirectory = new File(tempDirectoryName);
        if (!tempDirectory.exists()) {
            tempDirectory.mkdirs();
        }
        String outputAddress = tempDirectoryName + filename;
        boolean encRes = EncryptFile(source, outputAddress, CCustomerId);
        if (!encRes)
            return "";
        return outputAddress;
    }

    public static boolean craeteTilesAtManyZoom(String source, Context context, List<LatLng> bounds, int fromZoom, int toZoom) {
//                String resIdStr = "m78621se_mashhad6s2";//m78621se_mashhad6s2    m78624se_kang_s2   m79624sw_mashhad5
//        int resId = context.getResources().getIdentifier(resIdStr, "drawable", context.getPackageName());
//        List<LatLng> bounds = PersianMapIndex25000.stringToBoundsList(resIdStr);
//        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), resId);

        Bitmap bm = BitmapFactory.decodeFile(source);
        try {
            for (int i = fromZoom; i <= toZoom; i++) {
                MapTile.CreateTilesForZoom(bm, hMapTools.tilesFolder, i, bounds, context);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean deleteTilesAtManyZoom(Context context, NbMap item, int fromZoom, int toZoom, boolean DeleteSourceFile) {
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
                else {
                    Log.e(TAG, "Source File Not Found...");
                }
                DeleteStep = 450;
            }
            DeleteStep = 500;

        } catch (FileNotFoundException ex) {
            Log.e(TAG, ex.getMessage());
            ex.printStackTrace();
            TTExceptionLogSQLite.insert(ex.getMessage(), "ds is : " + DeleteStep + "_" + stktrc2k(ex), PrjConfig.app, 101);
            return false;
        }
        return true;
    }

    public static String getFileNameAtMapsFolder(String NCCIndex, Context context) {
        String downloadDirectoryName = context.getFilesDir() + File.separator + mapsFolder + File.separator;
        String fileName = NCCIndex + ".jpg";
        return downloadDirectoryName + File.separator + fileName;
    }

    public static String createAndRetTempDownloadFolder(Context context) {
        String tempDirectoryName = context.getFilesDir() + File.separator + tempFolder + File.separator;
        File tempDirectory = new File(tempDirectoryName);
        if (!tempDirectory.exists()) {
            tempDirectory.mkdirs();
        }
        tempDirectoryName = tempDirectoryName + downloadFolder + File.separator;
        tempDirectory = new File(tempDirectoryName);
        if (!tempDirectory.exists()) {
            tempDirectory.mkdirs();
        }
        return tempDirectoryName;
    }

    public static String createAndRetMapsFolder(Context context) {
        String tempDirectoryName = context.getFilesDir() + File.separator + mapsFolder + File.separator;
        File tempDirectory = new File(tempDirectoryName);
        if (!tempDirectory.exists()) {
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

    public static long getTileFolderSize(Context context) {
        String path = context.getFilesDir() + File.separator + hMapTools.tilesFolder;
        return hutilities.getFolderSize(path);
    }

    public static long getMapsFolderSize(Context context) {
        String path = context.getFilesDir() + File.separator + hMapTools.mapsFolder;
        return hutilities.getFolderSize(path);
    }

    public static long getHighZoomSize(Context context) {
        String path1 = context.getFilesDir() + File.separator + hMapTools.tilesFolder + File.separator + "16";
        String path2 = context.getFilesDir() + File.separator + hMapTools.tilesFolder + File.separator + "17";
        String path3 = context.getFilesDir() + File.separator + hMapTools.tilesFolder + File.separator + "18";
        String path4 = context.getFilesDir() + File.separator + hMapTools.tilesFolder + File.separator + "19";

        return hutilities.getFolderSize(path1) + hutilities.getFolderSize(path2) + hutilities.getFolderSize(path3) + hutilities.getFolderSize(path4);
    }

    public static void deleteTilesFolder(Context context) {
        String path = context.getFilesDir() + File.separator + hMapTools.tilesFolder;
        File file = new File(path);
        if (!file.exists())
            return;
        hutilities.deleteRecursive(file);
    }

    public static void deleteMapsFolder(Context context) {
        String path = context.getFilesDir() + File.separator + hMapTools.mapsFolder;
        File file = new File(path);
        if (!file.exists())
            return;
        hutilities.deleteRecursive(file);
    }

    public static void deleteHighZoomFolder(Context context) {
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

    public static float RefreshSavedDelinationIfNeeded(float lat, float lon, float elev) {
        long currentMils = System.currentTimeMillis();
        if (currentMils - app.lastDelinationRead > 60000 * 10) {
            app.lastDelinationRead = currentMils;
            float declination = GeoCalcs.GetNewDeclination(lat, lon, elev);
            return declination;
        }
        return app.declination;
    }
}
