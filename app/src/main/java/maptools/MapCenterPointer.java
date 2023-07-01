package maptools;

import android.appwidget.AppWidgetHost;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

import mojafarin.pakoob.MainActivity;
import mojafarin.pakoob.R;

public class MapCenterPointer extends View {
    private int XMid;
    private int YMid;
    private int Width;
    private int Height;
    private int radius;
    private int radiusHalf;
    private int lineLength;
    private int lineLengthHalf;
    final Paint paint;
    final Paint paintLockOnMe;
    final Paint paintForText;
    public MapCenterPointer(Context context) {
        this(context, null);
    }

    public MapCenterPointer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public MapCenterPointer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        paint=new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.parseColor("#000000"));
        paint.setStrokeWidth(3);

        paintLockOnMe=new Paint();
        paintLockOnMe.setStyle(Paint.Style.STROKE);
        paintLockOnMe.setColor(context.getResources().getColor(R.color.homeZoomButtonsMyLocation));
        paintLockOnMe.setStrokeWidth(3);


        paintForText=new Paint();
        paintForText.setStyle(Paint.Style.FILL);
        //paintForText.setColor(Color.parseColor("#000000"));
        paintForText.setColor(context.getResources().getColor(R.color.greenJeeegh));
        paintForText.setStrokeWidth(1);
        paintForText.setTextSize(30);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int width = measureDimension(desiredWidth(), widthMeasureSpec);
//        int height = measureDimension(desiredHeight(), heightMeasureSpec);
        Width = MeasureSpec.getSize(widthMeasureSpec)/2;
        Height = MeasureSpec.getSize(heightMeasureSpec)/2;
        setMeasuredDimension(Width, Height);

        XMid = Width/2;
        YMid = Height/2;
        radius = XMid / 3*2;
        lineLength = Width  / 2; //1399-12-03 Actual LineLength must be 150dp
        lineLengthHalf = lineLength / 2;
        radiusHalf = radius / 8;
    }
    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        int radius=40;

        if (!MainActivity.isLockOnMe) {
            canvas.drawCircle(XMid, YMid, radius, paint);
            canvas.drawLine(XMid, YMid - lineLengthHalf, XMid, YMid + lineLengthHalf, paint);
            canvas.drawLine(XMid - lineLengthHalf , YMid, XMid + lineLengthHalf, YMid, paint);

            if (!MainActivity.DistanceValueFormCurrent.isEmpty())
                canvas.drawText(MainActivity.DistanceValueFormCurrent, XMid + radius + radiusHalf, YMid - radiusHalf, paintForText);
            if (!MainActivity.BearingValueFormCurrent.isEmpty())
                canvas.drawText(MainActivity.BearingValueFormCurrent + "°" , XMid + radius + radiusHalf, YMid +radius  , paintForText);
        }
        else{
            canvas.drawCircle(XMid, YMid, radius, paintLockOnMe);
            canvas.drawLine(XMid, YMid - lineLengthHalf, XMid, YMid + lineLengthHalf, paintLockOnMe);
            canvas.drawLine(XMid - lineLengthHalf, YMid, XMid + lineLengthHalf, YMid, paintLockOnMe);
        }
    }
}
