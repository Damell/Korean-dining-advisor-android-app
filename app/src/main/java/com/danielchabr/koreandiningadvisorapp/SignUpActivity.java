package com.danielchabr.koreandiningadvisorapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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

import org.parceler.Parcels;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * A login screen that offers login via email/password.
 */
public class SignUpActivity extends AppCompatActivity {

    // UI references.
    private EditText mEmailView;
    private EditText mUsernameView;
    private EditText mPasswordView;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Set up the login form.
        mUsernameView = (EditText) findViewById(R.id.username);
        mEmailView = (EditText) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.signup_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.signup_password || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_up_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    public void attemptLogin() {
        // Reset errors.
        mUsernameView.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(username) && !isUsernameValid(username)) {
            mUsernameView.setError(getString(R.string.error_invalid_password));
            focusView = mUsernameView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(password);

            final String TAG = "CreateUser";
            UserService userService = new ApiClient().getUserService();
            Call<Void> call = userService.create(user);
            final ProgressDialog progress = new ProgressDialog(this);
            progress.setTitle("Loading");
            progress.setMessage("Wait while loading...");
            progress.show();
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Response response, Retrofit retrofit) {
                    progress.dismiss();
                    if (response.isSuccess()) {
                        Log.v(TAG, "successfully created user");
                        Log.v(TAG, "code: " + response.code());

                        Intent returnUser = new Intent();
                        returnUser.putExtra("user", Parcels.wrap(user));
                        setResult(Activity.RESULT_OK, returnUser);
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
                    progress.dismiss();
                    Log.v(TAG, "error creating user");
                    Log.v(TAG, t.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                    builder.setMessage("Network error")
                            .setTitle("No network connection");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
            Intent showDashboard = new Intent();
            setResult(Activity.RESULT_OK, showDashboard);
            finish();
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 3;
    }

    private boolean isUsernameValid(String password) {
        return password.length() > 2;
    }

}

