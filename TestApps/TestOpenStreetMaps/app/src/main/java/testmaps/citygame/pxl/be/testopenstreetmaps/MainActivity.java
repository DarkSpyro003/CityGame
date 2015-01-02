package testmaps.citygame.pxl.be.testopenstreetmaps;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.*;
import android.location.*;
import android.content.Context;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;


public class MainActivity extends ActionBarActivity {

    private MapView mapView;
    private LocationGps loc;
    private LocationManager locMan;
    private String locationProviderName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setClickable(true);
        mapView.getController().setZoom(15);
        createLocationService();
        Location lloc = locMan.getLastKnownLocation(locationProviderName);
        updateLoc(lloc);
    }

    public void updateLoc(Location loc) {
        if (loc != null) {
            GeoPoint currentLoc;
            currentLoc = new GeoPoint(loc);
            mapView.getController().setCenter(currentLoc);
            mapView.getController().animateTo(currentLoc);
            Toast.makeText(getApplicationContext(), getLocationString(loc), Toast.LENGTH_LONG).show();
        }
    }

    private String getLocationString(Location loc) {
        if (loc == null) {
            return null;
        } else {
            return String.format(Locale.US, "(%.2f,%.2f)", loc.getLatitude(), loc.getLongitude());
        }
    }

    private void createLocationService() {
        locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
            Toast popup = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
            popup.show();
        }

        loc = new LocationGps(this);
        locMan.requestLocationUpdates(locationProviderName, 5000, 10, loc);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
