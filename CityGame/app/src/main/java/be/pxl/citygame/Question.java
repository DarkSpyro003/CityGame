package be.pxl.citygame;

import android.graphics.drawable.Drawable;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christina on 7/01/2015.
 */
public class Question {

    // type: 0 = plain text, 1 = multiple choice
    private int type;
    private String question;
    private String text_answer;
    private int multi_answer;
    private List<String> options;
    private String extraInfo;
    private Uri remoteContentUri; // The remote location for the content
    private Uri localContentUri; // When the content is downloaded, its location gets set here

    private boolean answered = false; // Gets set to true when the question is answered
    private boolean answeredCorrect = false; // Gets set to if the result was correct or not

    // Constructor plain text question
    public Question(int type, String question, String answer) {
        if( type != 0 )
            throw new IllegalArgumentException("Type must be 0 for using this constructor");

        this.type = type;
        this.question = question;
        this.text_answer = answer;
        this.extraInfo = "";
    }

    // Constructor multiple choice question
    public Question(int type, String question, int answer, List<String> options) {
        if( type != 1 )
            throw new IllegalArgumentException("Type must be 1 for using this constructor");

        this.type = type;
        this.question = question;
        this.multi_answer = answer;
        this.options = new ArrayList<String>(options);
        this.extraInfo = "";
    }

    // Checks plain text answer
    public boolean checkAnswer(String text) {
        this.answered = true;
        this.answeredCorrect = text.toLowerCase().equals(this.text_answer.toLowerCase());
        return this.answeredCorrect;
    }

    // Checks multiple choice answer
    public boolean checkAnswer(int id) {
        this.answered = true;
        answeredCorrect = id == this.multi_answer ;
        return this.answeredCorrect;
    }

    public int getType() {
        return type;
    }

    public String getQuestion() {
        return question;
    }

    public String getText_answer() {
        return text_answer;
    }

    public int getMulti_answer() {
        return multi_answer;
    }

    public List<String> getOptions() {
        return options;
    }

    public boolean isAnswered() {
        return answered;
    }

    public boolean isAnsweredCorrect() {
        return answeredCorrect;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public Uri getRemoteContentUri() {
        return remoteContentUri;
    }

    public void setRemoteContentUri(Uri remoteContentUri) {
        this.remoteContentUri = remoteContentUri;
    }

    public Uri getLocalContentUri() {
        return localContentUri;
    }

    public void setLocalContentUri(Uri localContentUri) {
        this.localContentUri = localContentUri;
    }
}
