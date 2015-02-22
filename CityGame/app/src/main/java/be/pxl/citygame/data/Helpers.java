package be.pxl.citygame.data;

import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import be.pxl.citygame.CityGameApplication;
import be.pxl.citygame.R;
import be.pxl.citygame.providers.Providers;

/**
 * Created by Christina on 21/02/2015.
 */
public class Helpers {

    /**
     * Returns the first qid following passed qid
     * @param qid   Previous question's id
     * @param gid   Game's id
     * @return      Next question id or -1 in case of none
     */
    public static int getNextQid(int gid, int qid) {
        for( Map.Entry<Integer, Question> entry : Providers.getGameContentProvider().getGameContentById(gid).getQuestionList().entrySet() ) {
            int key = entry.getKey();
            if( key > qid )
                return key;
        }
        return -1;
    }

    /**
     * Shows an internet/webservice connection failure dialog.
     * @param application   The CityGameApplication requesting this dialog
     */
    public static void showInternetErrorDialog(CityGameApplication application) {
        AlertDialog.Builder builder = new AlertDialog.Builder(application.getActivity());
        builder.setTitle(application.getString(R.string.err_no_internet_title))
                .setMessage(application.getString(R.string.err_no_internet_content))
                .setCancelable(true)
                .setPositiveButton(application.getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Checks if we can connect to our webservice from the current connectivity. Will automatically
     * run on a seperate thread when called from UI thread, or in same thread of non-UI calling
     * thread.
     * @param app   Android application requesting this data
     * @return      True if able to connect, false otherwise
     * @see #testConnectivity(android.app.Application)
     */
    public static boolean isConnectedToInternet(Application app) {
        if(Looper.getMainLooper().getThread() == Thread.currentThread()) {
            // This is the UI thread. Avoid networking on there
            AsyncTask task = new ConnectivityTester().execute(app);
            try {
                return (Boolean)task.get();
            } catch (InterruptedException|ExecutionException e) {
                return false;
            }
        } else {
            // Already on an extra thread. Just make the check in there.
            return testConnectivity(app);
        }
    }

    /**
     * Checks if we can connect to our webservice from the current connectivity
     * Only call this from non-UI threads.
     * Prefer using {@link #isConnectedToInternet(android.app.Application)} which will automatically
     * determine if a new thread needs to be spawned or not.
     * @param app   Android application requesting this data
     * @return      True if able to connect, false otherwise
     */
    public static boolean testConnectivity(Application app) {
        try {
            Uri url = Uri.parse(app.getString(R.string.webservice_url));
            InetAddress ip = InetAddress.getByName(url.getHost());

            return !ip.equals("");
        } catch (UnknownHostException e) {
            return false;
        }
    }

    /**
     * Converts data from InputStream into a String, and then closes the inputstream.
     * @param stream    The InputStream to read from
     * @return          The built String. On failure, an empty String.
     */
    public static String getStringFromStream(InputStream stream) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"), 65536);
            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }
}
