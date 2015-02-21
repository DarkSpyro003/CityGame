package be.pxl.citygame.providers;

import java.util.NoSuchElementException;

import be.pxl.citygame.MainActivity;
import be.pxl.citygame.data.GameContent;

/**
 * Created by Lorenz Jolling on 2015-01-16.
 */
public interface IGameContentProvider {

    public void initGameContentById(int id, GameContentCaller caller);
    public GameContent getGameContentById(int id) throws NoSuchElementException;

}
