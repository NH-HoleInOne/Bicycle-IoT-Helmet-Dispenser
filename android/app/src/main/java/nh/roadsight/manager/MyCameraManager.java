package nh.roadsight.manager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.IOException;

import nh.roadsight.MainActivity;

public class MyCameraManager {
    private MyCameraManager() {
    }

    private static class CameraSurfaceHolderCallback implements SurfaceHolder.Callback2 {
        static class LazyHolder {
            static final CameraSurfaceHolderCallback instance = new CameraSurfaceHolderCallback();
        }

        static CameraSurfaceHolderCallback getInstance() {
            return LazyHolder.instance;
        }

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            Log.d(TAG, "suface created");
            if (cameraEnable) {
                MyCameraManager.startPreview(surfaceHolder);
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
            Log.d(TAG, "suface changed");
        }

        @Override
        public void surfaceRedrawNeeded(SurfaceHolder surfaceHolder) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            Log.d(TAG, "suface destory");
        }

        /*
        @Override
        public void onPointerCaptureChanged(boolean hasCapture) {
        }
        @Override
        public void surfaceRedrawNeededAsync(SurfaceHolder holder, Runnable drawingFinished) {
        }
        */
    }

    public static SurfaceHolder.Callback2 getCameraSurfaceHolderCallback() {
        return CameraSurfaceHolderCallback.getInstance();
    }

    private static final String TAG = "-tag-mCAMm";
    private static Camera mCamera; /* Camera2 가 필요해진다면 그때 변경 */
    private static boolean cameraEnable;
    private static WindowManager mWindowManager;

    /**
     * 호출한 activity 의 requestPermissionResult 에서
     * PERMISSION_GRANTED 받았을 때
     * 다시 해당 메서드를 호출해야 함.
     */
    public static void open(Context mContext, final SurfaceView mSurfaceView) {
        final String permissionNameOfCamera = Manifest.permission.CAMERA;
        int cameraPermissionState = ContextCompat.checkSelfPermission(mContext, permissionNameOfCamera);
        if (cameraPermissionState == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{permissionNameOfCamera}, MainActivity.REQUEST_CODE_CAMERA);
        } else {
            cameraEnable = true;
            mWindowManager = ((Activity) mContext).getWindowManager();

            SurfaceHolder mSurfaceHolder = mSurfaceView.getHolder();
            if (!mSurfaceHolder.isCreating()) {
                startPreview(mSurfaceHolder);
            }
        }
    }

    private static void startPreview(SurfaceHolder mSurfaceHolder) {
        if (mWindowManager == null) {
            Log.d(TAG, "mWindowManager == null");
            return;
        }
        if (mCamera == null) {
            mCamera = Camera.open(0);
            // todo: https://developer.android.com/reference/android/hardware/Camera.CameraInfo.html#orientation
            Camera.CameraInfo mCameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(0, mCameraInfo);

            int deviceRotation = mWindowManager.getDefaultDisplay().getRotation() * 90;
            int cameraRotation = mCameraInfo.orientation;

            int rotationOffset = (360 + cameraRotation - deviceRotation) % 360;
            //Log.d(TAG, "회전 : " + mCameraInfo.orientation);
            mCamera.setDisplayOrientation(rotationOffset);

        }
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "error : " + e.getMessage());
            close();
        }
    }

    public static void close() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }
}
