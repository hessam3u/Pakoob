package utils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import pakoob.DbAndLayout.R;

public class PersianDateDialog {
    public AlertDialog alertDialog = null;
    public Context context;
    public LinearLayout lblTitle_Container;
    public TextView lblTitle, lblDesc;
    public NumberPicker txtYear, txtMonth, txtDay ;
    public TextView txtSelectedDate;
    public TextView btnAccept;
    public TextView btnCancel;
    public int viewIdToInfalate;
    int defYear, defMonth, defDay;
    public PersianDateDialog(int viewIdToInfalate, int defYear, int defMonth, int defDay){
        this.viewIdToInfalate = viewIdToInfalate;
        this.defMonth= defMonth;
        this.defYear= defYear;
        this.defDay= defDay;
    }
    public String getSelectedDate(){
        int month =txtMonth.getValue();
        int day =txtDay.getValue();
        return txtYear.getValue() + "/" + (month < 10?"0":"") + month + "/" + (day < 10?"0":"") + day;
    }
    void resetSelectedValue(){
        int month =txtMonth.getValue();
        if (month <= 6)
            txtDay.setMaxValue(31);
        else
            txtDay.setMaxValue(30);
        if(month == 12 && !MyDate.isLeapYearPersian(txtYear.getValue())){
            txtDay.setMaxValue(29);
        }
        txtDay.invalidate();

        int day =txtDay.getValue();
        txtSelectedDate.setText(txtYear.getValue() + "/" + (month < 10?"0":"") + month + "/" + (day < 10?"0":"") + day);
    }
    public AlertDialog.Builder GetBuilder(Context context,View.OnClickListener PosetiveListener, View.OnClickListener NegativeListener){
        this.context = context;;
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(viewIdToInfalate, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        lblTitle = promptsView.findViewById(R.id.lblTitle);
        lblTitle_Container = promptsView.findViewById(R.id.lblTitle_Container);
        lblDesc = promptsView.findViewById(R.id.lblDesc);

        txtSelectedDate = promptsView.findViewById(R.id.txtSelectedDate);
        txtYear = promptsView.findViewById(R.id.txtYear);
        txtMonth = promptsView.findViewById(R.id.txtMonth);
        txtDay = promptsView.findViewById(R.id.txtDay);

        txtYear.setMinValue(1300);
        txtYear.setMaxValue(1400);
        txtYear.setValue(defYear);
        txtMonth.setOnValueChangedListener((numberPicker, i, i1) -> {
            resetSelectedValue();
        });

        txtMonth.setDisplayedValues(MyDate.PersianMonths);
        txtMonth.setMinValue(1);
        txtMonth.setMaxValue(12);
        txtMonth.setValue(defMonth);
        txtMonth.setOnValueChangedListener((numberPicker, i, i1) -> {
            resetSelectedValue();
        });

        txtDay.setMinValue(1);
        txtDay.setMaxValue(30);
        txtDay.setValue(defDay);
        txtDay.setOnValueChangedListener((numberPicker, i, i1) -> {
            resetSelectedValue();
        });
        btnAccept = promptsView.findViewById(R.id.btnAccept);
        btnCancel = promptsView.findViewById(R.id.btnCancel);


        btnAccept.setOnClickListener(PosetiveListener);
        btnCancel.setOnClickListener(NegativeListener);

        resetSelectedValue();
        return alertDialogBuilder;
    }

}