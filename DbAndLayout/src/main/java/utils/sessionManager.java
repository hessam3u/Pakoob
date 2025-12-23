package utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.HashMap;

import bo.entity.PersonalInfoDTO;

public class sessionManager {
    private static final String PREF_NAME = "PakoobPref";

    private static final String k_FilterClubNameIds = "FilterClubNameIds";
    private static final String k_FilterTourLengths = "FilterTourLengths";
    private static final String k_FilterCategoryIds = "FilterCategoryIds";
    private static final String k_VisitCounter = "k_VisitCounter";
    private static final String k_FirebaseRegId = "k_FirebaseRegId";
    private static final String k_TempDeviceId = "TempDeviceId";
    private static final String k_CCustomer = "k_CCustomer";
    private static final String k_session = "session";
    private static final String k_LastMapType = "k_LastMapType";
    private static final String k_LastLockRotate = "k_LastLockRotate";
    private static final String k_LastAproxLocation = "k_LastAproxLocation";
    private static final String k_LastAproxLocationFixTime = "k_LastAproxLocationFixTime";
    private static final String k_LastAproxLocationFixType = "k_LastAproxLocationFixType";
    private static final String k_LastZoom = "k_LastZoom";
    private static final String k_NorthReference = "k_NorthReference";
    private static final String k_PositionFormat = "k_PositionFormat";
    public static String k_RecordingPanelActive = "RecordingPanelActive";
    public static String k_IsTrackRecording = "IsTrackRecording";
    public static String k_OpenHomeAtStartup = "OpenHomeAtStartup";
    public static String k_WebApiAddress = "WebApiAddress";
    public static String k_HomeHelpSeen = "HomeHelpSeen";
    public static String k_MapHelpSeen = "MapHelpSeen";


    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    // Shared Preferences
    SharedPreferences pref;
    // Editor for Shared preferences
    SharedPreferences.Editor editor;
    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;
    // Constructor
    public sessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

        //برای بازیابی سشن
        getSession();

    }

    /**
     * Create login session
     * */
    public void createLoginSession(String name, String email){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        editor.putString(KEY_NAME, name);

        // Storing email in pref
        editor.putString(KEY_EMAIL, email);

        // commit changes
        editor.commit();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
//        // Check login status
//        if(!this.isLoggedIn()){
//            // user is not logged in redirect him to Login Activity
//            Intent i = new Intent(_context, LoginActivity.class);
//            // Closing all the Activities
//            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//            // Add new Flag to start new Activity
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//            // Staring Login Activity
//            _context.startActivity(i);
//        }

    }



    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));

        // user email id
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        // return user
        return user;
    }

    /**
     * Clear session details
     * */
    public void logoutUser(MainActivityManager current){
        hutilities.CCustomerId = 0;
        editor.clear();
        editor.commit();
        current.logOutUser(current);
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return hutilities.CCustomerId != 0;
    }

    public int getVisitCounter(){
        int res = pref.getInt(k_VisitCounter, 0);
        return res;
    }
    public void setVisitCounter(int counter){
        editor.putInt(k_VisitCounter, counter);
        editor.commit();
    }
    public boolean getHomeHelpSeen(){
        boolean res = pref.getBoolean(k_HomeHelpSeen, false);
        return res;
    }
    public void setHomeHelpSeen(boolean value){
        editor.putBoolean(k_HomeHelpSeen, value);
        editor.commit();
    }
    public boolean getMapHelpSeen(){
        boolean res = pref.getBoolean(k_MapHelpSeen, false);
        return res;
    }
    public void setMapHelpSeen(boolean value){
        editor.putBoolean(k_MapHelpSeen, value);
        editor.commit();
    }
    public String getFirebaseRegId(){
        String res = pref.getString(k_FirebaseRegId, "");
        return res;
    }
    public void setFirebaseRegId(String value){
        editor.putString(k_FirebaseRegId, value);
        editor.commit();
    }
    public String getWebApiAddress(){
        String res = pref.getString(k_WebApiAddress, PrjConfig.WebApiAddress);
        return res;
    }
    public void setWebApiAddress(String value){
        editor.putString(k_WebApiAddress, value);
        editor.commit();
    }
    //------------------------------- END Club Tour Filters----------------------------------------.
    public String getTempDeviceId(){
        String res = pref.getString(k_TempDeviceId, "");
        if (res == ""){
            res = java.util.UUID.randomUUID().toString();
            setTempDeviceId(res);
        }
        return res;
    }
    public void setTempDeviceId(String sort){
        editor.putString(k_TempDeviceId, sort);
        editor.commit();
    }
    public String getSession(){
        String res = pref.getString(k_session, "");
        hutilities.Session = res;
        return res;
    }
    public void setSession(String value){
        hutilities.Session = value;
        editor.putString(k_session, value);
        editor.commit();
    }
    public int getLastMapType(){
        int res = pref.getInt(k_LastMapType, 4);
        return res;
    }
    public void setLastMapType(int value){
        editor.putInt(k_LastMapType, value);
        editor.commit();
    }
    public boolean getLastLockRotate(){
        boolean res = pref.getBoolean(k_LastLockRotate, true && !("google_sdk".equals(Build.MODEL)));
        return res;
    }
    public void setLastLockRotate(boolean value){
        editor.putBoolean(k_LastLockRotate, value);
        editor.commit();
    }


    public static LatLng emptyLocation = new LatLng(35.954652, 52.110038);
    public static float emptyZoom = 14f;
    public LatLng getLastAproxLocation(){
        String st = pref.getString(k_LastAproxLocation, "");
        if (st.equals(""))
            return emptyLocation;
        int index = st.lastIndexOf(',');
        if (index == -1)
            return emptyLocation;
        try {
            double lat = Double.valueOf(st.substring(0, index));
            double lon = Double.valueOf(st.substring(index + 1));
            return new LatLng(lat, lon);
        } catch (Exception e) {
            return emptyLocation;
        }
    }
    public void setLastAproxLocation(LatLng value){
        String resToSave = Double.toString(value.latitude) + "," + Double.toString(value.longitude);
        editor.putString(k_LastAproxLocation, resToSave);
        editor.commit();
    }
    public void setLastAproxLocationFixTime(String value){
        String resToSave = value;
        editor.putString(k_LastAproxLocationFixTime, resToSave);
        editor.commit();
    }
    public String getLastAproxLocationFixTime(){
        String resToSave = pref.getString(k_LastAproxLocationFixTime, "");
        return resToSave;
    }
    public void setLastAproxLocationFixType(byte value){
        editor.putInt(k_LastAproxLocationFixType, value);
        editor.commit();
    }
    public byte getLastAproxLocationFixType(){
        int resToSave = pref.getInt(k_LastAproxLocationFixType, 1);
        return (byte)resToSave;
    }

    public float getLastZoom(){
        float res = pref.getFloat(k_LastZoom, emptyZoom);
        return res;
    }
    public void setLastZoom(float value){
        editor.putFloat(k_LastZoom, value);
        editor.commit();
    }
    public int getPositionFormat(int Default_Value){
        int res = pref.getInt(k_PositionFormat, Default_Value);
        return res;
    }
    public void setPositionFormat(int value){
        editor.putInt(k_PositionFormat, value);
        editor.commit();
    }

    public int getNorthReference(int Default_Value){
        int res = pref.getInt(k_NorthReference, Default_Value);
        return res;
    }
    public void setNorthReference(int value){
        editor.putInt(k_NorthReference, value);
        editor.commit();
    }

    public int getRecordingPanelActive(){
        int res = pref.getInt(k_RecordingPanelActive, 2);
        return res;
    }
    public void setRecordingPanelActive(int value){
        editor.putInt(k_RecordingPanelActive, value);
        editor.commit();
    }

    public int getIsTrackRecording(){
        int res = pref.getInt(k_IsTrackRecording, 2);
        return res;
    }
    public void setIsTrackRecording(int value){
        editor.putInt(k_IsTrackRecording, value);
        editor.commit();
    }

    public PersonalInfoDTO getCCustomer(){
        Gson gson = new Gson();
        String json = pref.getString(k_CCustomer, "");

        Log.e("APP",  "CC From JSON : " + String.valueOf(json));
        PersonalInfoDTO res;
        if (json.equals("")) {
            res = new PersonalInfoDTO();
        }
        else {
            res = gson.fromJson(json, PersonalInfoDTO.class);
        }
        hutilities.CCustomerId = res.CCustomerId;
        return res;
    }
    public void setCCustomer(PersonalInfoDTO info){
        hutilities.CCustomerId = info.CCustomerId;
        Gson gson = new Gson();
        String json = gson.toJson(info);
        editor.putString(k_CCustomer, json);
        editor.commit();
    }

    public int getOpenHomeAtStartup(){
        int res = pref.getInt(k_OpenHomeAtStartup, 1);
        return res;
    }
    public void setOpenHomeAtStartup(int value){
        editor.putInt(k_OpenHomeAtStartup, value);
        editor.commit();
    }
    public Boolean getAsked_notification_permission(){
        Boolean res = pref.getBoolean("asked_notification_permission", false);
        return res;
    }
    public void setAsked_notification_permission(Boolean value){
        editor.putBoolean("asked_notification_permission", value);
        editor.commit();
    }


    public String getNextNbPoiName(){
        int res = pref.getInt("NbPoiName", 1);
        if (res == 10000)
            res = 1;

        setLastNbPoiName(res + 1);
        return String.format("%04d", res);
    }
    public void setLastNbPoiName(int value){
        editor.putInt("NbPoiName", value);
        editor.commit();
    }

//    public List<> getOpenHomeAtStartup(){
//        String res = pref.getString(k_OpenHomeAtStartup, "");
//        if (res == ""){
//            res = java.util.UUID.randomUUID().toString();
//            setTempDeviceId(res);
//        }
//        return res;
//    }
//    public void setExpiredMaps(String sort){
//        editor.putString(k_ExpiredMaps, sort);
//        editor.commit();
//    }
}

