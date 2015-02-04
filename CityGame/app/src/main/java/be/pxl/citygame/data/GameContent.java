package be.pxl.citygame.data;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import be.pxl.citygame.MainActivity;

/**
 * Created by Christina on 7/01/2015.
 * Class holds the content of a full CityGame
 */
public class GameContent {

    private String title;
    private List<Question> questionList;
    private int id = 0;
    private boolean completed = false;
    private int score;

    public List<Question> getQuestionList() {
        return questionList;
    }

    public GameContent(String title) {
        questionList = new ArrayList<Question>();
        this.title = title;
    }

    public int getNumQuestions() {
        return questionList.size();
    }

    public Question getQuestionById(int id) throws NoSuchElementException {
        Log.d(MainActivity.class.toString(), "Fetching question with id " + id);

        if( id >= questionList.size() )
            throw new NoSuchElementException("No such question id in this gamecontent id");

        Question question = questionList.get(id);
        if( question == null )
            throw new NoSuchElementException("No such question id in this gamecontent id");

        return question;
    }

    public GameContent(String title, List<Question> questionList) {
        this.questionList = questionList;
        this.title = title;
    }

    public void addQuestion(Question question) {
        questionList.add(question);
    }

    public void removeQuestion(Question question) throws IllegalArgumentException {
        if( !questionList.remove(question) )
            throw new IllegalArgumentException("Question to remove does not exist");
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
