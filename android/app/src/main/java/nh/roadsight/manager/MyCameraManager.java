package nh.roadsight.manager;

import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceView;
import android.view.ViewTreeObserver;

import java.io.IOException;

public class MyCameraManager {
    private MyCameraManager() {
    }

    private static final String TAG = "-tag-mCAMm";
    private static Camera camera; /* Camera2 가 필요해진다면 그때 변경 */

    public static void open(final SurfaceView mSurfaceView) {
        mSurfaceView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (camera == null)
                    camera = Camera.open();
                try {
                    camera.setPreviewDisplay(mSurfaceView.getHolder());
                    camera.startPreview();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "error : " + e.getMessage());
                }
            }
        });
    }

    public static void close(){
        if(camera != null){
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }
}
