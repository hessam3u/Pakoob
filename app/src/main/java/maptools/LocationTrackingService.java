package maptools;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import bo.entity.NbCurrentTrack;
import bo.sqlite.NbCurrentTrackSQLite;
import mojafarin.pakoob.MainActivity;
import mojafarin.pakoob.R;
import mojafarin.pakoob.app;
import utils.hutilities;

import com.google.android.gms.location.*;

public class LocationTrackingService extends Service {
    private static final int NOTIF_ID = 1001;
    private static final String CHANNEL_ID = "location_channel_v1";
    private FusedLocationProviderClient fusedClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    //برای اونایی که Play ندارن دو تای زیری
    private LocationManager mLocationManager;
    private android.location.LocationListener mLocationListener;


    private final long LOCATION_INTERVAL = 2;
    private final float LOCATION_DISTANCE = 10f;

    @Override
    public void onCreate() {
        super.onCreate();
        app.repo = LocationRepository.getInstance();
        if (hutilities.checkGooglePlayServiceAvailability(this)) {
            fusedClient = LocationServices.getFusedLocationProviderClient(this);
        } else {
            mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        }


        createNotificationChannel();
    }

    public void stopFromOutside() {
        app.repo.setTrackingActive(false);
        stopLocationUpdates();//انتقال پیدا کرد به تابع مربوطه در LocationChanged در mapPage
        stopForeground(true);
        stopSelf();
        MainActivity.isTrackingServiceRunning = false;
    }

    private Notification buildNotification() {
        Intent openIntent = new Intent(this, MainActivity.class);
        openIntent.setAction("Location_Tracking_Service");
        PendingIntent pi = PendingIntent.getActivity(this, 0, openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0));

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("در حال برداشت موقعیت")
                .setContentText("پاکوب در حال ثبت مسیر است")
                .setSmallIcon(R.drawable.ic__track_icon48) // جایگزین کن
                .setContentIntent(pi)
                .setOngoing(true)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel(CHANNEL_ID,
                    "Location tracking", NotificationManager.IMPORTANCE_LOW);
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (nm != null) nm.createNotificationChannel(ch);
        }
    }

    private void setupLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(LOCATION_INTERVAL); // 2s
        locationRequest.setFastestInterval(LOCATION_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(10f);
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        if (hutilities.checkGooglePlayServiceAvailability(this)
            //&& android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.O
        ) {
            Log.e(Tag, "خط آخر استارت سرویس");
            if (locationCallback != null) return;
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult result) {
                    MainActivity.isTrackingServiceRunning = true;
                    Location loc = result.getLastLocation();
                    if (loc != null) {
                        app.repo.setCurrentLocation(loc);
                        savePointToDB_IfTracking(loc);
                        // در صورت نیاز: ذخیره یا ارسال به سرور اینجا انجام شود
                    }
                }
            };
            fusedClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        } else {
            if (mLocationListener != null) return;
            mLocationListener = new android.location.LocationListener() {
                @SuppressLint("MissingPermission")
                public void onLocationChanged(Location locationListener) {
                    //1400-01-03 I thinks it is faster than traditional way whick commented in next lines
                    MainActivity.isTrackingServiceRunning = true;
                    Location loc = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    app.repo.setCurrentLocation(loc);
                    savePointToDB_IfTracking(loc);
                }

                public void onProviderDisabled(String provider) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }
            };
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, mLocationListener);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        app.repo.setTrackingActive(false);
        stopLocationUpdates();
        stopForeground(true);
        MainActivity.isTrackingServiceRunning = false;
    }

    public void stopLocationUpdates() {
        MainActivity.isTrackingServiceRunning = false;
        if (fusedClient != null && locationCallback != null) {
            fusedClient.removeLocationUpdates(locationCallback);
            locationCallback = null;
        }
        if (mLocationManager != null && mLocationListener != null) {
            mLocationManager.removeUpdates((android.location.LocationListener) mLocationListener);
            mLocationListener = null;
        }
    }

    private final String Tag = "TrkService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(Tag, "استارت سرویس");
        if (MainActivity.isTrackingServiceRunning)
            return START_STICKY;
        else
            MainActivity.isTrackingServiceRunning = true;

        startForeground(NOTIF_ID, buildNotification());
        setupLocationRequest();
        startLocationUpdates();
        app.repo.setTrackingActive(true);
        Log.e(Tag, "خط آخر استارت سرویس");


        // START_STICKY تا سیستم در صورت نیاز سرویس را دوباره ری‌استارت کند
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder; // سرویس unbound
    }

    Long lastTime = 0l;
    public void savePointToDB_IfTracking(Location currentLatLon){
        if (app.session.getIsTrackRecording() != 1){
            return;
        }
        NbCurrentTrack trkPt = new NbCurrentTrack();
        trkPt.Latitude = currentLatLon.getLatitude();
        trkPt.Longitude = currentLatLon.getLongitude();
        trkPt.Time = System.currentTimeMillis();
        trkPt.Elevation = (float) currentLatLon.getAltitude();

        if (trkPt.Time - lastTime < 1000)
            return;
        NbCurrentTrackSQLite.insert(trkPt);
        lastTime = trkPt.Time;
    }


    //برای اتصال سرویس به اپ بخصوص در زمانی که سرویس بسته میشه
    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        public LocationTrackingService getService() {
            return LocationTrackingService.this;
        }
    }
    //اتمام - برای اتصال سرویس به اپ بخصوص در زمانی که سرویس بسته میشه

}
