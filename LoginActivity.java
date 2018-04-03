package edu.gatech.mhiggins36.homeless_shelter_app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Checks login credentials
 */

public class LoginActivity extends AppCompatActivity {

    private static final String usernameTest = "user";
    private static final String passwordTest = "pass";
    EditText userField;
    EditText passField;
    TextView incorrectText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userField = findViewById(R.id.usernameField);
        passField = findViewById(R.id.passwordField);
        incorrectText = findViewById(R.id.incorrectCred);
        incorrectText.setVisibility(View.INVISIBLE);

    }

    public void checkCredentials(View view) {
        String username = userField.getText().toString();
        String password = passField.getText().toString();
        if (username.equals(userField) && password.equals(passField)) {
            Intent dashboardIntent = new Intent(this, DashboardActivity.class);
            startActivity(dashboardIntent);
            incorrectText.setVisibility(View.INVISIBLE);
        } else {
            incorrectText.setVisibility(View.VISIBLE);
        }
    }

}
