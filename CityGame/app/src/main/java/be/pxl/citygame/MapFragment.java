package be.pxl.citygame;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.DirectedLocationOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.SimpleLocationOverlay;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;

import be.pxl.citygame.gps.ILocationRequest;
import be.pxl.citygame.gps.LocationGps;
import be.pxl.citygame.providers.Providers;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class MapFragment extends Fragment implements ILocationRequest {

    private int gameId;
    private int questionId;
    private boolean dataSet = false;
    private MapView mapView;
    private MyLocationNewOverlay myLocationOverlay;
    private OverlayItem targetOverlay;
    private Location targetLocation;

    private OnFragmentInteractionListener mListener;

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
            if( loc.distanceTo(targetLocation) < 15 ) {
                CityGameApplication context = (CityGameApplication) getActivity().getApplicationContext();
                if( (questionId + 1) < Providers.getGameContentProvider().getGameContentById(gameId).getNumQuestions() ) {
                    // TODO: Make question activity!!
                    /*// Switch to next activity
                    Intent intent = new Intent(context, QuestionActivity.class);
                    intent.putExtra("gameId", gameId);
                    // Next question
                    intent.putExtra("questionId", questionId + 1);
                    startActivity(intent);*/
                }
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // TODO: This code crashes. Review please?
        /*try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
