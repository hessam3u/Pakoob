package mojafarin.pakoob;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.github.eloyzone.jalalicalendar.JalaliDate;
import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;
import java.util.TimeZone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import bo.entity.NbPoi;
import bo.entity.NbPoiCompact;
import bo.sqlite.NbPoiSQLite;
import maptools.TrackData;
import maptools.hMapTools;
import maptools.GeoCalcs;
import utils.MyDate;
import utils.projectStatics;

public class ViewTrackPoints extends Fragment {
    Toolbar toolbar;
    TextView btnBack;
    TableLayout dgdData;
    long NbPoiId;
    NbPoi currentObj = null;
    NbPoiCompact currentObjCompact;
    ProgressDialog mProgressBar;
    TrackData data;
    TextView txtPageTitle;

    public ViewTrackPoints(long nbPoiId, String Sender){
        NbPoiId = nbPoiId;
//        Intent intent = getIntent();
//        NbPoiId = intent.getLongExtra("NbPoiId", 0);
        currentObj = NbPoiSQLite.select(NbPoiId);
        currentObjCompact = NbPoiCompact.getInstance(currentObj);

    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {//4th Event
        super.onViewCreated(view, savedInstanceState);

        initializeComponents(view);

        startLoadData();
    }

    private void initializeComponents(View v) {

        btnBack =v.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(view -> {((AppCompatActivity) context).onBackPressed();});

        txtPageTitle = v.findViewById(R.id.txtPageTitle);
        txtPageTitle.setText(currentObj.Name);

        dgdData = v.findViewById(R.id.dgdData);
        dgdData.setStretchAllColumns(true);
        mProgressBar = new ProgressDialog(context);
    }

    Thread thread;

    public void startLoadData() {
//        mProgressBar.setCancelable(false);
//        mProgressBar.setMessage("در حال بارگذاری");
//        mProgressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        mProgressBar.show();
        data = TrackData.readTrackData(currentObjCompact.Address, null);
        int Padding_RIGHT = 0;
        int Padding_LEFT = 5;
        int Padding_TOP = 5;
        int Padding_BOTTOM = 5;
        Handler handler = new Handler(Looper.getMainLooper());
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeZone currentTimeZone = TimeZone.getDefault();
                    int rowCount = data.Points.size();

                    boolean hasTimeCol = false;
                    boolean hasElevCol = false;
                    float minElev = 150000;
                    float maxElev = -150000;

                    for (int i = 0; i < rowCount ; i++) {
                        if (data.Time.get(i) != null)
                            hasTimeCol = true;
                        float elev=data.Elev.get(i);
                        if (elev != 0)
                            hasElevCol = true;
                        if (elev > maxElev)
                            maxElev = elev;
                        if (elev < minElev)
                            minElev = elev;
                    }
                    int clBotMin = Color.GREEN;
                    int clBotMax = Color.argb(100, 255, 230, 224);
                    int clTopMax = Color.RED;
                    int clTopMin = Color.argb(100, 255, 230, 224);
                    double TopMinValue = (maxElev + minElev) / 2;

                    String title_Elevation = getResources().getString(R.string.Elevation);
                    String title_Position = getResources().getString(R.string.Position);
                    String title_Date = getResources().getString(R.string.Date);
                    String title_Speed = getResources().getString(R.string.SpeedAndTime);
                    String title_Course = getResources().getString(R.string.CourseAndDistance);
                    int leftRowMargin = 0;
                    int topRowMargin = 0;
                    int rightRowMargin = 0;
                    int bottomRowMargin = 0;
                    int textSizeMedium = 0, smallTextSize = 0, verySmallTextSize = 0;

                    textSizeMedium = (int) getResources().getDimension(R.dimen.font_size_medium);
                    smallTextSize = (int) getResources().getDimension(R.dimen.font_size_small);
                    verySmallTextSize = (int) getResources().getDimension(R.dimen.font_size_verysmall);

                    TextView textSpacer = null;
                    LatLng currentLatLon = null;
                    float currentDeclination = 0;
                    LatLng prevLatLon = null;
                    long currentTimeMilis = 0;
                    long prevTimeMilis = 0;
                    TextView tvPrevCourse = null, tvPrevSpeed = null;
                    int[] itemBackgrounds = new int[]{Color.parseColor("#f8f8f8"), Color.parseColor("#ffffff")};
                    int[] headerBackgrounds = new int[]{Color.parseColor("#f0f0f0"), Color.parseColor("#f7f7f7"),};

                    dgdData.removeAllViews();

                    // -1 means heading row
                    for (int i = -1; i < rowCount; i++) {
                        int colCounter = 0;
                        if (i > -1) {
                            if (i > 0) {
                                prevLatLon = currentLatLon;
                                if (hasTimeCol)
                                    prevTimeMilis = currentTimeMilis;
                            }
                            currentLatLon = data.Points.get(i);
                            if (hasTimeCol)
                                currentTimeMilis = data.Time.get(i).getTimeInMillis();
                        } else {
                            textSpacer = new TextView(context);
                            textSpacer.setText("");
                        }
                        if (i == 0 && app.currentNorth == hMapTools.NORTH_MAG) {
                            currentDeclination = GeoCalcs.GetNewDeclination((float) currentLatLon.latitude, (float) currentLatLon.longitude, hasElevCol ? data.Elev.get(i) : 0);
                        }

                        // add table row
                        final TableRow tr = new TableRow(context);
                        tr.setId(i + 1);
                        TableLayout.LayoutParams trParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                                TableLayout.LayoutParams.WRAP_CONTENT);
                        trParams.setMargins(leftRowMargin, topRowMargin, rightRowMargin, bottomRowMargin);
                        tr.setPadding(0, 0, 0, 0);
                        tr.setLayoutParams(trParams);

                        // data columns
                        final TextView tv = new TextView(context);
                        tv.setTypeface(projectStatics.getIranSans_FONT(context));
                        tv.setGravity(Gravity.CENTER);

                        if (i == -1) {
                            tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                                    TableRow.LayoutParams.MATCH_PARENT));
                            tv.setPadding(Padding_LEFT, Padding_TOP * 2, Padding_RIGHT, Padding_BOTTOM * 2);
                            tv.setText("#");
                            tv.setBackgroundColor(headerBackgrounds[colCounter % 2]);
                            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
                        } else {

                            tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                    TableRow.LayoutParams.MATCH_PARENT));
                            tv.setPadding(Padding_LEFT, Padding_TOP, Padding_RIGHT, Padding_BOTTOM);
                            tv.setBackgroundColor(itemBackgrounds[colCounter % 2]);
                            tv.setText(String.valueOf(i + 1));
                            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowCount > 1000?smallTextSize:textSizeMedium);
                        }
                        tr.addView(tv);
                        colCounter++;

                        if (hasElevCol) {
                            final TextView tv2 = new TextView(context);
                            tv2.setTypeface(projectStatics.getIranSans_FONT(context));
                            tv2.setGravity(Gravity.CENTER);
                            if (i == -1) {
                                tv2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                                        TableRow.LayoutParams.MATCH_PARENT));
                                tv2.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
                                tv2.setPadding(Padding_LEFT, Padding_TOP * 2, Padding_RIGHT, Padding_BOTTOM * 2);
                                tv2.setText(title_Elevation);
                                tv2.setBackgroundColor(headerBackgrounds[colCounter % 2]);
                            } else {
                                tv2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                        TableRow.LayoutParams.MATCH_PARENT));
                                tv2.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowCount > 1000?smallTextSize:textSizeMedium);
                                tv2.setPadding(Padding_LEFT, Padding_TOP, Padding_RIGHT, Padding_BOTTOM);
                                //tv2.setBackgroundColor(itemBackgrounds[colCounter % 2]);
                                float elev = data.Elev.get(i).floatValue();
                                tv2.setBackgroundColor(GetColorOfCell(elev, clTopMax, clTopMin, clBotMax, clBotMin, maxElev, TopMinValue, minElev));
                                tv2.setTextColor(Color.parseColor("#000000"));
                                tv2.setText(String.valueOf((int) elev));
                            }
                            tr.addView(tv2);
                            colCounter++;
                        }

                        if (hasTimeCol) {
                            final LinearLayout layDateAndTime = new LinearLayout(context);
                            layDateAndTime.setOrientation(LinearLayout.VERTICAL);
                            final TextView tv3 = new TextView(context);
                            tv3.setTypeface(projectStatics.getIranSans_FONT(context));
                            tv3.setGravity(Gravity.CENTER);
                            tv3.setTextColor(Color.parseColor("#000000"));
                            if (i == -1) {
                                layDateAndTime.setBackgroundColor(headerBackgrounds[colCounter % 2]);
                                layDateAndTime.setPadding(Padding_LEFT, Padding_TOP * 2, Padding_RIGHT, Padding_BOTTOM * 2);
                                layDateAndTime.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                                        TableRow.LayoutParams.MATCH_PARENT));

                                tv3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                                        TableRow.LayoutParams.WRAP_CONTENT));
                                tv3.setPadding(0, 0, 0, 0);
                                tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);

                                tv3.setText(title_Date);
                                layDateAndTime.addView(tv3);
                            } else {
                                layDateAndTime.setBackgroundColor(itemBackgrounds[colCounter % 2]);
                                layDateAndTime.setPadding(Padding_LEFT, Padding_TOP, Padding_RIGHT, Padding_BOTTOM);

                                tv3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                                        TableRow.LayoutParams.WRAP_CONTENT));
                                tv3.setPadding(0, 0, 0, 0);
                                tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeMedium);

                                String timeRow = "";
                                String dateRow = "";
                                tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
                                tv3.setText("");

                                Calendar inputDate = data.Time.get(i);
                                inputDate.setTimeZone(currentTimeZone);
                                if (inputDate != null) {
                                    JalaliDate date = MyDate.getJalaliDate(inputDate);
                                    int hour = inputDate.get(Calendar.HOUR_OF_DAY);
                                    int min = inputDate.get(Calendar.MINUTE);
                                    int sec = inputDate.get(Calendar.SECOND);
                                    timeRow = (hour < 10 ? "0" : "") + hour + ":" + (min < 10 ? "0" : "") + min + ":" + (sec < 10 ? "0" : "") + sec;
                                    dateRow = date.getYear() + "/" + (date.getMonthPersian().getValue() < 10 ? "0" : "") + date.getMonthPersian().getValue() + "/" + (date.getDay() < 10 ? "0" : "") + date.getDay();
                                    tv3.setText(timeRow);
                                } else {
                                    tv3.setText("");
                                }

                                layDateAndTime.addView(tv3);

                                final TextView tv3b = new TextView(context);
                                tv3b.setTypeface(projectStatics.getIranSans_FONT(context));
                                tv3b.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                                        TableRow.LayoutParams.WRAP_CONTENT));
                                tv3b.setGravity(Gravity.CENTER);
                                tv3b.setTextSize(TypedValue.COMPLEX_UNIT_PX, verySmallTextSize);
                                tv3b.setPadding(0, 0, 0, 0);
                                tv3b.setTextColor(Color.parseColor("#aaaaaa"));
                                tv3b.setText(dateRow);
                                layDateAndTime.addView(tv3b);
                            }
                            tr.addView(layDateAndTime);
                            colCounter++;
                        }
                        final TextView tv4 = new TextView(context);
                        tv4.setTypeface(projectStatics.getIranSans_FONT(context));
                        tv4.setGravity(Gravity.CENTER);

                        if (i == -1) {
                            tv4.setPadding(Padding_LEFT, Padding_TOP * 2, Padding_RIGHT, Padding_BOTTOM * 2);
                            tv4.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                                    TableRow.LayoutParams.MATCH_PARENT));
                            tv4.setBackgroundColor(headerBackgrounds[colCounter % 2]);
                            tv4.setText(title_Position);
                            tv4.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeMedium);
                        } else {
                            tv4.setPadding(Padding_LEFT, Padding_TOP, Padding_RIGHT, Padding_BOTTOM);
                            tv4.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                                    TableRow.LayoutParams.MATCH_PARENT));
//                            tv4.setPadding(5, 0, 1, 5);
                            tv4.setBackgroundColor(itemBackgrounds[colCounter % 2]);
                            tv4.setTextColor(Color.parseColor("#000000"));
                            tv4.setText(GeoCalcs.LocationToString(currentLatLon, app.CurrentPositionFormat, GeoCalcs.LocationToStringStyle.TwoLineSmall));
                            tv4.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
                        }
                        tr.addView(tv4);
                        colCounter++;

                        final TextView tv5 = new TextView(context);
                        tv5.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                TableRow.LayoutParams.MATCH_PARENT));
                        tv5.setTypeface(projectStatics.getIranSans_FONT(context));
                        tv5.setGravity(Gravity.CENTER);

                        double distanceInMeter = 0;
                        if (i == -1) {
                            tv5.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                    TableRow.LayoutParams.WRAP_CONTENT));
                            tv5.setPadding(Padding_LEFT, Padding_TOP * 2, Padding_RIGHT, Padding_BOTTOM * 2);
                            tv5.setText(title_Course);
                            tv5.setBackgroundColor(headerBackgrounds[colCounter % 2]);
                            tv5.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
                        } else {
                            tv5.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                    TableRow.LayoutParams.MATCH_PARENT));
                            tv5.setPadding(Padding_LEFT, Padding_TOP, Padding_RIGHT, Padding_BOTTOM);
                            tv5.setBackgroundColor(itemBackgrounds[colCounter % 2]);

                            String courseValue = "0°";
                            if (i > 0) {
                                distanceInMeter = GeoCalcs.distanceBetweenMeteres(prevLatLon.latitude, prevLatLon.longitude, currentLatLon.latitude, currentLatLon.longitude);
                                courseValue = ((int) (GeoCalcs.GetAzimuthInDegree(prevLatLon, currentLatLon) + currentDeclination)) + "°"
                                        + "\n" + GeoCalcs.distanceBetweenFriendly(distanceInMeter);
                            }
//                            if (i + 1 < rowCount){
//                                 +"°";
//                            }
                            if (tvPrevCourse != null)
                                tvPrevCourse.setText(courseValue);
                            tvPrevCourse = tv5;
                            tv5.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
                        }
                        colCounter++;
                        tr.addView(tv5);

                        if (hasTimeCol) {
                            final TextView tv6 = new TextView(context);
                            tv6.setTypeface(projectStatics.getIranSans_FONT(context));
                            tv6.setGravity(Gravity.CENTER);

                            if (i == -1) {
                                tv6.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                        TableRow.LayoutParams.WRAP_CONTENT));
                                tv6.setPadding(Padding_LEFT, Padding_TOP * 2, Padding_RIGHT, Padding_BOTTOM * 2);
                                tv6.setText(title_Speed);
                                tv6.setBackgroundColor(headerBackgrounds[colCounter % 2]);
                                tv6.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
                            } else {
                                tv6.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                        TableRow.LayoutParams.WRAP_CONTENT));
                                tv6.setPadding(Padding_LEFT, Padding_TOP, Padding_RIGHT, Padding_BOTTOM);
                                tv6.setBackgroundColor(itemBackgrounds[colCounter % 2]);

                                String speedValue = "0°";
                                if (i > 0) {
                                    speedValue = (prevTimeMilis != 0 && currentTimeMilis != 0 ? GeoCalcs.GetSpeedFriendlyKmPh(distanceInMeter / ((currentTimeMilis - prevTimeMilis) / 1000)) : "0") + "km/h"
                                            + (prevTimeMilis != 0 && currentTimeMilis != 0 ? "\n" + GeoCalcs.timeBetweenFriendly(prevTimeMilis, currentTimeMilis, GeoCalcs.TIME_FRIENDLY_MODE_SMALL) : "");
                                }
//                            if (i + 1 < rowCount){
//                                 +"°";
//                            }
                                if (tvPrevSpeed != null)
                                    tvPrevSpeed.setText(speedValue);
                                tvPrevSpeed = tv6;
                                tv6.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
                            }
                            colCounter++;
                            tr.addView(tv6);
                        }
//                        if (i > -1) {
//                            final TextView tv4b = new TextView(context);
//                                                    tv4b.setTypeface(hessamTools.getIranSans_FONT(context));
//                                                    tv4b.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
//                                    TableRow.LayoutParams.WRAP_CONTENT));
//
//                            tv4b.setGravity(Gravity.CENTER);
//                            tv4b.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
//                            tv4b.setPadding(2, 2, 1, 5);
//                            tv4b.setTextColor(Color.parseColor("#00afff"));
//                            tv4b.setBackgroundColor(Color.parseColor("#ffffff"));
//
//                            String due = "";
//                            if (row.amountDue.compareTo(new BigDecimal(0.01)) == 1) {
//                                due = "Due:" + decimalFormat.format(row.amountDue);
//                                due = due.trim();
//                            }
//                            tv4b.setText(due);
//                            layAmounts.addView(tv4b);
//                        }


                        if (i > -1) {

                            tr.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    TableRow tr = (TableRow) v;
                                    //do whatever action is needed

                                }
                            });


                        }
//                        dgdData.addView(tr, trParams);

                        handler.post(new Runnable() {
                            public void run() {
                                dgdData.addView(tr, trParams);
                            }
                        });


                        if (i > -1) {

                            // add separator row
                            final TableRow trSep = new TableRow(context);
                            TableLayout.LayoutParams trParamsSep = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                                    TableLayout.LayoutParams.WRAP_CONTENT);
                            trParamsSep.setMargins(leftRowMargin, topRowMargin, rightRowMargin, bottomRowMargin);

                            trSep.setLayoutParams(trParamsSep);
                            TextView tvSep = new TextView(context);
                            TableRow.LayoutParams tvSepLay = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                                    TableRow.LayoutParams.WRAP_CONTENT);
                            tvSepLay.span = 4;
                            tvSep.setLayoutParams(tvSepLay);
                            tvSep.setBackgroundColor(Color.parseColor("#d9d9d9"));
                            tvSep.setHeight(1);

                            trSep.addView(tvSep);
                            //                            dgdData.addView(trSep, trParamsSep);

                            handler.post(new Runnable() {
                                public void run() {
                                    dgdData.addView(trSep, trParamsSep);
                                }
                            });
                        }
                    }

//                    handler.post(new Runnable() {
//                        public void run() {
////                            Toast.makeText(ViewTrackPoints.this, "در حال استخراج نقشه...", Toast.LENGTH_LONG).show();
//                            //holder.progressBarIndet.setVisibility(View.VISIBLE);
//                            mProgressBar.hide();
//                        }
//                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        thread.start();
    }
    int GetColorOfCell(double value, int clTopMax, int clTopMin, int clBotMax, int clBotMin, double TopMaxValue, double TopMinValue, double BotValue)
    {
        if (value >= TopMinValue)
        {
            Color.red(33993);
            int rMax = Color.red(clTopMax);
            int rMin = Color.red(clTopMin);
            int gMax = Color.green(clTopMax);
            int gMin = Color.green(clTopMin);
            int bMax = Color.blue(clTopMax);
            int bMin = Color.blue(clTopMin);

            int rValue = rMin + (int)((rMax - rMin) * (value - TopMinValue) / (TopMaxValue - TopMinValue));
            int gValue = gMin + (int)((gMax - gMin) * (value - TopMinValue) / (TopMaxValue - TopMinValue));
            int bValue = bMin + (int)((bMax - bMin) * (value - TopMinValue) / (TopMaxValue - TopMinValue));


            return Color.argb(100, rValue, gValue, bValue);
        }
        else
        {
            int rMax = Color.red(clBotMax);
            int rMin = Color.red(clBotMin);
            int gMax = Color.green(clBotMax);
            int gMin = Color.green(clBotMin);
            int bMax = Color.blue(clBotMax);
            int bMin = Color.blue(clBotMin);

            int rValue = rMin + (int)(Math.abs(rMax - rMin) * (value - BotValue) / (TopMinValue - BotValue));
            int gValue = gMin + (int)(Math.abs(gMax - gMin) * (value - BotValue) / (TopMinValue - BotValue));
            int bValue = bMin + (int)(Math.abs(bMax - bMin) * (value - BotValue) / (TopMinValue - BotValue));

            return Color.argb(100, rValue, gValue, bValue);

        }
    }

    Context context;
    @Override
    public void onAttach(Context _context) { //1st Event
        super.onAttach(context);
        this.context = _context;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {//2nd Event
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {//3nd Event
        return inflater.inflate(R.layout.mytracks_viewtrackpoints, parent, false);
    }
}
