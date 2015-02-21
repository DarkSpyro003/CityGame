package be.pxl.citygame.providers;

/**
 * Created by Christina on 21/02/2015.
 */
public interface GameContentCaller {

    /**
     * Called when initial game caching has completed
     * Should have all data locally now
     *
     * @param id The game's id
     */
    public void startGameCallback(int id);
}
