package be.pxl.citygame.providers;

import java.util.NoSuchElementException;

import be.pxl.citygame.GameContent;

/**
 * Created by Lorenz Jolling on 2015-01-16.
 */
public interface IGameContentProvider {

    public GameContent getGameContentById(int id) throws NoSuchElementException;

}
