package maptools;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import utils.PrjConfig;
import bo.entity.NbCurrentTrack;
import bo.sqlite.NbCurrentTrackSQLite;
import mojafarin.pakoob.MainActivity;
import mojafarin.pakoob.R;
import utils.hutilities;
import utils.projectStatics;

public class TrackInBackgroundService extends Service {
    private final String TAG = "HHH_Location";
    private String RecordTrack_NotificationTitle;
    private String RecordTrack_NotificationTitle_Desc;
    private final int IdOfNotification = Integer.parseInt(PrjConfig.NOTIF_CHANAL_RECORDING_TRACK_ID);
    Context context;

    public TrackInBackgroundService(){

    }
    @Override
    public void onCreate() {
        super.onCreate();
        if (context != null) {
            RecordTrack_NotificationTitle = context.getString(R.string.RecordTrack_NotificationTitle);
            RecordTrack_NotificationTitle_Desc =  context.getString(R.string.RecordTrack_NotificationTitle_Desc);
        }
        else{
            RecordTrack_NotificationTitle = "در حال ثبت مسیر";
            RecordTrack_NotificationTitle_Desc = "ثبت مسیر حرکت شما در حال انجام است. لطفا برای مدیریت آن، روی این پیام ضربه بزنید";
        }
        context = getApplicationContext();
        //انتقال به onStartCommand به درخواست جی پی تی
        //        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startMyOwnForeground();
//            Log.e("پیام ترک زدن", "انجام شد - جدید");
//        }
//        else {
//
//            Notification notification = getNotificationObj();
//            //NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//            //notificationManager.notify(IdOfNotification, notification);
//
//            startForeground(1, notification);
//            Log.e("پیام ترک زدن", "انجام شد");
//        }
    }

    Notification getNotificationObj(){
        Bitmap bmTitle = hutilities.textAsBitmap(RecordTrack_NotificationTitle, 43, Color.WHITE, projectStatics.getIranSans_FONT(this), context);
        Bitmap bmDesc = hutilities.textAsBitmap(RecordTrack_NotificationTitle_Desc, 35, Color.WHITE, projectStatics.getIranSans_FONT(this), context);
        SpannableStringBuilder sbTitle = new SpannableStringBuilder(RecordTrack_NotificationTitle);
        sbTitle.setSpan(new CustomTypefaceSpan("iransansweb", projectStatics.getIranSans_FONT(this)), 0, sbTitle.length() - 1,
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

        SpannableStringBuilder sbchannelName = new SpannableStringBuilder(RecordTrack_NotificationTitle_Desc);
        sbchannelName.setSpan(new CustomTypefaceSpan("iransansweb", projectStatics.getIranSans_FONT(this)), 0, sbchannelName.length() - 1,
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_track_is_recording);
        contentView.setImageViewResource(R.id.image, R.mipmap.ic_launcher);
        contentView.setImageViewBitmap(R.id.title, bmTitle);
//        contentView.setTextViewText(R.id.title, sbTitle);
        contentView.setImageViewBitmap(R.id.text, bmDesc);

        //Handle Notification ONCLICK:
        Intent notificationIntent = new Intent(this, MainActivity.class ) ;
        notificationIntent.putExtra( "ChanalId" , PrjConfig.NOTIF_CHANAL_RECORDING_TRACK_ID) ;
        notificationIntent.addCategory(Intent. CATEGORY_LAUNCHER ) ;
        notificationIntent.setAction(Intent. ACTION_MAIN );
        notificationIntent.setFlags(Intent. FLAG_ACTIVITY_CLEAR_TOP | Intent. FLAG_ACTIVITY_SINGLE_TOP ) ;
        PendingIntent resultIntent = PendingIntent.getActivity (this, 0 , notificationIntent , PendingIntent.FLAG_IMMUTABLE) ;

        Notification notification = new NotificationCompat.Builder(this, RecordTrack_NotificationTitle)
//                    .setContentTitle("در حال ذخیره مسیر")
//                    .setContentText("مسیر در حال ذخیره سازی است")
                .setSmallIcon(R.drawable.ic__track_icon48)
                .setOngoing(true) // Non Clearing Notification
                .setContent(contentView)
                .setContentIntent(resultIntent)
                .build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;

        return notification;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground()
    {
        NotificationChannel chan = new NotificationChannel(RecordTrack_NotificationTitle, RecordTrack_NotificationTitle_Desc, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);
        Notification notification = getNotificationObj();

        startForeground(IdOfNotification, notification);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startMyOwnForeground();
            Log.e("پیام ترک زدن", "انجام شد - جدید");
        }
        else {

            Notification notification = getNotificationObj();
            //NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            //notificationManager.notify(IdOfNotification, notification);

            startForeground(1, notification);
            Log.e("پیام ترک زدن", "انجام شد");
        }

        //startTimer();
        startGettingLocation();
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //stoptimertask();
        destroyGettingLocation();

        //Remove notificaton
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(IdOfNotification);

        //HHH Ya in bayad ejra she ya onDestroy in MainActivity
//        Intent broadcastIntent = new Intent();
//        broadcastIntent.setAction("restartservice");
//        broadcastIntent.setClass(this, Restarter.class);
//        this.sendBroadcast(broadcastIntent);
    }


    private final int LOCATION_INTERVAL = 3;
    private final int LOCATION_DISTANCE = 10;
    private TrackInBackgroundService.LocationListener mLocationListener;
    private LocationManager mLocationManager;
    public void startGettingLocation(){
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }
        mLocationListener = new TrackInBackgroundService.LocationListener(LocationManager.GPS_PROVIDER);

        try {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, mLocationListener);

        } catch (java.lang.SecurityException ex) {
            // Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            // Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    public void destroyGettingLocation(){
        if (mLocationManager != null) {
            try {
                mLocationManager.removeUpdates(mLocationListener);
            } catch (Exception ex) {
                Log.i(TAG, "fail to remove location listeners, ignore", ex);
            }
        }
    }
//    public int counter=0;
//    private Timer timer;
//    private TimerTask timerTask;
//    public void startTimer() {
//        timer = new Timer();
//        timerTask = new TimerTask() {
//            public void run() {
//                Log.i("Count", "=========  "+ (counter++));
//            }
//        };
//        timer.schedule(timerTask, 1000, 1000); //
//    }
//
//    public void stoptimertask() {
//        if (timer != null) {
//            timer.cancel();
//            timer = null;
//        }
//    }

    private class LocationListener implements android.location.LocationListener {
        private Location lastLocation = null;
        private final String TAG = "LocationListener";
        private Location mLastLocation;
        private Long lastTime = 0l;

        public LocationListener(String provider) {
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            mLastLocation = location;
            Log.i(TAG, "LocationChanged: " + location);
            NbCurrentTrack loc = new NbCurrentTrack();
            loc.Latitude = (location.getLatitude());
            loc.Longitude = (location.getLongitude());
            loc.Elevation = (float)location.getAltitude();
            loc.Time = System.currentTimeMillis();

//            if (loc.Time - lastTime < 1000)
//                return;
            lastTime = loc.Time;
            NbCurrentTrackSQLite.insert(loc);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + status);
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    public class CustomTypefaceSpan extends TypefaceSpan {
        private final Typeface newType;

        public CustomTypefaceSpan(String family, Typeface type) {
            super(family);
            newType = type;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            applyCustomTypeFace(ds, newType);
        }

        @Override
        public void updateMeasureState(TextPaint paint) {
            applyCustomTypeFace(paint, newType);
        }

        @SuppressLint("WrongConstant")
        private void applyCustomTypeFace(Paint paint, Typeface tf) {
            int oldStyle;
            Typeface old = paint.getTypeface();
            if (old == null) {
                oldStyle = 0;
            } else {
                oldStyle = old.getStyle();
            }

            int fake = oldStyle & ~tf.getStyle();
            if ((fake & Typeface.BOLD) != 0) {
                paint.setFakeBoldText(true);
            }

            if ((fake & Typeface.ITALIC) != 0) {
                paint.setTextSkewX(-0.25f);
            }

            paint.setTypeface(tf);
        }
    }
}