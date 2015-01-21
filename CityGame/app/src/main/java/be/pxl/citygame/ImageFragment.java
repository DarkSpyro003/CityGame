package be.pxl.citygame;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import be.pxl.citygame.providers.Providers;


/**
 * A simple Fragment
 */
public class ImageFragment extends Fragment {

    private int gameId;
    private int questionId;
    private boolean dataSet = false;

    public void setData(int gameId, int questionId) {
        this.gameId = gameId;
        this.questionId = questionId;
        this.dataSet = true;

        // Set image!
        Bitmap img = Providers.getQuestionProvider().loadQuestionById(gameId, questionId).getImage(getActivity().getApplication());
        ImageView pictureView = (ImageView) getActivity().findViewById(R.id.picture);
        pictureView.setImageBitmap(img);
    }

    public ImageFragment() {
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
        return inflater.inflate(R.layout.fragment_image, container, false);
    }

}
