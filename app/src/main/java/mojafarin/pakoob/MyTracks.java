package mojafarin.pakoob;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import utils.HFragment;
import utils.MainActivityManager;
import utils.PrjConfig;
import bo.entity.NbPoi;
import bo.sqlite.NbPoiSQLite;
import bo.sqlite.TTExceptionLogSQLite;
import maptools.GPXFile;
import maptools.hMapTools;
import mojafarin.pakoob.mainactivitymodes.DialogMapBuilder;
import utils.CustomTypeFaceSpan;
import utils.ImageTools;
import utils.RecyclerTouchListener;
import utils.projectStatics;

import static bo.entity.NbPoi.Enums.PoiType_Folder;
import static bo.entity.NbPoi.Enums.ShowStatus_Show;

public class MyTracks extends HFragment {
    public static String ShowIcon = "\uE81B";
    public static String HideIcon = "\uE81C";
    LinearLayout divSearch;
    Toolbar toolbar;
    private NbPoisAdapter adapter;
    private RecyclerView rvItems;
    int selectedItemCount = 0;

    EditText txtSearch;
    RecyclerView rvSearchSafeGpx, rvReadyToDownload;
    TextView txtSafeGpxSearchReuslt, txtReadyToDownloadSearchReuslt;
    LinearLayout liSearchSafeGpxNoResult;

    private ProgressBar pageProgressBar;
    boolean isLoadingMore = false;
    final Integer readPageSize = 100;
    TextView txtUp_Text, txtUp_Icon;
    int lastFirstVisiblePosition =  -1;
    int lastItemClicked= -1;
    int currentLevel = 0;
    long currentId = 0;
    Stack parentStack = new Stack();
    LinearLayout btnUp;
    LinearLayout toolbarNormal, toolbarSelected;
    TextView btnCancelAtToolbar, btnDeleteAtToolbar;
    TextView txtPageTitle, txtTitleToolbarSelected, btnBack;
    Bitmap recyclerViewSelectedImage = null;

    private GoogleMap map;
    SupportMapFragment mapFragment;
    FrameLayout mapFrameLayout;
    FloatingActionButton btnSelectLayer, btnShowCustomMaps;
    Button btnSelectLocationOnMap;
    View v;

    String mode = "";
    String inputFileName = "";
    String preferedRootNameIfNeeded = "";
    InputStream inputStream = null;
    //1401-05-19 added
    public static MyTracks getInstance(String _mode, String _fileName, String _preferedRootNameIfNeeded, InputStream inputStream){
        MyTracks res = new MyTracks();
        res.inputFileName = _fileName;
        res.preferedRootNameIfNeeded = _preferedRootNameIfNeeded;
        res.mode = _mode;
        res.inputStream = inputStream;

        if (inputStream != null){
            //if inputStream not null, fileName is uri and must converted to Extention ONLY 1399-12-22
            int extentionIndex = _fileName.lastIndexOf('.');
            res.inputFileName = extentionIndex >= 0?_fileName.substring(0, extentionIndex):_fileName;
            if (res.inputFileName.length() > 4){
                res.inputFileName = "";
            }
        }
        return res;
    }
    public MyTracks(){}
    //1401-05-19 commented
//    public MyTracks(String _mode, String _fileName, String _preferedRootNameIfNeeded, InputStream inputStream){
//        inputFileName = _fileName;
//        preferedRootNameIfNeeded = _preferedRootNameIfNeeded;
//        this.mode = _mode;
//        this.inputStream = inputStream;
//
//        if (inputStream != null){
//            //if inputStream not null, fileName is uri and must converted to Extention ONLY 1399-12-22
//            int extentionIndex = _fileName.lastIndexOf('.');
//            inputFileName = extentionIndex >= 0?_fileName.substring(0, extentionIndex):_fileName;
//            if (inputFileName.length() > 4){
//                inputFileName = "";
//            }
//        }
//    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {//4th Event
        super.onViewCreated(view, savedInstanceState);
        initializeComponents(view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        }
        else{
            checkREAD_WRITE_PermissionForBefore_ANDROID_R(context);
        }
    }

    public static void checkREAD_WRITE_PermissionForBefore_ANDROID_R(Context context){
        //Checking Permission... NOTE: onRequestPermissionsResult is in PARENT Activity
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((AppCompatActivity) context,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions((AppCompatActivity) context,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        PrjConfig.MY_PERMISSIONS_READ_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }

        if (ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale((AppCompatActivity)context,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions((AppCompatActivity)context,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PrjConfig.MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.mytracks);
        launcher = new ActivityResultLauncher<Intent>() {
            @Override
            public void launch(Intent input, @Nullable ActivityOptionsCompat options) {
            }
            @Override
            public void unregister() {
            }
            @NonNull
            @Override
            public ActivityResultContract<Intent, ?> getContract() {
                return null;
            }
        };
    }
    TextView btnMoreSelected, btnMoreNormal;

    @Override
    public boolean onBackPressedInChild() {
       if (selectedItemCount > 0) {
           clearSelection();
           return false;
       }
       if (currentLevel > 0){
           btnUp_OnCLick();
           return false;
       }
        return super.onBackPressedInChild(); //true
    }

    @Override
    public void initializeComponents(View v) {
        this.v = v;


        //Added for safeGpx
        txtSearch = v.findViewById(R.id.txtSearch);
        txtSearch.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                btnImportTrackFile_Click();
                return true;
            }
            return true;
        });
        //End Added for safeGpx


        btnBack = v.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(view -> {((MainActivityManager) context).onBackPressed();});


//        ((MainActivity) context).reCreateDialogMapObj(getContext());
//        this.dialogMapObj = MainActivity.dialogMapObj;
//        this.dialogMap = MainActivity.dialogMap;


        pageProgressBar = v.findViewById(R.id.progressBar);


        toolbar = v.findViewById(R.id.toolbarOfPage);
//        setSupportActionBar(toolbar);
////        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle("");

        btnUp = v.findViewById(R.id.btnUp);
        btnUp.setOnClickListener(view -> {btnUp_OnCLick();});
        txtUp_Text = v.findViewById(R.id.txtUp_Text);
        txtUp_Icon = v.findViewById(R.id.txtUp_Icon);
        showHideBtnUp(View.GONE);


        //1402-04 درراستای خرابکاری مربوط به نقشه کامنت شد
//        mapFrameLayout = v.findViewById(R.id.mapFrameLayout);
//        //Set dimens of mapLayout
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int height = displayMetrics.heightPixels;
//        int width = displayMetrics.widthPixels;
//        ViewGroup.LayoutParams params = mapFrameLayout.getLayoutParams();
//        params.width = width - 90;
//        params.height = height - 250;
//        mapFrameLayout.setLayoutParams(params);

        toolbarNormal = v.findViewById(R.id.toolbarNormal);
        toolbarSelected = v.findViewById(R.id.toolbarSelected);
        btnDeleteAtToolbar = v.findViewById(R.id.btnDeleteAtToolbar);
        btnDeleteAtToolbar.setOnClickListener(view -> {btnDeleteAtToolbar_Click(view);});
        btnCancelAtToolbar = v.findViewById(R.id.btnCancelAtToolbar);
        btnCancelAtToolbar.setOnClickListener(view -> {
            clearSelection();changeToolbarStatus(false);});
        txtTitleToolbarSelected = v.findViewById(R.id.txtTitleToolbarSelected);
        txtPageTitle = v.findViewById(R.id.txtPageTitle);

        btnMoreSelected = v.findViewById(R.id.btnMoreSelected);
        btnMoreSelected.setOnClickListener(view -> {
            btnMoreSelected_Click();
        });
        btnMoreNormal = v.findViewById(R.id.btnMoreNormal);
        btnMoreNormal.setOnClickListener(view -> {
            btnMoreNormal_Click();
        });

        //init Adapter
        List<NbPoi> tmpDataSource =  NbPoiSQLite.selectByLevel(currentLevel, currentId);
        adapter = new NbPoisAdapter(context, null);
        adapter.setData(tmpDataSource);

        //---init recyclerView
        rvItems = this.v.findViewById(R.id.rvItems);//_root be khatere fragment ezafe shod. in tab'e bayad baed az onViewCreated estefade she va tooye onCreate ya OnCreateView estefade she
        //Click events
        rvItems.addOnItemTouchListener(new RecyclerTouchListener(context, rvItems, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
//                lastItemClicked = position;
//                Intent i = new Intent(context, MyTracks.class);
//                i.putExtra("ix", position);
//                startActivity(i);
            }
            @Override
            public void onLongClick(View view, int position) {
            }
        }));
        //Scroll Events
        rvItems.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

//                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//
//                if (!isLoadingMore) {
//                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == getSource().size() - 1) {
//                        //bottom of list!
//                        try {
//                            loadMore();
//                        } catch (ExecutionException e) {
//                            e.printStackTrace();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        isLoadingMore = true;
//                    }
//                }
            }
        });

        rvItems.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //setRecyclerScrollOnBack();//1399-10-07 Commented
            }
        });
        rvItems.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Log.d("onPauseOrResume", "onGlobalLayout" + lastFirstVisiblePosition);
//                        setRecyclerScrollOnBack();//1399-10-07 Commented
                        //At this point the layout is complete and the  dimensions of recyclerView and any child views are known.
                        //Remove listener after changed RecyclerView's height to prevent infinite loop
                        rvItems.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });

        rvItems.setLayoutManager(new LinearLayoutManager(context));
        //rvItems.setHasFixedSize(true);
        rvItems.setItemAnimator(new DefaultItemAnimator());
        rvItems.setAdapter(adapter);
        //برای نمایش جدا کننده
        rvItems.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));//parentActivity -> getActivity -> this bood

        recyclerViewSelectedImage = ImageTools.getRoundedCornerBitmap(ImageTools.getBitmapFromVectorDrawable(context, R.drawable.ic_done_24px, Color.rgb(10, 135, 16), Color.WHITE), 30);


    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if ((inputFileName != null && inputFileName.length() > 0) || inputStream != null){
            //Recieved File...
            String noNameWord = getString(R.string.New);
            String inputFileNameForMessage = inputStream != null? noNameWord: (inputFileName != null? inputFileName.substring(inputFileName.lastIndexOf(File.separator)+1):noNameWord);

            projectStatics.showDialog(context
                    , getResources().getString(R.string.ImportFile)
                    , getResources().getString(R.string.AreYouSureToImportFile).replace("XXX", inputFileNameForMessage)
                    , getResources().getString(R.string.ok)
                    , view1 -> {
//                    progressTiles.setVisibility(View.VISIBLE);

                        GPXFile importRes = null;
                        if (inputStream != null)
                            importRes = GPXFile.ImportGpxFileIntoMapbaz(inputStream, inputFileName, preferedRootNameIfNeeded, context, currentId, (byte) currentLevel, true, null);
                        else
                            importRes = GPXFile.ImportGpxFileIntoMapbaz(inputFileName, preferedRootNameIfNeeded, context, currentId, (byte)currentLevel, true, null);

                        if (importRes != null ) {
                            String msg = context.getResources().getString(R.string.dialog_fileImported);
                            if (importRes.MainFileName.length() > 0 && importRes.tracks.size() > 0)
                                msg =  context.getResources().getString(R.string.dialog_fileImportedWithXXXName).replace("XXX", importRes.MainFileName);
                            projectStatics.showDialog(context, context.getResources().getString(R.string.dialog_FileImported_title)
                                    , msg
                                    , context.getResources().getString(R.string.ok)
                                    , null, "", null);

                            NbPoi inserted = NbPoiSQLite.select(importRes.InnerDbId);
                            inserted.isSelected=false;
                            int oldSize = adapter.data.size();
                            if (inserted.PoiType == NbPoi.Enums.PoiType_Folder) {
                                adapter.data.add(0, inserted);
                                adapter.notifyItemInserted(0);
//                                if (oldSize == 0)
//                                    adapter.notifyDataSetChanged();
//                                else
//                                    adapter.notifyItemInserted(0);
                            }
                            else {
                                adapter.data.add(inserted);
                                adapter.notifyItemInserted(oldSize);
//                                if (oldSize == 0)
//                                    adapter.notifyDataSetChanged();
//                                else
//                                    adapter.notifyItemInserted(oldSize);
                            }
                        }
//                    progressTiles.setVisibility(View.INVISIBLE);

                    }
                    , getResources().getString(R.string.cancel), null);
        }
    }
    private void btnSelectLocationOnMap_Click() {
        dialogMap.dismiss();
    }

    private void btnAddFolderInToolbar_Click() {
        projectStatics.showEnterTextDialog(context, getResources().getString(R.string.EnterName_Title)
                ,getResources().getString(R.string.EnterName_Message)
                , "",getResources().getString(R.string.btnAccept)
                , view -> {
                    View parent = (View)view.getParent().getParent();

                    EditText txtInput = parent.findViewById(projectStatics.showEnterTextDialog_EditTextId);
                    String text = txtInput.getText().toString();
                    if (text.trim().length()== 0){
                        projectStatics.showDialog(context
                                , getResources().getString(R.string.vali_GeneralError_Title)
                                , getResources().getString(R.string.vali_PleaseEnterName)
                                , getResources().getString(R.string.ok)
                                , null
                                , ""
                                , null);
                        return;
                    }
                    NbPoi folderObj = NbPoi.getInstance(text, "", (byte)(currentLevel), currentId, "", 0d
                            , 0d, 0d, 0d, 0, ShowStatus_Show, PoiType_Folder
                            , 0, (byte)1, (byte)0, (byte)0, (byte)100, (byte)0, (byte)1, "", 1, 0d, 0d);
                    folderObj.NbPoiId = NbPoiSQLite.insert(folderObj);

                    adapter.data.add(0, folderObj);
                    adapter.notifyItemInserted(0);
                }
                , getResources().getString(R.string.btnCancel), null, 0
        , true);

    }

    private void clearSelection() {
        changeToolbarStatus(false);
        txtTitleToolbarSelected.setText("");
        selectedItemCount = 0;
        int size = adapter.data.size();
        for (int i = 0; i < size; i++) {
            NbPoi item = adapter.data.get(i);
            if (item.isSelected){
                RecyclerView.ViewHolder vh = rvItems.findViewHolderForAdapterPosition(i);
                if (vh != null){
                    NbPoisAdapter.NbPoiViewHolder innerHolder = (NbPoisAdapter.NbPoiViewHolder)vh;
                    innerHolder.imgSelected.setVisibility(View.GONE);
                }
                item.isSelected = false;
            }
        }
    }

    private void selectAll() {
        int size = adapter.data.size();
        if (size == 0)
            return;

        changeToolbarStatus(true);
        selectedItemCount = size;
        txtTitleToolbarSelected.setText(selectedItemCount + " " + context.getResources().getString(R.string.CountItemsSelected));

        for (int i = 0; i < size; i++) {
            NbPoi item = adapter.data.get(i);

            RecyclerView.ViewHolder vh = rvItems.findViewHolderForAdapterPosition(i);
            if (vh != null){
                NbPoisAdapter.NbPoiViewHolder innerHolder = (NbPoisAdapter.NbPoiViewHolder)vh;
                innerHolder.imgSelected.setVisibility(View.VISIBLE);
            }
            item.isSelected = true;
        }
        adapter.notifyDataSetChanged();
    }

    private void btnDeleteAtToolbar_Click(View view) {
        projectStatics.showDialog(context
                , getResources().getString(R.string.DeleteConfirm)
                , getResources().getString(R.string.AreYouSureToDeleteFiles)
                , getResources().getString(R.string.ok)
                , view1 -> {
//                    progressTiles.setVisibility(View.VISIBLE);
                    int size = adapter.data.size();
                    for (int i = size-1; i >= 0; i--) {
                        NbPoi item = adapter.data.get(i);
                        if (item.isSelected) {
                            GPXFile.DeleteNbPoiRec(item);
                            adapter.data.remove(i);
                            adapter.notifyItemRemoved(i);
                        }
                    }
                    clearSelection();
//                    progressTiles.setVisibility(View.INVISIBLE);

                }
                , getResources().getString(R.string.cancel), null);
    }

    private void btnUp_OnCLick() {
        if (currentLevel == 0)
            return;
        clearSelection();
        currentId = Long.parseLong(parentStack.pop().toString());
        currentLevel--;
        adapter.data = NbPoiSQLite.selectByLevel(currentLevel, currentId);
        adapter.notifyDataSetChanged();
        showHideBtnUp(currentLevel > 0?View.VISIBLE:View.GONE);
    }

    void changeToolbarStatus(boolean isSelectionMode){
        if (isSelectionMode){
            toolbarSelected.setVisibility(View.VISIBLE);
            toolbarNormal.setVisibility(View.GONE);
        }
        else{
            toolbarSelected.setVisibility(View.GONE);
            toolbarNormal.setVisibility(View.VISIBLE);
        }
        txtTitleToolbarSelected.setText(selectedItemCount + " " + context.getResources().getString(R.string.CountItemsSelected));
    }

    void showHideBtnUp(int visibility){
        if (visibility == View.VISIBLE){
            btnUp.setVisibility(View.VISIBLE);
            txtUp_Icon.setVisibility(View.VISIBLE);
            txtUp_Text.setText(context.getResources().getString(R.string.Up_Text));
        }
        else{
            btnUp.setVisibility(View.GONE);
            //txtUp_Icon.setVisibility(View.INVISIBLE);
            //txtUp_Text.setText(context.getResources().getString(R.string.Up_Text_EmptyForMyTracks));
        }
    }


    public boolean onBackPressed() {
        if (dialogMap != null && dialogMap.isShowing()) {
            dialogMap.dismiss();
            return false;
        } else if (currentLevel != 0){
            btnUp_OnCLick();
            return false;
        }
        else {
//            Intent intent = new Intent();
//            setResult(1100, intent);
//
//            finish();
            return true;
        }
    }
    private void btnMoreSelected_Click() {
        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(context, btnMoreSelected);
        //Inflating the Popup using xml file
        popup.getMenuInflater()
                .inflate(R.menu.mytrack_rightclickonselect, popup.getMenu());

        Menu menu = popup.getMenu();
        for (int i = 0; i < menu.size() ; i++) {
            MenuItem item = menu.getItem(i);
            CustomTypeFaceSpan.applyFontToMenuItem(item, context, Color.BLACK);

            if (selectedItemCount != 1){
                if (item.getItemId() == R.id.btnRename)
                    item.setVisible(false);
            }
        }
        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.btnExportGPX:
                        btnExportGPX_Click();
                        break;
                    case R.id.btnRename:
                        btnRename_Click();
                        break;
//                    case R.id.btnMoveToFolder:
//                        btnMoveToFolder_Click();
//                        break;
                }
                return true;
            }
        });

        popup.show(); //showing popup menu
    }

    private void btnMoreNormal_Click() {
        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(context, btnMoreNormal);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.mytrack_rightclick, popup.getMenu());

        Menu menu = popup.getMenu();
        for (int i = 0; i < menu.size() ; i++) {
            MenuItem item = menu.getItem(i);
            CustomTypeFaceSpan.applyFontToMenuItem(item, context, Color.BLACK);
        }
        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.btnAddFolderInToolbar:
                        btnAddFolderInToolbar_Click();
                        break;
                    case R.id.btnImportTrackFile:
                        btnImportTrackFile_Click();
                        break;
                    case R.id.btnSelectAll:
                        selectAll();
                        break;
                }
                return true;
            }
        });

        popup.show(); //showing popup menu
    }

    //انتقال به شاخه
    public void btnMoveToFolder_Click(){
        int size = adapter.data.size();
        List<NbPoi> folders = new ArrayList<>();
        List<NbPoi> selectedItems = new ArrayList<>();

        for (int i = size-1; i >= 0; i--) {
            NbPoi item = adapter.data.get(i);
            if (item.isSelected) {
                selectedItems.add(item);
            }
            else if(item.PoiType == PoiType_Folder){
                folders.add(item);
            }
        }
        if (folders.size() == 0){
            projectStatics.showDialog(context, getString(R.string.NoFolderExistsForTransfer_Title)
                    , getString(R.string.NoFolderExistsForTransfer)
                    , getResources().getString(R.string.btnAccept)
                    , null
                    , ""
                    , null);
            return;
        }



//        int size = adapter.data.size();
//        for (int i = size-1; i >= 0; i--) {
//            NbPoi item = adapter.data.get(i);
//            if (item.isSelected) {GPXFile.DeleteNbPoiRec(item);
//                adapter.data.remove(i);
//                adapter.notifyItemRemoved(i);
//            }
//        }
//        clearSelection();
    }

    private void btnRename_Click() {
        int size = adapter.data.size();
        for (int i = size-1; i >= 0; i--) {
            NbPoi item = adapter.data.get(i);
            if (item.isSelected) {
                int finalI = i;
                projectStatics.showEnterTextDialog(context, getResources().getString(R.string.EnterName_Title)
                        ,getResources().getString(R.string.EnterNewName_Message)
                        , item.Name,getResources().getString(R.string.btnAccept)
                        , view -> {
                            View parent = (View)view.getParent().getParent();

                            EditText txtInput  = parent.findViewById(projectStatics.showEnterTextDialog_EditTextId);
                            String text = txtInput.getText().toString();
                            if (text.trim().length()== 0){
                                projectStatics.showDialog(context
                                        , getResources().getString(R.string.vali_GeneralError_Title)
                                        , getResources().getString(R.string.vali_PleaseEnterName)
                                        , getResources().getString(R.string.ok)
                                        , null
                                        , ""
                                        , null);
                                return;
                            }

                            NbPoi poi = NbPoiSQLite.select(item.NbPoiId);
                            poi.Name = text;
                            NbPoiSQLite.update(poi);

                            adapter.data.set(finalI, poi);
                            adapter.notifyItemChanged(finalI);
                            clearSelection();
                        }
                        , getResources().getString(R.string.btnCancel), null, 0
                , true);
                break;
            }
        }
    }

    private ActivityResultLauncher<Intent> launcher; // Initialise this object in Activity.onCreate()
    private Uri baseDocumentTreeUri;

    private void btnExportGPX_Click() {
        if (false && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            launcher.launch(intent);
        }
        else{
            //Old androids
            File ff = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            String path =ff.getPath();
            saveSelectedData(path);

        }
//        new ChooserDialog(context)
//                .withFilter(true, true, "")
//                .withStartFile(path)
//                .withResources(R.string.fileChooser_SelectPathToSave, R.string.fileChooser_myTracks_btnSelect, R.string.fileChooser_myTracks_btnCancel)
//                .withChosenListener(new ChooserDialog.Result() {
//                    @Override
//                    public void onChoosePath(String path, File pathFile) {
//                        saveSelectedData(path);
//                    }
//                })
//                // to handle the back key pressed or clicked outside the dialog:
//                .withOnCancelListener(new DialogInterface.OnCancelListener() {
//                    public void onCancel(DialogInterface dialog) {
//                        Log.d("CANCEL", "CANCEL");
//                        dialog.cancel(); // MUST have
//                    }
//                })
//                .build()
//                .show();

    }
    void saveSelectedData(String path){
        int size = adapter.data.size();
        List<NbPoi> toConvert = new ArrayList<>();

        String fileNameToSave = "";
        if (currentId != 0){
            NbPoi current = NbPoiSQLite.select(currentId);
            fileNameToSave = current.Name;
        }
        String folderStructure = "";
        for (int i = size-1; i >= 0; i--) {
            NbPoi item = adapter.data.get(i);
            if (item.isSelected) {
                if (item.PoiType == NbPoi.Enums.PoiType_Folder) {
                    //folderStructure += (folderStructure.length() > 0?";":"") +
                    if (fileNameToSave.length() == 0) {
                        fileNameToSave = item.Name;
                    }
                    toConvert.addAll(NbPoiSQLite.SelectWithChilds(item, false));
                } else {
                    toConvert.add(item);
                }
            }
        }
        if (fileNameToSave.length() == 0){
            fileNameToSave = toConvert.get(0).Name;
        }
        //NbPoi lastItem = NbPoiSQLite.selectLastInserted();
        String content = GPXFile.ExportGPXToString(toConvert);
        File file = new File(path + File.separator + fileNameToSave + ".gpx");
        if (file.exists()){
            Random random = new Random();
            fileNameToSave = fileNameToSave + "-" + random.nextInt(10000);
            file = new File(path + File.separator + fileNameToSave + ".gpx" );
        }
        try {
            OutputStreamWriter writer =
                    new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            writer.write(content, 0, content.length());
            writer.close();
            String message =getResources().getString(R.string.SaveCompleted_Desc).replace("000",
                    getString(R.string.DownloadFolder)).replace("111", fileNameToSave+ ".gpx");
            projectStatics.showDialog(context, getResources().getString(R.string.SaveCompleted)
                    , message
                    , getResources().getString(R.string.btnAccept)
                    , null
                    , ""
                    , null);

            clearSelection();
        } catch (Exception e) {
            String friendlyMsg = getResources().getString(R.string.ErrorInSave_Desc);
            boolean isUnknownEx = true;
            if (e.getMessage().contains("EPERM")){
                friendlyMsg = getResources().getString(R.string.ErrorInSaveMimeType_Desc);
                isUnknownEx = false;
            }
            projectStatics.showDialog(context, getResources().getString(R.string.ErrorInSave)
                    ,friendlyMsg
                    , getResources().getString(R.string.btnAccept)
                    , null
                    , ""
                    , null);
            if (isUnknownEx) {
                TTExceptionLogSQLite.insert("FILE PATH:" + path + "----" + e.getMessage(), stktrc2k(e), PrjConfig.frmEditTrack, 300);
                e.printStackTrace();
            }
        }
    }

    private void btnImportTrackFile_Click() {
        projectStatics.showDialog(context, context.getResources().getString(R.string.dialog_FileImportingHelp_Title)
                , context.getResources().getString(R.string.dialog_FileImportingHelp_Desc)
                , context.getResources().getString(R.string.ok)
                , null, "", null);

//        String path ="";
//        new ChooserDialog(context)
//                .withFilter(false, true, "gpx")
//                .withStartFile(path)
//                .withResources(R.string.fileChooser_myTracks_title, R.string.fileChooser_myTracks_btnSelect, R.string.fileChooser_myTracks_btnCancel)
//                .withChosenListener(new ChooserDialog.Result() {
//                    @Override
//                    public void onChoosePath(String path, File pathFile) {
//                        //NbPoi lastItem = NbPoiSQLite.selectLastInserted();
//                        importGPXFromPath(path);
//                    }
//                })
//                // to handle the back key pressed or clicked outside the dialog:
//                .withOnCancelListener(new DialogInterface.OnCancelListener() {
//                    public void onCancel(DialogInterface dialog) {
//                        Log.d("CANCEL", "CANCEL");
//                        dialog.cancel(); // MUST have
//                    }
//                })
//                .build()
//                .show();
    }

    boolean importGPXFromPath(String path){
        GPXFile importRes = GPXFile.ImportGpxFileIntoMapbaz(path, preferedRootNameIfNeeded, context, currentId, (byte)currentLevel, true, null);
        if (importRes != null ) {
            projectStatics.showDialog(context, context.getResources().getString(R.string.dialog_FileImported_title)
                    , context.getResources().getString(R.string.dialog_fileImported)
                    , context.getResources().getString(R.string.ok)
                    , null, "", null);

            NbPoi inserted = NbPoiSQLite.select(importRes.InnerDbId);
            inserted.isSelected=false;
            int oldSize = adapter.data.size();
            if (inserted.PoiType == NbPoi.Enums.PoiType_Folder) {
                adapter.data.add(0, inserted);
                adapter.notifyItemInserted(0);
//                                if (oldSize == 0)
//                                    adapter.notifyDataSetChanged();
//                                else
//                                    adapter.notifyItemInserted(0);
            }
            else {
                adapter.data.add(inserted);
                adapter.notifyItemInserted(oldSize);
//                                if (oldSize == 0)
//                                    adapter.notifyDataSetChanged();
//                                else
//                                    adapter.notifyItemInserted(oldSize);
            }
        }

        return true;
    }


    public class NbPoisAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public List<NbPoi> data;
        private Context context;
        private LayoutInflater layoutInflater;
        private OnDeleteButtonClickListener onDeleteButtonClickListener;

        public NbPoisAdapter(Context context, OnDeleteButtonClickListener listener) {
            this.data = new ArrayList<>();
            this.context = context;
            this.onDeleteButtonClickListener = listener;
            this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public NbPoiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.mytracks_item, parent, false);
            return new NbPoiViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            if (viewHolder instanceof NbPoiViewHolder) {
                ((NbPoiViewHolder)viewHolder).bind(data.get(position), position);
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public void setData(List<NbPoi> newData) {
            if (data != null) {
                NbPoiDiffCallback postDiffCallback = new NbPoiDiffCallback(data, newData);
                DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(postDiffCallback);

                data.clear();
                data.addAll(newData);
                diffResult.dispatchUpdatesTo(this);
            } else {
                // first initialization
                data = newData;
            }
        }

        public abstract class OnDeleteButtonClickListener {
            public abstract void onDeleteButtonClicked(NbPoi post);
        }
        public class NbPoiViewHolder extends RecyclerView.ViewHolder {
            View itemView;
            TextView txtIcon, txtName;
            TextView btnShowHide, btnViewOnMap;
            public ImageView imgSelected;
            NbPoiViewHolder(View itemView) {
                super(itemView);
                this.itemView = itemView;
                txtIcon = itemView.findViewById(R.id.txtIcon);
                txtName = itemView.findViewById(R.id.txtName);
                btnShowHide = itemView.findViewById(R.id.btnShowHide);
                btnViewOnMap = itemView.findViewById(R.id.btnViewOnMap);
                imgSelected = itemView.findViewById(R.id.imgSelected);
            }

            void bind(final NbPoi currentObj, int position) {
                imgSelected.setImageBitmap(recyclerViewSelectedImage);
                if (currentObj.isSelected)
                    imgSelected.setVisibility(View.VISIBLE);
                else
                    imgSelected.setVisibility(View.GONE);
                //Color Theme : https://coolors.co/8ecae6-219ebc-023047-ffb703-fb8500
                txtIcon.setTypeface(projectStatics.getFontello_FONT(context));
                btnShowHide.setTypeface(projectStatics.getFontello_FONT(context));
                btnViewOnMap.setTypeface(projectStatics.getFontello_FONT(context));
                if (currentObj != null) {
                    if (currentObj.PoiType == NbPoi.Enums.PoiType_Folder){
                        txtIcon.setTextColor(Color.parseColor("#ffb703"));
                        txtIcon.setText("\uF114");
                    }
                    else if (currentObj.PoiType == NbPoi.Enums.PoiType_Track || currentObj.PoiType == NbPoi.Enums.PoiType_Route){
                        int color = currentObj.Color;//Color.parseColor("#FB8500");
//                        if (currentObj.Color > 0)
//                            color = currentObj.Color;
                        txtIcon.setTextColor(color);
                        txtIcon.setText("\uE823");
                    }
                    else if(currentObj.PoiType >= NbPoi.Enums.PoiType_Danger_Avalanche && currentObj.PoiType < 11000){
                        txtIcon.setTextColor(Color.parseColor("#d00000"));
                        txtIcon.setText("\uE809");
                    }
                    else if(currentObj.PoiType >= NbPoi.Enums.PoiType_Waypoint && currentObj.PoiType < 10000){
                        txtIcon.setTextColor(Color.parseColor("#219ebc"));
                        txtIcon.setText("\uE810");
                    }
                    txtName.setText(currentObj.Name);
                    btnShowHide.setText(currentObj.ShowStatus == NbPoi.Enums.ShowStatus_Hide?HideIcon:ShowIcon);
                    btnShowHide.setTextColor(Color.parseColor("#4d0089"));
                    btnShowHide.setOnClickListener(v -> {
                        btnShowHide_Click(currentObj, btnShowHide);
                    });
                    btnViewOnMap.setText("\uF279");
                    btnViewOnMap.setTextColor(Color.parseColor("#2a6502"));
                    btnViewOnMap.setOnClickListener(v -> {
                        showOnMap_Click(currentObj);
                    });
                    txtName.setOnClickListener(view -> {onRvItemClick(view, false, currentObj, imgSelected, position);});
                    txtIcon.setOnClickListener(view -> {onRvItemClick(view, false, currentObj, imgSelected, position);});
                    txtName.setOnLongClickListener(view ->{onRvItemClick(view, true, currentObj, imgSelected, position);return false;});
                }
            }

        }

        boolean longClickEventFired = false;
        void onRvItemClick(View view, boolean isLongClick, NbPoi item, ImageView imgSelected, int position){
            if (longClickEventFired){
                longClickEventFired = false;
                return;
            }
            if (isLongClick)
                longClickEventFired = true;

            boolean isSelectionMode = checkMakingSelection(isLongClick, view, item, imgSelected);
            if (isSelectionMode){
                return;
            }

            if (item.PoiType == NbPoi.Enums.PoiType_Folder){
                clearSelection();
                currentLevel++;
                parentStack.push(currentId);
                currentId = item.NbPoiId;
                adapter.data = NbPoiSQLite.selectByLevel(currentLevel, currentId);
                adapter.notifyDataSetChanged();
            }
            else if(item.PoiType == NbPoi.Enums.PoiType_Track || item.PoiType == NbPoi.Enums.PoiType_Route){
                ((MainActivity)context).openEditTrack(item.NbPoiId, "MyTracks", position, adapter);
            }
            else{
                ((MainActivity) context).showNbPoiOnMapForEdit(item.NbPoiId, item.Name);
                ((MainActivity) context).backToMapPage();
            }
            showHideBtnUp(currentLevel > 0?View.VISIBLE:View.GONE);
        }
        public boolean checkMakingSelection(boolean isLongClick, View view, NbPoi selected, ImageView imgSelected){
            if (selectedItemCount > 0 || isLongClick){
                if (selected.isSelected){
                    selectedItemCount--;
                    imgSelected.setVisibility(View.GONE);
                }else{
                    selectedItemCount++;
                    imgSelected.setVisibility(View.VISIBLE);
                }
                selected.isSelected = !selected.isSelected;
                changeToolbarStatus(selectedItemCount > 0);
                return true;
            }
            return false;
        }

        void btnShowHide_Click(NbPoi selectedItem, TextView btnShowHide){
            byte ShowStatus = NbPoi.Enums.ShowStatus_Show;
            if (btnShowHide.getText().equals(ShowIcon)){
                ShowStatus = NbPoi.Enums.ShowStatus_Hide;
                btnShowHide.setText(HideIcon);
            }
            else{
                btnShowHide.setText(ShowIcon);
            }
            List<NbPoi> res = app.SetShowHide(selectedItem, ShowStatus, true, context);

        }


        class NbPoiDiffCallback extends DiffUtil.Callback {

            private final List<NbPoi> oldNbPois, newNbPois;

            public NbPoiDiffCallback(List<NbPoi> oldNbPois, List<NbPoi> newNbPois) {
                this.oldNbPois = oldNbPois;
                this.newNbPois = newNbPois;
            }

            @Override
            public int getOldListSize() {
                return oldNbPois.size();
            }

            @Override
            public int getNewListSize() {
                return newNbPois.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return oldNbPois.get(oldItemPosition).NbPoiId == newNbPois.get(newItemPosition).NbPoiId;
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return oldNbPois.get(oldItemPosition).equals(newNbPois.get(newItemPosition));
            }
        }

    }

    ShowMapDialog showMapDialog = null;
    public AlertDialog dialogMap = null;
    DialogMapBuilder dialogMapObj = null;
    private void showOnMap_Click(NbPoi poi) {
        int step = 0;
        try {
            boolean dialogIsNull = false;
            if (showMapDialog == null) {
                showMapDialog = ShowMapDialog.getInstance();
                dialogIsNull = true;
            }
            showMapDialog.setForModeShowTrack(poi, view -> {
                //dialogMap.dismiss();
            });
            if (true || !showMapDialog.isAdded())
                context.showFragment(showMapDialog);
            else{
//                context.changeFragmentVisibility(showMapDialog, true);
//                showMapDialog.fillForm();
            }
            return;

//            dialogMap = null;
//            if (dialogMap == null || MainActivity.appExistsBeforeAndShouldReloadAll_ReReadDialogMap) {
//                step = 100;
//                AlertDialog.Builder alertDialogBuilder = dialogMapObj.GetBuilder();
//                step = 200;
//                dialogMap = alertDialogBuilder.create();
//                step = 300;
//                Window w = dialogMap.getWindow();
//                step = 400;
//                w.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//
//                MainActivity.appExistsBeforeAndShouldReloadAll_ReReadDialogMap = false;
//            }
//            step = 500;
//            dialogMapObj.setForModeShowSelect(lastLocation, 10, view -> btnSelectLocationOnMap_Click());
//            step = 600;
//            dialogMap.show();
//            step = 700;
//            return true;
        }
        catch (Exception ex){
            projectStatics.showDialog(context, getResources().getString(R.string.dialog_UnknownError)
                    , getResources().getString(R.string.dialog_UnknownErrorDesc)
                    , getResources().getString(R.string.ok), null, "", null);

            TTExceptionLogSQLite.insert("Step: " + step + "-ex:" +ex.getMessage(),  stktrc2k(ex), PrjConfig.frmMapSelect, 120);
            Log.d("جستجو_روی_نقشه", "Step: " + step + "-ex:" + ex.getMessage() + ex.getStackTrace());
            ex.printStackTrace();
            return ;
        }
   //1402-04 برای مشکل نقشه کامنت شد
//        try {
//            if (dialogMap == null || MainActivity.appExistsBeforeAndShouldReloadAll_ReReadDialogMap) {
//                AlertDialog.Builder alertDialogBuilder = dialogMapObj.GetBuilder();
//                dialogMap = alertDialogBuilder.create();
//                Window w = dialogMap.getWindow();
//                w.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//                MainActivity.appExistsBeforeAndShouldReloadAll_ReReadDialogMap = false;
//            }
//            dialogMapObj.setForModeShowTrack(poi, view -> {
//                dialogMap.dismiss();
//            });
//
//            dialogMap.show();
//        }
//        catch (Exception ex){
//            Log.e(Tag, "Exception: " + ex.getMessage());
//            ex.printStackTrace();
//            TTExceptionLogSQLite.insert(ex.getMessage(), stktrc2k(ex), PrjConfig.frmMyTracks, 109);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {//3nd Event
        return inflater.inflate(R.layout.mytracks, parent, false);
    }
}