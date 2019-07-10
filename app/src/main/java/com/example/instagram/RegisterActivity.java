package com.example.instagram;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;
    private Button signupBtn;
    private TextView tvAccount;
    private TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // find views
        usernameInput = (EditText) findViewById(R.id.etUsername);
        passwordInput = (EditText) findViewById(R.id.etPassword);
        signupBtn = (Button) findViewById(R.id.btnSignup);
        tvAccount = (TextView) findViewById(R.id.tvAccount);
        tvLogin = (TextView) findViewById(R.id.tvLogin);

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent login = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(login);
                finish();
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = usernameInput.getText().toString();
                final String password = passwordInput.getText().toString();

                signup(username, password);
            }
        });
    }

    private void signup(String username, String password) {
        // Create the ParseUser
        ParseUser user = new ParseUser();
        // Set core properties
        user.setUsername(username);
        user.setPassword(password);
        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.
                    Log.d("SignupActivity", "Signup successful!");
                    // create intent to launch home activity after signing up successfully
                    final Intent home = new Intent(RegisterActivity.this, HomeActivity.class);
                    startActivity(home);
                    finish();
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    Log.e("SignupActivity", "Signup failure");
                    e.printStackTrace();
                }
            }
        });
    }
}
