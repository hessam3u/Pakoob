package maptools;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.NetworkSpecifier;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.textclassifier.TextClassifierEvent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import mojafarin.pakoob.R;
import mojafarin.pakoob.app;

public class DialogGetPosition {
    public static int CurrentSpinnerValue = 0;
    public static AlertDialog.Builder GetBuilder(Context context, LatLng position, int positionFormat, View.OnClickListener PosetiveListener, View.OnClickListener NegativeListener){
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.dialog_getposition, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);


        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final Spinner cmbPositionFormats = promptsView.findViewById(R.id.cmbPositionFormats);
        final Spinner cmbNorS_DDMMSS = promptsView.findViewById(R.id.cmbNorS_DDMMSS);
        final Spinner cmbEorW_DDMMSS = promptsView.findViewById(R.id.cmbEorW_DDMMSS);
        final TextView lblN_DegreeSign = promptsView.findViewById(R.id.lblN_DegreeSign);
        final TextView lblN_MinuteSign = promptsView.findViewById(R.id.lblN_MinuteSign);
        final TextView lblN_SecondSign = promptsView.findViewById(R.id.lblN_SecondSign);
        final TextView txtN_SS = promptsView.findViewById(R.id.txtN_SS);
        final TextView txtN_MM = promptsView.findViewById(R.id.txtN_MM);
        final TextView txtN_DD = promptsView.findViewById(R.id.txtN_DD);
        final TextView lblE_DegreeSign = promptsView.findViewById(R.id.lblE_DegreeSign);
        final TextView lblE_MinuteSign = promptsView.findViewById(R.id.lblE_MinuteSign);
        final TextView lblE_SecondSign = promptsView.findViewById(R.id.lblE_SecondSign);
        final TextView txtE_SS = promptsView.findViewById(R.id.txtE_SS);
        final TextView txtE_MM = promptsView.findViewById(R.id.txtE_MM);
        final TextView txtE_DD = promptsView.findViewById(R.id.txtE_DD);

        final LinearLayout divDDMMSS = promptsView.findViewById(R.id.divDDMMSS);
        final LinearLayout divUtm = promptsView.findViewById(R.id.divUtm);

        final EditText txtUtmEasting = promptsView.findViewById(R.id.txtUtmEasting);
        final EditText txtUtmNorthing = promptsView.findViewById(R.id.txtUtmNorthing);
        final Spinner cmbUtmZoonE = promptsView.findViewById(R.id.cmbUtmZoonE);
        final Spinner cmbUtmZoonN = promptsView.findViewById(R.id.cmbUtmZoonN);
        final RelativeLayout cmbNorS_DDMMSS_Container = promptsView.findViewById(R.id.cmbNorS_DDMMSS_Container);


        final TextView btnAccept = promptsView.findViewById(R.id.btnAccept);
        final TextView btnCancel = promptsView.findViewById(R.id.btnCancel);

        //Initialize Dialog...
        ArrayAdapter<CharSequence> positionAdaptor = ArrayAdapter.createFromResource(context,
                R.array.PositionFormats, R.layout.spinner_normal_item);
        positionAdaptor.setDropDownViewResource(R.layout.spinner_normal_item);
        cmbPositionFormats.setAdapter(positionAdaptor);

        List<String> northSouth = new ArrayList<String>();
        northSouth.add("N");
        northSouth.add("S");
        ArrayAdapter<String> northAdaptor = new ArrayAdapter<String>(
                context,
                android.R.layout.simple_list_item_1,
                northSouth );
        cmbNorS_DDMMSS.setAdapter(northAdaptor);

        List<String> eastWest = new ArrayList<String>();
        eastWest.add("E");
        eastWest.add("W");
        ArrayAdapter<String> eastAdaptor = new ArrayAdapter<String>(
                context,
                android.R.layout.simple_list_item_1,
                eastWest );
        cmbEorW_DDMMSS.setAdapter(eastAdaptor);


        List<String> utmNumbers = new ArrayList<String>();
        for (int i = 1; i <= 60; i++) {
            utmNumbers.add(Integer.toString(i));
        }
        ArrayAdapter<String> utmNumberAdaptor = new ArrayAdapter<String>(
                context,
                android.R.layout.simple_list_item_1,
                utmNumbers );
        cmbUtmZoonE.setAdapter(utmNumberAdaptor);


        List<String> utmAlph = new ArrayList<String>();
        for (char i = 'A'; i <= 'Z'; i++) {
            utmAlph.add(Character.toString(i));
        }
        ArrayAdapter<String> utmAlphAdaptor = new ArrayAdapter<String>(
                context,
                android.R.layout.simple_list_item_1,
                utmAlph );
        cmbUtmZoonN.setAdapter(utmAlphAdaptor);


        //-------------
        CurrentSpinnerValue = positionFormat == hMapTools.UTM? 3 : positionFormat == hMapTools.DecimalDegrees?2:positionFormat == hMapTools.DegreesDecimalMinutes?1:0;
        cmbPositionFormats.setSelection(CurrentSpinnerValue);
        setPosition(CurrentSpinnerValue, position,
                cmbNorS_DDMMSS, cmbEorW_DDMMSS, txtN_SS, txtN_MM, txtN_DD, txtE_SS, txtE_MM, txtE_DD, txtUtmEasting, txtUtmNorthing, cmbUtmZoonE, cmbUtmZoonN);

        cmbPositionFormats.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int index, long idValue) {

                LinearLayout.LayoutParams statWidth = new LinearLayout.LayoutParams(
                        120, cmbNorS_DDMMSS_Container.getHeight(),0f);
                LinearLayout.LayoutParams dayWidth = new LinearLayout.LayoutParams(
                        00,cmbNorS_DDMMSS_Container.getHeight(),1f);

                txtUtmEasting.setLayoutParams(dayWidth);
                txtUtmNorthing.setLayoutParams(dayWidth);


                if (index == 3) {
                    divDDMMSS.setVisibility(View.GONE);
                    divUtm.setVisibility(View.VISIBLE);
                }
                else{
                    divDDMMSS.setVisibility(View.VISIBLE);
                    divUtm.setVisibility(View.GONE);
                    if (index == 0){
                        txtN_DD.setInputType(InputType.TYPE_CLASS_NUMBER);
                        txtN_DD.setFilters(new InputFilter[] {new InputFilter.LengthFilter(3)});
                        txtN_DD.setLayoutParams(statWidth);
                        txtN_MM.setInputType(InputType.TYPE_CLASS_NUMBER);
                        txtN_MM.setFilters(new InputFilter[] {new InputFilter.LengthFilter(2)});
                        txtN_MM.setLayoutParams(statWidth);
                        txtN_SS.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        txtN_SS.setFilters(new InputFilter[] {new InputFilter.LengthFilter(5)});
                        txtN_SS.setLayoutParams(dayWidth);

                        txtN_DD.setVisibility(View.VISIBLE);
                        lblN_MinuteSign.setVisibility(View.VISIBLE);
                        txtN_MM.setVisibility(View.VISIBLE);
                        lblN_SecondSign.setVisibility(View.VISIBLE);
                        txtN_SS.setVisibility(View.VISIBLE);


                        txtE_DD.setInputType(InputType.TYPE_CLASS_NUMBER);
                        txtE_DD.setFilters(new InputFilter[] {new InputFilter.LengthFilter(3)});
                        txtE_DD.setLayoutParams(statWidth);
                        txtE_MM.setInputType(InputType.TYPE_CLASS_NUMBER);
                        txtE_MM.setFilters(new InputFilter[] {new InputFilter.LengthFilter(2)});
                        txtE_MM.setLayoutParams(statWidth);
                        txtE_SS.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        txtE_SS.setFilters(new InputFilter[] {new InputFilter.LengthFilter(5)});
                        txtE_SS.setLayoutParams(dayWidth);

                        txtE_DD.setVisibility(View.VISIBLE);
                        lblE_MinuteSign.setVisibility(View.VISIBLE);
                        txtE_MM.setVisibility(View.VISIBLE);
                        lblE_SecondSign.setVisibility(View.VISIBLE);
                        txtE_SS.setVisibility(View.VISIBLE);
                    }
                    else if (index == 1){
                        txtN_DD.setInputType(InputType.TYPE_CLASS_NUMBER);
                        txtN_DD.setFilters(new InputFilter[] {new InputFilter.LengthFilter(3)});
                        txtN_DD.setLayoutParams(statWidth);
                        txtN_MM.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        txtN_MM.setFilters(new InputFilter[] {new InputFilter.LengthFilter(7)});
                        txtN_MM.setLayoutParams(dayWidth);

                        txtN_DD.setVisibility(View.VISIBLE);
                        lblN_MinuteSign.setVisibility(View.VISIBLE);
                        txtN_MM.setVisibility(View.VISIBLE);
                        lblN_SecondSign.setVisibility(View.GONE);
                        txtN_SS.setVisibility(View.GONE);

                        txtE_DD.setInputType(InputType.TYPE_CLASS_NUMBER);
                        txtE_DD.setFilters(new InputFilter[] {new InputFilter.LengthFilter(3)});
                        txtE_DD.setLayoutParams(statWidth);
                        txtE_MM.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        txtE_MM.setFilters(new InputFilter[] {new InputFilter.LengthFilter(7)});
                        txtE_MM.setLayoutParams(dayWidth);


                        txtE_DD.setVisibility(View.VISIBLE);
                        lblE_MinuteSign.setVisibility(View.VISIBLE);
                        txtE_MM.setVisibility(View.VISIBLE);
                        lblE_SecondSign.setVisibility(View.GONE);
                        txtE_SS.setVisibility(View.GONE);
                    }
                    else if (index == 2){
                        txtN_DD.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        txtN_DD.setFilters(new InputFilter[] {new InputFilter.LengthFilter(9)});
                        txtN_DD.setLayoutParams(dayWidth);

                        txtN_DD.setVisibility(View.VISIBLE);
                        lblN_MinuteSign.setVisibility(View.GONE);
                        txtN_MM.setVisibility(View.GONE);
                        lblN_SecondSign.setVisibility(View.GONE);
                        txtN_SS.setVisibility(View.GONE);

                        txtE_DD.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        txtE_DD.setFilters(new InputFilter[] {new InputFilter.LengthFilter(9)});
                        txtE_DD.setLayoutParams(dayWidth);

                        txtE_DD.setVisibility(View.VISIBLE);
                        lblE_MinuteSign.setVisibility(View.GONE);
                        txtE_MM.setVisibility(View.GONE);
                        lblE_SecondSign.setVisibility(View.GONE);
                        txtE_SS.setVisibility(View.GONE);
                    }
                }
                LatLng tempOldSelected = GetPositionFromFields(cmbNorS_DDMMSS, cmbEorW_DDMMSS, txtN_SS, txtN_MM, txtN_DD, txtE_SS, txtE_MM, txtE_DD, txtUtmEasting, txtUtmNorthing, cmbUtmZoonE, cmbUtmZoonN);
                CurrentSpinnerValue = index;
                setPosition(index, tempOldSelected,
                        cmbNorS_DDMMSS, cmbEorW_DDMMSS, txtN_SS, txtN_MM, txtN_DD, txtE_SS, txtE_MM, txtE_DD, txtUtmEasting, txtUtmNorthing, cmbUtmZoonE, cmbUtmZoonN);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        btnAccept.setOnClickListener(PosetiveListener);
        btnCancel.setOnClickListener(NegativeListener);
        return alertDialogBuilder;
    }


    static void setPosition(int nSpinnerValue, LatLng inputLatLanIfNotNull,
                            Spinner cmbNorS_DDMMSS, Spinner cmbEorW_DDMMSS, TextView txtN_SS, TextView txtN_MM, TextView txtN_DD, TextView txtE_SS, TextView txtE_MM, TextView txtE_DD, EditText txtUtmEasting, EditText txtUtmNorthing, Spinner cmbUtmZoonE, Spinner cmbUtmZoonN){
        if (inputLatLanIfNotNull == null){
            inputLatLanIfNotNull = GetPositionFromFields(cmbNorS_DDMMSS, cmbEorW_DDMMSS, txtN_SS, txtN_MM, txtN_DD, txtE_SS, txtE_MM, txtE_DD, txtUtmEasting, txtUtmNorthing, cmbUtmZoonE, cmbUtmZoonN);
        }
        if (inputLatLanIfNotNull != null){
            if (nSpinnerValue == 0){
                double absolute = Math.abs(inputLatLanIfNotNull.latitude);
                int degrees = (int)Math.floor(absolute);
                double minutesNotTruncated = (absolute - degrees) * 60;
                int minutes = (int)Math.floor(minutesNotTruncated);
                double seconds = ((minutesNotTruncated - minutes) * 60);

                txtN_DD.setText(String.valueOf(degrees));
                txtN_MM.setText(String.valueOf(minutes));
                txtN_SS.setText(String.valueOf(seconds * 10f / 10f));

                absolute = Math.abs(inputLatLanIfNotNull.longitude);
                degrees = (int)Math.floor(absolute);
                minutesNotTruncated = (absolute - degrees) * 60;
                minutes = (int)Math.floor(minutesNotTruncated);
                seconds = ((minutesNotTruncated - minutes) * 60);

                txtE_DD.setText(String.valueOf(degrees));
                txtE_MM.setText(String.valueOf(minutes));
                txtE_SS.setText(String.valueOf(seconds * 10f / 10f));
            }
            else if (nSpinnerValue == 1){
                double absolute = Math.abs(inputLatLanIfNotNull.latitude);
                int degrees = (int)Math.floor(absolute);
                double minutesNotTruncated = (absolute - degrees) * 60;

                txtN_DD.setText(String.valueOf(degrees));
                txtN_MM.setText(String.valueOf(((int)(minutesNotTruncated * app.MinutePrecisionTen)/app.MinutePrecisionTen)));
//                txtN_SS.setText(String.valueOf(seconds));
                absolute = Math.abs(inputLatLanIfNotNull.longitude);
                degrees = (int)Math.floor(absolute);
                minutesNotTruncated = (absolute - degrees) * 60;

                txtE_DD.setText(String.valueOf(degrees));
                txtE_MM.setText(String.valueOf(((int)(minutesNotTruncated * app.MinutePrecisionTen)/app.MinutePrecisionTen)));
            }
            else if(nSpinnerValue == 2){
                txtN_DD.setText(String.valueOf(((int)(inputLatLanIfNotNull.latitude * app.DegreePrecisionTen)/app.DegreePrecisionTen)));
                txtE_DD.setText(String.valueOf(((int)(inputLatLanIfNotNull.longitude * app.DegreePrecisionTen)/app.DegreePrecisionTen)));
            }
            else if (nSpinnerValue == 3){
                Deg2UTM deg2UTM = new Deg2UTM(inputLatLanIfNotNull.latitude, inputLatLanIfNotNull.longitude, app.DegreePrecision, app.UtmPrecision);
                cmbUtmZoonE.setSelection(deg2UTM.Zone - 1 );
                cmbUtmZoonN.setSelection(deg2UTM.Letter - 'A');
                txtUtmEasting.setText(String.valueOf(deg2UTM.Easting));
                txtUtmNorthing.setText(String.valueOf(deg2UTM.Northing));
            }
            if (inputLatLanIfNotNull.latitude > 0)
                cmbNorS_DDMMSS.setSelection(0);
            else
                cmbNorS_DDMMSS.setSelection(1);
            if (inputLatLanIfNotNull.longitude > 0)
                cmbEorW_DDMMSS.setSelection(0);
            else
                cmbEorW_DDMMSS.setSelection(1);

        }
        //set inputlatLan to textboxes
    }

    public static LatLng GetPosition(AlertDialog promptsView){
        final Spinner cmbPositionFormats = promptsView.findViewById(R.id.cmbPositionFormats);
        final Spinner cmbNorS_DDMMSS = promptsView.findViewById(R.id.cmbNorS_DDMMSS);
        final Spinner cmbEorW_DDMMSS = promptsView.findViewById(R.id.cmbEorW_DDMMSS);
        final TextView lblN_DegreeSign = promptsView.findViewById(R.id.lblN_DegreeSign);
        final TextView lblN_MinuteSign = promptsView.findViewById(R.id.lblN_MinuteSign);
        final TextView lblN_SecondSign = promptsView.findViewById(R.id.lblN_SecondSign);
        final TextView txtN_SS = promptsView.findViewById(R.id.txtN_SS);
        final TextView txtN_MM = promptsView.findViewById(R.id.txtN_MM);
        final TextView txtN_DD = promptsView.findViewById(R.id.txtN_DD);
        final TextView lblE_DegreeSign = promptsView.findViewById(R.id.lblE_DegreeSign);
        final TextView lblE_MinuteSign = promptsView.findViewById(R.id.lblE_MinuteSign);
        final TextView lblE_SecondSign = promptsView.findViewById(R.id.lblE_SecondSign);
        final TextView txtE_SS = promptsView.findViewById(R.id.txtE_SS);
        final TextView txtE_MM = promptsView.findViewById(R.id.txtE_MM);
        final TextView txtE_DD = promptsView.findViewById(R.id.txtE_DD);
        final EditText txtUtmEasting = promptsView.findViewById(R.id.txtUtmEasting);
        final EditText txtUtmNorthing = promptsView.findViewById(R.id.txtUtmNorthing);
        final Spinner cmbUtmZoonE = promptsView.findViewById(R.id.cmbUtmZoonE);
        final Spinner cmbUtmZoonN = promptsView.findViewById(R.id.cmbUtmZoonN);

return GetPositionFromFields(cmbNorS_DDMMSS, cmbEorW_DDMMSS, txtN_SS, txtN_MM, txtN_DD, txtE_SS, txtE_MM, txtE_DD, txtUtmEasting, txtUtmNorthing, cmbUtmZoonE, cmbUtmZoonN);
    }
    static LatLng GetPositionFromFields(Spinner cmbNorS_DDMMSS, Spinner cmbEorW_DDMMSS, TextView txtN_SS, TextView txtN_MM, TextView txtN_DD, TextView txtE_SS, TextView txtE_MM, TextView txtE_DD, EditText txtUtmEasting, EditText txtUtmNorthing, Spinner cmbUtmZoonE, Spinner cmbUtmZoonN){
        LatLng inputLatLanIfNotNull = null;
        //Yaeni az rooye Current bekhoonesh
        try {
            if (CurrentSpinnerValue == 0){
                boolean isNorth = cmbNorS_DDMMSS.getSelectedItemPosition() == 0;
                boolean isEast = cmbEorW_DDMMSS.getSelectedItemPosition() == 0;
                int ddN = Integer.parseInt(txtN_DD.getText().toString());
                int mmN = Integer.parseInt(txtN_MM.getText().toString());
                double ssN = Double.parseDouble(txtN_SS.getText().toString());
                double ddNDegree = ddN + mmN / 60d + ssN / 3600d * (isNorth? 1: -1);
                int ddE = Integer.parseInt(txtE_DD.getText().toString());
                int mmE = Integer.parseInt(txtE_MM.getText().toString());
                double ssE = Double.parseDouble(txtE_SS.getText().toString());
                double ddEDegree = ddE + mmE / 60d + ssE / 3600d * (isEast? 1: -1);
                inputLatLanIfNotNull = new LatLng(ddNDegree, ddEDegree);
            }
            else if (CurrentSpinnerValue == 1){
                boolean isNorth = cmbNorS_DDMMSS.getSelectedItemPosition() == 0;
                boolean isEast = cmbEorW_DDMMSS.getSelectedItemPosition() == 0;
                int ddN = Integer.parseInt(txtN_DD.getText().toString());
                double mmN = Double.parseDouble(txtN_MM.getText().toString());
                double ddNDegree = ddN + mmN / 60d * (isNorth? 1: -1);
                int ddE = Integer.parseInt(txtE_DD.getText().toString());
                double mmE = Double.parseDouble(txtE_MM.getText().toString());
                double ddEDegree = ddE + mmE / 60d * (isEast? 1: -1);
                inputLatLanIfNotNull = new LatLng(ddNDegree, ddEDegree);
            }
            else if (CurrentSpinnerValue == 2){
                boolean isNorth = cmbNorS_DDMMSS.getSelectedItemPosition() == 0;
                boolean isEast = cmbEorW_DDMMSS.getSelectedItemPosition() == 0;
                double ddN = Double.parseDouble(txtN_DD.getText().toString());
                double ddNDegree = ddN * (isNorth? 1: -1);
                double ddE = Double.parseDouble(txtE_DD.getText().toString());
                double ddEDegree = ddE * (isEast? 1: -1);
                inputLatLanIfNotNull = new LatLng(ddNDegree, ddEDegree);
            }
            else if (CurrentSpinnerValue == 3){
//                int utmNumber = cmbUtmZoonE.getSelectedItemPosition() + 1;
//                int utmAlph =  (int)cmbUtmZoonN.getSelectedItemPosition() + (int)'A';
//                double utmNorthing = Double.parseDouble(txtUtmNorthing.getText().toString());
//                double utmEasting = Double.parseDouble(txtUtmEasting.getText().toString());
                String utmString = String.valueOf(cmbUtmZoonE.getSelectedItemPosition() + 1)+ " " + cmbUtmZoonN.getSelectedItem().toString() + " " +txtUtmEasting.getText().toString() + " " +txtUtmNorthing.getText().toString();
                UTM2Deg utm2Deg = new UTM2Deg(utmString, app.DegreePrecision);
                inputLatLanIfNotNull = new LatLng(utm2Deg.latitude, utm2Deg.longitude);
            }
        }
        catch (Exception ex){

        }
        return inputLatLanIfNotNull;
    }
//    public Spinner cmbPositionFormats;
//    public Spinner cmbNorS_DDMMSS, cmbNorS_DDMM, cmbNorS_DD;
//    public Spinner cmbEorW_DDMMSS, cmbEorW_DDMM, cmbEorW_DD;
//    public Spinner cmbUTMZoneNumber, cmbUtmZoneLetter;
//    LinearLayout divDDMMSS, divUtm, divDDMM, divDD;
//
//
//    private String name;
//    public String zip;
//    OnMyDialogResult mDialogResult; // the callback
//    Context context;
//
//    public DialogGetPosition(Context context) {
//        super(context);
//        this.context = context;
//        setContentView(R.layout.dialog_getposition);
//
//        initializeComponents();
//
//    }
//
//    private void initializeComponents() {
//        cmbPositionFormats = (Spinner) findViewById(R.id.cmbPositionFormats);
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
//                R.array.PositionFormats, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        cmbPositionFormats.setAdapter(adapter);
//
//        cmbNorS_DDMMSS = findViewById(R.id.cmbNorS_DDMMSS);
////        cmbNorS_DDMM = findViewById(R.id.cmbNorS_DDMM);
////        cmbNorS_DD = findViewById(R.id.cmbNorS_DD);
////        cmbEorW_DDMMSS = findViewById(R.id.cmbEorW_DDMMSS);
////        cmbEorW_DDMM = findViewById(R.id.cmbEorW_DDMM);
////        cmbEorW_DD = findViewById(R.id.cmbEorW_DD);
////        divDDMMSS = findViewById(R.id.divDDMMSS);
////        divDDMM = findViewById(R.id.divDDMM);
////        divDD = findViewById(R.id.divDD);
////        divUtm = findViewById(R.id.divUtm);
//
//
//
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        // same you have
//    }
//
////    private class OKListener implements android.view.View.OnClickListener {
////        @Override
////        public void onClick(View v) {
////            if( mDialogResult != null ){
////                mDialogResult.finish(String.valueOf(etName.getText()));
////            }
////            CustomDialog.this.dismiss();
////        }
////    }
//
//    public void setDialogResult(OnMyDialogResult dialogResult){
//        mDialogResult = dialogResult;
//    }
//
//    public interface OnMyDialogResult{
//        void finish(String result);
//    }
}