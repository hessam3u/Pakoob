package maptools;

import android.content.Context;
import android.hardware.GeomagneticField;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeoCalcs {
    public static int DegreePrecision = 6;
    public static float DegreePrecisionTen = 1000000;
    public static int MinutePrecision = 4;
    public static float MinutePrecisionTen = 10000;
    public static int UtmPrecision = 1;

    public static final double EQUATOR_LENGTH_METERS = 40075016.686;
    public static final double EQUATOR_LENGTH_FEET = 131479713.537;

    public static final int DegreesMinuteSeconds = 1;
    public static final int DegreesDecimalMinutes = 2;
    public static final int DecimalDegrees = 3;
    public static final int UTM = 4;
    public static final int DEFAULT_POSITION_FORMAT = UTM;
    public static final int DecimalDegrees_JustNumber = 5;
    public static final int TIME_FRIENDLY_MODE_LONG_EXACT = 1;
    public static final int TIME_FRIENDLY_MODE_SMALL = 2;
    //    36.6526,66.45454
//    N36.6526,E66.45454
//    S36.6526,E66.45454
//    52,29
//    S36.6526°,E66.45454    °
//    36.6526°, 66.45454°
//    N 36.6526, E 66.45454
    static final String latLonPatternD_simple1 = "\\s*(-?\\d+(\\.\\d+)?)\\s*[°]?\\s*[,]\\s*\\s*(-?\\d+(\\.\\d+)?)\\s*[°]?";
    static final String latLonPatternD = "^([N|S|s|n])*\\s*(-?\\d+(\\.\\d+)?)\\s*[°]?\\s*[,]\\s*([E|W|e|w])*\\s*(-?\\d+(\\.\\d+)?)\\s*[°]?$";
    //    N 36° 22.293', w 59° 20.093'
//    N 36° 0.5', E 59° 0.2'
    public static final String latLonPatternM = "^([N|S|s|n]?)\\s*(-?\\d+)\\s*[°]\\s*(\\d+(\\.\\d+)?)\\s*['][,]?\\s*([E|W|e|w]*)\\s*(-?\\d+)\\s*[°]\\s*(\\d+(\\.\\d+)?)\\s*[']?$";
    public static final String latLonPatternS = "^([N|S|s|n]?)\\s*(-?\\d+)\\s*[°]\\s*(\\d+)\\s*[']\\s*(\\d+(\\.\\d+)?)\\s*[\"][,]?\\s*([E|W|e|w]*)\\s*(-?\\d+)\\s*[°]\\s*(\\d+)\\s*[']\\s*(\\d+(\\.\\d+)?)\\s*[\"]$";
    public static final String regex = "(?:[NS]?\\s*|\\s*)([+-]?\\d+\\.\\d+)[°\\s]*[,\\s]*(?:[EW]?\\s*|\\s*)([+-]?\\d+\\.\\d+)[°\\s]*";
    static final String utmPattern = "^(\\d{1,2})\\s*([A-Za-z]?)\\s+(\\d{6,7})\\s*(me|ME|mE|Me|e|E)?\\s+(\\d{1,7})\\s*(mn|MN|mN|Mn|n|N)?$";

    public static int KmToMapZoom(double km) {
        //(zl) =  = radikal2( 40000/(km/2) )
        return (int) Math.log(40000 / (km / 2));
    }

    public static double ZoomToKm(int zoom) {
        //km = ( 40000/(2 ^ zl) ) * 2
        return (40000 / Math.pow(2, zoom)) * 2;
    }

    public static LatLng extractLatLonString(String text) {
        Matcher matcher;
        Pattern mPattern;
        //My old method without using gpt
        //mPattern = Pattern.compile(latLonPatternD_simple1);
//        matcher = mPattern.matcher(text);
//        if (matcher.find()) {
//            Double lat = Double.parseDouble(matcher.group(1));
//            Double lon = Double.parseDouble(matcher.group(3));
//            LatLng res = new LatLng(lat, lon);
//            return res;
//        }
//        mPattern = Pattern.compile(latLonPatternD);
//        matcher = mPattern.matcher(text);
//        if (matcher.find()) {
//            Double lat = Double.parseDouble(matcher.group(2));
//            Double lon = Double.parseDouble(matcher.group(5));
//            if (matcher.group(1) != null) {
//                if (matcher.group(1).toLowerCase() == "s" && lat > 0)
//                    lat = lat * -1;
//            }
//            if (matcher.group(4) != null) {
//                if (matcher.group(4).toLowerCase() == "w" && lon > 0)
//                    lon = lon * -1;
//            }
//            LatLng res = new LatLng(lat, lon);
//            return res;
//        }

        mPattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        matcher = mPattern.matcher(text);

        if (matcher.find()) {
            String latitude = matcher.group(1);
            String longitude = matcher.group(2);

            // Check if latitude is marked as South or longitude is marked as West and negate the values accordingly
            if (text.matches(".*[Ss].*")) {
                latitude = "-" + latitude;
            }
            if (text.matches(".*[Ww].*")) {
                longitude = "-" + longitude;
            }
            LatLng res = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
            return res;
        }


        mPattern = Pattern.compile(latLonPatternM);
        matcher = mPattern.matcher(text);
        if (matcher.find()) {
            Double ddlat = Double.parseDouble(matcher.group(2));
            Double mmlat = Double.parseDouble(matcher.group(3));

            Double ddlon = Double.parseDouble(matcher.group(6));
            Double mmlon = Double.parseDouble(matcher.group(7));

            Double lat = ddlat + (mmlat / 60);
            Double lon = ddlon + (mmlon / 60);

            if (matcher.group(1) != null) {
                if (matcher.group(1).toLowerCase() == "s" && lat > 0)
                    lat = lat * -1;
            }
            if (matcher.group(5) != null) {
                if (matcher.group(5).toLowerCase() == "w" && lon > 0)
                    lon = lon * -1;
            }
            LatLng res = new LatLng(lat, lon);
            return res;
        }
        mPattern = Pattern.compile(latLonPatternS);
        matcher = mPattern.matcher(text);
        if (matcher.find()) {
            Double ddlat = Double.parseDouble(matcher.group(2));
            Double mmlat = Double.parseDouble(matcher.group(3));
            Double sslat = Double.parseDouble(matcher.group(4));

            Double ddlon = Double.parseDouble(matcher.group(7));
            Double mmlon = Double.parseDouble(matcher.group(8));
            Double sslon = Double.parseDouble(matcher.group(9));

            mmlat = mmlat + (sslat / 60);
            mmlon = mmlon + (sslon / 60);

            Double lat = ddlat + (mmlat / 60);
            Double lon = ddlon + (mmlon / 60);

            if (matcher.group(1) != null) {
                if (matcher.group(1).toLowerCase() == "s" && lat > 0)
                    lat = lat * -1;
            }
            if (matcher.group(6) != null) {
                if (matcher.group(6).toLowerCase() == "w" && lon > 0)
                    lon = lon * -1;
            }
            LatLng res = new LatLng(lat, lon);
            return res;
        }
        return null;
    }

    public static LatLng extractUtmString(String text) {
        Pattern mPattern = Pattern.compile(utmPattern);
        Matcher matcher = mPattern.matcher(text);
        if (matcher.find()) {
            UTM2Deg utm2Deg = new UTM2Deg(matcher.group(1) + " " + matcher.group(2).toUpperCase() + " " + matcher.group(3) + " " + matcher.group(5), 6);

            Double lat = utm2Deg.latitude;
            Double lon = utm2Deg.longitude;

            LatLng res = new LatLng(lat, lon);
            return res;
        }
        return null;
    }


    public double getPixelsPerMeter(double lat, double zoom, Context context) {
        double pixelsPerTile = 256 * ((double) context.getResources().getDisplayMetrics().densityDpi / 160);
        double numTiles = Math.pow(2, zoom);
        double metersPerTile = Math.cos(Math.toRadians(lat)) * GeoCalcs.EQUATOR_LENGTH_METERS / numTiles;
        return pixelsPerTile / metersPerTile;
    }

    public double getMetersPerPixel(double lat, int zoom) {
        return GeoCalcs.EQUATOR_LENGTH_METERS * Math.abs(Math.cos(lat * Math.PI / 180)) / Math.pow(2, zoom + 8);
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

    public static String LocationToString(LatLng location, int destLocation, LocationToStringStyle style) {
        String res = "";
        if (destLocation == UTM) {
            Deg2UTM utm = new Deg2UTM(location.latitude, location.longitude, GeoCalcs.DegreePrecision, GeoCalcs.UtmPrecision);
            if (style == LocationToStringStyle.Inline || style == LocationToStringStyle.InLineSmall) {
                return utm.Zone + Character.toString(utm.Letter) + " " + (int) utm.Easting + " " + (int) utm.Northing;
            } else if (style == LocationToStringStyle.Twoline || style == LocationToStringStyle.TwoLineSmall) {
                return utm.Zone + Character.toString(utm.Letter) + " " + (int) utm.Easting + (style == LocationToStringStyle.Twoline ? "mE" : "")
                        + "\n" + (int) utm.Northing + (style == LocationToStringStyle.Twoline ? "mN" : "");
            }
        } else if (destLocation == DecimalDegrees) {
            return (location.latitude > 0 ? "N" : "S") + ((int) (location.latitude * GeoCalcs.DegreePrecisionTen) / GeoCalcs.DegreePrecisionTen) + (style == LocationToStringStyle.InLineSmall || style == LocationToStringStyle.TwoLineSmall ? "" : "°")
                    + (style == LocationToStringStyle.Twoline || style == LocationToStringStyle.TwoLineSmall ? "\n" : " ")
                    + (location.longitude > 0 ? "E" : "W") + ((int) (location.longitude * GeoCalcs.DegreePrecisionTen) / GeoCalcs.DegreePrecisionTen) + (style == LocationToStringStyle.InLineSmall || style == LocationToStringStyle.TwoLineSmall ? "" : "°");
        } else if (destLocation == DecimalDegrees_JustNumber) {
            return String.format(Locale.ENGLISH, "%.05f", location.latitude) + "," + String.format(Locale.ENGLISH, "%.05f", location.longitude);
        } else if (destLocation == DegreesDecimalMinutes) {
            double absolute_lat = Math.abs(location.latitude);
            int degrees_lat = (int) Math.floor(absolute_lat);
            //double minutesNotTruncated_lat = (int)((absolute_lat - degrees_lat) * 60 * 10000) / 10000d;
            double absolute_lon = Math.abs(location.longitude);
            int degrees_lon = (int) Math.floor(absolute_lon);

            return (location.latitude > 0 ? "N" : "S") + degrees_lat + "°" + ((int) ((absolute_lat - degrees_lat) * 60 * 10000) / 10000d) + "'"
                    + (style == LocationToStringStyle.Twoline || style == LocationToStringStyle.TwoLineSmall ? "\n" : " ")
                    + (location.longitude > 0 ? "E" : "W") + degrees_lon + "°" + ((int) ((absolute_lon - degrees_lon) * 60 * 10000) / 10000d) + "'";
        } else if (destLocation == DegreesMinuteSeconds) {
            double absolute_lat = Math.abs(location.latitude);
            int degrees_lat = (int) Math.floor(absolute_lat);
            double minutesNotTruncated_lat = (absolute_lat - degrees_lat) * 60;
            int minutes_lat = (int) Math.floor(minutesNotTruncated_lat);

            double absolute_lon = Math.abs(location.longitude);
            int degrees_lon = (int) Math.floor(absolute_lon);
            double minutesNotTruncated_lon = (absolute_lon - degrees_lon) * 60;
            int minutes_lon = (int) Math.floor(minutesNotTruncated_lon);

            return (location.latitude > 0 ? "N" : "S") + degrees_lat + "°" + minutes_lat + "'" + (((int) (Math.floor((minutesNotTruncated_lat - minutes_lat) * 60)) * 100000) / 100000d) + "\""
                    + (style == LocationToStringStyle.Twoline || style == LocationToStringStyle.TwoLineSmall ? "\n" : " ")
                    + (location.longitude > 0 ? "E" : "W") + degrees_lon + "°" + minutes_lon + "'" + (((int) (Math.floor((minutesNotTruncated_lon - minutes_lon) * 60)) * 100000) / 100000d) + "\"";
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

        double res = 6371010 * Math.acos(Math.sin(phi1) * Math.sin(phi2) + Math.cos(phi1) * Math.cos(phi2) * Math.cos(lam2 - lam1));
        return res;
    }

    public static String distanceBetweenFriendly(double lat1, double lon1, double lat2, double lon2) {
        return distanceBetweenFriendly(distanceBetweenMeteres(lat1, lon1, lat2, lon2));
    }

    public static String distanceBetweenFriendly(double dist) {
        int distInt = (int) dist;
        if (distInt < 100000)
            return distInt + "m";
        return distInt / 1000 + "km";
    }

    public static String distanceBetweenFriendlyInKm(double dist) {
        int distInt = (int) dist;
        int mtrs = distInt % 1000 / 100;
        return (distInt / 1000) + (mtrs > 0 ? "." + mtrs : "") + "km";
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

    public static String areaFriendly(double area) {
        if (area < 10000) {
            int areaInt = (int) area;
            return Integer.toString(areaInt) + " m2";
        } else {
            double nArea = area * 0.0001;
            int areaInt = (int) (nArea);
            int khoorde = ((int) ((nArea - areaInt) * 10) % 10);
            return Integer.toString(areaInt) + (khoorde > 0 ? "." + khoorde : "") + " ha";
        }
    }

    public static String timeBetweenFriendly(long miliStart, long miliEnd, final int compactMode) {
        long diff = (miliEnd - miliStart) / 1000;
        return timeFriendly(diff, compactMode);
    }

    public static String timeFriendly(long diff, final int compactMode) {
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

            String res = (days > 0 ? days + "d " : "")
                    + (hour < 10 ? "0" : "") + hour
                    + ":"
                    + (minutes < 10 ? "0" : "") + minutes
                    + ":"
                    + (seconds < 10 ? "0" : "") + seconds;
            return res;
        }
        return "";
    }

    public static String GetSpeedFriendlyKmPh(double mps) {
        //added 1401-06-01 به خاطر باگ عجیب سرعت توی هواپیما
        if (mps > 10000)
            mps = 10000;
        double res = (3.6 * mps);
        res = (long) (res * 10);
        return String.valueOf(res / 10);
    }

    public static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    public static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public static double GetAzimuthInDegree(LatLng source, LatLng destination) {
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
        double longDiff = Math.toRadians(longitude2 - longitude1);
        double y = Math.sin(longDiff) * Math.cos(latitude2);
        double x = Math.cos(latitude1) * Math.sin(latitude2) - Math.sin(latitude1) * Math.cos(latitude2) * Math.cos(longDiff);

        return (Math.toDegrees(Math.atan2(y, x)) + 360) % 360;

    }

    public static float GetNewDeclination(float lat, float lon, float elev) {
        GeomagneticField field = new GeomagneticField(
                (float) lat,
                (float) lon,
                (float) elev,
                System.currentTimeMillis()
        );
        return field.getDeclination();
    }

    public static float RefreshSavedDelination(float lat, float lon, float elev) {
        float declination = GetNewDeclination(lat, lon, elev);
        return declination;
    }

    public static LatLng newPointAtDistanceAndDegree(LatLng from, double distanceInMetres, double bearing) {
        double brngRad = Math.toRadians(bearing);
        double latRad = Math.toRadians(from.latitude);
        double lonRad = Math.toRadians(from.longitude);
        int earthRadiusInMetres = 6371000;
        double distFrac = distanceInMetres / earthRadiusInMetres;

        double latitudeResult = Math.asin(Math.sin(latRad) * Math.cos(distFrac) + Math.cos(latRad) * Math.sin(distFrac) * Math.cos(brngRad));
        double a = Math.atan2(Math.sin(brngRad) * Math.sin(distFrac) * Math.cos(latRad), Math.cos(distFrac) - Math.sin(latRad) * Math.sin(latitudeResult));
        double longitudeResult = (lonRad + a + 3 * Math.PI) % (2 * Math.PI) - Math.PI;

        return new LatLng(Math.toDegrees(latitudeResult), Math.toDegrees(longitudeResult));
    }

    public enum LocationToStringStyle {
        Inline,
        Twoline,
        InLineSmall,
        TwoLineSmall,
    }
}
