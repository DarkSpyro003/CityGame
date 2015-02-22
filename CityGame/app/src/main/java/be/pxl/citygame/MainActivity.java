package be.pxl.citygame;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import be.pxl.citygame.data.GameContent;
import be.pxl.citygame.data.Helpers;
import be.pxl.citygame.data.Player;
import be.pxl.citygame.data.Question;
import be.pxl.citygame.data.database.GameDB;
import be.pxl.citygame.data.database.GameDbHelper;
import be.pxl.citygame.providers.GameContentCaller;
import be.pxl.citygame.providers.Providers;

// todo: Design a logo
// todo: Design an app icon -> http://developer.android.com/design/style/iconography.html
public class MainActivity extends ActionBarActivity implements GameContentCaller {

    private static final int PRIMARY_CONTENT_ID = 1;
    private int scoreId = 0;

    /**
     * Initialize app and load user's data
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Loads the providers that have been declared in settings.xml
        Providers.load(getApplication());

        ((CityGameApplication)getApplicationContext()).setActivity(this);

        GameDbHelper helper = new GameDbHelper(getApplicationContext());
        SQLiteDatabase sqlDb = helper.getReadableDatabase();
        Cursor cur = sqlDb.query(GameDB.Games.TABLE_NAME, null, null, null, null, null, null, null);

        Player offlinePlayer = new Player("!!offline", getApplication());

        cur.moveToFirst();
        while (!cur.isAfterLast()) {
            int id = cur.getInt(cur.getColumnIndex(GameDB.Games.COL_GID));
            boolean completed = cur.getInt(cur.getColumnIndex(GameDB.Games.COL_COMPLETED)) == 1;
            int score = cur.getInt(cur.getColumnIndex(GameDB.Games.COL_SCORE));
            String title = cur.getString(cur.getColumnIndex(GameDB.Games.COL_TITLE));

            GameContent content = new GameContent(title);

            String where = GameDB.Questions.COL_GID + " = " + id;
            Cursor qcur = sqlDb.query(GameDB.Questions.TABLE_NAME, null, where, null, null, null, null, null);
            qcur.moveToFirst();
            while(!qcur.isAfterLast()) {
                Question q = null;
                int qid = qcur.getInt(qcur.getColumnIndex(GameDB.Questions.COL_QID));
                int qType = qcur.getInt(qcur.getColumnIndex(GameDB.Questions.COL_TYPE));
                String question = qcur.getString(qcur.getColumnIndex(GameDB.Questions.COL_QUESTION));
                if( qType == Question.PLAIN_TEXT ) {
                    String textanswer = qcur.getString(qcur.getColumnIndex(GameDB.Questions.COL_TEXT_ANSWER));
                    q = new Question(qType, question, textanswer);
                } else if( qType == Question.MULTIPLE_CHOICE ) {
                    int multianswer = qcur.getInt(qcur.getColumnIndex(GameDB.Questions.COL_MULTI_ANSWER));
                    String mcwhere = GameDB.QuestionMultiAnswer.COL_QID + " = ? AND " +
                            GameDB.QuestionMultiAnswer.COL_GID + " = ?";
                    String[] mcWhereArgs = { "" + qid, "" + id };
                    String mcorder = GameDB.QuestionMultiAnswer.COL_CID + " ASC";
                    Cursor mcCur = sqlDb.query(GameDB.QuestionMultiAnswer.TABLE_NAME, null, mcwhere, mcWhereArgs, null, null, mcorder, null);
                    mcCur.moveToFirst();
                    ArrayList<String> choices = new ArrayList<>();
                    while(!mcCur.isAfterLast()) {
                        choices.add(mcCur.getString(mcCur.getColumnIndex(GameDB.QuestionMultiAnswer.COL_ANSWER)));
                        mcCur.moveToNext();
                    }
                    q = new Question(qType, question, multianswer, choices);
                }
                if( q != null ) {
                    q.setApplication(getApplication());
                    q.setqId(qid);
                    q.setGameId(id);
                    q.setPlacename(qcur.getString(qcur.getColumnIndex(GameDB.Questions.COL_PLACENAME)));
                    q.setExtraInfo(qcur.getString(qcur.getColumnIndex(GameDB.Questions.COL_EXTRAINFO)));
                    q.setLocalContentUri(Uri.parse(qcur.getString(qcur.getColumnIndex(GameDB.Questions.COL_LOCALURL))));
                    q.setContentType(qcur.getInt(qcur.getColumnIndex(GameDB.Questions.COL_CONTENT_TYPE)));
                    Location loc = new Location("");
                    loc.setLatitude(qcur.getDouble(qcur.getColumnIndex(GameDB.Questions.COL_LATITUDE)));
                    loc.setLongitude(qcur.getDouble(qcur.getColumnIndex(GameDB.Questions.COL_LONGITUDE)));
                    q.setLocation(loc);
                    String localPhotoUri = qcur.getString(qcur.getColumnIndex(GameDB.Questions.COL_LOCALPHOTO));
                    if( localPhotoUri != null )
                        q.setLocalPhotoUri(Uri.parse(localPhotoUri));

                    boolean answered = qcur.getInt(qcur.getColumnIndex(GameDB.Questions.COL_ANSWERED)) == 1;
                    q.setAnswered(answered);
                    if( answered ) {
                        q.setAnsweredCorrect(qcur.getInt(qcur.getColumnIndex(GameDB.Questions.COL_ANSWERED_CORRECT)) == 1);
                        if (q.getType() == Question.PLAIN_TEXT) {
                            q.setUserTextInput(qcur.getString(qcur.getColumnIndex(GameDB.Questions.COL_ANSWERED_CONTENT)));
                        } else if (q.getType() == Question.MULTIPLE_CHOICE) {
                            q.setUserMultiInput(Integer.parseInt(qcur.getString(qcur.getColumnIndex(GameDB.Questions.COL_ANSWERED_CONTENT))));
                        }
                    }

                    content.addQuestion(q);
                }

                qcur.moveToNext();
            }
            content.setId(id);
            content.setCompleted(completed);
            content.setScore(score);

            offlinePlayer.getGames().add(content);
            cur.moveToNext();
        }
        cur.close();

        ((CityGameApplication)getApplication()).setPlayer(offlinePlayer);

        //Set text for loginbutton to logout
        CityGameApplication app = (CityGameApplication)getApplication();
        if(app.isLoggedIn()) {
            Button btn_login = (Button) findViewById(R.id.btn_go_to_log_in);
            btn_login.setText(getString(R.string.logout));
        }
    }

    public void logout()
    {
        CityGameApplication app = (CityGameApplication)getApplication();
        app.setLoggedIn(false);
        Button btn_login = (Button) findViewById(R.id.btn_go_to_log_in);
        btn_login.setText(getString(R.string.action_sign_in));
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((CityGameApplication)getApplicationContext()).setActivity(this);
    }

    public void handleBtnStart(View v) {
        startGame(PRIMARY_CONTENT_ID);
    }

    public void startGame(int id) {
        // Download data
        Providers.getGameContentProvider().initGameContentById(id, this);
    }

    /**
     * Called when initial game caching has completed
     * Should have all data locally now
     * @param id    The game's id
     */
    @Override
    public void startGameCallback(int id) {
        // Check if game is completed
        if( isGameCompleted(id) ) {
            scoreId = id;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder
                    .setTitle(getString(R.string.game_finished_title))
                    .setMessage(getString(R.string.game_finished_content))
                    .setCancelable(true)
                    .setPositiveButton(getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setNeutralButton(R.string.btn_show_score, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getApplicationContext(), GameResultsActivity.class);
                            intent.putExtra("gameId", scoreId);
                            startActivity(intent);
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
        }
        else {
            // Check if game was interrupted, and resume to the next available question, 0 if new game
            CityGameApplication context = (CityGameApplication) getApplicationContext();
            Intent intent = new Intent(context, NextLocationActivity.class);
            intent.putExtra("gameId", id);
            intent.putExtra("questionId", getStartQuestionId(id));
            startActivity(intent);
        }
    }

    /**
     * Finds out of the player was already playing this game, and finds out which question
     * they haven't answered yet.
     * @param gid   Game's id
     * @return      Question to start with id
     */
    private int getStartQuestionId(int gid) {
        GameDbHelper helper = new GameDbHelper(getApplicationContext());
        SQLiteDatabase sqlDb = helper.getReadableDatabase();

        String where = GameDB.Questions.COL_GID + " = ?";
        String whereArgs[] = { "" + gid };
        String order = GameDB.Questions.COL_QID + " ASC";
        Cursor cur = sqlDb.query(GameDB.Questions.TABLE_NAME, null, where, whereArgs, null, null, order, null);

        if( cur.getCount() < 1 ) {
            cur.close();
            return 0;
        }

        // Find out first qid
        int qid = Helpers.getNextQid(gid, -1);
        cur.moveToFirst();
        while(!cur.isAfterLast()) {
            for(int i = 0; i < cur.getColumnCount(); i++) {
                if( cur.getColumnName(i).equals(GameDB.Questions.COL_ANSWERED) ) {
                    if( cur.getInt(i) == 0 ) {
                        return qid;
                    } else { // Question possibly answered, load previous answer from DB.
                        Question current = Providers.getQuestionProvider().loadQuestionById(gid, qid);
                        current.setAnswered(cur.getInt(cur.getColumnIndex(GameDB.Questions.COL_ANSWERED)) == 1);
                        current.setAnsweredCorrect(cur.getInt(cur.getColumnIndex(GameDB.Questions.COL_ANSWERED_CORRECT)) == 1);
                        if( current.getType() == Question.PLAIN_TEXT ) {
                            current.setUserTextInput(cur.getString(cur.getColumnIndex(GameDB.Questions.COL_ANSWERED_CONTENT)));
                        } else if( current.getType() == Question.MULTIPLE_CHOICE ) {
                            current.setUserMultiInput(Integer.parseInt(cur.getString(cur.getColumnIndex(GameDB.Questions.COL_ANSWERED_CONTENT))));
                        }
                    }
                }
            }
            qid = Helpers.getNextQid(gid, -1);
            cur.moveToNext();
        }
        cur.close();

        return qid;
    }

    /**
     * Checks if a game is completed
     * @param id    The game's id
     * @return      Is the game completed
     */
    private boolean isGameCompleted(int id) {
        GameDbHelper helper = new GameDbHelper(getApplicationContext());
        SQLiteDatabase sqlDb = helper.getReadableDatabase();

        String where = GameDB.Games.COL_GID + " = ? AND " + GameDB.Games.COL_COMPLETED + " = ?";
        String whereArgs[] = { "" + id, "1" };
        Cursor cur = sqlDb.query(GameDB.Games.TABLE_NAME, null, where, whereArgs, null, null, null, null);

        boolean toReturn = cur.getCount() > 0;
        cur.close();

        return toReturn;
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

    public void scanQR(View v) {
        IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
        integrator.addExtra("SCAN_WIDTH", 640);
        integrator.addExtra("SCAN_HEIGHT", 480);
        integrator.addExtra("SCAN_MODE", "QR_CODE_MODE");
        //customize
        //integrator.addExtra("PROMPT_MESSAGE", "SCANNER_START");
        integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if(result != null) {
            // QR code should contain application name + gamecontent  in JSON
            String contents = result.getContents();
            if(contents != null) {
                try {
                    JSONObject object = new JSONObject(contents);
                    String app = object.getString("appname");
                    if( app.equals("be.pxl.citygame") ) {
                        int gameContentId = object.getInt("gameContentId");
                        startGame(gameContentId);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), R.string.QR_invalid, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), R.string.QR_invalid, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void goToLogin(View v) {

        CityGameApplication app = (CityGameApplication)getApplication();
        if(app.isLoggedIn()) {
            logout();
        }
        else{
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
    }
}

