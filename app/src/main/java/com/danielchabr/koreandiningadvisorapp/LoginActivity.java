package com.danielchabr.koreandiningadvisorapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.danielchabr.koreandiningadvisorapp.model.User;
import com.danielchabr.koreandiningadvisorapp.rest.ApiClient;
import com.danielchabr.koreandiningadvisorapp.rest.service.UserService;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "user:hello", "foo:world"
    };

    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mUsernameView = (EditText) findViewById(R.id.username);

        mPasswordView = (EditText) findViewById(R.id.login_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login_password || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        Button mSignUpButton = (Button) findViewById(R.id.sign_up_button);
        mSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent authenticate = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(authenticate);
            }
        });
    }

    public void attemptLogin() {
        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            mUsernameView.setError(getString(R.string.error_invalid_email));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            final String TAG = "AuthenticateUser";
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            UserService userService = new ApiClient().getUserService();
            Call<Boolean> call = userService.authenticate(user);
            call.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Response response, Retrofit retrofit) {
                    if (response.isSuccess()) {
                        Log.v(TAG, "successfully authenticated user");
                        Log.v(TAG, "code: " + response.code());

                        Intent showDashboard = new Intent();
                        setResult(Activity.RESULT_OK, showDashboard);
                        finish();
                    } else {
                        Log.v(TAG, "response: " + response.body());
                        Log.v(TAG, "response: " + response.errorBody().toString());
                        Log.v(TAG, "response: " + response.message());
                        Log.v(TAG, "code: " + response.code());
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.v(TAG, "error creating user");
                    Log.v(TAG, t.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage("Network error")
                            .setTitle("No network connection");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }
    }

    private boolean authenticate(User user) {
        for (String credential : DUMMY_CREDENTIALS) {
            String[] pieces = credential.split(":");
            if (pieces[0].equals(user.getUsername())) {
                // Account exists, return true if the password matches.
                return pieces[1].equals(user.getPassword());
            }
        }
        return false;
    }

    private boolean isUsernameValid(String username) {
        return username.length() > 2;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 3;
    }
}

