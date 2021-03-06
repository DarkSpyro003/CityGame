package be.pxl.citygame;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class InfoActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        ((CityGameApplication)getApplicationContext()).setActivity(this);

        Intent intent = getIntent();
        InfoFragment frag = (InfoFragment) getSupportFragmentManager().findFragmentById(R.id.infoFragment);
        ImageFragment imgFrag = (ImageFragment) getSupportFragmentManager().findFragmentById(R.id.imageFragment);
        frag.setData(intent.getIntExtra("gameId", 0), intent.getIntExtra("questionId", 0));
        imgFrag.setData(intent.getIntExtra("gameId", 0), intent.getIntExtra("questionId", 0));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
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

    public void handleBackToQuestion(View v) {
        InfoFragment frag = (InfoFragment) getSupportFragmentManager().findFragmentById(R.id.infoFragment);
        frag.handleBackToQuestion(v);
    }

    @Override
    protected void onResume() {
        ((CityGameApplication)getApplicationContext()).setActivity(this);
        super.onResume();
    }
}
