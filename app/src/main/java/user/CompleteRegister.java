package user;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import bo.PageIds_PA;
import bo.NewClasses.InsUpdRes;
import bo.entity.CityDTO;
import bo.entity.PersonalInfoDTO;
import bo.NewClasses.SimpleRequest;
import mojafarin.pakoob.R;
import mojafarin.pakoob.app;
import okhttp3.ResponseBody;
import pakoob.SelectCityDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import UI.HFragment;
import utils.MainActivityManager;
import utils.MyDate;
import utils.PersianDateDialog;
import utils.PrjConfig;
import utils.hutilities;
import utils.projectStatics;

public class CompleteRegister extends HFragment {
    PersonalInfoDTO pInfo;
    String mode = "hasback";//noback
    public CompleteRegister(String _mode){
        mode = _mode;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {//4th Event
        super.onViewCreated(view, savedInstanceState);

        initializeComponents(view);

        //dbConstantsTara.initFiltersAsync();
    }
    TextInputEditText txtBirthDate, txtName, txtFamily, txtNationalCode;
    //1400-04-11 Commented, Changed To Online mode
    AutoCompleteTextView txtCityId;
    RadioButton rbSexIsMan, rbSexIsWoman;
    Button btnSave, btnSkip;
    Button btnSignOut;
    LinearLayout divOtherButtons, liMoreInfo;
    TextInputLayout lay_txtName, lay_txtFamily, lay_txtNationalCode, lay_txtCityId;
    TextView lblTitle;
    ProgressBar loadingForDialog;
    TextView txtPageTitle, btnBack;

    String selectedCityName = "";
    int SelectedCityId = 0;

    @Override
    public void initializeComponents(View v) {
        txtPageTitle = v.findViewById(R.id.txtPageTitle);
        txtPageTitle.setText(context.getString(R.string.CompeleteProfile));
        btnBack = v.findViewById(R.id.btnBack);
        if (mode.equals("noback"))
            btnBack.setVisibility(View.INVISIBLE);
        else
            btnBack.setOnClickListener(view -> {hutilities.hideKeyboard((Activity)context);((MainActivityManager)context).onBackPressed();});

        btnSignOut = v.findViewById(R.id.btnSignOut);
        divOtherButtons = v.findViewById(R.id.divOtherButtons);
        liMoreInfo = v.findViewById(R.id.liMoreInfo);
        txtBirthDate = v.findViewById(R.id.txtBirthDate);
        lblTitle = v.findViewById(R.id.lblTitle);
        txtName = v.findViewById(R.id.txtName);
        txtFamily = v.findViewById(R.id.txtFamily);
        txtNationalCode = v.findViewById(R.id.txtNationalCode);
        txtCityId = v.findViewById(R.id.txtCityId);
        rbSexIsMan = v.findViewById(R.id.rbSexIsMan);
        rbSexIsWoman = v.findViewById(R.id.rbSexIsWoman);
        btnSkip = v.findViewById(R.id.btnSkip);
        btnSave = v.findViewById(R.id.btnSave);
        pInfo = app.session.getCCustomer();
        lay_txtName = v.findViewById(R.id.lay_txtName);
        lay_txtFamily = v.findViewById(R.id.lay_txtFamily);
        lay_txtNationalCode = v.findViewById(R.id.lay_txtNationalCode);
        lay_txtCityId = v.findViewById(R.id.lay_txtCityId);
        loadingForDialog = v.findViewById(R.id.loadingForDialog);

        //txtBirthDate.setFocusable(false);
        if (!mode.equals("noback")) {
            btnSkip.setText(context.getString(R.string.cancel));
            liMoreInfo.setVisibility(View.VISIBLE);
        }
        else{
            liMoreInfo.setVisibility(View.GONE);
        }

        btnSkip.setOnClickListener(view -> {
            hutilities.hideKeyboard(context);
            if (mode.equals("noback")){
                context.backToHome();
            }
            else {
                context.onBackPressed();
            }
        });

        if (!mode.equals("noback"))
            btnSave.setText(context.getString(R.string.save));
        btnSave.setOnClickListener(view -> {
            SaveAndExit();
        });

        txtName.setOnFocusChangeListener((view, hasFocus) -> {if (!hasFocus) validateNameAndFamily();});
        txtFamily.setOnFocusChangeListener((view, hasFocus) -> {if (!hasFocus) validateNameAndFamily();});
        txtNationalCode.setOnFocusChangeListener((view, hasFocus) -> {if (!hasFocus) validateNationalCode();});
        txtCityId.setText(pInfo.CityName);
        selectedCityName = pInfo.CityName;
        SelectedCityId = pInfo.CityId;

        txtName.setText(pInfo.Name);
        txtFamily.setText(pInfo.Family);
        txtNationalCode.setText(pInfo.NationalCode);
        txtBirthDate.setText(MyDate.persianStringFromInt(pInfo.BirthDate.intValue(), ""));
        if (pInfo.Sex == 1){
            rbSexIsWoman.setChecked(true);
        }
        else
            rbSexIsMan.setChecked(true);
        if (mode.equals("hasback") ||mode.equals("clubsearch") || mode.equals("clubview")) {
            divOtherButtons.setVisibility(View.VISIBLE);
        }
        btnSignOut.setOnClickListener(view -> {
            app.session.logoutUser(context);
        });

            //1400-04-11 Commented, Changed To Online mode
//        if (projectStatics.cities != null) {
//            ArrayList<String> cityNames = new ArrayList<>();
//            for (int i = 0; i < projectStatics.cities.size(); i++) {
//                cityNames.add(projectStatics.cities.get(i).Name);
//            }
//
//            CustomListAdapter_Offline adapter = new CustomListAdapter_Offline(context, android.R.layout.simple_dropdown_item_1line, cityNames);
//            txtCityId.setAdapter(adapter);
//        }
//        else {
//            //Mohemmmmm
//        }
        txtCityId.setFocusable(false);
        txtCityId.setOnClickListener(view -> {
            SelectCityDialog dialogBuilder = new SelectCityDialog(this);

            AlertDialog.Builder alertDialogBuilder = dialogBuilder.GetBuilder(context
                    ,(selected, position) ->{
                        CityDTO cityDTO = (CityDTO) selected;
                        txtCityId.setText(cityDTO.Name);
                        selectedCityName = cityDTO.Name;
                        SelectedCityId = cityDTO.CityId;
                        dialogBuilder.alertDialog.dismiss();

                    }, null, view2 -> {
                        dialogBuilder.alertDialog.dismiss();
                    });
            dialogBuilder.alertDialog = alertDialogBuilder.create();
            dialogBuilder.alertDialog.show();
        });
        txtBirthDate.setFocusable(false);
        txtBirthDate.setOnClickListener(view -> {
            int Year = 1370;
            int month = 5;
            int day = 15;
            String birthday = txtBirthDate.getText().toString();
            if (birthday.length() != 0){
                String[] parts = birthday.split("/");
                Year = Integer.valueOf(parts[0]);
                month = Integer.valueOf(parts[1]);
                day = Integer.valueOf(parts[2]);
            }
            PersianDateDialog dialogBuilder = new PersianDateDialog(R.layout.dialog_selectdate, Year, month, day);
            AlertDialog.Builder alertDialogBuilder = dialogBuilder.GetBuilder(context
                 , view1 -> {
                        txtBirthDate.setText(dialogBuilder.getSelectedDate());
                        dialogBuilder.alertDialog.dismiss();
                    }, view2 -> {
                        dialogBuilder.alertDialog.dismiss();
                    });
            dialogBuilder.lblTitle_Container.setVisibility(View.GONE);
            dialogBuilder.lblDesc.setVisibility(View.GONE);
            dialogBuilder.alertDialog = alertDialogBuilder.create();
            dialogBuilder.alertDialog.show();
        });


        super.initializeComponents(v);
    }
    int validateCity() {
        String city = txtCityId.getText().toString();
        lay_txtCityId.setErrorEnabled(false);
        if (city.length() == 0)
            return 0;
        int cityId = 0;
        if (projectStatics.cities != null) {
            for (int i = 0; i < projectStatics.cities.size() && cityId == 0; i++) {
                if (projectStatics.cities.get(i).Name.equals(city))
                    cityId = projectStatics.cities.get(i).CityId;
            }
        }
        if (cityId == 0){
            lay_txtCityId.setErrorEnabled(true);
            lay_txtCityId.setError("شهر انتخابی در لیست نمی باشد. لطفا یکی از شهرهای نزدیک را انتخاب کنید");
        }
        return cityId;
    }

    boolean validateNationalCode(){
        if (txtNationalCode.getText().length() == 0)
            return true;
        boolean valres = hutilities.validationNationalCode(txtNationalCode.getText().toString());
        if (valres) {
            lay_txtNationalCode.setErrorEnabled(false);
            return true;
        }
        lay_txtNationalCode.setError("کد ملی وارد شده معتبر نمی باشد");
        lay_txtNationalCode.setErrorEnabled(true);

        return false;
    }
    boolean validateNameAndFamily(){
        String name =txtName.getText().toString();
        if (name.equals("")){
            lay_txtName.setError("لطفا نام را وارد نمایید"); lay_txtName.setErrorEnabled(true); return false;
        }
        if (name.matches(".*\\d.*")) {
            lay_txtName.setError("نام نباید شامل اعداد باشد");
            lay_txtName.setErrorEnabled(true);
            return false;
        }
//        if (name.matches(".*[\\x00-\\x7F].*")) {
//            lay_txtName.setError("نام نباید شامل حروف انگلیسی باشد");
//            lay_txtName.setErrorEnabled(true);
//            return false;
//        }
        lay_txtName.setErrorEnabled(false);

        String family = txtFamily.getText().toString();
        if (family.equals("")){
            lay_txtFamily.setError("لطفا نام را وارد نمایید"); lay_txtFamily.setErrorEnabled(true); return false;
        }
        if (family.matches(".*\\d.*")) {
            lay_txtFamily.setError("نام خانوادگی نباید شامل اعداد باشد");
            lay_txtFamily.setErrorEnabled(true);
            return false;
        }
//        if (family.matches(".*[\\x00-\\x7F].*")) {
//            lay_txtFamily.setError("نام خانوادگی نباید شامل حروف انگلیسی باشد");
//            lay_txtFamily.setErrorEnabled(true);
//            return false;
//        }
        lay_txtFamily.setErrorEnabled(false);

        return true;
    }
    private void SaveAndExit(){
        if (!hutilities.isInternetConnected(context)) {
            projectStatics.showDialog(context, context.getResources().getString(R.string.NoInternet), context.getResources().getString(R.string.NoInternet_Desc), context.getResources().getString(R.string.ok), view -> {}, "", null);
            return;
        }
        //Validation

        boolean valRes;
        int cityId = 0;
        valRes = validateNameAndFamily() || validateNationalCode() ;

        //1400-04-11 Commented because of Dialog of City
//        if (valRes)
//            cityId = validateCity();
        cityId = SelectedCityId;

        if (!valRes)
            return;

        String birthDate = txtBirthDate.getText().toString();
        if (birthDate.length() > 0)
            pInfo.BirthDate = MyDate.intFromPersianString(birthDate, 0);
        pInfo.Name = txtName.getText().toString();
        pInfo.Family = txtFamily.getText().toString();
        pInfo.NationalCode = txtNationalCode.getText().toString();
        pInfo.CityId  = cityId;
        pInfo.CityName = selectedCityName;
        pInfo.Sex = (byte)(rbSexIsMan.isChecked()? 2 : 1);

        hutilities.showHideLoading(true, loadingForDialog, (Activity)context);

        SimpleRequest request = SimpleRequest.getInstance(pInfo);

        Call<ResponseBody> call = app.apiMap.UpdatePersonalInfo(request);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!isAdded()) return;
                hutilities.showHideLoading(false, loadingForDialog, (Activity)context);
                try {
                    if(response.code() == 200) {
                        InsUpdRes res = InsUpdRes.fromBytes(response.body().bytes());
                        Log.d("testing", res.message);
                        if (res.isOk) {
                            app.session.setCCustomer(pInfo);
                            //Same CODE in : CompleteRegister-Register
                            if (mode.equals("clubsearch") || mode.equals("clubview")){
                                //Bere be safheye asli va hame chi refresh she
                                context.backToHome();
                            }
                            else {
                                hutilities.hideKeyboard(context);
                                context.backToHome();
                                //((MainActivityManager) context).onBackPressed();
                            }

//                                //if it comes from another page
//                                projectStatics.showDialog(context, "", "ثبت نام/ورود شما انجام شد. اکنون می توانید عملیات مد نظرتان را ادامه دهید.", getResources().getString(R.string.ok)
//                                        , view ->{((MainActivityManager)context).onBackPressed();}
//                                , "", null);


                        } else {
                            String msg = res.message;
                            String whatTODO = res.command;
                            if (msg == null || msg.equals(""))
                                msg = "متاسفانه مشکلی در ورود شما به سامانه به وجود آمده است. لطفا مجددا تلاش کنید";

                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                        }
                    }
                    else{
                        projectStatics.ManageCallResponseErrors(true, PageIds_PA.CompleteRegister, 210, context, response.code());
                    }
                } catch (IOException e) {
                    Toast.makeText(context, R.string.timeout_error, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                projectStatics.ManageCallExceptions(true, PageIds_PA.CompleteRegister, 150, t, context);
                if (!isAdded()) return;
                hutilities.showHideLoading(false, loadingForDialog, (Activity)context);
            }
        });
    }

    public class CustomListAdapter_Offline extends ArrayAdapter {
        private List<String> dataList;
        private Context mContext;
        private int itemLayout;

        private ListFilter listFilter = new ListFilter();
        private List<String> dataListAllItems;

        public CustomListAdapter_Offline(Context context, int resource, List<String> storeDataLst) {
            super(context, resource, storeDataLst);
            dataList = storeDataLst;
            mContext = context;
            itemLayout = resource;
        }

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public String getItem(int position) {
            Log.d("CustomListAdapter",
                    dataList.get(position));
            return dataList.get(position);
        }

//        @Override
//        public View getView(int position, View view, @NonNull ViewGroup parent) {
//            if (view == null) {
//                view = LayoutInflater.from(parent.getContext())
//                        .inflate(itemLayout, parent, false);
//            }
//
//            TextView strName = (TextView) view.findViewById();
//            strName.setText(getItem(position));
//            return view;
//        }

        @NonNull
        @Override
        public Filter getFilter() {return listFilter;}

        public class ListFilter extends Filter {
            private Object lock = new Object();

            @Override
            protected FilterResults performFiltering(CharSequence prefix) {
                FilterResults results = new FilterResults();
                if (dataListAllItems == null) {
                    synchronized (lock) {
                        dataListAllItems = new ArrayList<>(dataList);
                    }
                }

                if (prefix == null || prefix.length() == 0) {
                    synchronized (lock) {
                        results.values = dataListAllItems;
                        results.count = dataListAllItems.size();
                    }
                }
                else {
                    String[] parts= prefix.toString().split(" ");

                    ArrayList<String> matchValues = new ArrayList<String>();

                    for (String dataItem : dataListAllItems) {
                        for(String pp:parts){
                            if (pp.equals("باشگاه") || pp.equals("کوهنوردی") || pp.equals("طبیعتگردی") || pp.equals("تخصصی") || pp.equals("ورزشی"))
                                continue;
                            if (dataItem.contains(pp)){
                                matchValues.add(dataItem);
                                break;
                            }
                        }
                    }

                    results.values = matchValues;
                    results.count = matchValues.size();
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.values != null) {
                    dataList = (ArrayList<String>)results.values;
                } else {
                    dataList = null;
                }
                if (results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }

        }
    }


    //تنظیمات مربوط به صفحه --------------
    @Override
    protected int getScreenId() {return PrjConfig.frmCompleteRegister;}
    @Override
    protected String tag() {return SCREEN_TAG;}
    public static final String SCREEN_TAG = "CompRegister";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {//3nd Event
        return inflater.inflate(R.layout.frm_user_completeregister, parent, false);
    }
}
