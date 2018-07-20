package nh.roadsight.manager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import nh.roadsight.MainActivity;

public class MyCameraManager {
    private MyCameraManager() {
    }

    private static final String TAG = "-tag-mCAMm";

    private static Camera mCamera; /* Camera2 가 필요해진다면 그때 변경 */
    private static MediaRecorder mMediaRecorder;
    private static boolean cameraEnable;
    private static WindowManager mWindowManager;

    private static final String PATH = "RoadSight/temporaryVideo";
    private static String getDirectoryPath(){
        String dir = Environment.getExternalStorageDirectory().getPath();
        File file = new File(dir, PATH);
        dir = file.getAbsolutePath();
        if(!file.exists()){
            Boolean resultOfMkdirs = file.mkdirs();
            Log.d(TAG, "file creation : " + resultOfMkdirs + " : " + dir);
        }
        return dir;
    }
    private static String getTimeStampFilePath(){
        File file = new File(getDirectoryPath(), Calendar.getInstance().getTimeInMillis() +".mp4");
        Log.d(TAG, "file ::: "+file.getAbsolutePath());
        return file.getAbsolutePath();
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
            Log.d(TAG, "suface redrawneeded");
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

    /**
     * 호출한 activity 의 requestPermissionResult 에서
     * PERMISSION_GRANTED 받았을 때
     * 다시 해당 메서드를 호출해야 함.
     */
    public static void open(Context mContext, final SurfaceView mSurfaceView) {
        int cameraPermissionState = ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA);
        if (cameraPermissionState == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.CAMERA}, MainActivity.REQUEST_CODE_CAMERA);
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
            Camera.CameraInfo mCameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(0, mCameraInfo);
            int cameraRotation = mCameraInfo.orientation;
            int deviceRotation = mWindowManager.getDefaultDisplay().getRotation() * 90;
            int rotationOffset = (360 - deviceRotation + cameraRotation) % 360;
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

    public static void startRecord(final Activity activity, final SurfaceHolder mSurfaceHolder){
        if(mCamera == null){
            Toast.makeText(activity, "camera allocation failed", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "mCamera 가 null 인데 startRecord 를 호출함.");
            return;
        }
        if(mMediaRecorder != null){
            Toast.makeText(activity, "still recording...", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "녹화중에 startRecord 함수룰 호출함");
            return;
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // todo : 만약 촬영했을때 화면이 회전된다면 보정해줘야 함 : https://developer.android.com/reference/android/hardware/Camera.CameraInfo.html#orientation
                // -> 된다. 보정해야 함.
                mMediaRecorder = new MediaRecorder();
                mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
                mMediaRecorder.setMaxDuration(1000);
                mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
                mMediaRecorder.setOutputFile(getTimeStampFilePath());
                mMediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
                    @Override
                    public void onInfo(MediaRecorder mediaRecorder, int what, int extra) {
                        if(what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED){
                            mMediaRecorder.release();
                            mMediaRecorder = null;
                            try {
                                // TODO : 다시 camera 의 상이 surfaceview에 맺혀야 함.
                                mCamera.setPreviewDisplay(mSurfaceHolder);
                                mCamera.startPreview();
                                // -------------------
                            } catch (IOException e) {
                                Log.e(TAG, e.getMessage());
                            }
                            ((MainActivity)activity).mButton.setClickable(true);
                            // TODO : 이걸 무한하게 반복시키면서 동시에 최근 5초간의 동영상만 저장해야 함.
                            ((MainActivity)activity).mButton.setText("촬영 다시 가능");
                        }
                    }
                });
                try {
                    mMediaRecorder.prepare();
                    mMediaRecorder.start();
                    ((MainActivity)activity).mButton.setClickable(false);
                    ((MainActivity)activity).mButton.setText("촬영중...");
                } catch(IllegalStateException | IOException e){
                    Toast.makeText(activity, "can't start recorder", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "mediaRecorder prepare error : " + e.getMessage());
                    mMediaRecorder.release();
                    mMediaRecorder = null;
                }
            }
        });
    }

    public static void close() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }
}
