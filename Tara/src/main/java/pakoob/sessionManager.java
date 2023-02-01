package pakoob;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bo.dbConstantsTara;
import bo.entity.PersonalInfoDTO;
import bo.entity.TTClubTour;

public class sessionManager {
    private static final String PREF_NAME = "PakoobPref";

    private static final String k_myClubNameIds = "MyClubNameIds";
    private static final String k_myClubName = "myClubName";
    private static final String k_myClubLogo = "myClubLogo";
    private static final String k_FilterClubNameIds = "FilterClubNameIds";
    private static final String k_FilterCityIds = "FilterCityIds";
    private static final String k_FilterTourLengths = "FilterTourLengths";
    private static final String k_FilterCategoryIds = "FilterCategoryIds";
    private static final String k_Sort_of_tour = "k_Sort_of_tour";
    private static final String k_LastPublishRead = "k_LastPublishRead11";
    private static final String k_FirebaseRegId = "k_FirebaseRegId";
    private static final String k_TempDeviceId = "TempDeviceId";
    private static final String k_CCustomer = "k_CCustomer";
    private static final String k_session = "session";
    private static final String k_LastFilterUpdate = "LastFilterUpdate";




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
    public void logoutUser(Activity current){
        editor.clear();
        editor.commit();

        //1400-01-10 Commented becaus
//        // After logout redirect user to Loing Activity
//        Intent i = new Intent(_context, PleaseRegister.class);
//        // Closing all the Activities
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        // Add new Flag to start new Activity
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        if (current != null)
//            current.finish();
//        // Staring Login Activity
//        _context.startActivity(i);
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }


    //------------------------------- Start of Club Tour Filters----------------------------------------
    public ArrayList<Integer> get_FilterCategoryIds(){
        ArrayList<Integer> res = new ArrayList<Integer>();
        String str = pref.getString(k_FilterCategoryIds, null);
        if (str == null || str.length() == 0) return res;
        String[] arrOfStr = str.split("@");
        for (int i = 0;i < arrOfStr.length;i++) res.add(Integer.parseInt(arrOfStr[i]));
        return res;
    }
    public void set_FilterCategoryIds(ArrayList<Integer> list){
        String res = "";
        for (int i = 0;i < list.size();i++) {res += list.get(i) + "@";}
        editor.putString(k_FilterCategoryIds, res);
        editor.commit();
    }

    public ArrayList<Integer> get_FilterTourLengths(){
        ArrayList<Integer> res = new ArrayList<Integer>();
        String str = pref.getString(k_FilterTourLengths, null);
        if (str == null || str.length() == 0) return res;
        String[] arrOfStr = str.split("@");
        for (int i = 0;i < arrOfStr.length;i++) res.add(Integer.parseInt(arrOfStr[i]));
        return res;
    }
    public void set_FilterTourLengths(ArrayList<Integer> list){
        String res = "";
        for (int i = 0;i < list.size();i++) {res += list.get(i) + "@";}
        editor.putString(k_FilterTourLengths, res);
        editor.commit();
    }

    public ArrayList<Integer> get_FilterClubNameIds(){
        ArrayList<Integer> res = new ArrayList<Integer>();
        String str = pref.getString(k_FilterClubNameIds, null);
        if (str == null || str.length() == 0) return res;
        String[] arrOfStr = str.split("@");
        for (int i = 0;i < arrOfStr.length;i++) res.add(Integer.parseInt(arrOfStr[i]));
        return res;
    }
    public void set_FilterClubNameIds(ArrayList<Integer> list){
        String res = "";
        for (int i = 0;i < list.size();i++) {res += list.get(i) + "@";}
        editor.putString(k_FilterClubNameIds, res);
        editor.commit();
    }

    public ArrayList<Integer> get_FilterCityIds(){
        ArrayList<Integer> res = new ArrayList<Integer>();
        String str = pref.getString(k_FilterCityIds, null);
        if (str == null || str.length() == 0) return res;
        String[] arrOfStr = str.split("@");
        for (int i = 0;i < arrOfStr.length;i++) res.add(Integer.parseInt(arrOfStr[i]));
        return res;
    }
    public void set_FilterCityIds(ArrayList<Integer> list){
        String res = "";
        for (int i = 0;i < list.size();i++) {res += list.get(i) + "@";}
        editor.putString(k_FilterCityIds, res);
        editor.commit();
    }

    public Integer getMyClubNameIds(){
        Integer res = pref.getInt(k_myClubNameIds, 0);
        return res;
    }
    public void setMyClubNameIds(Integer value){
        editor.putInt(k_myClubNameIds, value);
        editor.commit();
    }
    public String getMyClubName(){
        String res = pref.getString(k_myClubName, "");
        return res;
    }
    public void setMyClubName(String name){
        editor.putString(k_myClubName, name);
        editor.commit();
    }
    public String getMyClubLogo(){
        String res = pref.getString(k_myClubLogo, "");
        return res;
    }
    public void setMyClubLogo(String name){
        editor.putString(k_myClubLogo, name);
        editor.commit();
    }
    public String getSort_of_tour(){
        String res = pref.getString(k_Sort_of_tour, "TTClubtourId desc");
        return res;
    }
    public void setSort_of_tour(String sort){
        editor.putString(k_Sort_of_tour, sort);
        editor.commit();
    }
    public String getLastPublishRead(){
        String res = pref.getString(k_LastPublishRead, "");
        return res;
    }
    public void setLastPublishRead(String id){
        editor.putString(k_LastPublishRead, id);
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
    public void setLastPublishRead_FromRecievedTours( List<TTClubTour> tours){
        String max = getLastPublishRead();
        for (int i = 0; i < tours.size(); i++) {
            if (tours.get(i).PublishDate.compareTo(max) > 0)
                max = tours.get(i).PublishDate;
        }
        setLastPublishRead(max);
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
//    public String getSession(){
//        String res = pref.getString(k_session, "");
//        return res;
//    }
//    public void setSession(String value){
//        editor.putString(k_session, value);
//        editor.commit();
//    }
    public String getLastFilterUpdate(){
        String res = pref.getString(k_LastFilterUpdate, "");
        return res;
    }
    public void setLastFilterUpdate(String value){
        editor.putString(k_LastFilterUpdate, value);
        editor.commit();
    }

    public PersonalInfoDTO getCCustomer(){
        Gson gson = new Gson();
        String json = pref.getString(k_CCustomer, "");
        if (json.equals(""))
            return new PersonalInfoDTO();
        return gson.fromJson(json, PersonalInfoDTO.class);
    }
    public void setCCustomer(PersonalInfoDTO info){
        Gson gson = new Gson();
        String json = gson.toJson(info);
        editor.putString(k_CCustomer, json);
        editor.commit();
    }
}

