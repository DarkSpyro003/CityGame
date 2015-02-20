package be.pxl.citygame.gps;

import android.app.Activity;
import android.content.Context;
import android.location.*;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Christina on 20/01/2015.
 * Handles getting GPS location when necessary
 */
public class LocationGps implements LocationListener {

    private ILocationRequest request;
    private LocationManager locMan;
    private String locationProviderName;
    private boolean started = false;

    /**
     * Initialize the GPS and begin sending location updates
     * @param request   The object that wants to receive location updates
     * @param activity  The activity that will provide the SystemService for the locationprovider
     * @see be.pxl.citygame.gps.ILocationRequest
     */
    public LocationGps(ILocationRequest request, Activity activity) {
        this.request = request;

        locMan = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        List<String> provList = locMan.getProviders(true);
        Log.d("provider", "Provider listing: " + provList.size());
        for (String provName : provList) {
            Log.d("provider", provName);
        }

        // define some criteria for the location provider
        Criteria provProps = new Criteria();
        provProps.setCostAllowed(false);
        provProps.setAccuracy(Criteria.ACCURACY_COARSE);
        provProps.setPowerRequirement(Criteria.POWER_MEDIUM);

        // get a location provider with these criteria

        locationProviderName = locMan.getBestProvider(provProps, true);
        if (locationProviderName == null || !locMan.isProviderEnabled(locationProviderName)) {
            // if none is found use default
            locationProviderName = LocationManager.NETWORK_PROVIDER;
        }

        if (!locMan.isProviderEnabled(locationProviderName)) {
            String text = "No working location provider found!";
            Toast popup = Toast.makeText(activity.getApplicationContext(), text, Toast.LENGTH_LONG);
            popup.show();
        }

        locMan.requestLocationUpdates(locationProviderName, 5000, 10, this);
        this.started = true;
    }

    /**
     * Stops the location updates, disabling this GPS instance
     */
    public void stopGpsLocation() {
        if( started ) {
            this.started = false;
            locMan.removeUpdates(this);
        }
    }

    /**
     * (Re)starts the location updates, enabling this GPS instance
     */
    public void startGpsLocation() {
        if( !started ) {
            locMan.requestLocationUpdates(locationProviderName, 5000, 10, this);
            this.started = true;
        }
    }

    /**
     * Location callback to the requesting object
     * @param location  The new current location
     */
    @Override
    public void onLocationChanged(Location location) {
        request.setLocation(location);
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
