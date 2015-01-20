package be.pxl.citygame;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;

public class NextLocationActivity extends ActionBarActivity
        implements NextLocationFragment.OnFragmentInteractionListener {

    private int currGame;
    private int currQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_location);

        Intent intent = getIntent();
        currGame = intent.getIntExtra("gameId", 0);
        currQuestion = intent.getIntExtra("questionId", 0);

        NextLocationFragment loc = (NextLocationFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_next_location);
        loc.setData(currGame, currQuestion);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_next_location, menu);
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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
