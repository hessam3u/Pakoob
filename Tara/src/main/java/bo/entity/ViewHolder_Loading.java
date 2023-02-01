package bo.entity;

import android.view.View;
import android.widget.ProgressBar;

import com.pakoob.tara.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder_Loading extends RecyclerView.ViewHolder {
    ProgressBar progressBar;
    public ViewHolder_Loading(@NonNull View itemView) {
        super(itemView);
        progressBar = itemView.findViewById(R.id.progressBar_in_controls_list_is_loading);
    }
}

