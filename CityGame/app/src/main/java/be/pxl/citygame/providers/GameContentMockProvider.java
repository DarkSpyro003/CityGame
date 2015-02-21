package be.pxl.citygame.providers;

import java.util.NoSuchElementException;

import be.pxl.citygame.MainActivity;
import be.pxl.citygame.data.GameContent;

/**
 * Created by Lorenz Jolling on 2015-01-17.
 */
class GameContentMockProvider implements IGameContentProvider {

    @Override
    public void initGameContentById(int id, GameContentCaller caller) {
        // Only id 1 valid for testing
        if (id != 1) {
            throw new NoSuchElementException("No GameContent with id: " + id + " (Mock provider)");
        }
    }

    @Override
    public GameContent getGameContentById(int id) throws NoSuchElementException {
        // Only id 1 valid for testing
        if (id != 1) {
            throw new NoSuchElementException("No GameContent with id: " + id + " (Mock provider)");
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
