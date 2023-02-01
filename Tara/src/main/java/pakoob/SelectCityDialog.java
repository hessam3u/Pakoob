package pakoob;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.service.quickaccesswallet.GetWalletCardsResponse;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pakoob.tara.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import bo.NewClasses.CityDTOList;
import bo.dbConstantsTara;
import bo.entity.SearchRequestDTO;
import bo.NewClasses.SimpleRequest;
import bo.entity.CityDTO;
import bo.sqlite.TTExceptionLogSQLite;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.PrjConfig;
import utils.SelectAnythingDialog;
import utils.hutilities;
import utils.projectStatics;

public class SelectCityDialog extends SelectAnythingDialog {
    private CityDTOsAdapter adapterSearch;

    public SelectCityDialog(Fragment parentFragment) {
        super(R.layout.dialog_selectanything, parentFragment);
    }

    @Override
    public AlertDialog.Builder GetBuilder(Context context, OnItemSelected onItemSelected, View.OnClickListener PosetiveListener, View.OnClickListener NegativeListener) {
        AlertDialog.Builder res = super.GetBuilder(context, onItemSelected, PosetiveListener, NegativeListener);

        lblTitle.setText(context.getResources().getString(R.string.SelectCity_Title));
        lblDesc.setText(context.getResources().getString(R.string.SelectCity_Desc));
        lblDesc.setVisibility(View.GONE);
        return res;
    }



    int callCount = 0;
    @Override
    protected void doOnTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (!hutilities.isInternetConnected(context)) {
            projectStatics.showDialog(context, context.getResources().getString(R.string.NoInternet), context.getResources().getString(R.string.NoInternet_Desc), context.getResources().getString(R.string.ok), view -> {}, "", null);
            return;
        }
        String st = charSequence.toString();
        if (st.length() > 0){
            callCount++;

            pageProgressBarIndet.setVisibility(View.VISIBLE);
            txtSearchResult.setVisibility(View.GONE);

            SearchRequestDTO requestDTO = new SearchRequestDTO();
            requestDTO.Filter = txtSearch.getText().toString() + "***0";
//            if (txtSearch.getText().toString().trim().length() > 0)
//                requestDTO.Sort = "FollowerCount desc, name asc";

            Call<ResponseBody> call = dbConstantsTara.apiTara.GetCitiesByFilter(SimpleRequest.getInstance(requestDTO));

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    //if (!isAdded()) return;
                    try {
                        callCount--;
                        if (callCount > 1)
                            return;
                        //divSearch.setVisibility(View.VISIBLE);
                        pageProgressBarIndet.setVisibility(View.INVISIBLE);
                        if (response.isSuccessful()) {
                            CityDTOList result = CityDTOList.fromBytes(response.body().bytes());
                            if (!result.isOk) {
                                txtSearchResult.setVisibility(View.VISIBLE);
                                txtSearchResult.setText(result.message);
                            } else {
                                txtSearchResult.setVisibility(View.GONE);
                                initAdapterSearch(result.resList);
                                if (result.resList.size() > 0) {
                                    //HHH rvSearchResult.setVisibility(View.VISIBLE);
                                } else {
                                    //HHH rvSearchResult.setVisibility(View.GONE);
                                    txtSearchResult.setText(context.getResources().getString(R.string.NoItemFoundInSearch));
                                    txtSearchResult.setVisibility(View.VISIBLE);
                                }
                            }

                        } else {
                            txtSearchResult.setVisibility(View.VISIBLE);
                            txtSearchResult.setText("متاسفانه در برقراری ارتباط با سرور مشکلی به وجود آمده است. لطفا بعدا مجددا تلاش نمایید.");
                            Log.e("MY_ERROR", "ResponseCODE: " + response.code());
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();

                        Log.e("MY_ERROR", ex.getMessage());
                        TTExceptionLogSQLite.insert(ex.getMessage(), "", PrjConfig.frm_SelectCityDialog, 101);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    //if (!isAdded()) return;
                    try {
                        TTExceptionLogSQLite.insert(t.getMessage(), "", PrjConfig.frm_SelectCityDialog, 100);
                        callCount--;
                        //divSearch.setVisibility(View.VISIBLE);
                        pageProgressBarIndet.setVisibility(View.INVISIBLE);
                        txtSearchResult.setVisibility(View.VISIBLE);
                        txtSearchResult.setText("متاسفانه مشکلی در برقراری ارتباط با سرور به وجود آمده است. لطفا بعدا مجددا تلاش نمایید.");
                    }catch (Exception ex){
                        //Unhandlled-Amdi
                    }
                }
            });
        }
        else{
            //برای پاک کردن محتویات
            initAdapterSearch(new ArrayList<>());
        }
    }

    void initAdapterSearch(List<CityDTO> result) {
        if (true || adapterSearch == null) {
            CityDTOsAdapter.OnItemClickListener itemClickListener = (post, Position) -> {
                RecyclerView_ItemClicked(post, Position);
            };
            adapterSearch = new CityDTOsAdapter(context, "simple", itemClickListener);
            adapterSearch.setData(result);
            rvSearchResult.setAdapter(adapterSearch);
        }
        if (rvSearchResult.getAdapter() == null) {
        }
    }

    public boolean STATUS_SAVING = false;
    private void RecyclerView_ItemClicked(CityDTO currentObj, int position) {
        CityDTO currentSelectedClub  = currentObj;
        hutilities.hideKeyboard(context, txtSearch);

        //فراخوانی تابع خارجی کلیک در صورتی که نال نبود
        if (onItemSelected != null)
            onItemSelected.doOnItemSelected(currentObj, position);
    }

    public static class CityDTOsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<CityDTO> data;
        private Context context;
        private LayoutInflater layoutInflater;
        CityDTOsAdapter.OnItemClickListener itemClickFunction;
        String mode;

        public CityDTOsAdapter(Context context, String _mode, CityDTOsAdapter.OnItemClickListener _itemClickFunction) {
            this.mode = _mode;
            this.data = new ArrayList<>();
            this.context = context;
            this.itemClickFunction = _itemClickFunction;
        }

        @Override
        public CityDTOsAdapter.TTClubViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            layoutInflater = LayoutInflater.from(context);
            //this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = layoutInflater.inflate(R.layout.dialog_2lineitem, parent, false);
            return new CityDTOsAdapter.TTClubViewHolder(itemView, this.itemClickFunction);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            if (viewHolder instanceof CityDTOsAdapter.TTClubViewHolder) {
                ((CityDTOsAdapter.TTClubViewHolder) viewHolder).bind(data.get(position), position);
            } else if (1 == 2) {
                //private void showLoadingView(LoadingViewHolder viewHolder, int position) {
                //        //ProgressBar would be displayed
                //
                //    }
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public void setData(List<CityDTO> newData) {
            if (data != null) {
//                CityDTODiffCallback postDiffCallback = new CityDTODiffCallback(data, newData);
//                DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(postDiffCallback);

                data.clear();
                data.addAll(newData);
                //diffResult.dispatchUpdatesTo(this);
                notifyDataSetChanged();
            } else {
                // first initialization
                data = newData;
            }
        }

        //من اضافه کردم
        public abstract interface OnItemClickListener {
            public abstract void onItemClicked(CityDTO post, int Position);
        }

        class TTClubViewHolder extends RecyclerView.ViewHolder {
            public View itemView;
            public TextView txtLine2, txtLine1;
            LinearLayout itemMainPart;
            CityDTOsAdapter.OnItemClickListener itemClickFunction;

            TTClubViewHolder(View itemView, CityDTOsAdapter.OnItemClickListener itemClickFunction) {
                super(itemView);
                this.itemView = itemView;
                this.itemClickFunction = itemClickFunction;
                txtLine2 = itemView.findViewById(R.id.txtLine2);
                itemMainPart = itemView.findViewById(R.id.itemMainPart);
                txtLine1 = itemView.findViewById(R.id.txtLine1);
            }


            void bind(final CityDTO currentObj, int position) {
                if (currentObj != null) {
                    txtLine1.setText(currentObj.Name);
                    txtLine2.setText(currentObj.ProvinceName);

                    if (itemClickFunction != null) {
                        itemMainPart.setOnClickListener(view -> {
                            itemClickFunction.onItemClicked(currentObj, position);
                        });
                    }
                }
            }
        }

        final String TAG = "Downloading...";

        class CityDTODiffCallback extends DiffUtil.Callback {

            private final List<CityDTO> oldCityDTOs, newCityDTOs;

            public CityDTODiffCallback(List<CityDTO> oldCityDTOs, List<CityDTO> newCityDTOs) {
                this.oldCityDTOs = oldCityDTOs;
                this.newCityDTOs = newCityDTOs;
            }

            @Override
            public int getOldListSize() {
                return oldCityDTOs.size();
            }

            @Override
            public int getNewListSize() {
                return newCityDTOs.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return oldCityDTOs.get(oldItemPosition).CityId == newCityDTOs.get(newItemPosition).CityId;
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return oldCityDTOs.get(oldItemPosition).equals(newCityDTOs.get(newItemPosition));
            }
        }
    }

}