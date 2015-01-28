package be.pxl.citygame.data;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christina on 7/01/2015.
 */
public class Question {

    // type: 0 = plain text, 1 = multiple choice
    public static final int PLAIN_TEXT = 0,
                            MULTIPLE_CHOICE = 1;
    private int type;
    private String question;
    private String text_answer;
    private int multi_answer;
    private String userTextInput;
    private int userMultiInput;
    private List<String> options;
    private String extraInfo;
    private String placename;
    private Location location;
    private Uri remoteContentUri; // The remote location for the content
    private Uri localContentUri; // When the content is downloaded, its location gets set here

    private boolean answered = false; // Gets set to true when the question is answered
    private boolean answeredCorrect = false; // Gets set to if the result was correct or not

    // Constructor plain text question
    public Question(int type, String question, String answer) {
        if( type != PLAIN_TEXT )
            throw new IllegalArgumentException("Type must be 0 for using this constructor");

        this.type = type;
        this.question = question;
        this.text_answer = answer;
        this.extraInfo = "";
    }

    // Constructor multiple choice question
    public Question(int type, String question, int answer, List<String> options) {
        if( type != MULTIPLE_CHOICE )
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
        this.setUserTextInput(text);
        return this.answeredCorrect;
    }

    // Checks multiple choice answer
    public boolean checkAnswer(int id) {
        this.answered = true;
        answeredCorrect = id == this.multi_answer ;
        this.setUserMultiInput(id);
        return this.answeredCorrect;
    }

    public Bitmap getImage(Application app) {
        try {
            File cacheFile = new File(localContentUri.toString());
            InputStream fileInputStream = null;
            fileInputStream = new FileInputStream(cacheFile);
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inJustDecodeBounds = false;
            Bitmap image = BitmapFactory.decodeStream(fileInputStream, null, bitmapOptions);
            fileInputStream.close();

            return image;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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

    public String getOption(int i) {
        return options.get(i);
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getPlacename() {
        return placename;
    }

    public void setPlacename(String placename) {
        this.placename = placename;
    }

    public String getUserTextInput() {
        return userTextInput;
    }

    public void setUserTextInput(String userTextInput) {
        this.userTextInput = userTextInput;
    }

    public int getUserMultiInput() {
        return userMultiInput;
    }

    public void setUserMultiInput(int userMultiInput) {
        this.userMultiInput = userMultiInput;
    }
}
