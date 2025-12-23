package user;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import bo.sqlite.TTExceptionLogSQLite;
import mojafarin.pakoob.app;
import UI.HFragment;
import utils.PrjConfig;
import mojafarin.pakoob.MainActivity;
import mojafarin.pakoob.R;

public class PleaseRegister extends HFragment {
    Button btnSkip, btnLogin;

    public PleaseRegister(){

    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {//4th Event
        super.onViewCreated(view, savedInstanceState);
        //MainActivity.initFiltersAsync();

        initializeComponents(view);
    }

    @Override
    public void initializeComponents(View v) {
        btnSkip = v.findViewById(R.id.btnSkip);
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
        btnSkip.setOnClickListener(view -> {
            ((MainActivity) context).backToHome();
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

    //تنظیمات مربوط به صفحه --------------
    @Override
    protected int getScreenId() {return PrjConfig.frm_PleaseRegister;}
    @Override
    protected String tag() {return SCREEN_TAG;}
    public static final String SCREEN_TAG = "PleaseRegister";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {//3nd Event
        return inflater.inflate(R.layout.frm_user_pleaseregister, parent, false);
    }
}
