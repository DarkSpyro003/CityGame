package be.pxl.citygame.data;

import android.app.Application;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import be.pxl.citygame.R;

/**
 * Created by Christina on 21/01/2015.
 */
public class Player {

    private int id;
    private String username;
    private String email;
    private String realname;
    private String games;

    private boolean checkLogin(Application application, String username, String password) {

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

            return statusCode == HttpStatus.SC_OK;
        } catch (IOException e) {
            Log.e(Player.class.toString(), e.getMessage(), e);
        } catch (JSONException e) {
            Log.e(Player.class.toString(), e.getMessage(), e);
        }
        return false;
    }
}
