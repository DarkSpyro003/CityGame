package be.pxl.citygame.data;

import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;

import be.pxl.citygame.CityGameApplication;
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
    private Application application;

    private String dialogTitle;
    private String dialogContent;

    private static final int JOB_LOGIN = 0, JOB_REGISTER = 1, JOB_UPDATE = 2;
    private int job = 0;

    public Player(String username, Application application) {
        this.username = username;
        this.application = application;
    }

    public boolean register(String password) {
        this.dialogTitle = "Registering...";
        this.dialogContent = "Please wait.";
        job = this.JOB_REGISTER;
        AsyncTask register = new GetRestData().execute(password);
        try {
            // If not true, let it go to the bottom to show the AlertDialog
            if( ((Boolean) register.get()) )
                return true;
        } catch (InterruptedException e) {
            Log.e(Player.class.toString(), e.getMessage(), e);
        } catch (ExecutionException e) {
            Log.e(Player.class.toString(), e.getMessage(), e);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(((CityGameApplication)application).getActivity());
        builder.setTitle("Registration failed")
                .setMessage("Sorry, something went wrong")
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
        return false;
    }

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

    private class GetRestData extends AsyncTask<String, Void, Boolean> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            this.dialog = new ProgressDialog(((CityGameApplication)application).getActivity());
            this.dialog.setTitle(dialogTitle);
            this.dialog.setMessage(dialogContent);
            this.dialog.show();
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (dialog.isShowing())
                dialog.dismiss();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            switch(job) {
                case JOB_LOGIN:
                    return tryLogin(params[0]);
                case JOB_REGISTER:
                    return tryRegister(params[0]);
            }
            return null;
        }
    }

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

            StringBuilder sb = new StringBuilder();
            try {
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 65728);
                String line = null;

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
            catch (IOException e) { e.printStackTrace(); }
            catch (Exception e) { e.printStackTrace(); }

            Log.d(Player.class.toString(), "User registration with status " + statusCode + " and content " + sb.toString());

            return statusCode == HttpStatus.SC_CREATED;
        } catch (IOException e) {
            Log.e(Player.class.toString(), e.getMessage(), e);
        } catch (JSONException e) {
            Log.e(Player.class.toString(), e.getMessage(), e);
        }

        return false;
    }

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

            return statusCode == HttpStatus.SC_OK;
        } catch (IOException e) {
            Log.e(Player.class.toString(), e.getMessage(), e);
        } catch (JSONException e) {
            Log.e(Player.class.toString(), e.getMessage(), e);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(((CityGameApplication)application).getActivity());
        builder.setTitle(R.string.login_fail_title)
                .setMessage(R.string.login_fail_content)
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
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

    public String getGames() {
        return games;
    }

    public void setGames(String games) {
        this.games = games;
    }
}
