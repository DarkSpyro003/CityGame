package be.pxl.citygame;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christina on 7/01/2015.
 * Class holds the content of a full CityGame
 */
public class GameContent {

    private String title;
    private List<Question> questionList;

    public GameContent(String title) {
        questionList = new ArrayList<Question>();
        this.title = title;
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
}
