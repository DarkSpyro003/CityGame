package be.pxl.citygame.data;

import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import be.pxl.citygame.CityGameApplication;
import be.pxl.citygame.R;
import be.pxl.citygame.data.database.GameDB;
import be.pxl.citygame.data.database.GameDbHelper;
import be.pxl.citygame.providers.GameContentCaller;
import be.pxl.citygame.providers.Providers;

/**
 * Created by Christina on 21/01/2015.
 */
public class Player {

    private int id;
    private String username;
    private String email;
    private String realname;
    private ArrayList<GameContent> games;
    private CityGameApplication application;

    private String dialogTitle;
    private String dialogContent;

    private static final int JOB_LOGIN = 0, JOB_REGISTER = 1, JOB_UPDATE = 2, JOB_POST_GAME = 3;
    private int job = 0;

    /**
     * Initializes player of this application
     * @param username      Username, if any
     * @param application   application creating this instance
     */
    public Player(String username, Application application) {
        this.username = username;
        this.application = (CityGameApplication)application;
        this.games = new ArrayList<GameContent>();
    }

    /**
     * Try to post games to online account
     * @param password  User's password
     * @see #tryPostGames(String)
     */
    public void postGames(String password) {
        job = this.JOB_POST_GAME;
        AsyncTask postgames = new GetRestData().execute(password);
    }

    /**
     * Try to register to online service
     * @param password  User's password
     * @return          Success or failure
     */
    public boolean register(String password) {
        this.dialogTitle = application.getString(R.string.registering_dialog_title);
        this.dialogContent = application.getString(R.string.registering_dialog_content);
        job = this.JOB_REGISTER;
        AsyncTask register = new GetRestData().execute(password);
        try {
            // If not true, let it go to the bottom to show the AlertDialog
            if( ((Boolean) register.get()) )
                return true;
            else
                Log.d(Player.class.toString(), "Registration failed without error");
        } catch (InterruptedException e) {
            Log.e(Player.class.toString(), e.getMessage(), e);
        } catch (ExecutionException e) {
            Log.e(Player.class.toString(), e.getMessage(), e);
        }
        if(Helpers.isConnectedToInternet(application)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(application.getActivity());
            builder.setTitle(application.getString(R.string.register_fail_title))
                    .setMessage(application.getString(R.string.register_fail_content))
                    .setCancelable(true)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
            Log.d(Player.class.toString(), "Showing alert dialog");
        }
        return false;
    }

    /**
     * Checks if user credentials are valid on the online service
     * @param password  User's password
     * @return          Credentials' validity
     */
    public boolean checkLogin(String password) {
        this.dialogTitle = application.getString(R.string.login_progress_title);
        this.dialogContent = application.getString(R.string.login_progress_content);
        job = this.JOB_LOGIN;
        AsyncTask login = new GetRestData().execute(password);
        try {
            return (Boolean) login.get();
        } catch (InterruptedException e) {
            Log.e(Player.class.toString(), e.getMessage(), e);
        } catch (ExecutionException e) {
            Log.e(Player.class.toString(), e.getMessage(), e);
        }
        return false;
    }

    /**
     * Class to handle all Player network tasks from a central async hub
     */
    private class GetRestData extends AsyncTask<String, Void, Boolean> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            if( Helpers.isConnectedToInternet(application) ) {
                this.dialog = new ProgressDialog(application.getActivity());
                this.dialog.setTitle(dialogTitle);
                this.dialog.setMessage(dialogContent);
                this.dialog.show();
            } else {
                Helpers.showInternetErrorDialog(application);
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (dialog != null && dialog.isShowing())
                dialog.dismiss();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            if( Helpers.isConnectedToInternet(application) ) {
                switch (job) {
                    case JOB_LOGIN:
                        return tryLogin(params[0]);
                    case JOB_REGISTER:
                        return tryRegister(params[0]);
                    case JOB_POST_GAME:
                        return tryPostGames(params[0]);
                }
            }
            return false;
        }
    }

    /**
     * Try to connect to the online service to register the account
     * @param password  User's password
     * @return          Success or failure
     */
    private boolean tryRegister(String password) {
        DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
        HttpPost httpPost = new HttpPost(application.getString(R.string.webservice_url) + "player/" + username);
        httpPost.setHeader("Content-Type", "application/json");
        try {
            JSONObject data = new JSONObject();
            data.put("username", username);
            data.put("password", password);
            data.put("email", email);
            data.put("realname", realname);
            httpPost.setEntity(new StringEntity(data.toString()));

            HttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();

            String result = Helpers.getStringFromStream(response.getEntity().getContent());
            Log.d(Player.class.toString(), "User registration with status " + statusCode + " and content " + result);

            if (statusCode == HttpStatus.SC_CREATED) {
                // Login
                CityGameApplication app = (CityGameApplication) application;
                app.setUsername(username);
                app.setPassword(password);
                app.setLoggedIn(true);

                // Any games completed yet?
                tryPostGames(password);

                return true;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Try to post games to online account
     * @param password  User's password
     * @return          Success or failure
     * @see #tryPostGame(String, String, GameContent)
     */
    private boolean tryPostGames(String password) {
        boolean success = true;
        for( GameContent content : games ) {
            if( content.isCompleted() ) {
                success = tryPostGame(username, password, content);
            }
        }
        return success;
    }

    /**
     * Try to post an individual game to the online account
     * @param username  Username
     * @param password  User's password
     * @param content   The game content to post
     * @return          Success or failure
     */
    private boolean tryPostGame(String username, String password, GameContent content) {
        int gameContentId = content.getId();
        int score = content.getScore();

        DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
        HttpPost httpPost = new HttpPost(application.getString(R.string.webservice_url) + "player/" + username + "/" + gameContentId);
        httpPost.setHeader("Content-Type", "application/json");

        JSONObject data = new JSONObject();
        try {
            data.put("password", password);
            data.put("score", score);

            // And individual question result data
            JSONArray questions = new JSONArray();
            for( Map.Entry<Integer, Question> entry : content.getQuestionList().entrySet() ) {
                Question question = entry.getValue();
                JSONObject addQuestion = new JSONObject();
                addQuestion.put("gid", content.getId());
                addQuestion.put("qid", question.getqId());
                if( question.getType() == Question.PLAIN_TEXT ) {
                    addQuestion.put("answer", question.getUserTextInput());
                } else if( question.getType() == Question.MULTIPLE_CHOICE ) {
                    addQuestion.put("answer", question.getUserMultiInput());
                }
                questions.put(addQuestion);
            }
            data.put("questions", questions);
            Log.d(Player.class.toString(), "Data: " + data.toString());

            httpPost.setEntity(new StringEntity(data.toString()));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpResponse response;

        try {
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();

            String result = Helpers.getStringFromStream(response.getEntity().getContent());
            Log.d(Player.class.toString(), "User post Game with status " + statusCode + " and content " + result);

            return response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Checks if user's credentials are valid on the online webservice
     * @param password  User's password
     * @return          Credentials' validity
     */
    private boolean tryLogin(String password) {
        DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
        HttpPost httpPost = new HttpPost(application.getString(R.string.webservice_url) + "player/login/" + username);
        httpPost.setHeader("Content-Type", "application/json");
        try {
            JSONObject data = new JSONObject();
            data.put("username", username);
            data.put("password", password);
            httpPost.setEntity(new StringEntity(data.toString()));

            HttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();

            String result = Helpers.getStringFromStream(response.getEntity().getContent());
            Log.d(Player.class.toString(), "User login with status " + statusCode + " and content " + result);

            if( statusCode == HttpStatus.SC_OK ) {
                application.setUsername(username);
                application.setPassword(password);
                application.setLoggedIn(true);

                // Post completed games to server
                tryPostGames(password);

                // Get completed games from server and store locally
                HttpGet dataGet = new HttpGet(application.getString(R.string.webservice_url) + "/player/" + username);
                HttpResponse playerDataResponse = httpClient.execute(dataGet);
                String jsonData = Helpers.getStringFromStream(playerDataResponse.getEntity().getContent());
                JSONObject playerData = new JSONObject(jsonData);
                JSONArray gamesData = playerData.getJSONArray("games");

                // Iterate through all games played
                for( int i = 0; i < gamesData.length(); ++i ) {
                    JSONObject gameData = gamesData.getJSONObject(i);
                    int gameId = gameData.getInt("gameContentId");
                    // Check if it is in database
                    GameDbHelper helper = new GameDbHelper(application.getApplicationContext());
                    SQLiteDatabase sqlDb = helper.getWritableDatabase();
                    String where = GameDB.Games.COL_GID + " = ?";
                    String[] whereArgs = { "" + gameId };
                    Cursor cur = sqlDb.query(GameDB.Games.TABLE_NAME, null, where, whereArgs, null, null, null, null);
                    boolean hasData = cur.getCount() > 0;
                    cur.close();

                    // Don't continue unless we don't actually have any data on this game locally
                    if( !hasData ) {
                        try {
                            // Initialize GameContent (Blocking until ready)
                            Log.d(Player.class.toString(), "Preparing gamecontent...");
                            Providers.getGameContentProvider().getGameContentByIdSync(gameId);
                            Log.d(Player.class.toString(), "Preparing gamecontent finished!");
                            JSONArray gameQuestionsData = gameData.getJSONArray("questionAnswerData");
                            // iterate over all questions in this game
                            for (int j = 0; j < gameQuestionsData.length(); ++j) {
                                Log.d(Player.class.toString(), "Running question " + j + "...");
                                JSONObject question = gameQuestionsData.getJSONObject(j);
                                int qid = question.getInt("qid");
                                Question questionData = Providers.getQuestionProvider().loadQuestionById(gameId, qid);
                                String answer = question.getString("answer");
                                // Store data through the checkAnswer method
                                if( questionData.getType() == Question.PLAIN_TEXT ) {
                                    questionData.checkAnswer(answer);
                                } else if( questionData.getType() == Question.MULTIPLE_CHOICE ) {
                                    int answerInt = Integer.parseInt(answer);
                                    questionData.checkAnswer(answerInt);
                                }
                            }
                            // And finally, flag game as completed
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(GameDB.Games.COL_COMPLETED, 1);
                            contentValues.put(GameDB.Games.COL_SCORE, gameData.getInt("score"));
                            String whereGame = GameDB.Games.COL_GID + " = ?";
                            String[] whereArgsGame = { "" + gameId };
                            sqlDb.update(GameDB.Games.TABLE_NAME, contentValues, whereGame, whereArgsGame);
                        } catch( ClassCastException|NumberFormatException e ) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            return statusCode == HttpStatus.SC_OK;
        } catch (IOException e) {
            Log.e(Player.class.toString(), e.getMessage(), e);
        } catch (JSONException e) {
            Log.e(Player.class.toString(), e.getMessage(), e);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(application.getActivity());
        builder.setTitle(R.string.login_fail_title)
                .setMessage(R.string.login_fail_content)
                .setCancelable(true)
                .setPositiveButton(application.getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();

        return false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public ArrayList<GameContent> getGames() {
        return games;
    }

    public void setGames(ArrayList<GameContent> games) {
        this.games = games;
    }
}
