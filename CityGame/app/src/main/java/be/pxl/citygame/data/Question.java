package be.pxl.citygame.data;

import android.app.Application;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import be.pxl.citygame.data.database.GameDB;
import be.pxl.citygame.data.database.GameDbHelper;

/**
 * Created by Christina on 7/01/2015.
 */
public class Question {

    // question type: 0 = plain text, 1 = multiple choice
    // content type: 0 = image, 1 = video
    public static final int PLAIN_TEXT = 0,
                            MULTIPLE_CHOICE = 1,
                            CONTENT_IMAGE = 0,
                            CONTENT_VIDEO = 1;
    private int type;
    private int gameId;
    private int qId;
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
    private Uri localPhotoUri = null;
    private int contentType;

    private boolean answered = false; // Gets set to true when the question is answered
    private boolean answeredCorrect = false; // Gets set to if the result was correct or not

    private Application application; // Store calling application

    /**
     * Constructor plain text question
     * @param type      The type of the question
     * @param question  The question text
     * @param answer    The correct answer text
     */
    public Question(int type, String question, String answer) {
        if( type != PLAIN_TEXT )
            throw new IllegalArgumentException("Type must be 0 for using this constructor");

        this.type = type;
        this.question = question;
        this.text_answer = answer;
        this.extraInfo = "";
        this.contentType = 0;
    }

    /**
     * Constructor for a multiple choice question
     * @param type      The type of the question
     * @param question  The question text
     * @param answer    The answer id
     * @param options   List of possible answers
     */
    public Question(int type, String question, int answer, List<String> options) {
        if( type != MULTIPLE_CHOICE )
            throw new IllegalArgumentException("Type must be 1 for using this constructor");

        this.type = type;
        this.question = question;
        this.multi_answer = answer;
        this.options = new ArrayList<>(options);
        this.extraInfo = "";
        this.contentType = 0;
    }

    /**
     * Flags a question as answered and store the flag in local db
     * @param result        Validity of question's answer
     * @param resultText    The exact written answer
     */
    private void storeAnswered(boolean result, String resultText) {
        int iresult = 0;
        if( result )
            iresult = 1;

        GameDbHelper helper = new GameDbHelper(application.getApplicationContext());
        SQLiteDatabase sqlDb = helper.getReadableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(GameDB.Questions.COL_ANSWERED, 1);
        contentValues.put(GameDB.Questions.COL_ANSWERED_CORRECT, iresult);
        contentValues.put(GameDB.Questions.COL_ANSWERED_CONTENT, resultText);
        String where = GameDB.Questions.COL_GID + " = ? AND " + GameDB.Questions.COL_QID + " = ?";
        String[] whereArgs = { "" + gameId, "" + qId };
        sqlDb.update(GameDB.Questions.TABLE_NAME, contentValues, where, whereArgs);
    }

    /**
     * Checks validity of plaintext answer and stores it
     * @param text  The given answer
     * @return      The answer's validity
     * @see #storeAnswered(boolean, String)
     */
    public boolean checkAnswer(String text) {
        this.answered = true;
        this.answeredCorrect = text.toLowerCase().equals(this.text_answer.toLowerCase());
        this.setUserTextInput(text);
        storeAnswered(this.answeredCorrect, text);
        return this.answeredCorrect;
    }

    /**
     * Checks validity of multichoice answer and stores it
     * @param id    The given answer
     * @return      The answer's validity
     * @see #storeAnswered(boolean, String)
     */
    public boolean checkAnswer(int id) {
        this.answered = true;
        answeredCorrect = id == this.multi_answer ;
        this.setUserMultiInput(id);
        storeAnswered(this.answeredCorrect, "" + id);
        return this.answeredCorrect;
    }

    /**
     * Loads question's picture as bitmap
     * @return  The picture's bitmap
     * @see #loadImage(String)
     */
    public Bitmap getImage() {
        return loadImage(localContentUri.toString());
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

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public void setqId(int qId) {
        this.qId = qId;
    }

    public int getqId() {
        return qId;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }

    public void setAnsweredCorrect(boolean answeredCorrect) {
        this.answeredCorrect = answeredCorrect;
    }

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    /**
     * Save photo to app's getFilesDir as a jpeg with 85% quality
     * @param photo     The photo to save
     */
    public void savePhoto(Bitmap photo) {
        File saveDir = application.getFilesDir();
        File image = new File(saveDir, "photo_" + this.qId + "_" + this.gameId + ".jpg");
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(image);
            photo.compress(Bitmap.CompressFormat.JPEG, 85, out);
            this.localPhotoUri = Uri.fromFile(image);

            // Save to DB
            GameDbHelper helper = new GameDbHelper(application.getApplicationContext());
            SQLiteDatabase sqlDb = helper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(GameDB.Questions.COL_LOCALPHOTO, image.getAbsolutePath());
            String where = GameDB.Questions.COL_GID + " = ? AND " + GameDB.Questions.COL_QID + " = ?";
            String[] whereArgs = { "" + this.gameId, "" + this.qId };
            sqlDb.update(GameDB.Questions.TABLE_NAME, contentValues, where, whereArgs);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if( out != null ) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Loads an image from file as a bitmap
     * @param location  String ocntaining the path to the file
     * @return          The loaded Bitmap
     */
    private Bitmap loadImage(String location) {
        File file = new File(location);
        InputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inJustDecodeBounds = false;
            Bitmap image = BitmapFactory.decodeStream(fileInputStream, null, bitmapOptions);
            fileInputStream.close();
            return image;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if( fileInputStream != null ) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * Loads the photo taken for this question from storage
     * @return  The photo's Bitmap
     * @see #loadImage(String)
     */
    public Bitmap getLocalPhoto() {
        return loadImage(localPhotoUri.toString());
    }

    /**
     * Checks if the local photo (still) exists
     * @return  Exists?
     * @see java.io.File#exists()
     */
    public boolean hasLocalPhoto() {
        if(localPhotoUri != null) {
            File image = new File(localPhotoUri.toString());
            return image.exists();
        }
        else{
            return false;
        }
    }

    public void setLocalPhotoUri(Uri localPhotoUri) {
        this.localPhotoUri = localPhotoUri;
    }
}
