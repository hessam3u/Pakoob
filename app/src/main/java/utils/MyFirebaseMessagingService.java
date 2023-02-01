package utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import bo.dbConstantsTara;
import bo.entity.FmMessage;
import bo.sqlite.FmMessageDao;
import bo.sqlite.FmMessageSQLite;
import bo.sqlite.TTExceptionLogSQLite;
import mojafarin.pakoob.MainActivity;
import mojafarin.pakoob.app;

/**
 * Created by Ravi Tamada on 08/08/16.
 * www.androidhive.info
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "Firebase";

    private NotificationUtils notificationUtils;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            //HHH
            handleNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());

                FmMessage data = new FmMessage(json);//new Gson().fromJson(remoteMessage.getData().values().toArray()[0].toString(), FmToS.class);
                //HHH فعلا نمیخوایم چیزی توی دیتابیس ذخیره شه FmMessageSQLite.saveRecievedMessage(data);
                if (data.FmMessageType != FmMessage.FmMessageType_System){
                    showNotificationForFmToS(data);
                }
                else{
                    //Process System Messages
                    List<String> parts = new ArrayList<>();
                    int index = 0;
                    int index2 = 0;
                    for (int i = 0; i < 10; i++) {
                        index2 = data.Text1.indexOf(PrjConfig.MessageSplitor, index);
                        if (index2 == -1) {
                            if (data.Text1.length() - index > 0)
                                parts.add(data.Text1.substring(index));
                            break;
                        }
                        parts.add(data.Text1.substring(index, index2 - index));
                        index = index2+PrjConfig.MessageSplitor.length();
                    }
                    //String [] parts = data.Text1.split(PrjConfig.MessageSplitor);
                    if (parts.size() > 0){
                        if (parts.get(0).equals(PrjConfig.SYSTEM_CODE_CHANGESERVER)){
                            if (parts.get(1).length() > 0){
                                app.session.setWebApiAddress(parts.get(1));
                                PrjConfig.WebApiAddress = parts.get(1);
                                Log.e("پ سیستمی", "CS:" + parts.get(1));
                            }
                        }
                    }
                }
                //HHH orginal Sample:
//                JSONObject json = new JSONObject(remoteMessage.getData().toString());
//                handleDataMessage_Sample(json);
            } catch (Exception ex) {
                TTExceptionLogSQLite.insert(ex.getMessage(), "", PrjConfig.frm_FunctionRecFCM, 100);
                Log.e(TAG, "Exception: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    public void handleFmToS(FmMessage rec){
        //1 - save in db if needed
        //2 - show notification if needed
    }

    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(PrjConfig.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            //notificationUtils.playNotificationSound();
        }else{
            // If the app is in background, firebase itself handles the notification
        }
    }

    private void showNotificationForFmToS(FmMessage rec) {
        Log.e(TAG, "Rec FmToS: " + rec.toString());
        String ChanalId = PrjConfig.NOTIF_CHANAL_SYSTEM_ID;
        try {

            String title = "پیام جدید";
            if (rec.ForecedTitle != null && rec.ForecedTitle.length() > 0)
                title = rec.ForecedTitle;
            String message = rec.Text1;
            //boolean isBackground = data.getBoolean("is_background");
            String imageUrl = "";//data.getString("image");
            String timestamp = rec.SendDate;

            if (false && !NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(PrjConfig.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            } else {

                // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);

                switch (rec.ContentCat){
                    case ContentCats.PublicMessage:
                        ChanalId = PrjConfig.NOTIF_CHANAL_PublicMessage_ID;
                        break;
                    case ContentCats.PrivateMessage:
                        ChanalId = PrjConfig.NOTIF_CHANAL_PrivateMessage_ID;
                        break;
                    case ContentCats.System:
                        ChanalId = PrjConfig.NOTIF_CHANAL_SYSTEM_ID;
                        break;
                    case ContentCats.Adv:
                        ChanalId = PrjConfig.NOTIF_CHANAL_ADV_ID;
                        break;
                }
//                resultIntent.putExtra("message", message); //commented at 1399-09-09
                resultIntent.putExtra("NotificationMessage", message);
                resultIntent.putExtra("NotificationTitle", title);
                resultIntent.putExtra("ChanalId", ChanalId);
                Gson gson = new Gson();
                resultIntent.putExtra("FmMessage", gson.toJson(rec, FmMessage.class));

                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent, ChanalId);
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl, ChanalId);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    private void handleDataMessage_Sample(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());

        String ChanalId = PrjConfig.NOTIF_CHANAL_SYSTEM;
        try {
            JSONObject data = json.getJSONObject("data");

            String title = data.getString("title");
            String message = data.getString("message");
            boolean isBackground = data.getBoolean("is_background");
            String imageUrl = "";//data.getString("image");
            String timestamp = data.getString("timestamp");
            JSONObject payload = data.getJSONObject("payload");

            Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);
            Log.e(TAG, "isBackground: " + isBackground);
            Log.e(TAG, "payload: " + payload.toString());
            Log.e(TAG, "imageUrl: " + imageUrl);
            Log.e(TAG, "timestamp: " + timestamp);


            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(PrjConfig.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            } else {
                // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                resultIntent.putExtra("message", message);

                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent, ChanalId);
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl, ChanalId);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent, String ChanalId) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent,ChanalId);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl, String ChanalId) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl, ChanalId);
    }






    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        String refreshedToken = token;

        // Saving reg id to shared preferences
        app.session.setFirebaseRegId(refreshedToken);

        // sending reg id to your server
        sendRegistrationToServer(refreshedToken);

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(PrjConfig.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server
        Log.e(TAG, "sendRegistrationToServer: " + token);
    }


    public class RecTypes{
        public static final int None = 0;
        public static final int KarbareKhas = 1;
        public static final int ChanaleKhas = 2;
    }

    public class FmMessageTypes{
        public static final int None = 0;
        public static final int Karbar = 1;
        public static final int Group = 2;
        public static final int Chanal = 3;
        public static final int Service = 4;
        public static final int System = 5;
    }

    public class OpenActions{
        public static final int None = 0;
        public static final int DoNothing = 1;
        public static final int NormalOpen = 2;
        public static final int OpenInApp = 3;
        public static final int OpenParamLink = 4;
    }
    public class ContentTypes{
        public static final int None = 0;
        public static final int Text = 1;
        public static final int Html = 2;
    }
    public class ContentCats{
        public static final int None = 0;
        public static final int System = 1;
        public static final int PublicMessage = 2;
        public static final int PrivateMessage = 3;
        public static final int Adv = 4;
    }

}