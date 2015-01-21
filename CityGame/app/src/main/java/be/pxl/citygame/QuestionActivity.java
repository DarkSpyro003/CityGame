package be.pxl.citygame;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class QuestionActivity extends ActionBarActivity {

    private int currGame;
    private int currQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        Intent intent = getIntent();
        currGame = intent.getIntExtra("gameId", 0);
        currQuestion = intent.getIntExtra("questionId", 0);

        // Pass data to fragments
        ImageFragment imgFrag = (ImageFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_image);
        imgFrag.setData(currGame, currQuestion);

        QuestionFragment questFrag = (QuestionFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_question);
        questFrag.setData(currGame, currQuestion);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_question, menu);
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
}
