package be.pxl.citygame;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import be.pxl.citygame.data.GameContent;
import be.pxl.citygame.data.Question;
import be.pxl.citygame.providers.Providers;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuestionFragment extends Fragment {

    private int gameId;
    private int questionId;
    private boolean dataSet = false;
    private Question question;

    private EditText txtAnswer;
    private List<RadioButton> optionList;

    /**
     * Generates dynamic question layout after receiving data
     * @param gameId        Game's id
     * @param questionId    Question's id
     */
    public void setData(int gameId, int questionId) {
        this.gameId = gameId;
        this.questionId = questionId;
        this.question = Providers.getQuestionProvider().loadQuestionById(gameId, questionId);
        this.dataSet = true;

        // Build Question input UI now that we have received data
        TextView tv_question = (TextView) getActivity().findViewById(R.id.tv_question);
        tv_question.setText(question.getQuestion());

        LinearLayout layout_procedural_answer = (LinearLayout) getActivity().findViewById(R.id.layout_procedural_answer);
        if(question.getType() == Question.PLAIN_TEXT) {
            txtAnswer = new EditText(getActivity());
            layout_procedural_answer.addView(txtAnswer);
        }
        else if(question.getType() == Question.MULTIPLE_CHOICE) {
            RadioGroup rg_options = new RadioGroup(getActivity());
            rg_options.setOrientation(LinearLayout.VERTICAL);
            layout_procedural_answer.addView(rg_options);
            optionList = new ArrayList<RadioButton>();

            for (String option : question.getOptions()) {
                RadioButton rb_option = new RadioButton(getActivity());
                rb_option.setText(option);
                rg_options.addView(rb_option);
                optionList.add(rb_option);
            }
        }
    }

    public QuestionFragment() {
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
        return inflater.inflate(R.layout.fragment_question, container, false);
    }

    /**
     * Returns the first qid following passed qid
     * @param qid   Previous question's id
     * @param gid   Game's id
     * @return      Next question id or -1 in case of none
     */
    private int getNextQid(int gid, int qid) {
        for( Map.Entry<Integer, Question> entry : Providers.getGameContentProvider().getGameContentById(gid).getQuestionList().entrySet() ) {
            int key = entry.getKey();
            if( key > qid )
                return key;
        }
        return -1;
    }

    public void handleAnswer(View v) {
        if( question.getType() == Question.PLAIN_TEXT )
            question.checkAnswer(txtAnswer.getText().toString());
        else if( question.getType() == Question.MULTIPLE_CHOICE ) {
            int i = 0;
            for( RadioButton option : optionList ) {
                if( option.isChecked() ) {
                    question.checkAnswer(i);
                }
                i++;
            }
        }

        CityGameApplication context = (CityGameApplication) getActivity().getApplicationContext();
        int nextQid = getNextQid(gameId, questionId);
        if( nextQid >= 0 ) {
            // Switch to next activity
            Intent intent = new Intent(context, NextLocationActivity.class);
            intent.putExtra("gameId", gameId);
            // Go to next question
            intent.putExtra("questionId", nextQid);
            startActivity(intent);
            Log.d(QuestionFragment.class.toString(), "Switching to NextLocation activity");
        } else {
            Intent intent = new Intent(context, GameResultsActivity.class);
            intent.putExtra("gameId", gameId);
            startActivity(intent);
        }
    }

    public void showMoreInfo(View v) {
        Intent intent = new Intent(getActivity().getApplicationContext(), InfoActivity.class);
        intent.putExtra("gameId", gameId);
        intent.putExtra("questionId", questionId);
        startActivityForResult(intent, 0);
    }
}
