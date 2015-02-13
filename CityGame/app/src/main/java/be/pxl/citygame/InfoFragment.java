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

import be.pxl.citygame.data.Question;
import be.pxl.citygame.providers.Providers;


/**
 * A simple {@link Fragment} subclass.
 */
public class InfoFragment extends Fragment {

    private int gameId;
    private int questionId;
    private boolean dataSet = false;

    public void setData(int gameId, int questionId) {
        this.gameId = gameId;
        this.questionId = questionId;
        this.dataSet = true;

        Question question = Providers.getQuestionProvider().loadQuestionById(gameId, questionId);

        //Set info text
        TextView tv_info = (TextView) getActivity().findViewById(R.id.tv_info);
        tv_info.setText(question.getExtraInfo());
    }

    public InfoFragment() {
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
        return inflater.inflate(R.layout.fragment_info, container, false);
    }

    public void handleBackToQuestion(View v) {
       getActivity().finish();
    }

}
