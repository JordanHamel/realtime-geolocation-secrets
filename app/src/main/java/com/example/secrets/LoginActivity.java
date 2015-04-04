package com.example.secrets;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.firebase.client.Firebase;
import com.firebase.simplelogin.FirebaseSimpleLoginError;
import com.firebase.simplelogin.FirebaseSimpleLoginUser;
import com.firebase.simplelogin.SimpleLogin;
import com.firebase.simplelogin.SimpleLoginAuthenticatedHandler;


public class LoginActivity extends ActionBarActivity {

    private String email;
    private String password;
    private EditText emailField;
    private EditText passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Firebase ref = new Firebase("https://intense-fire-3164.firebaseio.com");
        final SimpleLogin authClient = new SimpleLogin(ref, getApplicationContext());

        checkAuthStatus(authClient);

        emailField = (EditText) findViewById(R.id.email);
        passwordField = (EditText) findViewById(R.id.password);

        findViewById(R.id.signUpButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = emailField.getText().toString();
                password = passwordField.getText().toString();
                authClient.createUser(email, password, new SimpleLoginAuthenticatedHandler() {
                    public void authenticated(FirebaseSimpleLoginError error, FirebaseSimpleLoginUser user) {
                        if (error != null) {
                            Toast.makeText(getApplicationContext(), "Error:" + error.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            login(email, password, authClient);
                        }
                    }
                });
            }
        });

        findViewById(R.id.logInButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = emailField.getText().toString();
                password = passwordField.getText().toString();
                login(email, password, authClient);
            }
        });
    }

    private void login(String email, String password, SimpleLogin authClient) {
        authClient.loginWithEmail(email, password, new SimpleLoginAuthenticatedHandler() {
            public void authenticated(FirebaseSimpleLoginError error, FirebaseSimpleLoginUser user) {
                if(error != null) {
                    Toast.makeText(getApplicationContext(), "Error:" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
                else {
                    startActivity(new Intent(getApplicationContext(), ListNearbySecretsActivity.class));
                }
            }
        });
    }

    private void checkAuthStatus(SimpleLogin authClient) {
        authClient.checkAuthStatus(new SimpleLoginAuthenticatedHandler() {
            @Override
            public void authenticated(FirebaseSimpleLoginError error, FirebaseSimpleLoginUser user) {
                if (user != null) {
                    startActivity(new Intent(getApplicationContext(), ListNearbySecretsActivity.class));
                }
            }
        });
    }
}
