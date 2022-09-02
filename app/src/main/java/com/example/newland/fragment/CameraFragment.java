package com.example.newland.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.newland.base.BaseFragment;
import com.example.newland.databinding.FragmentCameraBinding;
import com.example.newland.utils.XToastUtils;
import com.newland.CameraManager;
import com.newland.PTZ;
import com.tencent.mmkv.MMKV;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.utils.TitleBar;
import com.xuexiang.xui.XUI;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.popupwindow.status.Status;

import java.io.File;

@Page(name = "Camera")
public class CameraFragment extends BaseFragment {
    private static final String TAG = "CameraFragment";
    FragmentCameraBinding binding;
    private CameraManager cameraManager;
    private final Handler handler = new Handler();
    private MMKV kv;
    private int faceNum = 0;

    @Override
    protected View inflateView(LayoutInflater inflater, ViewGroup container) {
        binding = FragmentCameraBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    protected TitleBar initTitleBar() {
        return null;
    }

    @SuppressLint({"SetTextI18n"})
    @Override
    protected void initViews() {
        kv = MMKV.mmkvWithID("InetInfo", MMKV.MULTI_PROCESS_MODE);

        binding.cameraIp.setText("ip地址: " + kv.decodeString("ipaddress_camera", "请在设置中配置连接参数"));
        binding.cameraProt.setText("通道号: " + kv.decodeString("channel_camera", "11"));

        binding.btCameraconnection.setOnClickListener(view -> {
            binding.status.setStatus(Status.LOADING);
            cameraManager = CameraManager.getInstance();
            cameraManager.setBaseInfo(binding.playerView,
                            kv.decodeString("username_camera", "admin"),
                            kv.decodeString("password_camera", "admin"),
                            kv.decodeString("ipaddress_camera", "192.168.0.1"),
                            kv.decodeString("channel_camera", "11"))
                    .videoSetting(XUI.getContext());
            handler.postDelayed(() -> {
                try {
                    cameraManager.openCamera();
                } catch (Exception e) {
                    e.printStackTrace();
                    XToastUtils.error(e);
                    binding.status.setStatus(Status.ERROR);
                }
            }, 2000);
        });
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initListeners() {
        binding.status.setOnCompleteClickListener(v -> binding.status.dismiss());
        binding.status.setOnErrorClickListener(v -> binding.status.dismiss());
        binding.status.setOnLoadingClickListener(v -> binding.status.dismiss());
        binding.btCapture.setOnClickListener(view -> {
            try {
                String fileData = requireContext().getExternalCacheDir().getAbsolutePath();
                String fileName = System.currentTimeMillis() + ".png";
                cameraManager.capture(fileData, fileName);
                XToastUtils.info("截图生成中请等待!");
                handler.postDelayed(() -> {

                    ImageView imageView = new ImageView(getContext());

                    imageView.setImageBitmap(drawFace(fileData + "/" + fileName));

//                    if (XUI.isTablet()) {
//                        imageView.setImageBitmap(BitmapFactory.decodeFile(fileData + "/" + fileName));
//                    } else {
//                        imageView.setImageBitmap(drawFace("/storage/emulated/0/Android/data/com.muxmu.newland/cache/1659439578272.png"));
////                    imageView.setImageBitmap(drawFace(fileData + "/" + fileName));
//                    }

                    MaterialDialog dialog = new MaterialDialog.Builder(requireContext())
                            .customView(imageView, true)
                            .title("截图结果(人脸数量:" + faceNum + ")")
                            .positiveText("删除图片")
                            .onPositive((dialog1, which) -> {
                                dialog1.dismiss();
                                File file = new File(fileData + "/" + fileName);
                                if (file.delete()) {
                                    XToastUtils.success("图片已删除");
                                } else {
                                    XToastUtils.error("删除失败");
                                }
                            })
                            .build();
                    dialog.setCancelable(false);
                    dialog.show();
                }, 2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        binding.btUp.setOnTouchListener((v1, event) -> {
            try {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    cameraManager.controlDir(PTZ.Up);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    cameraManager.controlDir(PTZ.Stop);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        });
        binding.btLeft.setOnTouchListener((v1, event) -> {
            try {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    cameraManager.controlDir(PTZ.Left);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    cameraManager.controlDir(PTZ.Stop);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        });
        binding.btRight.setOnTouchListener((v1, event) -> {
            try {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    cameraManager.controlDir(PTZ.Right);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    cameraManager.controlDir(PTZ.Stop);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        });
        binding.btDown.setOnTouchListener((v1, event) -> {
            try {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    cameraManager.controlDir(PTZ.Down);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    cameraManager.controlDir(PTZ.Stop);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (cameraManager != null) {
            cameraManager.releaseCamera();
        }
    }

    /**
     * 调用Android自带人脸识别库
     * 实现人脸数量及绘制人脸识别框
     *
     * @param pathName 需识别图片路径
     * @return 绘制完成后的Bitmap
     */
    private Bitmap drawFace(String pathName) {
        int maxFaces = 10;
        BitmapFactory.Options BitmapFactoryOptionsbfo = new BitmapFactory.Options();
        BitmapFactoryOptionsbfo.inMutable = true;
        BitmapFactoryOptionsbfo.inPreferredConfig = Bitmap.Config.RGB_565;    //构造位图生成的参数，必须为565。类名+enum
        Bitmap myBitmap = BitmapFactory.decodeFile(pathName, BitmapFactoryOptionsbfo);
        int imageWidth = myBitmap.getWidth();
        int imageHeight = myBitmap.getHeight();
        FaceDetector.Face[] myFace = new FaceDetector.Face[maxFaces];        //分配人脸数组空间
        FaceDetector myFaceDetect = new FaceDetector(imageWidth, imageHeight, maxFaces);
        int numberOfFaceDetected = myFaceDetect.findFaces(myBitmap, myFace);    //FaceDetector 构造实例并解析人脸
        faceNum = numberOfFaceDetected;
        XToastUtils.info("识别到" + numberOfFaceDetected + "张人脸信息");
        Canvas canvas = new Canvas(myBitmap);
        Paint myPaint = new Paint();
        myPaint.setColor(Color.GREEN);
        myPaint.setAntiAlias(false);
        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeWidth(5);            //设置位图上paint操作的参数

        Paint textPaint = new Paint();
        textPaint.setColor(Color.GREEN);
        textPaint.setTextSize(50);
        textPaint.setStyle(Paint.Style.FILL);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        canvas.drawText("仅供测试", 30, Math.abs(fontMetrics.top), textPaint);//文字完全显示

        for (int i = 0; i < numberOfFaceDetected; i++) {
            FaceDetector.Face face = myFace[i];
            PointF myMidPoint = new PointF();
            face.getMidPoint(myMidPoint);
            float myEyesDistance = face.eyesDistance() + 20;    //得到人脸中心点和眼间距离参数，并对每个人脸进行画框
            Log.i(TAG, "onDraw: " + myEyesDistance);
            canvas.drawRect(            //矩形框的位置参数
                    (int) (myMidPoint.x - myEyesDistance),
                    (int) (myMidPoint.y - myEyesDistance),
                    (int) (myMidPoint.x + myEyesDistance),
                    (int) (myMidPoint.y + myEyesDistance),
                    myPaint);
        }
        return myBitmap;
    }
}