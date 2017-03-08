package com.qq.youtu.youtuyundemo;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.common.Config;
import com.common.LoadingDialog;
import com.common.YTServerAPI;
import com.facein.CameraActivity;
import com.facein.CardVideoActivity;
import com.youtu.Youtu;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

/*
* demo展示如何调用优图开放平台API接口，网络请求返回的数据以log形式展示，请开发者用Android studio查看，
* 具体的接口协议请参考：http://open.youtu.qq.com。
*
* 请在Config.java里设置自己申请的 APP_ID, SECRET_ID, SECRET_KEY,否则网络请求签名验证会出错。
*
*
*人脸核身相关接口，需要申请权限接入，具体参考http://open.youtu.qq.com/welcome/service#/solution-facecheck
*人脸核身接口包括：
*	public JSONObject IdcardOcrVIP(Bitmap bitmap, int cardType) throws  IOException,
*			JSONException, KeyManagementException, NoSuchAlgorithmException;
*	public JSONObject FaceCompareVip(Bitmap bitmapA, Bitmap bitmapB) throws  IOException,
*			JSONException, KeyManagementException, NoSuchAlgorithmException
*	public JSONObject IdcardFaceCompare(Bitmap bitmap, String name, String idcard) throws  IOException,
*			JSONException, KeyManagementException, NoSuchAlgorithmException ;
*	public JSONObject LivegetFour() throws  IOException,
*			JSONException, KeyManagementException, NoSuchAlgorithmException;
*	public JSONObject LiveDetectFour(byte[] video, Bitmap bitmap, String validateData, boolean isCompare) throws  IOException,
*			JSONException, KeyManagementException, NoSuchAlgorithmException;
*	public JSONObject IdcardLiveDetectFour(byte[] video, String validateData, String name, String idcard) throws  IOException,
*			JSONException, KeyManagementException, NoSuchAlgorithmException;
*
*/

public class MainActivity extends Activity {
    private final String LOG_TAG = MainActivity.class.getName();
    private Bitmap theSelectedImage = null;
    private BitmapFactory.Options opts = null;
    private Button mLocalPicButton;
    private Button mremotePicButton;
    private Button mEnterFaceIn;
    private YTServerAPI mServerAPI;
    private LoadingDialog mLoadingDialog;

    private String APP_ID = "10077659";
    private String SECRET_ID = "AKIDxkVSRBK1JLSM1duoT6hJfmqshJg4G5vj";
    private String SECRET_KEY = "hAOQEm5KL5YjdFdka9QQ8bmgIdgU0EBq";

    private Button similarity;
    private Button discern;//识别
    private Button card;//身份证
    private Button xiangsi;//相似
    private Camera camera;//声明相机
    private SurfaceHolder holder;
    private SurfaceView surface;
    private Bitmap rectBitmap;
    private Bitmap bitmap1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        APP_ID = Config.APP_ID;
        SECRET_ID = Config.SECRET_ID;
        SECRET_KEY = Config.SECRET_KEY;

        mLocalPicButton = (Button)findViewById(R.id.localTest);
        mremotePicButton = (Button)findViewById(R.id.remoteTest);
        mEnterFaceIn = (Button)findViewById(R.id.enterFaceIn);
        opts = new BitmapFactory.Options();
        opts.inDensity = this.getResources().getDisplayMetrics().densityDpi;
        opts.inTargetDensity = this.getResources().getDisplayMetrics().densityDpi;

        similarity = (Button) findViewById(R.id.similarity);
        discern = (Button) findViewById(R.id.discern);
        card = (Button) findViewById(R.id.card);
        xiangsi = (Button) findViewById(R.id.xiangsi);
        xiangsi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
//                        structor structor = new structor();
//                        Bitmap bitmap1 = rectPhoto.rectBitmap;
//                        Bitmap rectBitmap = myCameraActivity.rectBitmap;
                        Log.d("MainActivity", rectBitmap + "");
                        Log.d("MainActivity", bitmap1 + "");
                        Bitmap bitmap = null;
                        if (bitmap1 != null){
                            bitmap = Bitmap.createBitmap(bitmap1, 550, 480, 250, 250);
//                            saveJpeg(bitmap);
                        }
                        Youtu faceYoutu = new Youtu(APP_ID, SECRET_ID, SECRET_KEY, Youtu.API_YOUTU_END_POINT);
                        try {
                            JSONObject jsonObject = faceYoutu.FaceCompare(rectBitmap, bitmap);
                            String similarity1 = jsonObject.getString("similarity");
                            Log.d("MainActivity", "相似度:" + similarity1 + "%");
                            Message message = new Message();
                            message.what = 1;
                            Bundle bundle = new Bundle();
                            bundle.putString("similarity1",similarity1);
                            message.setData(bundle);
                            handler.sendMessage(message);
                            bitmap1 = null;
                            rectBitmap = null;
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.d("aaaaaaaaaaaaaaaaaaaaa",e.getMessage());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (KeyManagementException e) {
                            e.printStackTrace();
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        discern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                openFrontFacingCameraGingerbread();
                Intent intent = new Intent(MainActivity.this, MyCameraActivity.class);
                startActivityForResult(intent, 2);
            }
        });
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RectPhoto.class);
                startActivityForResult(intent, 3);
            }
        });
        similarity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Youtu faceYoutu = new Youtu(APP_ID, SECRET_ID, SECRET_KEY, Youtu.API_YOUTU_END_POINT);
                        Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.geyou_1, null);
                        Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.geyou_2, null);
                        JSONObject respone = null;
                        try {
                            respone = faceYoutu.FaceCompare(bitmap1, bitmap2);
                            String similarity1 = respone.getString("similarity");
                           // Log.d("MainActivity" + "xxxxxxxxxxxx", "respone:" + respone);
                            Log.d("MainActivity", "相似度:" + similarity1 + "%");
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
            }
        });

        mLocalPicButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });

        mremotePicButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
//                            Youtu faceYoutu = new Youtu(APP_ID, SECRET_ID, SECRET_KEY, Youtu.API_YOUTU_END_POINT);
//                            JSONObject respose = faceYoutu.FaceCompareUrl("http://open.youtu.qq.com/content/img/slide-1.jpg", "http://open.youtu.qq.com/content/img/slide-1.jpg");
//                            Log.d(LOG_TAG, respose.toString());
                            Youtu faceYoutu = new Youtu(APP_ID, SECRET_ID, SECRET_KEY, Youtu.API_YOUTU_END_POINT);
                            Bitmap selectedImage = BitmapFactory.decodeResource(getResources(), R.drawable.cccard, opts);
                            JSONObject respose = faceYoutu.FaceShape(selectedImage, 1);
                            Log.d(LOG_TAG, respose.toString());
                            if (null != selectedImage) {
                                selectedImage.recycle();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        mEnterFaceIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, CardVideoActivity.class);
                Intent intent = new Intent(MainActivity.this, CardVideoActivity.class);
                startActivity(intent);
            }
        });
//        testImageOcr();



        //testFaceIn();
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    Bundle data = msg.getData();
                    String similarity1 = data.getString("similarity1");
                    Toast.makeText(MainActivity.this, "相似度:" + similarity1 + "%", Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        }
    });

    /*给定一个Bitmap，进行保存*/
    public void saveJpeg(Bitmap bm){
        String savePath = "/mnt/sdcard/rectPhoto/";
        File folder = new File(savePath);
        if(!folder.exists()) //如果文件夹不存在则创建
        {
            folder.mkdir();
        }
        long dataTake = System.currentTimeMillis();
        String jpegName = savePath + dataTake +".jpg";
        Log.d("MainActivity", "saveJpeg:jpegName--" + jpegName);
        //File jpegFile = new File(jpegName);
        try {
            FileOutputStream fout = new FileOutputStream(jpegName);
            BufferedOutputStream bos = new BufferedOutputStream(fout);

            //			//如果需要改变大小(默认的是宽960×高1280),如改成宽600×高800
            //			Bitmap newBM = bm.createScaledBitmap(bm, 600, 800, false);

            bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
            Log.i("MainActivity", "saveJpeg：存储完毕！");
//            finish();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.i("MainActivity", "saveJpeg:存储失败！");
            e.printStackTrace();
        }
        MainActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + jpegName)));

    }

    void testFaceIn(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Youtu faceYoutu = new Youtu(APP_ID, SECRET_ID, SECRET_KEY, Youtu.API_VIP_END_POINT);

                //IdcardOcrVIP
                try {
                    Log.d(LOG_TAG, "=====================================");
                    Log.d(LOG_TAG, "IdcardOcrVIP");
                    Bitmap selectedImage = BitmapFactory.decodeResource(getResources(), R.drawable.id, opts);
                    JSONObject respose = faceYoutu.IdcardOcrVIP(selectedImage, 0);
                    Log.d(LOG_TAG, respose.toString());
                    if(null != selectedImage) {
                        selectedImage.recycle();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //FaceCompareVip

//                try {
//                    Log.d(LOG_TAG, "=====================================");
//                    Log.d(LOG_TAG, "FaceCompareVip");
//                    Bitmap selectedImage = BitmapFactory.decodeResource(getResources(), R.drawable.geyou_2, opts);
//                    Bitmap selectedImage2 = BitmapFactory.decodeResource(getResources(), R.drawable.geyou_1, opts);
//                    JSONObject respose = faceYoutu.FaceCompareVip(selectedImage, selectedImage2);
//                    Log.d(LOG_TAG, respose.toString());
//                    if(null != selectedImage) {
//                        selectedImage.recycle();
//                    }
//                    if(null != selectedImage2) {
//                        selectedImage2.recycle();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

               //IdcardFaceCompare

//                try {
//                    Log.d(LOG_TAG, "=====================================");
//                    Log.d(LOG_TAG, "IdcardFaceCompare");
//                    Bitmap selectedImage = BitmapFactory.decodeResource(getResources(), R.drawable.geyou_2, opts);
//                    JSONObject respose = faceYoutu.IdcardFaceCompare(selectedImage, "张三", "123456789012121234");
//                    Log.d(LOG_TAG, respose.toString());
//                    if(null != selectedImage) {
//                        selectedImage.recycle();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }


                //LivegetFour
//                try {
//                    Log.d(LOG_TAG, "=====================================");
//                    Log.d(LOG_TAG, "LivegetFour");
//                    Bitmap selectedImage = BitmapFactory.decodeResource(getResources(), R.drawable.geyou_2, opts);
//                    JSONObject respose = faceYoutu.LivegetFour();
//                    Log.d(LOG_TAG, respose.toString());
//                    if(null != selectedImage) {
//                        selectedImage.recycle();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

                //LiveDetectFour
//                try {
//                    Log.d(LOG_TAG, "=====================================");
//                    Log.d(LOG_TAG, "LiveDetectFour");
//
//                    InputStream stream = getResources().openRawResource(R.raw.video);
//                    ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
//                    byte[] b = new byte[1000];
//                    int n;
//                    while ((n = stream.read(b)) != -1)
//                        out.write(b, 0, n);
//                    stream.close();
//                    out.close();
//
//                    final byte[] vedioByte = out.toByteArray();
//                    Bitmap selectedImage = BitmapFactory.decodeResource(getResources(), R.drawable.geyou_2, opts);
//                    JSONObject respose = faceYoutu.LiveDetectFour(vedioByte ,selectedImage, "3388", true);
//                    Log.d(LOG_TAG, respose.toString());
//                    if(null != selectedImage) {
//                        selectedImage.recycle();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }


                //IdcardLiveDetectFour
//                try {
//                    Log.d(LOG_TAG, "=====================================");
//                    Log.d(LOG_TAG, "IdcardLiveDetectFour");
//
//                    InputStream stream = getResources().openRawResource(R.raw.video);
//                    ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
//                    byte[] b = new byte[1000];
//                    int n;
//                    while ((n = stream.read(b)) != -1)
//                        out.write(b, 0, n);
//                    stream.close();
//                    out.close();
//
//                    final byte[] vedioByte = out.toByteArray();
//                    Bitmap selectedImage = BitmapFactory.decodeResource(getResources(), R.drawable.geyou_2, opts);
//                    JSONObject respose = faceYoutu.IdcardLiveDetectFour(vedioByte ,"3388", "张三", "123456789012121234");
//                    Log.d(LOG_TAG, respose.toString());
//                    if(null != selectedImage) {
//                        selectedImage.recycle();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }



            }
        }).start();
    }

    void testImage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Youtu faceYoutu = new Youtu(APP_ID, SECRET_ID, SECRET_KEY, API_TENCENTYUN_END_POINT);
                Youtu faceYoutu = new Youtu(APP_ID, SECRET_ID, SECRET_KEY, Youtu.API_YOUTU_END_POINT);
                //               Youtu faceYoutu = new Youtu(APP_ID, SECRET_ID, SECRET_KEY, "http://10.198.5.146/youtu/");

                try {
                    Log.d(LOG_TAG, "=====================================");
                    Log.d(LOG_TAG, "imagePorn");
                    Bitmap selectedImage = BitmapFactory.decodeResource(getResources(), R.drawable.id, opts);
                    JSONObject respose = faceYoutu.ImagePorn(selectedImage);
                    Log.d(LOG_TAG, respose.toString());
                    if(null != selectedImage) {
                        selectedImage.recycle();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    void testImageOcr() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Youtu faceYoutu = new Youtu(APP_ID, SECRET_ID, SECRET_KEY, API_TENCENTYUN_END_POINT);
                Youtu faceYoutu = new Youtu(APP_ID, SECRET_ID, SECRET_KEY, Youtu.API_YOUTU_END_POINT);
//                Youtu faceYoutu = new Youtu(APP_ID, SECRET_ID, SECRET_KEY, "http://101.226.76.164:18082/youtu/");

                try {
                    Log.d(LOG_TAG, "=====================================");
                    Log.d(LOG_TAG, "idcardocr");
                    Bitmap selectedImage = BitmapFactory.decodeResource(getResources(), R.drawable.id, opts);
                    JSONObject respose = faceYoutu.IdcardOcr(selectedImage, 0);
                    Log.d(LOG_TAG, respose.toString());
                    if(null != selectedImage) {
                        selectedImage.recycle();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    Log.d(LOG_TAG, "=====================================");
                    Log.d(LOG_TAG, "namecardocr");
                    Bitmap selectedImage = BitmapFactory.decodeResource(getResources(), R.drawable.namecard, opts);
                    JSONObject respose = faceYoutu.NamecardOcr(selectedImage);
                    Log.d(LOG_TAG, respose.toString());
                    if(null != selectedImage) {
                        selectedImage.recycle();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    void testcase1() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Youtu faceYoutu = new Youtu(APP_ID, SECRET_ID, SECRET_KEY, API_TENCENTYUN_END_POINT);
                Youtu faceYoutu = new Youtu(APP_ID, SECRET_ID, SECRET_KEY, Youtu.API_YOUTU_END_POINT);

                Context context = getApplicationContext();
                Resources res = context.getResources();

                Uri noface = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                        + res.getResourcePackageName(R.drawable.noface) + "/"
                        + res.getResourceTypeName(R.drawable.noface) + "/"
                        + res.getResourceEntryName(R.drawable.noface));
                Uri sameface1 = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                        + res.getResourcePackageName(R.drawable.geyou_1) + "/"
                        + res.getResourceTypeName(R.drawable.geyou_1) + "/"
                        + res.getResourceEntryName(R.drawable.geyou_1));
                Uri sameface2 = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                        + res.getResourcePackageName(R.drawable.geyou_2) + "/"
                        + res.getResourceTypeName(R.drawable.geyou_2) + "/"
                        + res.getResourceEntryName(R.drawable.geyou_2));
                Uri multiface = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                        + res.getResourcePackageName(R.drawable.nose_tip_871_254) + "/"
                        + res.getResourceTypeName(R.drawable.nose_tip_871_254) + "/"
                        + res.getResourceEntryName(R.drawable.nose_tip_871_254));
                Uri multiface2 = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                        + res.getResourcePackageName(R.drawable.multi1) + "/"
                        + res.getResourceTypeName(R.drawable.multi1) + "/"
                        + res.getResourceEntryName(R.drawable.multi1));
                Uri onface_in_multiface = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                        + res.getResourcePackageName(R.drawable.oneface_femal) + "/"
                        + res.getResourceTypeName(R.drawable.oneface_femal) + "/"
                        + res.getResourceEntryName(R.drawable.oneface_femal));
                Uri broken_image1 = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                        + res.getResourcePackageName(R.drawable.broken1) + "/"
                        + res.getResourceTypeName(R.drawable.broken1) + "/"
                        + res.getResourceEntryName(R.drawable.broken1));
                Uri broken_image2 = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                        + res.getResourcePackageName(R.drawable.bad) + "/"
                        + res.getResourceTypeName(R.drawable.bad) + "/"
                        + res.getResourceEntryName(R.drawable.bad));
                Uri broken_image3 = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                        + res.getResourcePackageName(R.drawable.broken2) + "/"
                        + res.getResourceTypeName(R.drawable.broken2) + "/"
                        + res.getResourceEntryName(R.drawable.broken2));
                Uri food = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                        + res.getResourcePackageName(R.drawable.food01) + "/"
                        + res.getResourceTypeName(R.drawable.food01) + "/"
                        + res.getResourceEntryName(R.drawable.food01));
                Uri fuzzy_pic = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                        + res.getResourcePackageName(R.drawable.fuzzy) + "/"
                        + res.getResourceTypeName(R.drawable.fuzzy) + "/"
                        + res.getResourceEntryName(R.drawable.fuzzy));
                Uri not_fuzzy_pic = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                        + res.getResourcePackageName(R.drawable.good_pic) + "/"
                        + res.getResourceTypeName(R.drawable.good_pic) + "/"
                        + res.getResourceEntryName(R.drawable.geyou_2));

                try {

                    Log.d(LOG_TAG, "=====================================");
                    Log.d(LOG_TAG, "detectFace");
                    Log.d(LOG_TAG, "-------------------------------------");
                    Log.d(LOG_TAG, "detect face mode 0");
                    Bitmap selectedImage = BitmapFactory.decodeResource(getResources(), R.drawable.geyou_1, opts);
                    JSONObject respose = faceYoutu.DetectFace(selectedImage, 0);
                    Log.d(LOG_TAG, respose.toString());
                    if(null != selectedImage) {
                        selectedImage.recycle();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {

                    Log.d(LOG_TAG, "-------------------------------------");
                    Log.d(LOG_TAG, "detect face mode 1");
                    Bitmap selectedImage = BitmapFactory.decodeResource(getResources(), R.drawable.geyou_1, opts);
                    JSONObject respose = faceYoutu.DetectFace(selectedImage, 1);
                    Log.d(LOG_TAG, respose.toString());
                    if(null != selectedImage) {
                        selectedImage.recycle();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {

                    Log.d(LOG_TAG, "-------------------------------------");
                    Log.d(LOG_TAG, "detect multiface mode 0");
                    Bitmap selectedImage = BitmapFactory.decodeResource(getResources(), R.drawable.nose_tip_871_254, opts);
                    JSONObject respose = faceYoutu.DetectFace(selectedImage, 0);
                    // get respose
                    Log.d(LOG_TAG, respose.toString());
                    if(null != selectedImage) {
                        selectedImage.recycle();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {

                    Log.d(LOG_TAG, "-------------------------------------");
                    Log.d(LOG_TAG, "detect multiface mode 1");
                    Bitmap selectedImage = BitmapFactory.decodeResource(getResources(), R.drawable.multi2, opts);
                    JSONObject respose = faceYoutu.DetectFace(selectedImage, 1);
                    // get respose
                    Log.d(LOG_TAG, respose.toString());
                    if(null != selectedImage) {
                        selectedImage.recycle();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {

                    Log.d(LOG_TAG, "-------------------------------------");
                    Log.d(LOG_TAG, "detect noface mode 0");
                    Bitmap selectedImage = BitmapFactory.decodeResource(getResources(), R.drawable.noface, opts);
                    JSONObject respose = faceYoutu.DetectFace(selectedImage, 0);
                    // get respose
                    Log.d(LOG_TAG, respose.toString());
                    if(null != selectedImage) {
                        selectedImage.recycle();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {

                    Log.d(LOG_TAG, "-------------------------------------");
                    Log.d(LOG_TAG, "detect face image is illegal");
                    Bitmap selectedImage = BitmapFactory.decodeResource(getResources(), R.drawable.broken1, opts);
                    JSONObject respose = faceYoutu.DetectFace(selectedImage, 0);
                    // get respose
                    Log.d(LOG_TAG, respose.toString());

                    Bitmap selectedImage2 = BitmapFactory.decodeResource(getResources(), R.drawable.broken2, opts);
                    JSONObject ret2 = faceYoutu.DetectFace(selectedImage2, 0);
                    Log.d(LOG_TAG, ret2.toString());

                    Bitmap selectedImage3 = BitmapFactory.decodeResource(getResources(), R.drawable.bad, opts);
                    JSONObject ret3 = faceYoutu.DetectFace(selectedImage3, 0);
                    Log.d(LOG_TAG, ret3.toString());
                    if(null != selectedImage) {
                        selectedImage.recycle();
                    }
                    if(null != selectedImage2) {
                        selectedImage2.recycle();
                    }
                    if(null != selectedImage3){
                        selectedImage3.recycle();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {

                    Log.d(LOG_TAG, "=====================================");
                    Log.d(LOG_TAG, "faceshape");
                    Log.d(LOG_TAG, "-------------------------------------");
                    Log.d(LOG_TAG, "faceshape mode 0");
                    Bitmap selectedImage = BitmapFactory.decodeResource(getResources(), R.drawable.geyou_2 , opts);
                    JSONObject respose = faceYoutu.FaceShape(selectedImage, 0);
                    // get respose
                    Log.d(LOG_TAG, respose.toString());

                    if(null != selectedImage) {
                        selectedImage.recycle();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {

                    Log.d(LOG_TAG, "-------------------------------------");
                    Log.d(LOG_TAG, "FaceShape mode 1");
                    Bitmap selectedImage = BitmapFactory.decodeResource(getResources(), R.drawable.geyou_2, opts);
                    JSONObject respose = faceYoutu.FaceShape(selectedImage, 1);
                    // get respose
                    Log.d(LOG_TAG, respose.toString());
                    if(null != selectedImage) {
                        selectedImage.recycle();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            try {

                Log.d(LOG_TAG, "-------------------------------------");
                Log.d(LOG_TAG, "shape multiface mode 0");
                Bitmap selectedImage = BitmapFactory.decodeResource(getResources(), R.drawable.multi2, opts);;
                JSONObject respose = faceYoutu.FaceShape(selectedImage, 0);
                // get respose
                Log.d(LOG_TAG, respose.toString());
                if(null != selectedImage) {
                    selectedImage.recycle();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {

                Log.d(LOG_TAG, "-------------------------------------");
                Log.d(LOG_TAG, "shape multiface mode 1");
                Bitmap selectedImage = BitmapFactory.decodeResource(getResources(), R.drawable.multi2, opts);
                JSONObject respose = faceYoutu.FaceShape(selectedImage, 1);
                // get respose
                Log.d(LOG_TAG, respose.toString());
                if(null != selectedImage) {
                    selectedImage.recycle();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {

                Log.d(LOG_TAG, "-------------------------------------");
                Log.d(LOG_TAG, "shape noface mode 0");
                Bitmap selectedImage = BitmapFactory.decodeResource(getResources(), R.drawable.noface, opts);
                JSONObject respose = faceYoutu.FaceShape(selectedImage, 0);
                // get respose
                Log.d(LOG_TAG, respose.toString());
                if(null != selectedImage) {
                    selectedImage.recycle();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {

                Log.d(LOG_TAG, "-------------------------------------");
                Log.d(LOG_TAG, "shape face image is illegal");
                Bitmap selectedImage = BitmapFactory.decodeResource(getResources(), R.drawable.broken1, opts);
                JSONObject respose = faceYoutu.FaceShape(selectedImage, 0);
                Log.d(LOG_TAG, respose.toString());

                Bitmap selectedImage2 = BitmapFactory.decodeResource(getResources(), R.drawable.broken2, opts);
                JSONObject ret2 = faceYoutu.FaceShape(selectedImage2, 0);
                Log.d(LOG_TAG, ret2.toString());

                Bitmap selectedImage3 = BitmapFactory.decodeResource(getResources(), R.drawable.bad, opts);
                JSONObject ret3 = faceYoutu.FaceShape(selectedImage3, 0);
                Log.d(LOG_TAG, ret3.toString());
                if(null != selectedImage) {
                    selectedImage.recycle();
                }
                if(null != selectedImage2) {
                    selectedImage2.recycle();
                }
                if(null != selectedImage3){
                    selectedImage3.recycle();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {

                Log.d(LOG_TAG, "=====================================");
                Log.d(LOG_TAG, "FaceCompare");
                Log.d(LOG_TAG, "-------------------------------------");
                Log.d(LOG_TAG, "FaceCompare both face only one");
                Bitmap selectedImage = BitmapFactory.decodeResource(getResources(), R.drawable.geyou_2, opts);
                Bitmap selectedImage2 = BitmapFactory.decodeResource(getResources(), R.drawable.geyou_1, opts);
                ;
                JSONObject respose = faceYoutu.FaceCompare(selectedImage, selectedImage2);
                Log.d(LOG_TAG + "xxxxxxxxxxxx", respose.toString());
                if(null != selectedImage) {
                    selectedImage.recycle();
                }
                if(null != selectedImage2) {
                    selectedImage2.recycle();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {

                Log.d(LOG_TAG, "-------------------------------------");
                Log.d(LOG_TAG, "FaceCompare A multiface face B multiface");
                Bitmap selectedImage = BitmapFactory.decodeResource(getResources(), R.drawable.multi2, opts);
                Bitmap selectedImage2 = BitmapFactory.decodeResource(getResources(), R.drawable.multi3, opts);

                JSONObject respose = faceYoutu.FaceCompare(selectedImage, selectedImage2);
                // get respose
                Log.d(LOG_TAG, respose.toString());
                if(null != selectedImage) {
                    selectedImage.recycle();
                }
                if(null != selectedImage2) {
                    selectedImage2.recycle();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {

                Log.d(LOG_TAG, "-------------------------------------");
                Log.d(LOG_TAG, "FaceCompare A face B no");
                Bitmap selectedImage = BitmapFactory.decodeResource(getResources(), R.drawable.geyou_2, opts);
                Bitmap selectedImage2 = BitmapFactory.decodeResource(getResources(), R.drawable.noface, opts);

                JSONObject respose = faceYoutu.FaceCompare(selectedImage, selectedImage2);
                // get respose
                Log.d(LOG_TAG, respose.toString());
                if(null != selectedImage) {
                    selectedImage.recycle();
                }
                if(null != selectedImage2) {
                    selectedImage2.recycle();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {

                Log.d(LOG_TAG, "-------------------------------------");
                Log.d(LOG_TAG, "FaceCompare A face B broken");
                Bitmap selectedImage = BitmapFactory.decodeResource(getResources(), R.drawable.geyou_2, opts);
                Bitmap selectedImage2 = BitmapFactory.decodeResource(getResources(), R.drawable.broken1, opts);
                JSONObject respose = faceYoutu.FaceCompare(selectedImage, selectedImage2);
                // get respose
                Log.d(LOG_TAG, respose.toString());

                Bitmap selectedImage3= BitmapFactory.decodeResource(getResources(), R.drawable.broken2, opts);
                JSONObject ret2 = faceYoutu.FaceCompare(selectedImage, selectedImage3);
                Log.d(LOG_TAG, ret2.toString());

                Bitmap selectedImage4 = BitmapFactory.decodeResource(getResources(), R.drawable.bad, opts);
                JSONObject ret3 = faceYoutu.FaceCompare(selectedImage, selectedImage4);
                Log.d(LOG_TAG, ret3.toString());
                if(null != selectedImage) {
                    selectedImage.recycle();
                }
                if(null != selectedImage2) {
                    selectedImage2.recycle();
                }
                if(null != selectedImage3) {
                    selectedImage3.recycle();
                }
                if(null != selectedImage4) {
                    selectedImage4.recycle();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
                }
            }).start();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            Log.e("uri", uri.toString());
            try {
                String path = getPath(uri);
                theSelectedImage = getBitmap(path, 1000, 1000);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (theSelectedImage != null) {
                            try {
                                Youtu faceYoutu = new Youtu(APP_ID, SECRET_ID, SECRET_KEY,Youtu.API_YOUTU_END_POINT);
                                JSONObject respose = faceYoutu.DetectFace(theSelectedImage, 1);
                                Log.d(LOG_TAG + "xxxxxxxxxxxxx", respose.toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            } catch ( Exception e) {
                Log.e("Exception", e.getMessage(), e);
            }
        }
        if (requestCode == 2){
            rectBitmap = MyCameraActivity.rotaBitmap;
            Log.d("MainActivity",rectBitmap + "");
        }
        if (requestCode == 3){
            bitmap1 = RectPhoto.rotaBitmap;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String uri2Path(Uri uri) {
        String path = null;
        if(uri == null) {
            return path;
        }

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if(!isKitKat) {
            //android 版本小于4.4
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if(null != cursor) {
                cursor.moveToFirst();
                path = cursor.getString(1);
                cursor.close();
            }
        }else{
            //android 版本大于等于4.4
            final String docId = DocumentsContract.getDocumentId(uri);
            final String[] split = docId.split(":");
            final String type = split[0];

            Uri contentUri = null;
            if ("image".equals(type)) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            }

            final String selection = "_id=?";
            final String[] selectionArgs = new String[] { split[1] };

            path =  getDataColumn(this, contentUri, selection, selectionArgs);

        }
        return path;
    }


    public String getPath( final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(this, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(this, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("raw".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(this, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(this, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


    private static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    private Bitmap getBitmap(String path , int maxWidth, int maxHeight){
        //先解析图片边框的大小
        Bitmap bm = null;
        File file = new File(path);
        if(file.exists()) {
            BitmapFactory.Options ops = new BitmapFactory.Options();
            ops.inJustDecodeBounds = true;
            ops.inSampleSize = 1;
            BitmapFactory.decodeFile(path, ops);
            int oHeight = ops.outHeight;
            int oWidth = ops.outWidth;

            //控制压缩比
            int contentHeight = maxWidth;
            int contentWidth = maxHeight;
            if (((float) oHeight / contentHeight) < ((float) oWidth / contentWidth)) {
                ops.inSampleSize = (int) Math.ceil((float) oWidth / contentWidth);
            } else {
                ops.inSampleSize = (int) Math.ceil((float) oHeight / contentHeight);
            }
            ops.inJustDecodeBounds = false;
            bm = BitmapFactory.decodeFile(path, ops);

        }

        return bm;
    }
}



