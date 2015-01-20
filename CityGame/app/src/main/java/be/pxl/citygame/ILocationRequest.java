package be.pxl.citygame;

import android.location.Location;

/**
 * Created by Christina on 20/01/2015.
 * This interface can be used by anything that wants to create a
 * LocationGps to get back location results.
 */
public interface ILocationRequest {

    public abstract void setLocation(Location loc);
}
