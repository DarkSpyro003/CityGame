package be.pxl.citygame;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import be.pxl.citygame.data.Question;
import be.pxl.citygame.data.database.GameDB;
import be.pxl.citygame.data.database.GameDbHelper;
import be.pxl.citygame.providers.Providers;


public class GameResultsActivity extends ActionBarActivity {

    private TextView correctAnswersText;
    private int gameId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_results);

        correctAnswersText = (TextView)findViewById(R.id.tv_correct_answers);

        Intent intent = getIntent();
        this.gameId = intent.getIntExtra("gameId", 0);

        ListView view = (ListView)findViewById(R.id.lv_question_response);
        List<Question> questionList = new ArrayList<Question>();
        TreeMap<Integer, Question> questionsTable = Providers.getGameContentProvider().getGameContentById(gameId).getQuestionList();
        for( Map.Entry<Integer, Question> entry : questionsTable.entrySet() ) {
            questionList.add(entry.getValue());
        }
        Question[] questions = questionList.toArray(new Question[questionList.size()]);
        QuestionResponseAdapter adapter = new QuestionResponseAdapter(this, questions);
        view.setAdapter(adapter);

        CityGameApplication app = (CityGameApplication)getApplication();

        //Calculate score
        int score = 0;
        for( Question question : questionList) {
            if( question.isAnsweredCorrect() ) {
                score++;
            }
        }

        correctAnswersText.setText(String.format("%s %d/%d", correctAnswersText.getText().toString(), score, questionList.size()));

        GameDbHelper helper = new GameDbHelper(getApplicationContext());
        SQLiteDatabase sqlDb = helper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(GameDB.Games.COL_COMPLETED, 1);
        contentValues.put(GameDB.Games.COL_SCORE, score);
        String where = GameDB.Games.COL_GID + " = ?";
        String[] whereArgs = { "" + gameId };
        sqlDb.update(GameDB.Games.TABLE_NAME, contentValues, where, whereArgs);

        if( app.isLoggedIn() )
            app.getPlayer().postGames(app.getPassword());

        sqlDb.close();
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

    @Override
    public void onBackPressed() {
        // Do nothing - you can't go back to previous question!
    }

    public void goToMainActivity(View v) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(intent, 0);
    }
}
