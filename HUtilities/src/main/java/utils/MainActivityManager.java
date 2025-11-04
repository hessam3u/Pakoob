package utils;

import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import bo.entity.FmMessage;

public class MainActivityManager extends AppCompatActivity{
    public String Tag = "اکتیویتی_اصلی";
    public void backToHome() {
    }
    public void showFragment(Fragment mFragment){

    }
    //برای نمایش مجدد دادن فرگمنت هایی که حالت پاپ آپ دارن استفاده میشه
    public void ShowChidFragmentAgain(Fragment mFragment){

    }
    //برای مخفی کردن فرگمنت هایی که حالت پاپ آپ دارن استفاده میشه
    public void HideChildFragment(Fragment mFragment){

    }
    public void showFragment(Fragment mFragment, boolean closeCurrent){

    }
    public void logOutUser(MainActivityManager current){

    }
    public void showLoginProcess(String mode){

    }
    public void OpenFmMessageCommand(FmMessage currentObj){

    }
    public void OpenInAppCommand(String command){

    }
    public void changeFragmentVisibility(Fragment fragment, boolean makeVisible) {
    }
}
