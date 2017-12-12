package com.huyn.demogroup.video;

import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Handler;
import android.util.Log;
import android.view.TextureView;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by huyaonan on 17/2/8.
 */
public class CameraTextureInstance {

    private static final String TAG = "CameraTextureInstance";
    private Camera mCamera;
    private Camera.Parameters mParams;
    private boolean isPreviewing = false;
    public static final int MIN_PREVIEW_SIZE = 200;

    private Handler mHandler = new Handler();

    private TextureView mTextureView;

    private int mDefaultCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private int mScreenWidth = 800;

    private boolean mTextureDestroyed = false;

    private AtomicBoolean mCameraAvailable = new AtomicBoolean(false);

    /**
     * {@link TextureView.SurfaceTextureListener} handles several lifecycle events on a
     * {@link TextureView}.
     */
    private final TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
            System.out.println("----------------------onSurfaceTextureAvailable");
            openCamera(mDefaultCameraId);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
            System.out.println("----------------------onSurfaceTextureDestroyed");
            mTextureDestroyed = true;
            stopCamera();
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture texture) {
        }

    };

    public void onCreate() {
    }

    public void onResume() {
        // When the screen is turned off and turned back on, the SurfaceTexture is already
        // available, and "onSurfaceTextureAvailable" will not be called. In that case, we can open
        // a camera and start preview from here (otherwise, we wait until the surface is ready in
        // the SurfaceTextureListener).
        mTextureDestroyed = false;
        if (mTextureView.isAvailable()) {
            openCamera(mDefaultCameraId);
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }

    public void onPause() {
        stopCamera();
    }

    public void onDestroy() {
    }

    public void switchPreviewSize() {
        try {
            if(mCamera == null)
                return;
            mParams = mCamera.getParameters();
            mParams.setPictureFormat(PixelFormat.JPEG);//设置拍照后存储的图片格式

            CamParaUtil.getInstance().printSupportPictureSize(mParams);
            boolean support11 = true;
            //设置PreviewSize和PictureSize
            float previewRate = 1f;

            if (previewRate == 1f && !support11)
                previewRate = 4f / 3;

            Camera.Size pictureSize = CamParaUtil.getInstance().getPropPictureSize(mParams.getSupportedPictureSizes(), previewRate, MIN_PREVIEW_SIZE, mScreenWidth);
            System.out.println("-----switch picture size:" + previewRate + " picture w:" + pictureSize.width + "/" + pictureSize.height);
            mParams.setPictureSize(pictureSize.width, pictureSize.height);

            Camera.Size previewSize = CamParaUtil.getInstance().getPropPreviewSize(mParams.getSupportedPreviewSizes(), previewRate, MIN_PREVIEW_SIZE);
            System.out.println("-----switch preview size:" + previewRate + " preview w:" + previewSize.width + "/" + previewSize.height);
            mParams.setPreviewSize(previewSize.width, previewSize.height);

            mCamera.setDisplayOrientation(90);

            //CamParaUtil.getInstance().printSupportFocusMode(mParams);
            List<String> focusModes = mParams.getSupportedFocusModes();
            if (focusModes.contains("continuous-video")) {
                mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }
            mCamera.setParameters(mParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CameraTextureInstance(TextureView textureView) {
        this.mTextureView = textureView;
    }

    public void openCamera(int cameraId) {
        try {
            if(mTextureDestroyed)
                return;

            /*mTextureView.getSurfaceTexture().setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                @Override
                public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                    System.out.println("++++++++onFrameAvailable");
                    //surfaceTexture.updateTexImage();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mTextureView.getSurfaceTexture().updateTexImage();
                        }
                    });
                }
            });*/

            Log.i(TAG, "Camera open...." + cameraId);
            mCamera = Camera.open(cameraId);

            /*if(StorageUtil.SDK_INT >= 23)
                mPresenter.showCameraPermissionLayout();*/

            startPreview();
            Log.i(TAG, "Camera open over....");
        } catch (Exception e) {
            e.printStackTrace();

            //授权被拒绝
            //if(e instanceof SecurityException || e instanceof RuntimeException) {
            //    Utils.showToast(getActivity(), "继续使用需要开启相机和访问本地相册权限, 请至<设置>/<应用程序>开启相关权限");
            //}

            stopCamera();
        }
    }

    public void startPreview() {
        Log.i(TAG, "doStartPreview...");
        if (isPreviewing) {
            mCamera.stopPreview();
            return;
        }
        if (mCamera != null) {
            try {
                switchPreviewSize();
                mCamera.setPreviewTexture(mTextureView.getSurfaceTexture());
                mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                    @Override
                    public void onPreviewFrame(byte[] data, Camera camera) {
                        /**
                         * 有些6.0的手机，比如vivo，通过checkpermission的方法来判断是否获取到权限并不可用
                         * 原因可能是checkPermission只能用于IPC方法中调用
                         * 5.0的手机反而可以通过catch异常的方式来判断
                         * 所以，对于这些6.0的手机，可以通过判断是否能获取到实时的preview数据来判断相机是否打开
                         */
                        /*mPresenter.hideCameraPermissionLayout();
                        mCamera.setPreviewCallback(null);*/
                        mCameraAvailable.set(true);
                        mCamera.setPreviewCallback(null);
                    }
                });
                mCamera.startPreview();//开启预览
            } catch (Exception e) {
                e.printStackTrace();
            }

            isPreviewing = true;
        }
    }

    /**
     * 停止预览，释放Camera
     */
    public void stopCamera() {
        if (null != mCamera) {
            try {
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
                isPreviewing = false;
                mCamera.release();
                mCamera = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
