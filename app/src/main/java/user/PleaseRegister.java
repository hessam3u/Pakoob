package user;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.io.File;

import mojafarin.pakoob.app;
import utils.PrjConfig;
import mojafarin.pakoob.Home;
import mojafarin.pakoob.MainActivity;
import mojafarin.pakoob.R;

public class PleaseRegister extends Fragment {
    Button btnSkip, btnLogin;

    public PleaseRegister(){

    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {//4th Event
        super.onViewCreated(view, savedInstanceState);
        //MainActivity.initFiltersAsync();

        initializeComponents(view);
    }

    private void initializeComponents(View v) {
        //btnSkip = findViewById(R.id.btnSkip);
        btnLogin = v.findViewById(R.id.btnLogin);

        //        btnSkip.setOnClickListener(view -> {
//            gotoMainActivity();
//        });

        btnLogin.setOnClickListener(view -> {
            gotoMainActivity();
//
//            Intent intent = new Intent(this, Register.class);
//            intent.putExtra("mode", "start");
//            startActivity(intent);
//            finish();
        });
    }
    public boolean onBackPressed() {
        if (!app.session.isLoggedIn())
            return false;
        return true;
    }
    public void gotoMainActivity(){
        if (!app.session.isLoggedIn()){
            //IMPORTANT : *****************
            //ALSO change BackPressed in MainActivity to disable back in this page and Above function
            //((MainActivity) context).backToMapPage();
            ((MainActivity) context).showFragment(new Register("start"));
        }
        else{
            //((MainActivity) context).backToMapPage();
//            ((MainActivity) context).showFragment(new Home());
            ((MainActivity) context).backToHome();
        }

    }
    Context context;
    @Override
    public void onAttach(Context _context) { //1st Event
        super.onAttach(context);
        this.context = _context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {//2nd Event
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {//3nd Event
        return inflater.inflate(R.layout.frm_user_pleaseregister, parent, false);
    }
}
