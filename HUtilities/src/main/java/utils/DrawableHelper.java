package utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
//Class for change Color of Vector Drawable from link : https://stackoverflow.com/questions/32924986/change-fill-color-on-vector-asset-in-android-studio
//Usage:
//final Drawable drawable = DrawableHelper
//        .withContext(this)
//        .withColor(R.color.white)
//        .withDrawable(R.drawable.ic_search_24dp)
//        .tint()
//        .get();
//
//        actionBar.setHomeAsUpIndicator(drawable);
//        OR:
//        DrawableHelper
//        .withContext(this)
//        .withColor(R.color.white)
//        .withDrawable(R.drawable.ic_search_24dp)
//        .tint()
//        .applyTo(mSearchItem);
/**
 * {@link Drawable} helper class.
 *
 * @author Filipe Bezerra
 * @version 18/01/2016
 * @since 18/01/2016
 */
public class DrawableHelper {
    @NonNull
    Context mContext;
    private int mColor;
    private Drawable mDrawable;
    private Drawable mWrappedDrawable;

    public DrawableHelper(@NonNull Context context) {
        mContext = context;
    }

    public static DrawableHelper withContext(@NonNull Context context) {
        return new DrawableHelper(context);
    }

    public DrawableHelper withDrawable(@DrawableRes int drawableRes) {
        mDrawable = ContextCompat.getDrawable(mContext, drawableRes);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mDrawable = (DrawableCompat.wrap(mDrawable)).mutate();
        }
        return this;
    }

    public DrawableHelper withDrawable(@NonNull Drawable drawable) {
        mDrawable = drawable;
        return this;
    }

    public DrawableHelper withColor(int colorCode) {
        mColor = colorCode;
        return this;
    }

    @SuppressLint("ResourceAsColor")
    public DrawableHelper tint() {
        if (mDrawable == null) {
            throw new NullPointerException("É preciso informar o recurso drawable pelo método withDrawable()");
        }

        if (mColor == 0) {
            throw new IllegalStateException("É necessário informar a cor a ser definida pelo método withColor()");
        }

        mWrappedDrawable = mDrawable.mutate();
        mWrappedDrawable = DrawableCompat.wrap(mWrappedDrawable);
        DrawableCompat.setTint(mWrappedDrawable, mColor);
        DrawableCompat.setTintMode(mWrappedDrawable, PorterDuff.Mode.SRC_IN);

        return this;
    }

    @SuppressWarnings("deprecation")
    public void applyToBackground(@NonNull View view) {
        if (mWrappedDrawable == null) {
            throw new NullPointerException("É preciso chamar o método tint()");
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(mWrappedDrawable);
        } else {
            view.setBackgroundDrawable(mWrappedDrawable);
        }
    }

    public void applyTo(@NonNull ImageView imageView) {
        if (mWrappedDrawable == null) {
            throw new NullPointerException("É preciso chamar o método tint()");
        }

        imageView.setImageDrawable(mWrappedDrawable);
    }

    public void applyTo(@NonNull MenuItem menuItem) {
        if (mWrappedDrawable == null) {
            throw new NullPointerException("É preciso chamar o método tint()");
        }

        menuItem.setIcon(mWrappedDrawable);
    }

    public Drawable get() {
        if (mWrappedDrawable == null) {
            throw new NullPointerException("É preciso chamar o método tint()");
        }

        return mWrappedDrawable;
    }
}