package be.pxl.citygame;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import be.pxl.citygame.providers.Providers;


public class CameraActivity extends ActionBarActivity {

    private Camera camera;
    private CameraView cameraView;
    private ImageView imageView;

    private int gameId;
    private int questionId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        camera = Camera.open();
        cameraView = new CameraView(this, camera);
        imageView = (ImageView)findViewById(R.id.image_view);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(cameraView);

        gameId = this.getIntent().getIntExtra("gameid", 0);
        questionId = this.getIntent().getIntExtra("questionid", 0);
    }

    private Camera.PictureCallback callback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            imageView.setImageBitmap(bitmap);
            camera.stopPreview();
            camera.startPreview();
            Providers.getQuestionProvider().loadQuestionById(gameId, questionId).savePhoto(bitmap);
        }
    };

    public void onClick(View view) {
        camera.takePicture(null, null, callback);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void goBack(View v)
    {
        finish();
    }
}
