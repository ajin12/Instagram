package com.example.instagram;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginBtn;
    private TextView tvAccount;
    private TextView tvSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // find views
        usernameInput = findViewById(R.id.etUsername);
        passwordInput = findViewById(R.id.etPassword);
        loginBtn = findViewById(R.id.btnLogin);
        tvAccount = findViewById(R.id.tvAccount);
        tvSignup = findViewById(R.id.tvSignup);

        // if existing, access the cached current user and directly launch home activity
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            final Intent home = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(home);
            finish();
        }

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = usernameInput.getText().toString();
                final String password = passwordInput.getText().toString();

                login(username, password);
            }
        });

        tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent register = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(register);
                finish();
            }
        });
    }

    private void login(String username, String password) {
        // log in
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    Log.d("LoginActivity", "Login successful!");
                    // create intent to launch home activity after logging in successfully
                    final Intent home = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(home);
                    finish();
                } else {
                    Log.e("LoginActivity", "Login failure");
                    e.printStackTrace();
                }
            }
        });
    }
}
