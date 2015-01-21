package be.pxl.citygame;

import android.app.Activity;
import android.app.Application;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import be.pxl.citygame.data.Question;
import be.pxl.citygame.providers.Providers;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link QuestionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class QuestionFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private int gameId;
    private int questionId;
    private boolean dataSet = false;
    private Question question;

    private EditText txtAnswer;
    private List<RadioButton> optionList;

    public void setData(int gameId, int questionId) {
        this.gameId = gameId;
        this.questionId = questionId;
        this.question = Providers.getQuestionProvider().loadQuestionById(gameId, questionId);
        this.dataSet = true;
    }

    public QuestionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView tv_question = (TextView) getView().findViewById(R.id.tv_question);
        tv_question.setText(question.getQuestion());

        //Build Question input UI
        LinearLayout layout_procedural_answer = (LinearLayout) getView().findViewById(R.id.layout_procedural_answer);
        if(question.getType() == Question.PLAIN_TEXT)
        {
            txtAnswer = new EditText(getActivity());
            layout_procedural_answer.addView(txtAnswer);
        }
        else if(question.getType() == Question.MULTIPLE_CHOICE)
        {
            RadioGroup rg_options = new RadioGroup(getActivity());
            rg_options.setOrientation(LinearLayout.VERTICAL);
            layout_procedural_answer.addView(rg_options);
            optionList = new ArrayList<RadioButton>();

            for (String option : question.getOptions())
            {
                RadioButton rb_option = new RadioButton(getActivity());
                rb_option.setText(option);
                rg_options.addView(rb_option);
                optionList.add(rb_option);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_question, container, false);
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
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
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

    public void handleAnswer(View v)
    {

    }

    public void showMoreInfo(View v)
    {

    }
}
