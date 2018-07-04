package com.lalala.fangs.data;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

/**
 * Created by FANGs on 2017/8/20.
 */

public class WallpaperDrawable extends Drawable {


    Bitmap mBitmap;
    int mIntrinsicWidth;
    int mIntrinsicHeight;

    public void setBitmap(Bitmap bitmap) {

        mBitmap = bitmap;

        if (mBitmap == null)

            return;

        mIntrinsicWidth = mBitmap.getWidth();

        mIntrinsicHeight = mBitmap.getHeight();

    }

    @Override

    public void draw(Canvas canvas) {

        if (mBitmap == null) return;

        int width = canvas.getWidth();

        int height = canvas.getHeight();
        int x = (width - mIntrinsicWidth) / 2;
        int y = (height - mIntrinsicHeight) / 2;
        canvas.drawBitmap(mBitmap, x, y, null);
    }

    @Override

    public int getOpacity() {

        return PixelFormat.OPAQUE;

    }

    @Override

    public void setAlpha(int alpha) {

        // Ignore

    }


    @Override

    public void setColorFilter(ColorFilter cf) {

        // Ignore

    }
}