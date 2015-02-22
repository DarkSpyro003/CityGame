package be.pxl.citygame;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import be.pxl.citygame.R;
import be.pxl.citygame.data.Question;

public class PhotoViewActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        Intent intent = getIntent();
        Uri uri = (Uri) intent.getParcelableExtra("Bitmap");

        ImageView image = (ImageView) findViewById(R.id.iv_photo);
        image.setImageURI(uri);
    }
}
