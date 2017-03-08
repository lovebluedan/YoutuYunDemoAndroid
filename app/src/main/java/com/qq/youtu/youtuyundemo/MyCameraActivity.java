package com.qq.youtu.youtuyundemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.youtu.Youtu;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Created by Administrator on 2017/2/23.
 */
public class MyCameraActivity extends Activity {

    //声明拍照界面组件SurfaceView
    private SurfaceView mSurfaceView;
    //声明界面控制组件SurfaceHolder
    private SurfaceHolder mSurfaceHolder;
    private DrawImageView mDrawIV = null;
    //声明照相机
    private Camera mCamera;
//    private boolean isPreviewing = false;
    private Context context;
    private String APP_ID = "10077659";
    private String SECRET_ID = "AKIDxkVSRBK1JLSM1duoT6hJfmqshJg4G5vj";
    private String SECRET_KEY = "hAOQEm5KL5YjdFdka9QQ8bmgIdgU0EBq";
    private Bitmap bitmap1;
    private Camera.AutoFocusCallback myAutoFocusCallback = null;
    public static Bitmap rectBitmap;
    public static Bitmap rotaBitmap;
    public static Bitmap sizeBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycamera);
        context = getApplicationContext();
        //实例化拍照界面组件SurfaceView
        mSurfaceView = (SurfaceView) findViewById(R.id.msurfaceview);
        //从SurfaceView中提取SurfaceHolder
        mSurfaceHolder = mSurfaceView.getHolder();
//        mDrawIV = (DrawImageView) findViewById(R.id.drawIV);
//        mDrawIV.onDraw(new Canvas());
        //为mSurfaceHolder添加回调方法
        mSurfaceHolder.addCallback(mSurfaceCallback);
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);//translucent半透明 transparent透明
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        final Button mButton = (Button) findViewById(R.id.mbutton);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mButton.setText("识别中");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                            takePicture();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        //自动聚焦变量回调
        myAutoFocusCallback = new Camera.AutoFocusCallback() {

            public void onAutoFocus(boolean success, Camera camera) {
                // TODO Auto-generated method stub
                if(success)//success表示对焦成功
                {
                    Log.i("MyCameraActivity", "myAutoFocusCallback: success...");
                    //myCamera.setOneShotPreviewCallback(null);
                }
                else
                {
                    //未对焦成功
                    Log.i("MyCameraActivity", "myAutoFocusCallback: 失败了...");

                }
            }
        };
    }
    //SurfaceHolder回调，处理打开相机、关闭相机以及照片尺寸的改变
    SurfaceHolder.Callback mSurfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            //打开相机
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
            //设置预览
            try{
                mCamera.setPreviewDisplay(holder);
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            //获取相机参数
            Camera.Parameters mParameters = mCamera.getParameters();
            //获取系统支持的照片尺寸
            Camera.Parameters parameters = mCamera.getParameters();
            WindowManager wm = getWindowManager();

            int screenWidth = wm.getDefaultDisplay().getWidth();
            int screenHeight = wm.getDefaultDisplay().getHeight();
            Camera.Size preSize = getCloselyPreSize(true, screenWidth, screenHeight, parameters.getSupportedPreviewSizes());
            parameters.setPreviewSize(preSize.width, preSize.height);
            mCamera.setParameters(parameters);
            //设置照片格式
            mParameters.setPictureFormat(PixelFormat.JPEG);
            //设置相机参数
            /*mCamera.setParameters(mParameters);*/
            mCamera.setDisplayOrientation(90);
            //开始预览
            mCamera.startPreview();


            mCamera.autoFocus(myAutoFocusCallback);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
//                        takePicture();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }






        private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
            final double ASPECT_TOLERANCE = 0.1;
            double targetRatio = (double) w / h;
            if (sizes == null) return null;

            Camera.Size optimalSize = null;
            double minDiff = Double.MAX_VALUE;

            int targetHeight = h;

            // Try to find an size match aspect ratio and size
            for (Camera.Size size : sizes) {
                double ratio = (double) size.width / size.height;
                if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }

            // Cannot find the one match the aspect ratio, ignore the requirement
            if (optimalSize == null) {
                minDiff = Double.MAX_VALUE;
                for (Camera.Size size : sizes) {
                    if (Math.abs(size.height - targetHeight) < minDiff) {
                        optimalSize = size;
                        minDiff = Math.abs(size.height - targetHeight);
                    }
                }
            }
            return optimalSize;
        }















        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            //停止预览
            mCamera.stopPreview();
            //释放资源
            mCamera.release();
            mCamera = null;
        }
    };
        //定义拍照方法
    private void takePicture(){
        //停止预览
//        mCamera.stopPreview();
        //拍照
        mCamera.takePicture(null, null, mPictureCallback);
    }
    Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            Log.d("MyCameraActivity", "myShutterCallback:onShutter...");
        }
    };

    //定义照片回调方法
    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            //执行照片保存方法
//            new SavePictureTask().execute(data);
            Bitmap bitmap = null;
            if (data != null){
                bitmap = BitmapFactory.decodeByteArray(data,0,data.length);//data是字节数据，将其解析成位图
                if (bitmap == null){
                    return;
                }
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                Log.d("MyCameraActivity", "width:" + width);
                Log.d("MyCameraActivity", "height:" + height);
                Matrix matrix = new Matrix();
//                matrix.setRotate(270);
                matrix.postRotate((float)270.0);
                rotaBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
//                Bitmap rotaBitmap = ImageUtil.getRotateBitmap(bitmap, 90.0f);
//                Bitmap bitmap1 = Bitmap.createBitmap(bitmap, width / 4, height / 4, width / 2, height / 2, matrix, false);
//                bitmap1 = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
                //旋转后rotaBitmap是960×1280.预览surfaview的大小是540×800
                //将960×1280缩放到540×800
//                Bitmap sizeBitmap = Bitmap.createScaledBitmap(rotaBitmap, 800, 540, true);
                sizeBitmap = Bitmap.createScaledBitmap(rotaBitmap, 800, 600, true);
//                Bitmap rectBitmap = Bitmap.createBitmap(sizeBitmap, 100, 200, 300, 300);//截取
                rectBitmap = Bitmap.createBitmap(sizeBitmap, 200, 150, 400, 300);//截取
                camera.stopPreview();
                //保存图片到sdcard
                FileOutputStream fos = null;
                File file = new File("/sdcard/Image/");
                file.mkdirs();//创建文件夹
                String name = System.currentTimeMillis() + ".jpg";
                String fileName = "/sdcard/Image/"+name;
                try {
                    fos = new FileOutputStream(fileName);
                    rotaBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);//把数据写入文件
                    Log.d("SavePictureTask", "保存成功");
                    structor structor = new structor();
                    structor.setRectBitmap(rotaBitmap);

//                    final Bitmap finalBitmap = rectBitmap;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Log.d("SavePictureTask", "线程");
                                Youtu faceYoutu = new Youtu(APP_ID, SECRET_ID, SECRET_KEY,Youtu.API_YOUTU_END_POINT);
                                JSONObject respose = faceYoutu.DetectFace(rotaBitmap, 1);
                                Log.d("SavePictureTask", respose.toString());

                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (KeyManagementException e) {
                                e.printStackTrace();
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    finish();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }finally {
                    try {
                        fos.flush();
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //把文件插入系统图库
                try {
                    MediaStore.Images.Media.insertImage(context.getContentResolver(),
                            file.getAbsolutePath(),fileName,null);
                    Log.d("SavePictureTask", "保存成功");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                //最后通知图库更新
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + fileName)));
            }

            //开启预览
//            camera.startPreview();
        }
    };
        //定义预览的类
    /**
     * 通过对比得到与宽高比最接近的预览尺寸（如果有相同尺寸，优先选择）
     *
     * @param isPortrait 是否竖屏
     * @param surfaceWidth 需要被进行对比的原宽
     * @param surfaceHeight 需要被进行对比的原高
     * @param preSizeList 需要对比的预览尺寸列表
     * @return 得到与原宽高比例最接近的尺寸
     */
    public static  Camera.Size getCloselyPreSize(boolean isPortrait, int surfaceWidth, int surfaceHeight, List<Camera.Size> preSizeList) {
        int reqTmpWidth;
        int reqTmpHeight;
        // 当屏幕为垂直的时候需要把宽高值进行调换，保证宽大于高
        if (isPortrait) {
            reqTmpWidth = surfaceHeight;
            reqTmpHeight = surfaceWidth;
        } else {
            reqTmpWidth = surfaceWidth;
            reqTmpHeight = surfaceHeight;
        }
        //先查找preview中是否存在与surfaceview相同宽高的尺寸
        for(Camera.Size size : preSizeList){
            if((size.width == reqTmpWidth) && (size.height == reqTmpHeight)){
                return size;
            }
        }

        // 得到与传入的宽高比最接近的size
        float reqRatio = ((float) reqTmpWidth) / reqTmpHeight;
        float curRatio, deltaRatio;
        float deltaRatioMin = Float.MAX_VALUE;
        Camera.Size retSize = null;
        for (Camera.Size size : preSizeList) {
            curRatio = ((float) size.width) / size.height;
            deltaRatio = Math.abs(reqRatio - curRatio);
            if (deltaRatio < deltaRatioMin) {
                deltaRatioMin = deltaRatio;
                retSize = size;
            }
        }

        return retSize;
    }
    //定义保存照片任务类
    class SavePictureTask extends AsyncTask<byte[],String,String>{
        @Override
        protected String doInBackground(byte[]... params) {
            //创建文件
            File picture = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");
            //如果文件存在删除掉
            if (picture.exists()) {
                picture.delete();
            }
            ;
            try {
                //获得文件输出流
                FileOutputStream fos = new FileOutputStream(picture.getPath());
                //写到该文件
                fos.write(params[0]);
                //关闭文件流
                fos.close();
            } catch (Exception e) {
            }
            return null;
        }
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}

