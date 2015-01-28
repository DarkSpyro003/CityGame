package be.pxl.citygame;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import be.pxl.citygame.data.Player;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {

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
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    public void register(View v)
    {
        EditText txtUsername = (EditText) getActivity().findViewById(R.id.txtUser);
        EditText txtPassword = (EditText) getActivity().findViewById(R.id.txtUser);
        EditText txtName = (EditText) getActivity().findViewById(R.id.txtUser);
        EditText txtEmail = (EditText) getActivity().findViewById(R.id.txtUser);

        String username = txtUsername.getText().toString();
        String password = txtPassword.getText().toString();
        String email = txtEmail.getText().toString();
        String name = txtName.getText().toString();

        if(isUsernameValid(username) && isPasswordValid(password) && isEmailValid(email))
        {
            Player player = new Player(username, getActivity().getApplication());
            player.setEmail(email);
            player.setRealname(name);

            Boolean success = player.register(password);
            Log.d("Register", success.toString());
        }
    }

    private boolean isPasswordValid(String password)
    {
        Boolean success = !password.isEmpty() && password.length() >= 4;
        Log.d("Password is valid", success.toString());

        return  success;
    }

    private boolean isUsernameValid(String username)
    {
        Boolean success = !username.isEmpty() && username.length() >= 6;
        Log.d("Username is valid", success.toString());

        return  success;
    }

    private boolean isEmailValid(String email)
    {
        //return true;
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        Boolean success = matcher.matches();
        Log.d("Email is valid", success.toString());

        return  success;
    }

}
