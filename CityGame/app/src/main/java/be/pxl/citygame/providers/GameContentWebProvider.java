package be.pxl.citygame.providers;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
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
import be.pxl.citygame.data.GameContent;
import be.pxl.citygame.data.Question;
import be.pxl.citygame.R;

/**
 * Created by Lorenz Jolling on 2015-01-16.
 */
class GameContentWebProvider implements IGameContentProvider
{
    private Application application;
    private Hashtable<Integer, GameContent> contentCache;

    public GameContentWebProvider(Application application) {
        contentCache = new Hashtable<Integer, GameContent>();
        this.application = application;
    }

    @Override
    public GameContent getGameContentById(int id) throws NoSuchElementException {
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

            content.setId(id);
            // TODO: Christina gaat dit doen: Insert this gamecontent into local database, including questions
            contentCache.put(id, content);
        }

        return content;
    }

    public GameContent getWebGameContentById(int id) {

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
                    questionObject.setPlacename(quest.getString("placename"));
                    questionObject.setExtraInfo(quest.getString("extraInfo"));
                    questionObject.setRemoteContentUri(Uri.parse(quest.getString("contentUrl")));
                    // TODO: Christina gaat dit doen: Download remote video
                    URL remoteURL = null;
                    try {
                        String link = questionObject.getRemoteContentUri().toString();
                        remoteURL = new URL(link);
                        URLConnection connection = remoteURL.openConnection();
                        InputStream remoteInput = new BufferedInputStream(remoteURL.openStream(), 10240);
                        File cacheDir = application.getCacheDir();
                        String fileName = link.substring(link.lastIndexOf('/') + 1);
                        File cacheFile = new File(cacheDir, fileName);

                        FileOutputStream cacheOutput = new FileOutputStream(cacheFile);
                        byte[] buff = new byte[1024];
                        int dataSize;
                        int loadedSize = 0;
                        while( (dataSize = remoteInput.read(buff)) != -1 ) {
                            loadedSize += dataSize;
                            cacheOutput.write(buff, 0, dataSize);
                        }
                        cacheOutput.close();

                        questionObject.setLocalContentUri(Uri.parse(cacheFile.getAbsolutePath()));
                    } catch (MalformedURLException e) {
                        Log.e(GameContentWebProvider.class.toString(), e.getMessage(), e);
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
            return content;
        } catch(JSONException e) {
        }
        return null;
    }

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
        }

        @Override
        protected GameContent doInBackground(Integer... params) {
            this.gameContentId = params[0];
            GameContent content = getWebGameContentById(gameContentId);

            return content;
        }
    }
}
