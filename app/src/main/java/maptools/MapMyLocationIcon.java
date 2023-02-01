package maptools;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import mojafarin.pakoob.MainActivity;
import mojafarin.pakoob.R;

public class MapMyLocationIcon extends View {
    public int XMid;
    public int YMid;
    private int Width;
    private int Height;
    private int radius;
    private int lineLength, lineLengthHalf;
    final Paint paint;
    final Paint paintPointer;
    public MapMyLocationIcon(Context context) {
        this(context, null);
    }

    public MapMyLocationIcon(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public MapMyLocationIcon(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        paint=new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.parseColor("#bbcccccc"));
        paint.setStrokeWidth(20);
        paint.setAntiAlias(true);

        paintPointer=new Paint();
        paintPointer.setColor(context.getResources().getColor(R.color.greenJeeegh));//homeMyLocationIcon_Correct
        paintPointer.setAntiAlias(true);
        paintPointer.setStyle(Paint.Style.STROKE);
        paintPointer.setStrokeJoin(Paint.Join.ROUND);
        paintPointer.setStrokeCap(Paint.Cap.ROUND);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Width = MeasureSpec.getSize(widthMeasureSpec);
        Height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(Width, Height);
//        Log.e("SSSSS", "widthMeasureSpec:"+widthMeasureSpec
//        + "heightMeasureSpec:"+heightMeasureSpec
//        + "Width:"+Width
//        + "Height:"+Height
//        );

        //1401-05-18 commented and changed to Width/6
        //paintPointer.setStrokeWidth(15.0f); Width in 2017 phone is 90, in emualator is 83, in tablet is 40
        paintPointer.setStrokeWidth((float)Width / 7f);

        XMid = Width/2;
        YMid = Height/2;
        lineLength = Width / 5;
        lineLengthHalf = lineLength / 2;


        xPos = (Width / 2);
        yPos = YMid + radius + radius / 2;//For Middle Center: (int) ((Height / 2) - ((paintForText.descent() + paintForText.ascent()) / 2)) ;

    }
    int xPos;
    int yPos;
    public float bearingRelatedToMap = 0;
    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
//        int radius=40;
//        MainActivity.TargetNext_DegreeTo = 0;
//        MainActivity.TargetNext_DistanceFriendly = "120m";
        canvas.save();
        canvas.rotate(bearingRelatedToMap, XMid, YMid);
        canvas.drawLine(XMid, YMid - lineLength - lineLength - lineLengthHalf, XMid - lineLength - lineLength, YMid + lineLength + lineLengthHalf, paintPointer);
        canvas.drawLine(XMid, YMid + lineLengthHalf, XMid - lineLength - lineLength, YMid + lineLength + lineLengthHalf, paintPointer);
        canvas.drawLine(XMid, YMid - lineLength - lineLength - lineLengthHalf, XMid + lineLength + lineLength, YMid + lineLength + lineLengthHalf, paintPointer);
        canvas.drawLine(XMid, YMid + lineLengthHalf, XMid + lineLength + lineLength, YMid + lineLength + lineLengthHalf, paintPointer);

//        canvas.drawLine(XMid, YMid - lineLength + 15, XMid + 160, YMid - lineLength + 200, paintPointer);
        canvas.restore();

//            canvas.drawLine(XMid - lineLength , YMid, XMid + lineLength, YMid, paint);
//
//            canvas.drawText(MainActivity.BearingValueFormCurrent + "°" , XMid + radius + radiusHalf, YMid +radius  , paintForText);

    }
}
