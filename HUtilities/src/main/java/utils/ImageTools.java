package utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

public class ImageTools {
    //Make Image rounded Corner
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        //website : https://stackoverflow.com/questions/2459916/how-to-make-an-imageview-with-rounded-corners/40150715
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    //Convert VectorAsset to Bitmap Set backColor for 0 for null
    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId, int backColor, int foreColor) {
        //webstie : https://stackoverflow.com/questions/33696488/getting-bitmap-from-vector-drawable
        Drawable drawable = null;
        if (foreColor == 0) {
            drawable = ContextCompat.getDrawable(context, drawableId);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                drawable = (DrawableCompat.wrap(drawable)).mutate();
            }
        }
        else{
            drawable = DrawableHelper
                    .withContext(context)
                    .withColor(foreColor)
                    .withDrawable(drawableId)
                    .tint()
                    .get();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        if (backColor != -1){
            Paint paint2 = new Paint();
            paint2.setColor(backColor);
            paint2.setStyle(Paint.Style.FILL);
            canvas.drawPaint(paint2);
        }
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static boolean privateFileExists(final String dir, final String fileName, Context context, ContextWrapper cw){
        final File directory = cw.getDir(dir, Context.MODE_PRIVATE); // path to /data/data/yourapp/app_imageDir
        final File myImageFile = new File(directory, fileName);
        return myImageFile.exists();
    }
    public static boolean fileExists(final String filePathAndName){
        final File myImageFile = new File(filePathAndName);
        return myImageFile.exists();
    }

    //برای ذخیره یک عکس توسط پیکاسو استفاده میشه
    //منبع : https://www.codexpedia.com/android/android-download-and-save-image-through-picasso/
    public static Target picassoImageTarget(Context context, final String imageDir, final String imageName, ImageView imgView) {
        Log.e("تارگت", " Started - " + imageDir);
        final File directory = new File(imageDir);
        if (!directory.exists())
            directory.mkdir();

        return new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                Log.e("تارگت", " picassoImageTarget");
                if (imgView != null)
                    imgView.setImageBitmap(bitmap);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final File myImageFile = new File(directory, imageName); // Create image file
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(myImageFile);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        } catch (IOException e) {
                            Log.e("تارگت", " خطای تارگت به شرح زیر هست:");
                            e.printStackTrace();
                        } finally {
                            try {
                                fos.close();
                            } catch (IOException e) {
                                Log.e("تارگت", " خطای بستن تارگت به شرح زیر هست:");
                                e.printStackTrace();
                            }
                        }
                        Log.e("تارگت", "image saved to >>>" + myImageFile.getAbsolutePath());

                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                Log.e("تارگت", e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                if (placeHolderDrawable != null) {}
                Log.i("تارگت", "Preload");
            }
        };
    }
}
