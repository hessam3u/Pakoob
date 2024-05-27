package mojafarin.pakoob;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import utils.TextFormat;

public class MapSelect_Dialog_GotoBank extends DialogFragment {
    public double price;
    public String message;
    public String discountMsg = "";
    public String link;
    TextView lblPrice, lblDiscountMsg, lblCurrencyName ;
    LinearLayout linMsg;
    Button btnGotoPayment, btnSkip;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.mapselect_gotobank, container, false);
        lblPrice = v.findViewById(R.id.lblPrice);
        lblCurrencyName = v.findViewById(R.id.lblCurrencyName);
        lblDiscountMsg = v.findViewById(R.id.lblDiscountMsg);
        linMsg = v.findViewById(R.id.linMsg);
        btnGotoPayment =  v.findViewById(R.id.btnGotoPayment);
        btnGotoPayment.setOnClickListener(view -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            startActivity(browserIntent);
            this.dismiss();
        });
        btnSkip =  v.findViewById(R.id.btnSkip);
        btnSkip.setOnClickListener(view -> {
            this.dismiss();
        });
        // Do all the stuff to initialize your custom view
        lblPrice.setText(TextFormat.GetStringFromDecimalPrice(price) );
        lblCurrencyName.setText(app.CurrencyName);
        if (discountMsg.length() > 0){
            lblDiscountMsg.setText(discountMsg);
            linMsg.setVisibility(View.VISIBLE);
        }
        else{
            linMsg.setVisibility(View.GONE);
        }
        return v;
    }
}