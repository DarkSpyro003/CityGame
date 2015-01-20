package be.pxl.citygame.providers;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import be.pxl.citygame.GameContent;
import be.pxl.citygame.Question;

/**
 * Created by Lorenz Jolling on 2015-01-17.
 */
class GameContentMockProvider implements IGameContentProvider {

    @Override
    public GameContent getGameContentById(int id) throws NoSuchElementException {
        // Only id 1 valid for testing
        if (id != 1) {
            throw new NoSuchElementException("No GameContent with id: " + id);
        }

        GameContent content = new GameContent("Mock game");
        for (int i = 0; i < 4; i++) {
            try {
                content.addQuestion(Providers.getQuestionProvider().loadQuestionById(id, i));
            }
            catch (NoSuchElementException e) {
                break;
            }
        }

        return content;
    }
}
