package be.pxl.citygame.providers;

import android.app.Application;
import android.location.Location;
import android.net.Uri;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import be.pxl.citygame.GameContent;
import be.pxl.citygame.Question;
import be.pxl.citygame.R;

/**
 * Created by Lorenz Jolling on 2015-01-16.
 */
class GameContentWebProvider implements IGameContentProvider
{
    private Application application;

    public GameContentWebProvider(Application application) {
        this.application = application;
    }

    @Override
    public GameContent getGameContentById(int id) {

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
                    content.addQuestion(questionObject);
                }
                if( questionObject != null ) {
                    questionObject.setExtraInfo(quest.getString("extraInfo"));
                    questionObject.setRemoteContentUri(Uri.parse(quest.getString("contentUrl")));
                    // TODO : Download remote content
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
}
