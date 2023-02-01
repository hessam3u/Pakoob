package bo.entity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.pakoob.tara.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import pakoob.ClubSearch;
import utils.PicassoOnScrollListener;

//ghabl az ezafe kardane LoadingViewHolder, extends RecyclerView.Adapter<TTClubTourAdapter.ViewHolder_TourList> bood
public class Adapter_TTClubTour extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<TTClubTour> items;
    private int rowLayout;
    private Context context;

    //Mitoonim Noo'e Item haye mokhtalefi ro dasht bashim, in baraye item haye maemooli va baedish baraye item-e loading:
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    OnItemClickListener itemClickFunction;
    OnItemLongClickListener itemLongClickFunction;

    public interface OnItemClickListener {
        void onItemClicked(TTClubTour post, int Position);
    }
    public interface OnItemLongClickListener {
        boolean onItemLongClicked(TTClubTour post, int Position);
    }


    public Adapter_TTClubTour(List<TTClubTour> recitems, int ItemLayoutFile, OnItemClickListener itemClickFunction, OnItemLongClickListener itemLongClickFunction) {
        addAll(recitems);
        this.rowLayout = ItemLayoutFile;
        this.itemClickFunction = itemClickFunction;
        this.itemLongClickFunction = itemLongClickFunction;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();

        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
            if (this.rowLayout == R.layout.tour_list_item){
            return new ViewHolder_TourList(view, itemClickFunction, itemLongClickFunction);
            }
            else{
                return new ViewHolder_TourListHorizontal(view, itemClickFunction, itemLongClickFunction);
            }
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.controls_list_is_loading, parent, false);
            return new ViewHolder_Loading(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder instanceof ViewHolder_TourList) {
            ((ViewHolder_TourList) viewHolder).DoBind(items.get(position), position, context);
        }
        if (viewHolder instanceof ViewHolder_TourListHorizontal) {
            ((ViewHolder_TourListHorizontal) viewHolder).DoBind(items.get(position), position, context);
        } else if (viewHolder instanceof ViewHolder_Loading) {
            //private void showLoadingView(LoadingViewHolder viewHolder, int position) {
            //        //ProgressBar would be displayed
            //
            //    }
        }
    }


    public void addAll(List<TTClubTour> recitems) {
        items = recitems;
        notifyDataSetChanged();
    }
    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }
    @Override
    public int getItemViewType(int position) {
        return items.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size():0;
    }
}
