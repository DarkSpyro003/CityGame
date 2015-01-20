package be.pxl.citygame.providers;

import java.util.NoSuchElementException;

import be.pxl.citygame.GameContent;
import be.pxl.citygame.Question;

/**
 * Created by Christina on 20/01/2015.
 */
public class QuestionProvider implements IQuestionProvider {

    @Override
    public Question loadQuestionById(int gameId, int questionId) throws NoSuchElementException {
        Question question = Providers.getGameContentProvider().getGameContentById(gameId).getQuestionById(questionId);
        if( question == null ) {
            throw new NoSuchElementException("No such question id in this gamecontent id");
        }

        return question;
    }
}
