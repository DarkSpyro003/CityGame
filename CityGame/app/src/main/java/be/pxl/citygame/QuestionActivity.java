package be.pxl.citygame;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import be.pxl.citygame.data.Question;
import be.pxl.citygame.providers.Providers;


public class QuestionActivity extends ActionBarActivity implements QuestionFragment.OnFragmentInteractionListener {

    private int currGame;
    private int currQuestion;
    private Question question;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        ((CityGameApplication)getApplicationContext()).setActivity(this);

        Intent intent = getIntent();
        currGame = intent.getIntExtra("gameId", 0);
        currQuestion = intent.getIntExtra("questionId", 0);

        // Pass data to fragments
        ImageFragment imgFrag = (ImageFragment) getSupportFragmentManager().findFragmentById(R.id.imageFragment);
        imgFrag.setData(currGame, currQuestion);

        QuestionFragment questFrag = (QuestionFragment) getSupportFragmentManager().findFragmentById(R.id.questionFragment);
        questFrag.setData(currGame, currQuestion);

        this.question = Providers.getQuestionProvider().loadQuestionById(currGame, currQuestion);
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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void handleAnswer(View v) {
        QuestionFragment fragment = (QuestionFragment) getSupportFragmentManager().findFragmentById(R.id.questionFragment);
        fragment.handleAnswer(v);
    }

    public void showMoreInfo(View v) {
        QuestionFragment fragment = (QuestionFragment) getSupportFragmentManager().findFragmentById(R.id.questionFragment);
        fragment.showMoreInfo(v);
    }
}
