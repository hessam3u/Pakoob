package MorphingButton;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.StateSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import pakoob.DbAndLayout.R;

//Based On :https://github.com/dmytrodanylyk/android-morphing-button
//and https://medium.com/android-news/make-your-app-shine-how-to-make-a-button-morph-into-a-loading-spinner-9efee6e39711
public class MorphingButton extends AppCompatButton{
    public enum State {
        PROGRESS, IDLE_Start, IDLE_End
    }
    public State state = State.IDLE_Start;
    private Padding mPadding;
    private int mHeight;
    private int mWidth;
    private int mColor;
    private int mCornerRadius;
    private int mStrokeWidth;
    private int mStrokeColor;

    protected boolean mAnimationInProgress;

    private StrokeGradientDrawable mDrawableNormal;
    private StrokeGradientDrawable mDrawablePressed;

    public MorphingButton(Context context) {
        super(context);
        initView();
    }

    public MorphingButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public MorphingButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mHeight == 0 && mWidth == 0 && w != 0 && h != 0) {
            mHeight = getHeight();
            mWidth = getWidth();
        }
    }

    public StrokeGradientDrawable getDrawableNormal() {
        return mDrawableNormal;
    }

    public void morph(@NonNull Params params) {
        if (!mAnimationInProgress) {

            mDrawablePressed.setColor(params.colorPressed);
            mDrawablePressed.setCornerRadius(params.cornerRadius);
            mDrawablePressed.setStrokeColor(params.strokeColor);
            mDrawablePressed.setStrokeWidth(params.strokeWidth);

            if (params.duration == 0) {
                morphWithoutAnimation(params);
            } else {
                morphWithAnimation(params);
            }

            mColor = params.color;
            mCornerRadius = params.cornerRadius;
            mStrokeWidth = params.strokeWidth;
            mStrokeColor = params.strokeColor;
        }
    }

    private void morphWithAnimation(@NonNull final Params params) {
        mAnimationInProgress = true;
        setText(null);
        setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        setPadding(mPadding.left, mPadding.top, mPadding.right, mPadding.bottom);

        MorphingAnimation.Params animationParams = MorphingAnimation.Params.create(this)
                .color(mColor, params.color)
                .cornerRadius(mCornerRadius, params.cornerRadius)
                .strokeWidth(mStrokeWidth, params.strokeWidth)
                .strokeColor(mStrokeColor, params.strokeColor)
                .height(getHeight(), params.height)
                .width(getWidth(), params.width)
                .duration(params.duration)
                .listener(new MorphingAnimation.Listener() {
                    @Override
                    public void onAnimationEnd() {
                        finalizeMorphing(params);
                        setClickable(true);
                    }

                    @Override
                    public void onAnimationStart() {
                        setClickable(false);
                    }
                });

        MorphingAnimation animation = new MorphingAnimation(animationParams);
        animation.start();
    }

    private void morphWithoutAnimation(@NonNull Params params) {
        mDrawableNormal.setColor(params.color);
        mDrawableNormal.setCornerRadius(params.cornerRadius);
        mDrawableNormal.setStrokeColor(params.strokeColor);
        mDrawableNormal.setStrokeWidth(params.strokeWidth);

        if(params.width != 0 && params.height !=0) {
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            layoutParams.width = params.width;
            layoutParams.height = params.height;
            setLayoutParams(layoutParams);
        }

        finalizeMorphing(params);
    }

    private void finalizeMorphing(@NonNull Params params) {
        mAnimationInProgress = false;

        if (params.icon != 0 && params.text != null) {
            setIconLeft(params.icon);
            setText(params.text);
        } else if (params.icon != 0) {
            setIcon(params.icon);
        } else if(params.text != null) {
            setText(params.text);
        }

        if (params.animationListener != null) {
            params.animationListener.onAnimationEnd();
        }
    }

    public void blockTouch() {
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    public void unblockTouch() {
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
    }

    private void initView() {
        mPadding = new Padding();
        mPadding.left = getPaddingLeft();
        mPadding.right = getPaddingRight();
        mPadding.top = getPaddingTop();
        mPadding.bottom = getPaddingBottom();

        Resources resources = getResources();
        int cornerRadius = (int) resources.getDimension(R.dimen.mb_corner_radius_2);
        int blue = resources.getColor(R.color.mb_blue);
        int blueDark = resources.getColor(R.color.mb_blue_dark);

        StateListDrawable background = new StateListDrawable();
        mDrawableNormal = createDrawable(blue, cornerRadius, 0);
        mDrawablePressed = createDrawable(blueDark, cornerRadius, 0);

        mColor = blue;
        mStrokeColor = blue;
        mCornerRadius = cornerRadius;

        background.addState(new int[]{android.R.attr.state_pressed}, mDrawablePressed.getGradientDrawable());
        background.addState(StateSet.WILD_CARD, mDrawableNormal.getGradientDrawable());

        setBackgroundCompat(background);
    }

    private StrokeGradientDrawable createDrawable(int color, int cornerRadius, int strokeWidth) {
        StrokeGradientDrawable drawable = new StrokeGradientDrawable(new GradientDrawable());
        drawable.getGradientDrawable().setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(color);
        drawable.setCornerRadius(cornerRadius);
        drawable.setStrokeColor(color);
        drawable.setStrokeWidth(strokeWidth);

        return drawable;
    }

    private void setBackgroundCompat(@Nullable Drawable drawable) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
            setBackgroundDrawable(drawable);
        } else {
            setBackground(drawable);
        }
    }

    public void setIcon(@DrawableRes final int icon) {
        // post is necessary, to make sure getWidth() doesn't return 0
        post(new Runnable() {
            @Override
            public void run() {
                Drawable drawable = ContextCompat.getDrawable(getContext(), icon);
                int padding = (getWidth() / 2) - (drawable.getIntrinsicWidth() / 2);
                setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
                setPadding(padding, 0, 0, 0);
            }
        });
    }

    public void setIconLeft(@DrawableRes int icon) {
        setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
    }

    private class Padding {
        public int left;
        public int right;
        public int top;
        public int bottom;
    }

    public static class Params {
        private int cornerRadius;
        private int width;
        private int height;
        private int color;
        private int colorPressed;
        private int duration;
        private int icon;
        private int strokeWidth;
        private int strokeColor;
        private String text;
        private MorphingAnimation.Listener animationListener;

        private Params() {

        }

        public static Params create() {
            return new Params();
        }

        public Params text(@NonNull String text) {
            this.text = text;
            return this;
        }

        public Params icon(@DrawableRes int icon) {
            this.icon = icon;
            return this;
        }

        public Params cornerRadius(int cornerRadius) {
            this.cornerRadius = cornerRadius;
            return this;
        }

        public Params width(int width) {
            this.width = width;
            return this;
        }

        public Params height(int height) {
            this.height = height;
            return this;
        }

        public Params color(int color) {
            this.color = color;
            return this;
        }

        public Params colorPressed(int colorPressed) {
            this.colorPressed = colorPressed;
            return this;
        }

        public Params duration(int duration) {
            this.duration = duration;
            return this;
        }

        public Params strokeWidth(int strokeWidth) {
            this.strokeWidth = strokeWidth;
            return this;
        }

        public Params strokeColor(int strokeColor) {
            this.strokeColor = strokeColor;
            return this;
        }

        public Params animationListener(MorphingAnimation.Listener animationListener) {
            this.animationListener = animationListener;
            return this;
        }
    }
}
//Ind-Sample:
//final IndeterminateProgressButton btnRequestTrackToPakoob = v.findViewById(R.id.btnRequestTrackToPakoob);
//        btnRequestTrackToPakoob.setOnClickListener(new View.OnClickListener() {
//@Override
//public void onClick(View view) {
//        onMorphButton1Clicked(btnRequestTrackToPakoob);
//        }
//        });
//        morphToSquare(btnRequestTrackToPakoob, 0);


//    private void simulateProgress1(@NonNull final IndeterminateProgressButton button) {
//        int progressColor1 = color(R.color.holo_blue_bright);
//        int progressColor2 = color(R.color.holo_green_light);
//        int progressColor3 = color(R.color.holo_orange_light);
//        int progressColor4 = color(R.color.holo_red_light);
//        int color = color(R.color.mb_gray);
//        int progressCornerRadius = dimen(R.dimen.mb_corner_radius_4);
//        int width = dimen(R.dimen.mb_width_200);
//        int height = dimen(R.dimen.mb_height_8);
//        int duration = integer(R.integer.mb_animation);
//
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                morphToSuccess(button);
//                button.unblockTouch();
//            }
//        }, 4000);
//
//        button.blockTouch(); // prevent user from clicking while button is in progress
//        button.morphToProgress(color, progressCornerRadius, width, height, duration, progressColor1, progressColor2,
//                progressColor3, progressColor4);
//    }
//    private void morphToSquare(final MorphingButton btnMorph, int duration) {
//        MorphingButton.Params square = MorphingButton.Params.create()
//                .duration(duration)
//                .cornerRadius(dimen(R.dimen.mb_corner_radius_2))
//                .width(dimen(R.dimen.mb_width_200))
//                .height(dimen(R.dimen.mb_height_56))
//                .color(color(R.color.mb_blue))
//                .colorPressed(color(R.color.mb_blue_dark))
//                .text(getString(R.string.btnRequestTrackToPakoob));
//        btnMorph.morph(square);
//    }



//---------------------------------------------------------------------
//Det Sample:
//



