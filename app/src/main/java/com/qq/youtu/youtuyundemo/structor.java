package com.qq.youtu.youtuyundemo;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * Created by Administrator on 2017/2/26.
 */
public class structor {

    private Bitmap rectBitmap,bitmap1;

    public void setRectBitmap(Bitmap rectBitmap) {
        this.rectBitmap = rectBitmap;
        Log.d("MainActivity","设置成功"  );
    }

    public Bitmap getRectBitmap() {
        return rectBitmap;
    }

    public void setBitmap1(Bitmap bitmap1) {
        this.bitmap1 = bitmap1;
        Log.d("MainActivity","设置成功"  );
    }

    public Bitmap getBitmap1() {
        return bitmap1;
    }
}
