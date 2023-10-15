package utils;

import static android.text.InputType.TYPE_CLASS_NUMBER;

import static utils.HFragment.stktrc2kt;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
//import mojafarin.naghshebaz.BuildConfig; //1400-01-13 commented
//import mojafarin.naghshebaz.R;
//import mojafarin.naghshebaz.app;
import bo.entity.CityDTO;
import bo.sqlite.TTExceptionLogSQLite;
import okhttp3.Response;
import pakoob.DbAndLayout.R;

//1399-10-10 add parseFloat and copyFile
//1399-06-26 add textAsBitmap
//1399-06-25 add hideKeyboard
//1399-06-04 edit serialNumber and decrptBytes...
//1399-04-24 edit areYouSure...
public class projectStatics {
    //----------Working with font--------------
    public static Typeface IranSans_FONT = null;
    public static Typeface Fontello_FONT = null;
    public static final String IranSans_Text = "IranSans";
    public static List<CityDTO> cities;


    public static void setCustomTypeface(View view, String typeFaceName, Context context) {
        //sample: hessamTools.setCustomTypeface(view, "IranSans", this.getContext());
        Typeface typeFace = null;
        if (typeFaceName.equals(IranSans_Text)) {
            if (IranSans_FONT == null) {
                getIranSans_FONT(context);//ResourcesCompat.getFont(context, R.font.iransansweb); bood ke comment shod, khat baed ham hichi
//                IranSans_FONT= Typeface.createFromAsset(context.getAssets(),"font/iransansweb.ttf");
            }
            typeFace = IranSans_FONT;
        }
        if (view instanceof TextView) {
            ((TextView) view).setTypeface(typeFace);
        } else if (view instanceof EditText) {
            ((EditText) view).setTypeface(typeFace);
        } else if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int count = viewGroup.getChildCount();
            for (int i = 0; i < count; i++) {
                setCustomTypeface(viewGroup.getChildAt(i), typeFaceName, context);
            }
        }
    }

    public static Typeface getIranSans_FONT(Context context) {
        if (IranSans_FONT == null) {
            IranSans_FONT = ResourcesCompat.getFont(context, R.font.iransansweb);
        }
        return IranSans_FONT;
    }

    public static Typeface getFontello_FONT(Context context) {
        if (Fontello_FONT == null) {
            Fontello_FONT = ResourcesCompat.getFont(context, R.font.fontello);
        }
        return Fontello_FONT;
    }

    public static void showDialog(Context context, String title, String message, String posetiveText, View.OnClickListener posetiveClickEvent, String negativeText, View.OnClickListener negativeClickEvent) {
        try {
            LinearLayout layMain = new LinearLayout(context);
            layMain.setOrientation(LinearLayout.VERTICAL);
            layMain.setPadding(25, 35, 25, 35);

            if (title.length() > 0) {
                LinearLayout.LayoutParams layparams_title = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layparams_title.setMargins(16, 32, 16, 16);
                TextView txtTitle = new TextView(context);
                txtTitle.setText(title);
                txtTitle.setLayoutParams(layparams_title);
                //            txtTitle.setTextSize(18);
                txtTitle.setTextAppearance(context, R.style.label_xl);
                layMain.addView(txtTitle);
            }
            if (message.length() > 0) {
                LinearLayout.LayoutParams layparams_message = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layparams_message.setMargins(16, title.length() > 0 ? 16 : 32, 16, 16);
                TextView txtMessage = new TextView(context);
                txtMessage.setText(message);
                txtMessage.setLayoutParams(layparams_message);
                txtMessage.setTextAppearance(context, R.style.label);
                //            txtMessage.setTextSize(18);
                layMain.addView(txtMessage);
            }
            RelativeLayout layButtons = new RelativeLayout(context);
            RelativeLayout.LayoutParams layButtons_params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layButtons_params.setMargins(16, 32, 16, 32);
            layButtons.setLayoutParams(layButtons_params);
            layMain.addView(layButtons);

            //1)add Splitor in middle
            View viewSplit1 = new View(context);
            if (negativeText.length() > 0) {
                RelativeLayout.LayoutParams split_params = new RelativeLayout.LayoutParams(30, 1);
                split_params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                viewSplit1.setLayoutParams(split_params);
                viewSplit1.setId((int) 324);
                layButtons.addView(viewSplit1, split_params);
            }
            //2)add pos button to right
            RelativeLayout.LayoutParams lp_pos_button = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            if (negativeText.length() > 0)
                lp_pos_button.addRule(RelativeLayout.RIGHT_OF, viewSplit1.getId());
            else
                lp_pos_button.addRule(RelativeLayout.CENTER_HORIZONTAL);
            Button btnPosetive = new Button(context);
            btnPosetive.setPadding(32, 3, 32, 3);
            btnPosetive.setText(posetiveText);
            btnPosetive.setLayoutParams(lp_pos_button);
            btnPosetive.setTextAppearance(context, R.style.btnAccept);
            btnPosetive.setBackgroundColor(context.getResources().getColor(R.color.btnAccept_background));
            btnPosetive.setTextColor(context.getResources().getColor(R.color.btnAccept_text));
            if (posetiveClickEvent != null)
                btnPosetive.setOnClickListener(posetiveClickEvent);
            layButtons.addView(btnPosetive, lp_pos_button);

            //3)add negative button
            Button btnNegative = null;
            if (negativeText.length() > 0) {
                RelativeLayout.LayoutParams lp_neg_button = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp_neg_button.addRule(RelativeLayout.LEFT_OF, viewSplit1.getId());
                btnNegative = new Button(context);
                btnNegative.setPadding(16, 3, 16, 3);
                btnNegative.setText(negativeText);
                btnNegative.setLayoutParams(lp_neg_button);
                btnNegative.setTextAppearance(context, R.style.btnLink);
                btnNegative.setBackgroundColor(context.getResources().getColor(R.color.btnLink_background));
                btnNegative.setTextColor(context.getResources().getColor(R.color.btnLink_text));

                layButtons.addView(btnNegative, lp_neg_button);
            }


            //hessamTools.setCustomTypeface(linearLayout1, "IranSans", context);
            projectStatics.setCustomTypeface(layMain, "IranSans", context);
            layMain.setBackgroundColor(Color.WHITE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                layMain.setElevation(100);
            }
//            layMain.setLayoutDirection(LayoutDirection.RTL);


            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.GlobalAlertDialog));
            builder.setView(layMain);
            AlertDialog alert = builder.create();

            if (btnNegative != null)
                btnNegative.setOnClickListener(view -> {
                    alert.dismiss();
                    if (negativeClickEvent != null)
                        negativeClickEvent.onClick(view);
                });
            btnPosetive.setOnClickListener(view -> {
                alert.dismiss();
                if (posetiveClickEvent != null)
                    posetiveClickEvent.onClick(view);
            });

            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final int showEnterTextDialog_EditTextId = 125678654;

    public static AlertDialog showEnterTextDialog(Context context, String title, String message, String defaultInputText, String posetiveText, View.OnClickListener posetiveClickEvent, String negativeText, View.OnClickListener negativeClickEvent, int inputType, boolean SingeLine) {
        try {
            LinearLayout layMain = new LinearLayout(context);
            layMain.setOrientation(LinearLayout.VERTICAL);
            layMain.setPadding(25, 35, 25, 35);

            if (title.length() > 0) {
                LinearLayout.LayoutParams layparams_title = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layparams_title.setMargins(16, 32, 16, 16);
                TextView txtTitle = new TextView(context);
                txtTitle.setText(title);
                txtTitle.setLayoutParams(layparams_title);
                //            txtTitle.setTextSize(18);
                txtTitle.setTextAppearance(context, R.style.label_xl);
                layMain.addView(txtTitle);
            }
            if (message.length() > 0) {
                LinearLayout.LayoutParams layparams_message = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layparams_message.setMargins(16, title.length() > 0 ? 16 : 32, 16, 16);
                TextView txtMessage = new TextView(context);
                txtMessage.setText(message);
                txtMessage.setLayoutParams(layparams_message);
                txtMessage.setTextAppearance(context, R.style.label);
                //            txtMessage.setTextSize(18);
                layMain.addView(txtMessage);
            }
            //Add EditText
            EditText txtInput = new EditText(context);
            LinearLayout.LayoutParams layparams_txtInput = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            txtInput.setMargins(16, 32, 16, 16);
            txtInput.setId((int) showEnterTextDialog_EditTextId);//txtInput.setId(R.id.txtInput);
//            txtInput.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_roundcorner_gray));
            txtInput.setText(defaultInputText);
            txtInput.setLayoutParams(layparams_txtInput);
            txtInput.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                txtInput.setFocusedByDefault(true);
//            }
            //نمایش کیبور و دریافت فوکوس خطوط زیر
            txtInput.requestFocus();
            //و دو خط زیر برای نمایش کیبورد که فعلا کار نمیکنه
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(txtInput, InputMethodManager.SHOW_IMPLICIT);
            
            txtInput.setTextAppearance(context, R.style.label);
            txtInput.setTextSize(18);
            txtInput.setSingleLine(SingeLine);
            if (SingeLine)
                txtInput.setMaxLines(1);
            txtInput.setImeOptions(EditorInfo.IME_ACTION_DONE);
            txtInput.setPadding(2, 2, 2, 2);
            if (inputType != 0) {
                txtInput.setInputType(inputType);

                if (inputType == TYPE_CLASS_NUMBER){
                    txtInput.setTextDirection(View.TEXT_DIRECTION_LTR);
                }
            }
            layMain.addView(txtInput);


            //Add Button panel and Buttons
            RelativeLayout layButtons = new RelativeLayout(context);
            RelativeLayout.LayoutParams layButtons_params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layButtons_params.setMargins(16, 32, 16, 32);
            layButtons.setLayoutParams(layButtons_params);
            layMain.addView(layButtons);

            //1)add Splitor in middle
            View viewSplit1 = new View(context);
            if (negativeText.length() > 0) {
                RelativeLayout.LayoutParams split_params = new RelativeLayout.LayoutParams(30, 1);
                split_params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                viewSplit1.setLayoutParams(split_params);
                viewSplit1.setId((int) 324);
                layButtons.addView(viewSplit1, split_params);
            }
            //2)add pos button to right
            RelativeLayout.LayoutParams lp_pos_button = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            if (negativeText.length() > 0)
                lp_pos_button.addRule(RelativeLayout.RIGHT_OF, viewSplit1.getId());
            else
                lp_pos_button.addRule(RelativeLayout.CENTER_HORIZONTAL);
            Button btnPosetive = new Button(context);
            btnPosetive.setPadding(32, 3, 32, 3);
            btnPosetive.setText(posetiveText);
            btnPosetive.setLayoutParams(lp_pos_button);
            btnPosetive.setTextAppearance(context, R.style.btnAccept);
            btnPosetive.setBackgroundColor(context.getResources().getColor(R.color.btnAccept_background));
            btnPosetive.setTextColor(context.getResources().getColor(R.color.btnAccept_text));
            if (posetiveClickEvent != null)
                btnPosetive.setOnClickListener(posetiveClickEvent);
            layButtons.addView(btnPosetive, lp_pos_button);

            //3)add negative button
            Button btnNegative = null;
            if (negativeText.length() > 0) {
                RelativeLayout.LayoutParams lp_neg_button = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp_neg_button.addRule(RelativeLayout.LEFT_OF, viewSplit1.getId());
                btnNegative = new Button(context);
                btnNegative.setPadding(16, 3, 16, 3);
                btnNegative.setText(negativeText);
                btnNegative.setLayoutParams(lp_neg_button);
                btnNegative.setTextAppearance(context, R.style.btnLink);
                btnNegative.setBackgroundColor(context.getResources().getColor(R.color.btnLink_background));
                btnNegative.setTextColor(context.getResources().getColor(R.color.btnLink_text));

                layButtons.addView(btnNegative, lp_neg_button);
            }


            //hessamTools.setCustomTypeface(linearLayout1, "IranSans", context);
            projectStatics.setCustomTypeface(layMain, "IranSans", context);
            layMain.setBackgroundColor(Color.WHITE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                layMain.setElevation(100);
            }
//            layMain.setLayoutDirection(LayoutDirection.RTL);


            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.GlobalAlertDialog));
            builder.setView(layMain);
            AlertDialog alert = builder.create();

            if (btnNegative != null)
                btnNegative.setOnClickListener(view -> {
                    alert.dismiss();
                    if (negativeClickEvent != null)
                        negativeClickEvent.onClick(view);
                });
            btnPosetive.setOnClickListener(view -> {
                alert.dismiss();
                if (posetiveClickEvent != null)
                    posetiveClickEvent.onClick(view);
            });
            alert.show();

            return alert;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void SetDialogButtonStylesAndBack(android.app.AlertDialog dialog, Context context, Typeface font, float size) {
        Button button1 = (Button) dialog.getWindow().findViewById(android.R.id.button1);
        Button button2 = (Button) dialog.getWindow().findViewById(android.R.id.button2);
        if (button1 != null) {
            button1.setTypeface(font);
            button1.setTextSize(size);
        }
        if (button2 != null) {
            button2.setTypeface(font);
            button2.setTextSize(size);
        }
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.cancel();
                    return true;
                }
                return false;
            }
        });
    }

    public static void areYouSureToExitActivity(Context context, Activity activityToFinish) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(25, 35, 25, 35);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(16, 16, 16, 16);
        TextView textView = new TextView(context);
        textView.setText("از خروج مطمئن هستید؟");
        textView.setLayoutParams(params);
        textView.setTextSize(18);

        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params2.setMargins(16, 48, 16, 16);
        Button button = new Button(context);
        button.setText("خروج");
        button.setLayoutParams(params2);
        button.setTextSize(16);
        button.setTextColor(context.getResources().getColor(android.R.color.white));
//        button.setWidth(350);
//        button.setHeight(40);
        button.setGravity(Gravity.CENTER);
        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(20);
        shape.setColor(context.getResources().getColor(R.color.colorAccent));
        button.setBackground(shape);
        button.setPadding(70, 5, 70, 5);
        //button.setBackgroundColor(Color.RED);

        linearLayout.addView(textView);
        linearLayout.addView(button);
        projectStatics.setCustomTypeface(linearLayout, "IranSans", context);

        AlertDialog.Builder builder = new AlertDialog.Builder(activityToFinish);
        builder.setView(linearLayout);

        AlertDialog alert = builder.create();
        button.setOnClickListener(view -> {
            alert.dismiss();
            activityToFinish.finish();
        });

        alert.show();

//        LinearLayout linearLayout  = new LinearLayout(context);
//        linearLayout .setOrientation(LinearLayout.VERTICAL);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        TextView textView = new TextView(context);
//        textView.setText("از خروج مطمئن هستید؟");
//        textView.setLayoutParams(params);
//        textView.setTextSize(18);
//
//        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        Button button = new Button(context);
//        button.setText("خروج");
//        button.setTextSize(18);
//        button.setLayoutParams(params2);
//        button.setBackgroundColor(Color.RED);
//
//
//        linearLayout.addView(textView);
//        linearLayout.addView(button);
//        hessamTools.setCustomTypeface(linearLayout, IranSans_Text, context);
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setView(linearLayout);
//
//        AlertDialog alert = builder.create();
//        button.setOnClickListener(view -> {alert.dismiss();activityToFinish.finish();});
//
//        alert.show();
    }


    public static Bitmap textAsBitmapFontello(String text, float textSize, int textColor, Context context) {
        Typeface font = ResourcesCompat.getFont(context, R.font.fontello);
        return hutilities.textAsBitmap(text, textSize, textColor, font, context);
    }


    public static String ManageCallResponseErrors(boolean showMsg, int Page_Id, int OperationCode, Context context, int responseCode) {
        String title = context.getResources().getString(R.string.dialog_ertebatBaServer_Title);
        String msg = "";
        if (responseCode == 0) {
            //Failure rokh dade
            msg = "متاسفانه ارتباط با سرور قطع می باشد، لطفا مجددا تلاش نمایید.";
        } else {
            if (responseCode == 500) {
                msg = "متاسفانه سرور به طور موقتی غیر فعال می باشد. لطفا بعدا تلاش نمایید";
            } else {
                msg = context.getResources().getString(R.string.dialog_ertebatBaServer_Desc);
            }
        }
        if (showMsg) {
            projectStatics.showDialog(context, title
                    , msg
                    , context.getResources().getString(R.string.ok)
                    , null, "", null);
        }
        TTExceptionLogSQLite.insert(responseCode + msg, "From ManageCall", Page_Id, OperationCode);
        Log.d("Operation", "server contact failed with code : " + responseCode);
        return msg;
    }

    public static String ManageCallExceptions(boolean showMsg, int Page_Id, int OperationCode, Throwable t, Context context) {
        String title = context.getResources().getString(R.string.dialog_UnknownError);
        String msg = "";
        String exMessage = t.getMessage();
        if (exMessage.contains("timeout")) {
            msg = "مدت زمان انتظار برای دریافت پاسخ از سرور طولانی شده است. لطفا دوباره تلاش کنید.";
        } else {
            //Failure rokh dade
            msg = "متاسفانه خطایی ناشناخته در ارتباط با سرور رخ داده است. لطفا بعدا دوباره تلاش کنید";
        }
        if (showMsg) {
            projectStatics.showDialog(context, title
                    , msg
                    , context.getResources().getString(R.string.ok)
                    , null, "", null);
        }
        TTExceptionLogSQLite.insert("From ManageEx" + " - " + t.getMessage(), stktrc2kt(t), Page_Id, OperationCode);
        t.printStackTrace();
        return msg;
    }

    public static void ChangeTabsFont(TabLayout tabLayout, Context context) {
        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(projectStatics.getIranSans_FONT(context), Typeface.NORMAL);
                }
            }
        }
    }


}
