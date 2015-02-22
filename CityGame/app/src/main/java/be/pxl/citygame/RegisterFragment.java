package be.pxl.citygame;

import android.accounts.*;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import be.pxl.citygame.data.Helpers;
import be.pxl.citygame.data.Player;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {

    private AutoCompleteTextView mEmailView;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_register, container, false);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) v.findViewById(R.id.txtEmail);
        new SetupEmailAutoCompleteTask().execute(null, null);
        return v;
    }

    public void register(View v) {
        EditText txtUsername = (EditText) getActivity().findViewById(R.id.txtUser);
        EditText txtPassword = (EditText) getActivity().findViewById(R.id.txtPassword);
        EditText txtName = (EditText) getActivity().findViewById(R.id.txtName);
        EditText txtEmail = (EditText) getActivity().findViewById(R.id.txtEmail);

        String username = txtUsername.getText().toString();
        String password = txtPassword.getText().toString();
        String email = txtEmail.getText().toString();
        String name = txtName.getText().toString();

        if(isUsernameValid(username) && isPasswordValid(password) && isEmailValid(email)) {
            Player player = ((CityGameApplication)getActivity().getApplication()).getPlayer();
            player.setUsername(username);
            player.setEmail(email);
            player.setRealname(name);

            Boolean success = player.register(password);
            Log.d("Register", success.toString());
            if( success ) {
                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.registration_success), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                startActivity(intent);
            } else {
                if(Helpers.isConnectedToInternet(getActivity().getApplication()))
                    Toast.makeText(getActivity().getApplicationContext(), getActivity().getApplication().getString(R.string.registration_web_fail), Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean isPasswordValid(String password) {
        Boolean success = !password.isEmpty() && password.length() >= 4;
        Log.d("Password is valid", success.toString());
        if( !success )
            Toast.makeText(getActivity().getApplicationContext(), getActivity().getApplication().getString(R.string.registration_err_password), Toast.LENGTH_LONG).show();
        return  success;
    }

    private boolean isUsernameValid(String username) {
        Boolean success = !username.isEmpty() && username.length() >= 6;
        Log.d("Username is valid", success.toString());
        if( !success )
            Toast.makeText(getActivity().getApplicationContext(), getActivity().getApplication().getString(R.string.registration_err_username), Toast.LENGTH_LONG).show();
        return  success;
    }

    private boolean isEmailValid(String email) {
        //return true;
        String expression = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        Boolean success = matcher.matches();
        Log.d("Email is valid", success.toString() + " " + email);
        if( !success )
            Toast.makeText(getActivity().getApplicationContext(), getActivity().getApplication().getString(R.string.registration_err_email), Toast.LENGTH_LONG).show();

        return  success;
    }

    /**
     * Use an AsyncTask to fetch the user's email addresses on a background thread, and update
     * the email text field with results on the main UI thread.
     */
    class SetupEmailAutoCompleteTask extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... voids) {
            AccountManager manager = (AccountManager) getActivity().getSystemService(Context.ACCOUNT_SERVICE);
            Account[] accounts = manager.getAccounts();
            ArrayList<String> emailAddressCollection = new ArrayList<String>();
            for( Account account : accounts ) {
                emailAddressCollection.add(account.name);
            }
            return emailAddressCollection;
        }

        @Override
        protected void onPostExecute(List<String> emailAddressCollection) {
            addEmailsToAutoComplete(emailAddressCollection);
        }
    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

}
