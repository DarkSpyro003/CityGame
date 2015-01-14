package be.pxl.citygame.providers;

import android.app.Application;

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

    /**
     * Needs to be called when the application gets created and before any other methods are accessed
     * @param application The current Application instance
     */
    public static void load(Application application)
    {
        {
            try {
                Class c = Class.forName(application.getString(R.string.question_provider));
                @SuppressWarnings("unchecked")
                Constructor ctor = c.getDeclaredConstructor(Application.class);
                questionProvider = (IQuestionProvider)ctor.newInstance(application);
            }
            catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    public static IQuestionProvider getQuestionProvider()
    {
        return questionProvider;
    }
}
