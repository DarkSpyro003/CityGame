package be.pxl.citygame;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import be.pxl.citygame.data.Question;
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

        // Set content
        Question question = Providers.getQuestionProvider().loadQuestionById(gameId, questionId);
        ImageView pictureView = (ImageView) getActivity().findViewById(R.id.picture);
        if( question.getContentType() == Question.CONTENT_IMAGE ) {
            Bitmap img = question.getImage(getActivity().getApplication());
            pictureView.setImageBitmap(img);
        } else if( question.getContentType() == Question.CONTENT_VIDEO ) {
            pictureView.setVisibility(View.GONE);
            VideoView videoView = (VideoView) getActivity().findViewById(R.id.video);
            videoView.setVisibility(View.VISIBLE);
            videoView.setVideoURI(question.getLocalContentUri());

            // Setup media controls
            MediaController controller = new MediaController(getActivity());
            controller.setMediaPlayer(videoView);
            videoView.setMediaController(controller);

            // Initial image instead of black screen?
            videoView.seekTo(10);

            // Notify user to tap video and hit play
            Toast.makeText(getActivity().getApplicationContext(), getActivity().getApplicationContext().getString(R.string.instruction_tap_video), Toast.LENGTH_LONG).show();
        }
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
