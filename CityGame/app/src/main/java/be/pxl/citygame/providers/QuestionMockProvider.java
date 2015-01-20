package be.pxl.citygame.providers;

import android.app.Application;

import java.util.Arrays;
import java.util.NoSuchElementException;

import be.pxl.citygame.Question;

/**
 * Created by Lorenz Jolling on 2015-01-14.
 * Provides test Questions from a hardcoded mock database
 * Useful while developing to avoid network while testing.
 * Can not be instantiated use the Providers class
 */
class QuestionMockProvider implements IQuestionProvider {

    static Question[] questions;

    static
    {
        // Load questions
        questions = new Question[] {
            new Question(0, "Test question 1", "test 1"),
            new Question(0, "Test question 2", "test 2"),
            new Question(1, "Test question 3", 1, Arrays.asList("Wrong", "Correct", "Wrong", "Wrong")),
            new Question(1, "Test question 4", 0, Arrays.asList("Correct", "Wrong", "Wrong"))
        };
    }

    public QuestionMockProvider(@SuppressWarnings("unused") Application application) { }
    @Override
    public Question loadQuestionById(@SuppressWarnings("unused") int gameId, int id) throws NoSuchElementException {
        // If id is in a valid range
        if (id >= 0 && id < questions.length)
        {
            return questions[id];
        }
        throw new NoSuchElementException("Invalid id. (index out of bounds)");
    }
}
