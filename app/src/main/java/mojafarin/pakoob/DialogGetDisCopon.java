package mojafarin.pakoob;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import maptools.Deg2UTM;
import maptools.UTM2Deg;
import maptools.hMapTools;
import mojafarin.pakoob.R;
import mojafarin.pakoob.app;

public class DialogGetDisCopon {

    public static AlertDialog.Builder GetBuilder(Context context, View.OnClickListener PosetiveListener, View.OnClickListener NegativeListener) {
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.dialog_getdiscount_copon, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);


        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final TextView btnContinue = promptsView.findViewById(R.id.btnContinue);

        btnContinue.setOnClickListener(PosetiveListener);
        return alertDialogBuilder;
    }

    public static String GetDiscountCode(AlertDialog promptsView){
        final EditText txtDiscountCode = promptsView.findViewById(R.id.txtDiscountCode);
        return txtDiscountCode.getText().toString().trim();
    }
}