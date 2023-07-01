package maptools;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import mojafarin.pakoob.MainActivity;
import mojafarin.pakoob.R;
import utils.projectStatics;

import static mojafarin.pakoob.mainactivitymodes.GoToTargetMode.TargetNext_DegreeToWithDeltaAngle;
import static mojafarin.pakoob.mainactivitymodes.GoToTargetMode.TargetNext_DistanceFriendly;

public class MapNavigateToPoint extends View {
    private int XMid;
    private int YMid;
    private int Width;
    private int Height;
    private int radius;
    private int lineLength;
    final Paint paint;
    final Paint paintPointer;
    final Paint paintForText;
    final String DistToDest_Text;
    public MapNavigateToPoint(Context context) {
        this(context, null);
    }

    public MapNavigateToPoint(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public MapNavigateToPoint(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        paint=new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.parseColor("#bbcccccc"));
        paint.setStrokeWidth(20);
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);


        paintPointer=new Paint();
        paintPointer.setStyle(Paint.Style.STROKE);
        paintPointer.setColor(context.getResources().getColor(R.color.homeZoomButtonsMyLocation));
        paintPointer.setAntiAlias(true);
        paintPointer.setStrokeCap(Paint.Cap.ROUND);


        paintForText=new Paint();
        paintForText.setTypeface(projectStatics.getIranSans_FONT(context));
        paintForText.setStyle(Paint.Style.FILL);
        paintForText.setColor(context.getResources().getColor(R.color.greenJeeegh));//Color.parseColor("#444444"));
        paintForText.setStrokeWidth(1);
        paintForText.setTextSize(50);
        paintForText.setTextAlign(Paint.Align.CENTER);
        paintForText.setAntiAlias(true);

        DistToDest_Text = getResources().getString(R.string.distToDest);

    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int width = measureDimension(desiredWidth(), widthMeasureSpec);
//        int height = measureDimension(desiredHeight(), heightMeasureSpec);
        Width = MeasureSpec.getSize(widthMeasureSpec);
        Height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(Width, Height + 5); //در 1402-04 این عدد 5 رو اضافه کردم که پایینش درست بیافته

        paintPointer.setStrokeWidth(Width / 15.0f);

        XMid = Width/2;
        YMid = Height/2;
        radius = XMid / 3*2;
        lineLength = Width / 4;


        xPos = (Width / 2);
        yPos = YMid + radius + radius / 2;//For Middle Center: (int) ((Height / 2) - ((paintForText.descent() + paintForText.ascent()) / 2)) ;
        //((textPaint.descent() + textPaint.ascent()) / 2) is the distance from the baseline to the center.

    }
    int xPos;
    int yPos;
    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
//        int radius=40;
//        MainActivity.TargetNext_DegreeTo = 0;
//        MainActivity.TargetNext_DistanceFriendly = "120m";


        canvas.drawCircle(XMid, YMid, radius, paint);
        canvas.save();
        canvas.rotate((float)TargetNext_DegreeToWithDeltaAngle, XMid, YMid);
        canvas.drawLine(XMid, YMid - lineLength + Width/50, XMid, YMid + lineLength, paintPointer);
        canvas.drawLine(XMid, YMid - lineLength + Width/50, XMid - Width/ 5.625f, YMid - lineLength + Width/ 4.5f, paintPointer);
        canvas.drawLine(XMid, YMid - lineLength + Width/50, XMid + Width/ 5.625f, YMid - lineLength + Width/ 4.5f, paintPointer);
//        canvas.drawLine(XMid, YMid - lineLength + 15, XMid + 160, YMid - lineLength + 200, paintPointer);
        canvas.restore();


        canvas.drawText(DistToDest_Text + TargetNext_DistanceFriendly, xPos, yPos, paintForText);

//            canvas.drawLine(XMid - lineLength , YMid, XMid + lineLength, YMid, paint);
//
//            canvas.drawText(MainActivity.BearingValueFormCurrent + "°" , XMid + radius + radiusHalf, YMid +radius  , paintForText);

    }
}
