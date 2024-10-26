package user;

import static utils.HFragment.stktrc2k;

import android.app.Activity;
import android.app.AlertDialog;
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

import com.google.android.gms.maps.Projection;

import java.io.File;

import bo.sqlite.TTExceptionLogSQLite;
import mojafarin.pakoob.app;
import utils.PrjConfig;
import mojafarin.pakoob.Home;
import mojafarin.pakoob.MainActivity;
import mojafarin.pakoob.R;
import utils.projectStatics;

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
        try {
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
        catch (Exception ex){
            TTExceptionLogSQLite.insert(ex.getMessage(), stktrc2k(ex), PrjConfig.frm_PleaseRegister, 100);
            Log.d("بازکردن", "Bind Ver Tour: " + ex.getMessage() + ex.getStackTrace());
            ex.printStackTrace();
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
