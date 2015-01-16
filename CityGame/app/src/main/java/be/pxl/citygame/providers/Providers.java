package be.pxl.citygame.providers;

import android.app.Application;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import be.pxl.citygame.R;

/**
 * Created by Lorenz Jolling on 2015-01-14.
 * Very light dependency injection-like class
 *
 *
 */
public class Providers {
    private static IQuestionProvider questionProvider;
    private static IGameContentProvider gameContentProvider;

    /**
     * Needs to be called when the application gets created and before any other methods are accessed
     * @param application The current Application instance
     */
    public static void load(Application application)
    {

        try {
            {
                // Load question provider
                Class c = Class.forName(application.getString(R.string.question_provider));
                @SuppressWarnings("unchecked")
                        Constructor ctor = c.getDeclaredConstructor(Application.class);
                questionProvider = (IQuestionProvider) ctor.newInstance(application);
            }
            {
                // Load game content provider
                Class c = Class.forName(application.getString(R.string.game_content_provider));
                @SuppressWarnings("unchecked")
                        Constructor ctor = c.getDeclaredConstructor(Application.class);
                gameContentProvider = (IGameContentProvider) ctor.newInstance(application);
            }
        }
        catch (NoSuchMethodException e) {
            Log.e(Providers.class.toString(), e.getMessage());
        } catch (InvocationTargetException e) {
            Log.e(Providers.class.toString(), e.getMessage());
        } catch (InstantiationException e) {
            Log.e(Providers.class.toString(), e.getMessage());
        } catch (IllegalAccessException e) {
            Log.e(Providers.class.toString(), e.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e(Providers.class.toString(), e.getMessage());
        }


    }

    public static IQuestionProvider getQuestionProvider()
    {
        return questionProvider;
    }

    public static IGameContentProvider getGameContentProvider() {
        return gameContentProvider;
    }
}
