package be.pxl.citygame;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import be.pxl.citygame.data.Question;
import be.pxl.citygame.providers.Providers;


public class GameResultsActivity extends ActionBarActivity {

    private TextView correctAnswersText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_results);

        correctAnswersText = (TextView)findViewById(R.id.tv_correct_answers);

        ListView view = (ListView)findViewById(R.id.lv_question_response);
        // todo: Get correct list of answers from question activity
        Question[] questions = new Question[] { Providers.getQuestionProvider().loadQuestionById(1, 1) };
        QuestionResponseAdapter adapter = new QuestionResponseAdapter(this, questions);
        view.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_results, menu);
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
