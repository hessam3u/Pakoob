package pakoob;

import static utils.HFragment.stktrc2k;
import static utils.HFragment.stktrc2kt;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.pakoob.tara.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.List;

import bo.dbConstantsTara;
import bo.NewClasses.InsUpdRes;
import bo.entity.SearchRequestDTO;
import bo.NewClasses.SimpleRequest;
import bo.NewClasses.StringContentDTO;
import bo.entity.TTClubNameDTO;
import bo.entity.TTClubNameDTOList;
import bo.sqlite.TTExceptionLogSQLite;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.ImageTools;
import utils.MainActivityManager;
import utils.PrjConfig;
import utils.SelectAnythingDialog;
import utils.hutilities;
import utils.projectStatics;

public class SelectClubDialogForMyClub extends SelectAnythingDialog {
    private ClubSearch.TTClubNameDTOsAdapter adapterSearch;

    public SelectClubDialogForMyClub(Fragment parentFragment) {
        super(R.layout.dialog_selectanything, parentFragment);
    }
    @Override
    public AlertDialog.Builder GetBuilder(Context context, OnItemSelected itemSelectedListenerOnClose, View.OnClickListener PosetiveListener, View.OnClickListener NegativeListener) {
        AlertDialog.Builder res = super.GetBuilder(context
                , itemSelectedListenerOnClose, PosetiveListener, NegativeListener);

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
        if (st.length() > 2){
            callCount++;
            pageProgressBarIndet.setVisibility(View.VISIBLE);
            txtSearchResult.setVisibility(View.GONE);

            SearchRequestDTO requestDTO = new SearchRequestDTO();
            requestDTO.Filter = txtSearch.getText().toString() + "***";
            if (txtSearch.getText().toString().trim().length() > 0)
                requestDTO.Sort = "FollowerCount desc, name asc";

            Call<ResponseBody> call = dbConstantsTara.apiTara.GetClubNamesWithFilter(SimpleRequest.getInstance(requestDTO));

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
                            TTClubNameDTOList result = TTClubNameDTOList.fromBytes(response.body().bytes());
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
                        TTExceptionLogSQLite.insert(ex.getMessage(), stktrc2k(ex), PrjConfig.frm_SelectClub, 101);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    //if (!isAdded()) return;
                    try {
                        TTExceptionLogSQLite.insert(t.getMessage(), stktrc2kt(t), PrjConfig.frm_SelectClub, 100);
                        callCount--;
                        //divSearch.setVisibility(View.VISIBLE);
                        pageProgressBarIndet.setVisibility(View.INVISIBLE);
                        txtSearchResult.setVisibility(View.VISIBLE);
                        txtSearchResult.setText("متاسفانه مشکلی در برقراری ارتباط با سرور به وجود آمده است. لطفا بعدا مجددا تلاش نمایید.");
                    }
                    catch (Exception ex){
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

    void initAdapterSearch(List<TTClubNameDTO> result) {
        if (true || adapterSearch == null) {
            ClubSearch.TTClubNameDTOsAdapter.OnItemClickListener itemClickListener = (post, Position) -> {
                RecyclerView_ItemClicked(post, Position);
            };
            adapterSearch = new ClubSearch.TTClubNameDTOsAdapter(context, "simple", itemClickListener, null, this.parentFragment);
            adapterSearch.setData(result);
            rvSearchResult.setAdapter(adapterSearch);
        }
        if (rvSearchResult.getAdapter() == null) {
        }
    }

    public boolean STATUS_SAVING = false;
    private void RecyclerView_ItemClicked(TTClubNameDTO currentObj, int position) {
        TTClubNameDTO currentSelectedClub  = currentObj;
        hutilities.hideKeyboard(context, txtSearch);

        //Start Selecting

        if (hutilities.CCustomerId == 0) {
            projectStatics.showDialog(context, context.getResources().getString(R.string.need_loginOrRegister)
                    , context.getResources().getString(R.string.need_loginOrRegister_Desc)
                    , context.getResources().getString(R.string.ok)
                    , view -> {((MainActivityManager)context).showLoginProcess("clubsearch");}, "", null);

            return;
        }
        if (!hutilities.isInternetConnected(context)) {
            projectStatics.showDialog(context, context.getResources().getString(R.string.NoInternet), context.getResources().getString(R.string.NoInternet_Desc), context.getResources().getString(R.string.ok), view -> {}, "", null);
            return;
        }
        if (STATUS_SAVING)
            return;
        byte nSelectStatus = 1;

        StringContentDTO contentDTO = StringContentDTO.getInstance(currentObj.TTClubNameId + "***" + nSelectStatus);
        pageProgressBarIndet.setVisibility(View.VISIBLE);

        SimpleRequest request = SimpleRequest.getInstance(contentDTO);
        Call<ResponseBody> call = dbConstantsTara.apiTara.DoSelectMainClub(request);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //if (!isAdded()) return;
                try {
                    if (response.isSuccessful()) {
                        InsUpdRes res = InsUpdRes.fromBytes(response.body().bytes());
                        Log.e("برگشت", "OK");
                        if (res.isOk) {
                            dbConstantsTara.session.setMyClubName(currentObj.Name);
                            dbConstantsTara.session.setMyClubNameIds(currentObj.TTClubNameId);
                            //Delete Old Logo
                            String getCurrentLogoFile = dbConstantsTara.session.getMyClubLogo();
                            if (getCurrentLogoFile.length() > 0){
                                Log.e("بریم برای حذف", ""+getCurrentLogoFile);
                                File file = new File(getCurrentLogoFile);
                                file.delete();
                            }
                            dbConstantsTara.session.setMyClubLogo(saveClubLogo(context, currentObj.Logo));

                            projectStatics.showDialog(context, context.getResources().getString(R.string.ClubSelected), context.getResources().getString(R.string.ClubSelected_Desc).replace("@@@", currentObj.Name), context.getResources().getString(R.string.Continue), view -> {
                                ClubView_Home currentHomeFragment = ClubView_Home.getInstance(currentSelectedClub, 0);
                                ((MainActivityManager) context).showFragment(currentHomeFragment);

                                //فراخوانی تابع خارجی کلیک در صورتی که نال نبود
                                if (onItemSelected != null)
                                    onItemSelected.doOnItemSelected(currentObj, position);

                                alertDialog.dismiss();
                                hutilities.hideKeyboard((Activity)context);

                            }, "", null);
                        } else {
                            projectStatics.showDialog(context, context.getResources().getString(R.string.dialog_UnknownError), res.message, context.getResources().getString(R.string.ok)
                                    , null, "", null);
                        }
                    } else {
                        projectStatics.ManageCallResponseErrors(true, PrjConfig.frmClubSearch,  100, context, response.code());
                    }
                } catch (Exception ex) {ex.printStackTrace();}

                pageProgressBarIndet.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                try {
                    projectStatics.ManageCallExceptions(true, PrjConfig.frmClubSearch, 100, t, context);
                    //if (!isAdded()) return;
                    pageProgressBarIndet.setVisibility(View.INVISIBLE);
                }
                catch (Exception ex){
                    //Unhandlled-Amdi
                }
            }
        });
    }
    static Target target;
    public static String saveClubLogo(Context context, String logo){
        try {
            if (logo == null || logo.length() == 0 || logo.contains("/images/")){
                return "";
            }

            String path = context.getFilesDir() + "/" + PrjConfig.PrivateFolder;
            String filename=logo.substring(logo.lastIndexOf("/")+1);
            target = ImageTools.picassoImageTarget(context, path, filename, null);
            Picasso.get().load(logo).into(target);

            return path + File.separator + filename;
        } catch (Exception ex) {
            Log.e("خطا" , ex.getMessage());
            TTExceptionLogSQLite.insert(ex.getMessage(), stktrc2k(ex), PrjConfig.frm_SelectClub, 103);
            ex.printStackTrace();
        }
        return "";
    }
}