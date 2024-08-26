package bo.entity;

import static utils.HFragment.stktrc2k;

import android.content.Context;
import android.media.projection.MediaProjection;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pakoob.tara.R;
import com.squareup.picasso.Picasso;

import androidx.recyclerview.widget.RecyclerView;

import bo.sqlite.TTExceptionLogSQLite;
import utils.PicassoOnScrollListener;
import utils.PicassoTrustAll;
import utils.PrjConfig;


public class ViewHolder_TourList extends RecyclerView.ViewHolder {
    TextView txt_ct_CityName;
    TextView txt_ct_TourLengthView;
    TextView txt_ct_StartDateViewMonthText;
    TextView txt_ct_ClubTourCategoryIdView;
    TextView txt_ct_ClubName;
    TextView txt_ct_Name;
    ImageView txt_ct_ImageLinkUri;
    Adapter_TTClubTour.OnItemClickListener itemClickFunction;
    Adapter_TTClubTour.OnItemLongClickListener itemLongClickFunction;
    LinearLayout itemMainPart;

    public ViewHolder_TourList(View v, Adapter_TTClubTour.OnItemClickListener itemClickFunction, Adapter_TTClubTour.OnItemLongClickListener itemLongClickFunction) {
        super(v);
        this.itemClickFunction = itemClickFunction;
        this.itemLongClickFunction = itemLongClickFunction;
        itemMainPart = itemView.findViewById(R.id.itemMainPart);
        txt_ct_StartDateViewMonthText = v.findViewById(R.id.txt_ct_StartDateViewMonthText);
        txt_ct_CityName = v.findViewById(R.id.txt_ct_CityName);
        txt_ct_Name = v.findViewById(R.id.txt_ct_Name);
        txt_ct_ClubName = v.findViewById(R.id.txt_ct_ClubName);
        txt_ct_TourLengthView = v.findViewById(R.id.txt_ct_TourLengthView);
        txt_ct_ClubTourCategoryIdView = v.findViewById(R.id.txt_ct_ClubTourCategoryIdView);
        txt_ct_ImageLinkUri = v.findViewById(R.id.txtImage);
    }

    Picasso picassoInstance;

    public void DoBind(TTClubTour clubTour, int position, Context context) {
        try {
            this.txt_ct_Name.setText(clubTour.getName());
            this.txt_ct_CityName.setText(clubTour.CityName);
            this.txt_ct_StartDateViewMonthText.setText(clubTour.getStartDateView());
            this.txt_ct_ClubName.setText(clubTour.ClubName);
            this.txt_ct_ClubTourCategoryIdView.setText(clubTour.ClubTourCategoryIdView);
            this.txt_ct_TourLengthView.setText(clubTour.getTourLenghtView());

            if (itemClickFunction != null) {
                itemMainPart.setOnClickListener(view -> {
                    itemClickFunction.onItemClicked(clubTour, position);
                });
            }
            if (itemLongClickFunction != null) {
                itemMainPart.setOnLongClickListener(view -> {
                    return itemLongClickFunction.onItemLongClicked(clubTour, position);
                });
            }

//            Picasso builder = Picasso.get();//#PicassoUpdate140303 with(context)
//            builder.load(clubTour.ImageLink).tag(PicassoOnScrollListener.TAG)//.config(Bitmap.Config.RGB_565).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
//                    .into(this.txt_ct_ImageLinkUri);

            if (picassoInstance == null)
                picassoInstance = PicassoTrustAll.getInstance(context);
            picassoInstance.load(clubTour.ImageLink)
                    .error(R.drawable.ac_peak2)
                    .into(this.txt_ct_ImageLinkUri);

        } catch (Exception ex) {
            TTExceptionLogSQLite.insert(ex.getMessage(), stktrc2k(ex), PrjConfig.frm_Component_TourListVer, 100);
            Log.d("بازکردن", "Bind Ver Tour: " + ex.getMessage() + ex.getStackTrace());
            //Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }
}