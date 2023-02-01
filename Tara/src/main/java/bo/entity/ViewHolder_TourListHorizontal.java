package bo.entity;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pakoob.tara.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import androidx.recyclerview.widget.RecyclerView;
import bo.sqlite.TTExceptionLogSQLite;
import utils.PicassoOnScrollListener;
import utils.PrjConfig;


public  class ViewHolder_TourListHorizontal extends RecyclerView.ViewHolder {
    TextView lblItemTitle;
    TextView lblItemSbuTitle;
    ImageView txtImage;
    Adapter_TTClubTour.OnItemClickListener itemClickFunction;
    Adapter_TTClubTour.OnItemLongClickListener itemLongClickFunction;
    LinearLayout itemMainPart;

    public ViewHolder_TourListHorizontal(View v, Adapter_TTClubTour.OnItemClickListener itemClickFunction, Adapter_TTClubTour.OnItemLongClickListener itemLongClickFunction) {
        super(v);
        itemMainPart = itemView.findViewById(R.id.itemMainPart);
        lblItemTitle = v.findViewById(R.id.lblItemTitle);
        lblItemSbuTitle = v.findViewById(R.id.lblItemSbuTitle);
        txtImage = v.findViewById(R.id.txtImage);
        this.itemClickFunction = itemClickFunction;
        this.itemLongClickFunction = itemLongClickFunction;
    }
    
    public void DoBind(TTClubTour clubTour, int position, Context context){
        try {
            this.lblItemTitle.setText(clubTour.Name);
            this.lblItemSbuTitle.setText(clubTour.getStartDateView());

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
            //Picasso.Builder builder = new Picasso.Builder(context);
            //builder.downloader(new OkHttp3Downloader(context));
            //builder.build().load(...) --------> ghablan intori bood, ama singleton kardamesh ke break nashe
            Picasso builder = Picasso.with(context);
            builder.load(clubTour.ImageLink).tag(PicassoOnScrollListener.TAG)
                    //.config(Bitmap.Config.RGB_565).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    //.placeholder((R.drawable.ic_launcher_background)) HHH 1400-01-10
                    //.error(R.drawable.ic_launcher_background)  HHH 1400-01-10
                    .into(this.txtImage);
        } catch (Exception ex) {
            TTExceptionLogSQLite.insert(ex.getMessage(), ex.getStackTrace().toString(), PrjConfig.frm_Component_TourListHorizontal, 100);
            Log.d("بازکردن", "Bind Hor Tour: " + ex.getMessage() + ex.getStackTrace());
            //Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }
}