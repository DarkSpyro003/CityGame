package be.pxl.citygame;

import android.app.Activity;
import android.app.Application;

/**
 * Created by Christina on 15/01/2015.
 */
public class CityGameApplication extends Application {

    private Activity activity;

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
