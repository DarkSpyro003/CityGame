package be.pxl.citygame;

import android.app.Application;

/**
 * Created by Christina on 15/01/2015.
 */
public class CityGameApplication extends Application {

    private GameContent currentGame;

    public GameContent getCurrentGame() {
        return currentGame;
    }

    public void setCurrentGame(GameContent currentGame) {
        this.currentGame = currentGame;
    }
}
