package be.pxl.citygame;

import android.app.Activity;
import android.app.Application;

import be.pxl.citygame.data.Player;

/**
 * Created by Christina on 15/01/2015.
 */
public class CityGameApplication extends Application {

    private Activity activity;
    private boolean loggedIn = false;
    private String username = "!!offline";
    private String password;
    private Player player;

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
