package utils;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;
import android.view.MenuItem;

import androidx.annotation.ColorInt;

public class CustomTypeFaceSpan extends TypefaceSpan {

    private final Typeface newType;
    private final int mColor;

    public CustomTypeFaceSpan(String family, Typeface type, @ColorInt int color) {

        super(family);
        newType = type;
        mColor = color;
    }


    public static void applyFontToMenuItem(MenuItem mi, Context context, int mColor) {
        Typeface font = projectStatics.getIranSans_FONT(context);
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypeFaceSpan("", font,mColor), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(mColor);
        applyCustomTypeFace(ds, newType);
    }

    @Override
    public void updateMeasureState(TextPaint paint) {
        applyCustomTypeFace(paint, newType);
    }

    @Override
    public int getSpanTypeId() {
        return super.getSpanTypeId();
    }

    @ColorInt
    public int getForegroundColor() {
        return mColor;
    }

    private static void applyCustomTypeFace(Paint paint, Typeface tf) {
        int oldStyle;
        Typeface old = paint.getTypeface();
        if (old == null) {
            oldStyle = 0;
        } else {
            oldStyle = old.getStyle();
        }

        int fake = oldStyle & ~tf.getStyle();
        if ((fake & Typeface.BOLD) != 0) {
            paint.setFakeBoldText(true);
        }

        if ((fake & Typeface.ITALIC) != 0) {
            paint.setTextSkewX(-0.25f);
        }

        paint.setTypeface(tf);
    }
}