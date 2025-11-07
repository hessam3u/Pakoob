package user;

import static android.content.Context.RECEIVER_NOT_EXPORTED;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;

import bo.PageIds_PA;
import bo.dbConstantsTara;
import bo.entity.ConfirmSms2DTO;
import bo.entity.MobileInfoDTO;
import bo.entity.PersonalInfoDTO;
import bo.NewClasses.SimpleRequest;
import bo.sqlite.TTExceptionLogSQLite;
import mojafarin.pakoob.MainActivity;
import mojafarin.pakoob.R;
import mojafarin.pakoob.app;
import okhttp3.ResponseBody;
import pakoob.SelectClubDialogForMyClub;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.HFragment;
import utils.MainActivityManager;
import utils.PrjConfig;
import utils.hutilities;
import utils.projectStatics;

public class Register extends HFragment {
    static final int ENTER_MOBILE = 1;
    static final int ENTER_Code = 2;
    int state = ENTER_MOBILE;
    String mobileEntered = "";
    long countDownInSec = 120;
    CountDownTimer countDownTimer;
    String mode = "start";

    EditText txtInput;
    Button btnSkip, btnLogin, btnCounter, btnBackToMobile;
    Button btnNeedHelpInSendingCode;
    TextView lblTitle, lblDesc;
    ProgressBar loadingForDialog;
    LinearLayout divOtherButtons;

    public Register(String _mode) {
        mode = _mode;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {//4th Event
        super.onViewCreated(view, savedInstanceState);
        int step = 1;
        try {
            InitializeComponents(view);
            step = 2;
            //پیامک سیستمی - بخش اول
            IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
            step = 3;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                context.registerReceiver(smsVerificationReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED);
            step = 4;
            //dbConstantsTara.initFiltersAsync();
        } catch (Exception ex) {
            TTExceptionLogSQLite.insert(ex.getMessage(), stktrc2k(ex), PrjConfig.frm_PleaseRegister, 100);
            Log.d("بازکردن", "Bind Ver Tour: " + ex.getMessage() + ex.getStackTrace());
            ex.printStackTrace();
        }
    }

    private void InitializeComponents(View v) {
        btnSkip = v.findViewById(R.id.btnSkip);
        btnLogin = v.findViewById(R.id.btnLogin);
        txtInput = v.findViewById(R.id.txtInput);
        lblDesc = v.findViewById(R.id.lblDesc);
        lblTitle = v.findViewById(R.id.lblTitle);
        loadingForDialog = v.findViewById(R.id.loadingForDialog);
        divOtherButtons = v.findViewById(R.id.divOtherButtons);
        btnCounter = v.findViewById(R.id.btnCounter);
        btnBackToMobile = v.findViewById(R.id.btnBackToMobile);
        btnNeedHelpInSendingCode = v.findViewById(R.id.btnNeedHelpInSendingCode);

        btnNeedHelpInSendingCode.setOnClickListener(view -> {
            showNeedHelpDialog();
        });

        btnSkip.setOnClickListener(view -> {
            hutilities.hideKeyboard(context, txtInput);
            if (mode.equals("menu")) {
                ((MainActivityManager) context).onBackPressed();
            } else {
                ((MainActivityManager) context).onBackPressed();
//                Intent intent = new Intent(this, MainActivity.class);
//                startActivity(intent);
//                finish();
            }
        });

        btnLogin.setOnClickListener(view -> {
            if (state == ENTER_MOBILE) {
                clickButtonForState1();
            } else if (state == ENTER_Code) {
                clickButtonForState2();
            }
        });
        btnCounter.setOnClickListener(view -> {
            if (state == ENTER_MOBILE || !btnCounter.getText().toString().equals(counterDefaultSendAgain))
                return;
            clickButtonForState1();
        });
        btnBackToMobile.setOnClickListener(view -> {
            if (countDownTimer != null)
                countDownTimer.cancel();
            setNewState(ENTER_MOBILE);
            txtInput_TextChanged(mobileEntered, 0, 0, mobileEntered.length());
            txtInput.setText(mobileEntered);
        });

        setEnable_btnLogin(false);

        //*********** Baraye focus kardan va namayeshe keyboard
        hutilities.showKeyboard(context, txtInput);
        txtInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtInput_TextChanged(s, start, before, count);
            }
        });
    }

    private void showNeedHelpDialog() {
        projectStatics.showDialog(context, getResources().getString(R.string.NotGettingSMS), getResources().getString(R.string.NotGettingSMS_Desc), getResources().getString(R.string.SendRequest), view -> {
            app.DoSendProblem(PrjConfig.PROBLEM_CODE_ConfirmSMS, mobileEntered, context, res -> {
                Log.e("بازگشت مشکل", res == null ? "NULL" : (res.isOk ? "ISOK" : "NOOK:" + res.command));
                if (res == null) {
                    //failure or No Internet
                    Toast.makeText(context, R.string.NoInternet_Desc, Toast.LENGTH_LONG);
                } else if (!res.isOk) {
                    if (res.command == "-1") {
                        //No Internet
                        Toast.makeText(context, R.string.NoInternet_Desc, Toast.LENGTH_LONG);
                    } else if (res.command == "0") {
                        //Exception, Less Possible
                        Toast.makeText(context, R.string.dialog_UnknownErrorDesc, Toast.LENGTH_LONG);
                    } else {
                        //Response Problem
                        Toast.makeText(context, R.string.dialog_ertebatBaServer_Desc, Toast.LENGTH_LONG);
                    }
                } else {
                    //OK Everything
                    Toast.makeText(context, R.string.SendRequest_Completed, Toast.LENGTH_LONG);
                }
            });
        }, getResources().getString(R.string.cancel), null);
    }

    private void txtInput_TextChanged(CharSequence s, int start, int before, int count) {
        int len = s.length();
        if (state == ENTER_MOBILE) {
            if (len == 11 || (len == 10 && s.charAt(0) == '9')) {
                setEnable_btnLogin(true);
            } else {
                setEnable_btnLogin(false);
            }

            if (len > 0 && s.charAt(0) == '9') {
                setMaxLength_txtInput(10);
            } else {
                setMaxLength_txtInput(11);
            }
        } else if (state == ENTER_Code) {
            setMaxLength_txtInput(5);
            if (len == 5) {
                setEnable_btnLogin(true);
            } else {
                setEnable_btnLogin(false);
            }
        }
    }

    private void clickButtonForState2() {
        if (!hutilities.isInternetConnected(context)) {
            projectStatics.showDialog(context, getResources().getString(R.string.NoInternet), getResources().getString(R.string.NoInternet_Desc), getResources().getString(R.string.ok), null, "", null);
            return;
        }
        btnLogin.setEnabled(false);

        hutilities.showHideLoading(true, loadingForDialog, (Activity) context);
        ConfirmSms2DTO obj = new ConfirmSms2DTO();
        obj.info = MobileInfoDTO.instance();
        obj.Mobile = mobileEntered;
        obj.TempDeviceId = app.session.getTempDeviceId();
        obj.confirmCode = txtInput.getText().toString();
        obj.ConfirmType = 1;
        obj.FirebaseToken = app.session.getFirebaseRegId();
        LatLng lastLocation = app.session.getLastAproxLocation();
        if (lastLocation != app.session.emptyLocation)
            obj.whatToDoStr = String.valueOf(lastLocation.latitude) + "*" + String.valueOf(lastLocation.longitude);

        SimpleRequest request = SimpleRequest.getInstance(obj);
        Log.e("کانفریم", "آماده برای ارسال کد تایید" + obj.confirmCode);
        Call<ResponseBody> call = app.apiMap.CheckConfirmCode(request);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!isAdded()) return;
                hutilities.showHideLoading(false, loadingForDialog, (Activity) context);
                try {
                    Log.e("کانفریم", " ارسال شد کد تایید" + response.code());
                    if (response.code() == 200) {
                        PersonalInfoDTO res = PersonalInfoDTO.fromBytes(response.body().bytes());
                        Log.e("کانفریم", "شماره مشتری بازگردانده شده" + res.CCustomerId);
                        if (res.CCustomerId > 0) {
                            countDownTimer.cancel();
                            hutilities.hideKeyboard(context, txtInput);
                            //goto State 2 and Enter code
                            app.session.setCCustomer(res);
                            app.session.setSession(res.data);

                            dbConstantsTara.session.setMyClubName(res.ClubName);
                            dbConstantsTara.session.setMyClubNameIds(res.Int4);
                            Log.e("لوگو", res.ClubLogo != null ? res.ClubLogo : "");
                            dbConstantsTara.session.setMyClubLogo(SelectClubDialogForMyClub.saveClubLogo(context, res.ClubLogo));

                            app.doSyncsAndRedesignAfterLogin(getContext(), ((MainActivity) context), true);

                            btnLogin.setEnabled(true);

                            if (false && (res.Name.length() > 0 || res.Family.length() > 0)) {
                                //Means that he filled the next form previously
                                projectStatics.showDialog(context, context.getString(R.string.welcomeback), context.getString(R.string.welcomeback_desc)
                                        , context.getString(R.string.ok), view ->
                                        {
                                            //Same CODE in : CompleteRegister-Register
                                            if (true || mode.equals("clubsearch") || mode.equals("clubview") || mode.equals("start")) {
                                                //Bere be safheye asli va hame chi refresh she
                                                ((MainActivityManager) context).backToHome();
                                            } else
                                                ((MainActivityManager) context).onBackPressed();
                                        }, "", null);

                                return;
                            } else {//if (mode.equals("start") || mode.equals("menu")) {
                                //First Login
                                projectStatics.showDialog(context, context.getString(R.string.welcomeInFirstLogin), context.getString(R.string.welcomeInFirstLogin_desc)
                                        , context.getString(R.string.ok), view -> {
                                            ((MainActivityManager) context).showFragment(new CompleteRegister("noback"), true);
                                        }, "", null);
                            }
//                            else if(mode.equals("MapSelect")){
//                                ((MainActivityManager)context).showFragment(new CompleteRegister(mode), true);
//                                //Working code: if I decide to not complete other info
//                                //projectStatics.showDialog(Register.this, "", "ثبت نام/ورود شما با تلفن ارائه شده انجام شد. اکنون می توانید عملیات مد نظرتان را ادامه دهید.", getResources().getString(R.string.ok)
////                                        , view ->{finish();}
////                                , "", null);
//
//                            }

                        } else {
                            btnLogin.setEnabled(true);

                            String msg = res.message;
                            String whatTODO = res.whatToDoStr;
                            if (msg == null || msg.equals(""))
                                msg = "متاسفانه مشکلی در ورود شما به سامانه به وجود آمده است. لطفا مجددا تلاش کنید";

                            projectStatics.showDialog(context, "کد تایید اشتباه", msg
                                    , context.getResources().getString(pakoob.DbAndLayout.R.string.ok)
                                    , null, "", null);
                        }
                    } else {
                        btnLogin.setEnabled(true);
                        projectStatics.ManageCallResponseErrors(true, PageIds_PA.register, 110, context, response.code());
                    }
                } catch (IOException e) {
                    btnLogin.setEnabled(true);
                    Toast.makeText(context, R.string.timeout_error, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                projectStatics.ManageCallExceptions(true, PageIds_PA.register, 100, t, context);
                if (!isAdded()) return;
                btnLogin.setEnabled(true);
                hutilities.showHideLoading(false, loadingForDialog, (Activity) context);
            }
        });
    }

    void clickButtonForState1() {
        if (!hutilities.isInternetConnected(context)) {
            projectStatics.showDialog(context, getResources().getString(R.string.NoInternet), getResources().getString(R.string.NoInternet_Desc), getResources().getString(R.string.ok), null, "", null);
            return;
        }
        hutilities.showHideLoading(true, loadingForDialog, (Activity) context);
        ConfirmSms2DTO obj = new ConfirmSms2DTO();
        obj.info = MobileInfoDTO.instance();
        if (state == ENTER_MOBILE)
            obj.Mobile = mobileEntered = txtInput.getText().toString();
        else if (state == ENTER_Code)
            obj.Mobile = mobileEntered;
        obj.TempDeviceId = app.session.getTempDeviceId();
        obj.ConfirmType = 1;
        obj.confirmCode = "";
        obj.ConfirmStatus = 0;
        obj.info.Uid = 0;
        obj.message = "";
        obj.whatToDoStr = "";
        obj.FirebaseToken = app.session.getFirebaseRegId();

        SimpleRequest request = SimpleRequest.getInstance(obj);
        Call<ResponseBody> call = app.apiMap.GenerateConfirmCode(request);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!isAdded()) return;
                hutilities.showHideLoading(false, loadingForDialog, (Activity) context);
                try {
                    if (response.code() == 200) {
                        ConfirmSms2DTO res = ConfirmSms2DTO.fromBytes(response.body().bytes());

                        if (res.ConfirmStatus == 1) {
                            //goto State 2 and Enter code
                            setNewState(ENTER_Code);
                            //Sms rec function start
                            Task<Void> task = SmsRetriever.getClient(context).startSmsUserConsent(null /*null or senderPhoneNumber */);
                            task.addOnSuccessListener(aVoid -> Log.d("SMS", "SMS Retriever started successfully."))
                                    .addOnFailureListener(e -> Log.e("SMS", "Failed to start SMS Retriever."));
                            initCountDownTimer();
                            countDownTimer.start();
                        } else {
                            String msg = res.message;
                            String whatTODO = res.whatToDoStr;
                            if (msg == null || msg.equals(""))
                                msg = "متاسفانه مشکلی در ورود شما به سامانه به وجود آمده است. لطفا مجددا تلاش کنید";

                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        projectStatics.ManageCallResponseErrors(true, PageIds_PA.register, 150, context, response.code());
                    }
                } catch (IOException e) {
                    Toast.makeText(context, R.string.timeout_error, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                projectStatics.ManageCallExceptions(true, PageIds_PA.register, 100, t, context);
                if (!isAdded()) return;
                Log.e("کانفریم", t.getMessage());
                t.printStackTrace();
                hutilities.showHideLoading(false, loadingForDialog, (Activity) context);
                //projectStatics.showDialog(context, Tag, "خطای اکسپشن : " + t.getMessage() + " " + t.getStackTrace(), "قبول", null, "", null);
            }
        });
    }

    //پیامک سیستمی - بخش دوم
    //https://developers.google.com/identity/sms-retriever/user-consent/request#java
    //https://gits.id/blog/easy-ways-to-implement-automatic-sms-verification-in-android/
    private static final int SMS_CONSENT_REQUEST = 2;  // Set to an unused request code
    private final BroadcastReceiver smsVerificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
                Bundle extras = intent.getExtras();
                Status smsRetrieverStatus = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

                switch (smsRetrieverStatus.getStatusCode()) {
                    case CommonStatusCodes.SUCCESS:
                        // Get consent intent
                        Intent consentIntent = extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT);
                        try {
                            // Start activity to show consent dialog to user, activity must be started in
                            // 5 minutes, otherwise you'll receive another TIMEOUT intent
                            startActivityForResult(consentIntent, SMS_CONSENT_REQUEST);
                        } catch (ActivityNotFoundException e) {
                            // Handle the exception ...
                        }
                        break;
                    case CommonStatusCodes.TIMEOUT:
                        // Time out occurred, handle the error.
                        break;
                }
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("پیامک سیستمی", "کد: " + requestCode);
        switch (requestCode) {
            case SMS_CONSENT_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    // Get SMS message content
                    String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                    // Extract one-time code from the message and complete verification
                    // `sms` contains the entire text of the SMS message, so you will need
                    // to parse the string.
                    Log.e("پیامک سیستمی", "پیام: " + message);
                    Pattern p = Pattern.compile("\\d+");
                    Matcher m = p.matcher(message);
                    if (m.find()) {
                        String code = m.group();
                        Log.e("پیامک سیستمی", "عدد: " + code);
                        txtInput.setText(code);
                        btnLogin.callOnClick();
                    }

                    // send one time code to the server
                } else {
                    // Consent canceled, handle the error ...
                }
                break;
        }
    }
    //انتهای پیامک سیستمی - بخش دوم


    void setEnable_btnLogin(boolean enable) {
        if (enable) {
            btnLogin.setAlpha(1f);
            btnLogin.setClickable(true);
        } else {

            btnLogin.setAlpha(.5f);
            btnLogin.setClickable(false);
        }
    }

    void setMaxLength_txtInput(int len) {
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(len);
        txtInput.setFilters(filterArray);
    }

    void setNewState(int newState) {
        state = newState;
        if (newState == ENTER_MOBILE) {
            btnLogin.setText("ادامه...");
            lblTitle.setText("ثبت نام/ورود با شماره موبایل");
            lblDesc.setText("لطفا شماره موبایل خود را وارد نمایید");
            txtInput.setHint("09121112233");
            divOtherButtons.setVisibility(View.GONE);
            //txtInput.setText("");
        } else if (newState == ENTER_Code) {
            btnLogin.setText("ثبت نام/ورود");
            lblTitle.setText("ورود کد تایید");
            lblDesc.setText("لطفا کد ارسالی به " + mobileEntered + " را وارد نمایید");
            txtInput.setHint("");
            txtInput.setText("");
            divOtherButtons.setVisibility(View.VISIBLE);

            //setEnable_btnLogin(false);
        }
    }

    String counterDefaultText = "ارسال کد تا ";
    String counterDefaultSendAgain = "ارسال مجدد کد";

    void initCountDownTimer() {
        countDownTimer = new CountDownTimer(countDownInSec * 1000 + 1000, 1000) {
            @Override
            public void onTick(long l) {
                Long timeleftinmilliseconds = l;
                int minutes = (int) (timeleftinmilliseconds / 1000) / 60;
                int second = (int) (timeleftinmilliseconds / 1000) % 60;
                String timeletfFormated = String.format(Locale.getDefault(), "%02d:%02d", minutes, second);
                btnCounter.setText(counterDefaultText + timeletfFormated);
                btnCounter.setClickable(false);
            }

            @Override
            public void onFinish() {
                Context context = getContext();
                if (context == null) {
                    return;
                }

                btnCounter.setText(counterDefaultSendAgain);
                btnCounter.setClickable(true);

                //Namayeshe Payam
                LinearLayout linearLayout = new LinearLayout(context);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.setPadding(25, 35, 25, 35);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(16, 16, 16, 16);
                TextView textView = new TextView(context);
                textView.setText("مدت اعتبار کد تایید به پایان رسید. در صورتی که پیامک تایید را دریافت نکرده اید، دوباره تلاش نمایید و در صورتی که چند بار تلاش ناموفق داشته اید، " +
                        "می توانید در ساعت های آینده، مجددا درخواست کد تایید دهید.");
                textView.setLayoutParams(params);
                textView.setTextSize(18);

                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params2.setMargins(16, 48, 16, 16);
                Button button = new Button(context);
                button.setText("ارسال مجدد");
                button.setLayoutParams(params2);
                button.setTextSize(16);
                button.setTextColor(getResources().getColor(android.R.color.white));
//                    button.setWidth(350);
//                    button.setHeight(40);
                button.setGravity(Gravity.CENTER);
                GradientDrawable shape = new GradientDrawable();
                shape.setCornerRadius(20);
                shape.setColor(getResources().getColor(R.color.colorAccent));
                button.setBackground(shape);
                button.setPadding(70, 5, 70, 5);
                //button.setBackgroundColor(Color.RED);

                linearLayout.addView(textView);
                linearLayout.addView(button);
                projectStatics.setCustomTypeface(linearLayout, "IranSans", context);

                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AppTheme_NoActionBar));
                builder.setView(linearLayout);

                AlertDialog alert = builder.create();
                button.setOnClickListener(view -> {
                    clickButtonForState1();
                    alert.hide();
                });

                if (!((Activity) context).isFinishing())//َAdded 1399-12-05 for exception : Unable to add window -- token android.os.BinderProxy@2a60a53d is not valid; is your activity running?
                {
                    alert.show();
                }


            }
        };
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
        return inflater.inflate(R.layout.frm_user_register, parent, false);
    }

    @Override
    public void onPause() {
        if (txtInput != null)
            hutilities.hideKeyboard(context, txtInput);
        super.onPause();
    }


    public boolean onBackPressed() {
        if (!app.session.isLoggedIn() && mode.equals("start"))
            return false;
        return true;
    }
}
