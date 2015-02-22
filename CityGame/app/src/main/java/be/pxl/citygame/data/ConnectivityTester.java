package be.pxl.citygame.data;

import android.app.Application;
import android.os.AsyncTask;

/**
 * Created by Christina on 22/02/2015.
 * AsyncTask for Helpers class
 */

public class ConnectivityTester extends AsyncTask<Application, Void, Boolean> {

    @Override
    protected Boolean doInBackground(Application... params) {
        return Helpers.testConnectivity(params[0]);
    }
}
