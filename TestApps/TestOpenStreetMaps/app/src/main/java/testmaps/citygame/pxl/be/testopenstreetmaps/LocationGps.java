package testmaps.citygame.pxl.be.testopenstreetmaps;

import android.location.*;
import android.os.Bundle;

import org.osmdroid.util.GeoPoint;

/**
 * Created by Christina Korosec on 17/12/2014.
 */
public class LocationGps implements LocationListener {

    private MainActivity main;

    public LocationGps(MainActivity main) {
        this.main = main;
    }

    @Override
    public void onLocationChanged(Location location) {
        main.updateLoc(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
