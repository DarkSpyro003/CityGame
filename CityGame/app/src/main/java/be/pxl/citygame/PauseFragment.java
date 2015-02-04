package be.pxl.citygame;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.osmdroid.bonuspack.location.NominatimPOIProvider;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.bonuspack.overlays.FolderOverlay;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;

import be.pxl.citygame.gps.ILocationRequest;
import be.pxl.citygame.gps.LocationGps;
import be.pxl.citygame.providers.Providers;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class PauseFragment extends Fragment{

    private MapView mapView;
    private MyLocationNewOverlay myLocationOverlay;

    public void start(View v) {

        // Init map
        mapView = (MapView) v.findViewById(R.id.pauseview);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.setClickable(true);
        mapView.getController().setZoom(18);

        // Display locations on map
        myLocationOverlay = new MyLocationNewOverlay(getActivity().getApplicationContext(), mapView);
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.setDrawAccuracyEnabled(true);
        mapView.getOverlays().add(myLocationOverlay);

        do {
            if( myLocationOverlay != null && mapView.getController() != null )
                mapView.getController().setCenter(myLocationOverlay.getMyLocation());

        } while( myLocationOverlay == null || mapView.getController() == null );
    }

    public PauseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pause, container, false);
        start(view);
        return view;
    }




    public  void showPOIS(String tag)
    {
        AsyncTask poiTask = new GetPoi().execute(tag);
    }

    public void onCallback(ArrayList<POI> pois)
    {
        FolderOverlay poiMarkers = new FolderOverlay(getActivity());
        mapView.getOverlays().add(poiMarkers);

        Drawable poiIcon = getResources().getDrawable(R.drawable.restaurant);
        if(pois == null)
        {
            Log.d("POI", "pois is null");
        }
        for(POI poi : pois)
        {
            Marker poiMarker = new Marker(mapView);
            poiMarker.setTitle(poi.mType);
            poiMarker.setSnippet(poi.mDescription);
            poiMarker.setPosition(poi.mLocation);
            poiMarker.setIcon(poiIcon);
            Log.d("POI", poi.mDescription);
            /*if(poi.mThumbnail != null)
            {
                poiItem.setImage(new BitmapDrawable(poi.mThumbnail));
            }*/
            poiMarkers.add(poiMarker);
        }
    }

    private class GetPoi extends AsyncTask<String, Void, ArrayList<POI>> {

        @Override
        protected ArrayList<POI> doInBackground(String... params) {
            String tag = params[0];
            NominatimPOIProvider poiProvider = new NominatimPOIProvider();
            LocationManager lm = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            GeoPoint currentPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
            ArrayList<POI> pois = poiProvider.getPOICloseTo(currentPoint, tag, 50, 0.1);

            return pois;
        }

        @Override
        protected void onPostExecute(ArrayList<POI> pois) {
            onCallback(pois);
        }
    }

}
