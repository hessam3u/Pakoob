package utils;

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
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class HFragment extends Fragment {

    public MainActivityManager context;
    public boolean initCompleted = false;
    public String Tag = "تگ_تست";

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
    public void onFragmentShown(){}
    public void onFragmentHided(){}
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
}
