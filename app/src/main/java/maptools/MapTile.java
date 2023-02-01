package maptools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.Size;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MapTile {
    final static String TAG = "سینک_مپ"; //1401-07-30

    static final int DEF_SIZE = 256;
    public static void CreateTilesForZoom(Bitmap source, String outputDir, int zoom, List<LatLng> bounds, Context context) throws FileNotFoundException {
        PointF t_topleft = MapTile.WorldToTilePos(bounds.get(0).latitude, bounds.get(0).longitude, zoom);
        PointF t_botright = MapTile.WorldToTilePos(bounds.get(2).latitude, bounds.get(2).longitude, zoom);


        int imgWidth = source.getWidth();
        int imgHeigth = source.getHeight();

        int tcount_w = (int)t_botright.x - (int)t_topleft.x + 1;
        int tcount_h = (int)t_botright.y - (int)t_topleft.y + 1;

        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        paint.setAntiAlias(true);

        String directoryName = context.getFilesDir() +"/" + outputDir +"/" +zoom;
        File directory = new File(directoryName);
        if (! directory.exists()){
            directory.mkdirs();
        }
        int Size_Pix = DEF_SIZE;
        if (zoom == 13 )
            Size_Pix = DEF_SIZE * 2;
        if (zoom <= 12 )
            Size_Pix = DEF_SIZE * 3;
        if (zoom <= 10 )
            Size_Pix = DEF_SIZE * 4;
        for (int ix = 0; ix < tcount_w; ix++)
        {
            for (int jy = 0; jy < tcount_h; jy++)
            {

                boolean outPng = ix == 0 || ix == tcount_w - 1 || jy == 0 || jy == tcount_h - 1;
                Bitmap res = Bitmap.createBitmap(Size_Pix, Size_Pix, outPng || true ?Bitmap.Config.ARGB_8888:Bitmap.Config.RGB_565);
                if (outPng) {
                    //در صورتی که یکی از کناره ها بود، آلفا رو فعال کنیم
                    BitmapDrawable bd = new BitmapDrawable(context.getResources(), res);
                    bd.setAlpha(100);
                }
                Canvas canvas = new Canvas(res);
                //gr.Clear(Color.Transparent);

                List<PointF> tileBounds = new ArrayList<PointF>();
                tileBounds.add(MapTile.TileToWorldPos((int)t_topleft.x + ix, (int)t_topleft.y + jy, zoom));
                tileBounds.add(MapTile.TileToWorldPos((int)t_topleft.x + ix + 1, (int)t_topleft.y + jy, zoom));
                tileBounds.add(MapTile.TileToWorldPos((int)t_topleft.x + ix + 1, (int)t_topleft.y + jy + 1, zoom));
                tileBounds.add(MapTile.TileToWorldPos((int)t_topleft.x + ix, (int)t_topleft.y + jy + 1, zoom));

                //در آوردن شماره پیکسل تایل اخیر در تصویر منبع
                List<PointF> tilePixels = new ArrayList<PointF>();
                tilePixels.add(LatLonToPixel(imgWidth, imgHeigth, tileBounds.get(0), bounds));
                tilePixels.add(LatLonToPixel(imgWidth, imgHeigth, tileBounds.get(1), bounds));
                tilePixels.add(LatLonToPixel(imgWidth, imgHeigth, tileBounds.get(2), bounds));
                tilePixels.add(LatLonToPixel(imgWidth, imgHeigth, tileBounds.get(3), bounds));

                //if (tilePixels[0].x >= 0 && tilePixels[0].y >= 0 && tilePixels[2].x > 0 && tilePixels[2].y > 0)
                //{
                canvas.drawBitmap(source, new Rect((int)tilePixels.get(0).x, (int)tilePixels.get(0).y, (int)((tilePixels.get(2).x - tilePixels.get(0).x)+tilePixels.get(0).x), (int)((tilePixels.get(2).y - tilePixels.get(0).y)+ tilePixels.get(0).y)),new Rect(0, 0, Size_Pix, Size_Pix),  paint);


                String fileName = ((int)t_topleft.x + ix) + "-" + ((int)t_topleft.y + jy) + ".png";

                File file = new File( directoryName+"/" + fileName);

                //در صورتی که فایل قبلا وجود داشت و یک فایل حاشیه تصویر هم بود
                if (outPng && file.exists()) {
                    //File maybe belongs to another MAP, should merge 2 images
                    Bitmap oldImg = BitmapFactory.decodeFile(file.getPath());
                    for (int i = 0; i < Size_Pix; i++) {
                        for (int j = 0; j < Size_Pix; j++) {
                            int px = oldImg.getPixel(i,j);
                            if (px != 0 ) //HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH&& px != -16777216
                                res.setPixel(i,j,px);
                        }
                    }
                }
                res.compress(outPng?Bitmap.CompressFormat.PNG:Bitmap.CompressFormat.JPEG, 80, new FileOutputStream(file));
                //}
            }
        }

        int resultingngngng = 0;
    }

    public static void deleteTilesForZoom(String outputDir, int zoom, List<LatLng> bounds, Context context) throws FileNotFoundException {
        PointF t_topleft = MapTile.WorldToTilePos(bounds.get(0).latitude, bounds.get(0).longitude, zoom);
        PointF t_botright = MapTile.WorldToTilePos(bounds.get(2).latitude, bounds.get(2).longitude, zoom);

        int tcount_w = (int)t_botright.x - (int)t_topleft.x + 1;
        int tcount_h = (int)t_botright.y - (int)t_topleft.y + 1;

        String directoryName = context.getFilesDir() +"/" + outputDir +"/" +zoom;
        int Size_Pix = DEF_SIZE;
        if (zoom == 13 )
            Size_Pix = DEF_SIZE * 2;
        if (zoom <= 12 )
            Size_Pix = DEF_SIZE * 3;
        if (zoom <= 10 )
            Size_Pix = DEF_SIZE * 4;

        List<PointF> mapBounds = new ArrayList<>();
        mapBounds.add(new PointF((float)bounds.get(0).longitude, (float)bounds.get(0).latitude));
        mapBounds.add(new PointF((float)bounds.get(1).longitude, (float)bounds.get(1).latitude));
        mapBounds.add(new PointF((float)bounds.get(2).longitude, (float)bounds.get(2).latitude));
        mapBounds.add(new PointF((float)bounds.get(3).longitude, (float)bounds.get(3).latitude));

        for (int ix = 0; ix < tcount_w; ix++)
        {
            for (int jy = 0; jy < tcount_h; jy++)
            {
                boolean outPng = ix == 0 || ix == tcount_w - 1 || jy == 0 || jy == tcount_h - 1;

                String fileName = ((int)t_topleft.x + ix) + "-" + ((int)t_topleft.y + jy) + ".png";
                File file = new File( directoryName+"/" + fileName);

                //در صورتی که فایل قبلا وجود داشت و یک فایل حاشیه تصویر هم بود
                if (outPng && file.exists()) {
                    List<PointF> tileBounds = new ArrayList<PointF>();
                    tileBounds.add(MapTile.TileToWorldPos((int)t_topleft.x + ix, (int)t_topleft.y + jy, zoom)); //Top Left
                    tileBounds.add(MapTile.TileToWorldPos((int)t_topleft.x + ix + 1, (int)t_topleft.y + jy, zoom)); //Top Right
                    tileBounds.add(MapTile.TileToWorldPos((int)t_topleft.x + ix + 1, (int)t_topleft.y + jy + 1, zoom)); //Bot RIght
                    tileBounds.add(MapTile.TileToWorldPos((int)t_topleft.x + ix, (int)t_topleft.y + jy + 1, zoom));//Bot Left
                    List<LatLng> tileBoundsLL = new ArrayList<LatLng>();
                    tileBoundsLL.add(new LatLng(tileBounds.get(0).y, tileBounds.get(0).x));
                    tileBoundsLL.add(new LatLng(tileBounds.get(1).y, tileBounds.get(1).x));
                    tileBoundsLL.add(new LatLng(tileBounds.get(2).y, tileBounds.get(2).x));
                    tileBoundsLL.add(new LatLng(tileBounds.get(3).y, tileBounds.get(3).x));

                    PointF topIntersectLeft = getLineIntersection(tileBounds.get(0),  tileBounds.get(1), mapBounds.get(0), mapBounds.get(3));
                    PointF botIntersectLeft = getLineIntersection(tileBounds.get(2),  tileBounds.get(3), mapBounds.get(0), mapBounds.get(3));
                    PointF topIntersectRight = getLineIntersection(tileBounds.get(0),  tileBounds.get(1), mapBounds.get(1), mapBounds.get(2));
                    PointF botIntersectRight = getLineIntersection(tileBounds.get(2),  tileBounds.get(3), mapBounds.get(1), mapBounds.get(2));

                    PointF leftIntersectTop = getLineIntersection(tileBounds.get(0),  tileBounds.get(3), mapBounds.get(0), mapBounds.get(1));
                    PointF rightIntersectTop = getLineIntersection(tileBounds.get(1),  tileBounds.get(2), mapBounds.get(0), mapBounds.get(1));
                    PointF leftIntersectBot = getLineIntersection(tileBounds.get(0),  tileBounds.get(3), mapBounds.get(2), mapBounds.get(3));
                    PointF rightIntersectBot = getLineIntersection(tileBounds.get(1),  tileBounds.get(2), mapBounds.get(2), mapBounds.get(3));

                    PointF [] rectLatLng = new PointF[4];
                    List<PointF> tilePixels = new ArrayList<PointF>();
                    if (tcount_h == 1 && tcount_w == 1){
                        rectLatLng[0] = mapBounds.get(0);
                        rectLatLng[1] = mapBounds.get(1);
                        rectLatLng[2] = mapBounds.get(2);
                        rectLatLng[3] = mapBounds.get(3);
                    }
                    else if(tcount_h == 1 && tcount_w == 2) { // 1 * 2
                        if (ix == 0){ //rightIntersectTop and rightIntersectBot

                            rectLatLng[0] = mapBounds.get(0);
                            rectLatLng[1] = rightIntersectTop;
                            rectLatLng[2] = rightIntersectBot;
                            rectLatLng[3] = mapBounds.get(3);
                        }else if (ix == 1){
                            rectLatLng[0] = leftIntersectTop;
                            rectLatLng[1] = mapBounds.get(1);
                            rectLatLng[2] = mapBounds.get(2);
                            rectLatLng[3] = leftIntersectBot;
                        }
                    }
                    else if(tcount_h == 2 && tcount_w == 1) { // 2 * 1
                        if (jy == 0){
                            rectLatLng[0] = mapBounds.get(0);
                            rectLatLng[1] = mapBounds.get(1);
                            rectLatLng[2] = botIntersectRight;
                            rectLatLng[3] = botIntersectLeft;
                        }else if (jy == 1){
                            rectLatLng[0] = topIntersectLeft;
                            rectLatLng[1] = topIntersectRight;
                            rectLatLng[2] = mapBounds.get(2);
                            rectLatLng[3] = mapBounds.get(3);
                        }
                    }
                    else{
                        if (ix == 0 && jy == 0){
                            //right and bot intersects
                            rectLatLng[0] = mapBounds.get(0);
                            rectLatLng[1] = rightIntersectTop;
                            rectLatLng[2] = tileBounds.get(2);
                            rectLatLng[3] = botIntersectLeft;
                        } else if(ix == 0 && jy == tcount_h - 1){
                            //top and right intersects
                            rectLatLng[0] = topIntersectLeft;
                            rectLatLng[1] = tileBounds.get(1);
                            rectLatLng[2] = rightIntersectBot;
                            rectLatLng[3] = mapBounds.get(3);
                        }else if(ix == tcount_w - 1 && jy == 0){
                            //left and bot intersects
                            rectLatLng[0] = leftIntersectTop;
                            rectLatLng[1] = mapBounds.get(1);
                            rectLatLng[2] = botIntersectRight;
                            rectLatLng[3] = tileBounds.get(3);
                        }else if(ix == tcount_w - 1 && jy == tcount_h - 1){
                            //left and top intersects
                            rectLatLng[0] = tileBounds.get(0);
                            rectLatLng[1] = topIntersectRight;
                            rectLatLng[2] = mapBounds.get(2);
                            rectLatLng[3] = leftIntersectBot;
                        }else if(ix == 0){
                            //left and right on topRow
                            rectLatLng[0] = topIntersectLeft;
                            rectLatLng[1] = tileBounds.get(1);
                            rectLatLng[2] = tileBounds.get(2);
                            rectLatLng[3] = botIntersectLeft;
                        } else if(jy == 0){
                            //top and bot intersect left
                            rectLatLng[0] = leftIntersectTop;
                            rectLatLng[1] = rightIntersectTop;
                            rectLatLng[2] = tileBounds.get(2);
                            rectLatLng[3] = tileBounds.get(3);
                        } else if(jy == tcount_h - 1){
                            //top and bot intersect right
                            rectLatLng[0] = tileBounds.get(0);
                            rectLatLng[1] = tileBounds.get(1);
                            rectLatLng[2] = rightIntersectBot;;
                            rectLatLng[3] = leftIntersectBot;
                        }
                        else if(ix == tcount_w - 1){
                            //left and right on botRow
                            rectLatLng[0] = tileBounds.get(0);
                            rectLatLng[1] = topIntersectRight;
                            rectLatLng[2] = botIntersectRight;
                            rectLatLng[3] = tileBounds.get(3);
                        }
                        else{
                            int asdlkja = 01;
                            int yadslje = asdlkja * 10;
                        }

                    }

                    tilePixels.add(LatLonToPixel(Size_Pix, Size_Pix,  rectLatLng[0], tileBoundsLL));
                    tilePixels.add(LatLonToPixel(Size_Pix, Size_Pix,  rectLatLng[1], tileBoundsLL));
                    tilePixels.add(LatLonToPixel(Size_Pix, Size_Pix,  rectLatLng[2], tileBoundsLL));
                    tilePixels.add(LatLonToPixel(Size_Pix, Size_Pix,  rectLatLng[3], tileBoundsLL));
                    if (tilePixels.size() == 4) {
                        //File maybe belongs to another MAP, should merge 2 images
                        BitmapFactory.Options opt = new BitmapFactory.Options();
                        opt.inMutable = true;
                        Bitmap oldImg = BitmapFactory.decodeFile(file.getPath(), opt);

                        RectF rect = new RectF(tilePixels.get(0).x, tilePixels.get(0).y, tilePixels.get(2).x, tilePixels.get(2).y);
//
//                        Canvas canvas = new Canvas(oldImg);
//                        Paint paint2 = new Paint();
//                        paint2.setStyle(Paint.Style.FILL);
//                        paint2.setColor(0);//Transparent Color
//                        paint2.setAlpha(255);
//                        canvas.drawRect(rect, paint2);
                        for (int i = (int)rect.left; i < rect.right; i++) {
                            for (int j = (int)rect.top; j < rect.bottom; j++) {
                                oldImg.setPixel(i,j,0);
                            }
                        }
                        oldImg.compress(outPng?Bitmap.CompressFormat.PNG:Bitmap.CompressFormat.JPEG, 80, new FileOutputStream(file));
                    }
                }
                else{
                    //Delete Image
                    if (file.exists())
                        file.delete();
                    else{

                    }
                }
            }
        }

        int resultingngngng = 0;
    }


    static PointF LatLonToPixel(int imgWidth, int imgHeigth, PointF latLonToFind, List<LatLng> bounds)
    {
        //delta Lon
        double dlon = (imgWidth * (latLonToFind.x - bounds.get(0).longitude)) / (bounds.get(2).longitude - bounds.get(0).longitude);
        double dlat = (imgHeigth * (latLonToFind.y - bounds.get(0).latitude)) / (bounds.get(0).latitude - bounds.get(2).latitude);

        //return new PointF((float)(bounds.get(0).x + (float)dlon), (float)(bounds.get(0).y - dlat));
        return new PointF((float)dlon, (float)(dlat * -1));
    }



    public static PointF WorldToTilePos(final double lat, final double lon, final int zoom) {
        int xtile = (int)Math.floor( (lon + 180) / 360 * (1<<zoom) ) ;
        int ytile = (int)Math.floor( (1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * (1<<zoom) ) ;
        if (xtile < 0)
            xtile=0;
        if (xtile >= (1<<zoom))
            xtile=((1<<zoom)-1);
        if (ytile < 0)
            ytile=0;
        if (ytile >= (1<<zoom))
            ytile=((1<<zoom)-1);
        return new PointF(xtile, ytile);
    }
    public static PointF TileToWorldPos(double tile_x, double tile_y, int zoom){
        return new PointF( (float)tile2lon((int)tile_x, zoom), (float)tile2lat((int)tile_y, zoom) );
    }
    public static LatLng TileToWorldPosLatLon(double tile_x, double tile_y, int zoom){
        return new LatLng( tile2lat((int)tile_y, zoom) ,  tile2lon((int)tile_x, zoom));
    }

    static double tile2lon(int x, int z) {
        return x / Math.pow(2.0, z) * 360.0 - 180;
    }

    static double tile2lat(int y, int z) {
        double n = Math.PI - (2.0 * Math.PI * y) / Math.pow(2.0, z);
        return Math.toDegrees(Math.atan(Math.sinh(n)));
    }

    public static PointF getLineIntersection(PointF p1Line1, PointF p2Line1, PointF p1Line2, PointF p2Line2)
    {
        PointF
                result = null;

        double s1_x = p2Line1.x - p1Line1.x;
        double s1_y = p2Line1.y - p1Line1.y;

        double s2_x = p2Line2.x - p1Line2.x;
        double s2_y = p2Line2.y - p1Line2.y;

        double makhraj = (-s2_x * s1_y + s1_x * s2_y);

        if (makhraj != 0) {
            double s = (-s1_y * (p1Line1.x - p1Line2.x) + s1_x * (p1Line1.y - p1Line2.y)) / makhraj;
            double t = (s2_x * (p1Line1.y - p1Line2.y) - s2_y * (p1Line1.x - p1Line2.x)) / makhraj;

            if (s >= 0 && s <= 1 && t >= 0 && t <= 1) {
                // Collision detected
                result = new PointF(
                        (float) (p1Line1.x + (t * s1_x)),
                        (float) (p1Line1.y + (t * s1_y)));
            }   // end if
        }
        return result;
    }
}
