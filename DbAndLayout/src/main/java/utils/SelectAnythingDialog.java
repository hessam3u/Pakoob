package utils;

import android.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import pakoob.DbAndLayout.R;

public abstract class SelectAnythingDialog {
    public AlertDialog alertDialog = null;
    public Context context;
    public EditText txtSearch ;
    public TextView lblTitle, lblDesc;
    public RecyclerView rvSearchResult ;
    public ProgressBar pageProgressBarIndet;
    public TextView txtSearchResult ;
    public TextView btnAccept;
    public TextView btnCancel;
    public OnItemSelected onItemSelected;
    public int viewIdToInfalate;
    public Fragment parentFragment;
    public SelectAnythingDialog(int viewIdToInfalate, Fragment parentFragment){
        this.parentFragment = parentFragment;
        this.viewIdToInfalate = viewIdToInfalate;
    }
    public interface OnItemSelected{
        void doOnItemSelected(Object selected, int position);
    }
    public AlertDialog.Builder GetBuilder(Context context, OnItemSelected onItemSelected, View.OnClickListener PosetiveListener, View.OnClickListener NegativeListener){
        this.context = context;
        this.onItemSelected = onItemSelected;
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(viewIdToInfalate, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        txtSearch = promptsView.findViewById(R.id.txtSearch);
        txtSearch.requestFocus();
        hutilities.showKeyboard(context, txtSearch);

        lblTitle = promptsView.findViewById(R.id.lblTitle);
        lblDesc = promptsView.findViewById(R.id.lblDesc);
        Log.e("نال", lblDesc ==  null?"NULL lblDesc":"OK lblDesc");
        rvSearchResult = promptsView.findViewById(R.id.rvSearchResult);
        Log.e("نال", rvSearchResult ==  null?"NULL rvSearchResult":"OK rvSearchResult");
        initRecyclerView();
        pageProgressBarIndet = promptsView.findViewById(R.id.pageProgressBarIndet);
        txtSearchResult = promptsView.findViewById(R.id.txtSearchResult);
        btnAccept = promptsView.findViewById(R.id.btnAccept);
        btnCancel = promptsView.findViewById(R.id.btnCancel);

        //Initialize Dialog...
        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                doOnTextChanged(charSequence, i, i1, i2);
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });
        btnAccept.setOnClickListener(PosetiveListener);
        btnCancel.setOnClickListener(NegativeListener);
        return alertDialogBuilder;
    }

    protected abstract void doOnTextChanged(CharSequence charSequence, int i, int i1, int i2);

    private void initRecyclerView() {
        rvSearchResult.setLayoutManager(new LinearLayoutManager(context));
        rvSearchResult.setHasFixedSize(true);
        rvSearchResult.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);//TourList.this bayad mibood
        rvSearchResult.setLayoutManager(layoutManager);
        //initRecyclerView();
//            //baraye namayeshe joda konandeh
        rvSearchResult.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));//parentActivity -> getActivity -> this bood

        //rvSearchResult.setAdapter(adapterSearch);
        //initAdapterSearch(new ArrayList<>());

        //اضافه کردن یک اداپتر خالی
        rvSearchResult.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @NotNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                return null;
            }

            @Override
            public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {

            }

            @Override
            public int getItemCount() {
                return 0;
            }
        });

        rvSearchResult.addOnItemTouchListener(new RecyclerTouchListener(context, rvSearchResult, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));
    }
}