package be.pxl.citygame.providers;

import android.app.Application;

import java.util.NoSuchElementException;

import be.pxl.citygame.data.Question;

/**
 * Created by Christina on 20/01/2015.
 */
public class QuestionProvider implements IQuestionProvider {

    private Application application;

    public QuestionProvider(Application application) {
        this.application = application;
    }

    /**
     * Gets the question requested from the GameContentProvider
     * @param gameId        Game's id
     * @param questionId    Question's id
     * @return              The requested Question
     * @throws NoSuchElementException
     * @see be.pxl.citygame.data.GameContent#getQuestionById(int)
     * @see be.pxl.citygame.providers.GameContentWebProvider#getGameContentById(int)
     */
    @Override
    public Question loadQuestionById(int gameId, int questionId) throws NoSuchElementException {
        Question question = Providers.getGameContentProvider().getGameContentById(gameId).getQuestionById(questionId);
        if( question == null ) {
            throw new NoSuchElementException("No such question id in this gamecontent id");
        }

        return question;
    }
}
