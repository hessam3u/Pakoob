package UI;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.IntegerRes;
import androidx.fragment.app.Fragment;

import java.io.PrintWriter;
import java.io.StringWriter;

import bo.dbConstantsMap;
import bo.entity.NbScreenTime;
import bo.sqlite.NbScreenTimeDao;
import utils.MainActivityManager;

public abstract class HFragment extends Fragment {

    public MainActivityManager context;
    public boolean initCompleted = false;

    //دو تا متغیر مربوط به شمارنده
    private long visibleStartTime = 0;
    private boolean isVisibleToUser = false;

    protected abstract int getScreenId();
    protected abstract String tag();

    public void initializeComponents(View v){
        initCompleted = true;
    }
    //Need to Call in onBackPressed() of an Activity
    public boolean onBackPressedInChild(){ return true; }
    @Override
    public void onAttach(Context _context) { //1st Event
        super.onAttach(_context);
        this.context = (MainActivityManager) _context;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {//2nd Event
        super.onCreate(savedInstanceState);
    }
    //Call it after Fragment Commit
    public void onFragmentShown(){
        if (!isVisibleToUser) {
            visibleStartTime = System.currentTimeMillis();
            isVisibleToUser = true;
        }
    }
    public void onFragmentHided(){
        if (isVisibleToUser) {
            isVisibleToUser = false;
            onScreenTimbeCalculated();
        }
    }
    public void onFragmentRemoved(){}


    public int dimen(@DimenRes int resId) {
        return (int) getResources().getDimension(resId);
    }

    public int color(@ColorRes int resId) {
        return getResources().getColor(resId);
    }

    public int integer(@IntegerRes int resId) {
        return getResources().getInteger(resId);
    }

    public static String stktrc2k(Exception ex){
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        String exceptionAsString = sw.toString();
        return exceptionAsString.length() < 2000?exceptionAsString:exceptionAsString.substring(0, 2000);
    }
    public static String stktrc2kt(Throwable ex){
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        String exceptionAsString = sw.toString();
        return exceptionAsString.length() < 2000?exceptionAsString:exceptionAsString.substring(0, 2000);
    }

    protected void onScreenTimbeCalculated() {
        long cTime =System.currentTimeMillis();
        long durationMillis =  cTime - visibleStartTime;

        Log.d("ScreenTime",
                getScreenId() + " -> " + (durationMillis / 1000) + " sec");

        NbScreenTime scr = NbScreenTime.getInstance(getScreenId(), "", visibleStartTime, cTime);
        dbConstantsMap.appDB.NbScreenTimeDao().insert(scr);
    }
}
