package mojafarin.pakoob;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import maptools.hMapTools;

public class TestDialog extends AlertDialog.Builder {
    private Context context;
    private AlertDialog mAlertDialog;
    public TestDialog(Context _context) {
        super(_context);
        context = _context;
    }


    @SuppressLint("InflateParams")
    @Override
    public AlertDialog show() {
        View v = LayoutInflater.from(context).inflate(R.layout.frm_contactus, null);

        mAlertDialog = super.show();
        return mAlertDialog;
    }
}
