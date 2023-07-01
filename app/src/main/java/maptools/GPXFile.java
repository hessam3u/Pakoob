package maptools;

import android.content.Context;
import android.content.ReceiverCallNotAllowedException;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.animation.BounceInterpolator;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.NotNull;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.spi.AbstractSelectionKey;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import bo.entity.NbPoi;
import bo.entity.NbPoiCompact;
import bo.sqlite.NbPoiSQLite;
import mojafarin.pakoob.MainActivity;
import mojafarin.pakoob.MapPage;
import mojafarin.pakoob.R;
import mojafarin.pakoob.app;
import utils.hutilities;
import utils.projectStatics;

import static bo.entity.NbPoi.Enums.PoiType_Folder;
import static bo.entity.NbPoi.Enums.PoiType_Route;
import static bo.entity.NbPoi.Enums.PoiType_Track;
import static bo.entity.NbPoi.Enums.PoiType_Waypoint;
import static bo.entity.NbPoi.Enums.ShowStatus_Show;
import static utils.TextFormat.ReplacePersianNumbersWithEnglishOne;

public class GPXFile {
    public List<TrackData> tracks = new ArrayList<>();
    public List<WaypointData> waypoints = new ArrayList<>();
    public long InnerDbId = 0;
    public String MainFileName = "";

    public static GPXFile ParseFile(InputStream inputStream, Context context, String emptyObjectName) {
        GPXFile res = new GPXFile();
        int emptyNameCount = 1;

        try {
            XmlPullParserFactory parserCreator;

            parserCreator = XmlPullParserFactory.newInstance();

            XmlPullParser parser = parserCreator.newPullParser();

            parser.setInput(inputStream, null);
//            parser.setInput(context.getResources()
//                    .openRawResource(R.raw.testgpx), null);
            int parserEvent = parser.getEventType();
            String data;
            Float lat, lon;
            // Parse the XML returned on the network
            while (parserEvent != XmlPullParser.END_DOCUMENT) {
                String tag = parser.getName();
                if (parserEvent == XmlPullParser.START_TAG) {
                    if (tag.equals("wpt")) {
                        //Reed Waypoint to end
                        WaypointData waypoint = extractWaypointData(parser);
                        if (waypoint.Name == null || waypoint.Name.length() == 0)
                            waypoint.Name = emptyObjectName + "-" + emptyNameCount++;
                        res.waypoints.add(waypoint);
                    } else if (tag.equals("trk")) {
                        //Read Track
                        TrackData trackData = extractTrackData(parser);
                        if (trackData.Name == null || trackData.Name.length() == 0)
                            trackData.Name = emptyObjectName + "-" + emptyNameCount++;
                        res.tracks.add(trackData);
                    }
                }

                parserEvent = parser.next();
            }


        } catch (XmlPullParserException | IOException | ParseException e) {
            e.printStackTrace();
        }

        return res;
    }

    private static TrackData extractTrackData(XmlPullParser parser) throws IOException, XmlPullParserException, ParseException {
        TrackData track = new TrackData();
        String tag;
        String data;
        Float lat;
        Float lon;
        int parserEvent = parser.next();
        tag = parser.getName();
        TimeZone utc = TimeZone.getTimeZone("UTC");
        dateFormat.setTimeZone(utc);


        while (!(parserEvent == XmlPullParser.END_TAG && tag.equals("trk")) && parserEvent != XmlPullParser.END_DOCUMENT) {
            if (parserEvent == XmlPullParser.START_TAG) {
                switch (tag) {
                    case "name": {
                        parserEvent = parser.next();
                        data = parser.getText();
                        track.Name = data;
                        break;
                    }
                    case "extensions": {
                        parserEvent = parser.next();
                        tag = parser.getName();
                        while (!(parserEvent == XmlPullParser.END_TAG && tag.equals("extensions"))) {
                            if (parserEvent == XmlPullParser.START_TAG) {
                                if (tag.equals("gpxx:TrackExtension")) {
                                    parserEvent = parser.next();
                                    tag = parser.getName();
                                    while (!(parserEvent == XmlPullParser.END_TAG && tag.equals("gpxx:TrackExtension"))) {
                                        if (parserEvent == XmlPullParser.START_TAG) {
                                            if (tag.equals("gpxx:DisplayColor")) {
                                                parserEvent = parser.next();//Move forward
                                                data = ReplacePersianNumbersWithEnglishOne(parser.getText());
                                                track.ColorString = data;
                                            }
                                        }
                                        parserEvent = parser.next();
                                        tag = parser.getName();
                                    }

                                }
                            }
                            parserEvent = parser.next();
                            tag = parser.getName();
                        }
                        break;
                    }
                    case "trkseg": {
                        //1400-11-04
                        if (track.Points.size() > 0) {
                            //there was a Pause, so insert it:
                            LatLng pt = new LatLng(0, 0);
                            track.Points.add(pt);
                            if (track.Time.size() > 0) {
                                Calendar cl = Calendar.getInstance();
                                cl.setTimeInMillis(0);
                                track.Time.add(cl);
                            }
                            if (track.Elev.size() > 0)
                                track.Elev.add(0f);
                        }
                        parserEvent = parser.next();
                        tag = parser.getName();
                        while (!(parserEvent == XmlPullParser.END_TAG && tag.equals("trkseg"))) {
                            if (parserEvent == XmlPullParser.START_TAG) {
                                switch (tag) {
                                    case "trkpt":
                                        data = parser.getAttributeValue(null, "lat");
                                        lat = hutilities.parseFloatPersian(data);
                                        data = parser.getAttributeValue(null, "lon");
                                        lon = hutilities.parseFloatPersian(data);
                                        LatLng pt = new LatLng(lat, lon);
                                        track.Points.add(pt);

                                        parserEvent = parser.next();
                                        tag = parser.getName();
                                        while (!(parserEvent == XmlPullParser.END_TAG && tag.equals("trkpt"))) {
                                            if (parserEvent == XmlPullParser.START_TAG) {
                                                switch (tag) {
                                                    case "ele": {
                                                        parserEvent = parser.next();//Move forward
                                                        data = ReplacePersianNumbersWithEnglishOne(parser.getText());
                                                        track.Elev.add(hutilities.parseFloatPersian(data));
                                                        break;
                                                    }
                                                    case "time": {
                                                        parserEvent = parser.next();//Move forward
                                                        data = ReplacePersianNumbersWithEnglishOne(parser.getText());
                                                        Calendar cal = Calendar.getInstance();
//                                                        cal.setTimeZone(utc);
                                                        cal.setTime(parseDate(data));

                                                        track.Time.add(cal);
                                                        break;
                                                    }
                                                    case "gpxx:Temperature": {
                                                        //Any changes ALSO in ====> Pause Track Empty Add
                                                        parserEvent = parser.next();//Move forward
                                                        data = parser.getText();
                                                        break;
                                                    }
                                                }
                                            }
                                            parserEvent = parser.next();
                                            tag = parser.getName();
                                        }

                                        break;
                                }
                            }
                            parserEvent = parser.next();
                            tag = parser.getName();
                        }
                        break;
                    }
                }
            }

            parserEvent = parser.next();
            tag = parser.getName();
        }
        return track;
    }

    @NotNull
    private static WaypointData extractWaypointData(XmlPullParser parser) throws IOException, XmlPullParserException, ParseException {
        Float lat;
        Float lon;
        int parserEvent;
        String tag;
        String data;
        TimeZone utc = TimeZone.getTimeZone("UTC");
        dateFormat.setTimeZone(utc);

        WaypointData waypoint = new WaypointData();
        lat = hutilities.parseFloatPersian(parser.getAttributeValue(null, "lat"));
        lon = hutilities.parseFloatPersian(parser.getAttributeValue(null, "lon"));
        waypoint.Point = new LatLng(lat, lon);
        parserEvent = parser.next();
        tag = parser.getName();
        while (!(parserEvent == XmlPullParser.END_TAG && tag.equals("wpt")) && parserEvent != XmlPullParser.END_DOCUMENT) {
            if (parserEvent == XmlPullParser.START_TAG) {
                switch (tag) {
                    case "ele": {
                        parserEvent = parser.next();
                        data = ReplacePersianNumbersWithEnglishOne(parser.getText());
                        waypoint.Elevation = hutilities.parseFloatPersian(data);
                        break;
                    }
                    case "time": {
                        parserEvent = parser.next();
                        data = ReplacePersianNumbersWithEnglishOne(parser.getText());
                        Calendar cal = Calendar.getInstance();
//                        cal.setTimeZone(utc);
                        cal.setTime(parseDate(data));
                        waypoint.Time = cal;
                        break;
                    }
                    case "name": {
                        parserEvent = parser.next();
                        data = parser.getText();
                        waypoint.Name = data;
                        break;
                    }
                    case "sym": {
                        parserEvent = parser.next();
                        data = ReplacePersianNumbersWithEnglishOne(parser.getText());
                        waypoint.Icon = data;
                        break;
                    }
                }
            }
            parserEvent = parser.next();
            tag = parser.getName();
        }
        return waypoint;
    }

    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    static SimpleDateFormat[] allDateFormats = new SimpleDateFormat[]{
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"),
    };
    static int lastDateFormatIndex = 0;
    static Date parseDate(String data){
        int index = lastDateFormatIndex;
        for (int i = 0; i < allDateFormats.length; i++) {
            try
            {
                return allDateFormats[index].parse(data);
            }
            catch (ParseException e) {
                lastDateFormatIndex = (lastDateFormatIndex + 1) % allDateFormats.length;
                index = lastDateFormatIndex;
            }
        }
        return new Date();
    }

    public static GPXFile ImportGpxFileIntoMapbaz(String path, String preferedRootNameIfNeeded, Context context, long parentId, byte currentLevel, boolean addToMap, Handler handlerForAdd) {
        try {
            File file = new File(path);
            FileInputStream inputStream = new FileInputStream(file);
            String inputFileNameWithExtention = path.substring(path.lastIndexOf(File.separator) + 1);
            return ImportGpxFileIntoMapbaz(inputStream, inputFileNameWithExtention, preferedRootNameIfNeeded, context, parentId, currentLevel, addToMap, handlerForAdd);
        } catch (Exception ex) {
            projectStatics.showDialog(context, context.getResources().getString(R.string.dialog_invalidGpxFile_title)
                    , context.getResources().getString(R.string.dialog_invalidGpxFile)
                    , context.getResources().getString(R.string.ok)
                    , null, "", null);
            Log.e("بازکردن_جی_پی_ایکس", ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }

    public static GPXFile ImportGpxFileIntoMapbaz(InputStream inputStream, String inputFileNameWithExtention, String preferedRootNameIfNeeded, Context context, long parentId, byte currentLevel, boolean addToMap, Handler handlerForAdd) {

        Random rand = new Random();
        rand.nextInt();
        int extentionIndex = inputFileNameWithExtention.lastIndexOf('.');
        String inputFileName = "";
        if (inputFileName.length() > 0) {
            inputFileName = extentionIndex > 0 ? inputFileNameWithExtention.substring(0, extentionIndex) : "";
        }

        GPXFile res = GPXFile.ParseFile(inputStream, context, inputFileName.length() == 0 ? "NoName" : inputFileName);


        String pathToSave = context.getFilesDir() + File.separator + hMapTools.gpxFolder + File.separator;
        //Check GPX folder exists?
        File pathToSaveFile = new File(pathToSave);
        if (!pathToSaveFile.exists())
            pathToSaveFile.mkdirs();
        int tracksize = res.tracks.size();
        int poiSize = res.waypoints.size();

        List<NbPoi> insertedItems = new ArrayList<>();
        if (tracksize + poiSize == 0) {
            projectStatics.showDialog(context, context.getResources().getString(R.string.dialog_emptyGpxfile_title)
                    , context.getResources().getString(R.string.dialog_emptyGpxfile_desc)
                    , context.getResources().getString(R.string.ok)
                    , null, "", null);
            return null;
        }
        if (inputFileName.length() == 0) {
            if (tracksize > 0)
                inputFileName = res.tracks.get(0).Name;
            else if (poiSize > 0)
                inputFileName = res.waypoints.get(0).Name;
        }
        inputFileName = hutilities.ReplaceInvalidFileChars(inputFileName, "_");
        res.MainFileName = inputFileName;


        if (tracksize == 1 && poiSize == 0) {
            List<NbPoi> tmpInserted = saveTracksToDb(res, tracksize, pathToSave, currentLevel, parentId);
            insertedItems.addAll(tmpInserted);
            res.InnerDbId = tmpInserted.get(0).NbPoiId;
        } else if (poiSize == 1 && tracksize == 0) {
            List<NbPoi> tmpInserted = saveWaipointsToDb(res, poiSize, currentLevel, parentId);
            insertedItems.addAll(tmpInserted);
            res.InnerDbId = tmpInserted.get(0).NbPoiId;
        } else {
            //1401-05-19 added
            res.MainFileName = preferedRootNameIfNeeded.length() > 0?preferedRootNameIfNeeded:inputFileName;
            NbPoi folderObj = NbPoi.getInstance(res.MainFileName, ""/*AltName*/, currentLevel, parentId, "", 0d
                    , 0d, 0d, 0d, 0, ShowStatus_Show, PoiType_Folder
                    , 0, (byte) 1, (byte) 0, (byte) 0, (byte) 100, (byte) 0, (byte) 1
                    , "", 1, 0d, 0d);
            List<LatLng> bounds = GPXFile.calcGPXBounds(res);
            folderObj.LatS = bounds.get(0).latitude;
            folderObj.LatN = bounds.get(1).latitude;
            folderObj.LonW = bounds.get(0).longitude;
            folderObj.LonE = bounds.get(1).longitude;
            folderObj.LatBegin = folderObj.LatS;
            folderObj.LonBegin = folderObj.LonW;

            //Log.e("فولدر ساخته شد", folderObj.LatS + "-" + folderObj.LatN + "-" + folderObj.LonW + "-" + folderObj.LonE);

            res.InnerDbId = folderObj.NbPoiId = NbPoiSQLite.insert(folderObj);
            List<NbPoi> ptInserted = saveWaipointsToDb(res, poiSize, (byte) (currentLevel + 1), folderObj.NbPoiId);
            insertedItems.addAll(ptInserted);
            List<NbPoi> trkInserted = saveTracksToDb(res, tracksize, pathToSave, (byte) (currentLevel + 1), folderObj.NbPoiId);
            insertedItems.addAll(trkInserted);

            //Saving the file

//                String fileNameToSave = inputFileName  +"_"+rand.nextInt() + ".gpx";
//                String pathToSave = context.getFilesDir() + File.separator + hMapTools.gpxFolder + File.separator +fileNameToSave;
//                File pathToSaveFile = new File(context.getFilesDir() + File.separator + hMapTools.gpxFolder );
//                if (!pathToSaveFile.exists())
//                    pathToSaveFile.mkdirs();
//                hutilities.copyFile(file, new File(pathToSave));
        }

        if (addToMap) {
            if (handlerForAdd == null)
                handlerForAdd = new Handler(Looper.getMainLooper());
            handlerForAdd.post(new Runnable() {
                public void run() {
                    int insertedSize = insertedItems.size();
                    for (int i = 0; i < insertedSize; i++) {
                        NbPoiCompact compact = NbPoiCompact.getInstance(insertedItems.get(i));
                        MapPage.addPOIToMap(compact, MainActivity.map, false, context);
                        app.visiblePOIs.add(compact);
                    }
                }
            });

        }
        return res;


    }

    private static List<LatLng> calcGPXBounds(GPXFile res) {
        int trackSize = res.tracks.size();
        int poiSize = res.waypoints.size();
        double s = 0;
        double n = 0;
        double e = 0;
        double w = 0;
        for (int i = 0; i < trackSize; i++) {
            TrackData data = res.tracks.get(i);
            List<LatLng> bounds = data.calculateBounds();
            if (i == 0) {
                s = bounds.get(0).latitude;
                w = bounds.get(0).longitude;
                n = bounds.get(1).latitude;
                e = bounds.get(1).longitude;
            } else {
                if (s > bounds.get(0).latitude)
                    s = bounds.get(0).latitude;
                if (n < bounds.get(1).latitude)
                    n = bounds.get(1).latitude;
                if (w > bounds.get(0).longitude)
                    w = bounds.get(0).longitude;
                if (e < bounds.get(1).longitude)
                    e = bounds.get(1).longitude;
            }
        }
        for (int i = 0; i < poiSize; i++) {
            WaypointData data = res.waypoints.get(i);

            if (i == 0 && trackSize == 0){
                n = s = data.Point.latitude;
                e = w = data.Point.longitude;
            }
            if (s > data.Point.latitude)
                s = data.Point.latitude;
            if (n < data.Point.latitude)
                n = data.Point.latitude;
            if (w > data.Point.longitude)
                w = data.Point.longitude;
            if (e < data.Point.longitude)
                e = data.Point.longitude;
        }

        List<LatLng> answer = new ArrayList<>();
        answer.add(new LatLng(s, w));
        answer.add(new LatLng(n, e));
        return answer;
    }

    private static List<NbPoi> saveWaipointsToDb(GPXFile gpx, int poiSize, byte level, long parentId) {
        List<NbPoi> res = new ArrayList<>();
        for (int i = 0; i < poiSize; i++) {
            WaypointData data = gpx.waypoints.get(i);
            NbPoi inserted = NbPoi.getInstance(data.Name, "", level, parentId, "", data.Point.latitude
                    , data.Point.longitude, 0d, 0d, 0, ShowStatus_Show, PoiType_Waypoint
                    , 0, (byte) 1, (byte) 0, (byte) 0, (byte) 100, (byte) 0, (byte) 1, "", 1, data.Point.latitude, data.Point.longitude);
            inserted.NbPoiId = NbPoiSQLite.insert(inserted);
            res.add(inserted);
        }
        return res;
    }

    private static List<NbPoi> saveTracksToDb(GPXFile gpx, int trackSize, String address, byte level, long parentId) {
        List<NbPoi> res = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < trackSize; i++) {
            TrackData data = gpx.tracks.get(i);
            List<LatLng> bounds = data.bounds;
            if (bounds == null)
                bounds = data.calculateBounds();
            LatLng firstPoint = data.Points.size() > 0 ? data.Points.get(0) : null;
            double latBegin = firstPoint == null ? 0 : firstPoint.latitude;
            double lonBegin = firstPoint == null ? 0 : firstPoint.longitude;

            String tmpFileName = hutilities.ReplaceInvalidFileChars(data.Name, "_");
            String fileNameToSave = address + tmpFileName + "_" + rand.nextInt() + ".gpx";
            NbPoi inserted = NbPoi.getInstance(data.Name, "", level, parentId, fileNameToSave, bounds.get(0).latitude
                    , bounds.get(0).longitude, bounds.get(1).latitude, bounds.get(1).longitude, RandomColors.get(rand.nextInt(colorCount)), ShowStatus_Show
                    , PoiType_Track, 0, (byte) 1, (byte) 0, (byte) 0, (byte) 100, (byte) 0, (byte) 10, "", 1, latBegin, lonBegin);
            inserted.NbPoiId = NbPoiSQLite.insert(inserted);
            res.add(inserted);

            StringBuilder fileContent = data.getTrackFileContent();

            try {
                File ff = new File(fileNameToSave);
                FileWriter writer = new FileWriter(ff);
                writer.append(fileContent.toString());
                writer.flush();
                writer.close();
            } catch (Exception exception) {
                int asakal = 39;
                int yyy = 93;
                //---------
            }
        }
        return res;
    }

    public static NbPoi SaveDesignedRouteToDb(long nbPoiId, short POIType, TrackData data, Context context) throws IOException {
        NbPoi res = null;
        Random rand = new Random();
        String pathToSave = context.getFilesDir() + File.separator + hMapTools.gpxFolder + File.separator;
        File tempDirectory = new File(pathToSave);
        if (!tempDirectory.exists()) {
            tempDirectory.mkdirs();
        }

        String fileNameToSave = pathToSave + data.Name + "_" + rand.nextInt() + ".gpx";
        List<LatLng> bounds = data.bounds;
        if (bounds == null)
            bounds = data.calculateBounds();
        if (nbPoiId != 0) {
            res = NbPoiSQLite.select(nbPoiId);
            fileNameToSave = res.Address;
            res.Name = data.Name;
            res.Color = data.Color;
            res.LatN = bounds.get(1).latitude;
            res.LonW = bounds.get(0).longitude;
            res.LatS = bounds.get(0).latitude;
            res.LonE = bounds.get(1).longitude;
            NbPoiSQLite.update(res);
        } else {

            LatLng firstPoint = data.Points.size() > 0 ? data.Points.get(0) : null;
            double latBegin = firstPoint == null ? 0 : firstPoint.latitude;
            double lonBegin = firstPoint == null ? 0 : firstPoint.longitude;


            res = NbPoi.getInstance(data.Name, "", (byte) 0, 0, fileNameToSave, bounds.get(0).latitude
                    , bounds.get(0).longitude, bounds.get(1).latitude, bounds.get(1).longitude, data.Color, ShowStatus_Show
                    , POIType, 0, (byte) 1, (byte) 0, (byte) 0, (byte) 100, (byte) 0, (byte) 10, "", 1, latBegin, lonBegin);
            res.NbPoiId = NbPoiSQLite.insert(res);
        }
        int boundsSize = data.Points.size();
        int elevSize = data.Elev.size();
        int timeSize = data.Time.size();
        StringBuilder fileContent = new StringBuilder();
        Log.e("Saving", "SIZE:" + boundsSize);
        for (int j = 0; j < boundsSize; j++) {
            String latitude = DoubleToStringForLatLan(data.Points.get(j).latitude);
            String longitude = DoubleToStringForLatLan(data.Points.get(j).longitude);
            Log.e("Saving", "COUNTER:" + j);
            fileContent.append(latitude + "," + longitude + "," + (elevSize > j ? ((int) data.Elev.get(j).floatValue()) : "") + "," + (timeSize > j ? data.Time.get(j).getTimeInMillis() : ""));
            fileContent.append('\n');
        }
        try {
            File ff = new File(fileNameToSave);
            FileWriter writer = new FileWriter(ff, false);
            writer.append(fileContent.toString());
            writer.flush();
            writer.close();
        } catch (Exception exception) {
            //---------
            throw exception;
        }

        //Addinggggggggg to MAP. NOTE : *******same as DesignedTradk and SaveDesignedWaypoint
        if (nbPoiId == 0) {
            NbPoiCompact compact = NbPoiCompact.getInstance(res);
            MapPage.addPOIToMap(compact, MainActivity.map, false, context);
            app.visiblePOIs.add(compact);
        } else {
            NbPoiCompact compactItem = null;
            int visiSize = app.visiblePOIs.size();
            for (int i = 0; i < visiSize && compactItem == null; i++) {
                NbPoiCompact item = app.visiblePOIs.get(i);
                if (item.NbPoiId == nbPoiId) {
                    compactItem = item;
                }
            }
            if (compactItem != null) {
                //in This Place, I use another method, first Remove and Then Add. But in MainActivity.SetShowHide, I did another
                if (compactItem.polyLine != null) {
                    compactItem.polyLine.remove();
                }
            }
            if (res.ShowStatus == ShowStatus_Show) {
                NbPoiCompact compact = NbPoiCompact.getInstance(res);
                MapPage.addPOIToMap(compact, MainActivity.map, false, context);
                app.visiblePOIs.add(compact);
            }
        }

        return res;
    }

    public static NbPoi SaveDesignedWaypoint(long nbPoiId, String Name, LatLng location, Context context) {
        NbPoi res = null;
        if (nbPoiId > 0) {
            res = NbPoiSQLite.select(nbPoiId);
            res.Name = Name;
            res.LatS = location.latitude;
            res.LonW = location.longitude;
            NbPoiSQLite.update(res);
        } else {
            res = NbPoi.getInstance(Name, "", (byte) 0, 0, "", location.latitude
                    , location.longitude, 0d, 0d, 0, ShowStatus_Show, PoiType_Waypoint
                    , 0, (byte) 1, (byte) 0, (byte) 0, (byte) 100, (byte) 0, (byte) 1, "", 1, location.latitude, location.longitude);
            res.NbPoiId = NbPoiSQLite.insert(res);
        }

        //Addinggggggggg to MAP. NOTE : *******same as DesignedTradk and SaveDesignedWaypoint
        if (nbPoiId == 0) {
            NbPoiCompact compact = NbPoiCompact.getInstance(res);
            MapPage.addPOIToMap(compact, MainActivity.map, false, context);
            app.visiblePOIs.add(compact);
        } else {
            NbPoiCompact compactItem = null;
            int visiSize = app.visiblePOIs.size();
            for (int i = 0; i < visiSize && compactItem == null; i++) {
                NbPoiCompact item = app.visiblePOIs.get(i);
                if (item.NbPoiId == nbPoiId) {
                    compactItem = item;
                }
            }
            if (compactItem != null) {
                //in This Place, I use another method, first Remove and Then Add. But in MainActivity.SetShowHide, I did another
                if (compactItem.polyLine != null) {
                    compactItem.polyLine.remove();
                }
            }
            if (res.ShowStatus == ShowStatus_Show) {
                NbPoiCompact compact = NbPoiCompact.getInstance(res);
                MapPage.addPOIToMap(compact, MainActivity.map, false, context);
                app.visiblePOIs.add(compact);
            }
        }

        return res;
    }

    public static final int colorCount = 20;
    public static final List<Integer> RandomColors = Arrays.asList(
            0xfff44336, 0xffe91e63, 0xff9c27b0, 0xff673ab7,
            0xff3f51b5, 0xff2196f3, 0xff03a9f4, 0xff00bcd4,
            0xff009688, 0xff4caf50, 0xff8bc34a, 0xffcddc39,
            0xffffeb3b, 0xffffc107, 0xffff9800, 0xffff5722,
            0xff795548, 0xff9e9e9e, 0xff607d8b, 0xff333333
    );

    public static String DoubleToStringForLatLan(double dd) {
        String res = Double.toString(dd);
        int ix = res.indexOf('.');
        if (ix != -1) {
            return res.substring(0, Math.min(ix + 15, res.length()));
        }
        return res;
    }

    public static void DeleteAllNbPois() {
        List<NbPoi> allPois = NbPoiSQLite.selectAll();
        for (int i = 0; i < allPois.size(); i++) {
            DeleteNbPoiRec(allPois.get(i));
        }
    }

    public static void DeleteNbPoiRec(NbPoi obj) {
        if (obj.PoiType == (NbPoi.Enums.PoiType_Folder)) {
            List<NbPoi> allPois = NbPoiSQLite.selectByLevel(obj.Level + 1, obj.NbPoiId);
            for (int i = 0; i < allPois.size(); i++) {
                DeleteNbPoiRec(allPois.get(i));
            }
        }
        NbPoiSQLite.delete(obj);
        NbPoiCompact compact = app.findInVisiblePois(obj.NbPoiId);
        if (compact != null) {
            if (compact.marker != null)
                compact.marker.remove();
            if (compact.polyLine != null)
                compact.polyLine.remove();
        }
        if (obj.Address.length() > 0) {
            File ff = new File(obj.Address);
            if (ff.exists())
                ff.delete();
        }
    }

    public static StringBuilder ExportOneNbPoiToGPX_InnerContent(long NbPoiId) {
        return ExportOneNbPoiToGPX_InnerContent(NbPoiSQLite.select(NbPoiId));
    }

    public static StringBuilder ExportOneNbPoiToGPX_InnerContent(NbPoi poi) {
        TrackData data = TrackData.readTrackData(poi.Address, null);
        StringBuilder res = new StringBuilder();
        if (poi.PoiType == PoiType_Folder) {
            List<NbPoi> childs = NbPoiSQLite.selectByLevel(poi.Level + 1, poi.NbPoiId);
            for (int i = 0; i < childs.size(); i++) {
                res.append(ExportOneNbPoiToGPX_InnerContent(childs.get(i)));
            }
        } else if (poi.PoiType == PoiType_Track || poi.PoiType == PoiType_Route) {
            TimeZone utc = TimeZone.getTimeZone("UTC");
            dateFormat.setTimeZone(utc);

            long distance = 0;
            res.append("<trk>\n");
            res.append("<name>" + poi.Name + "</name>\n");
            res.append("<extensions>\n" +
                    "<gpxx:TrackExtension>\n" +
                    "<gpxx:DisplayColor>Cyan</gpxx:DisplayColor>\n" +
                    "</gpxx:TrackExtension>\n" +
//                    "<gpxtrkx:TrackStatsExtension>\n" +
//                    "<gpxtrkx:Distance>"+(long)data.GetDistanceInMeter()+"</gpxtrkx:Distance>\n" +
//                    "</gpxtrkx:TrackStatsExtension>\n" +
                    "</extensions>");

            res.append("<trkseg>\n");
            int timeSize = data.Time.size();
            int datasize = data.Points.size();
            for (int i = 0; i < datasize; i++) {
                LatLng pt = data.Points.get(i);

                Calendar time = data.Time.get(i);
                //1400-11-04 Pause Track
                if (pt.longitude == 0 && pt.longitude == 0 && (timeSize <= i || time == null || time.getTimeInMillis() == 0)
                        && data.Elev.get(i) == 0) {
                    res.append("</trkseg>\n" + "<trkseg>\n");
                    continue;
                }

                String timeValue = "";
                if (timeSize > i && time != null) {
                    timeValue = dateFormat.format(time.getTime());
                }

                res.append("<trkpt lat=\"" + pt.latitude + "\" lon=\"" + pt.longitude + "\">\n" +
                        "<ele>" + data.Elev.get(i) + "</ele>\n" +
                        (timeValue.length() > 0 ? "<time>" + timeValue + "</time>\n" : "") +
                        "</trkpt>\n");
            }

            res.append("</trkseg>\n" +
                    "</trk>");
        } else if (poi.PoiType == PoiType_Route) {

        } else {
            res.append("<wpt lat=\"" + poi.LatS + "\" lon=\"" + poi.LonW + "\">\n" +
                    "<name>" + poi.Name + "</name>" +
//                    "<time>2020-08-13T16:32:40Z</time>"+
                    "<sym>Flag, Blue</sym>\n" +
                    "<type>user</type>" +
                    "</wpt>");

        }

        return res;
    }
//    public static String ExportGPXToString(List<Long> NbPoiIdList){
//        StringBuilder res = new StringBuilder();
//
//        res.append("<gpx creator=\"MapBaz\" version=\"1.1\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd http://www.garmin.com/xmlschemas/GpxExtensions/v3 http://www8.garmin.com/xmlschemas/GpxExtensionsv3.xsd http://www.garmin.com/xmlschemas/WaypointExtension/v1 http://www8.garmin.com/xmlschemas/WaypointExtensionv1.xsd http://www.garmin.com/xmlschemas/TrackPointExtension/v1 http://www.garmin.com/xmlschemas/TrackPointExtensionv1.xsd\">\n" +
//                "<metadata><link href=\"http://www.pakoob24.ir\"><text>pakoob24.ir</text></link>\n" +
//                "<time>"+dateFormat.format(Calendar.getInstance().getTime())+"</time>\n" +
//                "</metadata>");
//
//        for (int i = 0; i < NbPoiIdList.size(); i++) {
//            StringBuilder middle = ExportOneNbPoiToGPX_InnerContent(NbPoiIdList.get(i));
//            res.append(middle);
//        }
//
//        res.append("</gpx>");
//        return res.toString();
//    }

    public static String ExportGPXToString(List<NbPoi> NbPoiList) {
        StringBuilder res = new StringBuilder();
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        dateFormat.setNumberFormat(numberFormat);

        res.append("<?xml version=\"1.0\" encoding=\"utf-8\"?><gpx creator=\"MapBaz\" version=\"1.1\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd http://www.garmin.com/xmlschemas/WaypointExtension/v1 http://www8.garmin.com/xmlschemas/WaypointExtensionv1.xsd http://www.garmin.com/xmlschemas/TrackPointExtension/v1 http://www.garmin.com/xmlschemas/TrackPointExtensionv1.xsd http://www.garmin.com/xmlschemas/GpxExtensions/v3 http://www8.garmin.com/xmlschemas/GpxExtensionsv3.xsd http://www.garmin.com/xmlschemas/ActivityExtension/v1 http://www8.garmin.com/xmlschemas/ActivityExtensionv1.xsd http://www.garmin.com/xmlschemas/AdventuresExtensions/v1 http://www8.garmin.com/xmlschemas/AdventuresExtensionv1.xsd http://www.garmin.com/xmlschemas/PressureExtension/v1 http://www.garmin.com/xmlschemas/PressureExtensionv1.xsd http://www.garmin.com/xmlschemas/TripExtensions/v1 http://www.garmin.com/xmlschemas/TripExtensionsv1.xsd http://www.garmin.com/xmlschemas/TripMetaDataExtensions/v1 http://www.garmin.com/xmlschemas/TripMetaDataExtensionsv1.xsd http://www.garmin.com/xmlschemas/ViaPointTransportationModeExtensions/v1 http://www.garmin.com/xmlschemas/ViaPointTransportationModeExtensionsv1.xsd http://www.garmin.com/xmlschemas/CreationTimeExtension/v1 http://www.garmin.com/xmlschemas/CreationTimeExtensionsv1.xsd http://www.garmin.com/xmlschemas/AccelerationExtension/v1 http://www.garmin.com/xmlschemas/AccelerationExtensionv1.xsd http://www.garmin.com/xmlschemas/PowerExtension/v1 http://www.garmin.com/xmlschemas/PowerExtensionv1.xsd http://www.garmin.com/xmlschemas/VideoExtension/v1 http://www.garmin.com/xmlschemas/VideoExtensionv1.xsd\" xmlns=\"http://www.topografix.com/GPX/1/1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:wptx1=\"http://www.garmin.com/xmlschemas/WaypointExtension/v1\" xmlns:gpxtrx=\"http://www.garmin.com/xmlschemas/GpxExtensions/v3\" xmlns:gpxtpx=\"http://www.garmin.com/xmlschemas/TrackPointExtension/v1\" xmlns:gpxx=\"http://www.garmin.com/xmlschemas/GpxExtensions/v3\" xmlns:trp=\"http://www.garmin.com/xmlschemas/TripExtensions/v1\" xmlns:adv=\"http://www.garmin.com/xmlschemas/AdventuresExtensions/v1\" xmlns:prs=\"http://www.garmin.com/xmlschemas/PressureExtension/v1\" xmlns:tmd=\"http://www.garmin.com/xmlschemas/TripMetaDataExtensions/v1\" xmlns:vptm=\"http://www.garmin.com/xmlschemas/ViaPointTransportationModeExtensions/v1\" xmlns:ctx=\"http://www.garmin.com/xmlschemas/CreationTimeExtension/v1\" xmlns:gpxacc=\"http://www.garmin.com/xmlschemas/AccelerationExtension/v1\" xmlns:gpxpx=\"http://www.garmin.com/xmlschemas/PowerExtension/v1\" xmlns:vidx1=\"http://www.garmin.com/xmlschemas/VideoExtension/v1\">\n" +
                "<metadata><link href=\"http://www.pakoob24.ir\"><text>pakoob24.ir</text></link>\n" +
                "<time>" + dateFormat.format(Calendar.getInstance().getTime()) + "</time>\n" +
                "</metadata>");

        //Sort list by Waypoints
        List<NbPoi> tempAdded = new ArrayList<>();
        for (int j = 0; j < NbPoiList.size(); j++) {
            NbPoi item = NbPoiList.get(j);
            if (item.PoiType == NbPoi.Enums.PoiType_Route || item.PoiType == NbPoi.Enums.PoiType_Track)
                tempAdded.add(item);
            else
                tempAdded.add(0, item);
        }

        for (int i = 0; i < tempAdded.size(); i++) {
            StringBuilder middle = ExportOneNbPoiToGPX_InnerContent(tempAdded.get(i));
            res.append(middle);
        }

        res.append("</gpx>");
        return res.toString();
    }
}
