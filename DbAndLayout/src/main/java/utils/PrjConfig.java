package utils;

public class PrjConfig {

//    public static final boolean IsDebugMode = true;
    public static final boolean IsDebugMode = false;
//        public static String WebApiAddress = "http://192.168.1.106/NaghshebazWeb";//http://10.0.2.2:52331/api1/tara-
    public static String WebApiAddress = "https://pakoob24.ir";
    public static final int frmMainActivity = 1;
    public static final int frmMapSelect = 2;
    public static final int frmMyTracks = 30;
    public static final int frmEditTrack = 40;
    public static final int frmClubSearch = 50;
    public static final int frmClubView_Home = 60;

    public static final int frmTourShowOne = 70;
    public static final int frmFmMessageStory = 80;
    public static final int frmSideList = 90;
    public static final int frm_FunctionSendProblem = 100;
    public static final int frm_FunctionRecFCM = 101;
    public static final int frm_SelectClub= 102;
    public static final int frm_Component_TourListHorizontal= 103;
    public static final int frm_Component_TourListVer= 104;
    public static final int frmMapPage = 105;
    public static final int frmHome = 106;
    public static final int frmDialogMapBuilder = 107;
    public static final int frm_SelectCityDialog = 108;
    public static final int frm_SearchOnMap = 109;
    public static final int app = 110;
    public static final int frmWeatherShow = 111;
    public static final int frmSafeGpxSearch = 112;
    public static final int frmSafeGpxView = 113;
    public static final int frmTourList = 114;
    public static final int frmTripComputer = 115;
    public static final int frmTrackRecording = 116;
    public static final int frmGPXFile = 117;
    public static final int frmInfoBottomOfMapPage = 118;
    public static final int frm_PleaseRegister = 119;
    public static final int frmMapPage_SearchText = 120;


    public static final String MessageSplitor = "*?*/*?/";
    public static final int PROBLEM_CODE_ConfirmSMS = 10;


    //کدهای پیام رسان سیستمی
    public static final String SYSTEM_CODE_CHANGESERVER = "10";


    public static final String PrivateFolder = "private";
    public static final String AdvFolder = "adv";

    // global topic to receive app wide push notifications
    //Default Message Senders

    public static final int     FCMSender_TOPIC_GLOBAL = 1;
    public static final String TOPIC_GLOBAL = "global";
    public static final String  FCMSender_TOPIC_GLOBAL_Name = "پیام رسان پاکوب";
    public static final int     FCMSender_TourDispacher = 2;
    public static final String TOPIC_GlobalTours = "gTours";
    public static final String  FCMSender_TourDispacher_Name = "اعلام برنامه";
    public static final int     FCMSender_AdvDispacher = 3;
    public static final String TOPIC_Adv = "TOPIC_Adv";
    public static final String  FCMSender_AdvDispacher_Name = "پیشنهادات ویژه";
    public static final int     FCMSender_SystemDispacher = 4;
    public static final String TOPIC_SYSTEM = "TOPIC_SYSTEM";
    public static final String  FCMSender_SystemDispacher_Name = "اپلیکیشن پاکوب";


    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "ah_firebase";


    public static final String GROUP_PAKOOB_GLOBAL = "mojafarin.pakoob.global";

    public static final String NOTIF_CHANAL_SYSTEM = "Pakoob_System";
    public static final String NOTIF_CHANAL_PublicMessage = "Pakoob_PublicMessage";
    public static final String NOTIF_CHANAL_PrivateMessage = "Pakoob_PrivateMessage";
    public static final String NOTIF_CHANAL_ADV = "Pakoob_ADV";


    public static final String  NOTIF_CHANAL_ADV_ID = "100";
    public static final String NOTIF_CHANAL_SYSTEM_ID = "101";
    public static final String NOTIF_CHANAL_PublicMessage_ID = "102";
    public static final String NOTIF_CHANAL_PrivateMessage_ID = "103";
    public static final String NOTIF_CHANAL_RECORDING_TRACK_ID = "500";


    public static final int Location_FINE_PERMISSION_REQUEST_CODE = 200; // New Record Track
    public static final int POST_NOTIFICATIONS_REQUEST_CODE = 201; // New Record Track
    public static final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 12039;
    public static final int MY_PERMISSIONS_READ_EXTERNAL_STORAGE = 12038;
    public static final int Location_BACKGROUND_PERMISSION_REQUEST_CODE = 21562;


}