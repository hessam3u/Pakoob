package mojafarin.pakoob;

import static utils.HFragment.stktrc2k;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.gms.maps.model.LatLng;
import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import utils.MainActivityManager;
import utils.PrjConfig;
import bo.entity.NbPoi;
import bo.entity.NbPoiCompact;
import bo.sqlite.NbPoiSQLite;
import bo.sqlite.TTExceptionLogSQLite;
import maptools.GPXFile;
import maptools.TrackData;
import maptools.hMapTools;
import utils.CustomTypeFaceSpan;
import utils.MyDate;
import utils.hutilities;
import utils.projectStatics;

public class EditTrack extends Fragment {
    long NbPoiId = 0;
    NbPoi currentObj = null;
    int CurrentColor = 0;
    String Sender = "";
    TrackData data = null;
    int dataSize = 0;
    int PositionInParent = -1;
    MyTracks.NbPoisAdapter adapter = null;

    public EditTrack(long _NbPoiId, String _Sender, int positionInParent, MyTracks.NbPoisAdapter _adapter) {
        //Intent intent = getIntent();
        NbPoiId = _NbPoiId;//intent.getLongExtra("NbPoiId", 0);
        Sender = _Sender;//intent.getStringExtra("Sender");
        currentObj = NbPoiSQLite.select(NbPoiId);
        data = TrackData.readTrackData(currentObj.Address, null);
        dataSize = data.Points.size();
        adapter = _adapter;
        PositionInParent = positionInParent;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {//4th Event
        super.onViewCreated(view, savedInstanceState);

        initializeComponents(view);
        fillForm();
        doCalculations();
    }

    private void fillForm() {
        txtPageTitle.setText(currentObj.Name);
        txtTitle.setText(currentObj.Name);
//        ColorDrawable cd = new ColorDrawable(currentObj.Color);
//        btnSelectColor.setBackground(cd);
        SampleLine mydrawing = new SampleLine(currentObj.Color);
        btnSelectColor.setImageDrawable(mydrawing);
        CurrentColor = currentObj.Color;
    }

    void doCalculations() {
        try {
            //1:
            LatLng cPoint = null;
            LatLng pPoint = null;
            long cTime = 0;
            long pTime = 0;
            float cElev = -1000;
            float pElev = -1000;
            double totalAscent = 0;
            double totalDecent = 0;
            long totalTime = 0;
            long movingMs = 0;
            long stoppedMs = 0;
            double cSpeed = 0;
            double maxSpeed = 0;
            double minSpeed = 0;
            long ascentMs = 0;
            long decentMs = 0;
            double distance = 0;
            double ascentDistance = 0;
            double descentDistance = 0;
            float maxElevation = -1000000;
            float minElevation = 1000000;

            boolean hasTime = data.Time.size() > 0;
            boolean hasElev = false;

            boolean TrackIsPaused = false;
            for (int i = 0; i < dataSize; i++) {
                if (i > 0 && !TrackIsPaused) {//1400-11-04  && !TrackIsPaused
                    pElev = cElev;
                    pPoint = cPoint;
                    pTime = cTime;
                }

                cElev = data.Elev.get(i);
                cPoint = data.Points.get(i);
                cTime = hasTime && data.Time.get(i) != null ? data.Time.get(i).getTimeInMillis() : 0;

                //1400-11-04 find Pause Track
                if (cElev == 0 && cPoint.longitude == 0 && cPoint.latitude == 0 && cTime == 0) {
                    Log.e("پراپرتیس", "رکورد خالی و توقف");
                    TrackIsPaused = true;
                    continue;
                }
                //1400-11-04
                if (TrackIsPaused) {
                    Log.e("پراپرتیس", "Track Pause Bood");
                    TrackIsPaused = false;
                    continue;
                }

                if (cTime == 0)
                    hasTime = false;
                if (cElev != 0)
                    hasElev = true;
                if (i == 0)
                    continue;
                if (cElev > maxElevation)
                    maxElevation = cElev;
                if (cElev < minElevation)
                    minElevation = cElev;

                Double diffDistance = hMapTools.distanceBetweenMeteres(pPoint.latitude, pPoint.longitude, cPoint.latitude, cPoint.longitude);

                if (diffDistance.isNaN())
                    continue;
                float diffElev = cElev - pElev;
                long diffTime = cTime - pTime;
                if (diffTime < 1000)//1401-10
                    diffTime = 0;

                totalTime += diffTime;

                distance += diffDistance;
                if (diffElev > 0) {
                    totalAscent += diffElev;
                    ascentDistance += diffDistance;
                    ascentMs += diffTime;
                } else if (diffElev < 0) {
                    diffElev *= -1;
                    totalDecent += diffElev;
                    descentDistance += diffDistance;
                    decentMs += diffTime;
                } else {
                    //???
                }
                if (diffTime > 0) {
                    cSpeed = diffDistance / (diffTime / 1000d);
                    if (cSpeed > maxSpeed && cSpeed < 1300)
                        maxSpeed = cSpeed;
                }


            }
            txtNumberOfPoints.setText(String.valueOf(dataSize));
            txtDistance.setText(hMapTools.distanceBetweenFriendly(distance));
            if (hasElev) {
                txtAscentTotal.setText(hMapTools.distanceBetweenFriendly(totalAscent));
                txtDescentTotal.setText(hMapTools.distanceBetweenFriendly(totalDecent));
                txtAscentTotal1.setText(hMapTools.distanceBetweenFriendly(totalAscent));
                txtDescentTotal1.setText(hMapTools.distanceBetweenFriendly(totalDecent));
                txtDistanceAscent.setText(hMapTools.distanceBetweenFriendly(ascentDistance));
                txtDistanceDescent.setText(hMapTools.distanceBetweenFriendly(descentDistance));
                txtMaxElevation.setText(hMapTools.distanceBetweenFriendly(maxElevation));
                txtMinElevation.setText(hMapTools.distanceBetweenFriendly(minElevation));

                double ascentSlope = Math.atan(totalAscent / (ascentDistance * 1d)) * 180 / Math.PI;
                double descentSlope = Math.atan(totalDecent / (descentDistance * 1d)) * 180 / Math.PI;
                txtSlopeAscent.setText(((int) (ascentSlope * 100) / 100d) + "°");
                txtSlopeDescent.setText(((int) (descentSlope * 100) / 100d) + "°");
            }
            if (hasTime) {
                txtDuration.setText(hMapTools.timeFriendly(totalTime / 1000, hMapTools.TIME_FRIENDLY_MODE_LONG_EXACT));
                txtDurationAscent.setText(hMapTools.timeFriendly(ascentMs / 1000, hMapTools.TIME_FRIENDLY_MODE_LONG_EXACT));
                txtDurationDescent.setText(hMapTools.timeFriendly(decentMs / 1000, hMapTools.TIME_FRIENDLY_MODE_LONG_EXACT));

                txtAvgSpeed.setText(hMapTools.GetSpeedFriendlyKmPh(distance / (totalTime / 1000)) + "kmh");
                txtMaxSpeed.setText(hMapTools.GetSpeedFriendlyKmPh(maxSpeed) + "kmh");

//TimeZone timeZone = TimeZone.getTimeZone("IST");
                TimeZone timeZone = TimeZone.getDefault();
                Calendar start = data.Time.get(0);
                Calendar end = data.Time.get(dataSize - 1);
                //1400-11-16 some times, some time parts are missing
                if (end != null && start != null) {
                    start.setTimeZone(timeZone);
                    end.setTimeZone(timeZone);
                    txtStartTime.setText(MyDate.CalendarToTimeString(start, MyDate.TimeToStringFormat.HourMinSec, ":") + " " + MyDate.CalendarToPersianDateString(start, MyDate.DateToStringFormat.yyyymmdd, "/"));
                    txtEndTime.setText(MyDate.CalendarToTimeString(end, MyDate.TimeToStringFormat.HourMinSec, ":") + " " + MyDate.CalendarToPersianDateString(end, MyDate.DateToStringFormat.yyyymmdd, "/"));
                }
            }
            if (!hasElev)
                divSlopeCalculations.setVisibility(View.GONE);
        } catch (Exception ex) {
            TTExceptionLogSQLite.insert(ex.getMessage(), stktrc2k(ex), PrjConfig.frmEditTrack, 102);
            ex.printStackTrace();
        }
    }

    TextView btnMore;
    Toolbar toolbar;
    TextView btnExport, btnSave;
    TextView btnBack, btnTrackInfo, btnEditOnMap, btnViewPoints, btnDelete, txtPageTitle;
    EditText txtTitle;
    ImageView btnSelectColor;

    private void initializeComponents(View v) {

        btnBack = v.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(view -> {
            ((AppCompatActivity) context).onBackPressed();
        });

        //toolbar = (Toolbar) v.findViewById(R.id.toolbarOfPage);
//        setSupportActionBar(toolbar);
////        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle("");

        txtPageTitle = v.findViewById(R.id.txtPageTitle);
        btnExport = v.findViewById(R.id.btnExportGPX);
        btnSave = v.findViewById(R.id.btnSave);

        btnSelectColor = v.findViewById(R.id.btnSelectColor);

        txtTitle = v.findViewById(R.id.txtTitle);
        hutilities.hideKeyboard(context, txtTitle);
        txtTitle.clearFocus();
        btnSelectColor.requestFocus();
//        txtTitle.clearFocus();

        btnTrackInfo = v.findViewById(R.id.btnTrackInfo);
        btnTrackInfo.setVisibility(View.GONE);
        btnEditOnMap = v.findViewById(R.id.btnEditOnMap);
        if (dataSize > 500){
            btnEditOnMap.setVisibility(View.GONE);
        }
        btnViewPoints = v.findViewById(R.id.btnViewPoints);
        btnDelete = v.findViewById(R.id.btnDelete);
        btnDelete.setVisibility(View.GONE);

        btnMore = v.findViewById(R.id.btnMoreSelected);

        btnSelectColor.setOnClickListener(view -> {
            btnSelectColor_Click();
        });
        btnSave.setOnClickListener(view -> {
            btnSave_Click();
        });
        btnViewPoints.setOnClickListener(view -> {
            btnViewPoints_Click();
        });
        btnEditOnMap.setOnClickListener(view -> {
            btnEditOnMap_Click();
        });
        btnTrackInfo.setOnClickListener(view -> {
            btnTrackInfo_Click();
        });
        btnMore.setOnClickListener(view -> {
            btnMore_Click();
        });
        initializeComponents_ForCalculations(v);
    }

    LinearLayout divSlopeCalculations;
    TextView txtNumberOfPoints, txtDistance, txtDuration, txtDurationMoving, txtDurationStopped, txtAvgSpeed, txtMaxSpeed, txtAvgSpeedMoving, txtDescentTotal, txtAscentTotal, txtMaxElevation, txtMinElevation, txtStartTime, txtEndTime;
    TextView txtAscentTotal1, txtDescentTotal1, txtDurationAscent, txtDistanceAscent, txtAvgSpeedAscent, txtSlopeAscent, txtDistanceDescent, txtDurationDescent, txtAvgSpeedDescent, txtSlopeDescent;

    private void initializeComponents_ForCalculations(View v) {
        divSlopeCalculations = v.findViewById(R.id.divSlopeCalculations);

        txtAscentTotal1 = v.findViewById(R.id.txtAscentTotal1);
        txtDescentTotal1 = v.findViewById(R.id.txtDescentTotal1);
        txtDurationAscent = v.findViewById(R.id.txtDurationAscent);
        txtDistanceAscent = v.findViewById(R.id.txtDistanceAscent);
        txtAvgSpeedAscent = v.findViewById(R.id.txtAvgSpeedAscent);
        txtSlopeAscent = v.findViewById(R.id.txtSlopeAscent);
        txtDistanceDescent = v.findViewById(R.id.txtDistanceDescent);
        txtDurationDescent = v.findViewById(R.id.txtDurationDescent);
        txtAvgSpeedDescent = v.findViewById(R.id.txtAvgSpeedDescent);
        txtSlopeDescent = v.findViewById(R.id.txtSlopeDescent);

        txtNumberOfPoints = v.findViewById(R.id.txtNumberOfPoints);
        txtDistance = v.findViewById(R.id.txtDistance);
        txtDuration = v.findViewById(R.id.txtDuration);
        txtDurationMoving = v.findViewById(R.id.txtDurationMoving);
        txtDurationStopped = v.findViewById(R.id.txtDurationStopped);
        txtAvgSpeed = v.findViewById(R.id.txtAvgSpeed);
        txtMaxSpeed = v.findViewById(R.id.txtMaxSpeed);
        txtAvgSpeedMoving = v.findViewById(R.id.txtAvgSpeedMoving);
        txtDescentTotal = v.findViewById(R.id.txtDescentTotal);
        txtAscentTotal = v.findViewById(R.id.txtAscentTotal);
        txtMaxElevation = v.findViewById(R.id.txtMaxElevation);
        txtMinElevation = v.findViewById(R.id.txtMinElevation);
        txtStartTime = v.findViewById(R.id.txtStartTime);
        txtEndTime = v.findViewById(R.id.txtEndTime);
    }


    private void btnTrackInfo_Click() {
        ((MainActivityManager) context).showFragment(new TrackInfo(currentObj.NbPoiId, "MyTracks"));
//        Intent it = new Intent(context, TrackInfo.class);
//        it.putExtra("NbPoiId", currentObj.NbPoiId);
//        it.putExtra("Sender", "MyTracks");
//        startActivity(it);
    }

    private void btnEditOnMap_Click() {
        if (dataSize > 500) {
            projectStatics.showDialog(context, getResources().getString(R.string.Warning), getResources().getString(R.string.TooManyPointsToEditOnMap), getResources().getString(R.string.btnAccept), null, "", null);
            return;
        } else {
            ((MainActivity) context).showNbPoiOnMapForEdit(currentObj.NbPoiId, currentObj.Name);
            ((MainActivity) context).backToMapPage();
            //not work after fragmenting
//            Intent intent = new Intent();
//            intent.putExtra("NbPoiId", currentObj.NbPoiId);
//            setResult(MainActivity.ResultCode_ForMyTracks, intent);
//            finish();//finishing activity
        }
    }

    private void btnViewPoints_Click() {
        ((MainActivity) context).showFragment(new ViewTrackPoints(currentObj.NbPoiId, "EditTrack"));
//        Intent it = new Intent(context, ViewTrackPoints.class);
//        it.putExtra("NbPoiId", currentObj.NbPoiId);
//        it.putExtra("Sender", "MyTracks");
//        startActivity(it);
    }

    private void btnSave_Click() {
        int debugStep = 0;
        try {
            String name = txtTitle.getText().toString().trim();
            if (name == null || name.length() == 0) {
                txtTitle.requestFocus();
                projectStatics.showDialog(context
                        , getResources().getString(R.string.vali_GeneralError_Title)
                        , getResources().getString(R.string.vali_PleaseEnterName)
                        , getResources().getString(R.string.ok)
                        , null
                        , ""
                        , null);
                return;
            }
            debugStep = 10;
            NbPoi poi = NbPoiSQLite.select(this.NbPoiId);
            poi.Name = name;
            debugStep = 20;
//        ColorDrawable cd = (ColorDrawable)btnSelectColor.getBackground();
//        cd.getColor();
            poi.Color = CurrentColor;
            NbPoiSQLite.update(poi);
            debugStep = 30;
            //find Item and Apply Changes
            NbPoiCompact compactItem = app.findInVisiblePois(this.NbPoiId);
            debugStep = 40;
            if (compactItem.polyLine != null) {
                compactItem.polyLine.setColor(poi.Color);
                compactItem.polyLine.setTag(poi.Name);
            }
            debugStep = 50;
            if (adapter != null) {
                //Updaet adaptor
                adapter.data.set(PositionInParent, poi);
                debugStep = 60;
                adapter.notifyItemChanged(PositionInParent);
                debugStep = 70;
            }
            ((MainActivity) context).onBackPressed();
            debugStep = 80;
        } catch (Exception ex) {
            TTExceptionLogSQLite.insert(ex.getMessage(), "DEBUG STEP:" + debugStep, PrjConfig.frmEditTrack, 101);
            Log.e("اکسپشن", "Next line - " + ex.getMessage() + " : ");
            ex.printStackTrace();
        }
    }

    private void btnSelectColor_Click() {
//        ColorDrawable cd = (ColorDrawable) btnSelectColor.getBackground();
//        int colorCode = cd.getColor();
        int colorCode = CurrentColor;
        ColorPickerDialogBuilder pickerDialogBuilder =
                ColorPickerDialogBuilder
                        .with(context)
                        .setTitle("انتخاب رنگ")
                        .initialColor(colorCode)
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                                //toast("onColorSelected: 0x" + Integer.toHexString(selectedColor));
                            }
                        })
                        .setPositiveButton("تایید", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
//                        ColorDrawable cd = new ColorDrawable();
//                        cd.setColor(selectedColor);
//                        btnSelectColor.setBackground(cd);
                                CurrentColor = selectedColor;
                                SampleLine mydrawing = new SampleLine(selectedColor);
                                btnSelectColor.setImageDrawable(mydrawing);

                            }
                        })
                        .setNegativeButton("انصراف", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
        AlertDialog dialog = pickerDialogBuilder.build();
        dialog.show();
        Button pos = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        pos.setTextColor(getResources().getColor(R.color.colorBlack));
        pos.setTypeface(projectStatics.getIranSans_FONT(context));
        Button neg = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        neg.setTextColor(getResources().getColor(R.color.colorBlack));
        neg.setTypeface(projectStatics.getIranSans_FONT(context));
    }

    public class SampleLine extends Drawable {
        private final Paint redPaint;
        public int ColorCode;

        public SampleLine(int _ColorCode) {
            // Set up color and text size
            redPaint = new Paint();
            redPaint.setStyle(Paint.Style.STROKE);
            redPaint.setAntiAlias(true);
            redPaint.setColor(_ColorCode);
            redPaint.setStrokeWidth(10);
//            redPaint.setColor(ColorCode);
//            redPaint.setStrokeWidth(10);
            this.ColorCode = _ColorCode;
        }

        @Override
        public void draw(Canvas canvas) {
            // Get the drawable's bounds
            int width = getBounds().width();
            int height = getBounds().height();
            float radius = Math.min(width, height) / 2;

//            // Draw a red circle in the center
            Paint paint = redPaint;

            Path sine = new Path();
            float wSlice = width / 10;
            float hSlice = height / 5;
            float yLine = height / 2;
            sine.moveTo(0, yLine);
            sine.quadTo(1 * wSlice, height + hSlice, 2 * wSlice, yLine);
            sine.moveTo(2 * wSlice, yLine);
            sine.quadTo(3 * wSlice, 0 - hSlice, 4 * wSlice, yLine);
            sine.moveTo(4 * wSlice, yLine);
            sine.quadTo(5.5f * wSlice, height + hSlice, 7 * wSlice, yLine);
            sine.moveTo(7 * wSlice, yLine);
            sine.quadTo(8.5f * wSlice, 0 - hSlice, 10 * wSlice, yLine);
            canvas.drawPath(sine, paint);
        }

        @Override
        public void setAlpha(int alpha) {
            // This method is required
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            // This method is required
        }

        @Override
        public int getOpacity() {
            // Must be PixelFormat.UNKNOWN, TRANSLUCENT, TRANSPARENT, or OPAQUE
            return PixelFormat.OPAQUE;
        }
    }

    private void btnMore_Click() {
        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(context, btnMore);
        //Inflating the Popup using xml file
        popup.getMenuInflater()
                .inflate(R.menu.edittrack_rightclick, popup.getMenu());

        Menu menu = popup.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            //Trying to set icon for menu items:
//            if (item.getItemId() == R.id.btnEditOnMap){
//                Drawable d =  new BitmapDrawable(getResources(), hutilities.textAsBitmap("\uF278", 40, Color.BLACK, getApplicationContext()));
//                item.setIcon(R.drawable.ic_arrow01);
//            }
            CustomTypeFaceSpan.applyFontToMenuItem(item, context, Color.BLACK);
        }
        hutilities.hideKeyboard(context, txtTitle);
        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.btnEditOnMap:
                        btnEditOnMap_Click();
                        break;
                    case R.id.btnViewPoints:
                        btnViewPoints_Click();
                        break;
                    case R.id.btnExportGPX:
                        btnExportGPX_Click();
                        break;
                    case R.id.btnDelete:
                        btnDelete_Click();
                        break;
                }
                return true;
            }
        });

        popup.show(); //showing popup menu
    }

    private void btnDelete_Click() {
        projectStatics.showDialog(context
                , getResources().getString(R.string.DeleteConfirm)
                , getResources().getString(R.string.AreYouSureToDeleteFiles)
                , getResources().getString(R.string.ok)
                , view1 -> {
                    GPXFile.DeleteNbPoiRec(currentObj);
                    if (adapter != null) {
                        adapter.data.remove(PositionInParent);
                        adapter.notifyItemRemoved(PositionInParent);
                    }
                    ((MainActivity) context).onBackPressed();
                }
                , getResources().getString(R.string.cancel), null);
    }


    private void btnExportGPX_Click() {
        File ff = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String path =ff.getPath();
        DoExportGPX(path);

//        new ChooserDialog(context)
//                .withFilter(true, true, "")
//                .withStartFile(path)
//                .withResources(R.string.fileChooser_SelectPathToSave, R.string.fileChooser_myTracks_btnSelect, R.string.fileChooser_myTracks_btnCancel)
//                .withChosenListener(new ChooserDialog.Result() {
//                    @Override
//                    public void onChoosePath(String path, File pathFile) {
//                        DoExportGPX(path);
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

    boolean DoExportGPX(String path){
        //NbPoi lastItem = NbPoiSQLite.selectLastInserted();
        Log.e("انتخاب پوشه", path + "----");
        List<NbPoi> toConvert = new ArrayList<>();
        toConvert.add(currentObj);
        String content = GPXFile.ExportGPXToString(toConvert);
        String fileNameToSave = currentObj.Name;
        File file = new File(path + File.separator + fileNameToSave + ".gpx");
        if (file.exists()) {
            Random random = new Random();
            fileNameToSave = fileNameToSave + "-" + random.nextInt(10000);
            file = new File(path + File.separator + fileNameToSave + ".gpx");
        }
        try {
            OutputStreamWriter writer =
                    new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            writer.write(content, 0, content.length());
            writer.close();
            String message = getResources().getString(R.string.SaveCompleted_Desc).replace("000",
                    getString(R.string.DownloadFolder)).replace("111", fileNameToSave + ".gpx");
            projectStatics.showDialog(context, getResources().getString(R.string.SaveCompleted)
                    , message
                    , getResources().getString(R.string.btnAccept)
                    , null
                    , ""
                    , null);
        } catch (Exception e) {
            String friendlyMsg = getResources().getString(R.string.ErrorInSave_Desc);
            boolean isUnknownEx = true;
            if (e.getMessage().contains("EPERM")){
                friendlyMsg = getResources().getString(R.string.ErrorInSaveMimeType_Desc);
                isUnknownEx = false;
            }
            else if (e.getMessage().contains("Permission denied")){
                friendlyMsg = getResources().getString(R.string.ErrorInSavePermissionDenied);
                MyTracks.checkREAD_WRITE_PermissionForBefore_ANDROID_R(context);
                isUnknownEx = false;
            }
            projectStatics.showDialog(context, getResources().getString(R.string.ErrorInSave)
                    ,friendlyMsg
                    , getResources().getString(R.string.btnAccept)
                    , null
                    , ""
                    , null);
            if (isUnknownEx) {
                TTExceptionLogSQLite.insert(e.getMessage(), "FILE PATH:" + path + "----" , PrjConfig.frmEditTrack, 300);
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }

    Context context;

    @Override
    public void onAttach(Context _context) { //1st Event
        super.onAttach(context);
        this.context = _context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {//3nd Event
        return inflater.inflate(R.layout.mytracks_edittrack, parent, false);
    }

    public void onCreate(Bundle savedInstanceState) {//2nd Event
        super.onCreate(savedInstanceState);
    }
}
