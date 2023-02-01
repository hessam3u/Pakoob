package mojafarin.pakoob;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import utils.TextFormat;

public class MapSelect_Dialog_GotoBank extends DialogFragment {
    public double price;
    public String link;
    TextView lblPrice ;
    Button btnGotoPayment, btnSkip;
    EditText txtDiscountCode;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.mapselect_gotobank, container, false);
        lblPrice = v.findViewById(R.id.lblPrice);
        txtDiscountCode = v.findViewById(R.id.txtDiscountCode);
        btnGotoPayment =  v.findViewById(R.id.btnGotoPayment);
        btnGotoPayment.setOnClickListener(view -> {
            if (txtDiscountCode.getText().length() > 0)
                link = link + "&" + "code=" +txtDiscountCode.getText();
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            startActivity(browserIntent);
            this.dismiss();
        });
        btnSkip =  v.findViewById(R.id.btnSkip);
        btnSkip.setOnClickListener(view -> {
            this.dismiss();
        });
        // Do all the stuff to initialize your custom view
        lblPrice.setText(TextFormat.GetStringFromDecimalPrice(price) + " " + app.CurrencyName);
        return v;
    }
}