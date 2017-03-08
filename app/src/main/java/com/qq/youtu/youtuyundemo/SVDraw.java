package com.qq.youtu.youtuyundemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2017/2/24.
 */
public class SVDraw extends SurfaceView implements SurfaceHolder.Callback {

    protected SurfaceHolder sh;
    private int mWidth;
    private int mHeight;
    private Context context;
    private Camera mCamera;

    public SVDraw(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        sh = getHolder();
        sh.addCallback(this);
        sh.setFormat(PixelFormat.TRANSPARENT);
        setZOrderOnTop(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        clearDraw(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    public void clearDraw(SurfaceHolder holder){
        this.sh = holder;
        if (sh != null){
            DisplayMetrics dm = new DisplayMetrics();
            //获取屏幕信息
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            int width = wm.getDefaultDisplay().getWidth();
            int height = wm.getDefaultDisplay().getHeight();
            Canvas canvas = holder.lockCanvas();
            Paint p = new Paint();
            p.setStrokeWidth(10);
            p.setAntiAlias(true);
            p.setColor(Color.GREEN);
            p.setStyle(Paint.Style.STROKE);
            if (canvas != null){
                canvas.drawRect(width/4,height/4,3*width/4,3*height/4,p);
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
