package be.pxl.citygame;

import android.content.Intent;;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


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
    private LocationGps locService;

    /**
     * Setup GPS and markers
     * @param gameId        Game's id
     * @param questionId    Question's id
     */
    public void setData(int gameId, int questionId) {
        this.gameId = gameId;
        this.questionId = questionId;

        // Init map
        mapView = (MapView) getActivity().findViewById(R.id.mapview);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.setClickable(true);
        mapView.getController().setZoom(18);

        // Start GPS
        locService = new LocationGps(this, getActivity());

        // Display locations on map
        myLocationOverlay = new MyLocationNewOverlay(getActivity().getApplicationContext(), mapView);
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.enableFollowLocation();
        myLocationOverlay.setDrawAccuracyEnabled(true);
        mapView.getOverlays().add(myLocationOverlay);

        targetLocation = Providers.getQuestionProvider().loadQuestionById(gameId, questionId).getLocation();

        targetOverlay = new OverlayItem("Doel", "Uw volgende stopplaats", new GeoPoint(targetLocation));
        targetOverlay.setMarker(getResources().getDrawable(R.drawable.marker));
        ArrayList<OverlayItem> overlayItems = new ArrayList<OverlayItem>();
        overlayItems.add(targetOverlay);
        ItemizedIconOverlay<OverlayItem> itemizedOverlayItems = new ItemizedIconOverlay<OverlayItem>(getActivity(), overlayItems, null);
        mapView.getOverlays().add(itemizedOverlayItems);

        this.dataSet = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        // Turn off GPS when the activity is not active
        if( dataSet && locService != null )
            locService.stopGpsLocation();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Turn GPS on again when the activity becomes active
        if( dataSet ) {
            if( locService != null ) {
                locService.startGpsLocation();
            } else { // Should never happen, but just in case:
                locService = new LocationGps(this, getActivity());
            }
        }
    }

    @Override
    public void setLocation(Location loc) {
        if (loc != null) {
            GeoPoint currentLoc;
            currentLoc = new GeoPoint(loc);
            mapView.getController().animateTo(currentLoc);
            if( loc.getAccuracy() <= getResources().getInteger(R.integer.gps_marker_distance) && loc.distanceTo(targetLocation) <= getResources().getInteger(R.integer.gps_marker_distance) ) {
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

}
