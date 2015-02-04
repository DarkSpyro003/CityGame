package be.pxl.citygame;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import be.pxl.citygame.providers.Providers;


/**
 * A simple {@link Fragment} subclass.
 */
public class NextLocationFragment extends Fragment {

    private int gameId;
    private int questionId;
    private boolean dataSet = false;

    public void setData(int gameId, int questionId) {
        this.gameId = gameId;
        this.questionId = questionId;
        this.dataSet = true;

        TextView locationLabel = (TextView) getActivity().findViewById(R.id.tv_nextLocation);
        locationLabel.setText(getString(R.string.next_location) + " " + Providers.getQuestionProvider().loadQuestionById(gameId, questionId).getPlacename());
    }

    public NextLocationFragment() {
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
        return inflater.inflate(R.layout.fragment_next_location, container, false);
    }

    public void goToPause(View v)
    {
        Intent intent = new Intent(getActivity().getApplicationContext(), PauseActivity.class);
        startActivityForResult(intent, 0);
    }
}
