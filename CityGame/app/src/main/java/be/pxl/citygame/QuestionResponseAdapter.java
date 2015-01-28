package be.pxl.citygame;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import be.pxl.citygame.data.Question;

/**
 * Created by Lorenz Jolling on 2015-01-21.
 */
public class QuestionResponseAdapter extends ArrayAdapter<Question> {

    private Question[] questions;

    public QuestionResponseAdapter(Context context, Question[] questions) {
        super(context, R.layout.question_response, questions);
        this.questions = questions;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View questionResponseView = inflater.inflate(R.layout.question_response, parent, false);

        TextView questionLabelView = (TextView) questionResponseView.findViewById(R.id.tv_question_label);
        TextView questionUserInputView = (TextView) questionResponseView.findViewById(R.id.tv_question_user_input);
        TextView questionUserInputLabelView = (TextView) questionResponseView.findViewById(R.id.tv_question_user_input_label);
        TextView questionCorrectOutputView = (TextView) questionResponseView.findViewById(R.id.tv_question_correct_output);

        Question question = questions[position];

        if (question.isAnsweredCorrect()) {
            questionCorrectOutputView.setTextColor(0xFF00CC00);
            questionUserInputLabelView.setVisibility(View.GONE);
            questionUserInputView.setVisibility(View.GONE);

        } else {
            questionUserInputView.setTextColor(0xFFCC0000);
        }

        switch (question.getType())
        {
            case Question.MULTIPLE_CHOICE:
                questionUserInputView.setText(question.getOption(question.getUserMultiInput()));
                questionCorrectOutputView.setText(question.getOption(question.getMulti_answer()));
                Log.d(QuestionResponseAdapter.class.toString(), "Showing score for multichoice question");
                break;
            case Question.PLAIN_TEXT:
                questionUserInputView.setText(question.getUserTextInput());
                questionCorrectOutputView.setText(question.getText_answer());
                Log.d(QuestionResponseAdapter.class.toString(), "Showing score for plaintext question, answer was " + question.getUserTextInput());
                break;
            default:
                Log.e(QuestionResponseAdapter.class.toString(), "Unknown question type");
        }
        questionLabelView.setText(question.getQuestion());

        return questionResponseView;
    }
}
