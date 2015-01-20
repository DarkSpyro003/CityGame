package be.pxl.citygame;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import be.pxl.citygame.providers.IQuestionProvider;
import be.pxl.citygame.providers.Providers;



public class MainActivity extends ActionBarActivity {

    private static final String PRIMARY_CONTENT = "http://public.ds003.info/citygame/gamecontent/1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Loads the providers that have been declared in settings.xml
        Providers.load(getApplication());

        // For testing purposes only
        // todo: remove later
        IQuestionProvider provider = Providers.getQuestionProvider();
        Question testQuestion = provider.loadQuestionById(0);
        ((TextView)(findViewById(R.id.tv_output_test))).setText(testQuestion.getQuestion());
    }

    public void handleBtnStart(View v) {
        // Download primary content
        new GetRestData().execute(PRIMARY_CONTENT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void scanQR(View v)
    {
        IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
        integrator.addExtra("SCAN_WIDTH", 640);
        integrator.addExtra("SCAN_HEIGHT", 480);
        integrator.addExtra("SCAN_MODE", "QR_CODE_MODE");
        //customize
        //integrator.addExtra("PROMPT_MESSAGE", "SCANNER_START");
        integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if(result != null)
        {
            String contents = result.getContents();
            if(contents != null)
            {
                // QR code should contain url to a valid GameContent JSON file or REST service
                // Try to download the content. If successful, it will switch to the next activity
                new GetRestData().execute(contents);
            }
        }
    }

    private class GetRestData extends AsyncTask<String, Void, GameContent> {

        @Override
        protected GameContent doInBackground(String... params) {
            GameContent content = Providers.getGameContentProvider().getGameContentById(1);
            return content;
        }

        @Override
        protected void onPostExecute(GameContent content) {
            // This runs in GUI thread
            if( content != null ) {
                CityGameApplication context = (CityGameApplication) getApplicationContext();
                // Store gamecontent in global variable
                context.setCurrentGame(content);
                // Switch to next activity
                Intent intent = new Intent(context, NextLocationActivity.class);
                intent.putExtra("questionNumber", 0);
                startActivity(intent);
            }
        }
    }
}

