package mojafarin.pakoob.mainactivitymodes;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Random;

import bo.entity.NbPoi;
import bo.entity.NbPoiCompact;
import maptools.DialogGetPosition;
import maptools.GPXFile;
import maptools.hMapTools;
import mojafarin.pakoob.MainActivity;
import mojafarin.pakoob.MapPage;
import mojafarin.pakoob.R;
import mojafarin.pakoob.app;
import utils.hutilities;
import utils.projectStatics;

import static mojafarin.pakoob.MainActivity.map;
import static mojafarin.pakoob.MapPage.MAP_CLICK_MODE_WAYPOINT;

public class AddWaypointMode {
    MainActivity context;
    MapPage mapPage;
    public AddWaypointMode(MainActivity mainActivity, MapPage mapPage){
        context = mainActivity;
        this.mapPage = mapPage;
        initializeComponentes();

    }

    private void initializeComponentes() {
        pnlAddWaypoint = context.findViewById(R.id.pnlAddWaypoint);

    }
    TextView txtLocationOfWaypoint, btnSaveWaypoint, btnDiscardWaypoint, btnGo, btnDeleteWaypoint;
    EditText txtNameOfWaypoint;
    public View pnlAddWaypoint;
    public Marker AddWaypointMarker = null;
    public NbPoi editingWaypoint = null;
    public boolean editingWaypointWasVisible = false;

    public void mapOnClick_Waypoint(LatLng latLng, boolean ChangeCameraToThere) {
        String name = "";
        if(txtNameOfWaypoint != null && txtNameOfWaypoint.getText().length() > 0){
            name = txtNameOfWaypoint.getText().toString();
        }
        else if (editingWaypoint == null ){
            Random rand = new Random();
            name = context.getResources().getString(R.string.Waypoint) + " " + rand.nextInt(10000);
        }
        else{
            name = editingWaypoint.Name;
        }
        if (AddWaypointMarker == null){
            AddWaypointMarker = map.addMarker(new MarkerOptions().position(latLng).title(name).icon(hutilities.bitmapDescriptorFromVector(context, R.drawable.ic_mosalasblue)) );
        }
        else{
            AddWaypointMarker.setPosition(latLng);
        }
        txtNameOfWaypoint.setText(name);
        txtLocationOfWaypoint.setText(hMapTools.LocationToString(latLng, app.CurrentPositionFormat, hMapTools.LocationToStringStyle.Inline));

        if (ChangeCameraToThere)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, map.getCameraPosition().zoom));
    }


    public void initAddWaypointPanel() {
        if (btnSaveWaypoint == null) {
            btnSaveWaypoint = pnlAddWaypoint.findViewById(R.id.btnSaveWaypoint);
            txtNameOfWaypoint = pnlAddWaypoint.findViewById(R.id.txtNameOfWaypoint);
            txtLocationOfWaypoint = pnlAddWaypoint.findViewById(R.id.txtLocationOfWaypoint);
            btnDiscardWaypoint = pnlAddWaypoint.findViewById(R.id.btnDiscardWaypoint);
            btnGo = pnlAddWaypoint.findViewById(R.id.btnGo);
            btnDeleteWaypoint = pnlAddWaypoint.findViewById(R.id.btnDeleteWaypoint);

            btnSaveWaypoint.setOnClickListener(view -> {
                saveWaypoint_Click();
                discardWaypoint_Click(true);
            });
            btnDiscardWaypoint.setOnClickListener(view -> {
                discardWaypoint_Click(false);
            });
            txtLocationOfWaypoint.setOnClickListener(view -> {
                txtLocationOfWaypoint_Click();
            });
            txtNameOfWaypoint.setOnClickListener(view -> {
                txtNameOfWaypoint_Click();
            });
            btnGo.setOnClickListener(view -> {
                saveWaypoint_Click();
                mapPage.btnGo_Click();
            });
            btnDeleteWaypoint.setOnClickListener(view -> {
                projectStatics.showDialog(context, context.getResources().getString(R.string.DeleteConfirm)
                        , context.getResources().getString(R.string.AreYouSureToDeleteWaypoint)
                        , context.getResources().getString(R.string.ok), view1 -> {
                    //CLICK OK:
                            GPXFile.DeleteNbPoiRec(editingWaypoint);
                            discardWaypoint_Click(true);
                        }, context.getResources().getString(R.string.cancel), null);
            });
        }
        if (editingWaypoint != null){
            btnDeleteWaypoint.setVisibility(View.VISIBLE);
        }
        else
            btnDeleteWaypoint.setVisibility(View.GONE);
    }

    private void txtNameOfWaypoint_Click() {

    }
    public AlertDialog dialog_position = null;
    private void txtLocationOfWaypoint_Click() {
        Context context = this.context;
        AlertDialog.Builder alertDialogBuilder = DialogGetPosition.GetBuilder(context, AddWaypointMarker.getPosition(), app.CurrentPositionFormat
                , view -> {
                    LatLng nPosition = DialogGetPosition.GetPosition(dialog_position);
                    if (nPosition != null){
                        AddWaypointMarker.setPosition(nPosition);
                        float zoom = map.getCameraPosition().zoom;
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(nPosition, zoom));
                        txtLocationOfWaypoint.setText(hMapTools.LocationToString(nPosition, app.CurrentPositionFormat, hMapTools.LocationToStringStyle.Inline));
                    }
                    dialog_position.dismiss();
                }, view -> {
                    dialog_position.dismiss();
                });

//        alertDialogBuilder
//                .setCancelable(false)
//                .setPositiveButton("تایید",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog,int id) {
//                                // get user input and set it to result
//                                // edit text
//                                //result.setText(userInput.getText());
//                            }
//                        })
//                .setNegativeButton("انصراف",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog,int id) {
//                                dialog.cancel();
//                            }
//                        });

        // create alert dialog
        dialog_position = alertDialogBuilder.create();
        // show it
        dialog_position.show();
        projectStatics.SetDialogButtonStylesAndBack(dialog_position, this.context, projectStatics.getIranSans_FONT(context), 18);

    }

    public void discardWaypoint_Click(boolean firedAfterSave) {
        if (mapPage.IsInAddWaypointMode == MAP_CLICK_MODE_WAYPOINT){
            if (!firedAfterSave && editingWaypoint != null && editingWaypointWasVisible){
                NbPoiCompact compact = app.findInVisiblePois(editingWaypoint.NbPoiId);
                if (compact != null)
                    compact.marker.setVisible(true);
            }

            mapPage.IsInAddWaypointMode = 0;
            pnlAddWaypoint.setVisibility(View.GONE);
            AddWaypointMarker.remove();
            editingWaypoint = null;
            AddWaypointMarker = null;
            editingWaypointWasVisible = false;
            txtNameOfWaypoint.setText("");
            hutilities.hideKeyboard(context, txtNameOfWaypoint);
        }
    }

    private void saveWaypoint_Click() {
        long editingWaypointId = 0;
        if (editingWaypoint != null ){
            editingWaypointId = editingWaypoint.NbPoiId;
        }
        String Name = txtNameOfWaypoint.getText().toString().trim();
        if (Name.length() == 0){
            projectStatics.showDialog(context
                    , context.getResources().getString(R.string.vali_GeneralError_Title)
                    , context.getResources().getString(R.string.vali_PleaseEnterName)
                    , context.getResources().getString(R.string.ok)
                    , null
                    , ""
                    , null);
            return;
        }
        NbPoi poiToSave= GPXFile.SaveDesignedWaypoint(editingWaypointId, Name,AddWaypointMarker.getPosition(), context);
        //working... but no need, because GPXFile done adding to MAP
//        if (editingWaypoint != null){
//            NbPoiCompact compact = app.findInVisiblePois(editingWaypointId);
//            if (compact != null) {
//                compact.marker.setPosition(AddWaypointMarker.getPosition());
//                if(editingWaypointWasVisible)
//                    compact.marker.setVisible(true);
//            }
//        }
    }
}
