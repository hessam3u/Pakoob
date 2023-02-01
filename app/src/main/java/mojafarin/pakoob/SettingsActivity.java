package mojafarin.pakoob;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import bo.entity.NbMap;
import bo.sqlite.NbMapSQLite;
import maptools.hMapTools;
import utils.HFragment;
import utils.hutilities;
import utils.projectStatics;

public class SettingsActivity extends HFragment {
    String pageKey = "";

    TextView txtDeleteMaps, txtDeleteTiles, txtDeleteHighZoom;
    Button btnDeleteTiles, btnDeleteMaps, btnDeleteHighZoom
            , btnShowHelp;

    ProgressBar progressTiles, progressMaps, progressHighZoom;
    Spinner cmbPositionFormats, cmbNorthReference;

    SwitchCompat rbshowSpotlightAtMapNextTime, rbshowSpotlightAtHomeNextTime;

    public SettingsActivity(){

    }
    public SettingsActivity(String _pageKey){
        this.pageKey = _pageKey;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {//4th Event
        super.onViewCreated(view, savedInstanceState);

        initializeComponents(view);

        fillForm();
    }
    private void fillForm() {
        resetSizes();

        cmbNorthReference.setSelection(app.currentNorth - 1);
        cmbPositionFormats.setSelection(app.CurrentPositionFormat - 1);
    }

    TextView txtPageTitle, btnBack;
    @Override
    public void initializeComponents(View v) {

        btnBack =v.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(view -> {((AppCompatActivity) context).onBackPressed();});
        txtPageTitle = v.findViewById(R.id.txtPageTitle);
        txtPageTitle.setText(context.getResources().getString(R.string.title_settings));

        //setContentView(R.layout.frm_setting);
        //force RTL
        hutilities.forceRTLIfSupported((AppCompatActivity)context);
        txtDeleteMaps = v.findViewById(R.id.txtDeleteMaps);
        txtDeleteTiles = v.findViewById(R.id.txtDeleteTiles);
        txtDeleteHighZoom = v.findViewById(R.id.txtDeleteHighZoom);
        progressTiles = v.findViewById(R.id.progressTiles);
        rbshowSpotlightAtMapNextTime = v.findViewById(R.id.rbshowSpotlightAtMapNextTime);
        rbshowSpotlightAtMapNextTime.setChecked(!app.session.getMapHelpSeen());
        rbshowSpotlightAtMapNextTime.setOnCheckedChangeListener((compoundButton, b) -> {
            app.session.setMapHelpSeen(!b);
        });
        rbshowSpotlightAtHomeNextTime = v.findViewById(R.id.rbshowSpotlightAtHomeNextTime);
        rbshowSpotlightAtHomeNextTime.setChecked(!app.session.getHomeHelpSeen());
        rbshowSpotlightAtHomeNextTime.setOnCheckedChangeListener((compoundButton, b) -> {
            app.session.setHomeHelpSeen(!b);
        });

        cmbPositionFormats = v.findViewById(R.id.cmbPositionFormats);
        ArrayAdapter<CharSequence> positionAdaptor = ArrayAdapter.createFromResource(context,
                R.array.PositionFormats, R.layout.spinner_normal_item);
        positionAdaptor.setDropDownViewResource(R.layout.spinner_normal_item);
        cmbPositionFormats.setAdapter(positionAdaptor);
        cmbPositionFormats.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int index, long idValue) {
                app.session.setPositionFormat(index + 1);
                app.CurrentPositionFormat = index + 1;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        cmbNorthReference = v.findViewById(R.id.cmbNorthReference);
        ArrayAdapter<CharSequence> cmbNorthReferenceAdaptor = ArrayAdapter.createFromResource(context,
                R.array.NorthRefrences, R.layout.spinner_normal_item);
        positionAdaptor.setDropDownViewResource(R.layout.spinner_normal_item);
        cmbNorthReference.setAdapter(cmbNorthReferenceAdaptor);
        cmbNorthReference.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int index, long idValue) {
                app.session.setNorthReference(index + 1);
                app.currentNorth = index + 1;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });



        Drawable progressDrawable1 = progressTiles.getIndeterminateDrawable().mutate(); //or getProgressDrawable for normal //https://stackoverflow.com/questions/2020882/how-to-change-progress-bars-progress-color-in-android
        progressDrawable1.setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
        progressTiles.setProgressDrawable(progressDrawable1);

        progressMaps = v.findViewById(R.id.progressMaps);
        Drawable progressDrawable2 = progressMaps.getIndeterminateDrawable().mutate(); //or getProgressDrawable for normal //https://stackoverflow.com/questions/2020882/how-to-change-progress-bars-progress-color-in-android
        progressDrawable2.setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
        progressMaps.setProgressDrawable(progressDrawable2);

        progressHighZoom = v.findViewById(R.id.progressHighZoom);
        Drawable progressDrawable3 = progressHighZoom.getIndeterminateDrawable().mutate(); //or getProgressDrawable for normal //https://stackoverflow.com/questions/2020882/how-to-change-progress-bars-progress-color-in-android
        progressDrawable3.setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
        progressHighZoom.setProgressDrawable(progressDrawable3);
        btnShowHelp = v.findViewById(R.id.btnShowHelp);
        btnShowHelp.setOnClickListener(view -> {
            context.showFragment(new InfoPage("help"));
        });

        btnDeleteTiles = v.findViewById(R.id.btnDeleteTiles);
        btnDeleteTiles.setOnClickListener(vv -> {
            projectStatics.showDialog(context
                    , getResources().getString(R.string.DeleteConfirm)
                    , getResources().getString(R.string.AreYouSureToDeleteFiles)
                    , getResources().getString(R.string.ok)
                    , view1 -> {
                progressTiles.setVisibility(View.VISIBLE);
                        List<NbMap> list = NbMapSQLite.selectAll();
                        if (list != null){
                            for (int i = 0; i < list.size(); i++) {
                                NbMap item =list.get(i);
                                item.Extracted = 2;
                                NbMapSQLite.update(item);
                            }
                        }
                        List<NbMap> list2 = NbMapSQLite.selectAll();

                        hMapTools.deleteTilesFolder(context);

                        resetSizes();
                        progressTiles.setVisibility(View.INVISIBLE);

                    }
                    , getResources().getString(R.string.cancel), null);
        });
        btnDeleteMaps = v.findViewById(R.id.btnDeleteMaps);
        btnDeleteMaps.setOnClickListener(view -> {
            projectStatics.showDialog(context
                    , getResources().getString(R.string.DeleteConfirm)
                    , getResources().getString(R.string.AreYouSureToDeleteFiles)
                    , getResources().getString(R.string.ok)
                    , view1 -> {
                        progressMaps.setVisibility(View.VISIBLE);
                        List<NbMap> list = NbMapSQLite.selectAll();
                        if (list != null){
                            for (int i = 0; i < list.size(); i++) {
                                NbMap item =list.get(i);
                                item.LocalFileAddress = "";
                                NbMapSQLite.update(item);
                            }
                        }

                        hMapTools.deleteMapsFolder(context);

                        resetSizes();
                        progressMaps.setVisibility(View.INVISIBLE);

                    }
                    , getResources().getString(R.string.cancel), null);
        });
        btnDeleteHighZoom = v.findViewById(R.id.btnDeleteHighZoom);
        btnDeleteHighZoom.setOnClickListener(view -> {
            projectStatics.showDialog(context
                    , getResources().getString(R.string.DeleteConfirm)
                    , getResources().getString(R.string.AreYouSureToDeleteFiles)
                    , getResources().getString(R.string.ok)
                    , view1 -> {
                        progressHighZoom.setVisibility(View.VISIBLE);

                        hMapTools.deleteHighZoomFolder(context);

                        resetSizes();
                        progressHighZoom.setVisibility(View.INVISIBLE);

                    }
                    , getResources().getString(R.string.cancel), null);
        });

    }

    void resetSizes() {
        String tilesSize = hutilities.getStringSizeLengthFile(hMapTools.getTileFolderSize(context));
        String mapsSize = hutilities.getStringSizeLengthFile(hMapTools.getMapsFolderSize(context));
        String highzoomsSize = hutilities.getStringSizeLengthFile(hMapTools.getHighZoomSize(context));

        txtDeleteMaps.setText(getResources().getString(R.string.deleteMaps) + " (" + mapsSize + ")");
        txtDeleteTiles.setText(getResources().getString(R.string.deleteTiles) + " (" + tilesSize + ")");
        txtDeleteHighZoom.setText(getResources().getString(R.string.deleteHighZoom) + " (" + highzoomsSize + ")");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {//3nd Event
        return inflater.inflate(R.layout.frm_setting, parent, false);
    }
}
