package be.pxl.citygame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
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
import org.osmdroid.views.overlay.DirectedLocationOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.SimpleLocationOverlay;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.lang.reflect.Array;
import java.util.ArrayList;

import be.pxl.citygame.gps.ILocationRequest;
import be.pxl.citygame.gps.LocationGps;
import be.pxl.citygame.providers.Providers;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements ILocationRequest {

    private int gameId;
    private int questionId;
    private boolean dataSet = false;
    private MapView mapView;
    private MyLocationNewOverlay myLocationOverlay;
    private OverlayItem targetOverlay;
    private Location targetLocation;

    public void setData(int gameId, int questionId) {
        this.gameId = gameId;
        this.questionId = questionId;

        // Init map
        mapView = (MapView) getActivity().findViewById(R.id.mapview);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setClickable(true);
        mapView.getController().setZoom(18);

        // Start GPS
        new LocationGps(this, getActivity());

        // Display locations on map
        myLocationOverlay = new MyLocationNewOverlay(getActivity().getApplicationContext(), mapView);
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.enableFollowLocation();
        myLocationOverlay.setDrawAccuracyEnabled(true);
        mapView.getOverlays().add(myLocationOverlay);

        targetLocation = Providers.getQuestionProvider().loadQuestionById(gameId, questionId).getLocation();
        targetOverlay = new OverlayItem("Doel", "Uw volgende stopplaats", new GeoPoint(targetLocation));
        ArrayList<OverlayItem> overlayItems = new ArrayList<OverlayItem>();
        overlayItems.add(targetOverlay);
        ItemizedIconOverlay<OverlayItem> itemizedOverlayItems = new ItemizedIconOverlay<OverlayItem>(getActivity(), overlayItems, null);
        mapView.getOverlays().add(itemizedOverlayItems);

        this.dataSet = true;
    }

    @Override
    public void setLocation(Location loc) {
        if (loc != null) {
            GeoPoint currentLoc;
            currentLoc = new GeoPoint(loc);
            mapView.getController().animateTo(currentLoc);
            if( loc.distanceTo(targetLocation) <= getResources().getInteger(R.integer.gps_marker_distance) ) {
                CityGameApplication context = (CityGameApplication) getActivity().getApplicationContext();
                // Switch to next activity
                Intent intent = new Intent(context, QuestionActivity.class);
                intent.putExtra("gameId", gameId);
                // Ask question
                intent.putExtra("questionId", questionId);
                startActivity(intent);
                Log.d(MapFragment.class.toString(), "Switching to Game activity");
            }
        }
    }

    public MapFragment() {
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
        return inflater.inflate(R.layout.fragment_map, container, false);
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
            ArrayList<POI> pois = poiProvider.getPOICloseTo(currentPoint, tag, 50, 50.0);

            return pois;
        }

        @Override
        protected void onPostExecute(ArrayList<POI> pois) {
            onCallback(pois);
        }
    }

}
