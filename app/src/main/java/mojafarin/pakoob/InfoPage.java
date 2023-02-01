package mojafarin.pakoob;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import utils.hutilities;
import utils.projectStatics;

public class InfoPage extends Fragment {
    String pageKey = "";
    public InfoPage(){

    }
    public InfoPage(String _pageKey){
        this.pageKey = _pageKey;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {//4th Event
        super.onViewCreated(view, savedInstanceState);

        initializeComponents(view);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {//2nd Event
        super.onCreate(savedInstanceState);
//        Intent it = getIntent();
//        Bundle bundle = it.getExtras();
//        pageKey = bundle.containsKey("pageKey")?bundle.getString("pageKey"):"";
    }

    TextView txtPageTitle, btnBack;
    void initializeComponents(View v) {

        btnBack =v.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(view -> {((AppCompatActivity) context).onBackPressed();});
        txtPageTitle = v.findViewById(R.id.txtPageTitle);
        txtPageTitle.setText(context.getResources().getString(R.string.title_settings));

        //force RTL
        hutilities.forceRTLIfSupported((AppCompatActivity)context);

        if (pageKey.equals("help") || pageKey.equals("")){
            txtPageTitle.setText(context.getResources().getString(R.string.menu_help));
        }
        else if (pageKey.equals("contactUs")){
            TextView txtCurrentVersion = v.findViewById(R.id.txtCurrentVersion);
            txtCurrentVersion.setText(BuildConfig.VERSION_NAME);
            txtPageTitle.setText(context.getResources().getString(R.string.title_contactus));
        }
        else if (pageKey.equals("privacypolicy")){
            txtPageTitle.setText(context.getResources().getString(R.string.title_privacypolicy));
        }

    }
    Context context;
    @Override
    public void onAttach(Context _context) { //1st Event
        super.onAttach(context);
        this.context = _context;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {//3nd Event
        if (pageKey.equals("help") || pageKey.equals("")){
            return inflater.inflate(R.layout.frm_helpfaq, parent, false);
        }
        else if (pageKey.equals("contactUs")){
            //setContentView(R.layout.frm_contactus);
            //setTitle(R.string.title_contactus);
            return inflater.inflate(R.layout.frm_contactus, parent, false);
        }
        else if (pageKey.equals("privacypolicy")){
//            setContentView(R.layout.frm_privacypolicy);
//            setTitle(R.string.title_privacypolicy);
            return inflater.inflate(R.layout.frm_privacypolicy, parent, false);
        }
        return null;

    }
}
