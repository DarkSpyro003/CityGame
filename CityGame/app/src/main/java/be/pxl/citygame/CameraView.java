package be.pxl.citygame;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.IOException;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder holdMe;
    private Camera theCamera;

    public CameraView(Context context, Camera camera) {
        super(context);
        theCamera = camera;
        holdMe = getHolder();
        holdMe.addCallback(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            theCamera.setPreviewDisplay(holder);
            theCamera.startPreview();
            WindowManager windowManager = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
            Point size = new Point();
            windowManager.getDefaultDisplay().getSize(size);
            float aspectRatio = size.x / size.y;

            switch (getResources().getConfiguration().orientation) {
                case Configuration.ORIENTATION_LANDSCAPE:
                    holder.setFixedSize((int)((float)size.x * (0.9) * aspectRatio), size.y);
                    break;
                case Configuration.ORIENTATION_PORTRAIT:
                    holder.setFixedSize(size.x, (int) ((float) size.y / (0.9) * aspectRatio));
                    break;
            }


        } catch (IOException e) {
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
    }
}
