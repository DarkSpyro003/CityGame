package be.pxl.citygame.providers;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;

import be.pxl.citygame.CityGameApplication;
import be.pxl.citygame.MainActivity;
import be.pxl.citygame.data.GameContent;
import be.pxl.citygame.data.Question;
import be.pxl.citygame.R;
import be.pxl.citygame.data.database.GameDB;
import be.pxl.citygame.data.database.GameDbHelper;

/**
 * Created by Lorenz Jolling on 2015-01-16.
 */
class GameContentWebProvider implements IGameContentProvider
{
    private int MODE_CALLBACK = 0, MODE_GET = 1;
    private Application application;
    private Hashtable<Integer, GameContent> contentCache;
    private int mode;
    private MainActivity caller;

    public GameContentWebProvider(Application application) {
        contentCache = new Hashtable<Integer, GameContent>();
        this.application = application;
    }

    /**
     * Initializes game content by id, trying to download it all into cache
     * while waiting on a progress dialog. Will callback to MainActivity
     * when finished.
     * @param id        Game's id
     * @param caller    MainActivity calling this method so we can callback to it
     * @see be.pxl.citygame.MainActivity#startGameCallback(int)
     */
    @Override
    public void initGameContentById(int id, MainActivity caller) {
        GameContent content = contentCache.get(id);
        if( content == null ) {
            this.mode = MODE_CALLBACK;
            this.caller = caller;
            AsyncTask dataTask = new GetRestData().execute(id);
        } else {
            caller.startGameCallback(id);
        }
    }

    /**
     * Check if we have game content in RAM, else request from next method before returning
     * @param id    game's id
     * @return      The GameContent object holding all content
     * @throws NoSuchElementException
     * @see #getWebGameContentById(int)
     */
    @Override
    public GameContent getGameContentById(int id) throws NoSuchElementException {
        this.mode = MODE_GET;
        GameContent content = contentCache.get(id);
        if( content == null ) {
            AsyncTask dataTask = new GetRestData().execute(id);
            try {
                content = (GameContent) dataTask.get();
            } catch (InterruptedException e) {
                content = null;
                Log.e(GameContentWebProvider.class.toString(), e.getMessage(), e);
            } catch (ExecutionException e) {
                content = null;
                Log.e(GameContentWebProvider.class.toString(), e.getMessage(), e);
            }

            if( content == null )
                throw new NoSuchElementException("No such gamecontent ID");
        }

        return content;
    }

    /**
     * Checks local SQL cache for gamecontent, and if it's not there it'll fetch it from the online
     * service
     * @param id    The game's id
     * @return      The GameContent object holding all content
     */
    public GameContent getWebGameContentById(int id) {
        // Check local SQL database first
        GameDbHelper helper = new GameDbHelper(application.getApplicationContext());

        // Get writable so we can write in case we don't have data
        SQLiteDatabase sqlDb = helper.getWritableDatabase();
        String where = GameDB.Games.COL_GID + " = " + id;
        Cursor gameCursor = sqlDb.query(GameDB.Games.TABLE_NAME, null, where, null, null, null, null, null);

        if( gameCursor.getCount() == 1 ) { // Have local data!
            Log.d(GameContentWebProvider.class.toString(), "SQL Cache hit! " + id);
            gameCursor.moveToFirst();
            GameContent content = new GameContent(gameCursor.getString(gameCursor.getColumnIndex(GameDB.Games.COL_TITLE)));
            content.setId(id);
            content.setCompleted(gameCursor.getInt(gameCursor.getColumnIndex(GameDB.Games.COL_COMPLETED)) == 1);
            content.setScore(gameCursor.getInt(gameCursor.getColumnIndex(GameDB.Games.COL_SCORE)));

            // Question data
            String qwhere = GameDB.Questions.COL_GID + " = " + id;
            String qorder = GameDB.Questions.COL_QID + " ASC";
            Cursor qcur = sqlDb.query(GameDB.Questions.TABLE_NAME, null, qwhere, null, null, null, qorder, null);

            ArrayList<Question> questionContent = new ArrayList<>();
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
                    q.setApplication(application);
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
                    q.setLocalPhotoUri(Uri.parse(qcur.getString(qcur.getColumnIndex(GameDB.Questions.COL_LOCALPHOTO))));

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
            return content;
        } else {
            // Slightly modified version of the code found in MainActivity(Christina's?)
            DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
            HttpGet httpGet = new HttpGet(application.getString(R.string.webservice_url) + "gamecontent/" + id);
            // Depends on your web service
            httpGet.setHeader("Content-type", "application/json");

            InputStream inputStream = null;
            String result = null;
            try {
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity entity = response.getEntity();

                inputStream = entity.getContent();
                // json is UTF-8 by default
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                result = sb.toString();
            } catch (Exception e) {
                Log.e(this.getClass().toString(), e.getMessage());
            }
            finally {
                try{
                    if(inputStream != null)
                        inputStream.close();
                } catch(Exception squish){}
            }

            try {
                JSONObject data = new JSONObject(result);
                String title = data.getString("title");
                GameContent content = new GameContent(title);

                JSONArray questionArray = data.getJSONArray("questionList");
                Log.d(GameContentWebProvider.class.toString(), "Got " + questionArray.length() + " questions.");
                for( int i = 0; i < questionArray.length(); ++i ) {
                    JSONObject quest = questionArray.getJSONObject(i);
                    int type = quest.getInt("type");
                    String question = quest.getString("question");
                    Question questionObject = null;
                    if( type == Question.PLAIN_TEXT ) {
                        String answer = quest.getString("text_answer");
                        questionObject = new Question(type, question, answer);
                    } else if( type == Question.MULTIPLE_CHOICE ) {
                        int answer = quest.getInt("multi_answer");
                        ArrayList<String> choices = new ArrayList<String>();
                        JSONArray choiceArray = quest.getJSONArray("options");
                        for( int j = 0; j < choiceArray.length(); ++j ) {
                            choices.add(choiceArray.getString(j));
                        }
                        questionObject = new Question(type, question, answer, choices);
                    }
                    if( questionObject != null )
                        content.addQuestion(questionObject);

                    if( questionObject != null ) {
                        questionObject.setqId(i);
                        questionObject.setGameId(id);
                        questionObject.setApplication(application);
                        questionObject.setPlacename(quest.getString("placename"));
                        questionObject.setExtraInfo(quest.getString("extraInfo"));
                        questionObject.setRemoteContentUri(Uri.parse(quest.getString("contentUrl")));
                        URL remoteURL = null;
                        try {
                            String link = questionObject.getRemoteContentUri().toString();
                            String fileName = link.substring(link.lastIndexOf('/') + 1);
                            File cacheDir = application.getCacheDir();

                            File cacheFile = new File(cacheDir, fileName);
                            if (!cacheFile.exists()){ // If already in cache, don't download again
                                Log.d(GameContentWebProvider.class.toString(), "File not in cache, fetching: " + link);
                                remoteURL = new URL(link);
                                String contentType = remoteURL.openConnection().getContentType();
                                if (contentType.toLowerCase().contains("video")) {
                                    questionObject.setContentType(Question.CONTENT_VIDEO);
                                } else if (contentType.toLowerCase().contains("image")) {
                                    questionObject.setContentType(Question.CONTENT_IMAGE);
                                }

                                InputStream remoteInput = new BufferedInputStream(remoteURL.openStream(), 10240);

                                FileOutputStream cacheOutput = new FileOutputStream(cacheFile);
                                byte[] buff = new byte[1024];
                                int dataSize;
                                int loadedSize = 0;
                                while ((dataSize = remoteInput.read(buff)) != -1) {
                                    loadedSize += dataSize;
                                    cacheOutput.write(buff, 0, dataSize);
                                }
                                cacheOutput.close();
                            } else {
                                Log.d(GameContentWebProvider.class.toString(), "File found in cache: " + link);
                            }
                            questionObject.setLocalContentUri(Uri.parse(cacheFile.getAbsolutePath()));
                        } catch (IOException e) {
                            Log.e(GameContentWebProvider.class.toString(), e.getMessage(), e);
                        }

                        application.getCacheDir();
                        Location loc = new Location("");
                        loc.setLatitude(quest.getDouble("latitude"));
                        loc.setLongitude(quest.getDouble("longitude"));
                        questionObject.setLocation(loc);
                    }
                }
                content.setId(id);
                // Store content into local database
                ContentValues gamecontent_data = new ContentValues();
                gamecontent_data.put(GameDB.Games.COL_GID, content.getId());
                gamecontent_data.put(GameDB.Games.COL_TITLE, content.getTitle());
                gamecontent_data.put(GameDB.Games.COL_COMPLETED, 0);
                gamecontent_data.put(GameDB.Games.COL_SCORE, 0);
                // Insert can fail if game played before, we don't care about this
                sqlDb.insert(GameDB.Games.TABLE_NAME, null, gamecontent_data);

                // Also insert the questions
                int i = 0;
                for( Question question : content.getQuestionList() ) {
                    ContentValues question_data = new ContentValues();
                    question_data.put(GameDB.Questions.COL_QID, i);
                    question_data.put(GameDB.Questions.COL_GID, content.getId());
                    question_data.put(GameDB.Questions.COL_TYPE, question.getType());
                    question_data.put(GameDB.Questions.COL_QUESTION, question.getQuestion());
                    if( question.getType() == Question.PLAIN_TEXT ) {
                        question_data.put(GameDB.Questions.COL_TEXT_ANSWER, question.getText_answer());
                    } else if( question.getType() == Question.MULTIPLE_CHOICE ) {
                        question_data.put(GameDB.Questions.COL_MULTI_ANSWER, question.getMulti_answer());
                        // Also insert multichoice data
                        int j = 0;
                        for( String option : question.getOptions() ) {
                            ContentValues mc_data = new ContentValues();
                            mc_data.put(GameDB.QuestionMultiAnswer.COL_QID, i);
                            mc_data.put(GameDB.QuestionMultiAnswer.COL_GID, content.getId());
                            mc_data.put(GameDB.QuestionMultiAnswer.COL_CID, j);
                            mc_data.put(GameDB.QuestionMultiAnswer.COL_ANSWER, option);
                            j++;
                            // Insert row
                            sqlDb.insert(GameDB.QuestionMultiAnswer.TABLE_NAME, null, mc_data);
                        }
                    }
                    question_data.put(GameDB.Questions.COL_PLACENAME, question.getPlacename());
                    question_data.put(GameDB.Questions.COL_EXTRAINFO, question.getExtraInfo());
                    question_data.put(GameDB.Questions.COL_LOCALURL, question.getLocalContentUri().toString());
                    question_data.put(GameDB.Questions.COL_CONTENT_TYPE, question.getContentType());
                    question_data.put(GameDB.Questions.COL_LATITUDE, question.getLocation().getLatitude());
                    question_data.put(GameDB.Questions.COL_LONGITUDE, question.getLocation().getLongitude());

                    question_data.put(GameDB.Questions.COL_ANSWERED, 0);
                    question_data.put(GameDB.Questions.COL_ANSWERED_CORRECT, 0);

                    // Insert can fail if game played before, we don't care about this
                    sqlDb.insert(GameDB.Questions.TABLE_NAME, null, question_data);
                    i++;
                }
                contentCache.put(id, content);

                return content;
            } catch(JSONException e) {
            }
            return null;
        }
    }

    /**
     * Handle all network tasks asynchronously and display a ProgressDialog where possible
     */
    private class GetRestData extends AsyncTask<Integer, Void, GameContent> {

        private int gameContentId;
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            if( application.getApplicationContext() == null )
                Log.e(GameContentWebProvider.class.toString(), "ApplicationContext = null");

            this.dialog = new ProgressDialog(((CityGameApplication)application).getActivity());
            this.dialog.setTitle(application.getString(R.string.load_game_progress_title));
            this.dialog.setMessage(application.getString(R.string.load_game_progress_content));
            this.dialog.show();
        }

        @Override
        protected void onPostExecute(GameContent content) {
            if (dialog.isShowing())
                dialog.dismiss();

            if( mode == MODE_CALLBACK ) {
                caller.startGameCallback(content.getId());
            }
        }

        @Override
        protected GameContent doInBackground(Integer... params) {
            this.gameContentId = params[0];
            GameContent content = getWebGameContentById(gameContentId);

            return content;
        }
    }
}
