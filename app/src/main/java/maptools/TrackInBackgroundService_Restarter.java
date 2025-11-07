package maptools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import androidx.core.content.ContextCompat;

public class TrackInBackgroundService_Restarter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("TrackService_Restarter", "شروع مجدد Service tried to stop, restarting...");

        try {
            Intent serviceIntent = new Intent(context, LocationTrackingService.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14+
                // از ContextCompat استفاده کن تا رفتار ایمن‌تر و با permission چک انجام شه
                ContextCompat.startForegroundService(context, serviceIntent);
            }
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // Android 8 - 13
                context.startForegroundService(serviceIntent);
            }
            else { // قدیمی‌تر از Android 8
                context.startService(serviceIntent);
            }

        } catch (Exception e) {
            Log.e("TrackService_Restarter", "Failed to restart service: " + e.getMessage());
        }
    }
}